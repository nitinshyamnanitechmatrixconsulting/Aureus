package com.twilio.video.app.ui.room.modal

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.databinding.LayoutMeetingOptionBinding
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.ui.room.ParticipantAdapter

class FileBottomSheetFragment(val activity: Activity,meettingOptionHandler: MeettingOptionHandler, val roomViewModel: RoomViewModel) : SuperBottomSheetFragment() {

    private lateinit var binding: LayoutMeetingOptionBinding

    companion object {
        private var instance: FileBottomSheetFragment? = null
        @JvmStatic
        fun open(activity: FragmentActivity,meettingOptionHandler: MeettingOptionHandler,roomViewModel: RoomViewModel) {
            if (instance == null) {
                instance = FileBottomSheetFragment(activity,meettingOptionHandler, roomViewModel)
            }
            instance?.show(activity.supportFragmentManager, "FileBottomSheetFragment")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutMeetingOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getCornerRadius() =
        activity.resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED
}
