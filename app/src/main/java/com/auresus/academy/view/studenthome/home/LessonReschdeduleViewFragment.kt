package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentLessonRescheduleViewBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.requests.LessonRescheduleRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class LessonReschdeduleViewFragment : BaseFragment() {
    private var lessonDetails: Booking? = null
    private var newlessonDetails: Booking? = null
    lateinit var binding: FragmentLessonRescheduleViewBinding
    val preferenceHelper: PreferenceHelper by inject()
    private lateinit var adapter: StudentLessonDetailPageAdapter
    private var currentDate: Date = Date()
    private val makeUpViewModel: MakeUpViewModel by viewModel()
    private val baseViewModel: BaseViewModel by viewModel()

    companion object {
        val TAG = LessonReschdeduleViewFragment::class.simpleName
        fun newInstance(
            lessonDetails: Booking,
            newLessonDeatils: Booking
        ): LessonReschdeduleViewFragment {
            var bundle = Bundle()
            bundle.putSerializable("lessonDetails", lessonDetails)
            bundle.putSerializable("lessonDetailsNew", newLessonDeatils)
            var frag = LessonReschdeduleViewFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_lesson_reschedule_view
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentLessonRescheduleViewBinding
        if (arguments != null) {
            lessonDetails = arguments?.get("lessonDetails") as Booking?
            newlessonDetails = arguments?.get("lessonDetailsNew") as Booking?
        }
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                findBooking(it, newlessonDetails!!.bookingId)
            }
        }
    }
    private var bookings: MutableList<Booking> = mutableListOf()

    private fun findBooking(loginResponse: StudentLoginResponse, id: String) {
        loginResponse.enrolments.forEach { it ->
            it.bookings.forEach {
                bookings.add(it)
            }
        }
        for (bookingLesson in bookings) {
            if (bookingLesson.bookingId == id)
                lessonDetails = bookingLesson
        }
        setData()
    }


    private fun initClickListener() {
        binding.cancelSession.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.reschdeuleSession.setOnClickListener {
            lessonReschulde()
        }
    }

    private fun lessonReschulde() {
        if (Connectivity.isConnected(activity)) {
            var request = LessonRescheduleRequest(
                date = newlessonDetails!!.date,
                duration = newlessonDetails!!.duration.replace(" min", ""),
                bookingId = newlessonDetails!!.bookingId,
                lessonTypeNew = newlessonDetails!!.lessonTypeNew,
                olddate = lessonDetails!!.date,
                oldteacherId = lessonDetails!!.teacherId,
                oldtime = lessonDetails!!.time,
                teacherId = newlessonDetails!!.teacherId,
                time = newlessonDetails!!.time
            )
            makeUpViewModel.reschdeuleLesson(request)
            makeUpViewModel.lessonReschdule.observe(this, availableTimeObserver)
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

    private val availableTimeObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleAvailableTimeResponse(it)
            }
        }
    }

    private fun handleAvailableTimeResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                (activity as HomeAcitivty).onBackPressed()
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


    private fun setData() {
        binding.userName.text = preferenceHelper.getUserName()
        binding.btnSchedules.text = newlessonDetails?.status
        binding.lessonDate.text = DateTimeUtil.studentDOB(newlessonDetails!!.date)
        binding.lessonType.text = newlessonDetails?.type
        binding.lessonTime.text = DateTimeUtil.studentTime(newlessonDetails!!.time)
        binding.teacherName.text = newlessonDetails?.teacherName
        binding.lessonDuration.text = newlessonDetails?.duration
        binding.lessonLocation.text = newlessonDetails?.lessonTypeNew
        binding.teacherName.text = newlessonDetails?.teacherName


        binding.prevUserName.text = preferenceHelper.getUserName()
        binding.prevbtnSchedules.text = lessonDetails?.status
        binding.prevlessonDate.text = DateTimeUtil.studentDOB(lessonDetails!!.date)
        binding.prevlessonType.text = lessonDetails?.type
        binding.prevlessonTime.text = DateTimeUtil.studentTime(lessonDetails!!.time)
        binding.prevteacherName.text = lessonDetails?.teacherName
        binding.prevlessonDuration.text = lessonDetails?.duration
        binding.prevlessonLocation.text = lessonDetails?.lessonTypeNew
        binding.prevteacherName.text = lessonDetails?.teacherName
    }

}