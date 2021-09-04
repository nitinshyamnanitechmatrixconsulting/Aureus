package com.auresus.academy.view.notification

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityNotificationBinding
import com.auresus.academy.model.bean.responses.NotificationList
import com.auresus.academy.model.bean.responses.NotificationListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class NotificationFragment : BaseFragment() {

    private lateinit var mNotificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityNotificationBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val notificationViewModel: NotificationViewModel by viewModel()

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as ActivityNotificationBinding
        initClickListener()
        initRecyclerView()
        hitNotificationApi()
    }


    companion object {
        val TAG = NotificationFragment::class.simpleName
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }

    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mNotificationAdapter = NotificationAdapter(mutableListOf(),
            object : INotificationItemListener {
                override fun itemClick(notificationItem: NotificationList) {
                    NotificationDetailsAcitivty.open(
                        activity as BaseActivity,
                        notificationItem
                    )
                }
            })
        binding.notificationRV.adapter = mNotificationAdapter
    }

    private fun hitNotificationApi() {
        if (Connectivity.isConnected(activity)) {
            notificationViewModel.getNotificationList(preferenceHelper[PreferenceHelper.PARENR_ID])
            notificationViewModel.notificationRequest.observe(this, notificationListObserver)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
            (activity as HomeAcitivty).onBackPressed()
        }
    }

    private val notificationListObserver: Observer<ApiResponse<NotificationListResponse>> by lazy {
        Observer<ApiResponse<NotificationListResponse>> {
            it?.let {
                handleNotificationResponse(it)
            }
        }
    }

    private fun handleNotificationResponse(response: ApiResponse<NotificationListResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null && response.data.notifications.isNotEmpty())
                    mNotificationAdapter.setList(response.data.notifications)
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    activity,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}