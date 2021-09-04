package com.auresus.academy.view.studenthome.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.auresus.academy.R
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import com.twilio.video.app.ui.room.RoomActivity
import kotlinx.android.synthetic.main.fragment_online_lesson_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class StudentOnlineLessonDetailsFragment : BaseFragment() {
    private val baseViewModel: BaseViewModel by viewModel()

    companion object {
        val TAG = "OnlineLessonDetailsFragment"
        val EXTRA_MEETING_CODE = "EXTRA_MEETING_CODE"
        fun newInstance(teacherBooking: Booking): StudentOnlineLessonDetailsFragment {
            val accountFragment = StudentOnlineLessonDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_MEETING_CODE, teacherBooking)
                }
            }
            return accountFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_online_lesson_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        val booking = arguments?.get(EXTRA_MEETING_CODE) as Booking
        booking?.let {
            val bookingdate = SimpleDateFormat("yyyy-MM-dd").parse(it.date)
            val isPastBookingDate = bookingdate.before(Date())
            detailLayout.isVisible = !isPastBookingDate
        }
        booking?.lessonPasscode?.let {
            onlineMeetingCode.text =
                String.format("Online Meeting Code: %s", booking?.lessonPasscode)
        }
        buttonLink.text = booking?.lessonOnlineURL
        buttonCopyLink.setOnClickListener {
            ShareCompat.IntentBuilder.from(activity as FragmentActivity)
                .setType("text/plain")
                .setText(booking?.lessonOnlineURL)
                .startChooser();
        }
        buttonJoinOnlineStudio.setOnClickListener {
            booking?.let {
                val studentName = it.studentName
                it.lessonOnlineURL?.let {
                    val uri = Uri.parse(booking?.lessonOnlineURL)
                    val roomName = uri.getQueryParameter("room_name")
                    val room_id = booking.bookingId
                    val room_code = booking.lessonPasscode
                    roomName?.let {
                       /* startActivity(Intent(
                            context,VideoActivity::class.java
                        ))*/
                       RoomActivity.open(
                           activity as BaseActivity,
                           roomName,
                           room_id,
                           studentName,
                           room_code
                       )
                    }
                }
            }
        }
    }


}