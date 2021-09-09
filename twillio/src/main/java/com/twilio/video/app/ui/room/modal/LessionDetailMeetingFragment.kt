package com.twilio.video.app.ui.room.modal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import com.twilio.video.app.R
import com.twilio.video.app.ui.room.RoomViewModel
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LessionDetailMeetingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LessionDetailMeetingFragment(val roomViewModel: RoomViewModel) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_lession_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomName = roomViewModel.roomName
        val roomId = roomViewModel.roomCode
        val link =  "https://aureusacademy-meeting.herokuapp.com/?room_name=${roomName}"
        val tvLink = view.findViewById<TextView>(R.id.roomLink)
        val tvLinkCode = view.findViewById<TextView>(R.id.tvJoiningCode)
        tvLinkCode.setText(roomId)
        tvLink.setText(link)
        tvLink.setOnClickListener {


        }
        view.findViewById<Button>(R.id.buttonShareLinkInfo).setOnClickListener {
            if (activity != null && (!(activity as FragmentActivity).isFinishing)) {
                ShareCompat.IntentBuilder.from(activity as FragmentActivity)
                    .setType("text/plain")
                    .setText(link+"&password="+roomId)
                    .startChooser();
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
            LessionDetailMeetingFragment(roomViewModel)
    }

}