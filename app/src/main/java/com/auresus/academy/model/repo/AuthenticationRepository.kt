package com.auresus.academy.model.repo

import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.ForgotPasswordRequest
import com.auresus.academy.model.bean.requests.LoginRequest
import com.auresus.academy.model.bean.requests.ResetRequest
import com.auresus.academy.model.bean.responses.CommonLoginResponse
import com.auresus.academy.model.bean.responses.ForgotPasswordResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.bean.responses.TeacherLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.local.room_db.dao.ContactDao
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.remote.ApiServices
import com.auresus.academy.model.remote.DataFetchCall
import com.auresus.academy.model.remote.ReflectionUtil
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthenticationRepository(
    private val apiServices: ApiServices,
    private val preferences: PreferenceHelper,
    private val contactDao: ContactDao
) : KoinComponent {

    /** ReflectionUtil get Object using koin DI
     * used to convert  request Model class to HashMap */
    private val reflectionUtil: ReflectionUtil by inject()
    private val STUDENT_ENROLLMENT = "enrolments"
    val gson = GsonBuilder().serializeNulls().create();

    fun login(
        loginRequest: LoginRequest,
        loginResponse: MutableLiveData<ApiResponse<CommonLoginResponse>>
    ) {
       val apiResponse = ApiResponse<CommonLoginResponse>(ApiResponse.Status.LOADING,null,null)
       loginResponse.value = apiResponse
        var commonLoginResponse = CommonLoginResponse(null,null)
        val call = apiServices.signIn(loginRequest)
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
               response?.let {
                   if(response.isSuccessful){
                       val apiResult = response.body()
                       val jsonString = apiResult?.string()
                       val jsonObject = JSONObject(jsonString)
                       val studentEnrollments = jsonObject.opt(STUDENT_ENROLLMENT)
                       studentEnrollments?.let {
                           commonLoginResponse.studentLoginResponse =gson.fromJson(jsonString,StudentLoginResponse::class.java)
                       }?: kotlin.run {
                           commonLoginResponse.teacherLoginResponse =gson.fromJson(jsonString,TeacherLoginResponse::class.java)
                       }
                       val apiResponse = ApiResponse<CommonLoginResponse>(ApiResponse.Status.SUCCESS,commonLoginResponse,null)
                       loginResponse.value = apiResponse
                   }
               }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val apiResponse = ApiResponse<CommonLoginResponse>(ApiResponse.Status.ERROR,null,null)
                loginResponse.value = apiResponse

            }
        })

       /* object : DataFetchCall<CommonLoginResponse>(loginResponse) {
            override suspend fun createCallAsync(): Response<CommonLoginResponse> {
                return apiServices.signIn(loginRequest)
            }
            *//*** override  saveResult() ro Save Api Response *//*
            *//*** override  loadFromDb() & shouldLoadFromDB() to Load Data from DB not from Network *//*
        }.execute()*/
    }

    fun forgotPassword(
        loginRequest: ForgotPasswordRequest,
        forgotPasswordResponse: MutableLiveData<ApiResponse<ForgotPasswordResponse>>
    ) {
        object : DataFetchCall<ForgotPasswordResponse>(forgotPasswordResponse) {
            override suspend fun createCallAsync(): Response<ForgotPasswordResponse> {
                return apiServices.forgotPassword(loginRequest)
            }
        }.execute()
    }

    fun resetPassword(
        loginRequest: ResetRequest,
        forgotPasswordResponse: MutableLiveData<ApiResponse<ForgotPasswordResponse>>
    ) {
        object : DataFetchCall<ForgotPasswordResponse>(forgotPasswordResponse) {
            override suspend fun createCallAsync(): Response<ForgotPasswordResponse> {
                return apiServices.resetPassword(loginRequest)
            }
        }.execute()

    }


}