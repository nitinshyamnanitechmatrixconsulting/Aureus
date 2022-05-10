package com.auresus.academy.utils

import com.auresus.academy.model.bean.Booking
import java.text.SimpleDateFormat
import java.util.*


object DateTimeUtil {
    val fullDateFormatter: SimpleDateFormat = SimpleDateFormat("d MMM, yyyy h:mm a")
    val SimpleDateFormatter: SimpleDateFormat = SimpleDateFormat("d MMM")
    val fullSimpleDateFormatter: SimpleDateFormat = SimpleDateFormat("d MMM, yyyy")
    val timeFormatter: SimpleDateFormat = SimpleDateFormat("h:mm a")
    val bookingTimeParser: SimpleDateFormat = SimpleDateFormat("HH:mm:ss.SSS'Z'")
    val dateTimeFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val invoiceDateTimeFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    val cardExpity: SimpleDateFormat = SimpleDateFormat("MM / yyyy")
    val monthFormatter: SimpleDateFormat = SimpleDateFormat("MMMM")

    val schdeuleDateTimeFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    val dayFormatter: SimpleDateFormat = SimpleDateFormat("EEEE")
    val dayTimeFormatter: SimpleDateFormat = SimpleDateFormat("EEEE h:mm a")

    fun isUpcomingBooking(b: Booking): Boolean {
        if (b.date == null) return false
        val todayStr: String = fullDateFormatter.format(System.currentTimeMillis())
        val today: Date = fullDateFormatter.parse(todayStr)
        val bookingDateStr: String =
            fullDateFormatter.format(SimpleDateFormat("yyyy-MM-dd").parse(b.date))
        val bookingDate: Date = fullDateFormatter.parse(bookingDateStr)
        if ((bookingDate.after(today) ||
                    bookingDate.day === today.day && bookingDate.month === today.month && bookingDate.year === today.year) &&
            b.status == "Scheduled"
        ) {
            return true
        } else if (b.status == "Canceled" || b.status == "No Show" || b.status == "Completed") {
            return false
        }
        return false
    }

    fun notificationDate(date: String): String {

        val date1 = dateTimeFormatter.parse(date)
        if (date1 != null)
            return fullSimpleDateFormatter.format(date1)
        return ""
    }

    fun notificationDateShort(date: String): String {
        val date1 = dateTimeFormatter.parse(date)
        if (date1 != null)
            return SimpleDateFormatter.format(date1)
        return ""
    }

    fun invoiceDate(date: String): String {
        val date1 = invoiceDateTimeFormatter.parse(date)
        if (date1 != null)
            return fullSimpleDateFormatter.format(date1)
        return ""
    }


    fun studentDOB(date: String): String {
        val date1 = invoiceDateTimeFormatter.parse(date)
        if (date1 != null)
            return fullSimpleDateFormatter.format(date1)
        return ""
    }

    fun dateToTimeStamp(date: String): Long {
        var date1 = invoiceDateTimeFormatter.parse(date)
        return date1.time
    }

    fun timeStampToTime(startDate: Long): String {
        var date = Date(startDate)
        return schdeuleDateTimeFormatter.format(date)
    }


    fun studentTime(date: String): String {
        val date1 = bookingTimeParser.parse(date)
        if (date1 != null)
            return timeFormatter.format(date1)
        return ""
    }

    fun studentTimeReverse(date: String): String {
        val date1 = timeFormatter.parse(date)
        if (date1 != null)
            return bookingTimeParser.format(date1)
        return ""
    }
    fun createMakeUpDate(date: String): String {
        val date1 = invoiceDateTimeFormatter.parse(date)
        if (date1 != null)
            return schdeuleDateTimeFormatter.format(date1)
        return ""
    }
}