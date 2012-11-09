package com.rumblefish.friendlymusic.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollViewKu extends ScrollView {
    public ScrollViewKu(Context context)
    {
        super(context);
    }

    public ScrollViewKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame2(i, j, k, l);
    }

    public ScrollViewKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public ScrollViewKu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    private void checkLayoutParams()
    {
        if(getLayoutParams() == null)
            setLayoutParams(new android.widget.RelativeLayout.LayoutParams(0, 0));
    }

    public void move(int i, int j)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.leftMargin = i;
        layoutparams.topMargin = j;
    }

    public ViewContainerKu parent()
    {
        return (ViewContainerKu)getParent();
    }

    public void removeFromParent()
    {
        parent().removeView(this);
    }

    public void resize(int i, int j)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams lps = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        lps.width = i;
        lps.height = j;
    }

    public void setFrame2(int i, int j, int k, int l)
    {
        move(i, j);
        resize(k, l);
    }

    public void setFrame2(FrameKu frameku)
    {
        move(frameku.x, frameku.y);
        resize(frameku.width, frameku.height);
    }

    public void setHeight(int i)
    {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).height = i;
    }

    public void setWidth(int i)
    {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).width = i;
    }

    public void setX(int i)
    {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).leftMargin = i;
    }

    public void setY(int i)
    {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).topMargin = i;
    }
}
