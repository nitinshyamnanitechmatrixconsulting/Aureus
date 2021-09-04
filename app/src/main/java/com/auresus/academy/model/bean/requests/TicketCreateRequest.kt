package com.auresus.academy.model.bean.requests

data class TicketCreateRequest(
    var parentId: String,
    var studentId: String,
    var requestType: String,
    var subject: String,
    var details: String,
    var bookingId: String,
    var invoiceId: String,
    var enrollmentId: String
)
