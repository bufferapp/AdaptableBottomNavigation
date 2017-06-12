package org.buffer.adaptablebottomnavigation.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.util.AttributeSet;
import android.view.MenuItem;

public class AdaptableBottomNavigationView extends BottomNavigationView {

    private BottomNavigationView.OnNavigationItemSelectedListener viewChangeListener;
    private ViewSwapperOnItemSelectedListener currentViewSwapperSelectedListener;
    private int selectedPosition;

    public AdaptableBottomNavigationView(Context context) {
        super(context);
    }

    public AdaptableBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptableBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = selectedPosition;
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
        selectedPosition = ss.position;
        getMenu().getItem(selectedPosition).setChecked(true);
    }

    @Override
    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {
        viewChangeListener = listener;
    }

    public void setupWithViewSwapper(@Nullable final ViewSwapper viewSwapper) {
        if (currentViewSwapperSelectedListener != null) {
            currentViewSwapperSelectedListener = null;
        }
        if (viewSwapper != null) {
            currentViewSwapperSelectedListener = new ViewSwapperOnItemSelectedListener(viewSwapper);
            super.setOnNavigationItemSelectedListener(currentViewSwapperSelectedListener);
        }
    }

    public class ViewSwapperOnItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        private final ViewSwapper viewSwapper;

        ViewSwapperOnItemSelectedListener(ViewSwapper viewSwapper) {
            this.viewSwapper = viewSwapper;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            for (int i = 0; i < AdaptableBottomNavigationView.this.getMenu().size(); i++) {
                if (AdaptableBottomNavigationView.this.getMenu().getItem(i).getItemId() ==
                        item.getItemId()) {
                    selectedPosition = i;
                    viewSwapper.showItemAt(selectedPosition);
                    break;
                }
            }
            if (viewChangeListener != null)  {
                viewChangeListener.onNavigationItemSelected(item);
            }
            return true;
        }
    }

    public static class SavedState extends AbsSavedState {
        int position;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            position = in.readInt();
            this.loader = loader;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
        }

        @Override
        public String toString() {
            return "AdaptableBottomNavigationView.SavedState{"
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
    }

}
