package com.auresus.academy.view.studenthome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class ReferViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _referDiscountAmtResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }
    private val _referAmtResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }
    private val _referUrlResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }
    private val _referEnrollmentResponse by lazy {
        MutableLiveData<ApiResponse<String>>()
    }


    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val referDiscountAmtRequest: LiveData<ApiResponse<String>> =
        _referDiscountAmtResponse

    val referAmtRequest: LiveData<ApiResponse<String>> =
        _referAmtResponse
    val referUrlRequest: LiveData<ApiResponse<String>> =
        _referUrlResponse
    val referEnrollmentRequest: LiveData<ApiResponse<String>> =
        _referEnrollmentResponse


    fun referDiscountAmt(request: String) {
        authenticationRepository.referDiscountAmt(request, _referDiscountAmtResponse)
    }

    fun referAmt(request: String) {
        authenticationRepository.referAmt(request, _referAmtResponse)
    }

    fun referUrl(request: String) {
        authenticationRepository.referUrl(_referUrlResponse)
    }

    fun referEnrollment(request: String) {
        authenticationRepository.referEnrollemnt(request, _referEnrollmentResponse)
    }


}