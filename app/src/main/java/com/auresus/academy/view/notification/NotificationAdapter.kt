package com.auresus.academy.view.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemNotificationBinding
import com.auresus.academy.model.bean.responses.NotificationList
import com.auresus.academy.utils.DateTimeUtil

class NotificationAdapter(private val items: MutableList<NotificationList>, private val listener: INotificationItemListener) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =  items.size

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: NotificationList) {
            binding.contact = enrollment
            binding.dateShort.text = DateTimeUtil.notificationDateShort(enrollment.createdDate)
            binding.dateFull.text = DateTimeUtil.notificationDate(enrollment.createdDate)
            binding.unreadIcon.visibility = if (enrollment.isRead) View.GONE else View.VISIBLE
            binding.executePendingBindings()
            binding.notificationItem.setOnClickListener {
                listener.itemClick(enrollment)
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(enrollments: List<NotificationList>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}