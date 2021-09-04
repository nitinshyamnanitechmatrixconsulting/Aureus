package com.auresus.academy.view.studenthome.ticket

import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.NotificationList
import com.auresus.academy.model.bean.responses.TicketList

interface ITicketItemListener {
    fun itemClick(ticketItem: TicketList)
}