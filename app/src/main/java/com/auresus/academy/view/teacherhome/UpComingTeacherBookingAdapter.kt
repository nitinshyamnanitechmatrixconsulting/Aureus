package com.auresus.academy.view.teacherhome

import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemTeacherBookinggBinding
import com.auresus.academy.databinding.ItemUpcomingLessonBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.utils.AureusColors
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.utils.DateTimeUtil.bookingTimeParser
import com.auresus.academy.view.OnItemClickListener
import java.text.SimpleDateFormat

class UpComingTeacherBookingAdapter(private val items: MutableList<TeacherBooking>) :
    RecyclerView.Adapter<UpComingTeacherBookingAdapter.ViewHolder>() {
    var clickListener: OnItemClickListener<TeacherBooking>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTeacherBookinggBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: ItemTeacherBookinggBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: TeacherBooking) {
            with(binding) {
                binding?.root.setOnClickListener{
                    clickListener?.onItemOnClick(booking)
                }
                val today = DateTimeUtil.fullDateFormatter.format(System.currentTimeMillis())
                val todayDate = DateTimeUtil.fullDateFormatter.parse(today)
//                val statusColor =
//                    AureusColors.getBookingColor(if (booking.isFifthBooking) "5th Lesson" else booking.status)
                val formatter = SimpleDateFormat("EEE, d MMM yyyy")
                val timeFormat = SimpleDateFormat("hh:mm aa")
                // ' - ${booking.instrument} Lesson (${booking.duration}) with Teacher ${booking.teacherName} at ${booking.centerName}'
                val studentDesc =
                    " " + "-" + " " + booking.Lesson_Type__c + " " + booking.Instrument__c + " " + "Lesson" + " " + "(" + booking.Duration__c + ")" + " " + "with Teacher" + " " + booking.Teacher_Account__r?.Name + " " + "at" + " " + booking.Center_Location__c
                val tileLabel =
                    formatter.format(SimpleDateFormat("yyyy-MM-dd").parse(booking.Booking_Date__c)) + " " + "at" + " " + timeFormat.format(
                        bookingTimeParser.parse(booking.Start_Time__c)
                    )
                val formattedDescription: SpannedString = buildSpannedString {
                    bold {
                        append(booking.student_Name__c)
                    }
                    append(studentDesc)
                }
                binding.lessonTime.text = tileLabel
               binding.studentDescription.text = formattedDescription

            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setLessons(bookings: MutableList<TeacherBooking>) {
        bookings?.let {
            items.clear()
            items.addAll(bookings)
            notifyDataSetChanged()
        }
    }

}