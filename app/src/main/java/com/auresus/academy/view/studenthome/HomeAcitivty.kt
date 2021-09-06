package com.auresus.academy.view.studenthome

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityHomeBinding
import com.auresus.academy.model.bean.Booking
import com.auresus.academy.model.bean.Enrollment
import com.auresus.academy.model.bean.Student
import com.auresus.academy.model.bean.requests.NotificationUpdateRequest
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.NotificationListResponse
import com.auresus.academy.model.bean.responses.TicketList
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.utils.DialogUtils
import com.auresus.academy.utils.OkDialogClickListener
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.invoice.InvoiceFragmnet
import com.auresus.academy.view.login.ProceedLoginAcitivty
import com.auresus.academy.view.notification.NotificationFragment
import com.auresus.academy.view.notification.NotificationViewModel
import com.auresus.academy.view.studenthome.home.*
import com.auresus.academy.view.studenthome.settings.*
import com.auresus.academy.view.studenthome.ticket.TicketCreateFragment
import com.auresus.academy.view.studenthome.ticket.TicketDetailsFragment
import com.auresus.academy.view.studenthome.ticket.TicketFragment
import com.auresus.academy.view_model.DashboardViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.progressLoader
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.progress_loader.view.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeAcitivty : BaseActivity() {


    var choosePhotoHelper: ChoosePhotoHelper? = null
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: DashboardViewModel by viewModel()
    private val notificationViewModel: NotificationViewModel by viewModel()
    private val notificationList = ArrayList<String>();
    private val loadingObserver: Observer<Boolean> by lazy {
        Observer<Boolean> {
            it?.let { showLoader(it) }
        }
    }

    private fun showLoader(show: Boolean) {
        progressLoader.loader.isVisible = show

    }

//    private val bookingListObserver: Observer<ApiResponse<BookingListResponse>> by lazy {
//        Observer<ApiResponse<BookingListResponse>> {
//            handleBookingListResponse(it)
//        }
//    }
//
//
//        private fun handleBookingListResponse(response: ApiResponse<BookingListResponse>) {
//            when (response.status) {
//                ApiResponse.Status.LOADING -> {
//
//                }
//                ApiResponse.Status.SUCCESS -> {
//                    setBookingList(response.data)
//                }
//                ApiResponse.Status.ERROR -> {
//                    if (response.error?.code == 500)
//                        Toast.makeText(this, response.error?.message, Toast.LENGTH_LONG).show()
//                    else
//                        Toast.makeText(
//                            this,
//                            getString(R.string.internal_server_error),
//                            Toast.LENGTH_LONG
//                        ).show()
//
//                }
//            }
//        }
//
//    private fun setBookingList(data: BookingListResponse?) {
//      data?.let {
//
//      }
//    }

//    private fun hitBookingListApi() {
//        if (Connectivity.isConnected(this)) {
//            /*** request viewModel to get data ***/
//            homeViewModel.getBookingList(
//                homeViewModel.createBookingRequest(
//                    "",
//                    UPCOMING_LIST,
//                    0,
//                    10
//                )
//            )
//            /*** observe live data of viewModel*/
//            homeViewModel.bookingListResponse.observe(this, bookingListObserver)
//        } else {
//            Toast.makeText(this, resources.getString(R.string.no_network_error), Toast.LENGTH_LONG)
//                .show()
//            finish()
//        }
//    }

    companion object {
        fun open(currActivity: BaseActivity) {
            currActivity.run {
                val intent = Intent(this, HomeAcitivty::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_home
    }

    private fun setBottomNavigation() {
        val bottomNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation
        val item1 = AHBottomNavigationItem(
            R.string.bottom_tab_home,
            R.drawable.ic_home,
            R.color.design_bottom_navigation_shadow_color
        )
        val item2 = AHBottomNavigationItem(
            R.string.bottom_tab_schedule,
            R.drawable.ic_schedule,
            R.color.design_bottom_navigation_shadow_color
        )
        val item3 = AHBottomNavigationItem(
            R.string.bottom_tab_make_ups,
            R.drawable.ic_makeup,
            R.color.design_bottom_navigation_shadow_color
        )
        val item4 = AHBottomNavigationItem(
            R.string.bottom_tab_ticket,
            R.drawable.ic_ticket,
            R.color.design_bottom_navigation_shadow_color
        )
        val item5 = AHBottomNavigationItem(
            R.string.bottom_tab_account,
            R.drawable.ic_account,
            R.color.design_bottom_navigation_shadow_color
        )
        bottomNavigation.addItem(item1)
        bottomNavigation.addItem(item2)
        bottomNavigation.addItem(item3)
        bottomNavigation.addItem(item4)
        bottomNavigation.addItem(item5)
        bottomNavigation.defaultBackgroundColor = Color.parseColor("#FEFEFE")
        bottomNavigation.isBehaviorTranslationEnabled = false
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW;
        // Change colors
        bottomNavigation.accentColor = Color.parseColor("#ffbf2f")
        bottomNavigation.inactiveColor = Color.parseColor("#747474")
        navigateToHome()
    }

    private fun setupHomeNavigation() {
        binding.appbar.toolbar.navMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        binding.navRightView.setBackgroundColor(resources.getColor(R.color.colorAccent));

        binding.navRightView.setNavigationItemSelectedListener {
            val id: Int = it.getItemId()
            when (id) {
                R.id.nav_home -> navigateToHome()
                R.id.nav_account_details -> navigateToAccountDetails()
                R.id.nav_billing_details -> navigateToBillindDetails()
                R.id.nav_invoices -> navigateToInvoices()
                R.id.nav_logout -> handleLogout()
                R.id.nav_makeup -> navigateToMakeUps()
                R.id.nav_notifications -> navigateToNotifications()
                R.id.nav_refer_friend -> navigateToReferFriends()
                R.id.nav_schedule -> navigateToSchedule()
                R.id.nav_ticket -> navigateToTicket()
                else -> return@setNavigationItemSelectedListener true
            }
            binding.drawerLayout.closeDrawer(GravityCompat.END); /*Important Line*/
            return@setNavigationItemSelectedListener true
        }
    }

    fun setToolbarTitleAndIcons(
        toolbarTitle: String,
        showNotification: Boolean,
        notificationCount: Boolean,
        showBackButton: Boolean,
        showMenuButton: Boolean,
        showDeleteButton: Boolean = false
    ) {
        binding.appbar.toolbar.toolbarTitle.text = toolbarTitle
        binding.appbar.toolbar.notification.visibility =
            if (showNotification) View.VISIBLE else View.GONE
        binding.appbar.toolbar.tvNotificationCount.visibility =
            if (notificationCount) View.VISIBLE else View.GONE
        binding.appbar.toolbar.backButton.visibility =
            if (showBackButton) View.VISIBLE else View.GONE
        binding.appbar.toolbar.navMenu.visibility =
            if (showMenuButton) View.VISIBLE else View.GONE
        binding.appbar.toolbar.navDelete.visibility =
            if (showDeleteButton) View.VISIBLE else View.GONE
    }

    private fun navigateToTicket() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Tickets",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, TicketFragment.newInstance(), TicketFragment.TAG)
            //addToBackStack(null)
        }
    }

    fun navigateToCreateNewTicket() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Tickets #",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(R.id.container, TicketCreateFragment.newInstance(), TicketCreateFragment.TAG)
            addToBackStack(null)
        }
    }

    fun navigateToSchedule() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Schedule",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, ScheduleFragment.newInstance(), ScheduleFragment.TAG)
            //addToBackStack(ScheduleFragment.TAG)
        }
    }

    fun navigateToContactUs() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Contact Us",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(R.id.container, ContactUsFragment.newInstance(), ContactUsFragment.TAG)
            //addToBackStack(null)
        }
    }

    fun navigateToEnrollmentDetails(enrollment: Enrollment) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Enrollment Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                EnrollmentDetailsFragment.newInstance(enrollment),
                EnrollmentDetailsFragment.TAG
            )
            //addToBackStack(null)
        }
    }

    fun navigateToEnrollmentChange(enrollment: Enrollment) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Change of Enrollment",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                EnrollmentChangeFragment.newInstance(enrollment),
                EnrollmentChangeFragment.TAG
            )
            addToBackStack(null)
        }
    }

    fun navigateToEnrollmentRequestChange(enrollment: Enrollment, value: String) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Change of Enrollment",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                EnrollmentChangeReuqestFragment.newInstance(enrollment, value),
                EnrollmentChangeReuqestFragment.TAG
            )
            addToBackStack(null)
        }
    }

    fun navigateToLessonDetails(enrollment: Booking) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Lesson Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                LessonDetailsFragment.newInstance(enrollment),
                LessonDetailsFragment.TAG
            )
//            addToBackStack(null)
        }
    }

    fun navigateToLessonReschdeduleView(enrollment: Booking, enrollment2: Booking) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Lesson Reschedule",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                LessonReschdeduleViewFragment.newInstance(enrollment, enrollment2),
                LessonReschdeduleViewFragment.TAG
            )
//            addToBackStack(null)
        }
    }

    fun navigateToMakeupBook(enrollment: List<Student>) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Make-up Booking",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                MakeupScheduleFragment.newInstance(enrollment as ArrayList<Student>),
                MakeupScheduleFragment.TAG
            )
            addToBackStack(null)
        }
    }

    fun navigateToLessonReschedule(enrollment: Booking) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Lesson Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                LessonReschduleFragment.newInstance(enrollment),
                LessonReschduleFragment.TAG
            )
            addToBackStack(null)
        }
    }

    fun navigateToTicketDetails(ticketItem: TicketList) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Ticker #${ticketItem.caseId}",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                TicketDetailsFragment.newInstance(ticketItem),
                TicketDetailsFragment.TAG
            )
            addToBackStack(null)
        }
    }

    private fun handleReferFriend() {

    }

    private fun navigateToNotifications() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Notifications",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = true,
            showDeleteButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, NotificationFragment.newInstance(), NotificationFragment.TAG)
            //addToBackStack(NotificationFragment.TAG)
        }
    }

    fun navigateToStudentList() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Students",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, StudentListFragment.newInstance(), StudentListFragment.TAG)
            addToBackStack(null)
        }
    }

    private fun navigateToMakeUps() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Make-Ups",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, MakeUpFragment.newInstance(), MakeUpFragment.TAG)
            //addToBackStack(null)
        }
    }

    fun navigateToMakeUpsStudent() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Make-Ups",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                MakeupStudentListFragment.newInstance(),
                MakeupStudentListFragment.TAG
            )
            //addToBackStack(MakeupStudentListFragment.TAG)
        }
    }

    private fun handleLogout() {
        logoutUser("FCM_TOKEN__C", "")
    }

    private fun moveToLoginScreen() {
        ProceedLoginAcitivty.open(this)
    }

    fun navigateToInvoices(isBackPress: Boolean = false) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Invoice",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(R.id.container, InvoiceFragmnet.newInstance(), InvoiceFragmnet.TAG)
            if (isBackPress)
                addToBackStack(null)
        }

    }

    fun navigateToReferFriends() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Refer-a-Friend",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(R.id.container, ReferFriendFragment.newInstance(), ReferFriendFragment.TAG)
            //    addToBackStack(null)
        }

    }

    fun navigateToBillindDetails(isBackPress: Boolean = false) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Billing Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, BillingFragment.newInstance(), BillingFragment.TAG)
            if (isBackPress)
                addToBackStack(null)
        }

    }

    private fun navigateToAccountDetails() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Account",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, AccountFragment.newInstance(), AccountFragment.TAG)
            //addToBackStack(null)
        }
    }

    fun navigateToNotificationPref() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Notification Prefercnes",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                NotificationPrefrencesFragment.newInstance(),
                NotificationPrefrencesFragment.TAG
            )
            addToBackStack(null)
        }
    }

    fun navigateToPersonalDetails() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Personal Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                PersonalDetailsFragment.newInstance(),
                PersonalDetailsFragment.TAG
            )
            addToBackStack(null)
        }
    }


    fun navigateToStudentDetails(student: Student, enrollment: ArrayList<Enrollment>) {
        setToolbarTitleAndIcons(
            toolbarTitle = "Student Details",
            showNotification = false,
            notificationCount = false,
            showBackButton = true,
            showMenuButton = false
        )
        supportFragmentManager.commit {
            replace(
                R.id.container,
                StudentDetailsFragment.newInstance(student, enrollment),
                StudentDetailsFragment.TAG
            )
            addToBackStack(null)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (supportFragmentManager.findFragmentByTag(StudentHomeFragment.TAG)?.tag == StudentHomeFragment.TAG) {
                finish()
            } else {
                binding.appbar.main.bottomNavigation.currentItem = 0
            }
        }
    }

    fun navigateToHome() {
        setToolbarTitleAndIcons(
            toolbarTitle = "Home",
            showNotification = true,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        supportFragmentManager.commit {
            replace(R.id.container, StudentHomeFragment.newInstance(), StudentHomeFragment.TAG)
            //addToBackStack(StudentHomeFragment.TAG)
        }
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityHomeBinding
        getLoading().observe(this, loadingObserver)
        setBottomNavigation()
        setupHomeNavigation()
        initClickListener()



  /*      var  handler = Handler()
        var runnable = Runnable {
            Log.i("SERVICE_RUNNING_STATUS", "SERVICE RUNNING")

            handler.postDelayed(runnable, 10000)
        }
        handler.postDelayed(runnable, 10000)*/

        val someHandler = Handler(Looper.getMainLooper())
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                if (Connectivity.isConnected(this@HomeAcitivty)) {
                    hitNotificationApi()
                } else {
                    Toast.makeText(
                        this@HomeAcitivty,
                        resources.getString(R.string.no_network_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                someHandler.postDelayed(this, 10000)
            }
        }, 10000)

        /*var handler = Handler();

         handler.postDelayed(Runnable {
         }, 1000 * 10)
         Handler(Looper.getMainLooper()).postDelayed(handler, 1000 * 10)*/
    }


    private fun initClickListener() {
        binding.appbar.toolbar.notification.setOnClickListener {
            navigateToNotifications()
        }
        binding.appbar.toolbar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.appbar.main.bottomNavigation.setOnTabSelectedListener { position, wasSelected ->
            when (position) {
                0 -> navigateToHome()
                1 -> navigateToSchedule()
                2 -> navigateToMakeUps()
                3 -> navigateToTicket()
                4 -> navigateToAccountDetails()
            }
            true
        }
        binding.appbar.toolbar.navDelete.setOnClickListener {

            DialogUtils.showAlertDialogInfoCancleCallback(
                this,
                "Are you sure you want to delete all notifications?",
                object : OkDialogClickListener {
                    override fun onOkClick(dialog: Dialog) {
                        homeViewModel.getNotificationDelete(preferenceHelper[PreferenceHelper.PARENR_ID])
                        homeViewModel.notificationDeleteAllRequest.observe(
                            this@HomeAcitivty,
                            notificationDeleteObserver
                        )
                    }
                }
            )
        }
    }

    private val notificationDeleteObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleNotificationResponse(it)
            }
        }
    }

    private fun handleNotificationResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun logoutUser(fieldApi: String, newValue: String) {
        if (Connectivity.isConnected(this)) {
            var request = NotificationUpdateRequest(
                recordId = preferenceHelper[PreferenceHelper.PARENR_ID],
                fieldApi = fieldApi,
                newValue = newValue
            )
            homeViewModel.logout(request)
            homeViewModel.notificationUpdateRequest.observe(this, logoutObserver)
        } else {
        }
    }

    private val logoutObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleLogoutResponse(it)
            }
        }
    }

    private fun handleLogoutResponse(response: ApiResponse<NotificationDeleteResponse>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                preferenceHelper.setUserLoggedIn(false)
                preferenceHelper.clearData()
                moveToLoginScreen()
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun hitNotificationApi() {
        if (Connectivity.isConnected(this)) {
            notificationViewModel.getNotificationList(preferenceHelper[PreferenceHelper.PARENR_ID])
            notificationViewModel.notificationRequest.observe(this, notificationListObserver)
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private val notificationListObserver: Observer<ApiResponse<NotificationListResponse>> by lazy {
        Observer<ApiResponse<NotificationListResponse>> {
            it?.let {
                handleNotificationListResponse(it)
            }
        }
    }

    private fun handleNotificationListResponse(response: ApiResponse<NotificationListResponse>) {
        //  progressLoader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                if (response.data != null && response.data.notifications.isNotEmpty()) {
                    notificationList.clear()
                    for (i in 0..response.data.notifications.size - 1) {
                        if (!response.data.notifications.get(i).isRead)
                            notificationList.add(response.data.notifications.get(i).notificationId)
                    }
                }
                if (notificationList.size > 9) {
                    binding.appbar.toolbar.tvNotificationCount.text = "9+"
                } else {
                    binding.appbar.toolbar.tvNotificationCount.text =
                        notificationList.size.toString()
                }
                if (notificationList.size == 0) {
                    binding.appbar.toolbar.tvNotificationCount.visibility = View.GONE
                } else {
                    binding.appbar.toolbar.tvNotificationCount.visibility = View.VISIBLE
                }
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    this,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}