package com.auresus.academy.view.studenthome.ticket

import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentTicketCreateBinding
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.OkDialogClickListener
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.settings.IStudentItemListener
import com.auresus.academy.view_model.BaseViewModel
import com.bigkoo.pickerview.MyOptionsPickerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TicketCreateFragment : BaseFragment() {
    private lateinit var typeSpinner: MyOptionsPickerView<String>
    lateinit var binding: FragmentTicketCreateBinding
    val preferenceHelper: PreferenceHelper by inject()
    private var students: List<Student> = ArrayList()
    private lateinit var mStudentAdapter: TicketStudentListAdapter
    private val baseViewModel: BaseViewModel by viewModel()
    private var requestType: String = ""
    private val ticketViewModel: TicketViewModel by viewModel()

    companion object {
        val TAG = TicketCreateFragment::class.simpleName
        fun newInstance(): TicketCreateFragment {
            return TicketCreateFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_ticket_create
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentTicketCreateBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        setData()
        initClickListener()
        initRecyclerView()
        initSpinner()
    }

    private fun initRecyclerView() {
        binding.studentListRv.layoutManager = LinearLayoutManager(activity)
        mStudentAdapter = TicketStudentListAdapter(mutableListOf(),
            object : IStudentItemListener {
                override fun itemClick(studentItem: Student) {

                }
            })
        binding.studentListRv.adapter = mStudentAdapter
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
        val schools = resources.getStringArray(R.array.requestType)
        for (arr2 in schools) {
            itemsSchool.add(arr2.toString())
        }
    }


    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                val events = it.events
                students = it.students
                setStudentList()
            }
        }
    }

    private fun setStudentList() {
        if (students.isNotEmpty())
            mStudentAdapter.setList(students)
    }


    private fun initClickListener() {
        binding.selectType.setOnClickListener {
            typeSpinner.show()
        }
        binding.createTicketBtn.setOnClickListener {
            if (TextUtils.isEmpty(requestType)) {
                Toast.makeText(context, "Select Request Type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.ticketSubject.text)) {
                Toast.makeText(context, "Please add ticket Details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.ticketDetails.text)) {
                Toast.makeText(context, "Please add ticket Details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            hitTicketApi()
        }
    }

    private fun hitTicketApi() {
        if (Connectivity.isConnected(activity)) {
            ticketViewModel.createTicket(
                ticketViewModel.ticketCreateRequest(
                    preferenceHelper[PreferenceHelper.PARENR_ID],
                    requestType,
                    binding.ticketSubject.text.toString(),
                    binding.ticketDetails.text.toString()
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

    private fun setData() {

    }

}