package com.auresus.academy.view.notification

import com.auresus.academy.model.bean.responses.NotificationList

interface INotificationItemListener {
    fun itemClick(notificationItem: NotificationList)
}