
package com.rumblefish.friendlymusic.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewKu extends TextView
{

    public TextViewKu(Context context)
    {
        super(context);
    }

    public TextViewKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame2(i, j, k, l);
    }

    public TextViewKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public TextViewKu(Context context, AttributeSet attributeset, int i)
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

    public int get_Height()
    {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams)getLayoutParams()).height;
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
        super.onDraw(canvas);
    }

    public ViewContainerKu parent()
    {
        return (ViewContainerKu)getParent();
    }

    public void removeFromParent()
    {
        if(parent() != null)
            parent().removeView(this);
    }

    public void resize(int i, int j)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.width = i;
        layoutparams.height = j;
        setLayoutParams(layoutparams);
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
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.height = i;
        setLayoutParams(layoutparams);
    }

    public void setX(int i)
    {
        checkLayoutParams();
        
        try {
	        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
	        layoutparams.leftMargin = i;
	        setLayoutParams(layoutparams);
        } catch (Exception e) {
        }
    }

    public void setY(int i)
    {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
        layoutparams.topMargin = i;
        setLayoutParams(layoutparams);
    }
    
    public int maxTextSize = 70;
    public int minTextSize = 10;
    private Paint testPaint;
    
    public void refitText(String text, int textWidth, int textHeight) {
    	testPaint = new Paint();
        testPaint.set(this.getPaint());
        float trySize = maxTextSize;
        
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
            trySize = maxTextSize;

            testPaint.setTextSize(trySize);
            while ((trySize > minTextSize) && (testPaint.measureText(text) > availableWidth)) {
                trySize -= 1;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                testPaint.setTextSize(trySize);
            }

            this.setTextSize(trySize);
        }
        if(textHeight > 0)
        {
        	
        	int availableHeight = textHeight - this.getPaddingTop() - this.getPaddingBottom();
        	while(this.getLineHeight() > availableHeight)
        	{
        		trySize -= 1;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                this.setTextSize(trySize);
        	}
        	this.setTextSize(trySize);
        }
    }
}