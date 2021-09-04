package com.auresus.academy.view.studenthome.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemEnrollmentStudentBinding
import com.auresus.academy.model.bean.Student
import com.auresus.academy.view.studenthome.settings.IStudentItemListener

class MakeStudentListAdapter(
    private val items: MutableList<Student>,
    private val listener: IStudentItemListener
) :
    RecyclerView.Adapter<MakeStudentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEnrollmentStudentBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemEnrollmentStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: Student) {
            binding.contact = enrollment
            items[adapterPosition].isChecked = binding.checkbox.isChecked
            binding.executePendingBindings()
            binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                items[adapterPosition].isChecked = isChecked
            }
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