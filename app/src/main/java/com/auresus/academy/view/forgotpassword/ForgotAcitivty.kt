package com.auresus.academy.view.forgotpassword

import android.app.Dialog
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityForgotPasswordBinding
import com.auresus.academy.model.bean.responses.ForgotPasswordResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogClickListener
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.login.LoginAcitivty
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgotAcitivty : BaseActivity(), DialogClickListener {


    private lateinit var binding: ActivityForgotPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModel()
    private val validate: com.auresus.academy.utils.validations.Validator by inject()

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                startActivity(Intent(this, ForgotAcitivty::class.java).apply {
                })
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_forgot_password
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityForgotPasswordBinding
        initClickListener()
    }

    private fun initClickListener() {
        binding.singinBtn.setOnClickListener {
            LoginAcitivty.open(this)
            finish()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.continueBtn.setOnClickListener {
            if (validate.validateEmail(binding.edittextEmail.text.toString())) {
                hitForgotPasswordApi();
            } else
                DialogUtils.showAlertDialog(this, "Please enter a valid email")
        }
    }

    var email: String = ""
    private fun hitForgotPasswordApi() {
        email = binding.edittextEmail.text.toString()
        if (Connectivity.isConnected(this)) {
            forgotPasswordViewModel.forgotPassword(
                forgotPasswordViewModel.createForgotPasswordRequest(
                    email
                )
            )
            forgotPasswordViewModel.forgotPasswordRequest.observe(this, forgotPasswordObserver)
        } else {
            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    private val forgotPasswordObserver: Observer<ApiResponse<ForgotPasswordResponse>> by lazy {
        Observer<ApiResponse<ForgotPasswordResponse>> {
            it?.let {
                handleLoginResponse(it)
            }
        }
    }
    private val resetPasswordObserver: Observer<ApiResponse<ForgotPasswordResponse>> by lazy {
        Observer<ApiResponse<ForgotPasswordResponse>> {
            it?.let {
                handleResetPasswordResponse(it)
            }
        }
    }

    private fun handleResetPasswordResponse(response: ApiResponse<ForgotPasswordResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                Toast.makeText(this, "Password Send Successfully to your email", Toast.LENGTH_LONG)
                    .show()
                LoginAcitivty.open(this)
                finish()
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleLoginResponse(response: ApiResponse<ForgotPasswordResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                DialogUtils.showEnterOtp(this, "Otp send to  \n$email", this)
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun resendOtp(dialog: Dialog) {
        hitForgotPasswordApi()
        Toast.makeText(this, getString(R.string.textOtpSend), Toast.LENGTH_LONG).show()
    }

    override fun onOkClick(dialog: Dialog, otp: String) {
        dialog.dismiss()
        hitResetPasswordApi(otp)
    }

    private fun hitResetPasswordApi(otp: String) {
        if (Connectivity.isConnected(this)) {
            forgotPasswordViewModel.resetPassword(
                forgotPasswordViewModel.createResetPasswordRequest(
                    email, otp
                )
            )
            forgotPasswordViewModel.resetPasswordRequest.observe(this, resetPasswordObserver)
        } else {
            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

}