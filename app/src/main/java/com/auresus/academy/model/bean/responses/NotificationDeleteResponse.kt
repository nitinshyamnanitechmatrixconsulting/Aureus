package com.auresus.academy.model.bean.responses

data class NotificationDeleteResponse(
/*    {"errorCode":"SUCCESS","message":"Mobile Notifications is Deleted Successfully"}*/

    var errorCode: String,
    var message: String,
    var status: String,
    var successMessage: String
)