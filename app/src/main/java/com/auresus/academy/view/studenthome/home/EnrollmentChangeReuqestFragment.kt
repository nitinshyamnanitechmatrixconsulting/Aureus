package com.auresus.academy.view.studenthome.home

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentEnrollmentChangeRequestBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.requests.EnrollmentUpdateRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.OkDialogClickListener
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.bigkoo.pickerview.MyOptionsPickerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnrollmentChangeReuqestFragment : BaseFragment() {
    private var requestType: String = ""
    private lateinit var typeSpinner: MyOptionsPickerView<String>
    private var enrollment: Enrollment? = null
    lateinit var binding: FragmentEnrollmentChangeRequestBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val homeViewModel: HomeViewModel by viewModel()
    var enrollmentType = ""

    companion object {
        val TAG = EnrollmentChangeReuqestFragment::class.simpleName
        fun newInstance(enrollment: Enrollment, value: String): EnrollmentChangeReuqestFragment {
            var bundle = Bundle()
            bundle.putSerializable("enrollment", enrollment)
            bundle.putSerializable("enrollmentValue", value)
            var frag = EnrollmentChangeReuqestFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_enrollment_change_request
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentEnrollmentChangeRequestBinding
        initClickListener()
        if (arguments != null) {
            enrollmentType = arguments?.get("enrollmentValue") as String
            enrollment = arguments?.get("enrollment") as Enrollment?
        }
        setData()
    }

    private fun setData() {
        var name = preferenceHelper.getUserName()
        binding.textDescription.text =
            "You are requesting to change your enrollment for $name and you select $enrollmentType. Please fill out this form bekow and we will get back to you on your request as soon aa possible,"
    }

    private fun initClickListener() {
        binding.nextButton.setOnClickListener { hitTicketApi() }

    }

    private fun hitTicketApi() {
        if (Connectivity.isConnected(activity)) {
            var request = EnrollmentUpdateRequest(
                parentId = preferenceHelper[PreferenceHelper.PARENR_ID],
                enrollmentId = enrollment!!.id,
                details = enrollmentType + " " + binding.addComment.toString(),
                invoiceId = "",
                requestType = "Update Student Details",
                studentId = enrollment!!.studentId,
                subject = "Update Student Details"
            )
            homeViewModel.updateEnrollment(request)
            homeViewModel.enrollmentUpdateRequest.observe(this, ticketListObserver)
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

    private val ticketListObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                DialogUtils.showAlertDialogCallback((activity as Activity), response.data!!.message,
                    object : OkDialogClickListener {
                        override fun onOkClick(dialog: Dialog) {
                            dialog.dismiss()
                            activity?.onBackPressed()
                        }
                    })
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