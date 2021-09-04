package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginRequest : Serializable {

    @SerializedName("emailId")
    var emailId: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("appversion")
    var appversion: String = ""

    @SerializedName("token")
    var token: String = ""

    @SerializedName("DeviceType")
    var DeviceType: String = "android"


}