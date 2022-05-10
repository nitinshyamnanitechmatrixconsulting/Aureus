package com.auresus.academy.view.studenthome.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemMakeupBookBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.view.notification.INotificationItemListener
import com.auresus.academy.view.studenthome.makeup.*

class MakeupSchedukeAdapter(
    var context: Context,
    private val items: MutableList<Enrollment>,
    private val listener: INotificationItemListener,
    private val instrumentListener: MakeUpInstrumentListener,
    private val teacherListener: MakeUpTeacherListener,
    private val packageItemListener: MakeUpPackageItemListener,
    private val makeUpSelectDateListener: MakeUpSelectDateListener,
    private val makeUpTimeSlotListener: MakeUpTimeSlotListener,
    var sameTimeOrAnyTime: String = ""

) :
    RecyclerView.Adapter<MakeupSchedukeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMakeupBookBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemMakeupBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
/*
        fun bind(enrollment: Enrollment) {
            binding.studentName.text = enrollment.studentName //+ enrollment.lastName
            binding.calendarView.selectedDate = binding.calendarView.currentDate
        }
*/

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bind(items[position])
        holder.binding.studentName.text = items[position].studentName //+ enrollment.lastName
        holder.binding.pionaText.text=items[position].instrument
        holder.binding.teacherText.text=items[position].teacherName
        holder.binding.calendarView.selectedDate = holder.binding.calendarView.currentDate




        if(holder.binding.btnCenter.isChecked){
            items[position].lessonTypeNew=holder.binding.btnCenter.text.toString()
        }else if(holder.binding.btnOnline.isChecked){
            items[position].lessonTypeNew=holder.binding.btnOnline.text.toString()
        }


        holder.binding.pionaText.setOnClickListener {
            instrumentListener.instrumentClickListener(holder.binding.pionaText, position)
        }

        holder.binding.teacherText.setOnClickListener {

            if (items[position].centerId != null)
                teacherListener.teacherClickListener(
                    holder.binding.teacherText,
                    items[position].centerId,
                    holder.binding.pionaText.text.toString(),
                    position
                )
        }

        holder.binding.packageText.setOnClickListener {
            if (items[position].centerId != null)
                packageItemListener.itemClick(holder.binding.packageText, items[position].centerId,position)
        }


        holder.binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            //yyyy-MM-dd
            var selectDate = "${date.year}-${date.month}-${date.day}"
            //Toast.makeText(context, "Today", Toast.LENGTH_LONG).show()
            // timeSlot = ""
            // getAvailableTime()
            if (items[position].centerId != null) {
                makeUpSelectDateListener.dateSelect(
                    holder.binding.selectSlotText,
                    items[position].centerId,
                    items[position].teacherId,
                    items[position].duration,
                    selectDate,
                    position
                )
            }
            items[position].date=selectDate
        }
        holder.binding.selectSlotText.setOnClickListener {
            makeUpTimeSlotListener.timeSlotClick(holder.binding.selectSlotText, position)
        }

        if (sameTimeOrAnyTime != "" && sameTimeOrAnyTime == "sametime") {

            if (position == items.size - 1) {
                holder.binding.calendarView.visibility = View.VISIBLE
                holder.binding.selectSlot.visibility = View.VISIBLE
            } else {
                holder.binding.calendarView.visibility = View.GONE
                holder.binding.selectSlot.visibility = View.GONE
            }
        }
    }

    fun setList(enrollments: List<Enrollment>?, string: String) {
        enrollments?.let {
            sameTimeOrAnyTime = string
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}