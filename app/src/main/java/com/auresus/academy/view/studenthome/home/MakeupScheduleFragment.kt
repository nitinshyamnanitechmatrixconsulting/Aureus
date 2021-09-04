package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityNotificationBinding
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.NotificationList
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.notification.INotificationItemListener
import com.auresus.academy.view.notification.NotificationViewModel
import com.auresus.academy.view.studenthome.HomeAcitivty
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MakeupScheduleFragment : BaseFragment() {

    private lateinit var mNotificationAdapter: MakeupSchedukeAdapter
    private lateinit var binding: ActivityNotificationBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val notificationViewModel: NotificationViewModel by viewModel()
    private var lessonDetails: List<Student>? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as ActivityNotificationBinding
        initClickListener()
        initRecyclerView()
        if (arguments != null)
            lessonDetails = arguments?.getParcelableArrayList("lessonDetails")
        if (lessonDetails != null) {
            mNotificationAdapter.setList(lessonDetails)
        }
    }


    companion object {
        val TAG = MakeupScheduleFragment::class.simpleName
        fun newInstance(lessonDetails: ArrayList<Student>): MakeupScheduleFragment {
            var bundle = Bundle()
            bundle.putParcelableArrayList("lessonDetails", lessonDetails)
            var frag = MakeupScheduleFragment()
            frag.arguments = bundle
            return frag
        }
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }

    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mNotificationAdapter = MakeupSchedukeAdapter(mutableListOf(),
            object : INotificationItemListener {
                override fun itemClick(notificationItem: NotificationList) {

                }
            })
        binding.notificationRV.adapter = mNotificationAdapter
    }


}