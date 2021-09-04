package com.auresus.academy.view.joinclass

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityJoinOnlineSessionBinding
import com.auresus.academy.model.bean.requests.MeetingServiceRequest
import com.auresus.academy.model.bean.responses.MeetingServiceResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view_model.DashboardViewModel
import com.twilio.video.app.ui.room.RoomActivity
import kotlinx.android.synthetic.main.fragment_join_lesson.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class JoinOnlineClassAcitivty : BaseActivity() {

    private lateinit var binding: ActivityJoinOnlineSessionBinding
    private val dashboardViewModel: DashboardViewModel by viewModel()

    var link :String? = ""

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                startActivity(Intent(this, JoinOnlineClassAcitivty::class.java))
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_join_online_session
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityJoinOnlineSessionBinding
        initClickListener()
        setMeetingServiceObserver()
        val deeplink = intent.data
        if (deeplink != null) {
            link = (deeplink.toString().split("=")[1]).toString()
            binding.editTextBookingId.setText(link)
            //Toast.makeText(this,link,Toast.LENGTH_LONG).show()
        }
    }

    private fun initClickListener() {
        binding.joinOnlineSessionImage.setOnClickListener {
            //  Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.joinSession.setOnClickListener {
            proceedForMeetingService()

        }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun proceedForMeetingService() {
        val isValid = true
        val meetingcode = binding.editTextonlineMeetingCode.text.toString()
        val bookingId = binding.editTextBookingId.text.toString()
        val name = binding.editTextName.text.toString()
        callMeetingServiceApi(meetingcode, name, bookingId)
    }

    private fun callMeetingServiceApi(meetingcode: String, name: String, bookingId: String) {
        val meetingRequest = MeetingServiceRequest(bookingId)
        dashboardViewModel.createMeetingService(meetingRequest)
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

    private fun showLoader(isShow: Boolean) {
        loader.isVisible = isShow
    }

    private fun handleSuccess(data: MeetingServiceResponse?) {
        data?.let {
            val rooomId = it.Id
            val name = binding.editTextName.text.toString()
            val meetingCodeLocal = binding.editTextonlineMeetingCode.text.toString()
            val meetingCodeRemote = it.Online_Lesson_Passcode__c
            if (meetingCodeLocal == meetingCodeRemote) {
                it.Online_Lesson_URL__c?.let {
                    val uri = Uri.parse(it)
                    val roomName = uri.getQueryParameter("room_name")
                    roomName?.let {
                        RoomActivity.open(
                            this as BaseActivity,
                            roomName,
                            rooomId,
                            name,
                            meetingCodeLocal
                        )
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.passcode_does_not_match), Toast.LENGTH_LONG)
                    .show()
            }


        }
    }

    private fun handleError() {
        Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
    }

}