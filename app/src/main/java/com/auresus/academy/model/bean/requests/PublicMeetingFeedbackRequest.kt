package com.auresus.academy.model.bean.requests

data class PublicMeetingFeedbackRequest(
    var person_name: String,
    var room_name: String,
    var rating: String
)
