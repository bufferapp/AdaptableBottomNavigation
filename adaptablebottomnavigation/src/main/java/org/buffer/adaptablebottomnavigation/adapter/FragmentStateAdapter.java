package org.buffer.adaptablebottomnavigation.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * uses a {@link Fragment} to manage each page. This class also handles
 * saving and restoring of fragment's state.
 * <p>
 * <p>This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * {@link FragmentAdapter} at the cost of potentially more overhead when
 * switching between pages.
 * <p>
 * <p>When using FragmentAdapter the host ViewPager must have a
 * valid ID set.</p>
 * <p>
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 * <p>
 * <p>Here is an example implementation of a pager containing fragments of
 * lists:
 * <p>
 * {@sample development/samples/Support13Demos/src/com/example/android/supportv13/app/FragmentStatePagerSupport.java
 * complete}
 * <p>
 * <p>The <code>R.layout.fragment_pager</code> resource of the top-level fragment is:
 * <p>
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager.xml
 * complete}
 * <p>
 * <p>The <code>R.layout.fragment_pager_list</code> resource containing each
 * individual fragment's layout is:
 * <p>
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager_list.xml
 * complete}
 */
public abstract class FragmentStateAdapter extends ViewSwapperAdapter {
    private final FragmentManager fragmentManager;
    private FragmentTransaction currentTransaction = null;
    private ArrayList<Fragment.SavedState> savedState = new ArrayList<Fragment.SavedState>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private Fragment currentPrimaryItem = null;

    public FragmentStateAdapter(FragmentManager fm) {
        fragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (fragments.size() > position) {
            Fragment f = fragments.get(position);
            if (f != null) {
                return f;
            }
        }
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }
        Fragment fragment = getItem(position);
        if (savedState.size() > position) {
            Fragment.SavedState fss = savedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (fragments.size() <= position) {
            fragments.add(null);
        }
        fragment.setMenuVisibility(true);
        fragment.setUserVisibleHint(true);
        fragments.set(position, fragment);
        currentTransaction.add(container.getId(), fragment);
        return fragment;
    }

    @Override
    public void clearItem(ViewGroup container, int position, Object object) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }
        savedState.clear();
        fragments.set(position, null);
        currentTransaction.detach((Fragment) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }
        while (savedState.size() <= position) {
            savedState.add(null);
        }
        savedState.set(position, fragmentManager.saveFragmentInstanceState(fragment));
        fragments.set(position, null);
        currentTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                currentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            currentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (currentTransaction != null) {
            currentTransaction.commitAllowingStateLoss();
            currentTransaction = null;
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (savedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[savedState.size()];
            savedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment f = fragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                fragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            savedState.clear();
            fragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    savedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = fragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (fragments.size() <= index) {
                            fragments.add(null);
                        }
                        f.setMenuVisibility(true);
                        f.setUserVisibleHint(true);
                        fragments.set(index, f);
                    }
                }
            }
        }
    }
}