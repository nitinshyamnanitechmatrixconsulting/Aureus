package com.twilio.video.app.ui.room.modal

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
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.databinding.LayoutMeetingOptionBinding
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.ui.room.ParticipantAdapter
import kotlin.math.roundToInt

class MeetingOptionBottomSheetFragment(val activity: Activity,val meettingOptionHandler: MeettingOptionHandler, val roomViewModel: RoomViewModel) : SuperBottomSheetFragment() {

    private lateinit var binding: LayoutMeetingOptionBinding

    companion object {
        private var instance: MeetingOptionBottomSheetFragment? = null
        @JvmStatic
        fun openMeetingOption(activity: FragmentActivity,meettingOptionHandler: MeettingOptionHandler,roomViewModel: RoomViewModel) {
            if (instance == null) {
                instance = MeetingOptionBottomSheetFragment(activity, meettingOptionHandler, roomViewModel)
            }
            instance?.show(activity.supportFragmentManager, "MeetingOptionBottomSheetFragment")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutMeetingOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivScreenshare.setTag(R.id.imageTag,1)
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
            dismiss()
            meettingOptionHandler.handleShowMessages()
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

    override fun getBackgroundColor()=activity.resources.getColor(R.color.color_353b3e)

    override fun getExpandedHeight() =activity.resources.getDimension(R.dimen.margin_200).roundToInt()

    override fun getPeekHeight()= activity.resources.getDimension(R.dimen.margin_250).roundToInt()



}
