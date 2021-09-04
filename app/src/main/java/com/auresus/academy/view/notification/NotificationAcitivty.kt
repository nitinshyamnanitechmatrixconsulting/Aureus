package com.auresus.academy.view.notification

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class NotificationAcitivty : BaseActivity() {

    private lateinit var mNotificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityNotificationBinding
    private val notificationViewModel: NotificationViewModel by viewModel()

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                startActivity(Intent(this, NotificationAcitivty::class.java).apply {
                })
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_notification
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityNotificationBinding
        initClickListener()
        initRecyclerView()
        hitNotificationApi()
    }

    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(this)
        mNotificationAdapter = NotificationAdapter(mutableListOf(),
            object : INotificationItemListener {
                override fun itemClick(notificationItem: NotificationList) {
                    NotificationDetailsAcitivty.open(
                        this@NotificationAcitivty,
                        notificationItem
                    )
                }
            })
        binding.notificationRV.adapter = mNotificationAdapter
    }

    private fun hitNotificationApi() {
        if (Connectivity.isConnected(this)) {
            notificationViewModel.getNotificationList(preferenceHelper[PreferenceHelper.PARENR_ID])
            notificationViewModel.notificationRequest.observe(this, notificationListObserver)
        } else {
            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
                .show()
            finish()
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
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}