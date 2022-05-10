package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentMakeupStudentBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.makeup.MakeUpStudentItemListener
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MakeupStudentListFragment : BaseFragment() {

    var selectedStuent: ArrayList<Enrollment> = ArrayList()
    private var enromentList: List<Enrollment> = ArrayList()

    //  private var students: List<Student> = ArrayList()
    private lateinit var mStudentAdapter: MakeStudentListAdapter
    private lateinit var binding: FragmentMakeupStudentBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    var sameTimeOrAnyTime: String = ""
    private var totalMin: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_makeup_student
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentMakeupStudentBinding
        if (arguments != null)
            totalMin = arguments?.getString("totalMin")!!
        initClickListener()
        initRecyclerView()
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                val events = it.events
                // students = it.students
                enromentList = it.enrolments
                setStudentList()
            }
        }
    }

    private fun setStudentList() {
        if (enromentList.isNotEmpty())
            mStudentAdapter.setList(enromentList)
    }

    companion object {
        val TAG = MakeupStudentListFragment::class.simpleName
        fun newInstance(totalMin: String): MakeupStudentListFragment {
            var bundle = Bundle()
            bundle.putString("totalMin", totalMin)
            var frag = MakeupStudentListFragment()
            frag.arguments = bundle
            return frag
            //return MakeupStudentListFragment()
        }
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            (activity as HomeAcitivty).onBackPressed()
        }
        binding.nextButton.setOnClickListener {
            // get selected radio button from radioGroup
            // get selected radio button from radioGroup
            val selectedId: Int = binding.rg.checkedRadioButtonId
            if (selectedStuent.size == 0) {
                Toast.makeText(context, "Please select student", Toast.LENGTH_SHORT).show()
            } else if (selectedStuent.size > 1 && !binding.rbAnyTime.isChecked && !binding.rbSameTime.isChecked) {
                Toast.makeText(context, "Please select booking preference", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (binding.rbSameTime.isChecked)
                    sameTimeOrAnyTime = "sametime"
                else if (binding.rbAnyTime.isChecked)
                    sameTimeOrAnyTime = "anytime"
                naviageToNextScreen(sameTimeOrAnyTime)
            }
            /*   if (selectedStuent.size > 0)
                   naviageToNextScreen()
               else
                   Toast.makeText(context, "Please select student", Toast.LENGTH_SHORT).show()*/
        }


    }

    private fun naviageToNextScreen(string: String) {
        (activity as HomeAcitivty).navigateToMakeupBook(selectedStuent, string,totalMin)
    }

    private fun findCheckStudent(): ArrayList<Enrollment> {
        var selectedStudent: ArrayList<Enrollment> = ArrayList()
        for (student in enromentList) {
            if (student.isChecked)
                selectedStudent.add(student)
            else
                selectedStudent.remove(student)
        }
        return selectedStudent
    }

    private fun initRecyclerView() {
        binding.studentListRv.layoutManager = LinearLayoutManager(activity)
        mStudentAdapter = MakeStudentListAdapter(mutableListOf(),
            object : MakeUpStudentItemListener {
                override fun itemClick(studentItem: Enrollment) {
                    selectedStuent = findCheckStudent()
                    if (selectedStuent.size > 1)
                        binding.llBookingPreference.visibility = View.VISIBLE
                    else
                        binding.llBookingPreference.visibility = View.GONE

                }
            })
        binding.studentListRv.adapter = mStudentAdapter
    }

}