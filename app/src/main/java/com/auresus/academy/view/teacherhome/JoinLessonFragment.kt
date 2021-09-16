package com.auresus.academy.view.teacherhome

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentJoinLessonBinding
import com.auresus.academy.model.bean.requests.MeetingServiceRequest
import com.auresus.academy.model.bean.responses.MeetingServiceResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.DashboardViewModel
import com.twilio.video.app.ui.room.RoomActivity
import kotlinx.android.synthetic.main.fragment_join_lesson.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class JoinLessonFragment : BaseFragment() {
    private lateinit var homeBinding: FragmentJoinLessonBinding
    private val dashboardViewModel: DashboardViewModel by viewModel()
    val preferenceHelper: PreferenceHelper by inject()

    companion object {
        val TAG = "JoinLessonFragment"
        val MEETING_CODE = "MEETING_CODE"
        fun newInstance(meetingcode: String?): JoinLessonFragment {
            val homeFragment = JoinLessonFragment()
            homeFragment.arguments = Bundle().apply {
                putString(MEETING_CODE, meetingcode)
            }
            return homeFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_join_lesson
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        homeBinding = binding as FragmentJoinLessonBinding
        val meetingcode = arguments?.getString(MEETING_CODE)
        meetingcode?.let {
            binding.editTextOnlineMarketingCode.setText(it)
        }
        homeBinding.continueBtn.setOnClickListener {
            proceedForMeetingService()
        }
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        setMeetingServiceObserver()

        binding.editTextName.setText(preferenceHelper.getUserName())

    }

    private fun setMeetingServiceObserver() {
        dashboardViewModel.meetingServiceResponse.observe(this,
            object : Observer<ApiResponse<MeetingServiceResponse>> {
                override fun onChanged(t: ApiResponse<MeetingServiceResponse>) {
                    handleMeetingServiceApiResponse(t)
                }

            })
    }

    private fun handleMeetingServiceApiResponse(apiResponse: ApiResponse<MeetingServiceResponse>) {
        showLoader(apiResponse.status == ApiResponse.Status.LOADING)
        when (apiResponse.status) {
            ApiResponse.Status.SUCCESS -> handleSuccess(apiResponse.data)
            ApiResponse.Status.ERROR -> handleError()
        }
    }

    private fun handleError() {
        Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()

    }

    private fun handleSuccess(data: MeetingServiceResponse?) {
        data?.let {
            val rooomId = it.Id
            val name = homeBinding.editTextName.text.toString()
            val meetingCodeLocal = homeBinding.editTextOnlineMarketingCode.text.toString()
            val meetingCodeRemote = it.Online_Lesson_Passcode__c
            val type = "3"
            if (meetingCodeLocal == meetingCodeRemote) {
                it.Online_Lesson_URL__c?.let {
                    val uri = Uri.parse(it)
                    val roomName = uri.getQueryParameter("room_name")
                    roomName?.let {
                        RoomActivity.open(
                            activity as BaseActivity,
                            roomName,
                            rooomId,
                            name,
                            meetingCodeLocal,
                            type
                        )
                    }
                }
            } else {
                Toast.makeText(activity, getString(R.string.passcode_does_not_match), Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun showLoader(isShow: Boolean) {
        loader.isVisible = isShow
    }

    private fun proceedForMeetingService() {
        val isValid = true
        val meetingcode = homeBinding.editTextOnlineMarketingCode.text.toString()
        val bookingId = homeBinding.editTextBookingId.text.toString()
        val name = homeBinding.editTextName.text.toString()
        callMeetingServiceApi(meetingcode,name,bookingId)

    }

    private fun callMeetingServiceApi(meetingcode: String, name: String, bookingId: String) {
        val meetingRequest = MeetingServiceRequest(bookingId)
       // val meetingRequest = MeetingServiceRequest("android", "", "a012s0000022wu1AAA")
        dashboardViewModel.createMeetingService(meetingRequest)
    }
}