package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentLessonRescheduleBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.requests.TeacherDateRequest
import com.auresus.academy.model.bean.responses.InstrumentListResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.bean.responses.TeacherList
import com.auresus.academy.model.bean.responses.TeacherListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view_model.BaseViewModel
import com.bigkoo.pickerview.MyOptionsPickerView
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LessonReschduleFragment : BaseFragment() {
    private var timeSlot: String = ""
    private val timeList: MutableList<String> = ArrayList();
    private val dateList: MutableList<String> = ArrayList();
    private var selectDate: String? = null
    private lateinit var studentSpinner: MyOptionsPickerView<String>
    private var timeSpinner: MyOptionsPickerView<String>? = null
    private var teacherList: List<TeacherList> = ArrayList()
    private var teacherId: String = ""
    private var teacherName: String = ""
    private var lessonDetails: Booking? = null
    private var updatedLessonDetails: Booking? = null
    lateinit var binding: FragmentLessonRescheduleBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    private var studentData: StudentLoginResponse? = null
    private val makeUpViewModel: MakeUpViewModel by viewModel()

    companion object {
        val TAG = LessonReschduleFragment::class.simpleName
        fun newInstance(enrollment: Booking): LessonReschduleFragment {
            val bundle = Bundle()
            bundle.putSerializable("lessonDetails", enrollment)
            val frag = LessonReschduleFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_lesson_reschedule
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentLessonRescheduleBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        if (arguments != null)
            lessonDetails = arguments?.get("lessonDetails") as Booking?
        if (lessonDetails != null) {
            setData()
            getTeacherList()
        }
        initClickListener()
        //getInsrumentList()
    }

    private fun getTeacherList() {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getTeacherList(lessonDetails!!.centerId, lessonDetails!!.instrument)
            makeUpViewModel.teacherRequest.observe(this, teacherListObserver)
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

    private fun getAvailableDate() {
        if (Connectivity.isConnected(activity)) {
            val request = TeacherDateRequest(
                date = "",
                centerId = lessonDetails!!.centerId,
                duration = lessonDetails!!.duration,
                teacherId = teacherId
            )
            makeUpViewModel.getTeacherDate(request)
            makeUpViewModel.teacherDateRequest.observe(this, availableDateObserver)
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

    private fun getAvailableTime() {
        if (Connectivity.isConnected(activity)) {
            val request = TeacherDateRequest(
                date = selectDate!!,
                centerId = lessonDetails!!.centerId,
                duration = lessonDetails!!.duration,
                teacherId = teacherId
            )
            makeUpViewModel.getTeacherTime(request)
            makeUpViewModel.teacherTimeRequest.observe(this, availableTimeObserver)
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

    private fun getInsrumentList() {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getInstumentList("Account", "Major_Instruments_Disciplines__c")
            makeUpViewModel.insturmentRequest.observe(this, ticketListObserver)
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

    private val ticketListObserver: Observer<ApiResponse<List<InstrumentListResponse>>> by lazy {
        Observer<ApiResponse<List<InstrumentListResponse>>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private val teacherListObserver: Observer<ApiResponse<TeacherListResponse>> by lazy {
        Observer<ApiResponse<TeacherListResponse>> {
            it?.let {
                handleTeacherListResponse(it)
            }
        }
    }
    private val availableDateObserver: Observer<ApiResponse<JsonElement>> by lazy {
        Observer<ApiResponse<JsonElement>> {
            it?.let {
                handleAvailableDateResponse(it)
            }
        }
    }

    private val availableTimeObserver: Observer<ApiResponse<JsonElement>> by lazy {
        Observer<ApiResponse<JsonElement>> {
            it?.let {
                handleAvailableTimeResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<List<InstrumentListResponse>>) {
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

    private fun handleAvailableDateResponse(response: ApiResponse<JsonElement>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    for (i in (response.data as JsonArray)) {
                        dateList.add(i.toString())
                    }
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

    private fun handleAvailableTimeResponse(response: ApiResponse<JsonElement>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    for (i in (response.data as JsonArray)) {
                        timeList.add(i.toString())
                    }
                    binding.selectSlotText.text = "Select a slot"
                    if (timeList.size > 0)
                        initTimeSpinner()
                    else
                        binding.selectSlotText.text = "No Slot Available"
                } else
                    binding.selectSlotText.text = "No Slot Available"
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

    private fun handleTeacherListResponse(response: ApiResponse<TeacherListResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    teacherList = response.data.teachers
                    teacherId = response.data.teachers[0].teacherId
                    getAvailableDate()
                    initTeacherSpinner()
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

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                studentData = it
                if (studentData != null)
                    setData()
            }
        }
    }


    private fun initClickListener() {
        binding.calendarView.selectedDate = binding.calendarView.currentDate

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            //yyyy-MM-dd
            selectDate = "${date.year}-${date.month}-${date.day}"
            timeSlot = ""
            getAvailableTime()
        }
        binding.teacherText.setOnClickListener {
            studentSpinner.show()
        }
        binding.selectSlotText.setOnClickListener {
            if (timeSpinner != null)
                timeSpinner!!.show()
        }
        binding.nextButton.setOnClickListener {
            if (TextUtils.isEmpty(timeSlot)) {
                Toast.makeText(activity, "Please select time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updatedLessonDetails = Booking(
                availableMakeupMin = if (lessonDetails?.availableMakeupMin != null) lessonDetails!!.availableMakeupMin else "",
                lessonOnlineURL = lessonDetails!!.lessonOnlineURL,
                isFifthBooking = lessonDetails!!.isFifthBooking,
                instrument = lessonDetails!!.instrument,
                gst = if (lessonDetails?.gst != null) lessonDetails!!.gst else "",
                expiryDate = if (lessonDetails?.expiryDate != null) lessonDetails!!.expiryDate else "",
                center = lessonDetails!!.center,
                cancellationReason = if (lessonDetails?.cancellationReason != null) lessonDetails!!.cancellationReason else "",
                centerId = lessonDetails!!.centerId,
                date = lessonDetails!!.date,
                duration = lessonDetails!!.duration,
                bookingId = lessonDetails!!.bookingId,
                lessonTypeNew = lessonDetails!!.lessonTypeNew,
                teacherId = lessonDetails!!.teacherId,
                enrollmentId = lessonDetails!!.enrollmentId,
                studentId = lessonDetails!!.studentId,
                lessonPasscode = lessonDetails!!.lessonPasscode,
                packageName = lessonDetails!!.packageName,
                packageType = lessonDetails!!.packageType,
                rescheduled = lessonDetails!!.rescheduled,
                status = lessonDetails!!.status,
                studentName = lessonDetails!!.studentName,
                teacherName = lessonDetails!!.teacherName,
                time = lessonDetails!!.time,
                type = lessonDetails!!.packageType,
                unitFee = if (lessonDetails?.unitFee != null) lessonDetails!!.unitFee else "",
                weekday = lessonDetails!!.weekday
            )

            if (binding.btnOnline.isChecked)
                updatedLessonDetails!!.lessonTypeNew = "Online"
            else
                updatedLessonDetails!!.lessonTypeNew = "In Centre"
            updatedLessonDetails!!.teacherId = teacherId
            updatedLessonDetails!!.teacherName = teacherName
            updatedLessonDetails!!.date = selectDate!!
            updatedLessonDetails!!.time = timeSlot
            (activity as HomeAcitivty).navigateToLessonReschdeduleView(
                lessonDetails!!,
                updatedLessonDetails!!
            )
        }
    }

    private fun setData() {
    }


    private fun initTeacherSpinner() {
        studentSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getTeacherNames(itemsSchool)
        studentSpinner.setPicker(itemsSchool)
        studentSpinner.setTitle("Teachers")
        studentSpinner.setCyclic(false)
        studentSpinner.setSelectOptions(0)
        studentSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            teacherName = itemsSchool[options1]
            teacherId = findTeacherId()
            binding.teacherText.text = itemsSchool[options1]
            getAvailableDate()
            timeSlot = ""
        }
        teacherName = itemsSchool[0]
        binding.teacherText.text = itemsSchool[0]
    }

    private fun findTeacherId(): String {
        for (techer in teacherList) {
            if (techer.teachername == teacherName)
                return techer.teacherId
        }
        return (lessonDetails?.teacherId!!)
    }

    private fun initTimeSpinner() {
        timeSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getSlots(itemsSchool)
        timeSpinner!!.setPicker(itemsSchool)
        timeSpinner!!.setTitle("Type")
        timeSpinner!!.setCyclic(false)
        timeSpinner!!.setSelectOptions(0)
        timeSpinner!!.setOnoptionsSelectListener { options1, option2, options3 ->
//            timeSlot = options1.toString()
            timeSlot = DateTimeUtil.studentTimeReverse(itemsSchool[options1].replace("\"", ""))
            binding.selectSlotText.text = itemsSchool[options1].replace("\"", "")
        }
    }

    private fun getTeacherNames(itemsSchool: ArrayList<String>) {
        for (item in teacherList) {
            itemsSchool.add(item.teachername)
        }
    }

    private fun getSlots(itemsSchool: ArrayList<String>) {
        itemsSchool.add("Select time slot")
        for (item in timeList) {
            itemsSchool.add(item)
        }
    }

}