package com.twilio.video.app.ui.room.modal

import android.view.View
import android.widget.ImageView

interface MeettingOptionHandler {
     fun handleShowMessages()
     fun handleShowLayoutOption()
     fun switchLayout(option:Int)
     fun handleShowInfo()
     fun handleOpenFiles()
     fun handleOpenShowParticipants()
     fun handleScreenShare(view:ImageView)
}