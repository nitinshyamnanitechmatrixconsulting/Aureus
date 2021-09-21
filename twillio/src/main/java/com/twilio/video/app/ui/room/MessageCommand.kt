package com.twilio.video.app.ui.room

object  MessageCommand {
    fun sendJoinRoomRequest(participantName: String) = "NewJoin_\$\$${participantName}"

    fun sendDenyJoinRoom(): String = "No_\$\$"

    fun sendAdmitJoinRoom(): String = "yes_\$\$"

    fun removeAll(): String = "removeAll"

    fun muteParticipantRequest(participantName: String) = "mute_\$\$${participantName}"


    fun removeParticipant(participantName: String) = "remove_\$\$${participantName}"

    fun chatMessage(message: String,participantName: String): String = "${participantName}\$\$\$${message}"

    fun typingMessage(participantName: String): String = "${participantName}\$\$\$typing...."

}