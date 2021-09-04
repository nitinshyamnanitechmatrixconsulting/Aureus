package com.auresus.academy.view.studenthome.ticket

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentTicketDetailsBinding
import com.auresus.academy.model.bean.responses.TicketList
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import org.koin.android.ext.android.inject

class TicketDetailsFragment : BaseFragment() {
    private var tickerListItem: TicketList? = null
    lateinit var binding: FragmentTicketDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()

    companion object {
        val TAG = TicketDetailsFragment::class.simpleName
        fun newInstance(enrollment: TicketList): TicketDetailsFragment {
            var bundle = Bundle()
            bundle.putSerializable("ticketList", enrollment)
            var frag = TicketDetailsFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_ticket_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentTicketDetailsBinding
        if (arguments != null)
            tickerListItem = arguments?.get("ticketList") as TicketList?
        if (tickerListItem != null) {
            setData()
        }

    }

    private fun setData() {
        binding.studentName.text = tickerListItem?.studentName
        binding.lessonDate.text = DateTimeUtil.notificationDate(tickerListItem!!.createdDate)
        binding.lessonType.text = tickerListItem?.type
        binding.lessonStatus.text = tickerListItem?.type

        binding.ticketDate.text = DateTimeUtil.notificationDate(tickerListItem!!.createdDate)
        binding.ticketDescrption.text = tickerListItem?.description
    }

}