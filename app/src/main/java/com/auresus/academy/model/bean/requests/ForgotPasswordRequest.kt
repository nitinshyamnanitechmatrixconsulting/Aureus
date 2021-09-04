package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ForgotPasswordRequest : Serializable {

    @SerializedName("emailId")
    var emailId: String = ""

    @SerializedName("appversion")
    var appversion: String = ""

    @SerializedName("reset")
    var reset: Boolean = false


}