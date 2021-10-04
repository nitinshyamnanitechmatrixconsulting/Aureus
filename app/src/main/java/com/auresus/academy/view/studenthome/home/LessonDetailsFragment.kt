package com.auresus.academy.view.studenthome.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentLessonDetailsBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.requests.LessonConvertRequest
import com.auresus.academy.model.bean.responses.InvoiceListResponse
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_lesson_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class LessonDetailsFragment : BaseFragment() {
    private var lessonDetails: Booking? = null
    lateinit var binding: FragmentLessonDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var adapter: StudentLessonDetailPageAdapter
    private var currentDate: Date = Date()

    companion object {
        val TAG = LessonDetailsFragment::class.simpleName
        fun newInstance(lessonDetails: Booking): LessonDetailsFragment {
            var bundle = Bundle()
            bundle.putSerializable("lessonDetails", lessonDetails)
            var frag = LessonDetailsFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_lesson_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentLessonDetailsBinding
        if (arguments != null)
            lessonDetails = arguments?.get("lessonDetails") as Booking?
        if (lessonDetails != null) {
            setData()
            hitLessonDetailsApi()
            initTabViewPager()
        }
        initClickListener()
    }


    private fun initClickListener() {
        binding.convetOnline.setOnClickListener {
            if (lessonDetails?.lessonTypeNew == "Online")
                hitLessonOfflineApi()
            else
                hitOnlineApi()
        }

        binding.contactUsBtn.setOnClickListener {
            (activity as HomeAcitivty).navigateToContactUs()
        }

        binding.reschdeuleSession.setOnClickListener {
            (activity as HomeAcitivty).navigateToLessonReschedule(lessonDetails!!)
        }
    }

    private fun hitOnlineApi() {
        if (Connectivity.isConnected(activity)) {
            var request = LessonConvertRequest(lessonDetails!!.bookingId)
            homeViewModel.lessonConvertOnline(request)
            homeViewModel.lessonConvertOnlineRequest.observe(this, lessonConvertObserver)
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

    private fun hitLessonOfflineApi() {
        if (Connectivity.isConnected(activity)) {
            var request = LessonConvertRequest(lessonDetails!!.bookingId)
            homeViewModel.lessonConvertOffline(request)
            homeViewModel.lessonConvertOfflineRequest.observe(this, lessonConvertObserver)
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

    private fun setData() {
        binding.userName.text = preferenceHelper.getUserName()
        binding.btnSchedules.text = lessonDetails?.status
        binding.lessonDate.text = DateTimeUtil.studentDOB(lessonDetails!!.date)
        binding.lessonType.text = lessonDetails?.type
        binding.lessonTime.text = DateTimeUtil.studentTime(lessonDetails!!.time)
        binding.teacherName.text = lessonDetails?.teacherName
        binding.lessonDuration.text = lessonDetails?.duration
        binding.lessonLocation.text = lessonDetails?.lessonTypeNew
        if (lessonDetails!!.lessonTypeNew == "Online")
            binding.ivLocationIcon.setImageResource(R.drawable.globe_icon)
        else
            binding.ivLocationIcon.setImageResource(R.drawable.ic_baseline_location)

        if (lessonDetails!!.lessonTypeNew == "Online")
            binding.convetOnline.text = "Convert To In Center"
        else
            binding.convetOnline.text = "Convert To Online"
    }

    private fun hitLessonDetailsApi() {
        if (Connectivity.isConnected(activity)) {
            homeViewModel.lessonDetails(lessonDetails!!.bookingId)
            homeViewModel.lessonDetailsRequest.observe(this, lessonDeatilsObvserver)
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

    private val lessonDeatilsObvserver: Observer<ApiResponse<InvoiceListResponse>> by lazy {
        Observer<ApiResponse<InvoiceListResponse>> {
            it?.let {
                handleLessonDeatilsResponse(it)
            }
        }
    }

    private val lessonConvertObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleLessonConvertResponse(it)
            }
        }
    }

    private fun handleLessonDeatilsResponse(response: ApiResponse<InvoiceListResponse>) {
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

    private fun handleLessonConvertResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (lessonDetails?.lessonTypeNew == "Online")
                    lessonDetails?.lessonTypeNew = "In center"
                else
                    lessonDetails?.lessonTypeNew = "Online"
                (activity as HomeAcitivty).navigateToLessonDetails(lessonDetails!!);
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

    private fun initTabViewPager() {
        lessonDetails?.let {
            val bookingdate = SimpleDateFormat("yyyy-MM-dd").parse(it.date)
            val isBookingInPast = bookingdate.before(currentDate)
            val firstTabTitle =
                if (isBookingInPast) getString(R.string.lesson_history) else getString(R.string.online_lesson_details)
            val secondTabTitle = getString(R.string.lesson_files)
            if (lessonDetails?.lessonTypeNew == "Online")
                tabLayout.addTab(tabLayout.newTab().setText(firstTabTitle))
            tabLayout.addTab(tabLayout.newTab().setText(secondTabTitle))
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL;
            adapter =
                StudentLessonDetailPageAdapter(
                    activity,
                    childFragmentManager,
                    tabLayout.tabCount
                )
            adapter.setTeacherBooking(lessonDetails)
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

    }

}