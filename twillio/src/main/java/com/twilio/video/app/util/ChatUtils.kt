package com.twilio.video.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

object ChatUtils {
    object MessageType{
        const val NEW_JOIN = 1
        const val JOIN_ROOM_ACCEPTED = 2
        const val JOIN_ROOM_DENIED = 3
        const val REMOVE_ALL = 4
        const val MUTE_PARTICIPANT = 5
        const val REMOVE_PARTICIPANT = 6
        const val TYPING = 7
        const val CHAT_MESSAGE = 8
    }

    fun openWebPage(url: String?,context: Context) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent)
        }
    }

    fun getMessageType(message: String): Int {
        var messageType = -1
        val isNewJoin =
        if (message.contains("NewJoin")) {
            messageType = MessageType.NEW_JOIN
        }
        else if (message.contains("yes")){
            messageType = MessageType.JOIN_ROOM_ACCEPTED

        }
        else if (message.contains("No")){
            messageType = MessageType.JOIN_ROOM_DENIED

        }else if (message.contains("removeAll")){
            messageType = MessageType.REMOVE_ALL

        }else if (message.contains("mute_")){
            messageType = MessageType.MUTE_PARTICIPANT

        }else if (message.contains("remove_")){
            messageType = MessageType.REMOVE_PARTICIPANT

        }else if (message.contains("typing")){
            messageType = MessageType.TYPING

        }
        else {
            messageType = MessageType.CHAT_MESSAGE
        }
        return messageType

    }
}