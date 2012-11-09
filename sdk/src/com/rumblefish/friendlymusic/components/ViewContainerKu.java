
package com.rumblefish.friendlymusic.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class ViewContainerKu extends RelativeLayout
{

    public ViewContainerKu(Context context)
    {
        super(context);
    }

    public ViewContainerKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame(i, j, k, l);
    }

    public ViewContainerKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public ViewContainerKu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public ViewContainerKu(Context context, FrameKu frameku)
    {
        super(context);
        setFrame(frameku);
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

    public float getX()
    {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).leftMargin;
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

    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setColor(0xffff0000);
        if(_path != null)
            canvas.drawPath(_path, paint);
    }

    public ViewContainerKu parent()
    {
        return (ViewContainerKu)getParent();
    }

    public void removeFromParent()
    {
        parent().removeView(this);
    }

    public void removePath()
    {
        _path = null;
    }

    public void resize(int i, int j)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.width = i;
        layoutparams.height = j;
        setLayoutParams(layoutparams);
    }

    public void setFrame(int i, int j, int k, int l)
    {
        move(i, j);
        resize(k, l);
    }

    public void setFrame(FrameKu frameku)
    {
        move(frameku.x, frameku.y);
        resize(frameku.width, frameku.height);
    }

    public void setHeight(int i)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.height = i;
        setLayoutParams(layoutparams);
    }

    public void setPath(Path path)
    {
        _path = path;
    }

    public void setWidth(int i)
    {
        checkLayoutParams();
        
        try {
	        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
	        layoutparams.width = i;
	        setLayoutParams(layoutparams);
        } catch(Exception e) {
        	
        }
    }

    public void setX(int i)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.leftMargin = i;
        setLayoutParams(layoutparams);
    }

    public void setY(int i)
    {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).topMargin = i;
    }

    private Path _path;
}