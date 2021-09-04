package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName

data class NotificationUpdateRequest(
    @SerializedName("recordId")
    var recordId: String = "",

    @SerializedName("fieldApi")
    var fieldApi: String = "",

    @SerializedName("newValue")
    var newValue: String = ""

)



