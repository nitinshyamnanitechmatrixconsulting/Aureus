package com.auresus.academy.model.bean.responses

data class ForgotPasswordResponse(
/*{"errorCode":"SUCCESS","message":"OTP sent to emily.chua1990@gmail.com.test"}*/
    var errorCode: String,
    var message: String
)