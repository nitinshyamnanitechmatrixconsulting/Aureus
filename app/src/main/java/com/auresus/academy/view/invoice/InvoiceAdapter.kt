package com.auresus.academy.view.invoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemInvoiceBinding
import com.auresus.academy.model.bean.responses.InvoiceList

class InvoiceAdapter(
    private val items: MutableList<InvoiceList>,
    private val listener: IInvoiceItemListener
) :
    RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInvoiceBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enrollment: InvoiceList) {
            binding.contact = enrollment
            binding.executePendingBindings()
            binding.notificationItem.setOnClickListener {
                listener.itemClick(enrollment)
            }
            binding.notificationDescrption.text = "SGD $${enrollment.totalAmount}"
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(enrollments: List<InvoiceList>?) {
        enrollments?.let {
            items.clear()
            items.addAll(enrollments)
            notifyDataSetChanged()
        }
    }

}