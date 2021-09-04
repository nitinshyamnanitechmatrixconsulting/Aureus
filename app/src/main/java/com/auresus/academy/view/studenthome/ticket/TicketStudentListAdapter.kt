package com.auresus.academy.view.studenthome.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemStudentTicketBinding
import com.auresus.academy.model.bean.Student
import com.auresus.academy.view.studenthome.settings.IStudentItemListener

class TicketStudentListAdapter(
    private val items: MutableList<Student>,
    private val listener: IStudentItemListener
) :
    RecyclerView.Adapter<TicketStudentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudentTicketBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size
    private var lastselectedPosition: Int = -1

    inner class ViewHolder(val binding: ItemStudentTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var lastCheckedRB: RadioButton? = null
        fun bind(enrollment: Student) {
            binding.contact = enrollment
            binding.executePendingBindings()
            binding.studentItem.setOnClickListener {
                listener.itemClick(enrollment)
            }
            binding.radioButton.setOnClickListener {
                lastselectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.binding.radioButton.isChecked = lastselectedPosition == position;
    }

    fun setList(enrollments: List<Student>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}