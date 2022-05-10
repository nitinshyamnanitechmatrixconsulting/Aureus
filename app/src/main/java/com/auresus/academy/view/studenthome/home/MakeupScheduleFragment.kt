package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeupScheduleBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.requests.TeacherDateRequest
import com.auresus.academy.model.bean.responses.*
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.notification.INotificationItemListener
import com.auresus.academy.view.notification.NotificationViewModel
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view.studenthome.makeup.*
import com.bigkoo.pickerview.MyOptionsPickerView
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MakeupScheduleFragment : BaseFragment() {

    private lateinit var mNotificationAdapter: MakeupSchedukeAdapter
    private lateinit var binding: FragmentMakeupScheduleBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val notificationViewModel: NotificationViewModel by viewModel()
    private var lessonDetails: List<Enrollment>? = null
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    private lateinit var instrumentsSpinner: MyOptionsPickerView<String>
    private lateinit var teachersSpinner: MyOptionsPickerView<String>
    private lateinit var packageSpinner: MyOptionsPickerView<String>
    private lateinit var timeSpinner: MyOptionsPickerView<String>
    private var instrumentList: List<InstrumentListResponse> = ArrayList()
    private var teacherList: MutableList<TeacherList> = ArrayList()
    private var packageList: List<PackageResponse> = ArrayList()
    private val timeList: MutableList<String> = ArrayList()
    var textViewTeacher: TextView? = null
    var textViewPackage: TextView? = null
    var textViewTimeSlot: TextView? = null

    // private var teacherId1: String = ""
    // private var timeDuration: String = ""
    var sameTimeOrAnyTime: String = ""
    private var posTeacher: Int = 0
    private var posDuration: Int = 0
    private var posTime: Int = 0
    private var totalMin: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_makeup_schedule
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeupScheduleBinding

        initClickListener()
        initRecyclerView()
        if (arguments != null)
            lessonDetails = arguments?.getParcelableArrayList("lessonDetails")
        sameTimeOrAnyTime = arguments?.getString("data").toString()
        totalMin = arguments?.getString("totalMin")!!
        if (lessonDetails != null) {
            mNotificationAdapter.setList(lessonDetails, sameTimeOrAnyTime)
        }
        getInstrumentList()

        //Toast.makeText(context, totalMin, Toast.LENGTH_LONG).show()
    }


    companion object {
        val TAG = MakeupScheduleFragment::class.simpleName
        fun newInstance(
            lessonDetails: ArrayList<Enrollment>,
            string: String,
            totalMin: String
        ): MakeupScheduleFragment {
            var bundle = Bundle()
            bundle.putParcelableArrayList("lessonDetails", lessonDetails)
            bundle.putString("data", string)
            bundle.putString("totalMin", totalMin)
            var frag = MakeupScheduleFragment()
            frag.arguments = bundle
            return frag
        }
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }

        binding.nextButton.setOnClickListener {
            var isError = false
            var msg = ""
            if (getTotalTime(lessonDetails!!) > totalMin.toInt()) {
                Toast.makeText(context, "You have not enough credit", Toast.LENGTH_LONG).show()
            } else {
                if (sameTimeOrAnyTime.equals("anytime") || sameTimeOrAnyTime == "") {
                    for (i in 0..lessonDetails!!.size - 1) {
                        if (lessonDetails!![i].instrument == null || lessonDetails!![i].instrument.isEmpty()) {
                            isError = true
                            msg = "Please select instrument"
                            break
                        } else if (lessonDetails!![i].teacherName == null || lessonDetails!![i].teacherName.isEmpty()) {
                            isError = true
                            msg = "Please select teacher"
                            break
                        } else if (lessonDetails!![i].packageName == null || lessonDetails!![i].packageName.isEmpty()) {
                            isError = true
                            msg = "Please select instrument"
                            break
                        } else if (lessonDetails!![i].date == null || lessonDetails!![i].date.isEmpty()) {
                            isError = true
                            msg = "Please select date"
                            break
                        } else if (lessonDetails!![i].time == null || lessonDetails!![i].time.isEmpty()) {
                            isError = true
                            msg = "Please select time slot"
                            break
                        }
                    }
                    if (isError == false) {
                        (activity as HomeAcitivty).navigateToMakeupBookList(lessonDetails!!,sameTimeOrAnyTime)
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }

                } else {
                    for (i in 0..lessonDetails!!.size - 1) {
                        if (lessonDetails!![i].instrument == null || lessonDetails!![i].instrument.isEmpty()) {
                            isError = true
                            msg = "Please select instrument"
                            break
                        } else if (lessonDetails!![i].teacherName == null || lessonDetails!![i].teacherName.isEmpty()) {
                            isError = true
                            msg = "Please select teacher"
                            break
                        } else if (lessonDetails!![i].packageName == null || lessonDetails!![i].packageName.isEmpty()) {
                            isError = true
                            msg = "Please select instrument"
                            break
                        } else if (lessonDetails!![lessonDetails!!.size - 1].time == null || lessonDetails!![i].time.isEmpty()) {
                            isError = true
                            msg = "Please select time slot"
                            break
                        } else if (lessonDetails!![lessonDetails!!.size - 1].date == null || lessonDetails!![i].date.isEmpty()) {
                            isError = true
                            msg = "Please select date"
                            break
                        }
                    }

                    if (isError == false) {
                        (activity as HomeAcitivty).navigateToMakeupBookList(lessonDetails!!,sameTimeOrAnyTime)
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }

                }
            }


        }

    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mNotificationAdapter = MakeupSchedukeAdapter(
            requireActivity(), mutableListOf(),
            object : INotificationItemListener {
                override fun itemClick(notificationItem: NotificationList) {

                    //  Toast.makeText(activity, "Fragment", Toast.LENGTH_LONG).show()
                }

            }, object : MakeUpInstrumentListener {
                override fun instrumentClickListener(textView: TextView, position: Int) {
                    initInstrumentSpinner(textView, position)
                    //Toast.makeText(activity, "Fragment", Toast.LENGTH_LONG).show()
                }

            }, object : MakeUpTeacherListener {
                override fun teacherClickListener(
                    textView: TextView,
                    centerId: String,
                    instrument: String,
                    position: Int
                ) {
                    posTeacher = position
                    getTeacherList(centerId, instrument)
                    textViewTeacher = textView
                }
            }, object : MakeUpPackageItemListener {
                override fun itemClick(textView: TextView, location: String, position: Int) {
                    getPackageList(location)
                    textViewPackage = textView
                }
            }, object : MakeUpSelectDateListener {
                override fun dateSelect(
                    textView: TextView,
                    centerId: String,
                    teacherId: String,
                    duration: String,
                    selectedDate: String,
                    position: Int
                ) {
                    posDuration = position
                    //Toast.makeText(context, "$teacherId,$duration", Toast.LENGTH_LONG).show()
                    // teacherId1 = teacherId
                    // timeDuration = duration
                    getAvailableTime(selectedDate, centerId, duration, teacherId)
                    textViewTimeSlot = textView
                }
            }, object : MakeUpTimeSlotListener {
                override fun timeSlotClick(textView: TextView, position: Int) {
                    posTime = position
                    if (timeList.size > 0)
                        initTimeSpinner(textView, position)
                    // timeSpinner.show()
                }
            }, sameTimeOrAnyTime
        )
        binding.notificationRV.adapter = mNotificationAdapter
    }


    private fun getInstrumentList() {
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

    private fun handleTicketResponse(response: ApiResponse<List<InstrumentListResponse>>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                instrumentList = response.data!!

                // initTeacherSpinner(notificationItem: TextView)
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

    private fun initInstrumentSpinner(textView: TextView, position: Int) {
        instrumentsSpinner = MyOptionsPickerView(context)
        val items = ArrayList<String>()
        getInstrumentNames(items)
        instrumentsSpinner.setPicker(items)
        instrumentsSpinner.setTitle("Instruments")
        instrumentsSpinner.setCyclic(false)
        instrumentsSpinner.setSelectOptions(0)
        instrumentsSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            // teacherName = itemsSchool[options1]
            // teacherId1 = findTeacherId()
            textView.text = items[options1]
            lessonDetails?.get(position)?.instrument = items[options1]
            // getAvailableDate()
            // timeSlot = ""
        }
        // teacherName = itemsSchool[0]
        // binding.teacherText.text = itemsSchool[0]
        instrumentsSpinner.show()
    }

    private fun getInstrumentNames(items: ArrayList<String>) {
        for (item in instrumentList) {
            items.add(item.label)
        }
    }

    private fun getTeacherList(centerId: String, instrument: String) {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getTeacherList(centerId, instrument)
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

    private val teacherListObserver: Observer<ApiResponse<TeacherListResponse>> by lazy {
        Observer<ApiResponse<TeacherListResponse>> {
            it?.let {
                handleTeacherListResponse(it)
            }
        }
    }

    private fun handleTeacherListResponse(response: ApiResponse<TeacherListResponse>) {
        teacherList.clear()
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    teacherList = response.data.teachers.toMutableList()
                    if (teacherList.size > 0)
                        initTeacherSpinner(textViewTeacher!!, posTeacher)
                    else
                        textViewTeacher!!.text = "Select a Teacher"
                    // teacherId = response.data.teachers[0].teacherId
                    // getAvailableDate()
                } else
                    textViewTeacher!!.text = "Select a Teacher"
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

    private fun initTeacherSpinner(textView: TextView, position: Int) {
        teachersSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getTeacherNames(itemsSchool)
        teachersSpinner.setPicker(itemsSchool)
        teachersSpinner.setTitle("Teachers")
        teachersSpinner.setCyclic(false)
        teachersSpinner.setSelectOptions(0)
        teachersSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            //teacherName = itemsSchool[options1]

            //teacherId1 = findTeacherId()
            textView.text = itemsSchool[options1]
            lessonDetails?.get(position)?.teacherId = findTeacherId()
            lessonDetails?.get(position)?.teacherName = itemsSchool[options1]
            //getAvailableDate()
            // timeSlot = ""
        }
        // teacherName = itemsSchool[0]
        //  binding.teacherText.text = itemsSchool[0]
        teachersSpinner.show()
    }

    private fun getTeacherNames(itemsSchool: ArrayList<String>) {
        for (item in teacherList) {
            itemsSchool.add(item.teachername)
        }
    }

    private fun findTeacherId(): String {
        for (techer in teacherList) {
            if (techer.teachername.equals(textViewTeacher!!.text.toString(), ignoreCase = true))
                return techer.teacherId
        }
        return (lessonDetails!![0].teacherId)
        // return ""
    }

    private fun getPackageList(location: String) {
        if (Connectivity.isConnected(activity)) {
            makeUpViewModel.getPackage(location)
            makeUpViewModel.packageRequest.observe(this, packageListObserver)
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

    private val packageListObserver: Observer<ApiResponse<List<PackageResponse>>> by lazy {
        Observer<ApiResponse<List<PackageResponse>>> {
            it?.let {
                handlePackageListResponse(it)
            }
        }
    }

    private fun handlePackageListResponse(response: ApiResponse<List<PackageResponse>>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    packageList = response.data
                    if (packageList.isNotEmpty())
                        initPackageSpinner(textViewPackage!!, posDuration)
                    // teacherId = response.data.teachers[0].teacherId
                    // getAvailableDate()
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

    private fun initPackageSpinner(textView: TextView, position: Int) {
        packageSpinner = MyOptionsPickerView(context)
        val items = ArrayList<String>()
        getPackageNames(items)
        packageSpinner.setPicker(items)
        packageSpinner.setTitle("Select a Package")
        packageSpinner.setCyclic(false)
        packageSpinner.setSelectOptions(0)
        packageSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            // teacherName = itemsSchool[options1]

            // timeDuration = findDuration()
            textView.text = items[options1]
            lessonDetails?.get(position)?.duration = findDuration()
            lessonDetails?.get(position)?.packageId = findPackageId()
            // getAvailableDate()
            // timeSlot = ""
        }
        // teacherName = itemsSchool[0]
        // binding.teacherText.text = itemsSchool[0]
        packageSpinner.show()
    }

    private fun getPackageNames(items: ArrayList<String>) {
        for (item in packageList) {
            item.packageName?.let { items.add(it) }
        }
    }

    private fun findDuration(): String {
        for (dur in packageList) {
            if (dur.packageName.equals(textViewPackage!!.text.toString(), ignoreCase = true))
                return dur.duration.toString()
        }
        return (lessonDetails!![0].duration)
        //  return ""
    }

    private fun findPackageId(): String {
        for (packID in packageList) {
            if (packID.packageName.equals(textViewPackage!!.text.toString(), ignoreCase = true))
                return packID.packageId.toString()
        }
        return (lessonDetails!![0].packageId)
    }


    private fun getAvailableTime(
        selectDate: String,
        centerId: String,
        duration: String,
        teacherId: String
    ) {
        if (Connectivity.isConnected(activity)) {
            val request = TeacherDateRequest(
                date = selectDate,
                centerId = centerId,
                duration = duration,
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

    private val availableTimeObserver: Observer<ApiResponse<JsonElement>> by lazy {
        Observer<ApiResponse<JsonElement>> {
            it?.let {
                handleAvailableTimeResponse(it)
            }
        }
    }

    private fun handleAvailableTimeResponse(response: ApiResponse<JsonElement>) {
        timeList.clear()
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null) {
                    for (i in (response.data as JsonArray)) {
                        timeList.add(i.toString())
                    }
                    textViewTimeSlot!!.text = "Select a slot"
                    if (timeList.size > 0)
                    //initTimeSpinner(textViewTimeSlot!!, posTime)
                    else
                        textViewTimeSlot!!.text = "No Slot Available"
                } else
                    textViewTimeSlot!!.text = "No Slot Available"
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

    private fun initTimeSpinner(textView: TextView, position: Int) {
        timeSpinner = MyOptionsPickerView(context)
        val items = ArrayList<String>()
        getSlots(items)
        timeSpinner.setPicker(items)
        timeSpinner.setTitle("Times")
        timeSpinner.setCyclic(false)
        timeSpinner.setSelectOptions(0)
        timeSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
//            timeSlot = options1.toString()
            //  timeSlot = DateTimeUtil.studentTimeReverse(itemsSchool[options1].replace("\"", ""))
            textView.text = items[options1].replace("\"", "")
            lessonDetails!![position].time = items[options1].replace("\"", "")
        }
        timeSpinner.show()
    }

    private fun getSlots(itemsSchool: ArrayList<String>) {
        for (item in timeList) {
            itemsSchool.add(item)
        }
    }

    fun createMakeUPLesson() {

    }

    private fun getTotalTime(tempList: List<Enrollment>): Int {

        var count: Int = 0
        for (temp in tempList) {
            if (temp.duration != null) {
                if (temp.duration.contains("min")) {
                    val strs = temp.duration.split("min").toTypedArray()
                    val parsedInt: Int? = strs[0].trim().toIntOrNull()
                    // val parsedInt: Int? = temp.duration.toInt()
                    if (parsedInt != null)
                        count += parsedInt
                } else {
                    val parsedInt: Int? = temp.duration.toInt()
                    count += parsedInt!!
                }
            }
        }
        return count
    }


}