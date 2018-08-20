package org.buffer.adaptablebottomnavigation.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.view.MenuItem;

public class AdaptableBottomNavigationView extends BottomNavigationView
{

    private BottomNavigationView.OnNavigationItemSelectedListener viewChangeListener;
    private ViewSwapperOnItemSelectedListener currentViewSwapperSelectedListener;
    private int selectedPosition;

    public AdaptableBottomNavigationView(Context context)
    {
        super(context);
    }

    public AdaptableBottomNavigationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AdaptableBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = selectedPosition;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedState))
        {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        selectedPosition = ss.position;
        getMenu().getItem(selectedPosition).setChecked(true);
    }

    @Override
    public void setOnNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener)
    {
        viewChangeListener = listener;
    }

    public void setupWithViewSwapper(@Nullable final ViewSwapper viewSwapper)
    {
        if (currentViewSwapperSelectedListener != null)
        {
            currentViewSwapperSelectedListener = null;
        }
        if (viewSwapper != null)
        {
            currentViewSwapperSelectedListener = new ViewSwapperOnItemSelectedListener(viewSwapper);
            super.setOnNavigationItemSelectedListener(currentViewSwapperSelectedListener);
        }
    }

    public class ViewSwapperOnItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener
    {

        private final ViewSwapper viewSwapper;

        ViewSwapperOnItemSelectedListener(ViewSwapper viewSwapper)
        {
            this.viewSwapper = viewSwapper;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            for (int i = 0; i < AdaptableBottomNavigationView.this.getMenu().size(); i++)
            {
                if (AdaptableBottomNavigationView.this.getMenu().getItem(i).getItemId() == item.getItemId())
                {
                    selectedPosition = i;
                    viewSwapper.showItemAt(selectedPosition);
                    break;
                }
            }
            if (viewChangeListener != null)
            {
                viewChangeListener.onNavigationItemSelected(item);
            }
            return true;
        }
    }

    public static class SavedState extends BaseSavedState
    {
        int position;
        Parcelable viewState;
        ClassLoader loader;
        Parcelable superState;

        public SavedState(Parcelable superState)
        {
            super(EMPTY_STATE);
            this.superState = superState;
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeParcelable(superState, flags);
            out.writeInt(position);
            out.writeParcelable(viewState, flags);
        }

        @Override
        public String toString()
        {
            return "AdaptableBottomNavigationView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel parcel)
            {
                return null;
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader)
            {
                if (Build.VERSION.SDK_INT >= 24)
                {
                    return new SavedState(parcel, classLoader);
                }
                else
                {
                    return new SavedState(parcel);
                }
            }
        };

        SavedState(Parcel in)
        {
            super(in);
            this.superState = in.readParcelable(getClass().getClassLoader());
            if (loader == null)
            {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            viewState = in.readParcelable(loader);
            this.loader = loader;
        }

        SavedState(Parcel in, ClassLoader loader)
        {
            super(in);
            this.superState = in.readParcelable(getClass().getClassLoader());
            if (loader == null)
            {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            viewState = in.readParcelable(loader);
            this.loader = loader;
        }
    }
}
