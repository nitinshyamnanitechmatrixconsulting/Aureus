package com.auresus.academy.model.bean.responses

 data class MeetingServiceResponse(
    val Id: String,
    val Online_Lesson_Passcode__c: String,
    val Online_Lesson_URL__c: String,
    val Parent_Name__c: String,
    val Parent__c: String,
    val Type__c: String,
    val attributes: Attributes
)

data class Attributes(
    val type: String,
    val url: String
)