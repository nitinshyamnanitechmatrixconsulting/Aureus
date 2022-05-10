package com.auresus.academy.view.studenthome.makeup

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.AureusApplication
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeUpListBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.requests.MakeUpCreateRequest
import com.auresus.academy.model.bean.responses.CommonLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.utils.UserType
import com.auresus.academy.utils.isVisible
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.login.LoginViewModel
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MakeUpListFragment : BaseFragment() {
    private lateinit var createMakeUpListAdapter: CreateMakeUpListAdapter
    private lateinit var binding: FragmentMakeUpListBinding
    val preferenceHelper: PreferenceHelper by inject()
    private var makeUpDetailList: List<Enrollment>? = null
    private var createMakeUpList: MutableList<MakeUpCreateRequest> = ArrayList()
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    var sameTimeOrAnyTime: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_make_up_list

    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeUpListBinding
        if (arguments != null)
            makeUpDetailList = arguments?.getParcelableArrayList("makeUpDetail")
        sameTimeOrAnyTime = arguments?.getString("data").toString()
        initRecyclerView()
        initClickListener()

        if (makeUpDetailList != null) {
            createMakeUpListAdapter.setList(makeUpDetailList)
        }
    }

    companion object {
        val TAG = MakeUpListFragment::class.simpleName
        fun newInstance(
            lessonDetails: ArrayList<Enrollment>,
            string: String
        ): MakeUpListFragment {
            var bundle = Bundle()
            bundle.putParcelableArrayList("makeUpDetail", lessonDetails)
            bundle.putString("data", string)

            var frag = MakeUpListFragment()
            frag.arguments = bundle
            return frag
        }
    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        createMakeUpListAdapter = CreateMakeUpListAdapter(mutableListOf())
        binding.notificationRV.adapter = createMakeUpListAdapter
    }

    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }
        binding.nextButton.setOnClickListener {
            createMakeUpList.clear()
            if(sameTimeOrAnyTime.equals("anytime") ||sameTimeOrAnyTime == "") {
                for (i in 0..makeUpDetailList!!.size - 1) {
                    var careObject = MakeUpCreateRequest()
                    careObject.apply {
                        studentId = makeUpDetailList!![i].studentId
                        packageId = makeUpDetailList!![i].packageId
                        centerId = makeUpDetailList!![i].centerId
                        enrolmentId = makeUpDetailList!![i].id
                        if (makeUpDetailList!![i].time.contains("am", ignoreCase = true))
                            startTime = makeUpDetailList!![i].time.split("AM")[0].trim()
                        else startTime = makeUpDetailList!![i].time.split("PM")[0].trim()
                        parentId = preferenceHelper[PreferenceHelper.PARENR_ID]
                        lessonTypeNew = makeUpDetailList!![i].lessonTypeNew
                        duration = makeUpDetailList!![i].duration.split("min")[0].trim()
                        teacherId = makeUpDetailList!![i].teacherId
                        bookingdate = DateTimeUtil.createMakeUpDate(makeUpDetailList!![i].date)
                    }
                    createMakeUpList.add(careObject)
                }
            }else{
                for (i in 0..makeUpDetailList!!.size - 1) {
                    var careObject = MakeUpCreateRequest()
                    careObject.apply {
                        studentId = makeUpDetailList!![i].studentId
                        packageId = makeUpDetailList!![i].packageId
                        centerId = makeUpDetailList!![i].centerId
                        enrolmentId = makeUpDetailList!![i].id
                        if (makeUpDetailList!![makeUpDetailList!!.size - 1].time.contains("am", ignoreCase = true))
                            startTime = makeUpDetailList!![makeUpDetailList!!.size - 1].time.split("AM")[0].trim()
                        else startTime = makeUpDetailList!![makeUpDetailList!!.size - 1].time.split("PM")[0].trim()
                        parentId = preferenceHelper[PreferenceHelper.PARENR_ID]
                        lessonTypeNew = makeUpDetailList!![i].lessonTypeNew
                        duration = makeUpDetailList!![i].duration.split("min")[0].trim()
                        teacherId = makeUpDetailList!![i].teacherId
                        bookingdate = DateTimeUtil.createMakeUpDate(makeUpDetailList!![makeUpDetailList!!.size - 1].date)
                    }
                    createMakeUpList.add(careObject)
                }

            }




            createMakeUp()
        }

    }

    private fun createMakeUp() {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.createMakeUpRequest(createMakeUpList)
            makeUpViewModel.createMakeUpRequest.observe(this, createMakeUpObserver)
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

    private val createMakeUpObserver: Observer<ApiResponse<ResponseBody>> by lazy {
        Observer<ApiResponse<ResponseBody>> {
            it?.let {
                handleCreateMakeUpResponse(it)
            }
        }
    }

    private fun handleCreateMakeUpResponse(response: ApiResponse<ResponseBody>) {

        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {

                    Toast.makeText(activity, "MakeUp created successfully", Toast.LENGTH_LONG)
                        .show()
                    hitFingerPrintApi()
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

    private fun hitFingerPrintApi() {
        val email = preferenceHelper.getEmail()
        val password = preferenceHelper.getPassword()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (Connectivity.isConnected(activity)) {
                loginViewModel.signIn(loginViewModel.createLoginRequest(email, password))
                loginViewModel.loginResponse.observe(this, loginResponseObserver)
            } else {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.no_network_error),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }


    }

    private val loginResponseObserver: Observer<ApiResponse<CommonLoginResponse>> by lazy {
        Observer<ApiResponse<CommonLoginResponse>> {
            it?.let {
                handleLoginResponse(it)
            }
        }
    }

    private fun handleLoginResponse(response: ApiResponse<CommonLoginResponse>) {
        progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                progressLoader.isVisible(false)
                //preferenceHelper.setUserLoggedIn(true)
                //preferenceHelper.setUserEmail(email)
                //preferenceHelper.setUserPassword(password)
                setLoginData(response.data)
            }
            ApiResponse.Status.ERROR -> {
                progressLoader.isVisible(false)
                if (response.error?.code == 500)
                    Toast.makeText(activity, response.error.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(
                        activity,
                        getString(R.string.internal_server_error),
                        Toast.LENGTH_LONG
                    ).show()

            }
        }

    }

    private fun setLoginData(data: CommonLoginResponse?) {
        data?.let {
            val studentData = data.studentLoginResponse
            val teacherData = data.teacherLoginResponse
            if (teacherData != null) {
                AureusApplication.getInstance().setTeacherLoginData(teacherData)
                preferenceHelper.setUserType(UserType.USER_TYPE_TEACHER)
                preferenceHelper.setUserName(teacherData.teacherName)
                preferenceHelper.put(
                    PreferenceHelper.PARENR_ID,
                    teacherData.teacherBookings.get(0).Teacher_Account__c.toString()
                )
                // TeacherHomeActivity.open(activity)
            }
            if (studentData != null) {
                AureusApplication.getInstance().setStudentLoginData(studentData)
                preferenceHelper.setUserName(studentData.name)
                preferenceHelper.put(PreferenceHelper.PARENR_ID, studentData.parentId)
                preferenceHelper.setUserType(UserType.USER_TYPE_STUDENT)
                (activity as HomeAcitivty).navigateToHome()
            }
        }
    }

}