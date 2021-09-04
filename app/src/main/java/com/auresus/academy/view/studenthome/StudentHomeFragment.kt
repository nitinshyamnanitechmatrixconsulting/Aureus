package com.auresus.academy.view.studenthome

import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentHomeBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Event
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.utils.DateTimeUtil.isUpcomingBooking
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.home.EnrollmentAdapter
import com.auresus.academy.view.studenthome.home.IEnrollmentItemListener
import com.auresus.academy.view.studenthome.home.ILessonItemListener
import com.auresus.academy.view.studenthome.home.UpComingLessonAdapter
import com.auresus.academy.view_model.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class StudentHomeFragment : BaseFragment() {
    private var students: List<Student> = ArrayList()
    private lateinit var mEnrollmentAdapter: EnrollmentAdapter
    private lateinit var mAdapter: UpComingLessonAdapter
    private lateinit var homeBinding: FragmentHomeBinding
    private val baseViewModel: BaseViewModel by viewModel()
    private lateinit var events: List<Event>
    private var bookings: MutableList<Booking> = mutableListOf()
    private var enrollments: MutableList<Enrollment> = mutableListOf()
    private lateinit var mBannerView: BannerViewPager<Event>


    companion object {
        val TAG = "HomeFragment"
        fun newInstance(): StudentHomeFragment {
            val homeFragment = StudentHomeFragment()
            return homeFragment
        }
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                val events = it.events
                setUpComingLessons(it)
                setupEnrollmentList(it)
                students = it.students
                events?.let {
                    mBannerView.refreshData(it)
                }
            }
        }
    }

    private fun setupEnrollmentList(loginResponse: StudentLoginResponse) {
        loginResponse.enrolments?.forEach {

        }
        mAdapter.setLessons(bookings)
    }

    private fun setUpComingLessons(loginResponse: StudentLoginResponse) {
        loginResponse.enrolments?.forEach {
            enrollments.add(it)
            it.bookings?.forEach {
                if (isUpcomingBooking(it)) {
                    bookings.add(it)
                }
            }
        }
        mAdapter.setLessons(bookings)
        mEnrollmentAdapter.setEnrollments(enrollments)

    }

    private fun setupViewPager() {
        activity?.let {
            mBannerView.apply {
                adapter = SimpleAdapter(requireActivity())
                setLifecycleRegistry(lifecycle)
                setIndicatorVisibility(View.GONE)
            }.create()
        }

    }

    private fun initUpComingLessonRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        mAdapter = UpComingLessonAdapter(mutableListOf(), object : ILessonItemListener {


            override fun onItemClick(enrollment: Booking) {

                (activity as HomeAcitivty).navigateToLessonDetails(enrollment)
            }
        })
        recycler_view.adapter = mAdapter
    }

    private fun initEnrollmentRecyclerView() {
        enrollmentList.layoutManager = LinearLayoutManager(activity)
        mEnrollmentAdapter = EnrollmentAdapter(mutableListOf(), object : IEnrollmentItemListener {
            override fun onItemClick(enrollment: Enrollment) {
                val dialog = BottomSheetDialog(activity!!)
                dialog.setContentView(R.layout.bottom_sheet_enrollemnt);
                dialog.setCanceledOnTouchOutside(false)
                val enrollmentDetails = dialog.findViewById<TextView>(R.id.enrllmentDetails)
                val studentDetails = dialog.findViewById<TextView>(R.id.studentDetails)
                val viewLesson = dialog.findViewById<TextView>(R.id.viewLesson)
                enrollmentDetails?.setOnClickListener {
                    dialog.cancel()
                    (activity as HomeAcitivty).navigateToEnrollmentDetails(enrollment)
                }
                studentDetails?.setOnClickListener {
                    dialog.cancel()
                    var student = findStudent(enrollment)
                    if (student != null) {
                        var enrollmentList = filterEnrollmentList(enrollments, student.studentId)
                        (activity as HomeAcitivty).navigateToStudentDetails(student, enrollmentList)

                    }
                }
                viewLesson?.setOnClickListener {
                    dialog.cancel()
                    (activity as HomeAcitivty).navigateToSchedule()
                }
                dialog.show()
            }
        })
        enrollmentList.adapter = mEnrollmentAdapter
    }


    private fun filterEnrollmentList(
        enrollments: MutableList<Enrollment>,
        studentId: String
    ): ArrayList<Enrollment> {
        var filteredList: ArrayList<Enrollment> = ArrayList()
        for (enrollemt in enrollments) {
            if (enrollemt.studentId == studentId)
                filteredList.add(enrollemt)
        }
        return filteredList
    }

    private fun findStudent(enrollment: Enrollment): Student? {
        if (students.isNotEmpty()) {
            for (student in students) {
                if (student.studentId == enrollment.studentId)
                    return student
            }
        }
        return null
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        homeBinding = binding as FragmentHomeBinding
        mBannerView = view.findViewById(R.id.banner_view)
        setupViewPager()
        initUpComingLessonRecyclerView()
        initEnrollmentRecyclerView()
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
    }

    override fun onPause() {
        if (banner_view != null) banner_view.stopLoop()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (banner_view != null) banner_view.startLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (banner_view != null) banner_view.stopLoop()
    }

}