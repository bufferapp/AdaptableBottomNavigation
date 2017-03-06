Simple Bottom Navigation
-------------------------

![Demo](/art/demo.gif)

When using the Bottom Navigation View from the Android Support Library, there can be a lot of boilerplate code for the switching of views. Because of this, we took inspiration from the TabLayout setupWithViewPager() method and created a custom ViewSwapper component that can be attached to a Bottom Navigation View to simplify the management of view display. Within this library are three core components that you'll need to use:

- SimpleBottomNavigationView - The Bottom Navigation View for displaying navigation items in a bar at the bottom of the screen.  This view extends the BottomNavigationView from the Design Support Library, so the two classes are easily interchangable in your projects.

- ViewSwapper - The View Swapper is used to easily switch between fragments that you wish to display to the user. Unlike the View pager, views will not transition between pages and swiping is not possible - hence adhering to the Design Principles for the Bottom Navigation View.

- FragmentStateAdapter / FragmentAdapter - An abstract adapter that allows you to create an adapter for displaying fragments within the ViewSwapper. These classes are the same as the implementations found within the Android Framework, agan with a few tweaks to better match the behaviour of the View Swapper component.

# How to use

In order to use this ViewSwapper component you need to use our SimpleBottomNavigationView - this is simply an extension of the support library component with the added functionality of ViewSwapper attachment and elevation. This will look like so in your layout file:

```xml
<org.buffer.simplebottomnavigation.ViewSwapper
        android:id="@+id/view_swapper"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/view_bottom_navigation" />

<org.buffer.simplebottomnavigation.SimpleBottomNavigationView
    android:id="@+id/view_bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    android:background="@color/colorPrimary"
    app:itemBackground="@color/colorPrimary"
    app:itemIconTint="@drawable/selector_menu"
    app:itemTextColor="@drawable/selector_menu"
    app:menu="@menu/main"
    app:showShadow="true" />
```

Next, you'll need to crate an adapter using either the FragmentStateAdapter or FragmentAdapter classes from the library. These are the essentially the same as the corresponding adapter classes found in the Android Framework. This could look something like so:

```java

public class ViewSwapperAdapter extends FragmentStateAdapter {

    private static final int INDEX_BUFFER = 0;
    private static final int INDEX_RETREAT = 1;
    private static final int INDEX_VALUES = 2;

    public ViewSwapperAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INDEX_BUFFER:
                return ImageFragment.newInstance(R.drawable.cat);
            case INDEX_RETREAT:
                return ImageFragment.newInstance(R.drawable.buffer);
            case INDEX_VALUES:
                return ImageFragment.newInstance(R.drawable.android);
        }
        return ImageFragment.newInstance(R.drawable.cat);
    }

    @Override
    public int getCount() {
        return 3;
    }

}
```

Now we have the views defined in our layout and the adapter to manage the display of our fragments (or views), we can go ahead and set the adapter for the View Swapper followed by attaching the View Swapper to the Bottom Navigation View:

e.g.
```java
viewSwapper.setAdapter(viewSwapperAdapter);
bottomNavigationView.setupWithViewSwapper(viewSwapper);
```

# Adding elevation

We've added an attribute to easily add elevation to the bottom navigtion view, we can apply this attribute to our SimpleBottomNavigationView like so:

```xml
app:showShadow="true"
```

