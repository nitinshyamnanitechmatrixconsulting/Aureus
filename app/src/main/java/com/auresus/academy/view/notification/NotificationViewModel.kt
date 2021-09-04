package com.auresus.academy.view.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.NotificationDetailsRequestRead
import com.auresus.academy.model.bean.requests.NotificationUpdateRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.NotificationListResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class NotificationViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _notificationListResponse by lazy {
        MutableLiveData<ApiResponse<NotificationListResponse>>()
    }
    private val _notificationDetailsResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }
    private val _notificationReadResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    private val _notificationUpdateResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val notificationRequest: LiveData<ApiResponse<NotificationListResponse>> =
        _notificationListResponse

    val notificationDeleteRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _notificationDetailsResponse
    val notificationReadRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _notificationReadResponse

    val notificationUpdateRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _notificationUpdateResponse


    fun getNotificationList(request: String) {
        authenticationRepository.getNotificationList(request, _notificationListResponse)
    }

    fun getNotificationDelete(request: String) {
        authenticationRepository.getNotificationDelete(request, _notificationDetailsResponse)
    }

    fun getNotificationRead(request: List<String>) {
        authenticationRepository.getNotificationRead(request, _notificationReadResponse)
    }

    fun updateNotification(request: NotificationUpdateRequest) {
        authenticationRepository.updateNotification(request, _notificationUpdateResponse)
    }

}