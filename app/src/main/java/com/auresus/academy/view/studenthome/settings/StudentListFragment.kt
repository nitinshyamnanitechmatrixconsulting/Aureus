package com.auresus.academy.view.studenthome.settings

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityNotificationBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class StudentListFragment : BaseFragment() {

    private var enromentList: List<Enrollment> = ArrayList()
    private var students: List<Student> = ArrayList()
    private lateinit var mStudentAdapter: StudentListAdapter
    private lateinit var binding: ActivityNotificationBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as ActivityNotificationBinding
        initClickListener()
        initRecyclerView()
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                val events = it.events
                students = it.students
                enromentList = it.enrolments
                setStudentList()
            }
        }
    }

    private fun setStudentList() {
        if (students.isNotEmpty())
            mStudentAdapter.setList(students)
    }

    private fun filterEnrollmentList(
        enrollments: List<Enrollment>,
        studentId: String
    ): ArrayList<Enrollment> {
        var filteredList: ArrayList<Enrollment> = ArrayList()
        for (enrollemt in enrollments) {
            if (enrollemt.studentId == studentId)
                filteredList.add(enrollemt)
        }
        return filteredList
    }


    companion object {
        val TAG = StudentListFragment::class.simpleName
        fun newInstance(): StudentListFragment {
            return StudentListFragment()
        }
    }

    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(activity)
        mStudentAdapter = StudentListAdapter(mutableListOf(),
            object : IStudentItemListener {
                override fun itemClick(studentItem: Student) {
                    var enrellemtns = filterEnrollmentList(enromentList, studentItem.studentId)
                    (activity as HomeAcitivty).navigateToStudentDetails(studentItem, enrellemtns)
                }
            })
        binding.notificationRV.adapter = mStudentAdapter
    }

}