package com.auresus.academy.view.studenthome.ticket

import android.view.View
import androidx.annotation.Nullable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentTicketBinding
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import org.koin.android.ext.android.inject

class TicketFragment : BaseFragment() {

    val preferenceHelper: PreferenceHelper by inject()
    lateinit var binding: FragmentTicketBinding

    companion object {
        val TAG = TicketFragment::class.java.simpleName
        fun newInstance(): TicketFragment {
            val ticketFragment = TicketFragment()
            return ticketFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_ticket
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeAcitivty).setToolbarTitleAndIcons(
            toolbarTitle = "Tickets",
            showNotification = false,
            notificationCount = false,
            showBackButton = false,
            showMenuButton = true
        )
        initView()
        setupViewPager(binding.tabViewpager)
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentTicketBinding
        binding.editPersonalDetails.setOnClickListener {
            (activity as HomeAcitivty).navigateToCreateNewTicket()
        }
    }

    private fun initView() {
        binding.tabs.setupWithViewPager(binding.tabViewpager)
    }

    // This function is used to add items in arraylist and assign
    // the adapter to view pager
    private fun setupViewPager(viewpager: ViewPager) {
        var adapter  = ViewPagerAdapter(childFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(TicketOpenFragment(), "Open Tickets")
        adapter.addFragment(TicketCloseFragment(), "Closed Tickets")
        // setting adapter to view pager.
        viewpager.adapter = adapter
    }


    // This "ViewPagerAdapter" class overrides functions which are
    // necessary to get information about which item is selected
    // by user, what is title for selected item and so on.*/
    class ViewPagerAdapter : FragmentPagerAdapter {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        private final var fragmentList1: ArrayList<Fragment> = ArrayList()
        private final var fragmentTitleList1: ArrayList<String> = ArrayList()

        // this is a secondary constructor of ViewPagerAdapter class.
        public constructor(supportFragmentManager: FragmentManager)
                : super(supportFragmentManager)

        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1.get(position)
        }

        // returns which item is selected from arraylist of titles.
        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1.get(position)
        }

        // returns the number of items present in arraylist.
        override fun getCount(): Int {
            return fragmentList1.size
        }

        // this function adds the fragment and title in 2 separate  arraylist.
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
        }
    }
}