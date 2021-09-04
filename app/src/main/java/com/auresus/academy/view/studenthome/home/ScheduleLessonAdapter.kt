package com.auresus.academy.view.studenthome.home

import android.text.SpannedString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.R
import com.auresus.academy.databinding.ItemUpcomingLessonBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.utils.AureusColors
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.utils.DateTimeUtil.bookingTimeParser
import java.text.SimpleDateFormat

class ScheduleLessonAdapter(
    private val items: MutableList<Booking>,
    private val listener: ILessonItemListener
) :
    RecyclerView.Adapter<ScheduleLessonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUpcomingLessonBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: ItemUpcomingLessonBinding) :


        RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            with(binding) {
                binding.root.setOnClickListener {
                    listener.onItemClick(booking)
                }
                val today = DateTimeUtil.fullDateFormatter.format(System.currentTimeMillis())
                val todayDate = DateTimeUtil.fullDateFormatter.parse(today)
                val statusColor =
                    AureusColors.getBookingColor(if (booking.isFifthBooking) "5th Lesson" else booking.status)
                val formatter = SimpleDateFormat("EEE, d MMM yyyy")
                val timeFormat = SimpleDateFormat("hh:mm aa")
                val chipLabel = if (booking.isFifthBooking) "5th Lesson" else booking.status
                // ' - ${booking.instrument} Lesson (${booking.duration}) with Teacher ${booking.teacherName} at ${booking.centerName}'
                val studentDesc =
                    " " + "-" + " " + booking.lessonTypeNew + " " + booking.instrument + " " + "Lesson" + " " + "(" + booking.duration + ")" + " " + "with Teacher" + " " + booking.teacherName + " " + "at" + " " + booking.center
                val tileLabel =
                    formatter.format(SimpleDateFormat("yyyy-MM-dd").parse(booking.date)) + " " + "at" + " " + timeFormat.format(
                        bookingTimeParser.parse(booking.time)
                    )
                val formattedDescription: SpannedString = buildSpannedString {
                    bold {
                        append(booking.studentName)
                    }
                    append(studentDesc)
                }
                binding.lessonTime.text = tileLabel
                binding.scheduleBtn.text = booking.status
                binding.studentDescription.text = formattedDescription

                if (booking.lessonTypeNew == "Online")
                    binding.timeIcon.setImageResource(R.drawable.globe_icon)
                else
                    binding.timeIcon.setImageResource(R.drawable.ic_baseline_location)

            }
        }
    }

    /*Widget getUpcomingLessonTile(ListWrapper bookingWrapper, bool isSelectable,
      Function _onStudentTileTap, Function _onCheckTap,
      {bool compact = false}) {
    Booking booking = bookingWrapper.booking;

    String todayStr = DateTimeUtil.fullDateFormatter.format(DateTime.now());
    DateTime today = DateTimeUtil.fullDateFormatter.parse(todayStr);

    Color statusColor =
        getBookingColor(booking.isFifthBooking ? '5th Lesson' : booking.status);
    var formatter = new DateFormat("EEE, d MMM yyyy");
    var timeformat = new DateFormat.jm("en_US");
    String chipLabel = booking.isFifthBooking ? '5th Lesson' : booking.status;

    Widget leading = isSelectable
        ? Checkbox(
            value: bookingWrapper.isSelected,
            onChanged: _onCheckTap,
          )
        : SizedBox(
            height: 0,
            width: 0,
          );
    String tileLabel = formatter.format(booking.dateTime) +
        ' at ' +
        timeformat.format(booking.dateTime);
    Widget title = Row(
      children: <Widget>[
        getListTileHeading(tileLabel,
            color: AureusColors.BOOKING_TITLE_COLOR, fontSize: 13),
        SizedBox(
          width: 4,
        ),
        AureusChip(
            Container(
              margin: EdgeInsets.all(3),
              child: getListTileHeading(chipLabel,
                  color: Colors.white, bold: false, fontSize: 11),
            ),
            statusColor),
      ],
    );*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setLessons(bookings: MutableList<Booking>) {
        bookings?.let {
            items.clear()
            items.addAll(bookings)
            notifyDataSetChanged()
        }
    }

}