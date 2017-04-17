package org.buffer.adaptablebottomnavigation.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.buffer.adaptablebottomnavigation.model.ItemInfo;
import org.buffer.adaptablebottomnavigation.adapter.ViewSwapperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewSwapper extends FrameLayout {

    private final ArrayList<ItemInfo> items = new ArrayList<>();

    private ViewSwapperAdapter adapter;
    private int currentRestoredItem = -1;
    private Parcelable restoredAdapterState = null;
    private ClassLoader restoredClassLoader = null;
    private PagerObserver observer;

    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
        @Override
        public int compare(ItemInfo lhs, ItemInfo rhs) {
            return lhs.position - rhs.position;
        }
    };

    private int currentItem;

    public ViewSwapper(Context context) {
        super(context);
    }

    public ViewSwapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewSwapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewSwapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (currentRestoredItem < 0 && adapter != null && adapter.getCount() > 0) {
            showItemInternal(0);
        }
    }

    public ViewSwapperAdapter getAdapter() {
        return this.adapter;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setAdapter(ViewSwapperAdapter adapter) {
        if (adapter != null) {
            adapter.setViewSwapperObserver(null);
            adapter.startUpdate(this);
            for (int i = 0; i < items.size(); i++) {
                final ItemInfo ii = items.get(i);
                adapter.destroyItem(this, ii.position, ii.object);
            }
            adapter.finishUpdate(this);
            items.clear();
        }

        this.adapter = adapter;

        if (adapter != null) {
            if (observer == null) {
                observer = new PagerObserver();
            }
            for (int i = 0; i < this.adapter.getCount(); i++) {
                addNewItem(i);
            }
            adapter.registerDataSetObserver(observer);
            if (currentRestoredItem >= 0) {
                adapter.restoreState(restoredAdapterState, restoredClassLoader);
                showItemInternal(currentRestoredItem);
                currentRestoredItem = -1;
                restoredAdapterState = null;
                restoredClassLoader = null;
            }
        }
    }

    ItemInfo addNewItem(int position) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        if (position < 0 || position >= items.size()) {
            items.add(ii);
        } else {
            items.add(position, ii);
        }
        return ii;
    }

    public void showItemAt(int position) {
        if (items.get(position) == null) {
            addNewItem(position);
        }

        if (currentItem == position) {
            this.adapter.clearItem(this, currentItem, items.get(currentItem).object);
        } else if (this.adapter.getCount() > 0) {
            this.adapter.destroyItem(this, currentItem, items.get(currentItem).object);
        }

        showItemInternal(position);
    }

    private void showItemInternal(int position) {
        currentItem = position;
        items.get(position).object = this.adapter.instantiateItem(this, position);
        this.adapter.finishUpdate(this);
    }

    /**
     * This is the persistent state that is saved by ViewPager.  Only needed
     * if you are creating a sublass of ViewPager that must save its own
     * state, in which case it should implement a subclass of this which
     * contains that state.
     */
    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = currentItem;
        if (adapter != null) {
            ss.adapterState = adapter.saveState();
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (adapter != null) {
            adapter.restoreState(ss.adapterState, ss.loader);
            showItemInternal(ss.position >= 0 ? ss.position : 0);
        } else {
            restoredAdapterState = ss.adapterState;
            restoredClassLoader = ss.loader;
        }
        currentRestoredItem = ss.position;
    }

    private class PagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    void dataSetChanged() {
        boolean needPopulate = items.size() < adapter.getCount();
        int newCurrItem = currentItem;
        boolean isUpdating = false;
        for (int i = 0; i < items.size(); i++) {
            final ItemInfo ii = items.get(i);
            final int newPos = adapter.getItemPosition(ii.object);
            if (newPos == ViewSwapperAdapter.POSITION_UNCHANGED) {
                continue;
            }
            if (newPos == ViewSwapperAdapter.POSITION_NONE) {
                items.remove(i);
                i--;
                if (!isUpdating) {
                    adapter.startUpdate(this);
                    isUpdating = true;
                }
                adapter.destroyItem(this, ii.position, ii.object);
                needPopulate = true;
                if (currentItem == ii.position) {
                    newCurrItem = Math.max(0, Math.min(currentItem, adapter.getCount() - 1));
                    needPopulate = true;
                }
                continue;
            }
            if (ii.position != newPos) {
                if (ii.position == currentItem) {
                    newCurrItem = newPos;
                }
                ii.position = newPos;
                needPopulate = true;
            }
        }
        if (isUpdating) {
            adapter.finishUpdate(this);
        }
        Collections.sort(items, COMPARATOR);
        if (needPopulate) {
            showItemInternal(newCurrItem);
            requestLayout();
        }
    }

}