package com.auresus.academy.view.studenthome.settings

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentContatcUsBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.OkDialogClickListener
import com.auresus.academy.utils.isVisible
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.ticket.TicketViewModel
import com.bigkoo.pickerview.MyOptionsPickerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactUsFragment : BaseFragment() {
    private var requestType: String = ""
    private lateinit var typeSpinner: MyOptionsPickerView<String>
    private var enrollment: Enrollment? = null
    lateinit var binding: FragmentContatcUsBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val ticketViewModel: TicketViewModel by viewModel()

    companion object {
        val TAG = ContactUsFragment::class.simpleName
        fun newInstance(): ContactUsFragment {
            var frag = ContactUsFragment()
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_contatc_us
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentContatcUsBinding
        initClickListener()
        setData()
        initSpinner()

    }

    private fun setData() {
    }

    private fun initClickListener() {
        binding.nextButton.setOnClickListener { hitTicketApi() }

    }

    private fun hitTicketApi() {
        if (Connectivity.isConnected(activity)) {
            ticketViewModel.createTicket(
                ticketViewModel.ticketCreateRequest(
                    preferenceHelper[PreferenceHelper.PARENR_ID],
                    requestType,
                    "Lesson Request",
                    binding.addComment.text.toString()
                )
            )
            ticketViewModel.createTicketRequest.observe(this, ticketListObserver)
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

    private val ticketListObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                DialogUtils.showAlertDialogCallback((activity as Activity), response.data!!.message,
                    object : OkDialogClickListener {
                        override fun onOkClick(dialog: Dialog) {
                            dialog.dismiss()
                            activity?.onBackPressed()
                        }
                    })
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

    private fun initSpinner() {
        typeSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getSchoolList(itemsSchool)
        typeSpinner.setPicker(itemsSchool)
        typeSpinner.setTitle("School")
        typeSpinner.setCyclic(false)
        typeSpinner.setSelectOptions(0)
        typeSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            requestType = itemsSchool[options1]
            binding.selectTypeText.text = itemsSchool[options1]
        }

    }

    private fun getSchoolList(itemsSchool: ArrayList<String>) {
        val schools = resources.getStringArray(R.array.requestTypeContact)
        for (arr2 in schools) {
            itemsSchool.add(arr2.toString())
        }
    }


}