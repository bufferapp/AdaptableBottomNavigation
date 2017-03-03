package org.buffer.simplebottomnavigation.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.buffer.simplebottomnavigation.adapter.FragmentStateAdapter;

public class ViewSwapperAdapter extends FragmentStateAdapter {

    private static final int INDEX_CAT = 0;
    private static final int INDEX_BUFFER = 1;
    private static final int INDEX_ANDROID = 2;

    public ViewSwapperAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INDEX_CAT:
                return ImageFragment.newInstance(R.drawable.cat);
            case INDEX_BUFFER:
                return ImageFragment.newInstance(R.drawable.buffer);
            case INDEX_ANDROID:
                return ImageFragment.newInstance(R.drawable.android);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
