package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentEnrollmentChangeBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import org.koin.android.ext.android.inject

class EnrollmentChangeFragment : BaseFragment() {
    private var enrollment: Enrollment? = null
    lateinit var binding: FragmentEnrollmentChangeBinding
    val preferenceHelper: PreferenceHelper by inject()

    companion object {
        val TAG = EnrollmentChangeFragment::class.simpleName
        fun newInstance(enrollment: Enrollment): EnrollmentChangeFragment {
            var bundle = Bundle()
            bundle.putSerializable("enrollment", enrollment)
            var frag = EnrollmentChangeFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_enrollment_change
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentEnrollmentChangeBinding
        initClickListener()
        if (arguments != null) {
            enrollment = arguments?.get("enrollment") as Enrollment?
        }
        setData()
    }

    private fun setData() {
        binding.changeEnrollmentMsg.text =
            "You are requesting to change your enrollment for ${preferenceHelper.getUserName()}. Please start by selecting one of the change enrollment types below. "
    }

    private fun initClickListener() {
        binding.nextButton.setOnClickListener {
            var value = ""
            if (binding.updateEnrollmentPackage.isChecked)
                value = binding.updateEnrollmentPackage.text.toString()
            else if (binding.changeEnrollmentPackage.isChecked)
                value = binding.changeEnrollmentPackage.text.toString()
            else if (binding.changeEnrollmentTeacher.isChecked)
                value = binding.changeEnrollmentTeacher.text.toString()
            else if (binding.changeLessonTime.isChecked)
                value = binding.changeLessonTime.text.toString()
            else
                value = binding.otherEnrollmentPackage.text.toString()
            (activity as HomeAcitivty).navigateToEnrollmentRequestChange(enrollment!!, value)
        }
    }


}