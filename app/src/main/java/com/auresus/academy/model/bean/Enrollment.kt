package com.auresus.academy.model.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Enrollment(
    var date: String,
    var time: String,
    var NextBillDate: String,
    val bookings: List<Booking>,
    val centerId: String,
    var duration: String,
    val id: String,
    var instrument: String,
    val lessonDay: String,
    val lessonOnlineURL: String,
    val lessonPasscode: String,
    val lessonType: String,
    var lessonTypeNew: String,
    val location: String,
    var packageId: String,
    val packageName: String,
    val packagePrice: String,
    val packageType: String,
    val programId: String,
    var regularTime: String,
    val status: String,
    val studentId: String,
    val studentName: String,
    var teacherId: String,
    var teacherName: String,
    var isChecked: Boolean = false
):Serializable, Parcelable