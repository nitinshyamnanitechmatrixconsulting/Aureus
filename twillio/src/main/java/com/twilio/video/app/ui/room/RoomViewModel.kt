package com.twilio.video.app.ui.room

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.annotation.VisibleForTesting.PROTECTED
import androidx.lifecycle.*
import co.intentservice.chatui.models.ChatMessage
import com.google.gson.GsonBuilder
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.Participant
import com.twilio.video.RemoteDataTrack
import com.twilio.video.app.data.api.VideoAppService
import com.twilio.video.app.data.api.model.ApiResponse
import com.twilio.video.app.participant.ParticipantManager
import com.twilio.video.app.participant.buildParticipantViewState
import com.twilio.video.app.sdk.RoomManager
import com.twilio.video.app.sdk.VideoTrackViewState
import com.twilio.video.app.ui.Attachment
import com.twilio.video.app.ui.room.RoomEvent.ConnectFailure
import com.twilio.video.app.ui.room.RoomEvent.Connected
import com.twilio.video.app.ui.room.RoomEvent.Connecting
import com.twilio.video.app.ui.room.RoomEvent.Disconnected
import com.twilio.video.app.ui.room.RoomEvent.DominantSpeakerChanged
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.AudioDisabled
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.AudioEnabled
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.AudioOff
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.AudioOn
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.ScreenCaptureOff
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.ScreenCaptureOn
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.VideoDisabled
import com.twilio.video.app.ui.room.RoomEvent.LocalParticipantEvent.VideoEnabled
import com.twilio.video.app.ui.room.RoomEvent.MaxParticipantFailure
import com.twilio.video.app.ui.room.RoomEvent.RecordingStarted
import com.twilio.video.app.ui.room.RoomEvent.RecordingStopped
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.MuteRemoteParticipant
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.NetworkQualityLevelChange
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.RemoteParticipantConnected
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.RemoteParticipantDisconnected
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.ScreenTrackUpdated
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.TrackSwitchOff
import com.twilio.video.app.ui.room.RoomEvent.StatsUpdate
import com.twilio.video.app.ui.room.RoomEvent.TokenError
import com.twilio.video.app.ui.room.RoomViewConfiguration.Lobby
import com.twilio.video.app.ui.room.RoomViewEffect.PermissionsDenied
import com.twilio.video.app.ui.room.RoomViewEffect.ShowConnectFailureDialog
import com.twilio.video.app.ui.room.RoomViewEffect.ShowMaxParticipantFailureDialog
import com.twilio.video.app.ui.room.RoomViewEffect.ShowTokenErrorDialog
import com.twilio.video.app.ui.room.RoomViewEvent.ActivateAudioDevice
import com.twilio.video.app.ui.room.RoomViewEvent.Connect
import com.twilio.video.app.ui.room.RoomViewEvent.DeactivateAudioDevice
import com.twilio.video.app.ui.room.RoomViewEvent.DisableLocalAudio
import com.twilio.video.app.ui.room.RoomViewEvent.DisableLocalVideo
import com.twilio.video.app.ui.room.RoomViewEvent.Disconnect
import com.twilio.video.app.ui.room.RoomViewEvent.EnableLocalAudio
import com.twilio.video.app.ui.room.RoomViewEvent.EnableLocalVideo
import com.twilio.video.app.ui.room.RoomViewEvent.OnPause
import com.twilio.video.app.ui.room.RoomViewEvent.OnResume
import com.twilio.video.app.ui.room.RoomViewEvent.PinParticipant
import com.twilio.video.app.ui.room.RoomViewEvent.ScreenTrackRemoved
import com.twilio.video.app.ui.room.RoomViewEvent.SelectAudioDevice
import com.twilio.video.app.ui.room.RoomViewEvent.StartScreenCapture
import com.twilio.video.app.ui.room.RoomViewEvent.StopScreenCapture
import com.twilio.video.app.ui.room.RoomViewEvent.SwitchCamera
import com.twilio.video.app.ui.room.RoomViewEvent.ToggleLocalAudio
import com.twilio.video.app.ui.room.RoomViewEvent.ToggleLocalVideo
import com.twilio.video.app.ui.room.RoomViewEvent.VideoTrackRemoved
import com.twilio.video.app.util.ChatUtils
import com.twilio.video.app.util.PermissionUtil
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.onState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.nio.ByteBuffer

class RoomViewModel(
    private val roomManager: RoomManager,
    private val audioSwitch: AudioSwitch,
    private val permissionUtil: PermissionUtil,
    private val participantManager: ParticipantManager = ParticipantManager(),
    initialViewState: RoomViewState = RoomViewState(participantManager.primaryParticipant)
) : AndroidDataFlow(defaultState = initialViewState) {

    private var name: String? = null
    internal var roomName: String? = null
    internal var roomId: String? = null
    internal var roomCode: String? = null
    private var permissionCheckRetry = false
    private val chatMessage: MutableLiveData<String> = MutableLiveData()
    private val chatMessageList: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()
    private val _attachmentListResponse by lazy {
        MutableLiveData<ApiResponse<List<Attachment>>>()
    }
    val attachmentListResponse: LiveData<ApiResponse<List<Attachment>>> = _attachmentListResponse
    fun getMessageLiveData(): MutableLiveData<String> {
        return chatMessage
    }

    fun getMessageListLiveData(): MutableLiveData<MutableList<ChatMessage>> {
        return chatMessageList
    }



    @VisibleForTesting(otherwise = PRIVATE)
    internal var roomManagerJob: Job? = null

    init {
        audioSwitch.start { audioDevices, selectedDevice ->
            updateState { currentState ->
                currentState.copy(
                    selectedDevice = selectedDevice,
                    availableAudioDevices = audioDevices
                )
            }
        }
        subscribeToRoomEvents()
    }
    fun getAttachmentList(bookingRoomName: String){
        viewModelScope.launch {
            roomManager.getAttachmentList(bookingRoomName, _attachmentListResponse)
        }
    }

    @VisibleForTesting(otherwise = PROTECTED)
    public override fun onCleared() {
        super.onCleared()
        audioSwitch.stop()
        roomManagerJob?.cancel()
    }

    fun processInput(viewEvent: RoomViewEvent) {
        Timber.d("View Event: $viewEvent")

        when (viewEvent) {
            OnResume -> checkPermissions()
            OnPause -> roomManager.onPause()
            is SelectAudioDevice -> {
                audioSwitch.selectDevice(viewEvent.device)
            }
            ActivateAudioDevice -> {
                audioSwitch.activate()
            }
            DeactivateAudioDevice -> {
                audioSwitch.deactivate()
            }
            is Connect -> {
                connect(viewEvent.identity, viewEvent.roomName)
            }
            is PinParticipant -> {
                participantManager.changePinnedParticipant(viewEvent.sid)
                updateParticipantViewState()
            }
            ToggleLocalVideo -> roomManager.toggleLocalVideo()
            EnableLocalVideo -> roomManager.enableLocalVideo()
            DisableLocalVideo -> roomManager.disableLocalVideo()
            is RoomViewEvent.SendMessage -> participantManager.sendMessage(viewEvent.message)
            ToggleLocalAudio -> roomManager.toggleLocalAudio()
            EnableLocalAudio -> roomManager.enableLocalAudio()
            DisableLocalAudio -> roomManager.disableLocalAudio()
            is StartScreenCapture -> roomManager.startScreenCapture(
                viewEvent.captureResultCode, viewEvent.captureIntent
            )
            StopScreenCapture -> roomManager.stopScreenCapture()
            SwitchCamera -> roomManager.switchCamera()
            is VideoTrackRemoved -> {
                participantManager.updateParticipantVideoTrack(viewEvent.sid, null)
                updateParticipantViewState()
            }
            is ScreenTrackRemoved -> {
                participantManager.updateParticipantScreenTrack(viewEvent.sid, null)
                updateParticipantViewState()
            }
            Disconnect -> roomManager.disconnect()
        }
    }

    private fun subscribeToRoomEvents() {
        roomManager.roomEvents.let { sharedFlow ->
            roomManagerJob = viewModelScope.launch {
                Timber.d("Listening for RoomEvents")
                sharedFlow.collect { observeRoomEvents(it) }
            }
        }
    }

    private fun checkPermissions() {
        val isCameraEnabled = permissionUtil.isPermissionGranted(permission.CAMERA)
        val isMicEnabled = permissionUtil.isPermissionGranted(permission.RECORD_AUDIO)

        updateState { currentState ->
            currentState.copy(isCameraEnabled = isCameraEnabled, isMicEnabled = isMicEnabled)
        }
        if (isCameraEnabled && isMicEnabled) {
            roomManager.onResume()
        } else {
            if (!permissionCheckRetry) {
                action {
                    sendEvent {
                        permissionCheckRetry = true
                        PermissionsDenied
                    }
                }
            }
        }
    }

    private fun observeRoomEvents(roomEvent: RoomEvent) {
        Timber.d("observeRoomEvents: %s", roomEvent)
        when (roomEvent) {
            is Connecting -> {
                showConnectingViewState()
            }
            is Connected -> {
                showConnectedViewState(roomEvent.roomName)
                checkParticipants(roomEvent.participants)
                action { sendEvent { RoomViewEffect.Connected(roomEvent.room) } }
            }
            is Disconnected -> showLobbyViewState()
            is DominantSpeakerChanged -> {
                participantManager.changeDominantSpeaker(roomEvent.newDominantSpeakerSid)
                updateParticipantViewState()
            }
            is ConnectFailure -> action {
                sendEvent {
                    showLobbyViewState()
                    ShowConnectFailureDialog
                }
            }
            is MaxParticipantFailure -> action {
                sendEvent { ShowMaxParticipantFailureDialog }
                showLobbyViewState()
            }
            is TokenError -> action {
                sendEvent {
                    showLobbyViewState()
                    ShowTokenErrorDialog(roomEvent.serviceError)
                }
            }
            RecordingStarted -> updateState { currentState -> currentState.copy(isRecording = true) }
            RecordingStopped -> updateState { currentState -> currentState.copy(isRecording = false) }
            is RemoteParticipantEvent -> handleRemoteParticipantEvent(roomEvent)
            is LocalParticipantEvent -> handleLocalParticipantEvent(roomEvent)
            is StatsUpdate -> updateState { currentState -> currentState.copy(roomStats = roomEvent.roomStats) }
        }
    }

    private fun handleRemoteParticipantEvent(remoteParticipantEvent: RemoteParticipantEvent) {
        when (remoteParticipantEvent) {
            is RemoteParticipantConnected -> addParticipant(remoteParticipantEvent.participant)
            is RemoteParticipantEvent.VideoTrackUpdated -> {
                participantManager.updateParticipantVideoTrack(remoteParticipantEvent.sid,
                    remoteParticipantEvent.videoTrack?.let { VideoTrackViewState(it) })
                updateParticipantViewState()
            }
            is TrackSwitchOff -> {
                participantManager.updateParticipantVideoTrack(
                    remoteParticipantEvent.sid,
                    VideoTrackViewState(
                        remoteParticipantEvent.videoTrack,
                        remoteParticipantEvent.switchOff
                    )
                )
                updateParticipantViewState()
            }
            is ScreenTrackUpdated -> {
                participantManager.updateParticipantScreenTrack(remoteParticipantEvent.sid,
                    remoteParticipantEvent.screenTrack?.let { VideoTrackViewState(it) })
                updateParticipantViewState()
            }
            is RemoteParticipantEvent.RemoteDataTrackSubscribed -> {
                setDataTrackMessageListener(remoteParticipantEvent.remoteDataTrack)
            }
            is MuteRemoteParticipant -> {
                participantManager.muteParticipant(
                    remoteParticipantEvent.sid,
                    remoteParticipantEvent.mute
                )
                updateParticipantViewState()
            }
            is NetworkQualityLevelChange -> {
                participantManager.updateNetworkQuality(
                    remoteParticipantEvent.sid,
                    remoteParticipantEvent.networkQualityLevel
                )
                updateParticipantViewState()
            }
            is RemoteParticipantDisconnected -> {
                participantManager.removeParticipant(remoteParticipantEvent.sid)
                updateParticipantViewState()
            }
        }
    }

    private fun getMessageListener(): RemoteDataTrack.Listener {
        return object : RemoteDataTrack.Listener {
            override fun onMessage(remoteDataTrack: RemoteDataTrack, messageBuffer: ByteBuffer) {

            }

            override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
                chatMessage.value = message
                val messageType = ChatUtils.getMessageType(message)
                if (messageType == ChatUtils.MessageType.CHAT_MESSAGE) {
                    val splitMessage = message.split("\$\$\$")
                    splitMessage?.let {
                        if (it.size > 1) {
                            val sender = it[0]
                            val data = it[1]
                            val chatMessage = ChatMessage(
                                data,
                                System.currentTimeMillis(),
                                ChatMessage.Type.RECEIVED,sender
                            )
                            addChatMessage(chatMessage)
                        }
                    }

                }
            }
        }
    }

    fun addChatMessage(chatMessage: ChatMessage) {
        val messageList = chatMessageList.value
        messageList?.let {
            it.add(chatMessage)
            chatMessageList.value = it

        } ?: kotlin.run {
            val chatMessageListLocal: MutableList<ChatMessage> = mutableListOf()
            chatMessageListLocal.add(chatMessage)
            chatMessageList.value = chatMessageListLocal
        }
    }

    private fun setDataTrackMessageListener(remoteDataTrack: RemoteDataTrack) {
        remoteDataTrack.setListener(getMessageListener())
    }


    private fun handleLocalParticipantEvent(localParticipantEvent: LocalParticipantEvent) {
        when (localParticipantEvent) {
            is LocalParticipantEvent.VideoTrackUpdated -> {
                participantManager.updateLocalParticipantVideoTrack(
                    localParticipantEvent.videoTrack?.let { VideoTrackViewState(it) })
                updateParticipantViewState()
                updateState { currentState -> currentState.copy(isVideoOff = localParticipantEvent.videoTrack == null) }
            }
            AudioOn -> updateState { currentState -> currentState.copy(isAudioMuted = false) }
            AudioOff -> updateState { currentState -> currentState.copy(isAudioMuted = true) }
            AudioEnabled -> updateState { currentState -> currentState.copy(isAudioEnabled = true) }
            AudioDisabled -> updateState { currentState -> currentState.copy(isAudioEnabled = false) }
            ScreenCaptureOn -> updateState { currentState -> currentState.copy(isScreenCaptureOn = true) }
            ScreenCaptureOff -> updateState { currentState -> currentState.copy(isScreenCaptureOn = false) }
            VideoEnabled -> updateState { currentState -> currentState.copy(isVideoEnabled = true) }
            VideoDisabled -> updateState { currentState -> currentState.copy(isVideoEnabled = false) }
            is LocalParticipantEvent.OnLocalDataTrackPublished -> {
                participantManager.updateLocalDataTrackPublication(localParticipantEvent.localDataTrackPublication)
            }
        }
    }

    private fun addParticipant(participant: Participant) {
        val participantViewState = buildParticipantViewState(participant)
        participantManager.addParticipant(participantViewState)
        updateParticipantViewState()
    }

    private fun showLobbyViewState() {
        action { sendEvent { RoomViewEffect.Disconnected } }
        updateState { currentState ->
            currentState.copy(configuration = Lobby)
        }
        participantManager.clearRemoteParticipants()
        updateParticipantViewState()
    }

    private fun showConnectingViewState() {
        updateState { currentState ->
            currentState.copy(configuration = RoomViewConfiguration.Connecting)
        }
    }

    private fun showConnectedViewState(roomName: String) {
        updateState { currentState ->
            currentState.copy(configuration = RoomViewConfiguration.Connected, title = roomName)
        }
    }

    private fun checkParticipants(participants: List<Participant>) {
        for ((index, participant) in participants.withIndex()) {
            if (index == 0) { // local participant
                participantManager.updateLocalParticipantSid(participant.sid)
            } else {
                participantManager.addParticipant(buildParticipantViewState(participant))
            }
        }
        updateParticipantViewState()
    }

    private fun updateParticipantViewState() {
        updateState { currentState ->
            currentState.copy(
                participantThumbnails = participantManager.participantThumbnails,
                primaryParticipant = participantManager.primaryParticipant
            )
        }
    }

    private fun connect(identity: String, roomName: String) =
        viewModelScope.launch {
            roomManager.connect(
                identity,
                roomName
            )
        }

    private fun updateState(action: (currentState: RoomViewState) -> UIState) =
        action { onState<RoomViewState> { currentState -> setState { action(currentState) } } }

    fun setIdentity(displayName: String?) {
        this.name = displayName
    }

    fun getIdentity(): String? {
        return name
    }



    @Suppress("UNCHECKED_CAST")
    class RoomViewModelFactory(
        private val roomManager: RoomManager,
        private val audioDeviceSelector: AudioSwitch,
        private val permissionUtil: PermissionUtil
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RoomViewModel(roomManager, audioDeviceSelector, permissionUtil) as T
        }
    }


}
