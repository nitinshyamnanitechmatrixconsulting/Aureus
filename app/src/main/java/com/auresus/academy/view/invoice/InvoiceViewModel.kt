package com.auresus.academy.view.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.responses.InvoiceListResponse
import com.auresus.academy.model.bean.responses.NotificationListResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class InvoiceViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _invoiceListResponse by lazy {
        MutableLiveData<ApiResponse<InvoiceListResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val notificationRequest: LiveData<ApiResponse<InvoiceListResponse>> =
        _invoiceListResponse

    fun getInvoiceList(request: String) {
        authenticationRepository.getInvoiceList(request, _invoiceListResponse)
    }


}