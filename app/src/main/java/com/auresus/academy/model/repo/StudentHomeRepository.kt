package com.auresus.academy.model.repo

import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.GetBookingListRequest
import com.auresus.academy.model.bean.requests.MeetingServiceRequest
import com.auresus.academy.model.bean.requests.NotificationDetailsAllRequest
import com.auresus.academy.model.bean.requests.NotificationUpdateRequest
import com.auresus.academy.model.bean.responses.*
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.local.room_db.dao.ContactDao
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.remote.ApiServices
import com.auresus.academy.model.remote.DataFetchCall
import com.auresus.academy.model.remote.ReflectionUtil
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import org.json.JSONArray
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class StudentHomeRepository(
    private val apiServices: ApiServices,
    private val preferences: PreferenceHelper,
    private val contactDao: ContactDao

) : KoinComponent {

    /** ReflectionUtil get Object using koin DI
     * used to convert  request Model class to HashMap */
    private val reflectionUtil: ReflectionUtil by inject()
    val gson = GsonBuilder().serializeNulls().create();

    fun getBookingList(
        getBookingListRequest: GetBookingListRequest,
        bookingListResponse: MutableLiveData<ApiResponse<BookingListResponse>>
    ) {
        object : DataFetchCall<BookingListResponse>(bookingListResponse) {
            override suspend fun createCallAsync(): Response<BookingListResponse> {
                return apiServices.getBookingAsync(
                    reflectionUtil.convertPojoToMap(
                        getBookingListRequest
                    )
                )
            }
            /*** override  saveResult() ro Save Api Response */
            /*** override  loadFromDb() & shouldLoadFromDB() to Load Data from DB not from Network */
        }.execute()
    }

    fun getAttachmentList(
        roomName: String,
        bookingListResponse: MutableLiveData<ApiResponse<List<Attachment>>>
    ) {
        val apiResponse = ApiResponse<List<Attachment>>(ApiResponse.Status.LOADING, null, null)
        bookingListResponse.value = apiResponse
        val call = apiServices.getAttachmentList(roomName)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.let {
                    if (response.isSuccessful) {
                        val apiResult = response.body()
                        val jsonString = apiResult?.string()
                        if (!jsonString.isNullOrEmpty()) {
                            val clean = jsonString.removeSurrounding("\"").replace("\\", "")
                            val jsonArray: JSONArray = JSONArray(clean)
                            val attachList = mutableListOf<Attachment>()
                            jsonArray.let {
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val attachment = gson.fromJson(
                                        jsonObject.toString(),
                                        Attachment::class.java
                                    )
                                    attachList.add(attachment)
                                }
                            }
                            val apiResponse = ApiResponse<List<Attachment>>(
                                ApiResponse.Status.SUCCESS,
                                attachList,
                                null
                            )
                            bookingListResponse.value = apiResponse
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val apiResponse = ApiResponse<List<Attachment>>(
                    ApiResponse.Status.SUCCESS,
                    null,
                    null
                )
                bookingListResponse.value = apiResponse
            }
        })
    }


    fun getAccessTokenTwillio(
        roomName: String,
        name: String,
        bookingListResponse: MutableLiveData<ApiResponse<GetAccessTokenResponse>>
    ) {
        val apiResponse =
            ApiResponse<GetAccessTokenResponse>(ApiResponse.Status.LOADING, null, null)
        bookingListResponse.value = apiResponse
        val ACCESS_TOKEN_SERVER = "https://aureusacademy-meeting.herokuapp.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(ACCESS_TOKEN_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiServices = retrofit.create(ApiServices::class.java)
        val call = apiServices.getTwilioAccessTooken(name ,roomName)
        call.enqueue(object : Callback<GetAccessTokenResponse> {
            override fun onResponse(
                call: Call<GetAccessTokenResponse>,
                response: Response<GetAccessTokenResponse>
            ) {
                response?.let {
                    if (it.isSuccessful) {
                        val apiResponse = ApiResponse<GetAccessTokenResponse>(
                            ApiResponse.Status.SUCCESS,
                            it.body(),
                            null
                        )
                        bookingListResponse.value = apiResponse
                    }
                }

            }

            override fun onFailure(call: Call<GetAccessTokenResponse>, t: Throwable) {
                     t.printStackTrace()
            }
        })
    }


    fun meetingService(meetingServiceRequest: MeetingServiceRequest, bookingListResponse: MutableLiveData<ApiResponse<MeetingServiceResponse>>) {
        object : DataFetchCall<MeetingServiceResponse>(bookingListResponse) {
            override suspend fun createCallAsync(): Response<MeetingServiceResponse> {
                return apiServices.meetingService(reflectionUtil.convertPojoToMap(
                    meetingServiceRequest
                ))
            }
        }.execute()
    }


    fun logout(
        notificationID: NotificationUpdateRequest,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                return apiServices.updateNotificationPref(notificationID)
            }
        }.execute()
    }

    fun getNotificationDeleteAll(
        notificationID: String,
        loginResponse: MutableLiveData<ApiResponse<NotificationDeleteResponse>>
    ) {
        object : DataFetchCall<NotificationDeleteResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<NotificationDeleteResponse> {
                var request = NotificationDetailsAllRequest()
                request.notificationId = notificationID
                return apiServices.notificationDeleteAll(request)
            }
        }.execute()
    }

}