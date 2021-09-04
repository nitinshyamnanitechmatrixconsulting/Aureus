package com.auresus.academy.view.studenthome.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmentAccountBinding
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view_model.BaseViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AccountFragment : BaseFragment() {
    lateinit var binding: FragmentAccountBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    private var studentData: StudentLoginResponse? = null
    private var choosePhotoHelper: ChoosePhotoHelper? = null

    companion object {
        val TAG = AccountFragment::class.simpleName
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_account
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmentAccountBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.userProfileImage.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        binding.invoiceRl.setOnClickListener {
            (activity as HomeAcitivty).navigateToInvoices(true)
        }
        binding.personalDetailsRl.setOnClickListener {
            (activity as HomeAcitivty).navigateToPersonalDetails()
        }
        binding.studentRl.setOnClickListener {
            (activity as HomeAcitivty).navigateToStudentList()

        }
        binding.notificationRl.setOnClickListener {
            (activity as HomeAcitivty).navigateToNotificationPref()
        }
        binding.editIcon.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding.billingInfoRl.setOnClickListener {
            (activity as HomeAcitivty).navigateToBillindDetails(true)
        }
    }

    private fun setData() {
        binding.userName.text = preferenceHelper.getUserName()
        binding.cardNumber.text = "x${studentData?.cardNumber}"
        binding.cardDate.text = studentData?.nextBillingDate
    }

}