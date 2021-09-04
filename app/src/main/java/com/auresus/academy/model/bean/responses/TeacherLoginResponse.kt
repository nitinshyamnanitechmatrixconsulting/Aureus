package com.auresus.academy.model.bean.responses

import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.TeacherBooking

data class TeacherLoginResponse(val teacherName: String, val teacherBookings: List<TeacherBooking>)