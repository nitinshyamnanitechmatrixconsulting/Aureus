package com.auresus.academy.model.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.AureusApplication
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.model.bean.requests.*
import com.auresus.academy.model.bean.responses.*
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.local.room_db.dao.ContactDao
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.remote.ApiServices
import com.auresus.academy.model.remote.DataFetchCall
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/** Created by Sahil Bharti on 26/8/19.
 *
 *Copyright (c) 2019 Sahil Inc. All rights reserved.
 */
class AppRepository(
    private val apiServices: ApiServices,
    private val preferences: PreferenceHelper,
    private val contactDao: ContactDao
) {

    fun getHomeData(): MutableLiveData<StudentLoginResponse> {
        return AureusApplication.getInstance().getStudentLoginLiveData()
    }

    fun getTeacherHomeData(): MutableLiveData<TeacherLoginResponse> {
        return AureusApplication.getInstance().getTeacherLoginLiveData()
    }

    fun getNotificationList(
        parentId: String,
        loginResponse: MutableLiveData<ApiResponse<NotificationListResponse>>
    ) {
        object : DataFetchCall<NotificationListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationListResponse> {
                return apiServices.notificationList(parentId)
            }
        }.execute()
    }

    fun getNotificationDelete(
        notificationID: String,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                var request = NotificationDetailsRequest()
                request.notificationId = notificationID
                return apiServices.notificationDelete(request)
            }
        }.execute()
    }

    fun getNotificationRead(
        notificationID: List<String>,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                var request = NotificationDetailsRequestRead()
                request.notificationId = notificationID
                return apiServices.notificationReadArray(request)
            }
        }.execute()
    }

    fun getInvoiceList(
        parentId: String,
        loginResponse: MutableLiveData<ApiResponse<InvoiceListResponse>>
    ) {
        object : DataFetchCall<InvoiceListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<InvoiceListResponse> {
                return apiServices.invoiceList(parentId)
            }
        }.execute()
    }

    fun getTicketList(
        request: TicketRequest,
        loginResponse: MutableLiveData<ApiResponse<TicketListResponse>>
    ) {
        object : DataFetchCall<TicketListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<TicketListResponse> {
                return apiServices.ticketList(
                    request.parentId,
                    request.type,
                    request.limit,
                    request.offset
                )
            }
        }.execute()
    }


    fun createTicket(
        request: TicketCreateRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.createTicket(request)
            }
        }.execute()
    }


    fun updateStudent(
        request: StudentUpdateRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.updateStudent(request)
            }
        }.execute()
    }

    fun getMakeupList(
        parentId: String,
        loginResponse: MutableLiveData<ApiResponse<MakeupListResponse>>
    ) {
        object : DataFetchCall<MakeupListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<MakeupListResponse> {
                return apiServices.makeupList(parentId)
            }
        }.execute()
    }

    fun updateNotification(
        notificationID: NotificationUpdateRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.updateNotificationPref(notificationID)
            }
        }.execute()
    }

    fun lessonDetails(
        request: String,
        loginResponse: MutableLiveData<ApiResponse<InvoiceListResponse>>
    ) {
        object : DataFetchCall<InvoiceListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<InvoiceListResponse> {
                return apiServices.lessonDetails(request)
            }
        }.execute()
    }

    fun lessonConvertOnline(
        request: LessonConvertRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.lessonConvertOnline(request.id)
            }
        }.execute()
    }

    fun lessonConvertOffline(
        request: LessonConvertRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.lessonConvertOffline(request.id)
            }
        }.execute()
    }


    fun getBookingListByDate(date: Date): LiveData<List<TeacherBooking>?> {
        val bookingListLiveData: MutableLiveData<List<TeacherBooking>> = MutableLiveData()
        val dateString = SimpleDateFormat("yyyy-MM-dd").format(date)
        val teacherData = AureusApplication.getInstance().getTeacherLoginLiveData()
        val bookingList = teacherData.value?.teacherBookings?.filter {
            it.Booking_Date__c == dateString
        }
        bookingListLiveData.value = bookingList
        return bookingListLiveData

    }

    fun getInsturementList(
        objName: String,
        fieldName: String,
        loginResponse: MutableLiveData<ApiResponse<List<InstrumentListResponse>>>
    ) {
        object : DataFetchCall<List<InstrumentListResponse>>(loginResponse) {
            override suspend fun createCallAsync(): Response<List<InstrumentListResponse>> {
                return apiServices.getList(objName, fieldName)
            }
        }.execute()
    }

    fun getTeacherList(
        objName: String,
        fieldName: String,
        loginResponse: MutableLiveData<ApiResponse<TeacherListResponse>>
    ) {
        object : DataFetchCall<TeacherListResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<TeacherListResponse> {
                return apiServices.getTeachersList(objName, fieldName)
            }
        }.execute()
    }

    fun getTeacherDate(
        request: TeacherDateRequest,
        loginResponse: MutableLiveData<ApiResponse<JsonElement>>
    ) {
        object : DataFetchCall<JsonElement>(loginResponse) {

            override suspend fun createCallAsync(): Response<JsonElement> {
                return apiServices.getTeacherDate(
                    request.centerId,
                    request.teacherId,
                    "60",
                    ""
                )
            }
        }.execute()
    }

    fun getTeacherTime(
        request: TeacherDateRequest,
        loginResponse: MutableLiveData<ApiResponse<JsonElement>>
    ) {
        object : DataFetchCall<JsonElement>(loginResponse) {
            override suspend fun createCallAsync(): Response<JsonElement> {
                return apiServices.getTeachersTime(
                    request.centerId,
                    request.teacherId,
                    request.date,
                    request.duration
                )
            }
        }.execute()
    }

    fun lessonReschedule(
        request: LessonRescheduleRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.lessonReschdule(request)
            }
        }.execute()
    }

    fun referDiscountAmt(
        request: String,
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.referDiscountAmt(request)
            }
        }.execute()
    }

    fun referAmt(
        request: String,
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.referAmt(request)
            }
        }.execute()
    }

    fun referUrl(
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.referUrl()
            }
        }.execute()
    }

    fun referEnrollemnt(
        request: String,
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.referEnrollement(request)
            }
        }.execute()
    }


    fun updateEnrollment(
        request: EnrollmentUpdateRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.updateEnrollment(request)
            }
        }.execute()
    }

    fun meetingFeedbackGuest(
        request: PublicMeetingFeedbackRequest,
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.publicMeetingFeedBack(request.person_name,request.room_name,request.rating)
            }
        }.execute()
    }

    fun meetingFeedback(
        person_name: String,
        booking_id: String,
        rating: String,
        person_id: String,
        loginResponse: MutableLiveData<ApiResponse<String>>
    ) {
        object : DataFetchCall<String>(loginResponse) {
            override suspend fun createCallAsync(): Response<String> {
                return apiServices.meetingFeedback(person_name,booking_id,rating,person_id)
            }
        }.execute()
    }

    fun getMakeupPackage(
        location: String,
        loginResponse: MutableLiveData<ApiResponse<List<PackageResponse>>>
    ) {
        object : DataFetchCall<List<PackageResponse>>(loginResponse) {
            override suspend fun createCallAsync(): Response<List<PackageResponse>> {
                return apiServices.packageMakeUp(location)
            }
        }.execute()
    }

    fun createMakeUpLesson(
        request: List<MakeUpCreateRequest>,
        loginResponse: MutableLiveData<ApiResponse<ResponseBody>>
    ) {
        object : DataFetchCall<ResponseBody>(loginResponse) {
            override suspend fun createCallAsync(): Response<ResponseBody> {
                return apiServices.createMakeUp(request)
            }
        }.execute()
    }

}