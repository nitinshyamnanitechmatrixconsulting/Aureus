package com.auresus.academy.view.studenthome

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeupBinding
import com.auresus.academy.model.bean.responses.MakeupList
import com.auresus.academy.model.bean.responses.MakeupListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.makeup.MakeUpListAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MakeUpFragment : BaseFragment() {
    val preferenceHelper: PreferenceHelper by inject()
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    lateinit var binding: FragmentMakeupBinding
    private lateinit var makeUpListAdapter: MakeUpListAdapter
    private var makeupList: List<MakeupList> = ArrayList()
    var count : Int=0

    companion object {
        const val TAG = "MakeUpFragment"
        fun newInstance(): MakeUpFragment {
            return MakeUpFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_makeup
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeupBinding
        initRecyclerView()
        hitTicketApi()
        initCLickListener()
    }

    private fun initCLickListener() {
        binding.editPersonalDetails.setOnClickListener {
            (activity as HomeAcitivty).navigateToMakeUpsStudent(count.toString())
        }
    }

    private fun initRecyclerView() {
        binding.makeupLisrRv.layoutManager = LinearLayoutManager(activity)
        makeUpListAdapter = MakeUpListAdapter(mutableListOf())
        binding.makeupLisrRv.adapter = makeUpListAdapter
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
                makeupList = response.data?.bookings ?: emptyList()
                setMakeUpList()
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
    private fun setMakeUpList() {
        if (makeupList.isNotEmpty()) {
            makeUpListAdapter.setList(makeupList, "Make-Up Credit")
        }

         count = getTotalTime(makeupList)

        binding.creditTextView.text = "Make-up Credits - $count mins"
        binding.textMin.text = "$count Minutes"

        if(! makeupList.isEmpty() ) {
            val tempData: MakeupList = makeupList[0]
            binding.makeMinInfo.text = "Make-up Credit of " + tempData.availableMakeupMin + " expiring on " + DateTimeUtil.invoiceDate(
                tempData.expiryDate
            )
        }
    }

    private fun getTotalTime(tempList: List<MakeupList>): Int {

        var count : Int = 0
        for(temp in tempList ){
            if(temp.availableMakeupMin != null){
                val strs = temp.availableMakeupMin.split("min").toTypedArray()
                val parsedInt : Int? = strs[0].trim().toIntOrNull()
                count += parsedInt!!
            }
        }
        return count;
    }

}