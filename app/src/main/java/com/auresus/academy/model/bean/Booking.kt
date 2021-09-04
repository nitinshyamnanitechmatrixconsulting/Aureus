package com.auresus.academy.model.bean;

import java.io.Serializable

data class  Booking(
    val availableMakeupMin: String,
    val bookingId: String,
    val cancellationReason: Any,
    val center: String,
    val centerId: String,
    var date: String,
    var duration: String,
    val enrollmentId: Any,
    val expiryDate: Any,
    val gst: Any,
    val instrument: String,
    val isFifthBooking: Boolean,
    val lessonOnlineURL: String,
    val lessonPasscode: String,
    var lessonTypeNew: String,
    val packageName: String,
    val packageType: String,
    val rescheduled: Boolean,
    val status: String,
    val studentId: String,
    val studentName: String,
    var teacherId: String,
    var teacherName: String,
    var time: String,
    val type: String,
    val unitFee: Any,
    val weekday: String
):Serializable