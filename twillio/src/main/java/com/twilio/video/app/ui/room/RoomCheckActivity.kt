package com.twilio.video.app.ui.room

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import co.intentservice.chatui.ChatView
import com.bumptech.glide.Glide
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.gdacciaro.iOSDialog.iOSDialogClickListener
import com.github.javiersantos.bottomdialogs.BottomDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.NetworkQualityLevel
import com.twilio.video.VideoTrack
import com.twilio.video.app.R
import com.twilio.video.app.base.BaseActivity
import com.twilio.video.app.data.api.AuthServiceError
import com.twilio.video.app.data.api.TokenService
import com.twilio.video.app.databinding.ActivityRoomCheckBinding
import com.twilio.video.app.helper.StringHelper
import com.twilio.video.app.participant.ParticipantViewState
import com.twilio.video.app.sdk.RoomManager
import com.twilio.video.app.sdk.VideoTrackViewState
import com.twilio.video.app.ui.LessonAddFileFragment
import com.twilio.video.app.ui.room.modal.*
import com.twilio.video.app.util.ChatUtils
import com.twilio.video.app.util.PermissionUtil
import io.uniflow.android.livedata.onEvents
import io.uniflow.android.livedata.onStates
import timber.log.Timber
import javax.inject.Inject

class RoomCheckActivity : BaseActivity() , MeettingOptionHandler {
    private lateinit var binding: ActivityRoomCheckBinding
    private var isConnected: Boolean = false
    private var participantCount: Int = 0
    private var isUp: Boolean = false
    private var roomID: String = ""
    private var roomCode: String = ""
    private lateinit var participantAdapter: ParticipantAdapter
    private var bottomDialog: BottomDialog? = null

    private var savedVolumeControlStream = 0
    private var displayName: String? = null
    private var localParticipantSid = LOCAL_PARTICIPANT_STUB_SID
    private var roomName: String = ""
    private var studentName: String = ""
    private var chatView: ChatView? = null
    private var name: String = ""
    private var currentLayoutMode = LayoutOptionSheetFragment.DEFAULT
    private var currentUserType =USER_TYPE_TEACHER
    private val mutableViewHolderEvents = MutableLiveData<RoomViewEvent>()
    private val viewHolderEvents: LiveData<RoomViewEvent> = mutableViewHolderEvents



    @Inject
    lateinit var tokenService: TokenService

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var roomManager: RoomManager

    @Inject
    lateinit var audioSwitch: AudioSwitch

    /** Coordinates participant thumbs and primary participant rendering.  */
    private lateinit var primaryParticipantController: PrimaryParticipantController
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var recordingAnimation: ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.disconnect.setOnClickListener {
            disconnectButtonClick()
            try {
                val myIntent = Intent(
                    this,
                    Class.forName("com.auresus.academy.view.feedback.FeedbackActivity")
                )
                myIntent.putExtra("roomName", roomName)
                myIntent.putExtra("roomId", roomID)
                myIntent.putExtra("studentName", studentName)
                myIntent.putExtra("meetingCodeLocal", roomCode)
                startActivity(myIntent)
                finish()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

        }
        binding.localVideo.setOnClickListener { toggleLocalVideo() }
        binding.localAudio.setOnClickListener { toggleLocalAudio() }
        binding.buttonJoinOnlineStudio.setOnClickListener {
            connect()
        }
        binding.meetingOption.setOnClickListener { openMeetingOption() }
        binding.room.localAudio1.setOnClickListener { toggleLocalAudio() }
        binding.room.localVideo1.setOnClickListener { toggleLocalVideo() }
        Glide.with(this).load(R.raw.loding).into(this.binding.joinProgressLoader)


        binding.switchCameraActionFab.setOnClickListener { roomViewModel.processInput(RoomViewEvent.SwitchCamera) }
        val factory =
            RoomViewModel.RoomViewModelFactory(roomManager, audioSwitch, PermissionUtil(this))
        roomViewModel = ViewModelProvider(this, factory).get(RoomViewModel::class.java)
        roomViewModel.getMessageLiveData().observe(this, Observer {
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

        setupRecordingAnimation()
        setUpThumbnail()


        onStates(roomViewModel) { state ->
            if (state is RoomViewState) bindRoomViewState(state)
        }
        onEvents(roomViewModel) { event ->
            if (event is RoomViewEffect) bindRoomViewEffects(event)
        }
    }

    private fun connect() {
        val viewEvent = RoomViewEvent.Connect(displayName ?: "", roomName)
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
        iOSDialogBuilder(this@RoomCheckActivity)
            .setTitle(getString(R.string.aureus))
            .setSubtitle(String.format(getString(R.string.wants_to_join), identitiy))
            .setBoldPositiveLabel(true)
            .setCancelable(true)
            .setPositiveListener(getString(R.string.admit), object : iOSDialogClickListener {
                override fun onClick(dialog: iOSDialog) {
                    dialog.run {
                        val admitMessage = MessageCommand.sendAdmitJoinRoom()
                        roomViewModel.processInput(RoomViewEvent.SendMessage(admitMessage))
                        dismiss()
                    }
                }
            })
            .setNegativeListener(getString(R.string.deny),
                object : iOSDialogClickListener {
                    override fun onClick(dialog: iOSDialog) {
                        dialog.run {
                            val denyMessage = MessageCommand.sendDenyJoinRoom()
                            roomViewModel.processInput(RoomViewEvent.SendMessage(denyMessage))
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
        roomViewModel.processInput(RoomViewEvent.OnResume)
    }

    override fun onPause() {
        super.onPause()
        roomViewModel.processInput(RoomViewEvent.OnPause)
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
        listView.setAdapter(arrayAdapter)
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
                roomViewModel.processInput(RoomViewEvent.OnResume)
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflaterf
//        inflater.inflate(R.menu.room_menu, menu)
//        onStates(roomViewModel) { state ->
//            if (state is RoomViewState) bindRoomViewState(state)
//        }
//        onEvents(roomViewModel) { event ->
//            if (event is RoomViewEffect) bindRoomViewEffects(event)
//        }
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.switch_camera_menu_item -> {
//                roomViewModel.processInput(SwitchCamera)
//                true
//            }
//            R.id.share_screen_menu_item -> {
//                openMeetingOption()
////                if (item.title == getString(R.string.share_screen)) {
////                    requestScreenCapturePermission()
////                } else {
////                    roomViewModel.processInput(StopScreenCapture)
////                }
//                true
//            }
//            R.id.device_menu_item -> {
//                displayAudioDeviceList()
//                true
//            }
//            R.id.pause_audio_menu_item -> {
//                if (item.title == getString(R.string.pause_audio))
//                    roomViewModel.processInput(DisableLocalAudio)
//                else
//                    roomViewModel.processInput(EnableLocalAudio)
//                true
//            }
//            R.id.pause_video_menu_item -> {
//                if (item.title == getString(R.string.pause_video))
//                    roomViewModel.processInput(DisableLocalVideo)
//                else
//                    roomViewModel.processInput(EnableLocalVideo)
//                true
//            }
//            R.id.settings_menu_item -> {
//                openMeetingOption()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    private fun openMeetingOption() {
        if (!isFinishing)
            MeetingOptionBottomSheetFragment.openMeetingOption(this, this, roomViewModel)
    }

    override fun handleShowMessages() {
        if (!isFinishing)
            ChatMessageFragment.openChat(this, roomViewModel)
    }


    override fun handleShowLayoutOption() {
        if (!isFinishing)
            LayoutOptionSheetFragment.openMeetingOption(this, this, roomViewModel)
    }

    override fun switchLayout(option: Int) {
        if (participantCount <= 1) return
        currentLayoutMode = option
        when (option) {

            LayoutOptionSheetFragment.SPLIT -> {
                binding.room.participantView.visibility = View.VISIBLE
                binding.room.participantThumbView.visibility = View.GONE
            }
            LayoutOptionSheetFragment.DEFAULT -> {
                binding.room.participantThumbView.visibility = View.VISIBLE
                binding.room.participantView.visibility = View.GONE
            }
        }
    }

    override fun handleShowInfo() {
        if (!isFinishing)
            LessionInfoSheetFragment.open(this, this, roomViewModel)
    }

    override fun handleOpenFiles() {
        val instance = LessonAddFileFragment.newInstance(roomName, roomID, roomViewModel)
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_out_up
            );
            replace(R.id.container, instance, LessonAddFileFragment.TAG)
            addToBackStack(null)
        }
    }

    override fun handleOpenShowParticipants() {
        if (!isFinishing)
            ParticipantBottomSheetFragment.openParticipantList(this, roomViewModel)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                roomViewModel.processInput(RoomViewEvent.StartScreenCapture(resultCode, data))
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count >= 1) {
            supportFragmentManager.popBackStack()
        } else if (binding.actionLayout.visibility == View.VISIBLE) {
            super.onBackPressed()
        } else {
            // super.onBackPressed()
            /*   roomViewModel.processInput(Disconnect)
               try {
                   val myIntent = Intent(
                       this,
                       Class.forName("com.auresus.academy.view.feedback.FeedbackActivity")
                   )
                   myIntent.putExtra("roomName", roomName)
                   myIntent.putExtra("roomId", roomID)
                   myIntent.putExtra("studentName", studentName)
                   myIntent.putExtra("meetingCodeLocal", roomCode)
                   startActivity(myIntent)
               } catch (e: ClassNotFoundException) {
                   e.printStackTrace()
               }*/

            // FeedbackActivity.open(this,roomName ,roomID,name,roomCode)
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
        displayName = studentName
        roomViewModel.setIdentity(displayName)
        roomViewModel.roomName = roomName
        roomViewModel.roomId = roomID
        roomViewModel.roomCode = roomCode
        return true
    }


    private fun disconnectButtonClick() {
        roomViewModel.processInput(RoomViewEvent.Disconnect)
        // TODO Handle screen share
    }

    private fun toggleLocalVideo() {
        roomViewModel.processInput(RoomViewEvent.ToggleLocalVideo)
    }

    private fun toggleLocalAudio() {
        roomViewModel.processInput(RoomViewEvent.ToggleLocalAudio)
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
            RoomViewConfiguration.Connecting -> {
                binding.connectProgress.visibility = View.VISIBLE
                disconnectButtonState = View.VISIBLE
            }
            RoomViewConfiguration.Connected -> {
                disconnectButtonState = View.VISIBLE
                binding.meetingOption.visibility = View.VISIBLE
                binding.switchCameraActionFab.visibility = View.VISIBLE
                binding.room.llBottom.visibility = View.GONE
                roomName = roomViewState.title
                toolbarTitle = roomName
            }
        }
        // binding.room.llBottom.visibility = View.VISIBLE
        binding.videoControlLayout.visibility = View.GONE
        val isMicEnabled = roomViewState.isMicEnabled
        val isCameraEnabled = roomViewState.isCameraEnabled
        val isLocalMediaEnabled = isMicEnabled && isCameraEnabled
        binding.localAudio.isEnabled = isLocalMediaEnabled
        binding.localVideo.isEnabled = isLocalMediaEnabled
        val micDrawable =
            if (roomViewState.isAudioMuted || !isLocalMediaEnabled) R.drawable.microphone_off else R.drawable.micropfone_on
        val videoDrawable =
            if (roomViewState.isVideoOff || !isLocalMediaEnabled) R.drawable.video_off else R.drawable.video_on
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

                }
                ChatUtils.MessageType.JOIN_ROOM_DENIED -> {

                }
                ChatUtils.MessageType.NEW_JOIN -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val sender = it[1]
                            openGuestAdmitDenyDialog(sender)
                        }
                    }
                }
                ChatUtils.MessageType.MUTE_PARTICIPANT -> {
                    toggleLocalAudio()
                }
                ChatUtils.MessageType.REMOVE_ALL -> {
                    disconnectButtonClick()
                }
                ChatUtils.MessageType.REMOVE_PARTICIPANT -> {
                    disconnectButtonClick()
                }
                ChatUtils.MessageType.TYPING -> {

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
        val viewEvent = if (enableAudioDevice) RoomViewEvent.ActivateAudioDevice else RoomViewEvent.DeactivateAudioDevice
        roomViewModel.processInput(viewEvent)
    }

    private fun bindRoomViewState(roomViewState: RoomViewState) {
        updateParticipantList(roomViewState)
        updateLayout(roomViewState)
        updateAudioDeviceIcon(roomViewState.selectedDevice)
    }

    private fun updateParticipantList(roomViewState: RoomViewState) {
        val fragment =
            supportFragmentManager.findFragmentByTag(ParticipantBottomSheetFragment.TAG) as? ParticipantBottomSheetFragment
        fragment?.renderThumbnails(roomViewState)
        renderPrimaryView(roomViewState.primaryParticipant)
        val newThumbnails = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        participantCount = roomViewState.participantThumbnails?.size ?: 0
        when (currentLayoutMode) {
            LayoutOptionSheetFragment.SPLIT -> {
                binding.room.participantThumbView.visibility = View.GONE
                if (participantCount > 1) {
                    binding.room.participantThumbView.visibility = View.VISIBLE
                    val topTwoPartcipantList = newThumbnails?.subList(0, 2)
                    val participantView = topTwoPartcipantList?.get(1)
                    participantView?.let {
                        renderDominantView(it)
                    }
                }

            }
            LayoutOptionSheetFragment.DEFAULT -> {
                binding.room.participantThumbView.visibility = View.VISIBLE
                renderPrimaryView(roomViewState.primaryParticipant)
                renderThumbnails(roomViewState)
            }
        }
    }

    override fun handleScreenShare(imageView: ImageView) {
        val tag = imageView.getTag(R.id.imageTag)
        if (tag == 1) {
            requestScreenCapturePermission()
            imageView.setTag(R.id.imageTag, -1)
        } else {
            roomViewModel.processInput(RoomViewEvent.StopScreenCapture)
            imageView.setTag(R.id.imageTag, 1)

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
        val localParticipantIdentity = getString(R.string.you)

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
        animate.setDuration(500)
        animate.setFillAfter(true)
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
        animate.setDuration(500)
        animate.setFillAfter(true)
        view.startAnimation(animate)
    }

    fun onSlideViewButtonClick(view: View?) {
        if (!isConnected) return
        if (isUp) {
            slideDown(binding.videoControlLayout)
        } else {
            slideUp(binding.videoControlLayout)
        }
        isUp = !isUp
    }

    fun updateVideoTrack(participantViewState: ParticipantViewState) {
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

    fun renderThumbnails(roomViewState: RoomViewState) {
        val newThumbnails = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        val participantCount = roomViewState.participantThumbnails?.size ?: 0
        if (participantCount > 1) {
            binding.room.participantThumbView.visibility =
                if (currentLayoutMode == LayoutOptionSheetFragment.DEFAULT) View.VISIBLE else View.GONE
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
            is RoomViewEffect.Connected -> {
                binding.meetingOption.visibility = View.VISIBLE
                binding.connectProgress.visibility = View.GONE
                binding.actionLayout.visibility = View.GONE
                binding.room.llBottom.visibility = View.GONE
                binding.videoControlLayout.visibility = View.INVISIBLE
                binding.banner.visibility = View.GONE
                binding.switchCameraActionFab.visibility = View.VISIBLE
                toggleAudioDevice(true)
                isConnected = true
            }
            RoomViewEffect.Disconnected -> {
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
            RoomViewEffect.ShowConnectFailureDialog, RoomViewEffect.ShowMaxParticipantFailureDialog -> {
                AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                    .setTitle(getString(R.string.room_screen_connection_failure_title))
                    .setMessage(getConnectFailureMessage(roomViewEffect))
                    .setNeutralButton(getString(android.R.string.ok), null)
                    .show()
                isConnected = false
                toggleAudioDevice(false)
            }
            is RoomViewEffect.ShowTokenErrorDialog -> {
                val error = roomViewEffect.serviceError
                isConnected = false
                handleTokenError(error)
            }
            RoomViewEffect.PermissionsDenied -> requestPermissions()
        }
    }

    private fun getConnectFailureMessage(roomViewEffect: RoomViewEffect) =
        getString(
            when (roomViewEffect) {
                RoomViewEffect.ShowMaxParticipantFailureDialog -> R.string.room_screen_max_participant_failure_message
                else -> R.string.room_screen_connection_failure_message
            }
        )

    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        val audioDeviceMenuIcon = when (selectedAudioDevice) {
            is AudioDevice.BluetoothHeadset -> R.drawable.ic_bluetooth_white_24dp
            is AudioDevice.WiredHeadset -> R.drawable.ic_headset_mic_white_24dp
            is AudioDevice.Speakerphone -> R.drawable.ic_volume_up_white_24dp
            else -> R.drawable.ic_phonelink_ring_white_24dp
        }
//        this.deviceMenuItem.setIcon(audioDeviceMenuIcon)
    }

    private fun renderPrimaryView(primaryParticipant: ParticipantViewState) {
        primaryParticipant.run {
            primaryParticipantController.renderAsPrimary(
                if (isLocalParticipant) getString(R.string.you) else identity,
                screenTrack,
                videoTrack,
                isMuted,
                isMirrored
            )
            binding.room.primaryVideo.showIdentityBadge(!primaryParticipant.isLocalParticipant)
            binding.room.primaryVideo.setOnClickListener { onSlideViewButtonClick(binding.room.primaryVideo) }
        }
    }

    private fun renderDominantView(primaryParticipant: ParticipantViewState) {
        primaryParticipant.run {
            primaryParticipantController.renderAsPrimary(
                if (isLocalParticipant) getString(R.string.you) else identity,
                screenTrack,
                videoTrack,
                isMuted,
                isMirrored
            )
            binding.room.participantView.showIdentityBadge(!primaryParticipant.isLocalParticipant)

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
                    val viewEvent = RoomViewEvent.SelectAudioDevice(audioDevices[i])
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
        private const val USER_TYPE_STUDENT = 1
        private const val USER_TYPE_TEACHER = 2
        private const val LOCAL_PARTICIPANT_STUB_SID = ""
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
            meetingCodeLocal: String?
        ) {
            currActivity.run {
                val intent = Intent(this, RoomCheckActivity::class.java)
                intent.putExtra(INTENT_EXTRA_ROOM_NAME, roomName)
                intent.putExtra(INTENT_EXTRA_ROOM_ID, roomId)
                intent.putExtra(INTENT_EXTRA_STUDENT_NAME, studentName)
                intent.putExtra(INTENT_EXTRA_ROOM_CODE, meetingCodeLocal)
                startActivity(intent)
            }
        }
    }
}