package com.auresus.academy.view.studenthome.settings

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentBillingDetailsBinding
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BillingFragment : BaseFragment() {
    lateinit var binding: FragmentBillingDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    private var studentData: StudentLoginResponse? = null

    companion object {
        val TAG = BillingFragment::class.simpleName
        fun newInstance(): BillingFragment {
            return BillingFragment()
        }
    }

    override fun getLayoutId(): Int {

        return R.layout.fragment_billing_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentBillingDetailsBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
    }

    private val eventDataObserver: Observer<StudentLoginResponse> by lazy {
        Observer<StudentLoginResponse> {
            it?.let {
                studentData = it
                if (studentData != null)
                    setData()
            }
        }
    }


    private fun initClickListener() {

    }

    private fun setData() {
        binding.cardDate.text = "x${studentData?.cardNumber}"
        binding.expiryDate.text = studentData?.expiration
    }

}