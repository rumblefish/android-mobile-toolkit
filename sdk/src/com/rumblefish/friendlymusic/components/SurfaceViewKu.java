
package com.rumblefish.friendlymusic.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class SurfaceViewKu extends SurfaceView
{

    public SurfaceViewKu(Context context)
    {
        super(context);
    }

    public SurfaceViewKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame2(i, j, k, l);
    }

    public SurfaceViewKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SurfaceViewKu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    private void checkLayoutParams()
    {
        if(getLayoutParams() == null)
            setLayoutParams(new android.widget.RelativeLayout.LayoutParams(0, 0));
    }

    public FrameKu getFrame()
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        return new FrameKu(layoutparams.leftMargin, layoutparams.topMargin, layoutparams.width, layoutparams.height);
    }

    public float getY()
    {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).topMargin;
    }

    public void move(int i, int j)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.leftMargin = i;
        layoutparams.topMargin = j;
        setLayoutParams(layoutparams);
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
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.width = i;
        layoutparams.height = j;
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