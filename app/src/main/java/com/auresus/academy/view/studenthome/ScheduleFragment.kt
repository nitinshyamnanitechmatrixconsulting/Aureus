package com.auresus.academy.view.studenthome

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentScheduleBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.home.ILessonItemListener
import com.auresus.academy.view.studenthome.home.ScheduleLessonAdapter
import com.auresus.academy.view_model.BaseViewModel
import com.bigkoo.pickerview.MyOptionsPickerView
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class ScheduleFragment : BaseFragment() {

    private lateinit var startTimePicker: SingleDateAndTimePickerDialog.Builder
    private lateinit var endTimePicker: SingleDateAndTimePickerDialog.Builder
    private var students: List<Student> = ArrayList()
    private lateinit var statusSpinner: MyOptionsPickerView<String>
    private lateinit var typeSpinner: MyOptionsPickerView<String>
    private lateinit var studentSpinner: MyOptionsPickerView<String>
    private lateinit var mAdapter: ScheduleLessonAdapter
    lateinit var binding: FragmentScheduleBinding
    private val baseViewModel: BaseViewModel by viewModel()
    private var enrollments: MutableList<Enrollment> = mutableListOf()
    private var bookings: MutableList<Booking> = mutableListOf()
    private var filterStatus = ""
    private var filterType = ""
    private var studentName = ""
    private var startDate: Long = Date().time
    private var endDate: Long = Date().time

    companion object {
        val TAG = ScheduleFragment::class.java.simpleName
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_schedule
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentScheduleBinding
        initUpComingLessonRecyclerView()
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
    }

    private fun initClickListener() {
        binding.startTimeContainer.setOnClickListener {
            startTimePicker.display()
        }
        binding.endTimeContainer.setOnClickListener {
            endTimePicker.display()
        }
        binding.textAllStatus.setOnClickListener {
            statusSpinner.show()
        }
        binding.textAllTyes.setOnClickListener {
            typeSpinner.show()
        }
        binding.textAllStudent.setOnClickListener {
            studentSpinner.show()
        }
    }

    private fun initStartDatePicker() {
        startTimePicker = SingleDateAndTimePickerDialog.Builder(activity)
            .bottomSheet()
            .curved()
            .displayMinutes(false)
            .displayHours(false)
            .displayDays(false)
            .displayMonth(true)
            .displayYears(true)
            .displayDaysOfMonth(true)
            .listener { date: Date? ->
                startDate = date!!.time
                binding.startDate.text = DateTimeUtil.schdeuleDateTimeFormatter.format(date)
            }
    }

    private fun initEndDatePicker() {
        endTimePicker = SingleDateAndTimePickerDialog.Builder(activity)
            .bottomSheet()
            .curved()
            .displayMinutes(false)
            .displayHours(false)
            .displayDays(false)
            .displayMonth(true)
            .displayYears(true)
            .displayDaysOfMonth(true)
            .listener { date: Date? ->
                endDate = date!!.time
                binding.endDate.text = DateTimeUtil.schdeuleDateTimeFormatter.format(date)
            }
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                val events = it.events
                students = it.students
                initData()
                setUpComingLessons(it)
            }
        }
    }

    private fun initData() {
        startDate = Date().time
        val c = Calendar.getInstance()
        c.time = Date() // Using today's date
        c.add(Calendar.DATE, c.getActualMaximum(Calendar.DATE))
        endDate = c.timeInMillis

        initStartDatePicker()
        initEndDatePicker()
        initStatusSpinner()
        initStudentSpinner()
        initTypeSpinner()

        binding.startDate.text = DateTimeUtil.timeStampToTime(startDate)
        binding.endDate.text = DateTimeUtil.timeStampToTime(endDate)
    }

    private fun initUpComingLessonRecyclerView() {
        binding.scheduleRv.layoutManager = LinearLayoutManager(activity)
        mAdapter = ScheduleLessonAdapter(mutableListOf(), object : ILessonItemListener {
            override fun onItemClick(enrollment: Booking) {
                (activity as HomeAcitivty).navigateToLessonDetails(enrollment)
            }
        })
        binding.scheduleRv.adapter = mAdapter
    }

    private fun setUpComingLessons(loginResponse: StudentLoginResponse) {
        loginResponse.enrolments.forEach { it ->
            enrollments.add(it)
            it.bookings.forEach {
                bookings.add(it)
            }
        }
        filterList()
    }

    private fun initStatusSpinner() {
        statusSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getStatusList(itemsSchool)
        statusSpinner.setPicker(itemsSchool)
        statusSpinner.setTitle("Status")
        statusSpinner.setCyclic(false)
        statusSpinner.setSelectOptions(0)
        statusSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            filterStatus = itemsSchool[options1]
            binding.textAllStatus.text = itemsSchool[options1]
            filterList()
        }
    }

    private fun getStatusList(itemsSchool: ArrayList<String>) {
        val schools = resources.getStringArray(R.array.statusList)
        for (arr2 in schools) {
            itemsSchool.add(arr2.toString())
        }
    }

    private fun initTypeSpinner() {
        typeSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getTypesList(itemsSchool)
        typeSpinner.setPicker(itemsSchool)
        typeSpinner.setTitle("Type")
        typeSpinner.setCyclic(false)
        typeSpinner.setSelectOptions(0)
        typeSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            filterType = itemsSchool[options1]
            binding.textAllTyes.text = itemsSchool[options1]
            filterList()
        }
    }

    private fun filterList() {
        var filterBookings: ArrayList<Booking> = ArrayList()
        var filterBookingsStatus: ArrayList<Booking> = ArrayList()
        var filterBookingsType: ArrayList<Booking> = ArrayList()

        filterBookings = filterTime(bookings)
        filterBookingsStatus = filterStatusList(filterBookings)
        filterBookingsType = filterTypeList(filterBookingsStatus)

        if (filterBookingsType.size > 0)
            binding.scheduleRv.visibility = View.VISIBLE
        else
            binding.scheduleRv.visibility = View.GONE
        mAdapter.setLessons(filterBookingsType)
    }

    private fun filterStatusList(filterBookings: ArrayList<Booking>): ArrayList<Booking> {
        if (filterStatus.equals("All Status", true) || filterStatus == "")
            return filterBookings
        var filterStatusList: ArrayList<Booking> = ArrayList()
        // filter time
        for (lesson in bookings) {
            if (lesson.status == filterStatus)
                filterStatusList.add(lesson)
        }
        return filterStatusList
    }

    private fun filterTypeList(filterBookings: ArrayList<Booking>): ArrayList<Booking> {
        if (filterType.equals("All Types", true) || filterType == "")
            return filterBookings
        var filterStatusList: ArrayList<Booking> = ArrayList()
        // filter time
        for (lesson in bookings) {
            if (lesson.status == filterType)
                filterStatusList.add(lesson)
        }
        return filterStatusList
    }

    private fun filterTime(bookings: MutableList<Booking>): ArrayList<Booking> {
        var filterBookings: ArrayList<Booking> = ArrayList()
        // filter time
        for (lesson in bookings) {
            var lessonDate = DateTimeUtil.dateToTimeStamp(lesson.date)
            if (isWithinRange(Date(lessonDate)))
                filterBookings.add(lesson)
        }
        return filterBookings
    }

    private fun isWithinRange(testDate: Date): Boolean {
        return !(testDate.before(Date(startDate)) || testDate.after(Date(endDate)))
    }


    private fun getTypesList(itemsSchool: ArrayList<String>) {
        val schools = resources.getStringArray(R.array.typeList)
        for (arr2 in schools) {
            itemsSchool.add(arr2.toString())
        }
    }

    private fun initStudentSpinner() {
        studentSpinner = MyOptionsPickerView(context)
        val itemsSchool = ArrayList<String>()
        getStudentList()
        studentSpinner.setPicker(itemsSchool)
        studentSpinner.setTitle("Type")
        studentSpinner.setCyclic(false)
        studentSpinner.setSelectOptions(0)
        studentSpinner.setOnoptionsSelectListener { options1, option2, options3 ->
            studentName = itemsSchool[options1]
            binding.textAllStudent.text = itemsSchool[options1]
            filterList()
        }
    }

    private fun getStudentList(): ArrayList<Student> {
        if (students.isEmpty())
            return students as ArrayList<Student>
        return ArrayList()
    }
}