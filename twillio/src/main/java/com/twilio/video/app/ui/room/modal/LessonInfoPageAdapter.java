package com.twilio.video.app.ui.room.modal;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.twilio.video.app.ui.room.RoomViewModel;


public class LessonInfoPageAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    private RoomViewModel viewModel;


    public LessonInfoPageAdapter(Context context, FragmentManager fm, int totalTabs, RoomViewModel roomViewModel) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
        this.viewModel = roomViewModel;
    }
    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LessionDetailMeetingFragment onlineLe = LessionDetailMeetingFragment.Companion.newInstance(viewModel);
                return onlineLe;
            case 1:
                LessionHelpMeetingFragment settingsFragment = LessionHelpMeetingFragment.Companion.newInstance(viewModel);
                return settingsFragment;

            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}