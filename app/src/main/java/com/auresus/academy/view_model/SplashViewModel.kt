package com.auresus.academy.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.GetContactListRequest
import com.auresus.academy.model.bean.responses.ContactListResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.ContactRepository


class SplashViewModel constructor(private val contactRepository: ContactRepository) :
    BaseViewModel() {

    private val _commentListResponse by lazy {
        MutableLiveData<ApiResponse<ContactListResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val commentListResponse: LiveData<ApiResponse<ContactListResponse>> = _commentListResponse

    fun getContactList(getContactListRequest: GetContactListRequest) {
        contactRepository.getCommentListFromNetwork(getContactListRequest, _commentListResponse)
    }

    fun createContactRequest(): GetContactListRequest {
        val request = GetContactListRequest()
        request.page = "1"
        return request
    }

}