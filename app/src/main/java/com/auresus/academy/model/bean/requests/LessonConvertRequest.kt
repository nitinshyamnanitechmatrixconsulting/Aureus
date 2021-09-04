package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName

data class LessonConvertRequest(
    @SerializedName("id")
    var id: String = ""
)



