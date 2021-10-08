package com.auresus.academy.view.login

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.AureusApplication
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityLoginBinding
import com.auresus.academy.model.bean.responses.CommonLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.UserType
import com.auresus.academy.utils.isVisible
import com.auresus.academy.utils.validations.ValidationResult
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.forgotpassword.ForgotAcitivty
import com.auresus.academy.view.joinclass.JoinOnlineClassAcitivty
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.teacherhome.TeacherHomeActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginAcitivty : BaseActivity() {

    private var emailEntered: Boolean = false;
    private val validate: com.auresus.academy.utils.validations.Validator by inject()
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()


    private val loginResponseObserver: Observer<ApiResponse<CommonLoginResponse>> by lazy {
        Observer<ApiResponse<CommonLoginResponse>> {
            it?.let {
                handleLoginResponse(it)
            }
        }
    }


    private fun handleLoginResponse(response: ApiResponse<CommonLoginResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
               progressLoader.isVisible(false)
                preferenceHelper.setUserLoggedIn(true)
                val password = binding.passwordEditText.text.toString()
                val email = binding.edittextEmail.text.toString()
                preferenceHelper.setUserEmail(email)
                preferenceHelper.setUserPassword(password)
                setLoginData(response.data)
            }
            ApiResponse.Status.ERROR -> {
                progressLoader.isVisible(false)
               // if (response.error?.code == 500)
                    Toast.makeText(this, response.error?.message, Toast.LENGTH_LONG).show()
              /*  else
                    Toast.makeText(
                        this,
                        getString(R.string.internal_server_error),
                        Toast.LENGTH_LONG
                    ).show()*/

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
                preferenceHelper.setUserName(teacherData.teacherName)
                preferenceHelper.put(
                    PreferenceHelper.PARENR_ID,
                    teacherData.teacherBookings.get(0).Teacher_Account__c.toString()
                )
                TeacherHomeActivity.open(this)
            }
            if (studentData != null) {
                AureusApplication.getInstance().setStudentLoginData(studentData)
                preferenceHelper.setUserName(studentData.name)
                preferenceHelper.put(PreferenceHelper.PARENR_ID, studentData.parentId)
                preferenceHelper.setUserType(UserType.USER_TYPE_STUDENT)
                moveToNextScreen()
            }
        }
    }

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                val intent = Intent(this, LoginAcitivty::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityLoginBinding
//        binding.edittextEmail.setText("emily.chua1990@gmail.com.test")
        binding.edittextEmail.setText("adrienne.ti@aureusacademy.com.test")
        binding.passwordEditText.setText("Singapore1!")
        Glide.with(this).load(R.raw.login).into(this.binding.loadGif)
        initClickListener()
        val email = preferenceHelper.getEmail()
        if (TextUtils.isEmpty(email))
            this.binding.signinWithBiometric.visibility = View.GONE
        else
            this.binding.signinWithBiometric.visibility = View.VISIBLE
        binding.edittextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s)) {

                } else {
                    if (!validate.validateEmail(s.toString())) {
                        binding.continueBtn.setBackgroundResource(R.drawable.round_button_grey)
                        binding.continueBtn.isClickable = false
                    } else {
                        binding.continueBtn.setBackgroundResource(R.drawable.round_button)
                        binding.continueBtn.isClickable = true
                    }
                }
            }

        })
    }

    private fun initClickListener() {
        binding.joinOnlineSession.setOnClickListener {
            JoinOnlineClassAcitivty.open(this)
        }
        binding.forgotPassword.setOnClickListener {
            ForgotAcitivty.open(this)
        }
        binding.signinWithBiometric.setOnClickListener { hitFingerPrintApi() }
        binding.continueBtn.setOnClickListener {
            if (emailEntered) {
                when (validate.validatePassword(binding.passwordEditText.text.toString())) {
                    ValidationResult.SUCCESS -> hitLoginApi()
                    ValidationResult.EMPTY_PASSWORD -> DialogUtils.showAlertDialog(
                        this,
                        "Please enter a password"
                    )
                    ValidationResult.ERROR_PASSWORD -> DialogUtils.showAlertDialog(
                        this,
                        "Please enter a password greater then 6 digit"
                    )
                }
            }
            binding.passwordEditTextLayout.visibility = View.VISIBLE
            binding.passwordEditText.requestFocus()
            emailEntered = true
        }
    }

    private fun hitLoginApi() {
        val password = binding.passwordEditText.text.toString()
        val email = binding.edittextEmail.text.toString()
        if (Connectivity.isConnected(this)) {
            loginViewModel.signIn(loginViewModel.createLoginRequest(email, password))
            loginViewModel.loginResponse.observe(this, loginResponseObserver)
        } else {
            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
                .show()
            finish()
        }

    }

    private fun hitFingerPrintApi() {
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
        }


    }

    private fun moveToNextScreen() {
        HomeAcitivty.open(this)
    }
}