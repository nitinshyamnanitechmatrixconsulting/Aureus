package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName

data class MakeUpCreateRequest(
    @SerializedName("studentId"     ) var studentId     : String? = null,
    @SerializedName("packageId"     ) var packageId     : String? = null,
    @SerializedName("centerId"      ) var centerId      : String? = null,
    @SerializedName("enrolmentId"   ) var enrolmentId   : String? = null,
    @SerializedName("startTime"     ) var startTime     : String? = null,
    @SerializedName("parentId"      ) var parentId      : String? = null,
    @SerializedName("lessonTypeNew" ) var lessonTypeNew : String? = null,
    @SerializedName("duration"      ) var duration      : String? = null,
    @SerializedName("teacherId"     ) var teacherId     : String? = null,
    @SerializedName("bookingdate"   ) var bookingdate   : String? = null

)
