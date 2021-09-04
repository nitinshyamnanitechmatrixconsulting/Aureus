package com.auresus.academy.view.splash


import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.AureusApplication
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivitySplashBinding
import com.auresus.academy.model.bean.responses.CommonLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.UserType
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.login.LoginAcitivty
import com.auresus.academy.view.login.LoginViewModel
import com.auresus.academy.view.login.ProceedLoginAcitivty
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.teacherhome.TeacherHomeActivity
import com.auresus.academy.view_model.SplashViewModel
import com.bumptech.glide.Glide
import com.twilio.video.app.ui.room.RoomActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : BaseActivity() {

    private val splashViewMode: SplashViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var binding: ActivitySplashBinding


    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivitySplashBinding
        splashView.isVisible = false
        Glide.with(this).load(R.raw.login).into(this.binding.loadGif)
        moveToNextScreen()
    }

    private val loginResponseObserver: Observer<ApiResponse<CommonLoginResponse>> by lazy {
        Observer<ApiResponse<CommonLoginResponse>> {
            handleLoginResponse(it)
        }
    }

    private fun showLoader(show: Boolean) {
        loader.isVisible = show
        splashView.isVisible = !show
    }

    private fun handleLoginResponse(response: ApiResponse<CommonLoginResponse>) {
        showLoader(response.status == ApiResponse.Status.LOADING)
        when (response.status) {
            ApiResponse.Status.LOADING -> {

            }
            ApiResponse.Status.SUCCESS -> {
                setLoginData(response.data)
            }
            ApiResponse.Status.ERROR -> {
                if (response.error?.code == 500)
                    Toast.makeText(this, response.error?.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(
                        this,
                        getString(R.string.internal_server_error),
                        Toast.LENGTH_LONG
                    ).show()

            }
        }
    }

    private fun setLoginData(data: CommonLoginResponse?) {

        data?.let {
            val studentData = data.studentLoginResponse
            val teacherData = data.teacherLoginResponse
            if (teacherData != null) {
                AureusApplication.getInstance().setTeacherLoginData(teacherData)
                preferenceHelper.setUserType(UserType.USER_TYPE_TEACHER)
                navigateToTeacherHome()
            }
            if (studentData != null) {
                AureusApplication.getInstance().setStudentLoginData(studentData)
                preferenceHelper.setUserType(UserType.USER_TYPE_STUDENT)
                preferenceHelper.put(PreferenceHelper.PARENR_ID, studentData.parentId)
                preferenceHelper.setUserType(UserType.USER_TYPE_STUDENT)
                navigateToStudentHome()
            }
        }
    }

    private fun moveToNextScreen() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (!isFinishing) {
                    val isLoggedIn = preferenceHelper.isUserLoggedIn()
                    if (isLoggedIn) {
                        hitLoginApi()
                    } else {
                        ProceedLoginAcitivty.open(this@SplashActivity)
                        finish()
                    }
                }


            }

        }, 100)
    }

    private fun navigateToTeacherHome() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (!isFinishing)
                    TeacherHomeActivity.open(this@SplashActivity)

            }

        }, 500)
    }

    private fun navigateToStudentHome() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (!isFinishing)
                    HomeAcitivty.open(this@SplashActivity)

            }

        }, 500)
    }

    private fun hitLoginApi() {
        val email = preferenceHelper.getEmail()
        val password = preferenceHelper.getPassword()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (Connectivity.isConnected(this)) {
                loginViewModel.signIn(loginViewModel.createLoginRequest(email, password))
                loginViewModel.loginResponse.observe(this, loginResponseObserver)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_network_error),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        } else {
            LoginAcitivty.open(this@SplashActivity)
        }


    }


}