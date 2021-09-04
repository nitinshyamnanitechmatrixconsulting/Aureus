package com.auresus.academy.view.studenthome.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmnetStudentDetailsBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.InstrumentListResponse
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view.studenthome.home.EnrollmentAdapter
import com.auresus.academy.view.studenthome.home.IEnrollmentItemListener
import com.bigkoo.pickerview.MyOptionsPickerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudentDetailsFragment : BaseFragment() {
    private var schoollist: List<InstrumentListResponse> = ArrayList()
    private lateinit var enrollmentAdapter: EnrollmentAdapter
    private var enrollment: ArrayList<Enrollment>? = null
    private var student: Student? = null
    lateinit var binding: FragmnetStudentDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()
    lateinit var genderSpinner: MyOptionsPickerView<String>
    lateinit var schooldSpinner: MyOptionsPickerView<String>
    private val studentViewModel: StudentViewModel by viewModel()
    private lateinit var updatedDate: Student
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    private var editMode: Boolean = false

    companion object {
        val TAG = StudentDetailsFragment::class.simpleName
        fun newInstance(
            student: Student,
            enrollment: ArrayList<Enrollment>
        ): StudentDetailsFragment {
            var bundle = Bundle();
            bundle.putSerializable("student", student)
            bundle.putSerializable("enrollment", enrollment)
            var fragment = StudentDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragmnet_student_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmnetStudentDetailsBinding
        if (arguments != null) {
            student = arguments?.get("student") as Student
            enrollment = arguments?.get("enrollment") as ArrayList<Enrollment>
        }
        if (student != null)
            setData()
        initRecyclerView()
        initClickListener()
        setEditMode(editMode)
        getInsrumentList()
    }

    private fun getInsrumentList() {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getInstumentList("Account", "School__c")
            makeUpViewModel.insturmentRequest.observe(this, schoolListObserver)
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

    private val schoolListObserver: Observer<ApiResponse<List<InstrumentListResponse>>> by lazy {
        Observer<ApiResponse<List<InstrumentListResponse>>> {
            it?.let {
                handleSchoolList(it)
            }
        }
    }


    private fun handleSchoolList(response: ApiResponse<List<InstrumentListResponse>>) {
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    schoollist = response.data
                    initSpinner()
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

    private fun setEditMode(isEditMode: Boolean) {
        binding.firstNameView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.lastNameView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.genderSpinnerIv.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.schoolSpinnerIv.visibility = if (isEditMode) View.VISIBLE else View.GONE

        binding.studentFirstName.isEnabled = isEditMode
        binding.studentLastName.isEnabled = isEditMode

        binding.textEnrollment.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.enrollemntRv.visibility = if (isEditMode) View.GONE else View.VISIBLE

        binding.editPersonalDetails.text =
            if (!isEditMode) "Edit Student Details" else "Update Student Details"
    }

    private fun initRecyclerView() {
        binding.enrollemntRv.layoutManager = LinearLayoutManager(activity)
        enrollmentAdapter = EnrollmentAdapter(mutableListOf(), object : IEnrollmentItemListener {
            override fun onItemClick(enrollment: Enrollment) {
                (activity as HomeAcitivty).navigateToEnrollmentDetails(enrollment)
            }
        })
        binding.enrollemntRv.adapter = enrollmentAdapter
        if (!enrollment.isNullOrEmpty())
            enrollmentAdapter.setEnrollments(enrollment!!.toMutableList())
        binding.editPersonalDetails.setOnClickListener {
            Toast.makeText(activity, "Coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initClickListener() {
        binding.editPersonalDetails.setOnClickListener {
            if (editMode)
                callApi()
            editMode = !editMode
            setEditMode(editMode)
        }
        binding.genderSpinnerIv.setOnClickListener {
            genderSpinner.show()
        }
        binding.schoolSpinnerIv.setOnClickListener {
            schooldSpinner.show()
        }
    }

    private fun callApi() {
        updatedDate = Student(
            studentId = student!!.studentId,
            Birthdate = student!!.Birthdate,
            firstName = binding.studentFirstName.text.toString(),
            gender = binding.studentGender.text.toString(),
            lastName = binding.studentLastName.text.toString(),
            learningStyle = "",
            schoolName = binding.studentSchool.text.toString()
        )
        studentViewModel.updateStudent(
            studentViewModel.createRequest(
                student!!,
                updatedDate,
                preferenceHelper[PreferenceHelper.PARENR_ID]
            )
        )
        studentViewModel.updateStudentRequest.observe(this, ticketListObserver)
    }

    private val ticketListObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<NotificationDeleteResponse>) {
        //binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
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


    private fun setData() {
        binding.studentFirstName.setText(student?.firstName)
        binding.studentLastName.setText(student?.lastName)
        binding.studentGender.text = student?.gender
        binding.studentSchool.text = student?.schoolName
        binding.studentDOB.text = DateTimeUtil.studentDOB(student?.Birthdate!!)
    }

    private fun initSpinner() {
        genderSpinner = MyOptionsPickerView(context)
        (genderSpinner.btnSubmit as Button).text = "Done"
        val items = ArrayList<String>()
        items.add("Male")
        items.add("Female")
        genderSpinner.setPicker(items)
        genderSpinner.setTitle("Gender")
        genderSpinner.setCyclic(false)
        genderSpinner.setSelectOptions(0)
        genderSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            //singleTVOptions.setText("Single Picker " + items.get(options1));
            binding.studentGender.text = items[options1].toString()
            binding.vMasker.visibility = View.GONE
        }

        schooldSpinner = MyOptionsPickerView(context)
        (genderSpinner.btnSubmit as Button).text = "Done"
        val itemsSchool = ArrayList<String>()
        getSchoolList(itemsSchool)
        schooldSpinner.setPicker(itemsSchool)
        schooldSpinner.setTitle("School")
        schooldSpinner.setCyclic(false)
        schooldSpinner.setSelectOptions(0)
        schooldSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            //singleTVOptions.setText("Single Picker " + items.get(options1));
            binding.studentSchool.text = itemsSchool[options1]
            binding.vMasker.visibility = View.GONE
        }

    }

    private fun getSchoolList(itemsSchool: ArrayList<String>) {
        for (item in schoollist) {
            itemsSchool.add(item.apiName)
        }
    }

}