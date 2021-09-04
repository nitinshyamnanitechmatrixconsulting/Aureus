package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetBookingListRequest : Serializable {

    @SerializedName("enrollmentId")
    var enrollmentId: String = ""

    @SerializedName("type")
    var type: String = ""

    @SerializedName("Limit")
    var Limit: Int = 0

    @SerializedName("offset")
    var offset: Int = 0


}