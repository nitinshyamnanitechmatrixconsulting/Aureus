package com.auresus.academy.view.teacherhome

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.auresus.academy.R
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.login.LoginAcitivty
import com.auresus.academy.view.login.ProceedLoginAcitivty
import com.auresus.academy.view_model.BaseViewModel
import com.twilio.video.app.data.Preferences
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_teacher_home.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.progress_loader.view.*
import kotlinx.android.synthetic.main.toolbar_center_title_left_back.*
import kotlinx.android.synthetic.main.toolbar_teacher_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class TeacherHomeActivity : BaseActivity() {


    private var paascode: String? = null
    private val baseViewModel: BaseViewModel by viewModel()
    private var currentDate: Date = Date()
    private lateinit var softInputAssist: SoftInputAssist
    private val loadingObserver: Observer<Boolean> by lazy {
        Observer<Boolean> {
            it?.let { showLoader(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        softInputAssist.onResume()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }

    private fun showLoader(show: Boolean) {
        progressLoader.loader.isVisible = show

    }

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                val intent = Intent(this, TeacherHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_teacher_home
    }

    private fun setBottomNavigation() {
        val bottomNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation
        val item1 = AHBottomNavigationItem(
            R.string.bottom_tab_schedule,
            R.drawable.ic_schedule,
            R.color.design_bottom_navigation_shadow_color
        )
        val item2 = AHBottomNavigationItem(
            R.string.bottom_tab_join_lession,
            R.drawable.ic_mic_24,
            R.color.design_bottom_navigation_shadow_color
        )
        bottomNavigation.addItem(item1)
        bottomNavigation.addItem(item2)
        bottomNavigation.setTitleTextSizeInSp(11.0f, 11.0f)
        bottomNavigation.defaultBackgroundColor = Color.parseColor("#FEFEFE")
        bottomNavigation.isBehaviorTranslationEnabled = false
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#ffbf2f"))
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"))
        navigateToSchedule()
    }

    private fun decrementDate(cDate: Date) {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        c.time = cDate

        c.add(
            Calendar.DATE,
            -1
        ) // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        currentDate = c.time

        val output = sdf.format(c.time)
        date.text = output
        baseViewModel.date.value = currentDate

    }

    private fun incrementDate(cDate: Date) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        c.time = cDate

        c.add(
            Calendar.DATE,
            1
        ) // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        currentDate = c.time
        val output = sdf.format(c.time)
        date.text = output
        baseViewModel.date.value = currentDate
    }

    private fun navigateToJoinLession() {
        toolbarJoinLesson.isVisible = true
        toolbarTeacherHome.isVisible = false
        supportFragmentManager.commit {
            replace(
                R.id.container,
                JoinLessonFragment.newInstance(paascode),
                JoinLessonFragment.TAG
            )
            addToBackStack(null)
        }
    }

    private fun navigateToSchedule() {
        toolbarJoinLesson.isVisible = false
        toolbarTeacherHome.isVisible = true
        supportFragmentManager.commit {
            replace(
                R.id.container,
                TeacherScheduleFragment.newInstance(),
                TeacherScheduleFragment.TAG
            )
        }
    }

    override fun initUI(binding: ViewDataBinding?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        date.setText(sdf.format(currentDate))
        baseViewModel.date.value = currentDate
        arrowLeftButton.setOnClickListener {
            decrementDate(currentDate)
        }
        arrowRightButton.setOnClickListener {
            incrementDate(currentDate)
        }
        backButton.setOnClickListener {
            bottom_navigation.getViewAtPosition(0).performClick()
        }
        getLoading().observe(this, loadingObserver)
        setBottomNavigation()
        initClickListener()
        softInputAssist = SoftInputAssist(this)

        val data: Uri? = intent.data
        if (data != null && data.getPathSegments().size >= 2) {
            paascode = data.getQueryParameter("room_name")
            bottom_navigation.getViewAtPosition(1).performClick()
        }

    }

    private fun initClickListener() {
        logout.setOnClickListener {
            ProceedLoginAcitivty.open(this)
            //preferenceHelper.clearData()
           // LoginAcitivty.open(this);
        }
        bottom_navigation.setOnTabSelectedListener { position, wasSelected ->
            when (position) {
                0 -> navigateToSchedule()
                1 -> navigateToJoinLession()
            }
            true
        }

    }
}