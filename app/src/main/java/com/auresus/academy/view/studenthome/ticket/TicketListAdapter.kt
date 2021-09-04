package com.auresus.academy.view.studenthome.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemTicketBinding
import com.auresus.academy.model.bean.responses.TicketList
import com.auresus.academy.utils.DateTimeUtil

class TicketListAdapter(
    private val items: MutableList<TicketList>,
    private val listener: ITicketItemListener
) :
    RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTicketBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: TicketList) {
            binding.contact = enrollment
            binding.executePendingBindings()
            binding.ticketTime.text =
                "  ${DateTimeUtil.notificationDateShort(enrollment.createdDate)}"
            binding.studentItem.setOnClickListener {
                listener.itemClick(enrollment)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(enrollments: List<TicketList>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}