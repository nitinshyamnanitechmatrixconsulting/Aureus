package com.auresus.academy.view.teacherhome

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.auresus.academy.R
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import com.twilio.video.app.ui.room.RoomActivity
import kotlinx.android.synthetic.main.fragment_online_lesson_details.*
import kotlinx.android.synthetic.main.fragment_online_lesson_details.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class OnlineLessonDetailsFragment : BaseFragment() {
    private val baseViewModel: BaseViewModel by viewModel()

    companion object {
        val TAG = "OnlineLessonDetailsFragment"
        val EXTRA_MEETING_CODE = "EXTRA_MEETING_CODE"
        fun newInstance(teacherBooking: TeacherBooking): OnlineLessonDetailsFragment {
            val accountFragment = OnlineLessonDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_MEETING_CODE, teacherBooking)
                }
            }
            return accountFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_online_lesson_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {

        val booking = arguments?.get(EXTRA_MEETING_CODE) as TeacherBooking
        booking?.let {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'")
            val bookingDate = formatter.parse(it.Booking_Date__c + " "+it.Start_Time__c)
            val isPastBookingDate = bookingDate.before(Date())
            detailLayout.isVisible = !isPastBookingDate
        }
        booking?.Online_Lesson_Passcode__c?.let {
            onlineMeetingCode.text =
                String.format("Online Meeting Code: %s", booking?.Online_Lesson_Passcode__c)
        }
        val uri = Uri.parse(booking?.Online_Lesson_URL__c)
        val roomName = uri.getQueryParameter("room_name")
        val shortRoom=roomName!!.substringAfter("-")
        booking?.Online_Lesson_Passcode__c?.let {
            onlineMeetingId.text =
                String.format("Booking Id: %s", shortRoom)
        }
        buttonLink.text = booking?.Online_Lesson_URL__c
        buttonCopyLink.setOnClickListener {
            ShareCompat.IntentBuilder.from(activity as FragmentActivity)
                .setType("text/plain")
                .setText(booking?.Online_Lesson_URL__c+"&password="+booking?.Online_Lesson_Passcode__c)
                .startChooser();
        }
        view.buttonJoinOnlineStudio.setOnClickListener {


            booking?.let {
                val studentName = it.Teacher_Account__r?.Name
                it.Online_Lesson_URL__c?.let {
                    val uri = Uri.parse(booking?.Online_Lesson_URL__c)
                    val roomName = uri.getQueryParameter("room_name")
                    val room_id = booking.Id
                    val room_code = booking.Online_Lesson_Passcode__c
                    val type = "1"
                    roomName?.let {
                       RoomActivity.open(
                           activity as BaseActivity,
                           roomName,
                           room_id,
                           studentName,
                           room_code,
                           type
                       )
                    }
                }
            }
        }
    }


}