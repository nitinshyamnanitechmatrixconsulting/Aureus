package com.auresus.academy.view.studenthome.makeup

import android.widget.TextView

interface MakeUpSelectDateListener {
    fun dateSelect(
        textView: TextView,
        centerId: String,
        teacherId: String,
        duration: String,
        selectedDate: String,
        position: Int
    )
}