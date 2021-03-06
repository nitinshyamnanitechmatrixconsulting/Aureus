package com.twilio.video.app.ui.room

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.twilio.video.NetworkQualityLevel
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FOUR
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ONE
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_THREE
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_TWO
import com.twilio.video.NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ZERO
import com.twilio.video.VideoTrack
import com.twilio.video.app.R
import com.twilio.video.app.participant.ParticipantViewState
import com.twilio.video.app.sdk.VideoTrackViewState
import com.twilio.video.app.ui.room.RoomViewEvent.PinParticipant
import timber.log.Timber

internal class BottomParticipantViewHolder(
    private val thumb: ParticipantBottomThumbView,
    private val roomViewModel: RoomViewModel
) :
    RecyclerView.ViewHolder(thumb) {

    //    private val localParticipantIdentity = thumb.context.getString(R.string.you)

    //   private val localParticipantIdentity = roomViewModel.name

    var swipeLayout: SwipeLayout? = null
    var llMain: LinearLayout? = null
    var view: View? = null

    fun bind(participantViewState: ParticipantViewState, viewEventAction: (RoomViewEvent) -> Unit) {
        Timber.d("bind ParticipantViewHolder with data item: %s", participantViewState)
        Timber.d("thumb: %s", thumb)

        thumb.run {
            participantViewState.sid?.let { sid ->
                setOnClickListener {
                    viewEventAction(PinParticipant(sid))
                }
            }



            swipeLayout = thumb.findViewById(R.id.swipe)
            llMain = thumb.findViewById(R.id.llMain)
            view = thumb.findViewById(R.id.view)
            swipeLayout!!.isClickable=false


            llMain!!.setOnClickListener { viewEventAction(participantViewState.sid?.let { it1 ->
                PinParticipant(
                    it1
                )
            }!!) }


            if (participantViewState.isLocalParticipant) {
                view?.visibility = View.VISIBLE
            } else {
                view?.visibility = View.GONE
            }

            if (participantViewState.isLocalParticipant) {
                swipeLayout!!.isSwipeEnabled = false
            }

            if (roomViewModel.type.equals("2") || roomViewModel.type.equals("3")) {
                swipeLayout!!.isSwipeEnabled = false
            }

            /* if (roomViewModel.type.equals("3")) {
                 swipeLayout!!.isSwipeEnabled = false
             }*/


            val identity = if (participantViewState.isLocalParticipant)
                roomViewModel.name else participantViewState.identity

            if (roomViewModel.type.equals("1")) {
                thumb.findViewById<ImageView>(R.id.mute).setOnClickListener {
                    val muteParticipantMessage = MessageCommand.muteParticipantRequest(identity!!)
                    roomViewModel.processInput(RoomViewEvent.SendMessage(muteParticipantMessage))
                    // toggleLocalAudio()
                }
                thumb.findViewById<ImageView>(R.id.remove).setOnClickListener {
                    val removeParticipantMessage = MessageCommand.removeParticipant(identity!!)
                    roomViewModel.processInput(RoomViewEvent.SendMessage(removeParticipantMessage))
                }
            }

            setIdentity(identity)
            setMuted(participantViewState.isMuted)
            setPinned(participantViewState.isPinned)

            updateVideoTrack(participantViewState)

            networkQualityLevelImg?.let {
                setNetworkQualityLevelImage(it, participantViewState.networkQualityLevel)
            }


        }
    }

    private fun toggleLocalAudio() {
        roomViewModel.processInput(RoomViewEvent.ToggleLocalAudio)
    }

    private fun updateVideoTrack(participantViewState: ParticipantViewState) {
        thumb.run {
            val videoTrackViewState = participantViewState.videoTrack
            val newVideoTrack = videoTrackViewState?.let { it.videoTrack }
            if (videoTrack !== newVideoTrack) {
                removeRender(videoTrack, this)
                videoTrack = newVideoTrack
                videoTrack?.let { videoTrack ->
                    setVideoState(videoTrackViewState)
                    if (videoTrack.isEnabled) videoTrack.addSink(this)
                } ?: setState(ParticipantView.State.NO_VIDEO)
            } else {
                setVideoState(videoTrackViewState)
            }
        }
    }

    private fun ParticipantBottomThumbView.setVideoState(videoTrackViewState: VideoTrackViewState?) {
        if (videoTrackViewState?.let { it.isSwitchedOff } == true) {
            setState(ParticipantView.State.SWITCHED_OFF)
        } else {
            videoTrackViewState?.videoTrack?.let { setState(ParticipantView.State.VIDEO) }
                ?: setState(ParticipantView.State.NO_VIDEO)
        }
    }

    private fun removeRender(videoTrack: VideoTrack?, view: ParticipantView) {
        if (videoTrack == null || !videoTrack.sinks.contains(view)) return
        videoTrack.removeSink(view)
    }

    private fun setNetworkQualityLevelImage(
        networkQualityImage: ImageView,
        networkQualityLevel: NetworkQualityLevel?
    ) {
        when (networkQualityLevel) {
            NETWORK_QUALITY_LEVEL_ZERO -> R.drawable.network_quality_level_0
            NETWORK_QUALITY_LEVEL_ONE -> R.drawable.network_quality_level_1
            NETWORK_QUALITY_LEVEL_TWO -> R.drawable.network_quality_level_2
            NETWORK_QUALITY_LEVEL_THREE -> R.drawable.network_quality_level_3
            NETWORK_QUALITY_LEVEL_FOUR -> R.drawable.network_quality_level_4
            NETWORK_QUALITY_LEVEL_FIVE -> R.drawable.network_quality_level_5
            else -> null
        }?.let { image ->
            networkQualityImage.visibility = View.VISIBLE
            networkQualityImage.setImageResource(image)
        } ?: run { networkQualityImage.visibility = View.GONE }
    }


}
