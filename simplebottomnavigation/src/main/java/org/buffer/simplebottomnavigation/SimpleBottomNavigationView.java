package org.buffer.simplebottomnavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.view.MenuItem;

import static org.buffer.simplebottomnavigation.DIsplayMetricsUtil.convertDpToPixel;

public class SimpleBottomNavigationView extends BottomNavigationView {

    private BottomNavigationView.OnNavigationItemSelectedListener viewChangeListener;
    private ViewSwapperOnItemSelectedListener currentViewSwapperSelectedListener;
    private Drawable shadowDrawable;
    private int selectedPosition;

    private float topPadding;

    public SimpleBottomNavigationView(Context context) {
        super(context);
    }

    public SimpleBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        parseAttributes(context, attrs);
    }

    public SimpleBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
        parseAttributes(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (shadowDrawable != null) {
            Rect newRect = canvas.getClipBounds();
            newRect.inset(0, (int) -topPadding);
            canvas.clipRect(newRect, Region.Op.REPLACE);

            shadowDrawable.setBounds(0, (int) (0 - topPadding), getRight(), 0);
            shadowDrawable.draw(canvas);
        }

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

    private void init(Context context) {
        topPadding = convertDpToPixel(3, context);
        setPadding(0, (int) topPadding, 0, 0);
        setWillNotDraw(false);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.SimpleBottomNavigationView, 0, 0);
        if (ta.getBoolean(R.styleable.SimpleBottomNavigationView_showShadow, false)) {
            shadowDrawable = getContext().getResources().getDrawable(R.drawable.above_shadow);
        }
        ta.recycle();
    }

    public class ViewSwapperOnItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        private final ViewSwapper viewSwapper;

        ViewSwapperOnItemSelectedListener(ViewSwapper viewSwapper) {
            this.viewSwapper = viewSwapper;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            for (int i = 0; i < SimpleBottomNavigationView.this.getMenu().size(); i++) {
                if (SimpleBottomNavigationView.this.getMenu().getItem(i).getItemId() ==
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

    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable viewState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(viewState, flags);
        }

        @Override
        public String toString() {
            return "ViewSwapper.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<ViewSwapper.SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ViewSwapper.SavedState>() {
            @Override
            public ViewSwapper.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new ViewSwapper.SavedState(in, loader);
            }

            @Override
            public ViewSwapper.SavedState[] newArray(int size) {
                return new ViewSwapper.SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            viewState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

}
