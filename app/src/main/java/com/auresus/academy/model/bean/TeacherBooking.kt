package com.auresus.academy.model.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TeacherBooking(
    val Appointment_Day__c: String?,
    val Booking_Date__c: String?,
    val Center_Location__c: String?,
    val Center__c: String?,
    val Center__r: CenterR?,
    val Duration__c: Int?,
    val Enrolment__c: String?,
    val Id: String?,
    val Instrument__c: String?,
    val Is_Reschedule_Booking__c: Boolean?,
    val Lesson_Type__c: String?,
    val Online_Lesson_Passcode__c: String?,
    val Online_Lesson_URL__c: String?,
    val Package__c: String?,
    val Package__r: PackageR?,
    val Start_Time__c: String?,
    val Status__c: String?,
    val Student__c: String?,
    val Student__r: StudentR,
    val Teacher_Account__c: String?,
    val Teacher_Account__r: TeacherAccountR?,
    val Type__c: String?,
    val attributes: Attributes?,
    val student_Name__c: String?
) : Parcelable

@Parcelize
data class CenterR(
    val Id: String?,
    val Name: String?,
    val attributes: Attributes?
) : Parcelable

@Parcelize
data class PackageR(
    val Id: String?,
    val Name: String?,
    val Type__c: String?,
    val attributes: Attributes?
) : Parcelable

@Parcelize
data class StudentR(
    val Id: String?,
    val Name: String?,
    val attributes: Attributes?
) : Parcelable

@Parcelize
data class TeacherAccountR(
    val Id: String?,
    val Name: String?,
    val attributes: Attributes?
) : Parcelable

@Parcelize
data class Attributes(
    val type: String?,
    val url: String?
) : Parcelable
