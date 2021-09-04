package com.auresus.academy.view.notification

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityNotificationDetailsBinding
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.NotificationList
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.OkDialogClickListener
import com.auresus.academy.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class NotificationDetailsAcitivty : BaseActivity() {

    private var notificationItem: NotificationList? = null
    private lateinit var mNotificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityNotificationDetailsBinding
    private val notificationViewModel: NotificationViewModel by viewModel()

    companion object {
        fun open(currActivity: BaseActivity, notification: NotificationList) {
            currActivity.run {
                val bundle = Bundle()
                bundle.putSerializable("notificationItem", notification)
                val intent = Intent(this, NotificationDetailsAcitivty::class.java)
                intent.putExtra("extra", bundle);
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_notification_details
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityNotificationDetailsBinding
        initClickListener()
        if (intent != null) {
            var bundle: Bundle? = intent.getBundleExtra("extra")
            notificationItem = bundle?.get("notificationItem") as NotificationList?
        }
        if (notificationItem != null)
            setData()
    }

    private fun setData() {
        binding.notificationDate.text =
            DateTimeUtil.notificationDateShort(notificationItem!!.createdDate)
        binding.notificationDescrption.text = notificationItem?.bodymsg
        binding.notificationTitle.text = notificationItem?.title
        if (!notificationItem!!.isRead)
            readNotification()
    }

    private fun readNotification() {
        if (Connectivity.isConnected(this)) {
            var list=ArrayList<String>();
            list.add(notificationItem!!.notificationId)
            notificationViewModel.getNotificationRead(list)
            notificationViewModel.notificationReadRequest.observe(
                this,
                notificationReadObserver
            )
        }
    }

    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.deleteNotificaions.setOnClickListener {
            deleteNotification()
        }
    }

    private fun deleteNotification() {
        if (Connectivity.isConnected(this)) {
            notificationViewModel.getNotificationDelete(notificationItem!!.notificationId)
            notificationViewModel.notificationDeleteRequest.observe(
                this,
                notificationDeleteObserver
            )
        } else {
            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    private val notificationDeleteObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleNotificationResponse(it)
            }
        }
    }
    private val notificationReadObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
               // handleReadNotificationResponse(it)
            }
        }
    }

    private fun handleReadNotificationResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                Toast.makeText(
                    this,
                    response.data!!.message,
                    Toast.LENGTH_LONG
                ).show()
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    response.data!!.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleNotificationResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                DialogUtils.showAlertDialogCallback(
                    this,
                    response.data!!.message,
                    object : OkDialogClickListener {
                        override fun onOkClick(dialog: Dialog) {
                            this@NotificationDetailsAcitivty.finish()
                        }
                    }
                )
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