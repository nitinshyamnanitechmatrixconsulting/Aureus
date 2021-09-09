package com.twilio.video.app.ui.room.modal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.twilio.video.app.databinding.LayoutChatRowBinding;

import java.util.ArrayList;

import co.intentservice.chatui.models.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<ChatMessage> dataList;
    Context context;

    public ChatAdapter(Context context, ArrayList<ChatMessage> messge) {
        dataList = messge;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutChatRowBinding binding = LayoutChatRowBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        ChatMessage chatMessage = dataList.get(position);
        if (chatMessage.getType().ordinal()==0) {
            holder.binding.rightlayout.setVisibility(View.VISIBLE);
            holder.binding.leftlayout.setVisibility(View.GONE);
            holder.binding.txtMessage.setText(chatMessage.getMessage());
            holder.binding.txtDate.setText(chatMessage.getFormattedTime());
            holder.binding.txtNameRight.setText(chatMessage.getSender());

            try {
                String inital = chatMessage.getSender().substring(0, 1);
                holder.binding.tvInitRight.setText(inital);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            holder.binding.leftlayout.setVisibility(View.VISIBLE);
            holder.binding.rightlayout.setVisibility(View.GONE);
            holder.binding.txtMessageleft.setText(chatMessage.getMessage());
            holder.binding.txtDateleft.setText(chatMessage.getFormattedTime());
            holder.binding.txtNameleft.setText(chatMessage.getSender());
            try {
                String inital = chatMessage.getSender().substring(0, 1);
                holder.binding.tvInitLeft.setText(inital);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LayoutChatRowBinding binding;

        public ViewHolder(LayoutChatRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}