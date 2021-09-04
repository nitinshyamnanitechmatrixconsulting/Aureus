package com.auresus.academy.model.bean

import java.io.Serializable

data class Enrollment(
    val NextBillDate: String,
    val bookings: List<Booking>,
    val centerId: String,
    val duration: String,
    val id: String,
    val instrument: String,
    val lessonDay: String,
    val lessonOnlineURL: Any,
    val lessonPasscode: Any,
    val lessonType: String,
    val lessonTypeNew: Any,
    val location: String,
    val packageId: String,
    val packageName: String,
    val packagePrice: String,
    val packageType: String,
    val programId: String,
    val status: String,
    val studentId: String,
    val studentName: String,
    val teacherId: String,
    val teacherName: String
):Serializable