package com.twilio.video.app.ui.room

import android.os.Bundle
import com.twilio.video.app.base.BaseActivity
import com.twilio.video.app.databinding.ActivityRoomCheckBinding

class RoomCheckActivity : BaseActivity() {
    private lateinit var binding: ActivityRoomCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}