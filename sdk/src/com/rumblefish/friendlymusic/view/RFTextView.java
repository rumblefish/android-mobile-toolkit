
package com.rumblefish.friendlymusic.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.rumblefish.friendlymusic.R;


public class RFTextView extends TextView
{
	private static Typeface _typeface = null;

	private final Paint mStrokePaint = new Paint();
    private final Rect mTextBounds = new Rect();
    
    protected void init()
	{
    	Context context = getContext();
    	Resources res = context.getResources();

    	if (_typeface == null)
    	{
    		String ttfname = String.format("%s.ttf", res.getString(R.string.fontname));
    		_typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + ttfname);
    	}


    	mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
	}
    
    public RFTextView(Context context)
    {
        super(context);
        init();
    }


    public RFTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init();
    }

    public RFTextView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init();
    }

    
    protected void onDraw(Canvas canvas)
    {
        // Get the text to print
        final float textSize = super.getTextSize();
        final String text = super.getText().toString();

        // setup stroke
        mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        mStrokePaint.setTextSize(textSize);
        mStrokePaint.setFlags(super.getPaintFlags());
        mStrokePaint.setTypeface(super.getTypeface());

        // Figure out the drawing coordinates
        mStrokePaint.getTextBounds(text, 0, text.length(), mTextBounds);

        int gravity = getGravity();

        if ((gravity & Gravity.LEFT) == Gravity.LEFT)
        {
            mStrokePaint.setTextAlign(Align.LEFT);
        	canvas.drawText(text, 0, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        	mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        	mStrokePaint.setStrokeWidth(0);
        	canvas.drawText(text, 0, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
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