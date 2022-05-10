package com.auresus.academy.view.studenthome.makeup

import android.widget.TextView
import java.text.FieldPosition

interface MakeUpInstrumentListener {
    fun instrumentClickListener(textView:TextView,position: Int)
}