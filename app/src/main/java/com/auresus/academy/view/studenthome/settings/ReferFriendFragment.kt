package com.auresus.academy.view.studenthome.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentReferFreindBinding
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.home.ReferViewModel
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReferFriendFragment : BaseFragment() {
    lateinit var binding: FragmentReferFreindBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    private var studentData: StudentLoginResponse? = null
    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private val referViewModel: ReferViewModel by viewModel()

    companion object {
        val TAG = ReferFriendFragment::class.simpleName
        fun newInstance(): ReferFriendFragment {
            return ReferFriendFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_refer_freind
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentReferFreindBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        choosePhotoHelper?.onSaveInstanceState(outState)
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
    private val pickImage = 100
    private var imageUri: Uri? = null

    private fun initClickListener() {
        binding.viewYourDashboard.setOnClickListener {
            if (binding.rewardInfo.isVisible)
                binding.rewardInfo.visibility = View.GONE
            else
                binding.rewardInfo.visibility = View.VISIBLE
        }
        binding.shareBtn.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "We are currently  taking music lessons at Aureus Academy and loving it! Sign up for a free trial lesson at Aureus with my link, and enjoy \$100.0 off your lesson fees when you complete your enrolment. \n\n https://full-aureusgroup.cs117.force.com/apex/freetrial_ipad1?refcode=AA06998&country=SG"
            )
            sendIntent.setType("text/plain")
            startActivity(sendIntent)
        }

    }

    private fun setData() {
        //hitReferDiscountAmtApi()
        //hitReferAmtApi()
        hitReferUrlApi()
        //hitReferEnrollemtnApi()
    }

    private fun hitReferDiscountAmtApi() {
        if (Connectivity.isConnected(activity)) {
            referViewModel.referDiscountAmt("SG")
            referViewModel.referDiscountAmtRequest.observe(this, referDiscountAmtObserver)
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

    private fun hitReferEnrollemtnApi() {
        if (Connectivity.isConnected(activity)) {
            referViewModel.referEnrollment(studentData!!.parentId)
            referViewModel.referEnrollmentRequest.observe(this, referEnrollmentObserver)
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

    private fun hitReferAmtApi() {
        if (Connectivity.isConnected(activity)) {
            referViewModel.referAmt(studentData!!.parentId)
            referViewModel.referAmtRequest.observe(this, referAmtObserver)
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

    private fun hitReferUrlApi() {
        if (Connectivity.isConnected(activity)) {
            referViewModel.referUrl(studentData!!.country)
            referViewModel.referUrlRequest.observe(this, referUrlObserver)
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

    private val referDiscountAmtObserver: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handlereferDiscountAmtObserver(it)
            }
        }
    }

    private val referEnrollmentObserver: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handleEnrollmentObserver(it)
            }
        }
    }
    private val referAmtObserver: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handlereferAmtObserver(it)
            }
        }
    }
    private val referUrlObserver: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handlerUrlObserver(it)
            }
        }
    }


    private fun handlereferDiscountAmtObserver(response: ApiResponse<String>) {
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

    private fun handlereferAmtObserver(response: ApiResponse<String>) {
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

    private fun handlerUrlObserver(response: ApiResponse<String>) {
        binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                binding.buttonLink.text = response.data
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

    private fun handleEnrollmentObserver(response: ApiResponse<String>) {
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

}