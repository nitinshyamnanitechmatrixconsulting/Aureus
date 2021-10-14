package com.auresus.academy.view.teacherhome

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.model.bean.responses.GetAccessTokenResponse
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view_model.BaseViewModel
import com.auresus.academy.view_model.DashboardViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_lesson_details.*
import kotlinx.android.synthetic.main.fragment_online_lesson_details.*
import kotlinx.android.synthetic.main.progress_loader.view.*
import kotlinx.android.synthetic.main.toolbar_center_title_left_back.*
import kotlinx.android.synthetic.main.toolbar_teacher_home.*
import kotlinx.android.synthetic.main.toolbar_teacher_home.arrowLeftButton
import kotlinx.android.synthetic.main.toolbar_teacher_home.toolbar_title
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class LessonDetailsActivity : BaseActivity() {


    private lateinit var adapter: LessonDetailPageAdapter
    private val homeViewModel: DashboardViewModel by viewModel()
    private val baseViewModel: BaseViewModel by viewModel()
    private var currentDate: Date = Date()
    private lateinit var booking: TeacherBooking
    private var isOnline:Boolean = false


    private val loadingObserver: Observer<Boolean> by lazy {
        Observer<Boolean> {
            it?.let { showLoader(it) }
        }
    }

    private val accessTokenResponseObserver: Observer<ApiResponse<GetAccessTokenResponse>> by lazy {
        Observer<ApiResponse<GetAccessTokenResponse>> {


        }
    }

    private fun handleAccessTokenResponse(response: ApiResponse<GetAccessTokenResponse>) {
        when (response.status) {
            ApiResponse.Status.LOADING -> {

            }
            ApiResponse.Status.SUCCESS -> {


            }
            ApiResponse.Status.ERROR -> {
                if (response.error?.code == 500)
                    Toast.makeText(this, response.error?.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(
                        this,
                        getString(R.string.internal_server_error),
                        Toast.LENGTH_LONG
                    ).show()

            }
        }
    }

    private fun showLoader(show: Boolean) {
        progressLoader.loader.isVisible = show
    }

    companion object {
        const val INTENT_EXTRA_BOOKING: String = "INTENT_EXTRA_BOOKING"
        fun open(booking: TeacherBooking, currActivity: BaseActivity) {
            currActivity.run {
                startActivity(Intent(this, LessonDetailsActivity::class.java).apply {
                    putExtra(INTENT_EXTRA_BOOKING, booking)
                })
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_lesson_details
    }

    override fun initUI(binding: ViewDataBinding?) {
        booking = intent.getParcelableExtra<TeacherBooking>(INTENT_EXTRA_BOOKING)!!
        toolbar_title.text = getString(R.string.lesson_details)
        backButton.setOnClickListener { finish() }
        initTabViewPager()
        setData(booking)
        initClickListener()
        setObserver()

    }

    private fun setObserver() {
        homeViewModel.accessTokenResponse.observe(this, accessTokenResponseObserver)
    }

    private fun initTabViewPager() {
        booking?.let {
//            val bookingdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'").parse(it.Booking_Date__c + " " + it.Start_Time__c)
            isOnline = it.Lesson_Type__c == "Online"
            if (isOnline) {
                inCenterLayoutContainer.visibility = View.GONE
                onlineLayoutContainer.visibility = View.VISIBLE
                val bookingdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(it.Booking_Date__c+" "+it.Start_Time__c)
                val isBookingInPast = bookingdate.before(currentDate)
                val firstTabTitle =
                    if (isBookingInPast) getString(R.string.lesson_history) else getString(R.string.online_lesson_details)
                val secondTabTitle = getString(R.string.lesson_files)
                tabLayout.addTab(tabLayout.newTab().setText(firstTabTitle))
                tabLayout.addTab(tabLayout.newTab().setText(secondTabTitle))
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                adapter = LessonDetailPageAdapter(this, supportFragmentManager, tabLayout.tabCount)
                viewPager.adapter = adapter
                viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewPager.currentItem = tab.position
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
                adapter.setTeacherBooking(it)

            }
            else{
                inCenterLayoutContainer.visibility = View.VISIBLE
                onlineLayoutContainer.visibility = View.GONE
                incenterTitle.setText(getString(R.string.lesson_files_incenter))
                supportFragmentManager.commit {
                    replace(
                        R.id.fragmentContainer,
                        LessonAddFileFragment.newInstance(it),
                        LessonAddFileFragment.TAG
                    )
                }
            }

        }

    }

    private fun setData(booking: TeacherBooking?) {
        booking?.let {
            tvDate.text =
                SimpleDateFormat("dd MMM").format(SimpleDateFormat("yyyy-MM-dd").parse(it.Booking_Date__c))
            tvStatus.text = it.Status__c
            tvTeacherName.text = it.Teacher_Account__r?.Name
            tvLessonType.text = it.Instrument__c
            tvLocation.text = it.Lesson_Type__c
            tvDuration.text = it.Duration__c.toString() + " " + "Min"
            tvTime.text =
                DateTimeUtil.timeFormatter.format(DateTimeUtil.bookingTimeParser.parse(it.Start_Time__c))
            tvStudentName.text = it.student_Name__c
            if (it.Lesson_Type__c == "Online")
               ivLocationIcon.setImageResource(R.drawable.globe_icon)
            else
                ivLocationIcon.setImageResource(R.drawable.ic_baseline_location)
        }
    }

    private fun initClickListener() {

    }
}