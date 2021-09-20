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
import com.google.android.material.tabs.TabLayout
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.databinding.LayoutInfoBinding
import com.twilio.video.app.databinding.LayoutMeetingOptionBinding
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.ui.room.ParticipantAdapter
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class LessionInfoSheetFragment(val activity: Activity, meettingOptionHandler: MeettingOptionHandler, val roomViewModel: RoomViewModel) : SuperBottomSheetFragment() {

    private lateinit var adapter: LessonInfoPageAdapter
    private lateinit var binding: LayoutInfoBinding

    companion object {
        private var instance: LessionInfoSheetFragment? = null
        @JvmStatic
        fun open(activity: FragmentActivity,meettingOptionHandler: MeettingOptionHandler,roomViewModel: RoomViewModel) {
            LessionInfoSheetFragment(activity,meettingOptionHandler, roomViewModel)?.show(activity.supportFragmentManager, "LessionInfoSheetFragment")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabViewPager()
    }

    private fun initTabViewPager() {
        val firstTabTitle =getString(R.string.lession_details)
        val secondTabTitle =getString(R.string.help)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(firstTabTitle))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(secondTabTitle))
        binding.backButton.setOnClickListener { dismiss() }
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        adapter = LessonInfoPageAdapter(activity, childFragmentManager, binding.tabLayout.tabCount,roomViewModel)
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    override fun getCornerRadius() =
        activity.resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED

    override fun getExpandedHeight() =activity.resources.getDimension(R.dimen.margin_420).roundToInt()

    override fun getPeekHeight()= activity.resources.getDimension(R.dimen.margin_420).roundToInt()
}
