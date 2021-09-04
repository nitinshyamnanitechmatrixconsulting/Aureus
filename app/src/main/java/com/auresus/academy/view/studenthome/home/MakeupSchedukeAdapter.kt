package com.auresus.academy.view.studenthome.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemMakeupBookBinding
import com.auresus.academy.model.bean.Student
import com.auresus.academy.view.notification.INotificationItemListener

class MakeupSchedukeAdapter(
    private val items: MutableList<Student>,
    private val listener: INotificationItemListener
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
        fun bind(enrollment: Student) {
            binding.studentName.text = enrollment.firstName + enrollment.lastName
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(enrollments: List<Student>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}