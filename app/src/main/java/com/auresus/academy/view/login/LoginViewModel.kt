package com.auresus.academy.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.BuildConfig
import com.auresus.academy.config.ConfigConstant
import com.auresus.academy.model.bean.requests.LoginRequest
import com.auresus.academy.model.bean.responses.CommonLoginResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AuthenticationRepository
import com.auresus.academy.view_model.BaseViewModel


class LoginViewModel constructor(private val authenticationRepository: AuthenticationRepository) :
    BaseViewModel() {

    private val _loginResponse by lazy {
        MutableLiveData<ApiResponse<CommonLoginResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val loginResponse: LiveData<ApiResponse<CommonLoginResponse>> = _loginResponse

    fun signIn(loginRequest: LoginRequest) {
        authenticationRepository.login(loginRequest, _loginResponse)
    }

    fun createLoginRequest(email: String, password: String): LoginRequest {
        val request = LoginRequest()
        request.emailId = email
        request.password = password
        request.DeviceType = ConfigConstant.DEVICE_TYPE
        request.appversion = BuildConfig.VERSION_NAME
        return request
    }

}