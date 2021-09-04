package com.auresus.academy.model.bean.requests

data class EnrollmentUpdateRequest(
    var parentId: String,
    var enrollmentId : String,
    var studentId : String,
    var requestType : String,
    var subject : String,
    var details : String,
    var invoiceId : String
)