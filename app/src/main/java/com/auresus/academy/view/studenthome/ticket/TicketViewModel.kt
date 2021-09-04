package com.auresus.academy.view.studenthome.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.TicketCreateRequest
import com.auresus.academy.model.bean.requests.TicketRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.TicketListResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class TicketViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _ticketListResponse by lazy {
        MutableLiveData<ApiResponse<TicketListResponse>>()
    }

    private val createTicketResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val ticketRequest: LiveData<ApiResponse<TicketListResponse>> =
        _ticketListResponse

    val createTicketRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        createTicketResponse


    fun getTicketList(request: TicketRequest) {
        authenticationRepository.getTicketList(request, _ticketListResponse)
    }

    fun createTicket(request: TicketCreateRequest) {
        authenticationRepository.createTicket(request, createTicketResponse)
    }

    fun ticketCreateRequest(
        parentId: String,
        requestType: String,
        subject: String,
        details: String
    ): TicketCreateRequest {
        return TicketCreateRequest(
            parentId = parentId,
            studentId = "",
            requestType = requestType,
            invoiceId = "",
            details = details,
            enrollmentId = "",
            bookingId = "",
            subject = subject
        )
    }


}