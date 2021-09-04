package com.auresus.academy.view.feedback

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityFeedbackBinding
import com.auresus.academy.model.bean.requests.PublicMeetingFeedbackRequest
import com.auresus.academy.model.bean.requests.TeacherDateRequest
import com.auresus.academy.model.bean.responses.GetAccessTokenResponse
import com.auresus.academy.model.bean.responses.TeacherListResponse
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.twilio.video.app.ui.room.RoomActivity
import com.auresus.academy.view.base.BaseActivity
import com.auresus.academy.view.login.LoginAcitivty
import com.auresus.academy.view.studenthome.HomeAcitivty
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view.teacherhome.TeacherHomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedbackActivity : BaseActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private var roomID: String = ""
    private var roomCode: String = ""
    private var roomName: String = ""
    private var studentName: String = ""
    private var rating: String = "1"
    private val feedbackViewModel: FeedbackViewModel by viewModel()
    var context: Context? = null

    override fun getLayout(): Int {
        return R.layout.activity_feedback
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityFeedbackBinding
        context = this
        if (intent != null) {
            roomName = intent.getStringExtra("roomName").toString()
            roomID = intent.getStringExtra("roomId").toString()
            studentName = intent.getStringExtra("studentName").toString()
            roomCode = intent.getStringExtra("meetingCodeLocal").toString()
        }
        initClickListener()
    }

    private fun initClickListener() {
        binding.ivBack?.setOnClickListener {
            if (preferenceHelper != null) {
                if (preferenceHelper.getUserType()==1) {
                    getMeetingFeedback()
                    var intent = Intent(this, HomeAcitivty::class.java)
                    startActivity(intent)
                } else if (preferenceHelper.getUserType()==2) {
                    getMeetingFeedback()
                    val intent = Intent(this, TeacherHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    getPublicMeeting()
                    val intent = Intent(this, LoginAcitivty::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                getPublicMeeting()
                val intent = Intent(this, LoginAcitivty::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnHome?.setOnClickListener {
            if (preferenceHelper != null) {
                if (preferenceHelper.getUserType()==1) {
                    getMeetingFeedback()
                    var intent = Intent(this, HomeAcitivty::class.java)
                    startActivity(intent)
                    finish()
                } else if (preferenceHelper.getUserType()==2) {
                    getMeetingFeedback()
                    val intent = Intent(this, TeacherHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    getPublicMeeting()
                    val intent = Intent(this, LoginAcitivty::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                getPublicMeeting()
                val intent = Intent(this, LoginAcitivty::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnRejoin.setOnClickListener {
            RoomActivity.open(
                context!!,
                roomName,
                roomID,
                studentName,
                roomCode
            )
            if (preferenceHelper != null) {
                if (preferenceHelper.getUserType()==1) {
                    getMeetingFeedback()
                } else if (preferenceHelper.getUserType()==2) {
                    getMeetingFeedback()
                } else {
                    getPublicMeeting()
                    val intent = Intent(this, LoginAcitivty::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                getPublicMeeting()
            }
        }

        binding.tv1.setOnClickListener { rating = binding.tv1.text.toString() }
        binding.tv2.setOnClickListener { rating = binding.tv2.text.toString() }
        binding.tv3.setOnClickListener { rating = binding.tv3.text.toString() }
        binding.tv4.setOnClickListener { rating = binding.tv4.text.toString() }
        binding.tv5.setOnClickListener { rating = binding.tv5.text.toString() }


    }

    override fun onStart() {
        super.onStart()
       // checkIntentURI()
    }

    private fun checkIntentURI(): Boolean {
        roomName = intent.getStringExtra(INTENT_EXTRA_ROOM_NAME) ?: ""
        roomID = intent.getStringExtra(INTENT_EXTRA_ROOM_ID) ?: ""
        studentName = intent.getStringExtra(INTENT_EXTRA_STUDENT_NAME) ?: ""
        roomCode = intent.getStringExtra(INTENT_EXTRA_ROOM_CODE) ?: ""
        return true
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
        private const val MEDIA_PROJECTION_REQUEST_CODE = 101
        private const val INTENT_EXTRA_ROOM_NAME = "intent_room_name"
        private const val INTENT_EXTRA_ROOM_ID = "intent_room_id"
        private const val INTENT_EXTRA_STUDENT_NAME = "intent_student_name"
        private const val INTENT_EXTRA_ROOM_CODE = "intent_room_code"
        private const val USER_TYPE_STUDENT = 1
        private const val USER_TYPE_TEACHER = 2
        private const val LOCAL_PARTICIPANT_STUB_SID = ""

        fun open(
            currActivity: Context,
            roomName: String?,
            roomId: String?,
            studentName: String?,
            meetingCodeLocal: String?
        ) {
            currActivity.run {
                val intent = Intent(this, FeedbackActivity::class.java)
                intent.putExtra(INTENT_EXTRA_ROOM_NAME, roomName)
                intent.putExtra(INTENT_EXTRA_ROOM_ID, roomId)
                intent.putExtra(INTENT_EXTRA_STUDENT_NAME, studentName)
                intent.putExtra(INTENT_EXTRA_ROOM_CODE, meetingCodeLocal)
                startActivity(intent)
            }
        }
    }


    private fun getPublicMeeting() {
        if (Connectivity.isConnected(context)) {
            val request = PublicMeetingFeedbackRequest(
                person_name = studentName,
                room_name = roomName,
                rating = rating
            )

            feedbackViewModel.meetingFeedbackGuest(request)
            feedbackViewModel.publicMeetingFeedbackMeetingResponse.observe(this, publicMeeting)
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()

        }
    }

    private val publicMeeting: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handlePublicMeetingResponse(it)
            }
        }
    }

    private fun handlePublicMeetingResponse(response: ApiResponse<String>) {
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                Toast.makeText(
                    context,
                    ApiResponse.Status.SUCCESS.toString(),
                    Toast.LENGTH_LONG
                ).show()

            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    context,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun getMeetingFeedback() {
        if (Connectivity.isConnected(context)) {
            feedbackViewModel.meetingFeedback(
                studentName,
                roomID,
                rating,
                preferenceHelper[PreferenceHelper.PARENR_ID]
            )
            feedbackViewModel.meetingFeedbackMeetingResponse.observe(this, meetingFeedback)
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()

        }
    }

    private val meetingFeedback: Observer<ApiResponse<String>> by lazy {
        Observer<ApiResponse<String>> {
            it?.let {
                handleMeetingResponse(it)
            }
        }
    }

    private fun handleMeetingResponse(response: ApiResponse<String>) {
        when (response.status) {
            ApiResponse.Status.SUCCESS -> {
                Toast.makeText(
                    context,
                    ApiResponse.Status.SUCCESS.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
            ApiResponse.Status.ERROR -> {
                Toast.makeText(
                    context,
                    getString(R.string.internal_server_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}