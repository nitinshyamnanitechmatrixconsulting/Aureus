package com.auresus.academy.view.studenthome.makeup

import android.widget.TextView
import com.auresus.academy.model.bean.Enrollment
import java.text.FieldPosition

interface MakeUpPackageItemListener {
    fun itemClick(textView: TextView,location:String,position: Int)
}