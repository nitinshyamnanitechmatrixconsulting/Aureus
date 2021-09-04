package com.auresus.academy.view.studenthome

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeupBinding
import com.auresus.academy.model.bean.responses.InvoiceListResponse
import com.auresus.academy.model.bean.responses.MakeupListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MakeUpFragment : BaseFragment() {
    val preferenceHelper: PreferenceHelper by inject()
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    lateinit var binding: FragmentMakeupBinding

    companion object {
        val TAG = "MakeUpFragment"
        fun newInstance(): MakeUpFragment {
            val makeUpFragment = MakeUpFragment()
            return makeUpFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_makeup
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeupBinding
        hitTicketApi()
        initCLickListener()
    }

    private fun initCLickListener() {
        binding.editPersonalDetails.setOnClickListener {
            (activity as HomeAcitivty).navigateToMakeUpsStudent()
        }
    }


    private fun hitTicketApi() {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getMakeupList(preferenceHelper[PreferenceHelper.PARENR_ID])
            makeUpViewModel.makeupRequest.observe(this, ticketListObserver)
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

    private val ticketListObserver: Observer<ApiResponse<MakeupListResponse>> by lazy {
        Observer<ApiResponse<MakeupListResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<MakeupListResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
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