package com.auresus.academy.model.remote


import com.auresus.academy.model.bean.requests.*
import com.auresus.academy.model.bean.responses.*
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/* Created by Sahil Bharti on 21/1/19.
 *
*/
interface ApiServices {


    @GET(ApiConstant.GET_CONTACTS)
    suspend fun getContactsAsync(@QueryMap params: HashMap<String, String>): Response<ContactListResponse>

    @GET(ApiConstant.GET_BOOKINGS)
    suspend fun getBookingAsync(@QueryMap params: HashMap<String, String>): Response<BookingListResponse>

    @POST(ApiConstant.SIGN_IN)
    fun signIn(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST(ApiConstant.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body loginRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST(ApiConstant.FORGOT_PASSWORD)
    suspend fun resetPassword(@Body loginRequest: ResetRequest): Response<ForgotPasswordResponse>

    @GET(ApiConstant.GET_ATTACHMENTS)
    fun getAttachmentList(@Query("id") id: String): Call<ResponseBody>

    @GET("token/{name}/{roomName}")
    fun getTwilioAccessTooken(
        @Path(value = "name", encoded = true) name: String,
        @Path(value = "roomName", encoded = true) roomName: String
    ): Call<GetAccessTokenResponse>

    @GET(ApiConstant.MEETING_SERVICE)
    suspend fun meetingService(@QueryMap params: HashMap<String, String>): Response<MeetingServiceResponse>

    @GET(ApiConstant.Notification_list)
    suspend fun notificationList(@Query("parentId") parentId: String): Response<NotificationListResponse>

    @POST(ApiConstant.Notification_list)
    suspend fun notificationDelete(@Body parentId: NotificationDetailsRequest): Response<NotificationDeleteResponse>

    @POST(ApiConstant.Notification_list)
    suspend fun notificationDeleteAll(@Body parentId: NotificationDetailsAllRequest): Response<NotificationDeleteResponse>

    @POST(ApiConstant.Notification_read)
    suspend fun notificationRead(@Body parentId: NotificationDetailsRequest): Response<NotificationDeleteResponse>

    @POST(ApiConstant.Notification_read)
    suspend fun notificationReadArray(@Body parentId: NotificationDetailsRequestRead): Response<NotificationDeleteResponse>


    @GET(ApiConstant.INVOICE_LIST)
    suspend fun invoiceList(@Path("parentId") parentId: String): Response<InvoiceListResponse>

    @GET(ApiConstant.TICKET_LIST)
    suspend fun ticketList(
        @Query("parent_sf_Id") parentId: String,
        @Query("type") type: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<TicketListResponse>

    @POST(ApiConstant.STUDENT_UPDATE)
    suspend fun updateStudent(@Body parentId: StudentUpdateRequest): Response<NotificationDeleteResponse>

    @GET(ApiConstant.MAKEUP_LIST)
    suspend fun makeupList(@Query("parentId") parentId: String): Response<MakeupListResponse>

    @POST(ApiConstant.TICKET_LIST)
    suspend fun createTicket(@Body parentId: TicketCreateRequest): Response<NotificationDeleteResponse>

    @POST(ApiConstant.NOTIFICATION_UPDATE)
    suspend fun updateNotificationPref(@Body parentId: NotificationUpdateRequest): Response<NotificationDeleteResponse>

    @GET(ApiConstant.LESSON_DETAILS)
    suspend fun lessonDetails(@Query("bookingId") parentId: String): Response<InvoiceListResponse>

    @GET(ApiConstant.LESSON_CONVERT_ONLINE)
    suspend fun lessonConvertOnline(@Query("id") parentId: String): Response<NotificationDeleteResponse>

    @GET(ApiConstant.LESSON_CONVERT_INCENTRE)
    suspend fun lessonConvertOffline(@Query("id") parentId: String): Response<NotificationDeleteResponse>

    @GET(ApiConstant.GET_LIST)
    suspend fun getList(
        @Query("objName") objName: String,
        @Query("fieldApiName") fieldApiName: String
    ): Response<List<InstrumentListResponse>>

    @GET(ApiConstant.TEACHER_LIST)
    suspend fun getTeachersList(
        @Query("centerId") centerId: String,
        @Query("instrument") instrument: String
    ): Response<TeacherListResponse>

    @GET(ApiConstant.TEACHER_DATE)
    suspend fun getTeacherDate(
        @Query("centre") centerId: String,
        @Query("teacher") teacher: String,
        @Query("duration") duration: String,
        @Query("date") date: String
    ): Response<JsonElement>

    @GET(ApiConstant.TEACHER_TIME)
    suspend fun getTeachersTime(
        @Query("centre") centerId: String,
        @Query("teacher") teacher: String,
        @Query("date") date: String,
        @Query("duration") duration: String
    ): Response<JsonElement>

    @POST(ApiConstant.LESSON_Rescheduke)
    suspend fun lessonReschdule(@Body parentId: LessonRescheduleRequest): Response<NotificationDeleteResponse>

    @GET(ApiConstant.REFER_DISCOUNT_AMT)
    suspend fun referDiscountAmt(@Query("country") parentId: String): Response<String>

    @GET(ApiConstant.REFER_AMT)
    suspend fun referAmt(@Query("accId") parentId: String): Response<String>

    @GET(ApiConstant.REFER_URL)
    suspend fun referUrl(): Response<String>

    @GET(ApiConstant.REFER_ENROLLMENT)
    suspend fun referEnrollement(@Query("accId") parentId: String): Response<String>

    @POST(ApiConstant.STUDENT_UPDATE)
    suspend fun updateEnrollment(@Body parentId: EnrollmentUpdateRequest): Response<NotificationDeleteResponse>

    @GET(ApiConstant.publicmeetingfeedback)
    suspend fun publicMeetingFeedBack(
        @Query("person_name") person_name: String,
        @Query("room_name") room_name: String,
        @Query("rating") rating: String
    ): Response<String>


    @GET(ApiConstant.meetingfeedback)
     fun meetingFeedback(
        @Query("person_name") person_name: String,
        @Query("booking_id") booking_id: String,
        @Query("rating") rating: String,
        @Query("person_id") person_id: String
    ): Response<String>

    @GET(ApiConstant.publicmeetingfeedback)
    fun guestFeedBack(
        @Query("person_name") person_name: String,
        @Query("room_name") room_name: String,
        @Query("rating") rating: String
    ): Call<Any?>

    @GET(ApiConstant.meetingfeedback)
    fun feedBack(
        @Query("person_name") person_name: String,
        @Query("booking_id") booking_id: String,
        @Query("rating") rating: String,
        @Query("person_id") person_id: String
    ): Call<Any?>
    @GET(ApiConstant.makeUpPackage)
    suspend fun packageMakeUp(
        @Query("location") location: String
    ): Response<List<PackageResponse>>

    @POST(ApiConstant.makeUpPackage)
    suspend fun createMakeUp(
        @Body createRequest: List<MakeUpCreateRequest>
    ): Response<ResponseBody>

}