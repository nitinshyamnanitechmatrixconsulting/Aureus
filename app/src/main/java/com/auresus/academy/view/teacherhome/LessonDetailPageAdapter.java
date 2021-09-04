package com.auresus.academy.view.teacherhome;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.auresus.academy.model.bean.TeacherBooking;

public class LessonDetailPageAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public TeacherBooking getTeacherBooking() {
        return teacherBooking;
    }

    public void setTeacherBooking(TeacherBooking teacherBooking) {
        this.teacherBooking = teacherBooking;
    }

    TeacherBooking teacherBooking;

    public LessonDetailPageAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }
    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OnlineLessonDetailsFragment onlineLe = OnlineLessonDetailsFragment.Companion.newInstance(teacherBooking);
                return onlineLe;
            case 1:
                LessonAddFileFragment settingsFragment = LessonAddFileFragment.Companion.newInstance(teacherBooking);
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