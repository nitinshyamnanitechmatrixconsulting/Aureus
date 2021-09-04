package com.auresus.academy.model.bean.responses

import java.io.Serializable

data class NotificationListResponse(
/*{
  "title": "Piano Lesson Cancelled",
  "notificationId": "a0o6F00000lLEK2QAO",
  "isRead": false,
  "isImportant": false,
  "image_url": null,
  "createdDate": "2019-10-15T07:21:07.000Z",
  "button_url": null,
  "bodymsg": "Ian Lang (App Test)&#39;s Piano lesson has been cancelled on 21, Oct 10:00 AM. Please view the lesson details page for more details."
}*/
    var notifications: List<NotificationList>
)

data class NotificationList(
    var title: String,
    var notificationId: String,
    var isRead: Boolean,
    var isImportant: Boolean,
    var image_url: String,
    var createdDate: String,
    var button_url: String,
    var bodymsg: String
) : Serializable