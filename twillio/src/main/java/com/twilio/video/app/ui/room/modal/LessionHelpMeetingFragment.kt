package com.twilio.video.app.ui.room.modal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.twilio.video.app.R
import com.twilio.video.app.ui.room.RoomViewModel
import com.twilio.video.app.util.ChatUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LessionDetailMeetingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LessionHelpMeetingFragment(val roomViewModel: RoomViewModel) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_lession_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvLink = view.findViewById<TextView>(R.id.visiting)
        val tvLinkCode = view.findViewById<TextView>(R.id.contact)
        tvLink.setOnClickListener {
            activity?.let {
                ChatUtils.openWebPage("https://full-aureus.cs117.force.com/aureusgrouphelpcenter/s/",it);
            }
        }
        tvLinkCode.setOnClickListener {
            activity?.let {
                ChatUtils.openWebPage("https://full-aureus.cs117.force.com/aureusgrouphelpcenter/s/",it);
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LessionDetailMeetingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(roomViewModel: RoomViewModel) =
            LessionHelpMeetingFragment(roomViewModel)

    }
}