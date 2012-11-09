
package com.rumblefish.friendlymusic.components;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;


public class MOTextViewKu extends TextViewKu
{
	private static Typeface _typeface = null;

	private final Paint mStrokePaint = new Paint();
    private final Rect mTextBounds = new Rect();
    
    private int mStrokeColor;
    private int mShadowColor;
    private int mStrokeWidth;

    protected void init()
	{
    	Context context = getContext();
    	Resources res = context.getResources();

//    	if (_typeface == null)
//    	{
//    		String ttfname = String.format("%s.ttf", res.getString(R.string.fontname));
//    		_typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + ttfname);
//    	}
//
//    	setTextSize((int) (res.getDimensionPixelSize(R.dimen.default_fontsize) * Utils.screenScaleY()));
//    	setTextColor(res.getColor(R.color.normal_text));
//    	setTypeface(_typeface);
//    	setGravity(Gravity.LEFT);
//    	
//    	mStrokeColor = res.getColor(R.color.caption_stroke);
//    	mShadowColor = res.getColor(R.color.shadow);
//    	mStrokeWidth = res.getDimensionPixelSize(R.dimen.default_stroke);
    	
    	mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        mStrokePaint.setShadowLayer(3, 2, 4, mShadowColor);
	}
    
    public MOTextViewKu(Context context)
    {
        super(context);
        init();
    }

    public MOTextViewKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame2(i, j, k, l);
        init();
    }

    public MOTextViewKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init();
    }

    public MOTextViewKu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init();
    }

    public int getStrokeColor()
    {
    	return mStrokeColor;
    }
    
    public void setStrokeColor(int strokeColor)
    {
    	mStrokeColor = strokeColor;
    }
    
    public int getStrokeWidth()
    {
    	return mStrokeWidth;
    }
    
    public void setStrokeWidth(int strokeWidth)
    {
    	mStrokeWidth = strokeWidth;
    }
    
    protected void onDraw(Canvas canvas)
    {
        // Get the text to print
        final float textSize = super.getTextSize();
        final String text = super.getText().toString();

        // setup stroke
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setTextSize(textSize);
        mStrokePaint.setFlags(super.getPaintFlags());
        mStrokePaint.setTypeface(super.getTypeface());

        // Figure out the drawing coordinates
        mStrokePaint.getTextBounds(text, 0, text.length(), mTextBounds);

        int gravity = getGravity();

        if ((gravity & Gravity.LEFT) == Gravity.LEFT)
        {
            mStrokePaint.setTextAlign(Align.LEFT);
        	canvas.drawText(text, mStrokeWidth, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        	mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        	mStrokePaint.setStrokeWidth(0);
        	canvas.drawText(text, mStrokeWidth, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        }
        else if ((gravity & Gravity.CENTER) == Gravity.CENTER)
        {
            mStrokePaint.setTextAlign(Align.CENTER);
            canvas.drawText(text, super.getWidth() * 0.5f, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        	mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        	mStrokePaint.setStrokeWidth(0);
            canvas.drawText(text, super.getWidth() * 0.5f, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        }
        else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT)
        {
            mStrokePaint.setTextAlign(Align.RIGHT);
            canvas.drawText(text, super.getWidth(), (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        	mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        	mStrokePaint.setStrokeWidth(0);
            canvas.drawText(text, super.getWidth(), (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        }
    }


}