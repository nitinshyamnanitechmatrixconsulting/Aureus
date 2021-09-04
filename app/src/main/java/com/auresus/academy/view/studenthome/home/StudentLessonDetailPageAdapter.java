package com.auresus.academy.view.studenthome.home;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.auresus.academy.model.bean.Booking;

public class StudentLessonDetailPageAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public Booking getTeacherBooking() {
        return teacherBooking;
    }

    public void setTeacherBooking(Booking teacherBooking) {
        this.teacherBooking = teacherBooking;
    }

    Booking teacherBooking;

    public StudentLessonDetailPageAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (totalTabs == 1) {
                    StudentLessonAddFileFragment settingsFragment = StudentLessonAddFileFragment.Companion.newInstance(teacherBooking);
                    return settingsFragment;
                } else {
                    StudentOnlineLessonDetailsFragment onlineLe = StudentOnlineLessonDetailsFragment.Companion.newInstance(teacherBooking);
                    return onlineLe;
                }
            case 1:
                StudentLessonAddFileFragment settingsFragment = StudentLessonAddFileFragment.Companion.newInstance(teacherBooking);
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