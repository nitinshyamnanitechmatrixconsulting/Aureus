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

package com.twilio.video.app.ui.room;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.twilio.video.app.R;
import com.twilio.video.app.databinding.ParticipantBottomViewBinding;
import com.twilio.video.app.databinding.ParticipantViewBinding;
import com.twilio.video.app.helper.ShortName;
import com.twilio.video.app.helper.StringHelper;

public class ParticipantBottomThumbView extends ParticipantView {
    private ParticipantBottomViewBinding binding;

    public ParticipantBottomThumbView(Context context) {
        super(context);
        init(context);
    }

    public ParticipantBottomThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParticipantBottomThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParticipantBottomThumbView(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void setIdentity(String identity) {
        try {
            Log.e("identity", identity + "...");
            binding.identityTextView.setText(identity);
            binding.tvShortName.setText(identity.substring(0, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(Context context) {
        binding = ParticipantBottomViewBinding.inflate(LayoutInflater.from(context), this, true);
        videoLayout = binding.videoLayout;
        videoIdentity = binding.videoIdentity;
        videoView = binding.video;
        selectedLayout = binding.selectedLayout;
        // stubImage = binding.stub;
        tvShortName = binding.tvShortName;
        //rlThumbView = binding.rlThumbView;
        networkQualityLevelImg = binding.networkQuality;
        selectedIdentity = binding.selectedIdentity;
        audioToggle = binding.audioToggle;
        pinImage = binding.pin;
        setIdentity(identity);
        setState(state);
        setMirror(mirror);
        setScaleType(scaleType);
    }

    @Override
    public void setState(int state) {
        super.setState(state);

        binding.participantTrackSwitchOffBackground.setVisibility(isSwitchOffViewVisible(state));
        binding.participantTrackSwitchOffIcon.setVisibility(isSwitchOffViewVisible(state));

        int resId = R.drawable.participant_background;
        if (state == State.SELECTED) {
            resId = R.drawable.participant_selected_background;
        }
        selectedLayout.setBackground(ContextCompat.getDrawable(getContext(), resId));
    }

    private int isSwitchOffViewVisible(int state) {
        return state == State.SWITCHED_OFF ? View.VISIBLE : View.GONE;
    }
}
