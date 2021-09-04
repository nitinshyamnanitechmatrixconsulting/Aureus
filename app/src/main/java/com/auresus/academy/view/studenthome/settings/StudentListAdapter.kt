package com.auresus.academy.view.studenthome.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemStudentBinding
import com.auresus.academy.model.bean.Student

class StudentListAdapter(
    private val items: MutableList<Student>,
    private val listener: IStudentItemListener
) :
    RecyclerView.Adapter<StudentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudentBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: Student) {
            binding.contact = enrollment
            binding.executePendingBindings()
            binding.studentItem.setOnClickListener {
                listener.itemClick(enrollment)
            }
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