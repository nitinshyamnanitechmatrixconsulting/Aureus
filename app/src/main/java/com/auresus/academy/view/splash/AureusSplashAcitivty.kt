package com.auresus.academy.view.splash

import android.os.Handler
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivitySplashBinding
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.login.LoginAcitivty
import com.bumptech.glide.Glide


class AureusSplashAcitivty : BaseActivity() {


    private lateinit var binding: ActivitySplashBinding

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivitySplashBinding
        Glide.with(this).load(R.raw.login).into(this.binding.loadGif)
        Handler().postDelayed({
            moveToNextScreen()
        }, 3000)
    }

    private fun moveToNextScreen() {
        LoginAcitivty.open(this)
    }
}