package com.auresus.academy.utils;

import android.graphics.Color
import android.graphics.Color.parseColor
import android.provider.CalendarContract.Colors




class AureusColors {
    companion object {
        var PRIMARY_COLOR: Int = parseColor("#ffffbf2f");
        var BUTTON_TEXT = Color.WHITE;
        var SUB_TITLE_HEADING = Color.parseColor("#ffababb6");
        var TITLE_HEADING = Color.parseColor("#ff4e4e56");
        var BOOKING_TITLE_COLOR = Color.parseColor("#ff3fa6fb");
        var CONTAINER_HEADING = Color.BLACK;
        var RED_BUTTON_COLOR = Color.parseColor("#ffe52b20");
        var RED_ALERT_COLOR = Color.parseColor("#ffe52b20");
        var LIGHT_BLUE_COLOR = Color.parseColor("#ff3fa6fb");
        var BLUE_COLOR = Color.parseColor("#ff5575c8");
        var GREEN_COLOR = Color.parseColor("#ff63ce63");
        var NAVBAR_SELECTED_COLOR = Color.parseColor("#ffffbf2f");
        var NAVBAR_UNSELECTED_COLOR = Color.parseColor("#ffccced0");
        var ALERT_YELLOW_COLOR = Color.parseColor("#ffffbf2f");

        var AUREUS_TAB_SELECTED_BG_COLOR = Color.WHITE;
        var AUREUS_TAB_UNSELECTED_BG_COLOR = Color.TRANSPARENT;
        var AUREUS_TAB_SELECTED_TEXT_COLOR = BLUE_COLOR;
        var AUREUS_TAB_UNSELECTED_TEXT_COLOR = NAVBAR_UNSELECTED_COLOR;
        var AUREUS_TAB_BG_COLOR = Color.parseColor("#FF343c43");
        var AUREUS_WHITTE_TEXT_COLOR = Color.WHITE;
        var AUREUS_GREEN_FLAT_BTN_COLOR = Color.GREEN;

        fun getBookingColor(status: String): Int? {
            var statusColor: Int? = PRIMARY_COLOR
            if (status == "Canceled") {
                statusColor = Color.parseColor("#ffccced0")
            } else if (status == "No Show") {
                statusColor = RED_ALERT_COLOR
            } else if (status == "Completed") {
                statusColor = GREEN_COLOR
            } else if (status == "Scheduled") {
                statusColor = PRIMARY_COLOR
            } else if (status == "Pending") {
                statusColor = Color.BLUE
            } else if (status == "5th Lesson") {
                statusColor = LIGHT_BLUE_COLOR
            }
            return statusColor
        }
    }
}
  