package com.auresus.academy.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.GetBookingListRequest
import com.auresus.academy.model.bean.requests.MeetingServiceRequest
import com.auresus.academy.model.bean.requests.NotificationUpdateRequest
import com.auresus.academy.model.bean.responses.*
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.StudentHomeRepository


class DashboardViewModel constructor(private val homeRepository: StudentHomeRepository) :
    BaseViewModel() {

    private val _bookingListResponse by lazy {
        MutableLiveData<ApiResponse<BookingListResponse>>()
    }

    private val _accessTokenResponse by lazy {
        MutableLiveData<ApiResponse<GetAccessTokenResponse>>()
    }

    private val _attachmentListResponse by lazy {
        MutableLiveData<ApiResponse<List<Attachment>>>()
    }

    private val _meetingServiceResponse by lazy {
        MutableLiveData<ApiResponse<MeetingServiceResponse>>()
    }
    private val _notificationUpdateResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }
    private val _notificationDetailsAllResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    val notificationDeleteAllRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _notificationDetailsAllResponse



    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val bookingListResponse: LiveData<ApiResponse<BookingListResponse>> = _bookingListResponse
    val attachmentListResponse: LiveData<ApiResponse<List<Attachment>>> = _attachmentListResponse
    val accessTokenResponse: LiveData<ApiResponse<GetAccessTokenResponse>> = _accessTokenResponse
    val meetingServiceResponse: LiveData<ApiResponse<MeetingServiceResponse>> = _meetingServiceResponse
    val notificationUpdateRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _notificationUpdateResponse

    fun getBookingList(getBookingListRequest: GetBookingListRequest) {
        homeRepository.getBookingList(getBookingListRequest, _bookingListResponse)
    }

    fun getAccessToken(roomName:String ,name:String) {
        homeRepository.getAccessTokenTwillio(roomName ,name,_accessTokenResponse)
    }


    fun getAttachmentList(bookingRoomName: String){
        homeRepository.getAttachmentList(bookingRoomName, _attachmentListResponse)
    }

    fun createMeetingService(meetingServiceRequest: MeetingServiceRequest){
        homeRepository.meetingService(meetingServiceRequest, _meetingServiceResponse)
    }


    fun createBookingRequest(enrollId: String,bookingType:String ,bookingListOffset:Int ,bookingListLimit:Int): GetBookingListRequest {
        val request = GetBookingListRequest().apply {
            Limit = bookingListLimit
            enrollmentId = enrollId
            offset = bookingListOffset
            type = bookingType
        }
        return request
    }

    fun logout(request: NotificationUpdateRequest) {
        homeRepository.logout(request, _notificationUpdateResponse)
    }

    fun getNotificationDelete(request: String) {
        homeRepository.getNotificationDeleteAll(request, _notificationDetailsAllResponse)
    }


}