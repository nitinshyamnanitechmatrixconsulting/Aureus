package com.auresus.academy.view.teacherhome

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentTeacherHomeBinding
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.model.bean.responses.TeacherLoginResponse
import com.auresus.academy.view.OnItemClickListener
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import kotlinx.android.synthetic.main.fragment_home.recycler_view
import kotlinx.android.synthetic.main.fragment_teacher_home.*


class TeacherScheduleFragment : BaseFragment(), OnItemClickListener<TeacherBooking> {
    private lateinit var mAdapter: UpComingTeacherBookingAdapter
    private lateinit var homeBinding: FragmentTeacherHomeBinding
    private val baseViewModel: BaseViewModel by activityViewModels()
    private var bookings: MutableList<TeacherBooking> = mutableListOf()

    companion object {
        val TAG = "TeacherScheduleFragment"
        val MEETING_CODE = "MEETING_CODE"
        fun newInstance(): TeacherScheduleFragment {
            val homeFragment = TeacherScheduleFragment()
            return homeFragment
        }
    }

    private fun setUpComingLessons(bookingList: List<TeacherBooking>) {
        this.bookings = bookingList.toMutableList()
        if(bookings.isNullOrEmpty()){
            tvNoData.isVisible = true
            recycler_view.isVisible = true
        }
        else{
            tvNoData.isVisible = false
            recycler_view.isVisible = true
        }
        mAdapter.setLessons(bookingList.toMutableList())
    }

    private fun initUpComingLessonRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        mAdapter = UpComingTeacherBookingAdapter(mutableListOf())
        mAdapter.clickListener = this
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(SpaceItemDecoration(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5f,
            resources.displayMetrics
        ).toInt()))
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_teacher_home
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        homeBinding = binding as FragmentTeacherHomeBinding
        initUpComingLessonRecyclerView()
        baseViewModel.getTeacherHomeLiveData()
            .observe(this, object : Observer<TeacherLoginResponse> {
                override fun onChanged(it: TeacherLoginResponse?) {
                    it?.let {

                    }
                }
            })
        baseViewModel.getDateWiseBookingListLiveData()
            .observe(this, object : Observer<List<TeacherBooking>?> {
                override fun onChanged(it: List<TeacherBooking>?) {
                    it?.let {
                        setUpComingLessons(it)
                    } ?: kotlin.run {
                        // No bookings
                    }
                }
            })
    }

    override fun onItemOnClick(booking: TeacherBooking) {
        LessonDetailsActivity.open(booking,activity as BaseActivity)
    }


}