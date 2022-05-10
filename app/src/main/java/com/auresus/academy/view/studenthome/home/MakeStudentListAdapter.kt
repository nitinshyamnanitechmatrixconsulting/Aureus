package com.auresus.academy.view.studenthome.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemEnrollmentStudentBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.view.studenthome.makeup.MakeUpStudentItemListener
import com.auresus.academy.view.studenthome.settings.IStudentItemListener

class MakeStudentListAdapter(
    private val items: MutableList<Enrollment>,
    private val listener: MakeUpStudentItemListener
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
        fun bind(enrollment: Enrollment) {
            binding.contact = enrollment
            items[adapterPosition].isChecked = binding.checkbox.isChecked
            binding.executePendingBindings()
            binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                items[adapterPosition].isChecked = isChecked
                listener.itemClick(enrollment)

            }
            /* binding.studentItem.setOnClickListener {
             }*/
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(enrollments: List<Enrollment>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}