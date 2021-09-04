package com.twilio.video.app.sdk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.twilio.video.*
import com.twilio.video.TwilioException.ROOM_MAX_PARTICIPANTS_EXCEEDED_EXCEPTION
import com.twilio.video.app.data.api.AuthServiceError
import com.twilio.video.app.data.api.AuthServiceException
import com.twilio.video.app.data.api.VideoAppService
import com.twilio.video.app.data.api.model.ApiResponse
import com.twilio.video.app.ui.Attachment
import com.twilio.video.app.ui.room.RoomEvent
import com.twilio.video.app.ui.room.RoomEvent.*
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.RemoteParticipantConnected
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.RemoteParticipantDisconnected
import com.twilio.video.app.ui.room.VideoService.Companion.startService
import com.twilio.video.app.ui.room.VideoService.Companion.stopService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


const val MICROPHONE_TRACK_NAME = "microphone"
const val CAMERA_TRACK_NAME = "camera"
const val SCREEN_TRACK_NAME = "screen"

class RoomManager(
    private val context: Context,
    private val videoClient: VideoClient,
    sharedPreferences: SharedPreferences,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private var statsScheduler: StatsScheduler? = null
    private val roomListener = RoomListener()
    @VisibleForTesting(otherwise = PRIVATE)
    internal var roomScope = CoroutineScope(coroutineDispatcher)
    private val mutableRoomEvents: MutableSharedFlow<RoomEvent> = MutableSharedFlow()
    val roomEvents: SharedFlow<RoomEvent> = mutableRoomEvents
    @VisibleForTesting(otherwise = PRIVATE)
    internal var localParticipantManager: LocalParticipantManager =
            LocalParticipantManager(context, this, sharedPreferences)
    var room: Room? = null
    var localDataTrack:LocalDataTrack? = LocalDataTrack.create(context)


    fun disconnect() {
        room?.disconnect()
    }

    suspend fun connect(identity: String, roomName: String) {
        sendRoomEvent(Connecting)
        connectToRoom(identity, roomName)
    }

    suspend fun getAttachmentList(roomName: String, bookingListResponse: MutableLiveData<ApiResponse<List<Attachment>>>) {
        val ATTACHEMENT_BASE_URL = "https://full-aureusgroup.cs117.force.com/services/apexrest/"
        var response: okhttp3.ResponseBody? = null
        val retrofit = Retrofit.Builder()
            .baseUrl(ATTACHEMENT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val videoAppService = retrofit.create(VideoAppService::class.java)
        val apiResponse = ApiResponse<List<Attachment>>(ApiResponse.Status.LOADING, null, null)
        bookingListResponse.value = apiResponse
        response = videoAppService.getAttachmentList(roomName)
        response.let {
                val jsonString = response?.string()
                if (!jsonString.isNullOrEmpty()) {
                    val clean = jsonString.removeSurrounding("\"").replace("\\", "")
                    val jsonArray: JSONArray = JSONArray(clean)
                    val attachList = mutableListOf<Attachment>()
                    jsonArray.let {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val gson= GsonBuilder().serializeNulls().create()
                            val attachment = gson.fromJson(jsonObject.toString(), Attachment::class.java)
                            attachList.add(attachment)
                        }
                    }
                    val apiResponse = ApiResponse<List<Attachment>>(
                        ApiResponse.Status.SUCCESS,
                        attachList,
                        null
                    )
                    bookingListResponse.value = apiResponse
                }
        }
    }

    private suspend fun connectToRoom(identity: String, roomName: String) {
        roomScope.launch {
            try {
                videoClient.connect(identity, roomName, roomListener, localDataTrack)
            } catch (e: AuthServiceException) {
                handleTokenException(e, e.error)
            } catch (e: Exception) {
                handleTokenException(e)
            }
        }
    }



    fun sendRoomEvent(roomEvent: RoomEvent) {
        Timber.d("sendRoomEvent: $roomEvent")
        roomScope.launch { mutableRoomEvents.emit(roomEvent) }
    }

    private fun handleTokenException(e: Exception, error: AuthServiceError? = null): Room? {
        Timber.e(e, "Failed to retrieve token")
        sendRoomEvent(RoomEvent.TokenError(serviceError = error))
        return null
    }

    fun onResume() {
        localParticipantManager.onResume()
    }

    fun onPause() {
        localParticipantManager.onPause()
    }

    fun toggleLocalVideo() {
        localParticipantManager.toggleLocalVideo()
    }

    fun toggleLocalAudio() {
        localParticipantManager.toggleLocalAudio()
    }

    fun startScreenCapture(captureResultCode: Int, captureIntent: Intent) {
        localParticipantManager.startScreenCapture(captureResultCode, captureIntent)
    }

    fun stopScreenCapture() {
        localParticipantManager.stopScreenCapture()
    }

    fun switchCamera() = localParticipantManager.switchCamera()

    fun sendStatsUpdate(statsReports: List<StatsReport>) {
        room?.let { room ->
            val roomStats = RoomStats(
                room.remoteParticipants,
                localParticipantManager.localVideoTrackNames,
                statsReports
            )
            sendRoomEvent(StatsUpdate(roomStats))
        }
    }

    fun enableLocalAudio() = localParticipantManager.enableLocalAudio()

    fun disableLocalAudio() = localParticipantManager.disableLocalAudio()

    fun enableLocalVideo() = localParticipantManager.enableLocalVideo()

    fun disableLocalVideo() = localParticipantManager.disableLocalVideo()

    inner class RoomListener : Room.Listener {
        override fun onConnected(room: Room) {
            Timber.i(
                "onConnected -> room sid: %s",
                room.sid
            )

            startService(context, room.name)
            setupParticipants(room)

            statsScheduler = StatsScheduler(this@RoomManager, room).apply { start() }
            this@RoomManager.room = room
        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {
            Timber.i(
                "Disconnected from room -> sid: %s, state: %s",
                room.sid, room.state
            )

            stopService(context)

            sendRoomEvent(Disconnected)

            localParticipantManager.localParticipant = null

            statsScheduler?.stop()
            statsScheduler = null
        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            Timber.e(
                "Failed to connect to room -> sid: %s, state: %s, code: %d, error: %s",
                room.sid,
                room.state,
                twilioException.code,
                twilioException.message
            )

            if (twilioException.code == ROOM_MAX_PARTICIPANTS_EXCEEDED_EXCEPTION) {
                sendRoomEvent(MaxParticipantFailure)
            } else {
                sendRoomEvent(ConnectFailure)
            }
        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
            Timber.i(
                "RemoteParticipant connected -> room sid: %s, remoteParticipant: %s",
                room.sid, remoteParticipant.sid
            )

            remoteParticipant.setListener(RemoteParticipantListener(this@RoomManager))
            sendRoomEvent(RemoteParticipantConnected(remoteParticipant))
        }

        override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
            Timber.i(
                "RemoteParticipant disconnected -> room sid: %s, remoteParticipant: %s",
                room.sid, remoteParticipant.sid
            )

            sendRoomEvent(RemoteParticipantDisconnected(remoteParticipant.sid))
        }

        override fun onDominantSpeakerChanged(room: Room, remoteParticipant: RemoteParticipant?) {
            Timber.i(
                "DominantSpeakerChanged -> room sid: %s, remoteParticipant: %s",
                room.sid, remoteParticipant?.sid
            )

            sendRoomEvent(DominantSpeakerChanged(remoteParticipant?.sid))
        }

        override fun onRecordingStarted(room: Room) = sendRoomEvent(RecordingStarted)

        override fun onRecordingStopped(room: Room) = sendRoomEvent(RecordingStopped)

        override fun onReconnected(room: Room) {
            Timber.i("onReconnected: %s", room.name)
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            Timber.i("onReconnecting: %s", room.name)
        }

        private fun setupParticipants(room: Room) {
            room.localParticipant?.let { localParticipant ->
                localParticipantManager.localParticipant = localParticipant
                localDataTrack?.let {
                    localParticipant.publishTrack(it)
                }
                val participants = mutableListOf<Participant>()
                participants.add(localParticipant)
                localParticipant.setListener(LocalParticipantListener(this@RoomManager))

                room.remoteParticipants.forEach {
                    it.setListener(RemoteParticipantListener(this@RoomManager))
                    participants.add(it)
                }

                sendRoomEvent(Connected(participants, room, room.name))
                localParticipantManager.publishLocalTracks()
            }
        }
    }


}
