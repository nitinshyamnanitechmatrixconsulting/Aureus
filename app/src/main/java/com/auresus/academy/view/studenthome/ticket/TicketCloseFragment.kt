package com.auresus.academy.view.studenthome.ticket

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityNotificationBinding
import com.auresus.academy.model.bean.requests.TicketRequest
import com.auresus.academy.model.bean.responses.TicketList
import com.auresus.academy.model.bean.responses.TicketListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TicketCloseFragment : BaseFragment() {

    private lateinit var mticketAdapter: TicketListAdapter
    private lateinit var binding: ActivityNotificationBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val ticketViewModel: TicketViewModel by viewModel()

    companion object {
        val TAG = TicketCloseFragment::class.simpleName
        fun newInstance(): TicketCloseFragment {
            return TicketCloseFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as ActivityNotificationBinding
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        hitTicketApi()
    }

    private fun hitTicketApi() {
        if (Connectivity.isConnected(activity)) {
            var request =
                TicketRequest(preferenceHelper[PreferenceHelper.PARENR_ID], "Historical", 10, 0)
            ticketViewModel.getTicketList(request)
            ticketViewModel.ticketRequest.observe(this, ticketListObserver)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
            (activity as HomeAcitivty).onBackPressed()
        }
    }

    private val ticketListObserver: Observer<ApiResponse<TicketListResponse>> by lazy {
        Observer<ApiResponse<TicketListResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<TicketListResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null && response.data.cases.isNotEmpty())
                    mticketAdapter.setList(response.data.cases)
                else {
                    binding.noTicketFound.visibility = View.VISIBLE
                }
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    activity,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mticketAdapter = TicketListAdapter(mutableListOf(),
            object : ITicketItemListener {
                override fun itemClick(ticketItem: TicketList) {
                    (activity as HomeAcitivty).navigateToTicketDetails(ticketItem)
                }
            })
        binding.notificationRV.adapter = mticketAdapter
    }

}