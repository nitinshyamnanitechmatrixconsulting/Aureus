package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentEnrollmentDetailsBinding
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import org.koin.android.ext.android.inject

class EnrollmentDetailsFragment : BaseFragment() {
    private var enrollment: Enrollment? = null
    lateinit var binding: FragmentEnrollmentDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()

    companion object {
        val TAG = EnrollmentDetailsFragment::class.simpleName
        fun newInstance(enrollment: Enrollment): EnrollmentDetailsFragment {
            var bundle = Bundle()
            bundle.putSerializable("enrollment", enrollment)
            var frag = EnrollmentDetailsFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_enrollment_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentEnrollmentDetailsBinding
        if (arguments != null)
            enrollment = arguments?.get("enrollment") as Enrollment?
        if (enrollment != null) {
            setData()
        }
        initClickListener()

    }

    private fun initClickListener() {
        binding.editPersonalDetails.setOnClickListener {
            if (enrollment != null)
                (activity as HomeAcitivty).navigateToEnrollmentChange(enrollment!!)
        }

    }

    private fun setData() {
        binding.studentName.text = enrollment?.studentName
        binding.lessonDate.text = enrollment?.lessonDay
        binding.lessonType.text = enrollment?.lessonType
        binding.lessonTime.text = enrollment?.lessonDay
        binding.teacherName.text = enrollment?.teacherName
        binding.lessonDuration.text = enrollment?.duration
        binding.enrollmentStatus.text = enrollment?.status
        binding.lessonLocation.text = enrollment?.location

        binding.packageInfo.text = enrollment?.packageName
        binding.packagePrice.text = "SGC $${enrollment?.packagePrice}"
        binding.packageBillDate.text = enrollment?.NextBillDate

    }

}