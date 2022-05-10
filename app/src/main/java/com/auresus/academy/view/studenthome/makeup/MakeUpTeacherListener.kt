package com.auresus.academy.view.studenthome.makeup

import android.widget.TextView

interface MakeUpTeacherListener {
    fun teacherClickListener(textView: TextView,centerId:String,instrument:String,position:Int)
}