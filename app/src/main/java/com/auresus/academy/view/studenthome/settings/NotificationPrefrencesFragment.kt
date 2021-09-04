package com.auresus.academy.view.studenthome.settings

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentNotificationPrefBinding
import com.auresus.academy.model.bean.requests.NotificationUpdateRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.notification.NotificationViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationPrefrencesFragment : BaseFragment() {
    lateinit var binding: FragmentNotificationPrefBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val notificationViewModel: NotificationViewModel by viewModel()

    companion object {
        val TAG = NotificationPrefrencesFragment::class.simpleName
        fun newInstance(): NotificationPrefrencesFragment {
            return NotificationPrefrencesFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification_pref
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentNotificationPrefBinding
        binding.bookingCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                updateNotification("Booking_Reminded_Notification__c", "true")
            else
                updateNotification("Booking_Reminded_Notification__c", "false")
        }

        binding.billingCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                updateNotification("Invoice_Paid_Notification__c", "true")
            else
                updateNotification("Invoice_Paid_Notification__c", "false")
        }
        binding.bookingNotifcationRl.setOnClickListener {
            binding.bookingNotificationInfo.visibility =
                if (binding.bookingNotificationInfo.isVisible) View.GONE else View.VISIBLE
        }
        binding.billingNoficationRl.setOnClickListener {
            binding.billingNotificationInfo.visibility =
                if (binding.billingNotificationInfo.isVisible) View.GONE else View.VISIBLE
        }
    }


    private fun updateNotification(fieldApi: String, newValue: String) {
        if (Connectivity.isConnected(activity)) {
            var request = NotificationUpdateRequest(
                recordId = preferenceHelper[PreferenceHelper.PARENR_ID],
                fieldApi = fieldApi,
                newValue = newValue
            )
            notificationViewModel.updateNotification(request)
            notificationViewModel.notificationDeleteRequest.observe(
                this,
                notificationDeleteObserver
            )
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
            activity?.finish()
        }
    }

    private val notificationDeleteObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleNotificationResponse(it)
            }
        }
    }

    private fun handleNotificationResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
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