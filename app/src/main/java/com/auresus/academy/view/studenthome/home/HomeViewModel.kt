package com.auresus.academy.view.studenthome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.EnrollmentUpdateRequest
import com.auresus.academy.model.bean.requests.LessonConvertRequest
import com.auresus.academy.model.bean.responses.InvoiceListResponse
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class HomeViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _lessonDetailsResponse by lazy {
        MutableLiveData<ApiResponse<InvoiceListResponse>>()
    }

    private val _lessonConvertOnlineResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    private val _lessonConvertOfflineResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    private val _updateEnrollmentResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val lessonDetailsRequest: LiveData<ApiResponse<InvoiceListResponse>> =
        _lessonDetailsResponse

    val lessonConvertOnlineRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _lessonConvertOnlineResponse

    val lessonConvertOfflineRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _lessonConvertOfflineResponse

    val enrollmentUpdateRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _updateEnrollmentResponse

    fun lessonDetails(request: String) {
        authenticationRepository.lessonDetails(request, _lessonDetailsResponse)
    }

    fun lessonConvertOnline(request: LessonConvertRequest) {
        authenticationRepository.lessonConvertOnline(request, _lessonConvertOnlineResponse)
    }

    fun lessonConvertOffline(request: LessonConvertRequest) {
        authenticationRepository.lessonConvertOffline(request, _lessonConvertOnlineResponse)
    }

    fun updateEnrollment(request: EnrollmentUpdateRequest) {
        authenticationRepository.updateEnrollment(request, _updateEnrollmentResponse)
    }

}