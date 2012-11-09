package com.rumblefish.friendlymusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.rumblefish.friendlymusic.api.Occasion;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.api.Producer;
import com.rumblefish.friendlymusic.api.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.components.ImageViewKu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OccasionActivity  extends Activity {
	
	public static final String 	LOGTAG = "OCCASIONACTIVITY";
	public static final String 	OCCASION_IMAGE_CACHE_PATH = "occasion_image_cache";
	public static final String  OCCASION_IMAGE_FILE_PREFIX = "occasions";
	public static final int 	OCCASION_IMAGE_DEPTH = 4;
	public static final float 	OCCASION_IMAGE_SWITCH_DELAY = 2.0f; // seconds

	public static final int		BUTTON_HIDDEN_OFFSET = 480;
	public static final int		BUTTON_HEIGHT_FIXED = 35;
	public static final int		BUTTON_SECOND_HEIGHT = 129;
	public static final int		BUTTON_THIRD_HEIGHT = 56;
	
	// member variables
	RelativeLayout	m_rlContent;
	RelativeLayout	m_rlNavBar;
	RelativeLayout	m_rlScroller;
	
	// navigation buttons
	ImageView	m_ivBtnNavDone;
	ImageView	m_ivBtnNavPlaylist;
	ImageView	m_ivBtnNavRemove;
	
	// occasion buttons
	ImageView	m_ivBtnMood;
	ImageView	m_ivBtnCelebration;
	ImageView	m_ivBtnTheme;
	ImageView	m_ivBtnEvent;
	ImageView	m_ivBtnSports;
	ImageView	m_ivBtnHoliday;
	
	//level buttons
	TextView 				m_firstButton;
	ArrayList<TextView>		m_secondButtons;
	ArrayList<TextView>		m_thirdButtons;

	//progress bar
	ProgressBar m_pbActivityIndicator;
	
	//some utils variable
	int		m_contentWidth;
	int 	m_contentHeight;
	float 	m_ratioTo320X;
	float 	m_ratioTo480Y;
	float 	m_minRatio;
	
	
	//occasions
	ArrayList<Integer> 						m_occasionKeys;
	HashMap<Integer, ArrayList<Bitmap>> 	m_occasionImageDict;
	ArrayList<Occasion>						m_occasions;
	ArrayList<Occasion>						m_occasionStack;
	
	
	
	// colors;
	int		m_firstLevelColor;
	int 	m_secondLevelColor;
	int 	m_thirdLevelColor;
	int		m_secondFontColor;
	int 	m_thirdFontColor;
	
	// position & size info
	Rect m_secondRect;
	Rect m_thirdRect;
	
	// utils variables
	boolean m_replaySong;
	int 	m_level;
	int	m_plRow;
	int m_plSection;
	
	// audio player
	MediaPlayer m_mediaPlayer = null;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.occasion);
        
        initView();
        
        m_occasionStack = new ArrayList<Occasion>();
        
        m_level = 1;
        
        m_secondButtons = new ArrayList<TextView>();
        m_thirdButtons = new ArrayList<TextView>();
        
        m_occasionKeys = new ArrayList<Integer>();
        m_occasionKeys.add(RFOccasionMood);
        m_occasionKeys.add(RFOccasionCelebration);
        m_occasionKeys.add(RFOccasionThemes);
        m_occasionKeys.add(RFOccasionCurrentEvents);
        m_occasionKeys.add(RFOccasionSports);
        m_occasionKeys.add(RFOccasionSports);
        
        m_occasionImageDict = new HashMap<Integer, ArrayList<Bitmap>>();
        
        for( int i = 0 ; i < m_occasionKeys.size(); i++)
        {
        	ArrayList<Bitmap> occasionArray = new ArrayList<Bitmap>();
        	m_occasionImageDict.put(m_occasionKeys.get(i), occasionArray);
        }
        
        loadOccasionImages();
        getOccasionsFromServer();
	}
	
	// occasion image utils function start
	public void updateOccasionImage()
	{
		
	}
	
	public void getOccasionsFromServer()
	{
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		final ArrayList<String> displayedOccasionNames = new ArrayList<String>();
		
		displayedOccasionNames.add("Celebrations");
		displayedOccasionNames.add("Current Events");
		displayedOccasionNames.add("Holidays"); 
		displayedOccasionNames.add("Moods"); 
		displayedOccasionNames.add("Sports"); 
		displayedOccasionNames.add("Themes");
		
		Producer getOccasion = RFAPI.getSingleTone().getOccasions();
		if(getOccasion == null)
    		return;
		
		getOccasion.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				
				
				Log.v(LOGTAG, "getOccasion result");
				
				ArrayList<Occasion> arrayOccasion = (ArrayList<Occasion>) obj;
				for(int i = arrayOccasion.size() - 1; i >= 0 ; i--)
				{
					Occasion occasion = arrayOccasion.get(i);
					boolean bFound = false;
					for(int j = 0; j < displayedOccasionNames.size(); j++)
					{
						if(occasion.m_name.equals(displayedOccasionNames.get(j)))
						{
							bFound = true;
							break;
						}
					}
					if(bFound == false)
					{
						arrayOccasion.remove(i);
					}
				}
				
				m_occasions = arrayOccasion;
				
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				setButtonsHidden(false);
			}

			@Override
			public void onError() {
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
			}
    	};
    	getOccasion.run();
		
	}
	
	public void loadOccasionImages()
	{
		//checks if image data is saved
		
		Bitmap bm = RFUtils.getBitmapFromPath(this, OCCASION_IMAGE_CACHE_PATH, OCCASION_IMAGE_FILE_PREFIX + "_0_00.jpg");
		if(bm != null)
		{
			//loads from disk
			Iterator<Integer> iterator = m_occasionImageDict.keySet().iterator();
			while(iterator.hasNext())
			{
				Integer key = iterator.next();
				ArrayList<Bitmap> imgs = m_occasionImageDict.get(key);

				int i = 0;
				while(true)
				{
					bm = RFUtils.getBitmapFromPath(this, OCCASION_IMAGE_CACHE_PATH, OCCASION_IMAGE_FILE_PREFIX + "_" + key.intValue() + "_" + i + ".jpg");
					if(bm == null){
						break;
					}
					imgs.add(bm);
					i++;
				}
			}
		}
		else
		{
			Iterator<Integer> iterator = m_occasionImageDict.keySet().iterator();
			while(iterator.hasNext())
			{
				Integer key = iterator.next();
				ArrayList<Bitmap> imgs = m_occasionImageDict.get(key);

				int i = 0;
				while(true)
				{
					try
					{
						bm = BitmapFactory.decodeStream(this.getAssets().open(OCCASION_IMAGE_CACHE_PATH + "/" + OCCASION_IMAGE_FILE_PREFIX + "_" + key.intValue() + "_" + i + ".png"));
					}
					catch(IOException e)
					{
						bm = null;
					}
					if(bm == null){
						break;
					}
					imgs.add(bm);
					i++;
				}
			}
		}
		
		m_bRunning = true;
		m_rotateImagesTimer.start();
	}
	
	public void saveOccasionImages()
	{
		Iterator<Integer> iterator = m_occasionImageDict.keySet().iterator();
		while(iterator.hasNext())
		{
			Integer key = iterator.next();
			ArrayList<Bitmap> imgs = m_occasionImageDict.get(key);

			int i = 0;
			for(i = 0; i < imgs.size(); i++)
			{
				Bitmap bm = imgs.get(i);
				RFUtils.saveBitmapToPath(this, OCCASION_IMAGE_CACHE_PATH, OCCASION_IMAGE_FILE_PREFIX + "_" + key.intValue() + "_" + i + ".jpg", bm);
			}
		}
	}
	
	
	
	//Levels
	public void loadSecondLevel(View button)
	{
		if(m_level == 1)
		{
			if(button == m_ivBtnMood)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.55f, 0.32f, 0.68f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.51f, 0.4f, 0.58f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.506f, 0.45f, 0.53f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.26f, 0.219f, 0.278f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.26f, 0.223f, 0.282f, 1.0f);
                m_firstButton.setText("mood");
                pushOccasionNamed("Moods");
			}
			else if(button == m_ivBtnCelebration)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.33f, 0.537f, 0.156f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.455f, 0.6f, 0.33f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.474f, 0.537f, 0.42f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.235f, 0.337f, 0.152f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.231f, 0.278f, 0.192f, 1.0f);
                m_firstButton.setText("celebration");
                pushOccasionNamed("Celebrations");
			}
			else if(button == m_ivBtnTheme)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.66f, 0.576f, 0.157f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.71f, 0.64f, 0.317f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.74f, 0.686f, 0.435f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.455f, 0.4f, 0.157f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.443f, 0.4f, 0.21f, 1.0f);
                m_firstButton.setText("themes");
                pushOccasionNamed("Themes");
			}
			else if(button == m_ivBtnEvent)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.243f, 0.654f, 0.63f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.455f, 0.73f, 0.713f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.6f, 0.713f, 0.706f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.28f, 0.46f, 0.455f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.35f, 0.44f, 0.435f, 1.0f);
                m_firstButton.setText("current events");
                pushOccasionNamed("Current Events");
			}
			else if(button == m_ivBtnSports)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.192f, 0.388f, 0.63f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.31f, 0.45f, 0.627f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.5f, 0.584f, 0.69f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.168f, 0.282f, 0.427f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.274f, 0.34f, 0.423f, 1.0f);
                m_firstButton.setText("sports");
                pushOccasionNamed("Sports");
			}
			else if(button == m_ivBtnHoliday)
			{
				m_firstLevelColor = RFUtils.getColorFromFloatVal(0.647f, 0.2f, 0.2f, 1.0f);
                m_secondLevelColor = RFUtils.getColorFromFloatVal(0.745f, 0.32f, 0.32f, 1.0f);
                m_thirdLevelColor = RFUtils.getColorFromFloatVal(0.75f, 0.455f, 0.455f, 1.0f);
                m_secondFontColor = RFUtils.getColorFromFloatVal(0.51f, 0.172f, 0.172f, 1.0f);
                m_thirdFontColor = RFUtils.getColorFromFloatVal(0.5f, 0.21f, 0.21f, 1.0f);
                m_firstButton.setText("holiday");
                pushOccasionNamed("Holidays");
			}
			
			m_firstButton.setBackgroundColor(m_firstLevelColor);
			m_firstButton.setTextColor(Color.WHITE);
			showSecondLevel();
			
		}
	}
	
	private void pushOccasionNamed(String string)
	{
		Occasion occasion = null;
		ArrayList<Occasion> arrayOccasion = (ArrayList<Occasion>) m_occasions;
		for(int i = arrayOccasion.size() - 1; i >= 0 ; i--)
		{
			occasion = arrayOccasion.get(i);
			if(occasion.m_name.equals(string))
			{
				break;
			}
		}
		m_occasionStack.add(occasion);
		for(int i = 0; i < occasion.m_children.size(); i++)
		{
			Occasion child = occasion.m_children.get(i);
			TextView button = new TextView(this);
			
			m_rlScroller.addView(button);
			setElemPosSize(button, 0, (int)(m_ratioTo480Y * (481 + i * 129)), m_ratioTo320X * 320.0f, 129.0f * m_ratioTo480Y);
			
			button.setAlpha(0);
			button.setBackgroundColor(m_secondLevelColor);
			button.setTextSize(100);
			button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			button.setTextColor(m_secondFontColor);
			button.setText(child.m_name);
			button.setClickable(true);
			button.setOnClickListener(m_OnSecondButtonClickListener);
			
			m_secondButtons.add(button);
		}
	}
	
	private void pushOccasion(Occasion occasion)
	{
		m_occasionStack.add(occasion);
		for( int i = 0; i < occasion.m_children.size(); i++)
		{
			Occasion child = occasion.m_children.get(i);
			
			TextView button = new TextView(this);
			m_rlScroller.addView(button);
			
			setElemPosSize(button,	0.0f * m_ratioTo320X, (BUTTON_HIDDEN_OFFSET + i * 56) * m_ratioTo480Y, 320.0f * m_ratioTo320X, 56.0f * m_ratioTo480Y) ;
			button.setBackgroundColor(m_thirdLevelColor);
			button.setTextSize(50);
			button.setAlpha(0.0f);
			button.setTextColor(m_thirdFontColor);
			button.setText(child.m_name.toLowerCase());
			button.setClickable(true);
			button.setOnClickListener(m_OnThirdButtonClickListener);
			
			m_thirdButtons.add(button);
			
		}
	}
	
	private void popOccasion()
	{
		m_occasionStack.remove(m_occasionStack.size() - 1);
	}
	
	
	
	// animation utils function
	public AnimationSet addMoveResizeAnimation( int duration, View view, float targetX, float targetY, float targetWidth, float targetHeight, boolean fadeIn)
	{
		AnimationSet newSet = new AnimationSet(true);
		newSet.setFillAfter(true);
		
		Rect rt = getElemPosSize(view);
		TranslateAnimation animMove = new TranslateAnimation(0, targetX - rt.left, 0, targetY - rt.top);
		ScaleAnimation animResize = new ScaleAnimation(1.0f, targetWidth / rt.width(), 1.0f, targetHeight / rt.height());
		AlphaAnimation animFade = null; 
		if(fadeIn == true) 
			animFade = new AlphaAnimation(0.0f, 1.0f);
		else
			animFade = new AlphaAnimation(1.0f, 0.0f);
		
		animMove.setDuration(duration);
		animResize.setDuration(duration);
		animFade.setDuration(duration);
		
		newSet.addAnimation(animMove);
		newSet.addAnimation(animResize);
		newSet.addAnimation(animFade);
		
		view.clearAnimation();
		view.setAnimation(newSet);
		
		return newSet;
	}
	
	public void positionFirstButtons(boolean hidden)
	{
		if(hidden == true)
		{
			setElemPosSize(m_ivBtnMood, 		-117.0f * m_ratioTo320X, -115.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnCelebration, 	 320.0f * m_ratioTo320X, -115.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnTheme, 		-117.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnEvent, 		 320.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnSports, 		-117.0f * m_ratioTo320X,  416.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnHoliday,		 320.0f * m_ratioTo320X,  416.0f * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			
			setButtonsHidden(true);
		}
		else
		{
			// Occasion Buttons
        	setElemPosSize(m_ivBtnMood, 		(int)( 29 * m_ratioTo320X), 	(int)(013 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnCelebration, 	(int)(175 * m_ratioTo320X), 	(int)(013 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnTheme, 		(int)( 29 * m_ratioTo320X), 	(int)(151 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnEvent, 		(int)(175 * m_ratioTo320X), 	(int)(151 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnSports, 		(int)( 29 * m_ratioTo320X), 	(int)(289 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnHoliday, 		(int)(175 * m_ratioTo320X), 	(int)(289 * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	
        	setButtonsHidden(false);
		}
		
		m_ivBtnMood.clearAnimation();
		m_ivBtnCelebration.clearAnimation();
		m_ivBtnTheme.clearAnimation();
		m_ivBtnEvent.clearAnimation();
		m_ivBtnSports.clearAnimation();
		m_ivBtnHoliday.clearAnimation();
	}
	
	public void positionSecondButtons(boolean hidden, View exception)
	{
		for( int i = 0; i < m_secondButtons.size(); i++)
		{
			TextView button = m_secondButtons.get(i);
			if(button == exception)
				continue;
			if(hidden == true)
			{
				setElemPosSize(button,	0.0f * m_ratioTo320X, (BUTTON_HIDDEN_OFFSET + i * 129) * m_ratioTo480Y, 320.0f * m_ratioTo320X, 129.0f * m_ratioTo480Y) ;
				button.setAlpha(0.0f);
			}
			else
			{
				setElemPosSize(button,	0.0f * m_ratioTo320X, (i * 129) * m_ratioTo480Y, 320.0f * m_ratioTo320X, 129.0f * m_ratioTo480Y) ;
				button.setAlpha(1.0f);
			}
			button.clearAnimation();
		}
	}
	
	
	public void positionThirdButtons(boolean hidden, View exception)
	{
		for( int i = 0; i < m_thirdButtons.size(); i++)
		{
			TextView button = m_thirdButtons.get(i);
			
			if(button == exception)
				continue;
			
			if(hidden == true)
			{
				setElemPosSize(button,	0.0f * m_ratioTo320X, (BUTTON_HIDDEN_OFFSET + i * 56) * m_ratioTo480Y, 320.0f * m_ratioTo320X, 56.0f * m_ratioTo480Y) ;
				button.setAlpha(0.0f);
			}
			else
			{
				setElemPosSize(button,	0.0f * m_ratioTo320X, (BUTTON_HEIGHT_FIXED * 2 + i * 56) * m_ratioTo480Y, 320.0f * m_ratioTo320X, 56.0f * m_ratioTo480Y) ;
				button.setAlpha(1.0f);
			}
			button.clearAnimation();
		}
	}
	
	public void animateToHomeScreen()
	{
		m_occasionStack.clear();
		
		if(m_plRow >= 0)
		{
			//stop();
		}
		
		AnimationSet animSet = null;
		
		
		//hide second buttons
		for(int i = 0; i < m_secondButtons.size(); i++)
		{
			TextView button = m_secondButtons.get(i);
			Point pos = getElemPos(button);
			button.setAlpha(1.0f);
			animSet = addMoveResizeAnimation(500, 	button,		 0.0f * m_ratioTo320X,  pos.y + BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 
																320f * m_ratioTo320X, 129.0f * m_ratioTo480Y,	false) ;
			animSet.startNow();
		}
		
		//hide third buttons
		for(int i = 0; i < m_thirdButtons.size(); i++)
		{
			TextView button = m_thirdButtons.get(i);
			Point pos = getElemPos(button);
			button.setAlpha(1.0f);
			animSet = addMoveResizeAnimation(500, 	button,		0.0f * m_ratioTo320X,  pos.y + (BUTTON_HIDDEN_OFFSET - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y, 
																320f * m_ratioTo320X,  56.0f * m_ratioTo480Y,	false) ;
			animSet.startNow();
		}
		
		animSet.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation animation) {
				
				m_thirdButtons.clear();
				m_secondButtons.clear();
				
				m_rlScroller.setVisibility(View.INVISIBLE);
				m_rlScroller.removeAllViews();
				
				
				AnimationSet animSetNew;
				
				setButtonsHidden(false);
				
				addMoveResizeAnimation(		500, 	m_ivBtnMood, 		  29.0f * m_ratioTo320X,   13.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnCelebration,	 175.0f * m_ratioTo320X,   13.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnTheme, 		  29.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnEvent, 		 175.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnSports, 		  29.0f * m_ratioTo320X,  289.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				animSetNew = 
				addMoveResizeAnimation( 	500, 	m_ivBtnHoliday,		 175.0f * m_ratioTo320X,  289.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				
				animSetNew.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation) {
						positionFirstButtons(false);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						
					}

					@Override
					public void onAnimationStart(Animation animation) {
						
					}
				});
				
				
				//table.reloadData
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				
			}
		});
		m_level = 1;
	}
	
	//Level showing functions
	public void showSecondLevel()
	{
		
		//animation1 0.5s
		AnimationSet animSet;
		
		
		addMoveResizeAnimation(		500, 	m_ivBtnMood, 		-117.0f * m_ratioTo320X, -115.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnCelebration,	 320.0f * m_ratioTo320X, -115.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnTheme, 		-117.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnEvent, 		 320.0f * m_ratioTo320X,  151.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnSports, 		-117.0f * m_ratioTo320X,  416.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		animSet = 
		addMoveResizeAnimation( 	500, 	m_ivBtnHoliday,		 320.0f * m_ratioTo320X,  416.0f * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		
		animSet.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation animation) {
				
				Log.v(LOGTAG, "First Button's Animation onAnimationEnd");
				positionFirstButtons(true);

				AnimationSet animSetNew = null;
				
				m_rlScroller.setVisibility(View.VISIBLE);
				
				//animation2 0.5s
				for(int i = 0; i < m_secondButtons.size(); i++)
				{
					TextView button = m_secondButtons.get(i);
					Point pos = getElemPos(button);
					button.setAlpha(1.0f);
					animSetNew = addMoveResizeAnimation(500, 	button,		 0.0f * m_ratioTo320X,  pos.y - 480.0f * m_ratioTo480Y, 320f * m_ratioTo320X, 129.0f * m_ratioTo480Y,	true) ;
					animSetNew.startNow();
				}
				
				animSetNew.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation) {
						Log.v(LOGTAG, "Second Button's Animation onAnimationEnd");
						
						//animation2 0.5s
						positionSecondButtons(false, null);
						m_firstButton.setAlpha(1);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						Log.v(LOGTAG, "Second Button's Animation onAnimationRepeat");
					}

					@Override
					public void onAnimationStart(Animation animation) {
						Log.v(LOGTAG, "Second Button's Animation onAnimationStart");
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "First Button's Animation onAnimationRepeat");
			}
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "First Button's Animation onAnimationStart");
			}
		});
		
		animSet.setDuration(500);
		animSet.startNow();
		
		m_level = 2;
	}
	
	public void showThirdLevel(View button)
	{
		if(m_level == 2)
		{
			int buttonTag = 0;
			
			for(int i = 0; i < m_secondButtons.size(); i++)
			{
				if(button == m_secondButtons.get(i))
				{
					buttonTag = i;
					break;
				}
			}
			
			m_replaySong = false;
			Occasion parent = m_occasionStack.get(0);
	        Occasion child = parent.m_children.get(buttonTag);
	        pushOccasion(child);
	        
	        final TextView tvButton = (TextView)button;
	        tvButton.setTextSize(32.0f);
	        
	        //second buttons disappears (animation 0.5 start)
	        
	        //leaves selected second button.
	        m_secondRect = getElemPosSize(tvButton);
	        AnimationSet animSet = addMoveResizeAnimation(500, 	tvButton, 0.0f * m_ratioTo320X,   BUTTON_HEIGHT_FIXED * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y,	true) ;
			animSet.startNow();
			
			//let the rest of buttons disappear!
	        for(int i = 0; i < m_secondButtons.size(); i++)
			{
	        	if(i != buttonTag)
	        	{
	        		View b = m_secondButtons.get(i);
	        		Point point = getElemPos(b);
	        		addMoveResizeAnimation(500, b,	0.0f * m_ratioTo320X,   point.y + BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 320f * m_ratioTo320X, 129.0f * m_ratioTo480Y,	false) ;
	        	}
			}
	        
	        animSet.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationEnd(Animation animation) {
					
					tvButton.clearAnimation();
					setElemPosSize(tvButton, 0.0f * m_ratioTo320X,   BUTTON_HEIGHT_FIXED * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y);
					positionSecondButtons(true, tvButton);
					
					Log.v(LOGTAG, "Second Button's Disappear Animation onAnimationEnd");
					
					//third buttons appears! animation with 0.5s duration
					m_firstButton.setAlpha(1);
					AnimationSet animSetNew = null;
					
					for(int i = 0; i < m_thirdButtons.size(); i++)
					{
						View b = m_thirdButtons.get(i);
			        	b.setAlpha(1.0f);
			        	Point point = getElemPos(b);
						animSetNew = addMoveResizeAnimation(500, 	b, 	0.0f * m_ratioTo320X,  point.y - (BUTTON_HIDDEN_OFFSET - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y, 320f * m_ratioTo320X, 56.0f * m_ratioTo480Y,	true) ;
						animSetNew.startNow();
					}
					
					animSetNew.setAnimationListener(new AnimationListener()
					{

						@Override
						public void onAnimationEnd(Animation animation) {
							positionThirdButtons(false, null);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
						
					}
					);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					Log.v(LOGTAG, "Third Button's Animation onAnimationRepeat");
				}

				@Override
				public void onAnimationStart(Animation animation) {
					Log.v(LOGTAG, "Third Button's Animation onAnimationStart");
				}
			});
	        
	        m_level = 3;
		}
		else if( m_level == 3 || m_level == 4)
		{
			popOccasion();
			if(m_plRow >= 0)
			{
				
				//stop();
			}
			
			//animation starts 0.5s
			for(int i = 0; i < m_thirdButtons.size(); i++)
	        {
	        	View b = m_thirdButtons.get(i);
	        	b.setAlpha(0.0f);
	        	Point point = getElemPos(b);
	        	setElemPosSize(b, 0.0f * m_ratioTo320X,  point.y + 346 * m_ratioTo480Y, 320f * m_minRatio, 56.0f * m_minRatio) ;
	        }
			
			//completed, starts this
			for(int i = 0; i < m_thirdButtons.size(); i++)
	        {
				View b = m_thirdButtons.get(i);
				m_rlContent.removeView(b);
	        }
			
			//starts anim
			for(int i = 0; i < m_secondButtons.size(); i++)
	        {
				View b = m_secondButtons.get(i);
				Point point = getElemPos(b);
	        	setElemPosSize(b, 0.0f * m_ratioTo320X,  point.y - 381 * m_ratioTo480Y, 320f * m_minRatio, 129.0f * m_minRatio) ;
	        	b.setAlpha(1.0f);
	        }
			
			((TextView)button).setTextSize(100);
			//table.reloadData();
			m_thirdButtons.clear();
			m_level = 2;
		}
	}
	
	private void setElemSize(View view, int width,int height)
    {
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.width = width;
    	paramold.height = height;
    	view.setLayoutParams(paramold);
    }
    
    private void setElemPos(View view, int left, int top)
    {
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.setMargins(left - paramold.width / 2, top - paramold.height / 2, 0, 0);
    	view.setLayoutParams(paramold);
    }
    
    private Rect getElemPosSize(View view)
    {
    	Rect rt = new Rect();
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	rt.left = paramold.leftMargin;
    	rt.top = paramold.topMargin;
    	rt.right = paramold.width + rt.left;
    	rt.bottom = paramold.height + rt.top;
    	
    	return rt;
    }
    private Point getElemPos(View view)
    {
    	Point pos = new Point();
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	pos.x = paramold.leftMargin;
    	pos.y = paramold.topMargin;
    	return pos;
    }
    
    private void setElemPosSize(View view, float left, float top, float width, float height)
    {
    	//Log.v(LOGTAG, "setElemPosSize left=" + left + " top=" + top + " width="+ width + " height="+height);
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.width = (int)width;
    	paramold.height = (int)height;
    	paramold.setMargins((int)left, (int)top , 0, 0);
    	view.setLayoutParams(paramold);
    }
    
    
	private void initView()
    {
		
		m_rlContent = (RelativeLayout)findViewById(R.id.rlOccasionContent);
		m_rlNavBar = (RelativeLayout)findViewById(R.id.rlNavBar);
		
		// Navigation Bar
		m_ivBtnNavDone = (ImageView)findViewById(R.id.ivNavBtnDone);
		m_ivBtnNavPlaylist = (ImageView)findViewById(R.id.ivNavBtnPlaylist);
		m_ivBtnNavRemove = (ImageView)findViewById(R.id.ivNavBtnRemoveAll);
		
		
		// indicator
		m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		
		// home buttons
		if(m_ivBtnMood == null){
    		m_ivBtnMood = new ImageView(OccasionActivity.this);
    		m_rlContent.addView(m_ivBtnMood);
    		m_ivBtnMood.setOnClickListener(m_OnFirstButtonClickListener);
    		m_ivBtnMood.setImageResource(R.drawable.occasion_mood);
    	}
		if(m_ivBtnCelebration == null)
		{
    		m_ivBtnCelebration = new ImageView(OccasionActivity.this);
    		m_rlContent.addView(m_ivBtnCelebration);
    		m_ivBtnCelebration.setOnClickListener(m_OnFirstButtonClickListener);
    		m_ivBtnCelebration.setImageResource(R.drawable.occasion_celebration);
		}
		if(m_ivBtnTheme == null) 
		{
			m_ivBtnTheme = new ImageView(OccasionActivity.this);
			m_rlContent.addView(m_ivBtnTheme);
			m_ivBtnTheme.setOnClickListener(m_OnFirstButtonClickListener);
			m_ivBtnTheme.setImageResource(R.drawable.occasion_themes);
		}
		if(m_ivBtnEvent == null)
    	{
    		m_ivBtnEvent = new ImageView(OccasionActivity.this);
    		m_rlContent.addView(m_ivBtnEvent);
    		m_ivBtnEvent.setOnClickListener(m_OnFirstButtonClickListener);
    		m_ivBtnEvent.setImageResource(R.drawable.occasion_currentevents);
    	}
		if(m_ivBtnSports == null)
		{
    		m_ivBtnSports = new ImageView(OccasionActivity.this);
    		m_rlContent.addView(m_ivBtnSports);
    		m_ivBtnSports.setOnClickListener(m_OnFirstButtonClickListener);
    		m_ivBtnSports.setImageResource(R.drawable.occasion_sports);
		}
		if(m_ivBtnHoliday == null)
    	{
    		m_ivBtnHoliday = new ImageView(OccasionActivity.this);
    		m_rlContent.addView(m_ivBtnHoliday);
    		m_ivBtnHoliday.setOnClickListener(m_OnFirstButtonClickListener);
    		m_ivBtnHoliday.setImageResource(R.drawable.ocassion_holiday);
    	}
		
		//first button
		if(m_firstButton == null)
        {
	        m_firstButton = new TextView(this);
	        m_firstButton.setAlpha(0);
	        m_firstButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	        m_rlContent.addView(m_firstButton);
	        m_firstButton.setOnClickListener(m_OnFirstButtonClickListener);
	    }
		
		//scroller
		if(m_rlScroller == null)
		{
			m_rlScroller = new RelativeLayout(this);
			m_rlContent.addView(m_rlScroller);
		}
		final ViewTreeObserver vto = m_rlContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	m_contentWidth = m_rlContent.getWidth();
            	m_contentHeight = m_rlContent.getHeight();
            	m_ratioTo320X = (float)m_contentWidth / 320.0f;
            	m_ratioTo480Y = (float)m_contentHeight / 416.0f;
            	
            	m_minRatio = Math.min(m_ratioTo320X, m_ratioTo480Y);
            	
            	int buttonSize = (int)(115 * m_minRatio);
            	
            	// Occasion Buttons
            	setElemPosSize(m_ivBtnMood, 		(int)( 29 * m_ratioTo320X), 	(int)(013 * m_ratioTo480Y), buttonSize, buttonSize);
            	setElemPosSize(m_ivBtnCelebration, 	(int)(175 * m_ratioTo320X), 	(int)(013 * m_ratioTo480Y), buttonSize, buttonSize);
            	setElemPosSize(m_ivBtnTheme, 		(int)( 29 * m_ratioTo320X), 	(int)(151 * m_ratioTo480Y), buttonSize, buttonSize);
            	setElemPosSize(m_ivBtnEvent, 		(int)(175 * m_ratioTo320X), 	(int)(151 * m_ratioTo480Y), buttonSize, buttonSize);
            	setElemPosSize(m_ivBtnSports, 		(int)( 29 * m_ratioTo320X), 	(int)(289 * m_ratioTo480Y), buttonSize, buttonSize);
            	setElemPosSize(m_ivBtnHoliday, 		(int)(175 * m_ratioTo320X), 	(int)(289 * m_ratioTo480Y), buttonSize, buttonSize);
            	
            	//first button
            	setElemPosSize(m_firstButton, 		(int)(  0 * m_ratioTo320X), 	(int)(  0 * m_ratioTo480Y),  m_contentWidth, (int)(35 * m_ratioTo480Y) );
            	m_firstButton.setTextSize(32 ); //setTextSize(32 * m_minRatio);
            	
            	//scroller position
            	setElemPosSize(m_rlScroller, 		(int)(  0 * m_ratioTo320X), 	(int)(  35 * m_ratioTo480Y),  m_contentWidth, m_contentHeight - (int)(35 * m_ratioTo480Y) );
            	
            	m_rlContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	
            	setButtonsHidden(true);
            }
        });
        
        
    }
	
	
	
	private void setButtonsHidden(boolean hidden)
	{
		int idx;
		if(hidden == false)
			idx = View.VISIBLE;
		else
			idx = View.INVISIBLE;
			
		m_ivBtnMood.setVisibility(idx);
		m_ivBtnCelebration.setVisibility(idx);
		m_ivBtnTheme.setVisibility(idx);
		m_ivBtnEvent.setVisibility(idx);
		m_ivBtnSports.setVisibility(idx);
		m_ivBtnHoliday.setVisibility(idx);
	}
	
	
	boolean m_bRunning = false; 
	Thread m_rotateImagesTimer = new Thread(new Runnable()
	{
		public void run()
		{
			while(m_bRunning == true)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						updateOccasionImage();
					}
				});
				
				try {
					Thread.sleep((long) (OCCASION_IMAGE_SWITCH_DELAY * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
	
	protected OnClickListener m_OnFirstButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(v == m_firstButton)
			{
				animateToHomeScreen();
			}
			else
				loadSecondLevel(v);
		}
    };
    
	protected OnClickListener m_OnSecondButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {			
			showThirdLevel(v);
		}
    };
    
    protected OnClickListener m_OnThirdButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			Log.v(LOGTAG,"m_OnThirdButtonClickListener called");
			//loadPlaylist(v);
		}
    };

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	m_bRunning = false;
    	
    }
    
    //enum
    //public enum RFOccasion {
    public static final int RFOccasionMood = 0;
    public static final int RFOccasionCelebration = 1; 
    public static final int RFOccasionThemes = 2;
    public static final int RFOccasionCurrentEvents = 3;
    public static final int RFOccasionSports = 4;
    public static final int RFOccasionHoliday = 5;
    //};
}
