/*
 * Copyright (C) 2019 Twilio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twilio.video.app.ui.room

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import co.intentservice.chatui.ChatView
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.gdacciaro.iOSDialog.iOSDialogClickListener
import com.github.javiersantos.bottomdialogs.BottomDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioDevice.*
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.app.R
import com.twilio.video.app.base.BaseActivity
import com.twilio.video.app.data.api.AuthServiceError
import com.twilio.video.app.data.api.TokenService
import com.twilio.video.app.databinding.RoomActivityBinding
import com.twilio.video.app.participant.ParticipantViewState
import com.twilio.video.app.sdk.RoomManager
import com.twilio.video.app.sdk.VideoTrackViewState
import com.twilio.video.app.ui.LessonAddFileFragment
import com.twilio.video.app.ui.room.RoomViewConfiguration.Connecting
import com.twilio.video.app.ui.room.RoomViewEffect.*
import com.twilio.video.app.ui.room.RoomViewEvent.*
import com.twilio.video.app.ui.room.RoomViewModel.RoomViewModelFactory
import com.twilio.video.app.ui.room.modal.*
import com.twilio.video.app.ui.room.modal.LayoutOptionSheetFragment.Companion.DEFAULT
import com.twilio.video.app.ui.room.modal.LayoutOptionSheetFragment.Companion.SPLIT
import com.twilio.video.app.util.ChatUtils
import com.twilio.video.app.util.PermissionUtil
import io.uniflow.android.livedata.onEvents
import io.uniflow.android.livedata.onStates
import timber.log.Timber
import javax.inject.Inject
import com.bumptech.glide.Glide
import com.twilio.video.*
import com.twilio.video.app.data.api.model.Token
import com.twilio.video.app.helper.ApiHelper
import com.twilio.video.app.helper.StringHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import com.twilio.video.RemoteDataTrack

import com.twilio.video.RemoteDataTrackPublication

import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrackPublication

import com.twilio.video.RemoteAudioTrackPublication

import com.twilio.video.TwilioException

import com.twilio.video.RemoteVideoTrack

import com.twilio.video.RemoteAudioTrack


@Suppress("DEPRECATION")
class RoomActivity : BaseActivity(),
    MeettingOptionHandler/*, LocalParticipant.Listener*//*,RemoteParticipant.Listener*/ {

    private lateinit var aa: ArrayAdapter<String>;
    private var isConnected: Boolean = false
    private var participantCount: Int = 0
    var bottomSelectPosition = 0
    var bottomSelectPosition1 = 0
    private var isUp: Boolean = false
    private var roomID: String = ""
    private var type: String = ""
    private var roomCode: String = ""
    private lateinit var participantAdapter: ParticipantAdapter
    private var bottomDialog: BottomDialog? = null
    private lateinit var binding: RoomActivityBinding
    private var savedVolumeControlStream = 0
    private var displayName: String? = null
    private var localParticipantSid = LOCAL_PARTICIPANT_STUB_SID
    private var roomName: String = ""
    private var studentName: String = ""
    private var chatView: ChatView? = null
    private var name: String = ""
    private var currentLayoutMode = DEFAULT
    private var currentUserType = USER_TYPE_TEACHER
    private val mutableViewHolderEvents = MutableLiveData<RoomViewEvent>()
    private val viewHolderEvents: LiveData<RoomViewEvent> = mutableViewHolderEvents
    var room: Room? = null
    var temToken: String = ""
    lateinit var localDataTrack: LocalDataTrack

    @Inject
    lateinit var tokenService: TokenService

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var roomManager: RoomManager

    @Inject
    lateinit var audioSwitch: AudioSwitch
    var mediaPlayer: MediaPlayer? = null
    var audioManager: AudioManager? = null

    /** Coordinates participant thumbs and primary participant rendering.  */
    private lateinit var primaryParticipantController: PrimaryParticipantController
    private lateinit var primaryParticipantController1: PrimaryParticipantController
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var recordingAnimation: ObjectAnimator

    // Map used to map remote data tracks to remote participants
    private val dataTrackRemoteParticipantMap: Map<RemoteDataTrack, RemoteParticipant> = HashMap()
    private val DATA_TRACK_MESSAGE_THREAD_NAME = "DataTrackMessages"
    val dataTrackMessageThread = HandlerThread(DATA_TRACK_MESSAGE_THREAD_NAME)
    lateinit var dataTrackMessageThreadHandler: Handler
    var handler: Handler? = null
    var runnable: Runnable? = null
    private var isRunning: Boolean = true
    var temp = 0
    var participantIdentity = ArrayList<String>()
    var newThumbnailsList: List<ParticipantViewState>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RoomActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        localDataTrack = LocalDataTrack.create(this@RoomActivity)!!
        // Start the thread where data messages are received
        dataTrackMessageThread.start()
        dataTrackMessageThreadHandler = Handler(dataTrackMessageThread.looper)

        fun token() {
            binding.connectProgress.visibility = View.VISIBLE
            val retrofit = Retrofit.Builder()
                .baseUrl("https://aureusacademy-meeting.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiHelper = retrofit.create(
                ApiHelper::class.java
            )
            val call = apiHelper.getToken(displayName!! + Uri.encode("*%\$&"), roomName)
            call.enqueue(object : Callback<Token?>, Room.Listener {
                override fun onResponse(
                    call: Call<Token?>,
                    response: Response<Token?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        try {
                            /* var temp= mutableListOf<LocalDataTrack>()
                              temp.add(localDataTrack)*/
                            temToken = response.body()?.key.toString()
                            val connectOptions =
                                ConnectOptions.Builder(temToken).roomName(roomName).dataTracks(
                                    Collections.singletonList(localDataTrack)
                                )
                                    .build()
                            room = Video.connect(this@RoomActivity, connectOptions, this)

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<Token?>, t: Throwable) {
                    Toast.makeText(this@RoomActivity, "something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onConnected(room: Room) {

                    // Let it continue running until it is stopped.
                    //  Toast.makeText(this, "Service Started onStartcommand", Toast.LENGTH_LONG).show();
                    handler = Handler()
                    runnable = Runnable {

                        /* val someHandler = Handler(Looper.getMainLooper())
                         someHandler.postDelayed(object : Runnable {
                             override fun run() {*/
                        //  room.localParticipant.publishTrack(local)
                        val d = (room.remoteParticipants)
                        val isChecked =
                            d.any { it.identity.replace("*%\$&", "") == displayName }
                        if (isChecked) {
                            AlertDialog.Builder(this@RoomActivity, R.style.AppTheme_Dialog)
                                .setTitle("Joined on Another Device")
                                .setMessage(
                                    "You have joined this call on another device.\n" +
                                            "If you would like to join the call on this device as $displayName, please leave the call on your other device and rejoin on this one."
                                )
                                .setPositiveButton(getString(android.R.string.ok), null)
                                .show()
                            room.disconnect()
                            binding.joinProgressLoader.visibility = View.INVISIBLE
                            handler!!.removeCallbacks(runnable!!)
                            isRunning = false
                        } else {
                            if (type == "1") {
                                room.disconnect()
                                connect()
                                handler!!.removeCallbacks(runnable!!)
                                isRunning = false
                            } else if (type == "2") {
                                var participantSize = 0
                                for (participant in room.remoteParticipants) {
                                    if (participant.identity.contains("*%\$&")) {
                                        participantSize += 1
                                    }
                                }
                                if (room.remoteParticipants.size >= 1 && participantSize == 0) {
                                    room.disconnect()
                                    connect()
                                    handler!!.removeCallbacks(runnable!!)
                                    isRunning = false
                                } else {
                                    //waiting screen
                                    //room.disconnect()
                                    binding.rlWaiting.visibility = View.VISIBLE
                                }
                            } else {
                                var participantSize = 0
                                for (participant in room.remoteParticipants) {
                                    if (participant.identity.contains("*%\$&")) {
                                        participantSize += 1
                                    }
                                }
                                if (room.remoteParticipants.size >= 1 && participantSize == 0) {
                                    //send request msg

                                    if (temp == 0) {
                                        room.localParticipant!!.publishTrack(localDataTrack)
                                        val someHandler = Handler(Looper.getMainLooper())
                                        someHandler.postDelayed(
                                            { localDataTrack.send("NewJoin_\$\$$displayName") },
                                            2000
                                        )
                                        onSNACK(binding.llMain)
                                    }
                                    temp += 1

                                } else {
                                    //waiting screen
                                    //room.disconnect()
                                    binding.rlWaiting.visibility = View.VISIBLE

                                }
                            }


                        }

                        for (remoteParticipant in room.remoteParticipants) {
                            addRemoteParticipant(remoteParticipant!!)
                        }
                        //token()
                        /* someHandler.postDelayed(this, 5000)
                    }
                }, 5000)*/
                        if (isRunning) {
                            handler!!.postDelayed(
                                runnable!!,
                                5000
                            )
                        }
                    }
                    handler!!.postDelayed(
                        runnable!!,
                        5000
                    )
                    //  TODO("Not yet implemented")
                }

                override fun onConnectFailure(room: Room, twilioException: TwilioException) {
                    //TODO("Not yet implemented")
                }

                override fun onReconnecting(room: Room, twilioException: TwilioException) {
                    //TODO("Not yet implemented")
                }

                override fun onReconnected(room: Room) {
                    //TODO("Not yet implemented")
                }

                override fun onDisconnected(room: Room, twilioException: TwilioException?) {
                    //TODO("Not yet implemented")

                }

                override fun onParticipantConnected(
                    room: Room,
                    remoteParticipant: RemoteParticipant
                ) {

                    addRemoteParticipant(remoteParticipant)

                    // TODO("Not yet implemented")
                }

                override fun onParticipantDisconnected(
                    room: Room,
                    remoteParticipant: RemoteParticipant
                ) {

                    //  TODO("Not yet implemented")
                }

                override fun onRecordingStarted(room: Room) {
                    // TODO("Not yet implemented")
                }

                override fun onRecordingStopped(room: Room) {
                    //TODO("Not yet implemented")
                }
            })


        }

        binding.disconnect.setOnClickListener {
            if (type == "1") {
                openDisconnectAllDialog()
            } else {
                disconnectButtonClick()
            }
        }
        binding.localVideo.setOnClickListener { toggleLocalVideo() }
        binding.localAudio.setOnClickListener { toggleLocalAudio() }
        binding.buttonJoinOnlineStudio.setOnClickListener {
            token()
        }
        binding.meetingOption.setOnClickListener { openMeetingOption() }
        binding.room.localAudio1.setOnClickListener { toggleLocalAudio() }
        binding.room.localVideo1.setOnClickListener { toggleLocalVideo() }
        binding.ivWaitingBack.setOnClickListener {
            binding.rlWaiting.visibility = View.GONE
            binding.joinProgressLoader.visibility = View.INVISIBLE
        }
        binding.room.participantView.setOnClickListener {
            onSlideViewButtonClick(binding.room.primaryVideo)
            if (binding.room.coursesspinner.visibility == View.VISIBLE) {
                binding.room.coursesspinner.visibility = View.GONE
            } else {
                binding.room.coursesspinner.visibility = View.VISIBLE
            }
        }

        if (binding.room.coursesspinner.visibility == View.VISIBLE) {
            val someHandler = Handler(Looper.getMainLooper())
            someHandler.postDelayed(object : Runnable {
                override fun run() {
                    binding.room.coursesspinner.visibility = View.GONE
                    someHandler.postDelayed(this, 5000)

                }
            }, 5000)
        }

        if (binding.room.downSpiner.visibility == View.VISIBLE) {
            val someHandler = Handler(Looper.getMainLooper())
            someHandler.postDelayed(object : Runnable {
                override fun run() {
                    binding.room.downSpiner.visibility = View.GONE
                    someHandler.postDelayed(this, 5000)
                }
            }, 5000)
        }

        Glide.with(this).load(R.raw.loding).into(this.binding.joinProgressLoader)
        Glide.with(this).load(R.raw.loding).into(this.binding.waitingjJoinProgressLoader)


        binding.switchCameraActionFab.setOnClickListener {
            roomViewModel.processInput(SwitchCamera)
        }
        val factory = RoomViewModelFactory(roomManager, audioSwitch, PermissionUtil(this))
        roomViewModel = ViewModelProvider(this, factory).get(
            RoomViewModel::
            class.java
        )
        roomViewModel.getMessageLiveData().observe(this, Observer
        {
            addMessage(it)
        })

        // So calls can be answered when screen is locked
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        // Grab views

        // Cache volume control stream
        savedVolumeControlStream = volumeControlStream

        // Setup participant controller
        primaryParticipantController = PrimaryParticipantController(binding.room.primaryVideo)
        primaryParticipantController1 = PrimaryParticipantController(binding.room.participantView)


        setupRecordingAnimation()
        setUpThumbnail()

        onStates(roomViewModel)
        { state ->
            if (state is RoomViewState) bindRoomViewState(state)
        }
        onEvents(roomViewModel)
        { event ->
            if (event is RoomViewEffect) bindRoomViewEffects(event)
        }
        aa =
            participantIdentity?.let {
                ArrayAdapter(
                    this, android.R.layout.simple_spinner_item,
                    it
                )
            }
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(R.layout.custom_simple_spinner_dropdown)
        // Set Adapter to Spinner

        binding.room.coursesspinner.adapter = aa
        binding.room.downSpiner.adapter = aa

        binding.room.ivSwap.setOnClickListener {
            var temppos = binding.room.downSpiner.selectedItemPosition

            primaryParticipantController.renderAsPrimary(
                if (newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].isLocalParticipant) studentName else newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].identity,
                newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].screenTrack,
                newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].videoTrack,
                newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].isMuted,
                if (newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].isLocalParticipant) true else newThumbnailsList!![binding.room.coursesspinner.selectedItemPosition].isMirrored
            )
            binding.room.downSpiner.setSelection(binding.room.coursesspinner.selectedItemPosition)

            primaryParticipantController1.renderAsPrimary(
                if (newThumbnailsList!![temppos].isLocalParticipant) studentName else newThumbnailsList!![temppos].identity,
                newThumbnailsList!![temppos].screenTrack,
                newThumbnailsList!![temppos].videoTrack,
                newThumbnailsList!![temppos].isMuted,
                if (newThumbnailsList!![temppos].isLocalParticipant) true else newThumbnailsList!![temppos].isMirrored
            )
            binding.room.coursesspinner.setSelection(temppos)


        }

        binding.room.coursesspinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // bottomSelectPosition1 = position
                    primaryParticipantController1.renderAsPrimary(
                        if (newThumbnailsList!![position].isLocalParticipant) studentName else newThumbnailsList!![position].identity,
                        newThumbnailsList!![position].screenTrack,
                        newThumbnailsList!![position].videoTrack,
                        newThumbnailsList!![position].isMuted,
                        if (newThumbnailsList!![position].isLocalParticipant) true else newThumbnailsList!![position].isMirrored
                    )
                }

            }

        binding.room.downSpiner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // bottomSelectPosition = position
                    if (currentLayoutMode == DEFAULT) {
                    } else {
                        primaryParticipantController.renderAsPrimary(
                            if (newThumbnailsList!![position].isLocalParticipant) studentName else newThumbnailsList!![position].identity,
                            newThumbnailsList!![position].screenTrack,
                            newThumbnailsList!![position].videoTrack,
                            newThumbnailsList!![position].isMuted,
                            if (newThumbnailsList!![position].isLocalParticipant) true else newThumbnailsList!![position].isMirrored
                        )

                    }
                }

            }
    }

    private fun connect() {
        val viewEvent = Connect(displayName ?: "", roomName)
        roomViewModel.processInput(viewEvent)

    }

    override fun onDestroy() {
        super.onDestroy()
        recordingAnimation.cancel()
    }

    override fun onStart() {
        super.onStart()
        checkIntentURI()
    }

    private fun openGuestAdmitDenyDialog(identitiy: String) {
        iOSDialogBuilder(this@RoomActivity)
            .setTitle(getString(R.string.aureus))
            .setSubtitle(String.format(getString(R.string.wants_to_join), identitiy))
            .setBoldPositiveLabel(true)
            .setCancelable(true)
            .setPositiveListener(getString(R.string.admit), object : iOSDialogClickListener {
                override fun onClick(dialog: iOSDialog) {
                    dialog.run {
                        val admitMessage = MessageCommand.sendAdmitJoinRoom()
                        roomViewModel.processInput(SendMessage(admitMessage))
                        dismiss()
                    }
                }
            })
            .setNegativeListener(getString(R.string.deny),
                object : iOSDialogClickListener {
                    override fun onClick(dialog: iOSDialog) {
                        dialog.run {
                            val denyMessage = MessageCommand.sendDenyJoinRoom()
                            roomViewModel.processInput(SendMessage(denyMessage))
                            dismiss()
                        }
                    }
                })
            .build().show()
    }

    private fun openDisconnectAllDialog() {
        iOSDialogBuilder(this@RoomActivity)
            .setTitle(getString(R.string.aureus))
            .setSubtitle("Are you sure you want to disconnect all ?")
            .setBoldPositiveLabel(true)
            .setCancelable(true)
            .setPositiveListener(getString(R.string.yes), object : iOSDialogClickListener {
                override fun onClick(dialog: iOSDialog) {
                    dialog.run {
                        val removeAllMessage = MessageCommand.removeAll()
                        roomViewModel.processInput(SendMessage(removeAllMessage))
                        dismiss()
                        disconnectButtonClick()
                    }
                }
            })
            .setNegativeListener(getString(R.string.no),
                object : iOSDialogClickListener {
                    override fun onClick(dialog: iOSDialog) {
                        dialog.run {
                            // val denyMessage = MessageCommand.sendDenyJoinRoom()
                            // roomViewModel.processInput(SendMessage(denyMessage))
                            dismiss()
                        }
                    }
                })
            .build().show()
    }

    override fun onResume() {
        super.onResume()
        displayName = studentName
        setTitle(displayName)
        roomViewModel.processInput(OnResume)
    }

    override fun onPause() {
        super.onPause()
        roomViewModel.processInput(OnPause)
    }

    // Pass list of your model as arraylist
    private fun showCustomAlertDialogBoxForUserList(allUsersList: ArrayList<ParticipantViewState>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.assignment_dialog_list_view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // this is optional
        val listView: ListView = dialog.findViewById(R.id.lv_assignment_users)
        val tv: TextView = dialog.findViewById(R.id.tv_popup_title)
        val arrayAdapter =
            UserListAdapter(this, R.layout.item_assignment_dialog_list_layout, allUsersList)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener({ adapterView, view, which, l ->
            changeDominantSpeakerView(
                allUsersList[which].sid
            )
        })
        dialog.show()
    }

    private fun changeDominantSpeakerView(sid: String?) {
        sid?.let {

        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val recordAudioPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val cameraPermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (recordAudioPermissionGranted && cameraPermissionGranted) {
                roomViewModel.processInput(OnResume)
            }
        }
    }


    private fun openMeetingOption() {
        if (!isFinishing) {
            MeetingOptionBottomSheetFragment.openMeetingOption(this, this, roomViewModel)
            supportFragmentManager.executePendingTransactions()
        }
    }

    override fun handleShowMessages() {
        if (!isFinishing) {
            ChatMessageFragment.openChat(this, roomViewModel)
            supportFragmentManager.executePendingTransactions()
        }
    }


    override fun handleShowLayoutOption() {

        if (!isFinishing) {
            LayoutOptionSheetFragment.openMeetingOption(this, this, roomViewModel)
            supportFragmentManager.executePendingTransactions()
        }

    }

    override fun switchLayout(option: Int) {
        if (participantCount < 1) return
        currentLayoutMode = option
        when (option) {
            SPLIT -> {
                binding.room.coursesspinner.visibility = View.VISIBLE
                binding.room.participantView.visibility = View.VISIBLE
                binding.room.ivSwap.visibility = View.VISIBLE
                binding.room.participantThumbView.visibility = View.GONE
                binding.room.downSpiner.visibility = View.VISIBLE
                binding.room.primaryVideo.setOnClickListener {
                    if (binding.room.downSpiner.visibility == View.VISIBLE) {
                        binding.room.downSpiner.visibility = View.GONE
                    } else {
                        binding.room.downSpiner.visibility = View.VISIBLE
                    }
                }

            }
            DEFAULT -> {
                binding.room.participantThumbView.visibility = View.VISIBLE
                binding.room.participantView.visibility = View.GONE
                binding.room.ivSwap.visibility = View.GONE
                binding.room.coursesspinner.visibility = View.GONE
                binding.room.downSpiner.visibility = View.GONE
            }
        }
    }

    override fun handleShowInfo() {
        if (!isFinishing) {
            LessionInfoSheetFragment.open(this, this, roomViewModel)
            supportFragmentManager.executePendingTransactions()
        }

    }

    @SuppressLint("ResourceType")
    override fun handleOpenFiles() {
        if (!isFinishing) {
            val instance = LessonAddFileFragment.newInstance(roomName, roomID, roomViewModel)
            this@RoomActivity.supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_in_down,
                    R.anim.slide_out_down,
                    R.anim.slide_out_up
                )
                replace(R.id.container, instance, LessonAddFileFragment.TAG)
                addToBackStack(null)
                supportFragmentManager.executePendingTransactions()
            }

        }
        /* val fragmentManager: FragmentManager = supportFragmentManager
         val ft: FragmentTransaction? = fragmentManager.beginTransaction()
         ft?.setCustomAnimations(  R.anim.slide_in_up,
             R.anim.slide_in_down,
             R.anim.slide_out_down,
             R.anim.slide_out_up)

         val newFragment : LessonAddFileFragment= LessonAddFileFragment.newInstance(roomName, roomID, roomViewModel)
         ft?.replace(R.id.container, newFragment, LessonAddFileFragment.TAG)
         ft?.commit()*/
    }


    override fun handleOpenShowParticipants() {
        if (!isFinishing) {
            ParticipantBottomSheetFragment.openParticipantList(this, roomViewModel)
            supportFragmentManager.executePendingTransactions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Snackbar.make(
                    binding.room.primaryVideo,
                    R.string.screen_capture_permission_not_granted,
                    BaseTransientBottomBar.LENGTH_LONG
                )
                    .show()
                return
            }
            data?.let { data ->
                roomViewModel.processInput(StartScreenCapture(resultCode, data))
                isBordcasting = true
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count >= 1) {
            supportFragmentManager.popBackStack()
            hideKeyboard()
        } else if (binding.actionLayout.visibility == View.VISIBLE && binding.rlWaiting.visibility == View.VISIBLE) {
            binding.rlWaiting.visibility = View.GONE
            binding.joinProgressLoader.visibility = View.INVISIBLE
            // super.onBackPressed()
        } else if (binding.actionLayout.visibility == View.VISIBLE) {
            room?.disconnect()
            super.onBackPressed();
        }
    }

    private fun setupRecordingAnimation() {
        val recordingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_recording)
        recordingAnimation = ObjectAnimator.ofPropertyValuesHolder(
            recordingDrawable,
            PropertyValuesHolder.ofInt("alpha", 100, 255)
        ).apply {
            target = recordingDrawable
            duration = 750
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun checkIntentURI(): Boolean {
        roomName = intent.getStringExtra(INTENT_EXTRA_ROOM_NAME) ?: ""
        roomID = intent.getStringExtra(INTENT_EXTRA_ROOM_ID) ?: ""
        studentName = intent.getStringExtra(INTENT_EXTRA_STUDENT_NAME) ?: ""
        roomCode = intent.getStringExtra(INTENT_EXTRA_ROOM_CODE) ?: ""
        type = intent.getStringExtra(TYPE) ?: ""
        displayName = studentName
        roomViewModel.setIdentity(displayName)
        roomViewModel.roomName = roomName
        roomViewModel.roomId = roomID
        roomViewModel.roomCode = roomCode
        roomViewModel.type = type
        roomViewModel.name = displayName
        return true
    }


    private fun disconnectButtonClick() {
        roomViewModel.processInput(Disconnect)

        try {
            val myIntent = Intent(
                this,
                Class.forName("com.auresus.academy.view.feedback.FeedbackActivity")
            )
            myIntent.putExtra("roomName", roomName)
            myIntent.putExtra("roomId", roomID)
            myIntent.putExtra("studentName", studentName)
            myIntent.putExtra("meetingCodeLocal", roomCode)
            myIntent.putExtra("type", type)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(myIntent)
            finish()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        // TODO Handle screen share
    }

    private fun toggleLocalVideo() {
        roomViewModel.processInput(ToggleLocalVideo)
    }

    private fun toggleLocalAudio() {
        roomViewModel.processInput(ToggleLocalAudio)
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun updateLayout(roomViewState: RoomViewState) {
        var disconnectButtonState = View.GONE
        var roomName = displayName
        var toolbarTitle = displayName
        when (roomViewState.configuration) {
            Connecting -> {
                binding.connectProgress.visibility = View.VISIBLE
                disconnectButtonState = View.VISIBLE
            }
            RoomViewConfiguration.Connected -> {
                disconnectButtonState = View.VISIBLE
                binding.meetingOption.visibility = View.VISIBLE
                binding.switchCameraActionFab.visibility = View.VISIBLE
                binding.room.llBottom.visibility = View.GONE
                binding.rlWaiting.visibility = View.GONE
                roomName = roomViewState.title
                toolbarTitle = roomName

            }
        }
        // binding.room.llBottom.visibility = View.VISIBLE
        binding.videoControlLayout.visibility = View.GONE
        val isMicEnabled = roomViewState.isMicEnabled
        val isCameraEnabled = roomViewState.isCameraEnabled
        val isLocalMediaEnabled = isMicEnabled && isCameraEnabled && roomViewState.isAudioEnabled
        binding.localAudio.isEnabled = isLocalMediaEnabled
        binding.localVideo.isEnabled = isLocalMediaEnabled
        val micDrawable =
            if (roomViewState.isAudioMuted || !isLocalMediaEnabled) R.drawable.microphone_off
            else R.drawable.micropfone_on
        val videoDrawable =
            if (roomViewState.isVideoOff || !isLocalMediaEnabled) R.drawable.video_off
            else R.drawable.video_on
        binding.localAudio.setImageResource(micDrawable)
        binding.localVideo.setImageResource(videoDrawable)
        binding.room.localAudio1.setImageResource(micDrawable)
        binding.room.localVideo1.setImageResource(videoDrawable)
        binding.disconnect.visibility = disconnectButtonState
        binding.disconnect.visibility = disconnectButtonState
        setTitle(toolbarTitle)
        binding.tvHostName.text = StringHelper.getShortString(studentName)
    }


    private fun addMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            val messageType = ChatUtils.getMessageType(message)
            when (messageType) {
                ChatUtils.MessageType.JOIN_ROOM_ACCEPTED -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val name = it[1]
                            room?.disconnect()
                            connect()
                        }
                    }
                }
                ChatUtils.MessageType.JOIN_ROOM_DENIED -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val name = it[1]
                            room?.disconnect()
                            AlertDialog.Builder(this@RoomActivity, R.style.AppTheme_Dialog)
                                .setTitle("Request Declined")
                                .setMessage("Your request to join the call has been denied.")
                                .setPositiveButton(getString(android.R.string.ok), null)
                                .show()
                        }
                    }
                }
                ChatUtils.MessageType.NEW_JOIN -> {
                    val splitMessage = message.split("_\$\$")[1]
                    if (type.equals("1")) {
                        openGuestAdmitDenyDialog(splitMessage)
/*
                        splitMessage.let {
                            if (it.size > 1) {
                                val sender = it[1]
                                openGuestAdmitDenyDialog(sender)
                            }
                        }
*/
                    }
                }
                ChatUtils.MessageType.MUTE_PARTICIPANT -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val name = it[1]
                            if (displayName.equals(name, ignoreCase = true)) {
                                // roomManager.disableLocalAudio()
                                roomViewModel.processInput(DisableLocalAudio)
                                //  toggleLocalAudio()
                            }
                        }
                    }

                    /* val requestType = message.split("_\$\$")[0]
                    val Name = message.split("_\$\$")[1]
                    if (requestType.equals("mute")) {
                        if (displayName.equals(Name)) {
                            toggleLocalAudio()
                        }

                    }*/
                }
                ChatUtils.MessageType.REMOVE_ALL -> {
                    disconnectButtonClick()
                }

                ChatUtils.MessageType.REMOVE_PARTICIPANT -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val name = it[1]
                            if (displayName.equals(name, ignoreCase = true)) {
                                disconnectButtonClick()
                            }

                        }
                    }
                    /* val requestType = message.split("_\$\$")[0]
                    val Name = message.split("_\$\$")[1]
                    if (requestType.equals("remove")) {
                        if (displayName.equals(Name)) {
                            disconnectButtonClick()
                        }

                    }*/

                }
                ChatUtils.MessageType.TYPING -> {
                    val splitMessage = message.split("\$\$\$typing....")[0]
                    splitMessage.let {
                        if (ChatMessageFragment.tvTyping != null) {
                            ChatMessageFragment.tvTyping!!.visibility = View.VISIBLE
                            ChatMessageFragment.tvTyping!!.text =
                                (splitMessage + " is Typing...").toString()
                            val someHandler = Handler(Looper.getMainLooper())
                            someHandler.postDelayed(object : Runnable {
                                override fun run() {
                                    ChatMessageFragment.tvTyping!!.text = " "
                                    // ChatMessageFragment.tvTyping!!.visibility = View.GONE
                                    // someHandler.postDelayed(this, 2000)
                                }
                            }, 3000)

                        }
                    }
                }
                ChatUtils.MessageType.CHAT_MESSAGE -> {
                    if (!isActive) {
                        audioManager =
                            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager!!.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                            0
                        )
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.newmessage)
                        mediaPlayer!!.start()
                    }
                    if (MeetingOptionBottomSheetFragment.ivMsg != null) {
                        if (!isActive && message.isNotEmpty()) {
                            MeetingOptionBottomSheetFragment.ivMsg!!.setImageResource(R.drawable.new_message_alert)
                        } else {
                            MeetingOptionBottomSheetFragment.ivMsg!!.setImageResource(R.drawable.messages_active)
                        }

                    }
                    if (message.isNotEmpty()) {
                        isFirstTime = false
                    }
                }

            }
        }

    }

    private fun setTitle(toolbarTitle: String?) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = toolbarTitle
        }
    }

    private fun setVolumeControl(setVolumeControl: Boolean) {
        volumeControlStream = if (setVolumeControl) {
            AudioManager.STREAM_VOICE_CALL
        } else {
            savedVolumeControlStream
        }
    }

    @TargetApi(21)
    private fun requestScreenCapturePermission() {
        Timber.d("Requesting permission to capture screen")
        val mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_REQUEST_CODE
        )
    }


    private fun toggleAudioDevice(enableAudioDevice: Boolean) {
        setVolumeControl(enableAudioDevice)
        val viewEvent = if (enableAudioDevice) ActivateAudioDevice else DeactivateAudioDevice
        roomViewModel.processInput(viewEvent)
    }

    private fun bindRoomViewState(roomViewState: RoomViewState) {
        updateParticipantList(roomViewState)
        updateLayout(roomViewState)
        updateAudioDeviceIcon(roomViewState.selectedDevice)
        getParticipantList(roomViewState)
    }

    private fun updateParticipantList(roomViewState: RoomViewState) {
        val fragment =
            supportFragmentManager.findFragmentByTag(ParticipantBottomSheetFragment.TAG) as? ParticipantBottomSheetFragment
        fragment?.renderThumbnails(roomViewState)
        //   renderPrimaryView(roomViewState.primaryParticipant)
        val newThumbnails = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        participantCount = roomViewState.participantThumbnails?.size ?: 0
        when (currentLayoutMode) {
            SPLIT -> {
                if (participantCount > 1) {
                    //  binding.room.participantThumbView.visibility = View.VISIBLE
                    binding.room.participantThumbView.visibility = View.GONE
                    binding.room.coursesspinner.visibility = View.VISIBLE
                    binding.room.downSpiner.visibility = View.VISIBLE
                    val topTwoPartcipantList = newThumbnails?.subList(0, 2)
                    val participantView = topTwoPartcipantList?.get(0)
                    participantView?.let {
                        // renderDominantView(it)
                    }
                    binding.room.primaryVideo.setOnClickListener {
                        onSlideViewButtonClick(binding.room.primaryVideo)
                        if (binding.room.downSpiner.visibility == View.VISIBLE) {
                            binding.room.downSpiner.visibility = View.GONE
                        } else {
                            binding.room.downSpiner.visibility = View.VISIBLE
                        }
                    }
                }

            }
            DEFAULT -> {
                binding.room.coursesspinner.visibility = View.GONE
                binding.room.downSpiner.visibility = View.GONE
                binding.room.participantThumbView.visibility = View.VISIBLE
                binding.room.downSpiner.visibility = View.GONE


                renderPrimaryView(roomViewState.primaryParticipant)
                renderThumbnails(roomViewState)
            }
        }
    }


    override fun handleScreenShare(imageView: ImageView) {
        val tag = imageView.getTag(R.id.imageTag)
        if (!isBordcasting) {
            requestScreenCapturePermission()
            imageView.setTag(R.id.imageTag, -1)
        } else {
            roomViewModel.processInput(StopScreenCapture)
            imageView.setTag(R.id.imageTag, 1)
            isBordcasting = false
        }

    }

    private fun setUpThumbnail() {
        viewHolderEvents.observe(this, { viewEvent: RoomViewEvent ->
            roomViewModel.processInput(
                viewEvent
            )
        })
    }


    fun bind(participantViewState: ParticipantViewState, viewEventAction: (RoomViewEvent) -> Unit) {
        Timber.d("bind ParticipantViewHolder with data item: %s", participantViewState)
        Timber.d("thumb: %s", binding.room.participantThumbView)
        //  val localParticipantIdentity = getString(R.string.you)
        val localParticipantIdentity = studentName

        binding.room.participantThumbView.run {
            participantViewState.sid?.let { sid ->
                setOnClickListener {
//                    viewEventAction(PinParticipant(sid))
                }
            }
            val identity = if (participantViewState.isLocalParticipant)
                localParticipantIdentity else participantViewState.identity
            setIdentity(identity)
            setMuted(participantViewState.isMuted)
            setPinned(participantViewState.isPinned)
            updateVideoTrack(participantViewState)

            networkQualityLevelImg?.let {
                setNetworkQualityLevelImage(it, participantViewState.networkQualityLevel)
            }
        }
    }

    // slide the view from below itself to the current position
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0.toFloat(),  // fromXDelta
            0.toFloat(),  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0.toFloat(),  // fromXDelta
            0.toFloat(),  // toXDelta
            0.toFloat(),  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun onSlideViewButtonClick(view: View?) {
        if (!isConnected) return
        if (isUp) {
            slideDown(binding.videoControlLayout)
        } else {
            slideUp(binding.videoControlLayout)
            val someHandler = Handler(Looper.getMainLooper())
            someHandler.postDelayed(object : Runnable {
                override fun run() {
                    slideDown(binding.videoControlLayout)
                    isUp = !isUp
                }
            }, 5000)
        }
        isUp = !isUp
    }

    private fun updateVideoTrack(participantViewState: ParticipantViewState) {
        binding.room.participantThumbView.run {
            val videoTrackViewState = participantViewState.videoTrack
            val newVideoTrack = videoTrackViewState?.let { it.videoTrack }
            if (videoTrack !== newVideoTrack) {
                removeRender(videoTrack, this)
                videoTrack = newVideoTrack
                videoTrack?.let { videoTrack ->
                    setVideoState(videoTrackViewState)
                    if (videoTrack.isEnabled) videoTrack.addSink(this)
                } ?: setState(ParticipantView.State.NO_VIDEO)
            } else {
                setVideoState(videoTrackViewState)
            }
        }
    }

    private fun ParticipantThumbView.setVideoState(videoTrackViewState: VideoTrackViewState?) {
        if (videoTrackViewState?.let { it.isSwitchedOff } == true) {
            setState(ParticipantView.State.SWITCHED_OFF)
        } else {
            videoTrackViewState?.videoTrack?.let { setState(ParticipantView.State.VIDEO) }
                ?: setState(ParticipantView.State.NO_VIDEO)
        }
    }

    private fun removeRender(videoTrack: VideoTrack?, view: ParticipantView) {
        if (videoTrack == null || !videoTrack.sinks.contains(view)) return
        videoTrack.removeSink(view)
    }

    private fun setNetworkQualityLevelImage(
        networkQualityImage: ImageView,
        networkQualityLevel: NetworkQualityLevel?
    ) {
        when (networkQualityLevel) {
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ZERO -> R.drawable.network_quality_level_0
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ONE -> R.drawable.network_quality_level_1
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_TWO -> R.drawable.network_quality_level_2
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_THREE -> R.drawable.network_quality_level_3
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FOUR -> R.drawable.network_quality_level_4
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE -> R.drawable.network_quality_level_5
            else -> null
        }?.let { image ->
            networkQualityImage.visibility = View.VISIBLE
            networkQualityImage.setImageResource(image)
        } ?: run { networkQualityImage.visibility = View.GONE }
    }

    private fun renderThumbnails(roomViewState: RoomViewState) {
        val newThumbnails = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        val participantCount = roomViewState.participantThumbnails?.size ?: 0
        if (participantCount > 1) {
            binding.room.participantThumbView.visibility =
                if (currentLayoutMode == DEFAULT) View.VISIBLE else View.GONE
            val topTwoPartcipantList = newThumbnails?.subList(0, 2)
            val participantView = topTwoPartcipantList?.get(0)
            participantView?.let {
                bind(it) { mutableViewHolderEvents.value = it }
            }
        } else {
            binding.room.participantThumbView.visibility = View.GONE
        }
    }

    private fun bindRoomViewEffects(roomViewEffect: RoomViewEffect) {
        when (roomViewEffect) {
            is Connected -> {
                binding.meetingOption.visibility = View.VISIBLE
                binding.connectProgress.visibility = View.GONE
                binding.actionLayout.visibility = View.GONE
                binding.room.llBottom.visibility = View.GONE
                // binding.videoControlLayout.visibility = View.INVISIBLE
                binding.banner.visibility = View.GONE
                binding.switchCameraActionFab.visibility = View.VISIBLE
                toggleAudioDevice(true)
                isConnected = true

            }
            Disconnected -> {
                binding.meetingOption.visibility = View.GONE
                binding.connectProgress.visibility = View.GONE
                binding.actionLayout.visibility = View.VISIBLE
                binding.room.llBottom.visibility = View.VISIBLE
                binding.room.primaryVideo.performClick()
                binding.switchCameraActionFab.visibility = View.GONE
                localParticipantSid = LOCAL_PARTICIPANT_STUB_SID
                isConnected = false

                // TODO Update stats
                toggleAudioDevice(false)
            }
            ShowConnectFailureDialog, ShowMaxParticipantFailureDialog -> {
                AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                    .setTitle(getString(R.string.room_screen_connection_failure_title))
                    .setMessage(getConnectFailureMessage(roomViewEffect))
                    .setNeutralButton(getString(android.R.string.ok), null)
                    .show()
                isConnected = false
                toggleAudioDevice(false)
            }
            is ShowTokenErrorDialog -> {
                val error = roomViewEffect.serviceError
                isConnected = false
                handleTokenError(error)
            }
            PermissionsDenied -> requestPermissions()
        }
    }

    private fun getConnectFailureMessage(roomViewEffect: RoomViewEffect) =
        getString(
            when (roomViewEffect) {
                ShowMaxParticipantFailureDialog -> R.string.room_screen_max_participant_failure_message
                else -> R.string.room_screen_connection_failure_message
            }
        )

    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        val audioDeviceMenuIcon = when (selectedAudioDevice) {
            is BluetoothHeadset -> R.drawable.ic_bluetooth_white_24dp
            is WiredHeadset -> R.drawable.ic_headset_mic_white_24dp
            is Speakerphone -> R.drawable.ic_volume_up_white_24dp
            else -> R.drawable.ic_phonelink_ring_white_24dp
        }
    }

    private fun renderPrimaryView(primaryParticipant: ParticipantViewState) {
        primaryParticipant.run {
            primaryParticipantController.renderAsPrimary(
                if (isLocalParticipant) studentName else identity,
                screenTrack,
                videoTrack,
                isMuted,
                if (isLocalParticipant) true else isMirrored
            )
            binding.room.primaryVideo.showIdentityBadge(primaryParticipant.isLocalParticipant)
            binding.room.primaryVideo.setOnClickListener { onSlideViewButtonClick(binding.room.primaryVideo) }
        }
    }

    private fun renderDominantView(primaryParticipant: ParticipantViewState) {
        primaryParticipant.run {
            primaryParticipantController1.renderAsPrimary(
                if (isLocalParticipant) studentName else identity,
                screenTrack,
                videoTrack,
                isMuted,
                if (isLocalParticipant) true else isMirrored
            )
            binding.room.participantView.showIdentityBadge(!primaryParticipant.isLocalParticipant)
            binding.room.participantView.setOnClickListener { onSlideViewButtonClick(binding.room.primaryVideo) }

        }
    }


    private fun displayAudioDeviceList() {
        (roomViewModel.getState() as RoomViewState).let { viewState ->
            val selectedDevice = viewState.selectedDevice
            val audioDevices = viewState.availableAudioDevices
            if (selectedDevice != null && audioDevices != null) {
                val index = audioDevices.indexOf(selectedDevice)
                val audioDeviceNames = ArrayList<String>()
                for (a in audioDevices) {
                    audioDeviceNames.add(a.name)
                }
                createAudioDeviceDialog(
                    this,
                    index,
                    audioDeviceNames
                ) { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                    val viewEvent = SelectAudioDevice(audioDevices[i])
                    roomViewModel.processInput(viewEvent)
                }.show()
            }
        }
    }

    private fun createAudioDeviceDialog(
        activity: Activity,
        currentDevice: Int,
        availableDevices: ArrayList<String>,
        audioDeviceClickListener: (DialogInterface, Int) -> Unit
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
        builder.setTitle(activity.getString(R.string.room_screen_select_device))
        builder.setSingleChoiceItems(
            availableDevices.toTypedArray<CharSequence>(),
            currentDevice,
            audioDeviceClickListener
        )
        return builder.create()
    }

    private fun handleTokenError(error: AuthServiceError?) {
        val errorMessage =
            if (error === AuthServiceError.EXPIRED_PASSCODE_ERROR) R.string.room_screen_token_expired_message else R.string.room_screen_token_retrieval_failure_message
        AlertDialog.Builder(this, R.style.AppTheme_Dialog)
            .setTitle(getString(R.string.room_screen_connection_failure_title))
            .setMessage(getString(errorMessage))
            .setNeutralButton(getString(android.R.string.ok), null)
            .show()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
        private const val MEDIA_PROJECTION_REQUEST_CODE = 101
        private const val INTENT_EXTRA_ROOM_NAME = "intent_room_name"
        private const val INTENT_EXTRA_ROOM_ID = "intent_room_id"
        private const val INTENT_EXTRA_STUDENT_NAME = "intent_student_name"
        private const val INTENT_EXTRA_ROOM_CODE = "intent_room_code"
        private const val TYPE = "type"
        private const val USER_TYPE_STUDENT = 1
        private const val USER_TYPE_TEACHER = 2
        private const val LOCAL_PARTICIPANT_STUB_SID = ""
        var isActive: Boolean = false
        var isFirstTime: Boolean = true
        var isBordcasting: Boolean = false
        var isMute: Boolean = false

        fun startActivity(context: Context, appLink: Uri?) {
            val intent = Intent(context, RoomActivity::class.java)
            intent.data = appLink
            context.startActivity(intent)
        }


        fun open(
            currActivity: Context,
            roomName: String?,
            roomId: String?,
            studentName: String?,
            meetingCodeLocal: String?,
            type: String
        ) {
            currActivity.run {
                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra(INTENT_EXTRA_ROOM_NAME, roomName)
                intent.putExtra(INTENT_EXTRA_ROOM_ID, roomId)
                intent.putExtra(INTENT_EXTRA_STUDENT_NAME, studentName)
                intent.putExtra(INTENT_EXTRA_ROOM_CODE, meetingCodeLocal)
                intent.putExtra(TYPE, type)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null && imm.isActive) {
            if (currentFocus != null)
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }


    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        // Observe remote participant events
        remoteParticipant.setListener(remoteParticipantListener())
        for (remoteDataTrackPublication in remoteParticipant.remoteDataTracks) {
            /*
             * Data track messages are received on the thread that calls setListener. Post the
             * invocation of setting the listener onto our dedicated data track message thread.
*/
            if (remoteDataTrackPublication.isTrackSubscribed) {
                dataTrackMessageThreadHandler.post {
                    addRemoteDataTrack(
                        remoteParticipant,
                        remoteDataTrackPublication.remoteDataTrack
                    )
                }
            }
/*
            if (remoteDataTrackPublication.isTrackSubscribed) {
                addRemoteDataTrack(
                    remoteParticipant,
                    remoteDataTrackPublication.remoteDataTrack
                )

            }
*/


        }
    }


    private fun addRemoteDataTrack(
        remoteParticipant: RemoteParticipant, remoteDataTrack: RemoteDataTrack?
    ) {
        //dataTrackRemoteParticipantMap.put(remoteDataTrack, remoteParticipant)
        remoteDataTrack!!.setListener(remoteDataTrackListener())
    }

    private fun remoteParticipantListener(): RemoteParticipant.Listener? {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {
            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                /*
                 * Data track messages are received on the thread that calls setListener. Post the
                 * invocation of setting the listener onto our dedicated data track message thread.
                 */
                dataTrackMessageThreadHandler.post {
                    addRemoteDataTrack(
                        remoteParticipant,
                        remoteDataTrack
                    )
                }
/*
                addRemoteDataTrack(
                    remoteParticipant,
                    remoteDataTrack
                )
*/

            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {
            }

            override fun onVideoTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {
            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }
        }
    }

    private fun remoteDataTrackListener(): RemoteDataTrack.Listener? {
        return object : RemoteDataTrack.Listener {
            override fun onMessage(remoteDataTrack: RemoteDataTrack, byteBuffer: ByteBuffer) {}
            override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
                val splitMessage = message.split("_\$\$")
                splitMessage?.let {
                    if (it.size > 1) {
                        val sender = it[0]
                        val data = it[1]
                        if (sender == "yes") {
                            room?.disconnect()
                            connect()
                            handler!!.removeCallbacks(runnable!!)
                            isRunning = false
                        } else if (sender == "No") {

                            room?.disconnect()
                            AlertDialog.Builder(this@RoomActivity, R.style.AppTheme_Dialog)
                                .setTitle("Request Declined")
                                .setMessage("Your request to join the call has been denied.")
                                .setPositiveButton(getString(android.R.string.ok), null)
                                .show()
                            handler!!.removeCallbacks(runnable!!)
                            isRunning = false
                        }

                    }

                }

            }
        }
    }

    fun onSNACK(view: View) {
        //Snackbar(view)
        val snackbar = Snackbar.make(
            view, "Your request has been send to the teacher",
            Snackbar.LENGTH_LONG
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.GREEN)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 20f
        snackbar.show()
    }

    private fun getParticipantList(roomViewState: RoomViewState) {
        newThumbnailsList = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        participantIdentity.clear()
        if (roomViewState.configuration is RoomViewConfiguration.Connected) {
            if (newThumbnailsList != null)
                for (item in newThumbnailsList!!)
                    participantIdentity?.add(if (item.isLocalParticipant) studentName else item.identity.toString())

            aa.notifyDataSetChanged()


            /*if (binding.room.downSpiner.adapter.count >= bottomSelectPosition) {
                binding.room.downSpiner.setSelection(bottomSelectPosition, false)
            } else {
                binding.room.downSpiner.setSelection(0, false)
                bottomSelectPosition = 0
            }

            if (binding.room.coursesspinner.adapter.count >= bottomSelectPosition1) {
                binding.room.coursesspinner.setSelection(bottomSelectPosition1, false)
            } else {
                binding.room.coursesspinner.setSelection(bottomSelectPosition1, false)
                bottomSelectPosition1 = 0
            }*/
        }

    }
}