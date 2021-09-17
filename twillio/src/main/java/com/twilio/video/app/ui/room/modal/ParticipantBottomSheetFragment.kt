package com.twilio.video.app.ui.room.modal

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.twilio.video.app.R
import com.twilio.video.app.databinding.FragmentParticipantListBinding
import com.twilio.video.app.ui.room.*

class ParticipantBottomSheetFragment(val activity: Activity, val roomViewModel: RoomViewModel) : SuperBottomSheetFragment() {

    private var participantAdapter: ParticipantListAdapter? = null
    private lateinit var binding: FragmentParticipantListBinding

    companion object {
        private var instance: ParticipantBottomSheetFragment? = null
        const val TAG ="ParticipantBottomSheetFragment"
        @JvmStatic
        fun openParticipantList(activity: FragmentActivity,roomViewModel: RoomViewModel) {
            if (instance == null) {
                instance = ParticipantBottomSheetFragment(activity, roomViewModel)
            }
            instance?.show(activity.supportFragmentManager, TAG )
        }
    }

    private fun setupThumbnailRecyclerView() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.participantList.layoutManager = layoutManager
        binding.toolbar.toolbarTitle.setText(getString(R.string.participant))
        binding.toolbar.backButton.setOnClickListener { dismiss() }
        participantAdapter = ParticipantListAdapter(roomViewModel)
        participantAdapter?.viewHolderEvents?.observe(
                this, { viewEvent: RoomViewEvent -> roomViewModel.processInput(viewEvent) })
        binding.participantList.adapter = participantAdapter
    }

    fun renderThumbnails(roomViewState: RoomViewState) {
        val newThumbnails = if (roomViewState.configuration is RoomViewConfiguration.Connected)
            roomViewState.participantThumbnails else null
        participantAdapter?.submitList(newThumbnails)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentParticipantListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThumbnailRecyclerView()

    }

    override fun getCornerRadius() =
        activity.resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED

    override fun getBackgroundColor()=activity.resources.getColor(R.color.color_353b3e)

}
