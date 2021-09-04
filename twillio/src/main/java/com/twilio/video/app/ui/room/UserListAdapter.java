package com.twilio.video.app.ui.room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twilio.video.app.R;
import com.twilio.video.app.participant.ParticipantViewState;

import java.util.List;


public class UserListAdapter extends ArrayAdapter<ParticipantViewState> {
    private final Context context;
    private final List<ParticipantViewState> userList;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<ParticipantViewState> objects) {
        super(context, resource, objects);
        userList = objects;
        this.context = context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_assignment_dialog_list_layout, parent, false);
        TextView userName = rowView.findViewById(R.id.tv_user_name);
        ParticipantViewState user = userList.get(position);
        userName.setText(user.getIdentity());
        return rowView;
    }

}