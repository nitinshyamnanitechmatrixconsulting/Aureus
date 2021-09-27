package com.twilio.video.app.ui.room.modal

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import co.intentservice.chatui.models.ChatMessage
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.twilio.video.app.R
import com.twilio.video.app.databinding.LayoutChatUiBinding
import com.twilio.video.app.ui.room.*
import com.twilio.video.app.util.ChatUtils
import java.util.ArrayList


class ChatMessageFragment(
    val activity: Activity,
    val roomViewModel: RoomViewModel

    ) : Fragment() {

    private lateinit var binding: LayoutChatUiBinding
    var mediaPlayer: MediaPlayer? = null

    companion object {
        private val TAG: String = "ChatMessageFragment"
        private var instance: ChatMessageFragment? = null
        var tvTyping: TextView? = null

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
        tvTyping = binding.tvTyping

/*
        roomViewModel.getMessageLiveData().observe(viewLifecycleOwner, Observer {
            addMessage(it)
        })
*/

        binding.toolbarJoinLesson.llMain.setBackgroundColor(activity.resources.getColor(R.color.white))
        binding.toolbarJoinLesson.toolbarTitle.setTextColor(activity.resources.getColor(R.color.black))
        binding.toolbarJoinLesson.backButton.setColorFilter(activity.resources.getColor(R.color.black))

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
                    val identity = roomViewModel.name
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

    @SuppressLint("SetTextI18n")
    fun addMessages(messages: ArrayList<ChatMessage?>?) {
        val adapter = ChatAdapter(context, messages);

        // Setting the Adapter with the recyclerview
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager!!.scrollToPosition(
            binding.rvList.adapter!!.itemCount - 1
        )
        /* val messageType = ChatUtils.getMessageType(messages!![0]!!.sender!!)
         when (messageType) {
             ChatUtils.MessageType.TYPING -> {
                 val splitMessage = messages!![0]!!.sender!!.split("_\$\$\$typing")
                 splitMessage.let {
                     if (it.size > 1) {
                         val sender = it[1]
                         binding.tvTyping.text = sender + "is Typing..."
                     } else {
                         binding.tvTyping.text = " "
                     }
                 }
             }
         }*/
        val chatMessageFragment: ChatMessageFragment =
            childFragmentManager.findFragmentByTag("ChatMessageFragment") as ChatMessageFragment
        if (chatMessageFragment != null && chatMessageFragment.isVisible) {

        } else {
            mediaPlayer = MediaPlayer.create(activity, R.raw.newmessage);
            mediaPlayer!!.start()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun addMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            val messageType = ChatUtils.getMessageType(message)
            when (messageType) {
                ChatUtils.MessageType.TYPING -> {
                    val splitMessage = message.split("_\$\$\$")
                    splitMessage.let {
                        if (it.size > 1) {
                            val sender = it[1]
                            binding.tvTyping.visibility = View.VISIBLE
                            binding.tvTyping.text = "$sender is Typing..."
                        } else {
                            binding.tvTyping.visibility = View.GONE
                        }

                    }
                }
            }
        }
    }

}
