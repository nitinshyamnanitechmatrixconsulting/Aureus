package com.auresus.academy.view_model

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.bean.responses.TeacherLoginResponse
import com.auresus.academy.model.repo.AppRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.InputStream
import java.util.*


/* Created by Sahil Bharti on 5/4/19.
 *
*/
open class BaseViewModel : ViewModel(), KoinComponent {
    /**
     *  ApplicationRepository is injected here to access application level
     *  functions & preference
     *
     */
    val date: MutableLiveData<Date> = MutableLiveData()
    val appRepo: AppRepository by inject()

    fun getHomeLiveData(): MutableLiveData<StudentLoginResponse> {
        return appRepo.getHomeData()
    }

    fun getTeacherHomeLiveData(): MutableLiveData<TeacherLoginResponse> {
        return appRepo.getTeacherHomeData()
    }

    fun getDateWiseBookingListLiveData(): LiveData<List<TeacherBooking>?> {
        return Transformations.switchMap(date) { date ->
            appRepo.getBookingListByDate(date)
    }
}

    fun uploadDocument(fileName: String, content: InputStream) {

    }

    fun errorOpeningDocument() {
    }

    fun userCancelledOpenOfDocument() {

    }
}