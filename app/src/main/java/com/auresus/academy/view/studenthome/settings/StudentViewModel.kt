package com.auresus.academy.view.studenthome.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.requests.StudentUpdateRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel


class StudentViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _updateStudnetResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    private val _updatePersonalDetailsResponse by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val updateStudentRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _updateStudnetResponse

    val updatePersonalDetailsRequest: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _updatePersonalDetailsResponse

    fun updateStudent(request: StudentUpdateRequest) {
        authenticationRepository.updateStudent(request, _updateStudnetResponse)
    }

    fun updatePersonalDetails(request: StudentUpdateRequest) {
        authenticationRepository.updateStudent(request, _updatePersonalDetailsResponse)
    }

    fun createRequest(
        student: Student,
        updatedDat: Student,
        parentid: String
    ): StudentUpdateRequest {
        var request = StudentUpdateRequest(
            parentId = parentid,
            studentId = student.studentId,
            enrollmentId = "",
            details = createStudentDetails(student, updatedDat),
            invoiceId = "",
            requestType = "Update Student Details",
            subject = "Update Student Details"
        )
        return request
    }

    fun createPersonalDetailsRequest(
        student: StudentLoginResponse,
        updatedDat: StudentLoginResponse,
        parentid: String
    ): StudentUpdateRequest {
        var request = StudentUpdateRequest(
            parentId = parentid,
            studentId = "",
            enrollmentId = "",
            details = createPersonalDetails(student, updatedDat),
            invoiceId = "",
            requestType = "Update Personal Details",
            subject = "Update Personal Details"
        )
        return request
    }

    private fun createStudentDetails(student: Student, updatedData: Student): String {
        var detailsStr = "You have requested to change following info in your account"
        detailsStr = "\n$detailsStr\nNew Details"
        detailsStr += newDetailsString(student, updatedData)
        detailsStr += "\n"

        detailsStr = "$detailsStr\nPrevious Details"
        detailsStr += prevoiusDetails(student, updatedData)
        detailsStr += "\n\n"
        return detailsStr
    }

    private fun prevoiusDetails(student: Student, updatedData: Student): String {
        var str = ""
        if (student.firstName != updatedData.firstName)
            str = str + "\nFirst Name: " + student.firstName
        if (student.lastName != updatedData.lastName)
            str = str + "\nLast Name: " + student.lastName
        if (student.gender != updatedData.gender)
            str = str + "\nGender: " + student.gender
        if (student.schoolName != updatedData.schoolName)
            str = str + "\nSchool: " + student.schoolName
        if (student.Birthdate != updatedData.Birthdate)
            str = str + "\nDate of Birth: " + student.Birthdate
        return str
    }

    private fun newDetailsString(student: Student, updatedData: Student): String {
        var str = ""
        if (student.firstName != updatedData.firstName)
            str = str + "\nFirst Name: " + updatedData.firstName
        if (student.lastName != updatedData.lastName)
            str = str + "\nLast Name: " + updatedData.lastName
        if (student.gender != updatedData.gender)
            str = str + "\nGender: " + updatedData.gender
        if (student.schoolName != updatedData.schoolName)
            str = str + "\nSchool: " + updatedData.schoolName
        if (student.Birthdate != updatedData.Birthdate)
            str = str + "\nDate of Birth: " + updatedData.Birthdate
        return str
    }

    private fun createPersonalDetails(
        student: StudentLoginResponse,
        updatedData: StudentLoginResponse
    ): String {
        var detailsStr = "You have requested to change following info in your account"
        detailsStr = "\n$detailsStr\nNew Details"
        detailsStr += newPersonalDetailsString(student, updatedData)
        detailsStr += "\n"

        detailsStr = "$detailsStr\nPrevious Details"
        detailsStr += prevoiusPersonalDetails(student, updatedData)
        detailsStr += "\n\n"
        return detailsStr
    }

    private fun newPersonalDetailsString(
        student: StudentLoginResponse,
        updatedData: StudentLoginResponse
    ): String {
        var str = ""
        if (student.firstName != updatedData.firstName)
            str = str + "\nFirst Name: " + updatedData.firstName
        if (student.lastName != updatedData.lastName)
            str = str + "\nLast Name: " + updatedData.lastName
        if (student.gender != updatedData.gender)
            str = str + "\nGender: " + updatedData.gender
        if (student.email != updatedData.email)
            str = str + "\nEmail: " + updatedData.email
        return str
    }


    private fun prevoiusPersonalDetails(
        student: StudentLoginResponse,
        updatedData: StudentLoginResponse
    ): String {
        var str = ""
        if (student.firstName != updatedData.firstName)
            str = str + "\nFirst Name: " + student.firstName
        if (student.lastName != updatedData.lastName)
            str = str + "\nLast Name: " + student.lastName
        if (student.gender != updatedData.gender)
            str = str + "\nGender: " + student.gender
        if (student.email != updatedData.email)
            str = str + "\nEmail: " + student.email
        return str
    }

}