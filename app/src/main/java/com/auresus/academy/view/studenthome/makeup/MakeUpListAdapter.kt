package com.auresus.academy.view.studenthome.makeup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.databinding.ItemMakeupsBinding
import com.auresus.academy.model.bean.responses.MakeupList
import com.auresus.academy.utils.DateTimeUtil

class MakeUpListAdapter(
    private val items: MutableList<MakeupList>
) :
    RecyclerView.Adapter<MakeUpListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val binding = ItemEnrollmentStudentBinding.inflate(inflater)
        val binding = ItemMakeupsBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemMakeupsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(makeupList: MakeupList) {
            binding.makeupList = makeupList
            binding.dateTextView.setText(""+DateTimeUtil.invoiceDate(makeupList.expiryDate))
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(makeupList: List<MakeupList>?, title : String ) {
        makeupList?.let {
            items.clear()
            items.addAll(makeupList)
            notifyDataSetChanged()
        }
    }

}