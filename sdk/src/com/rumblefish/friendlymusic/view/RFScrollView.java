package com.rumblefish.friendlymusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class RFScrollView extends ScrollView {

	public RFScrollView(Context context)
    {
        super(context);
    }

    public RFScrollView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RFScrollView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }
    
    private boolean mScrollable = true;

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        //super.onScrollChanged(x, y, oldx, oldy);
        Log.v("Scroller", "onScrollChanged x:" + x + " y:" + y + " oldx:" + oldx + " oldy:" + oldy);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if 
        // we are not scrollable
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }
}
