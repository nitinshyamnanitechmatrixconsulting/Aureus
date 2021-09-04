package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResetRequest : Serializable {

    @SerializedName("emailId")
    var emailId: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("appversion")
    var appversion: String = ""

    @SerializedName("reset")
    var reset: Boolean = false


}