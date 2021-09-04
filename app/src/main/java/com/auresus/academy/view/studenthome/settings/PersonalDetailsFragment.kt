package com.auresus.academy.view.studenthome.settings

import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.FragmnetPersonalDetailsBinding
import com.auresus.academy.model.bean.responses.NotificationDeleteResponse
import com.auresus.academy.model.bean.responses.StudentLoginResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PersonalDetailsFragment : BaseFragment() {
    lateinit var binding: FragmnetPersonalDetailsBinding
    val preferenceHelper: PreferenceHelper by inject()
    private val baseViewModel: BaseViewModel by viewModel()
    private var studentData: StudentLoginResponse? = null
    private var editMode: Boolean = false
    private lateinit var updatedDate: StudentLoginResponse
    private val studentViewModel: StudentViewModel by viewModel()

    companion object {
        val TAG = PersonalDetailsFragment::class.simpleName
        fun newInstance(): PersonalDetailsFragment {
            return PersonalDetailsFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragmnet_personal_details
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        this.binding = binding as FragmnetPersonalDetailsBinding
        baseViewModel.getHomeLiveData().observe(this, eventDataObserver)
        initClickListener()
        setEditMode(false)
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
        binding.editPersonalDetails.setOnClickListener {
            Toast.makeText(activity, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.editPersonalDetails.setOnClickListener {
            if (editMode)
                callApi()
            editMode = !editMode
            setEditMode(editMode)
        }
    }

    private fun callApi() {
        updatedDate = StudentLoginResponse(
            firstName = binding.studentFirstName.text.toString(),
            gender = binding.studentGender.text.toString(),
            lastName = binding.studentLastName.text.toString(),
            email = binding.studentEmail.text.toString(),
            street = binding.studentStreet.text.toString(),
            city = binding.studentCity.text.toString(),
            postal_Code = binding.studentPostalCode.text.toString(),
            parentId = studentData!!.parentId,
            availableMakeupUnits = studentData!!.availableMakeupUnits,
            bookingRemindedNotification = studentData!!.bookingRemindedNotification,
            cardNumber = studentData!!.cardNumber,
            cardType = studentData!!.cardType,
            country = studentData!!.country,
            currency = studentData!!.currency,
            enrolments = studentData!!.enrolments,
            events = studentData!!.events,
            expiration = studentData!!.expiration,
            fifthBookingFaqUrl = studentData!!.fifthBookingFaqUrl,
            invoicePaidNotification = studentData!!.invoicePaidNotification,
            isAmericanClubMember = studentData!!.isAmericanClubMember,
            isInvoiceDue = studentData!!.isInvoiceDue,
            name = studentData!!.name,
            nationality = studentData!!.nationality,
            nextBillingDate = if (studentData?.nextBillingDate != null) studentData!!.nextBillingDate else "",
            phone = studentData!!.phone,
            profilePictureUrl = if (studentData?.profilePictureUrl != null) studentData!!.profilePictureUrl else "",
            students = studentData!!.students
        )
        studentViewModel.updatePersonalDetails(
            studentViewModel.createPersonalDetailsRequest(
                studentData!!,
                updatedDate,
                preferenceHelper[PreferenceHelper.PARENR_ID]
            )
        )
        studentViewModel.updateStudentRequest.observe(this, ticketListObserver)
    }

    private val ticketListObserver: Observer<ApiResponse<NotificationDeleteResponse>> by lazy {
        Observer<ApiResponse<NotificationDeleteResponse>> {
            it?.let {
                handleTicketResponse(it)
            }
        }
    }

    private fun handleTicketResponse(response: ApiResponse<NotificationDeleteResponse>) {
        //binding.progressLoader.loader.isVisible = response.status == ApiResponse.Status.LOADING
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


    private fun setData() {
        binding.studentEmail.setText(preferenceHelper.getEmail())
        binding.studentFirstName.setText(studentData!!.firstName)
        binding.studentLastName.setText(studentData!!.lastName)
        binding.studentGender.setText(studentData!!.gender)
        binding.studentNationality.setText(studentData!!.nationality)
        binding.studentStreet.setText(studentData!!.street)
        binding.studentCity.setText(studentData!!.city)
        binding.studentPostalCode.setText(studentData!!.postal_Code)
        binding.studentCountry.setText(studentData!!.country)
    }

    private fun setEditMode(isEditMode: Boolean) {
        binding.firstNameView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.lastNameView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.emailView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.streetNameView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.cityView.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.postalCodeView.visibility = if (isEditMode) View.VISIBLE else View.GONE

        binding.studentFirstName.isEnabled = isEditMode
        binding.studentLastName.isEnabled = isEditMode
        binding.studentEmail.isEnabled = isEditMode
        binding.studentCity.isEnabled = isEditMode
        binding.studentStreet.isEnabled = isEditMode
        binding.studentPostalCode.isEnabled = isEditMode

        binding.editPersonalDetails.text =
            if (!isEditMode) "Edit Student Details" else "Update Student Details"
    }

}