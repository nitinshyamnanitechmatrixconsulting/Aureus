package com.auresus.academy.view.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.BuildConfig
import com.auresus.academy.model.bean.requests.ForgotPasswordRequest
import com.auresus.academy.model.bean.requests.ResetRequest
import com.auresus.academy.model.bean.responses.ForgotPasswordResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AuthenticationRepository
import com.auresus.academy.view_model.BaseViewModel


class ForgotPasswordViewModel constructor(private val authenticationRepository: AuthenticationRepository) :
    BaseViewModel() {

    private val _forgotPasswordResponse by lazy {
        MutableLiveData<ApiResponse<ForgotPasswordResponse>>()
    }
    private val _resetPasswordResponse by lazy {
        MutableLiveData<ApiResponse<ForgotPasswordResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val forgotPasswordRequest: LiveData<ApiResponse<ForgotPasswordResponse>> =
        _forgotPasswordResponse

    val resetPasswordRequest: LiveData<ApiResponse<ForgotPasswordResponse>> =
        _resetPasswordResponse

    fun forgotPassword(loginRequest: ForgotPasswordRequest) {
        authenticationRepository.forgotPassword(loginRequest, _forgotPasswordResponse)
    }

    fun resetPassword(loginRequest: ResetRequest) {
        authenticationRepository.resetPassword(loginRequest, _resetPasswordResponse)
    }

    fun createForgotPasswordRequest(email: String): ForgotPasswordRequest {
        val request = ForgotPasswordRequest()
        request.emailId = email
        request.reset = true
        request.appversion = BuildConfig.VERSION_NAME
        return request
    }

    fun createResetPasswordRequest(email: String, otp: String): ResetRequest {
        val request = ResetRequest()
        request.emailId = email
        request.password = otp
        request.reset = true
        request.appversion = BuildConfig.VERSION_NAME
        return request
    }

}