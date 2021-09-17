package com.twilio.video.app.ui.room.modal

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import co.intentservice.chatui.models.ChatMessage
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.databinding.LayoutChatUiBinding
import com.twilio.video.app.databinding.LayoutMeetingOptionBinding
import com.twilio.video.app.participant.ParticipantViewState
import com.twilio.video.app.sdk.RoomManager
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.ui.room.ParticipantAdapter
import com.twilio.video.app.util.ChatUtils
import com.twilio.video.app.util.PermissionUtil
import java.util.ArrayList
import javax.inject.Inject


class ChatMessageFragment(val activity: Activity, val roomViewModel: RoomViewModel) : Fragment() {

    private lateinit var binding: LayoutChatUiBinding


    companion object {
        private val TAG: String = "ChatMessageFragment"
        private var instance: ChatMessageFragment? = null

        @JvmStatic
        fun openChat(activity: FragmentActivity, roomViewModel: RoomViewModel) {
            val instance = ChatMessageFragment(activity, roomViewModel)
            activity.supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_in_down,
                    R.anim.slide_out_down,
                    R.anim.slide_out_up
                );
                replace(R.id.container, instance, ChatMessageFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutChatUiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomViewModel.getMessageListLiveData().observe(viewLifecycleOwner, Observer {
            if (it != null && it.size > 0) {
                // binding.chatView.clearMessages()
                //  binding.chatView.addMessages(it as ArrayList<ChatMessage>?)
                addMessages(it as ArrayList<ChatMessage?>);
            }
        })

        roomViewModel.getMessageLiveData().observe(viewLifecycleOwner, Observer {
            addMessage(it)
        })


        binding.rlSend.setOnClickListener(View.OnClickListener { view ->

            if (!TextUtils.isEmpty(binding.etMessage.text.toString())) {
                sendChatMessage(binding.etMessage.text.toString())
            }
            // Do some work here
        })
        binding.toolbarJoinLesson.backButton.setOnClickListener(View.OnClickListener { view ->
            getActivity()?.onBackPressed()
            // Do some work here
        })
        binding.chatView.setOnSentMessageListener {
            sendChatMessage(binding.chatView?.typedMessage)
            binding.chatView.inputEditText.setText("")
            false
        }



        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    val identity = roomViewModel.getIdentity()
                    val formattedMessage = MessageCommand.typingMessage(identity!!)
                    roomViewModel.processInput(RoomViewEvent.SendMessage(formattedMessage))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun sendChatMessage(message: String?) {
        message?.let {
            val identity = roomViewModel.getIdentity()
            identity?.let {
                val chatMessage = ChatMessage(
                    message,
                    System.currentTimeMillis(),
                    ChatMessage.Type.SENT, identity
                )
                roomViewModel.addChatMessage(chatMessage)
                val formattedMessage = MessageCommand.chatMessage(message, it)
                roomViewModel.processInput(RoomViewEvent.SendMessage(formattedMessage))
                binding.etMessage.setText("")
            }
        }
    }

    fun addMessages(messages: ArrayList<ChatMessage?>?) {
        val adapter = ChatAdapter(context, messages);

        // Setting the Adapter with the recyclerview
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager!!.scrollToPosition(
            binding.rvList.adapter!!.itemCount - 1
        )

    }

    @SuppressLint("SetTextI18n")
    private fun addMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            val messageType = ChatUtils.getMessageType(message)
            when (messageType) {
                ChatUtils.MessageType.TYPING -> {
                    val splitMessage = message.split("_\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val sender = it[1]
                            if (sender.equals(roomViewModel.name, ignoreCase = true)) {
                                binding.tvTyping.visibility = View.VISIBLE
                                binding.tvTyping.text = sender + "is Typing..."
                            }
                        }
                    }
                }
            }
        }
    }

}
