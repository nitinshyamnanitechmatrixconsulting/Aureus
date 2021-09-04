package com.auresus.academy.view.studenthome.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemEnrollmentBinding
import com.auresus.academy.model.bean.Enrollment

class EnrollmentAdapter(private val items: MutableList<Enrollment>, private val listener: IEnrollmentItemListener) :
    RecyclerView.Adapter<EnrollmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEnrollmentBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (items.size > 4) 4 else items.size


    inner class ViewHolder(val binding: ItemEnrollmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: Enrollment) {
            with(binding) {
                binding.contact = enrollment
                binding.executePendingBindings()
                binding.root.setOnClickListener {
                    listener.onItemClick(enrollment)
                }
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setEnrollments(enrollments: MutableList<Enrollment>) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}