package com.auresus.academy.model.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Student(
    val Birthdate: String,
    val firstName: String,
    val gender: String,
    val lastName: String,
    val learningStyle: String,
    val schoolName: String,
    val studentId: String,
    var isChecked: Boolean = false
) : Parcelable,Serializable
