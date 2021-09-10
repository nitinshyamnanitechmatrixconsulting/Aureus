package com.twilio.video.app.ui.room.modal

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentLayoutOptionBinding
import com.twilio.video.app.ui.room.RoomViewModel
import kotlin.math.roundToInt

class LayoutOptionSheetFragment(
    val activity: Activity,
    val meetingOptionHandler: MeettingOptionHandler,
    val roomViewModel: RoomViewModel
) : SuperBottomSheetFragment() {

    private lateinit var binding: FragmentLayoutOptionBinding

    companion object {
        private var instance: LayoutOptionSheetFragment? = null
        const val SPLIT = 1
        const val DEFAULT = 2

        @JvmStatic
        fun openMeetingOption(
            activity: FragmentActivity,
            meetingOptionHandler: MeettingOptionHandler,
            roomViewModel: RoomViewModel
        ) {
            if (instance == null) {
                instance = LayoutOptionSheetFragment(activity, meetingOptionHandler, roomViewModel)
            }
            instance?.show(activity.supportFragmentManager, "LayoutOptionSheetFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLayoutOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutOptionDefault.setOnClickListener {
            meetingOptionHandler.switchLayout(DEFAULT)
            dismiss()
        }
        binding.layoutOptionSplit.setOnClickListener {
            meetingOptionHandler.switchLayout(SPLIT)
            dismiss()
        }
        binding.backButton.setOnClickListener {
            dismiss()
        }

    }

    override fun getCornerRadius() =
        activity.resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED

    override fun getBackgroundColor() = activity.resources.getColor(R.color.color_353b3e)

    override fun getExpandedHeight() =
        activity.resources.getDimension(R.dimen.margin_200).roundToInt()

    override fun getPeekHeight() = activity.resources.getDimension(R.dimen.margin_200).roundToInt()


}
