package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName

data class LessonRescheduleRequest(
    @SerializedName("time")
    var time: String = "",

    @SerializedName("teacherId")
    var teacherId: String = "",

    @SerializedName("oldteacherId")
    var oldteacherId: String = "",

    @SerializedName("olddate")
    var olddate: String = "",

    @SerializedName("oldtime")
    var oldtime: String = "",

    @SerializedName("duration")
    var duration: String = "",

    @SerializedName("date")
    var date: String = "",

    @SerializedName("bookingId")
    var bookingId: String = "",

    @SerializedName("lessonTypeNew")
    var lessonTypeNew: String = ""

)



