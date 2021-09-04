package com.auresus.academy

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDex
import com.auresus.academy.di.koin.*
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.bean.responses.TeacherLoginResponse
import com.twilio.video.app.VideoApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import javax.inject.Inject

class AureusApplication : VideoApplication(), Application.ActivityLifecycleCallbacks{
    var mCurrencyActivity: Activity? = null
    var isBackground: Boolean = true
    var mutableLiveDataStudent: MutableLiveData<StudentLoginResponse> = MutableLiveData()
    var mutableLiveDataTeacher: MutableLiveData<TeacherLoginResponse> = MutableLiveData()


    init {
        instance = this
    }

    companion object {
        private var instance: AureusApplication? = null

        fun getInstance(): AureusApplication {
            return instance as AureusApplication
        }
    }


    fun setStudentLoginData(loginResponse: StudentLoginResponse) {
        mutableLiveDataStudent.postValue(loginResponse)
    }

    fun getStudentLoginLiveData(): MutableLiveData<StudentLoginResponse> {
        return mutableLiveDataStudent
    }


    fun setTeacherLoginData(loginResponse: TeacherLoginResponse) {
        mutableLiveDataTeacher.postValue(loginResponse)
    }

    fun getTeacherLoginLiveData(): MutableLiveData<TeacherLoginResponse> {
        return mutableLiveDataTeacher
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    override fun onCreate() {
        super.onCreate()




        // Stetho.initializeWithDefaults(this)
        /*** start Koin DI  */
        startKoin {
            androidLogger()
            androidContext(this@AureusApplication)
            modules(getModule())
        }

        registerActivityLifecycleCallbacks(this)
    }

    /*** function to get all di modules array*/
    private fun getModule(): Iterable<Module> {
        return listOf(appModule, viewModelModule, databaseModule, repoModule, splashActivityModule)
    }

    override fun onActivityPaused(p0: Activity) {
        isBackground = true
        mCurrencyActivity = p0
    }

    override fun onActivityResumed(activity: Activity) {
        isBackground = false
        mCurrencyActivity = activity
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

}