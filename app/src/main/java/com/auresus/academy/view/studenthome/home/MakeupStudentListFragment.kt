package com.auresus.academy.view.studenthome.home

import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeupStudentBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.settings.IStudentItemListener
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MakeupStudentListFragment : BaseFragment() {

    var selectedStuent: ArrayList<Student> = ArrayList()
    private var enromentList: List<Enrollment> = ArrayList()
    private var students: List<Student> = ArrayList()
    private lateinit var mStudentAdapter: MakeStudentListAdapter
    private lateinit var binding: FragmentMakeupStudentBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()

    override fun getLayoutId(): Int {
        return R.layout.fragment_makeup_student
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeupStudentBinding
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

    companion object {
        val TAG = MakeupStudentListFragment::class.simpleName
        fun newInstance(): MakeupStudentListFragment {
            return MakeupStudentListFragment()
        }
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }
        binding.nextButton.setOnClickListener {
            selectedStuent = findCheckStudent()
            if (selectedStuent.size > 0)
                naviageToNextScreen()
            else
                Toast.makeText(context, "Please select student", Toast.LENGTH_SHORT).show()
        }
    }

    private fun naviageToNextScreen() {
        (activity as HomeAcitivty).navigateToMakeupBook(selectedStuent)
    }

    private fun findCheckStudent(): ArrayList<Student> {
        var selectedStudent: ArrayList<Student> = ArrayList();
        for (student in students) {
            if (student.isChecked)
                selectedStudent.add(student)
        }
        return selectedStudent
    }

    private fun initRecyclerView() {
        binding.studentListRv.layoutManager = LinearLayoutManager(activity)
        mStudentAdapter = MakeStudentListAdapter(mutableListOf(),
            object : IStudentItemListener {
                override fun itemClick(studentItem: Student) {
                }
            })
        binding.studentListRv.adapter = mStudentAdapter
    }

}