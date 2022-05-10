package com.auresus.academy.view.studenthome.makeup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.R
import com.auresus.academy.databinding.ItemCreateMakeupBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.utils.DateTimeUtil


class CreateMakeUpListAdapter(private val items: MutableList<Enrollment>) :
    RecyclerView.Adapter<CreateMakeUpListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemCreateMakeupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(makeupList: Enrollment) {
            binding.enrollmentList = makeupList
            binding.userName.text = makeupList.studentName
            //binding.btnSchedules.text = lessonDetails?.status
            binding.lessonDate.text = DateTimeUtil.studentDOB(makeupList.date)
            binding.lessonType.text = makeupList.instrument
            binding.lessonTime.text = makeupList.time
            binding.teacherName.text = makeupList.teacherName
            binding.lessonDuration.text = makeupList.duration
            binding.lessonLocation.text = makeupList.location
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCreateMakeupBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(enrollments: List<Enrollment>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}