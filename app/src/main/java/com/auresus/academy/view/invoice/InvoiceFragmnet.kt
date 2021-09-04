package com.auresus.academy.view.invoice

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityInvoiceBinding
import com.auresus.academy.model.bean.responses.InvoiceList
import com.auresus.academy.model.bean.responses.InvoiceListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InvoiceFragmnet : BaseFragment() {

    private lateinit var mInvocieAdaper: InvoiceAdapter
    private lateinit var binding: ActivityInvoiceBinding
    private val notificationViewModel: InvoiceViewModel by viewModel()
    val preferenceHelper: PreferenceHelper by inject()

    companion object {
        val TAG = InvoiceFragmnet::class.simpleName
        fun newInstance(): InvoiceFragmnet {
            return InvoiceFragmnet()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_invoice

    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as ActivityInvoiceBinding
        initClickListener()
        initRecyclerView()
        hitInvoiceApi()
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mInvocieAdaper = InvoiceAdapter(mutableListOf(),
            object : IInvoiceItemListener {
                override fun itemClick(notificationItem: InvoiceList) {
                    InvoiceDetailsAcitivty.open(
                        (activity as BaseActivity),
                        notificationItem
                    )
                }
            })
        binding.notificationRV.adapter = mInvocieAdaper
    }

    private fun hitInvoiceApi() {
        if (Connectivity.isConnected(activity)) {
            notificationViewModel.getInvoiceList(preferenceHelper[PreferenceHelper.PARENR_ID])
            notificationViewModel.notificationRequest.observe(this, notificationListObserver)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private val notificationListObserver: Observer<ApiResponse<InvoiceListResponse>> by lazy {
        Observer<ApiResponse<InvoiceListResponse>> {
            it?.let {
                handleNotificationResponse(it)
            }
        }
    }

    private fun handleNotificationResponse(response: ApiResponse<InvoiceListResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null && response.data.invoices.isNotEmpty())
                    mInvocieAdaper.setList(response.data.invoices)
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

}