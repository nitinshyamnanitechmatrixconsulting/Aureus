package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetContactListRequest : Serializable {

    @SerializedName("page")
    var page: String = ""

}