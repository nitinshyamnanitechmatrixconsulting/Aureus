package com.auresus.academy.model.remote

class ApiConstant {

    companion object {
        /*********API BASE URL************/
        //JJ DEV_URL was 'https://full-aureusgroup.cs74.force.com/services/apexrest'
        const val DEV_URL = "https://full-aureusgroup.cs117.force.com/services/apexrest/";
        const val PROD_URL = "https://aureusgroup.secure.force.com/services/apexrest/";
        const val BASE_URL = "https://reqres.in/api/"
        const val API_TIME_OUT: Long = 6000
        const val GET_CONTACTS =  "users"
        const val GET_BOOKINGS =  "lesson"
        const val SIGN_IN =  "home"
        const val SIGN_UP =  "signup"
        const val FORGOT_PASSWORD =  "signup"
        const val GET_ATTACHMENTS ="attachmentbookingservice"
        const val GET_ACESS_TOKEN ="attachmentbookingservice"
        const val MEETING_SERVICE ="meetingservice"

        //"https://aureusacademy-meeting.herokuapp.com/token/" + newStringstudentName + "/" + roomName
        const val Notification_list =  "notifications"
        const val Notification_read =  "mobileNotification"
        const val INVOICE_LIST =  "Invoice/{parentId}"
        const val TICKET_LIST =  "case"
        const val STUDENT_UPDATE =  "case"
        const val MAKEUP_LIST =  "bookingCancellation"
        const val NOTIFICATION_UPDATE =  "updateFieldsRecords"
        const val LESSON_DETAILS =  "bookingHistory"
        const val LESSON_CONVERT_ONLINE =  "convertonlineapi"
        const val LESSON_CONVERT_INCENTRE =  "convertcentreapi"
        const val LESSON_Rescheduke =  "bookingReschedule"

        const val GET_LIST =  "picklist"
        const val TEACHER_LIST =  "TeachersInstrument"
        const val TEACHER_DATE =  "singleWorkingHours"
        const val TEACHER_TIME =  "singleWorkingHours"
        const val REFER_DISCOUNT_AMT =  "RAFMobileDiscountAmount"
        const val REFER_AMT =  "RAFMobileAmount"
        const val REFER_URL =  "RAFMobileBaseURL"
        const val REFER_ENROLLMENT =  "RAFMobileEnrolment"
        const val publicmeetingfeedback =  "publicmeetingfeedback"
        const val meetingfeedback =  "meetingfeedback"
    }

}