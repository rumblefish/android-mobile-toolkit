package com.rumblefish.friendlymusic.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MOButtonKu extends ButtonKu {

	private static Typeface _typeface = null;
	
	private NinePatchDrawable mBGDrawableA;
	private NinePatchDrawable mBGDrawableB;
	
	private boolean mIsTouchDown;

	private final Paint mStrokePaint = new Paint();
    private final Rect mTextBounds = new Rect();
    
    private int mStrokeColor;
    private int mShadowColor;
    private int mStrokeWidth;

    protected void init()
	{
//    	Context context = getContext();
//    	Resources res = context.getResources();
//
//    	if (_typeface == null)
//    	{
//    		String ttfname = String.format("%s.ttf", res.getString(R.string.fontname));
//    		_typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + ttfname);
//    	}
//
//    	mBGDrawableA = (NinePatchDrawable) res.getDrawable(R.drawable.button_a);
//    	mBGDrawableB = (NinePatchDrawable) res.getDrawable(R.drawable.button_b);
//    	
//    	setTextSize((int) (res.getDimensionPixelSize(R.dimen.default_fontsize) * Utils.screenScaleY()));
//    	setTextColor(res.getColor(R.color.normal_text));
//    	setTypeface(_typeface);
//
//    	mStrokeColor = res.getColor(R.color.caption_stroke);
//    	mShadowColor = res.getColor(R.color.shadow);
//    	mStrokeWidth = res.getDimensionPixelSize(R.dimen.default_stroke);

    	mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setTextAlign(Paint.Align.CENTER);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        
        mIsTouchDown = false;
	}

    public MOButtonKu(Context context)
	{
		super(context);
		init();
	}
	
    public MOButtonKu(Context context, int i, int j, int k, int l)
    {
        super(context);
        setFrame2(i, j, k, l);
		init();
    }

    public MOButtonKu(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
		init();
    }

    public MOButtonKu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
		init();
    }

    public MOButtonKu(Context context, FrameKu frameku)
    {
        super(context);
        setFrame2(frameku);
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

    @Override
	public boolean onTouchEvent(MotionEvent motionevent) {
    	
    	int action = motionevent.getAction();
    	
    	switch (action)
    	{
    	case MotionEvent.ACTION_DOWN:
    		mIsTouchDown = true;
    		invalidate();
    		break;
    	case MotionEvent.ACTION_MOVE:
    		break;
    	case MotionEvent.ACTION_UP:
    		mIsTouchDown = false;
    		invalidate();
    	}
    	
    	return super.onTouchEvent(motionevent);
	}

	@Override
	protected void onDraw(Canvas canvas) {
        // Get the text to print
        final float textSize = super.getTextSize();
        final String text = super.getText().toString();

        if (mIsTouchDown)
        {
        	// draw background
        	mBGDrawableB.setBounds(0, 0, getWidth(), getHeight());
        	mBGDrawableB.draw(canvas);

        	mStrokePaint.setShadowLayer(0, 0, 0, 0x00000000);
        }
        else
        {
        	// draw background
        	mBGDrawableA.setBounds(0, 0, getWidth(), getHeight());
        	mBGDrawableA.draw(canvas);

        	mStrokePaint.setShadowLayer(3, 2, 4, mShadowColor);
        	
        	// setup stroke
        	mStrokePaint.setColor(mStrokeColor);
        	mStrokePaint.setStrokeWidth(mStrokeWidth);
        	mStrokePaint.setTextSize(textSize);
        	mStrokePaint.setFlags(super.getPaintFlags());
        	mStrokePaint.setTypeface(super.getTypeface());
        	
        	// Figure out the drawing coordinates
        	mStrokePaint.getTextBounds(text, 0, text.length(), mTextBounds);
        	
        	// draw everything
        	canvas.drawText(text, super.getWidth() * 0.5f, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
        }
        
        mStrokePaint.setColor(super.getTextColors().getDefaultColor());
        mStrokePaint.setStrokeWidth(0);
    	// Figure out the drawing coordinates
    	mStrokePaint.getTextBounds(text, 0, text.length(), mTextBounds);
        canvas.drawText(text, super.getWidth() * 0.5f, (super.getHeight() + mTextBounds.height()) * 0.5f, mStrokePaint);
	}
}
