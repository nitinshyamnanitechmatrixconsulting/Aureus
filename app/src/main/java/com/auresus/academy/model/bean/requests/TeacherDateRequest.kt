package com.auresus.academy.model.bean.requests

data class TeacherDateRequest(
    var centerId: String,
    var teacherId: String,
    var date: String,
    var duration: String
)