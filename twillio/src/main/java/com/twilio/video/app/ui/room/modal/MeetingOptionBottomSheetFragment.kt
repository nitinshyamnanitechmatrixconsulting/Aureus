package com.twilio.video.app.ui.room.modal

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.twilio.video.LocalVideoTrack
import com.twilio.video.RemoteVideoTrack
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.databinding.LayoutMeetingOptionBinding
import com.twilio.video.app.participant.ParticipantViewState
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.ui.room.ParticipantAdapter
import kotlin.math.roundToInt
import com.twilio.video.app.ui.room.RoomEvent.RemoteParticipantEvent.ScreenTrackUpdated


class MeetingOptionBottomSheetFragment(
    val activity: Activity,
    val meettingOptionHandler: MeettingOptionHandler,
    val roomViewModel: RoomViewModel
) : SuperBottomSheetFragment() {

    private lateinit var binding: LayoutMeetingOptionBinding

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MeetingOptionBottomSheetFragment? = null
        @SuppressLint("StaticFieldLeak")
        var ivMsg: ImageView? = null

        @JvmStatic
        fun openMeetingOption(
            activity: FragmentActivity,
            meettingOptionHandler: MeettingOptionHandler,
            roomViewModel: RoomViewModel
        ) {
            if (instance == null) {
                instance =
                    MeetingOptionBottomSheetFragment(activity, meettingOptionHandler, roomViewModel)
            }
            instance?.show(activity.supportFragmentManager, "MeetingOptionBottomSheetFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutMeetingOptionBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!RoomActivity.isActive && !RoomActivity.isFirstTime) {
            binding.ivMsg.setImageResource(R.drawable.new_message_alert)
        } else {
            binding.ivMsg.setImageResource(R.drawable.messages_active)
        }
        if (!RoomActivity.isBordcasting) {
            binding.tvShareScreen.text = "Share Screen"
        } else {
            binding.tvShareScreen.text = "Stop Sharing"
        }
        ivMsg = binding.ivMsg
        // binding.ivScreenshare.setTag(R.id.imageTag, 1)
        binding.showFiles.setOnClickListener {
            dismiss()
            meettingOptionHandler.handleOpenFiles()
        }
        binding.showInfo.setOnClickListener {
            dismiss()
            meettingOptionHandler.handleShowInfo()
        }
        binding.showLayoutOption.setOnClickListener {
            dismiss()
            meettingOptionHandler.handleShowLayoutOption()
        }

        binding.showMessage.setOnClickListener {
            RoomActivity.isFirstTime = true
            ivMsg!!.setImageResource(R.drawable.messages_active)
            dismiss()
            meettingOptionHandler.handleShowMessages()
        }


         if(roomViewModel.sharing!=null ){
             binding.showPresentScreen.visibility=View.GONE
         }else{
             binding.showPresentScreen.visibility=View.VISIBLE
         }

        binding.showPresentScreen.setOnClickListener {
            dismiss()
            meettingOptionHandler.handleScreenShare(binding.ivScreenshare)
        }

        binding.showParticipants.setOnClickListener {
            dismiss()
            meettingOptionHandler.handleOpenShowParticipants()
        }

    }

    override fun getCornerRadius() =
        activity.resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED

    override fun getBackgroundColor() = activity.resources.getColor(R.color.color_353b3e)

    override fun getExpandedHeight() =
        activity.resources.getDimension(R.dimen.margin_200).roundToInt()

    override fun getPeekHeight() = activity.resources.getDimension(R.dimen.margin_250).roundToInt()


}
