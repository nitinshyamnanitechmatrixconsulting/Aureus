package com.twilio.video.app.ui.room

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.twilio.video.app.participant.ParticipantViewState

internal class ParticipantListAdapter(var roomViewModel: RoomViewModel) :
    ListAdapter<ParticipantViewState, BottomParticipantViewHolder>(
        ParticipantDiffCallback()
    ) {

    private val mutableViewHolderEvents = MutableLiveData<RoomViewEvent>()
    val viewHolderEvents: LiveData<RoomViewEvent> = mutableViewHolderEvents


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomParticipantViewHolder =BottomParticipantViewHolder(
        ParticipantBottomThumbView(parent.context),roomViewModel
    )


    override fun onBindViewHolder(holder: BottomParticipantViewHolder, position: Int) =
        holder.bind(getItem(position)) { mutableViewHolderEvents.value = it }

    class ParticipantDiffCallback : DiffUtil.ItemCallback<ParticipantViewState>() {
        override fun areItemsTheSame(
            oldItem: ParticipantViewState,
            newItem: ParticipantViewState
        ): Boolean =
            oldItem.sid == newItem.sid

        override fun areContentsTheSame(
            oldItem: ParticipantViewState,
            newItem: ParticipantViewState
        ): Boolean =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: ParticipantViewState,
            newItem: ParticipantViewState
        ): Any? {
            return newItem
        }
    }
}
