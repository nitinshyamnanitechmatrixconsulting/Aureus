package com.auresus.academy.view.studenthome.makeup

import android.widget.TextView
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.responses.InstrumentListResponse

interface MakeUpStudentItemListener {
   fun itemClick(notificationItem: Enrollment)
}