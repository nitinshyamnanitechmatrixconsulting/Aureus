package com.auresus.academy.model.bean.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotificationDetailsRequestRead: Serializable {
    @SerializedName("notificationId")
    var notificationId: List<String> = ArrayList();
}