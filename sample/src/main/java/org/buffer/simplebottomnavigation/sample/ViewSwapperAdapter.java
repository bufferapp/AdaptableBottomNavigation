package org.buffer.simplebottomnavigation.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.buffer.simplebottomnavigation.adapter.FragmentStateAdapter;

public class ViewSwapperAdapter extends FragmentStateAdapter {

    private static final int INDEX_CONTENT = 0;
    private static final int INDEX_ANALYTICS = 1;
    private static final int INDEX_SCHEDULES = 2;

    public ViewSwapperAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INDEX_CONTENT:
                return TextFragment.newInstance("First one!");
            case INDEX_ANALYTICS:
                return TextFragment.newInstance("Here's another");
            case INDEX_SCHEDULES:
                return TextFragment.newInstance("Three!");
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getViewTitle(int position) {
        switch (position) {
            case INDEX_CONTENT:
                return "Content";
            case INDEX_ANALYTICS:
                return "Analytics";
            case INDEX_SCHEDULES:
                return "Schedule";
        }
        return null;
    }
}
