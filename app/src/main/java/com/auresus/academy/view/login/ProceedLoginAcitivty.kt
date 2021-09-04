package com.auresus.academy.view.login

import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityStartBinding
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.forgotpassword.ForgotAcitivty
import com.auresus.academy.view.joinclass.JoinOnlineClassAcitivty
import com.bumptech.glide.Glide


class ProceedLoginAcitivty : BaseActivity() {

    private lateinit var binding: ActivityStartBinding


    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                val intent = Intent(this, ProceedLoginAcitivty::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_start
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityStartBinding
        Glide.with(this).load(R.raw.login).into(this.binding.loadGif)
        initClickListener()
    }

    private fun initClickListener() {
        binding.loginInButton.setOnClickListener {
            LoginAcitivty.open(this)
            finish()
        }
        binding.forgotPassword.setOnClickListener {
            ForgotAcitivty.open(this)
        }
        binding.joinOnlineSession.setOnClickListener {
            JoinOnlineClassAcitivty.open(this)
        }
    }
}