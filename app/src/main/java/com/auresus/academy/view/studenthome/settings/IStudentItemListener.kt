package com.auresus.academy.view.studenthome.settings

import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.NotificationList

interface IStudentItemListener {
    fun itemClick(notificationItem: Student)
}