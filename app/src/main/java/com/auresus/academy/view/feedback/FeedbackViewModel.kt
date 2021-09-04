package com.auresus.academy.view.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.PublicMeetingFeedbackRequest
import com.auresus.academy.model.bean.responses.ForgotPasswordResponse
import com.auresus.academy.model.bean.responses.GetAccessTokenResponse
import com.auresus.academy.model.bean.responses.MakeupListResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.model.repo.AuthenticationRepository
import com.auresus.academy.view_model.BaseViewModel

class FeedbackViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _publicMeetingFeedbackMeetingResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }

    private val _meetingFeedbackMeetingResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }

    val publicMeetingFeedbackMeetingResponse: LiveData<ApiResponse<String>> =
        _publicMeetingFeedbackMeetingResponse

    val meetingFeedbackMeetingResponse: LiveData<ApiResponse<String>> =
        _meetingFeedbackMeetingResponse

    fun meetingFeedbackGuest(request: PublicMeetingFeedbackRequest) {
        authenticationRepository.meetingFeedbackGuest(request,_publicMeetingFeedbackMeetingResponse)
    }

    fun meetingFeedback(person_name:String,booking_id:String,rating:String,person_id:String) {
        authenticationRepository.meetingFeedback(person_name,booking_id,rating,person_id,_meetingFeedbackMeetingResponse)
    }
}