package com.auresus.academy.model.bean.responses

import com.google.gson.annotations.SerializedName

data class PackageResponse(
    @SerializedName("packageName" ) var packageName : String? = null,
    @SerializedName("packageId"   ) var packageId   : String? = null,
    @SerializedName("duration"    ) var duration    : Int?    = null
)
