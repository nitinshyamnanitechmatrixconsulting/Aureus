package com.auresus.academy.model.bean.responses

import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Event
import com.auresus.academy.model.bean.Student

data class StudentLoginResponse(
    val availableMakeupUnits: String,
    val bookingRemindedNotification: Boolean,
    val cardNumber: String,
    val cardType: String,
    val city: String,
    val country: String,
    val currency: String,
    val email: String,
    val enrolments: List<Enrollment>,
    val events: List<Event>,
    val expiration: String,
    val fifthBookingFaqUrl: String,
    val firstName: String,
    val gender: String,
    val invoicePaidNotification: Boolean,
    val isAmericanClubMember: Boolean,
    val isInvoiceDue: Boolean,
    val lastName: String,
    val name: String,
    val nationality: String,
    val nextBillingDate: String,
    val parentId: String,
    val phone: String,
    val postal_Code: String,
    val profilePictureUrl: String,
    val street: String,
    val students: List<Student>
)