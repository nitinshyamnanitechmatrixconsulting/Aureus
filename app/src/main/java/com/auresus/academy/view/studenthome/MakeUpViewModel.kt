package com.auresus.academy.view.studenthome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auresus.academy.model.bean.requests.LessonRescheduleRequest
import com.auresus.academy.model.bean.requests.MakeUpCreateRequest
import com.auresus.academy.model.bean.requests.TeacherDateRequest
import com.auresus.academy.model.bean.responses.*
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.view_model.BaseViewModel
import com.google.gson.JsonElement
import okhttp3.ResponseBody


class MakeUpViewModel constructor(private val authenticationRepository: AppRepository) :
    BaseViewModel() {

    private val _makeupListResponse by lazy {
        MutableLiveData<ApiResponse<MakeupListResponse>>()
    }

    private val _instrumentListResponse by lazy {
        MutableLiveData<ApiResponse<List<InstrumentListResponse>>>()
    }

    private val _teacherListResponse by lazy {
        MutableLiveData<ApiResponse<TeacherListResponse>>()
    }

    private val _teacherDateResponse by lazy {
        MutableLiveData<ApiResponse<JsonElement>>()
    }
    private val _teacherTimeResponse by lazy {
        MutableLiveData<ApiResponse<JsonElement>>()
    }
    private val _lessonReschdeule by lazy {
        MutableLiveData<ApiResponse<NotificationDeleteResponse>>()
    }

    private val _packageResponse by lazy {
        MutableLiveData<ApiResponse<List<PackageResponse>>>()
    }

    private val _createMakeUpResponse by lazy {
        MutableLiveData<ApiResponse<ResponseBody>>()
    }

    /*** LiveData that view observing
     * you can modify this as MediatorLiveData if you want to modify data model coming from api*/
    val makeupRequest: LiveData<ApiResponse<MakeupListResponse>> =
        _makeupListResponse

    val insturmentRequest: LiveData<ApiResponse<List<InstrumentListResponse>>> =
        _instrumentListResponse

    val teacherRequest: LiveData<ApiResponse<TeacherListResponse>> =
        _teacherListResponse

    val teacherDateRequest: LiveData<ApiResponse<JsonElement>> =
        _teacherDateResponse

    val teacherTimeRequest: LiveData<ApiResponse<JsonElement>> =
        _teacherTimeResponse

    val lessonReschdule: LiveData<ApiResponse<NotificationDeleteResponse>> =
        _lessonReschdeule

    val packageRequest: LiveData<ApiResponse<List<PackageResponse>>> =
        _packageResponse

    val createMakeUpRequest:LiveData<ApiResponse<ResponseBody>> =
        _createMakeUpResponse

    fun getMakeupList(request: String) {
        authenticationRepository.getMakeupList(request, _makeupListResponse)
    }

    fun getInstumentList(request: String, fieldName: String) {
        authenticationRepository.getInsturementList(request, fieldName, _instrumentListResponse)
    }

    fun getTeacherList(centerId: String, instrument: String) {
        authenticationRepository.getTeacherList(centerId, instrument, _teacherListResponse)
    }

    fun getTeacherDate(request: TeacherDateRequest) {
        authenticationRepository.getTeacherDate(request, _teacherDateResponse)
    }

    fun getTeacherTime(request: TeacherDateRequest) {
        authenticationRepository.getTeacherTime(request, _teacherTimeResponse)
    }

    fun getPackage(request: String) {
        authenticationRepository.getMakeupPackage(request, _packageResponse)
    }

    fun reschdeuleLesson(request: LessonRescheduleRequest) {
        authenticationRepository.lessonReschedule(request, _lessonReschdeule)
    }

    fun createMakeUpRequest(request: List<MakeUpCreateRequest>){
        authenticationRepository.createMakeUpLesson(request,_createMakeUpResponse)
    }

}