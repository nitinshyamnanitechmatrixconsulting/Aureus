package com.auresus.academy.view.studenthome.home

import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.Enrollment

interface   ILessonItemListener {

    fun onItemClick(enrollment: Booking)

//    fun enrollmentDetails(notificationItem: Enrollment)
//    fun schedule(notificationItem: Enrollment)
//    fun studentDetails(notificationItem: Enrollment)
}