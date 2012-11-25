package com.rumblefish.friendlymusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rumblefish.friendlymusic.api.Occasion;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.api.Producer;
import com.rumblefish.friendlymusic.api.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.view.RFScrollView;
import com.rumblefish.friendlymusic.view.RFTextView;
import com.rumblefish.friendlymusic.view.SongListView;

public class OccasionActivity  extends Activity {
	
	public static final String 	LOGTAG = "OCCASIONACTIVITY";
	public static final String 	OCCASION_IMAGE_CACHE_PATH = "occasion_image_cache";
	public static final String  OCCASION_IMAGE_FILE_PREFIX = "occasions";
	public static final int 	OCCASION_IMAGE_DEPTH = 4;
	public static final float 	OCCASION_IMAGE_SWITCH_DELAY = 2.0f; // seconds

	public static final int 	BUTTON_TEXT_MAXSIZE = 100;
	public static final int		BUTTON_HIDDEN_OFFSET = 200;
	public static final int		BUTTON_HEIGHT_FIXED = 35;
	public static final int		BUTTON_SECOND_HEIGHT = 129;
	public static final int		BUTTON_THIRD_HEIGHT = 56;
	public static final int 	BUTTON_BORDER_WIDTH = 2;
	
	// member variables
	RelativeLayout	m_rlContent;
	RelativeLayout	m_rlNavBar;
	RFScrollView	m_svScroller;
	RelativeLayout	m_rlScrollContent;
	
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
	RFTextView 				m_firstButton;
	
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
	float 	m_textRatio;
	
	
	//occasions
	ArrayList<Integer> 						m_occasionKeys;
	HashMap<Integer, ArrayList<Bitmap>> 	m_occasionImageDict;
	ArrayList<Occasion>						m_occasions;
	ArrayList<Occasion>						m_occasionStack;
	
	Occasion				m_displayedOccasion;
	ArrayList<Playlist>		m_displayedPlaylists;
	
	
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
	
	boolean m_bAnimating = false;
	
	// song list view
	SongListView m_lvPlaylists;
	
	
	@SuppressLint("UseSparseArrays")
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
        m_occasionKeys.add(RFOccasionHoliday);
        
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
	
	public ImageView buttonForOccasion(int key)
	{
		if(key == RFOccasionMood)
		{
			return m_ivBtnMood;
		}
		else if(key == RFOccasionCelebration)
		{
			return m_ivBtnCelebration;
		}
		else if(key == RFOccasionThemes)
		{
			return m_ivBtnTheme;
		}
		else if(key == RFOccasionCurrentEvents)
		{
			return m_ivBtnEvent;
		}
		else if(key == RFOccasionSports)
		{
			return m_ivBtnSports;
		}
		else if(key == RFOccasionHoliday)
		{
			return m_ivBtnHoliday;
		}
		return null;
	}
	
	public void updateOccasionImage(boolean updateAll)
	{
		Random rand = new Random();
		if(updateAll == true)
		{
			Iterator<Integer> iterator = m_occasionImageDict.keySet().iterator();
			while(iterator.hasNext())
			{
				Integer key = iterator.next();
				ArrayList<Bitmap> imgs = m_occasionImageDict.get(key);
				
				int size = imgs.size();
				if(size > 0)
				{
					ImageView button = buttonForOccasion(key);
					BitmapDrawable bmDraw = new BitmapDrawable(imgs.get(rand.nextInt(size)));
					button.setBackgroundDrawable(bmDraw);
				}
			}
		}
		else
		{
			int dictsize = m_occasionImageDict.size(); 
			int n = rand.nextInt(dictsize);
			int idx = 0;
			
			Iterator<Integer> iterator = m_occasionImageDict.keySet().iterator();
			while(iterator.hasNext())
			{
				Integer key = iterator.next();
				if(idx == n)
				{
					ArrayList<Bitmap> imgs = m_occasionImageDict.get(key);
					
					int size = imgs.size();
					if(size > 0)
					{
						ImageView button = buttonForOccasion(key);
						BitmapDrawable bmDraw = new BitmapDrawable(imgs.get(rand.nextInt(size)));
						button.setBackgroundDrawable(bmDraw);
					}
					break;
				}
				idx++;
			}
		}
	}
	
	public void getOccasionsFromServer()
	{
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		m_pbActivityIndicator.bringToFront();
		
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
				
				@SuppressWarnings("unchecked")
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
				Toast.makeText(OccasionActivity.this, R.string.toast_connection_fail, Toast.LENGTH_LONG).show();
				getOccasionsFromServer();
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
		
		updateOccasionImage(true);
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
	
	public void recycleOccasionImages()
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
				if(bm.isMutable())
					bm.recycle();
			}
		}
	}
	
	
	
	//Levels
	public void loadSecondLevel(View button)
	{
		if(m_level == 1)
		{
//			button.setAlpha(0.5f);
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
	
	private void scrollerContentResize(float height)
	{
		m_rlScrollContent.getLayoutParams().height = (int)height;
		//Log.v(LOGTAG, "rlContentView set height as " + height + " and now it is " + m_rlScrollContent.getHeight());
		
		if(height < m_svScroller.getHeight())
		{
			m_svScroller.setScrollingEnabled(false);
		}
		else
		{
			m_svScroller.setScrollingEnabled(true);
		}
		
		setElemPosSize(m_lvPlaylists, 0, height + BUTTON_HEIGHT_FIXED * m_ratioTo480Y, m_contentWidth, (480 - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y - height);
//		setElemPosSize(m_pbActivityIndicator, m_contentWidth / 2 - m_pbActivityIndicator.getWidth(), height / 2 + BUTTON_HEIGHT_FIXED * m_ratioTo480Y, 
//												m_pbActivityIndicator.getWidth(), m_pbActivityIndicator.getHeight());
		
//		m_pbActivityIndicator.bringToFront();
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
			TextView button = new RFTextView(this);
			
			m_rlScrollContent.addView(button);
			button.setBackgroundColor(m_secondLevelColor);
			button.setTextSize(100 * m_textRatio);
			button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			button.setTextColor(m_secondFontColor);
			button.setText(child.m_name);
			button.setClickable(true);
			button.setOnClickListener(m_OnSecondButtonClickListener);
			
			m_secondButtons.add(button);
		}
		
		positionSecondButtons(true, null);
		
		scrollerContentResize((BUTTON_SECOND_HEIGHT * occasion.m_children.size() * m_ratioTo480Y));
		
	}
	
	private void pushOccasion(Occasion occasion)
	{
		m_occasionStack.add(occasion);
		for( int i = 0; i < occasion.m_children.size(); i++)
		{
			Occasion child = occasion.m_children.get(i);
			
			TextView button = new RFTextView(this);
			m_rlScrollContent.addView(button);
			
			button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			button.setBackgroundColor(m_thirdLevelColor);
			button.setTextSize(80 * m_textRatio);
			button.setTextColor(m_thirdFontColor);
			button.setText(child.m_name.toLowerCase());
			button.setClickable(true);
			button.setOnClickListener(m_OnThirdButtonClickListener);

			
			m_thirdButtons.add(button);
			
		}
		
		
		positionThirdButtons(true, null);
		
		scrollerContentResize((BUTTON_THIRD_HEIGHT * occasion.m_children.size() + BUTTON_HEIGHT_FIXED) * m_ratioTo480Y);
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
		
		MoveResizeAnimation animMoveResize = new MoveResizeAnimation(view, duration, rt.width(), targetWidth, rt.height(), targetHeight,
																					rt.left, targetX, rt.top, targetY);
		animMoveResize.setFillAfter(true);
		
//		float alpha = view.getAlpha();
//		AlphaAnimation animFade = null; 
//		if(alpha == 0 && fadeIn == true) 
//		{
//			animFade = new AlphaAnimation(0.0f, 1.0f);
//		}
//		else if(alpha == 1 && fadeIn == false)
//		{
//			animFade = new AlphaAnimation(1.0f, 0.0f);
//		}
		
		AlphaAnimation animFade = null; 
		if(fadeIn == true) 
		{
			animFade = new AlphaAnimation(0.0f, 1.0f);
		}
		else if(fadeIn == false)
		{
			animFade = new AlphaAnimation(1.0f, 0.0f);
		}
		

		newSet.addAnimation(animMoveResize);
		
		if(animFade != null)
		{
			animFade.setDuration(duration);
			newSet.addAnimation(animFade);
		}
		
		view.clearAnimation();
		view.setAnimation(newSet);
		
		return newSet;
	}
	
	
	public float homeButtonPos[][] = new float[][]{
			{29,  013, -117, -115},
			{175, 013,  320, -115},
			{29,  151, -117,  151},
			{175, 151,  320,  151},
			{29,  289, -117,  416},
			{175, 289,  320,  416},
	};
	
	public void positionFirstButtons(boolean hidden)
	{
		if(hidden == true)
		{
			setElemPosSize(m_ivBtnMood, 		homeButtonPos[0][2] * m_ratioTo320X,  homeButtonPos[0][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnCelebration, 	homeButtonPos[1][2] * m_ratioTo320X,  homeButtonPos[1][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnTheme, 		homeButtonPos[2][2] * m_ratioTo320X,  homeButtonPos[2][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnEvent, 		homeButtonPos[3][2] * m_ratioTo320X,  homeButtonPos[3][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnSports, 		homeButtonPos[4][2] * m_ratioTo320X,  homeButtonPos[4][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			setElemPosSize(m_ivBtnHoliday,		homeButtonPos[5][2] * m_ratioTo320X,  homeButtonPos[5][3] * m_ratioTo480Y, 115.0f * m_minRatio, 115.0f * m_minRatio) ;
			
			setButtonsHidden(true);
		}
		else
		{
			// Occasion Buttons
        	setElemPosSize(m_ivBtnMood, 		(int)( homeButtonPos[0][0] * m_ratioTo320X), 	(int)(homeButtonPos[0][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnCelebration, 	(int)( homeButtonPos[1][0] * m_ratioTo320X), 	(int)(homeButtonPos[1][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnTheme, 		(int)( homeButtonPos[2][0] * m_ratioTo320X), 	(int)(homeButtonPos[2][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnEvent, 		(int)( homeButtonPos[3][0] * m_ratioTo320X), 	(int)(homeButtonPos[3][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnSports, 		(int)( homeButtonPos[4][0] * m_ratioTo320X), 	(int)(homeButtonPos[4][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	setElemPosSize(m_ivBtnHoliday, 		(int)( homeButtonPos[5][0] * m_ratioTo320X), 	(int)(homeButtonPos[5][1] * m_ratioTo480Y), 115.0f * m_minRatio, 115.0f * m_minRatio);
        	
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
				setElemPosSize(button,	0.0f * m_ratioTo320X, BUTTON_HIDDEN_OFFSET * m_ratioTo480Y + (int)(BUTTON_SECOND_HEIGHT * i * m_ratioTo480Y), m_contentWidth, BUTTON_SECOND_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH) ;
//				button.setAlpha(0);
				button.setVisibility(View.INVISIBLE);
			}
			else
			{
				
				setElemPosSize(button,	0.0f * m_ratioTo320X, (int)(i * BUTTON_SECOND_HEIGHT) * m_ratioTo480Y, m_contentWidth, BUTTON_SECOND_HEIGHT * m_ratioTo480Y  - BUTTON_BORDER_WIDTH) ;
//				button.setAlpha(1); 
				button.setVisibility(View.VISIBLE);
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
				setElemPosSize(button,	0.0f * m_ratioTo320X, BUTTON_HIDDEN_OFFSET * m_ratioTo480Y + (int)(i * BUTTON_THIRD_HEIGHT * m_ratioTo480Y), m_contentWidth, BUTTON_THIRD_HEIGHT * m_ratioTo480Y  - BUTTON_BORDER_WIDTH) ;
				button.setVisibility(View.INVISIBLE);
//				button.setAlpha(0);
			}
			else
			{
				setElemPosSize(button,	0.0f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y + (int)( i * BUTTON_THIRD_HEIGHT * m_ratioTo480Y), m_contentWidth, BUTTON_THIRD_HEIGHT * m_ratioTo480Y  - BUTTON_BORDER_WIDTH) ;
				button.setVisibility(View.VISIBLE);
//				button.setAlpha(1);
			}
			button.clearAnimation();
		}
	}
	
	public void animateToHomeScreen()
	{
		Log.v(LOGTAG, "Animate to Home Screen");
		
		m_rlContent.invalidate();
		m_occasionStack.clear();
		
		m_lvPlaylists.stopMedia();
		m_lvPlaylists.setVisibility(View.INVISIBLE);
		
		AnimationSet animSet = null;
		m_bAnimating = true;
		
		//hide second buttons
		for(int i = 0; i < m_secondButtons.size(); i++)
		{
			TextView button = m_secondButtons.get(i);
			Point pos = getElemPos(button);
			animSet = addMoveResizeAnimation(500, 	button,		 0.0f * m_ratioTo320X,  pos.y + BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 
																320f * m_ratioTo320X, BUTTON_SECOND_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
			animSet.startNow();
		}
		
		//hide third buttons
		for(int i = 0; i < m_thirdButtons.size(); i++)
		{
			TextView button = m_thirdButtons.get(i);
			Point pos = getElemPos(button);
			animSet = addMoveResizeAnimation(500, 	button,		0.0f * m_ratioTo320X,  pos.y + (BUTTON_HIDDEN_OFFSET - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y, 
																320f * m_ratioTo320X,  BUTTON_THIRD_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
			animSet.startNow();
		}
		
		animSet.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation animation) {
				
				//hides first button
				m_firstButton.setVisibility(View.INVISIBLE);
				
				//removes all buttons!
				m_thirdButtons.clear();
				m_secondButtons.clear();
				
				m_svScroller.setVisibility(View.INVISIBLE);
				m_rlScrollContent.removeAllViews();
				
				
				//home buttons animate to appear
				AnimationSet animSetNew;
				
				setButtonsHidden(false);
				
				addMoveResizeAnimation(		500, 	m_ivBtnMood, 		 homeButtonPos[0][0]  * m_ratioTo320X,  homeButtonPos[0][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnCelebration,	 homeButtonPos[1][0]  * m_ratioTo320X,  homeButtonPos[1][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnTheme, 		 homeButtonPos[2][0]  * m_ratioTo320X,  homeButtonPos[2][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnEvent, 		 homeButtonPos[3][0]  * m_ratioTo320X,  homeButtonPos[3][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				addMoveResizeAnimation( 	500, 	m_ivBtnSports, 		 homeButtonPos[4][0]  * m_ratioTo320X,  homeButtonPos[4][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				animSetNew = 
				addMoveResizeAnimation( 	500, 	m_ivBtnHoliday,		 homeButtonPos[5][0]  * m_ratioTo320X,  homeButtonPos[5][1]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	true) ;
				
				animSetNew.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation) {
						positionFirstButtons(false);
						m_rlContent.invalidate();
						m_bAnimating = false;
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
		
		//playlist hide
		m_lvPlaylists.stopMedia();
		m_lvPlaylists.removeAll();
		m_lvPlaylists.setVisibility(View.INVISIBLE);
		
	}
	
	//Level showing functions
	public void showSecondLevel()
	{
		
		m_bAnimating = true;
		m_rlContent.invalidate();
		
		//animation1 0.5s
		AnimationSet animSet;
		
		
		addMoveResizeAnimation(		500, 	m_ivBtnMood, 		homeButtonPos[0][2]  * m_ratioTo320X, homeButtonPos[0][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnCelebration,	homeButtonPos[1][2]  * m_ratioTo320X, homeButtonPos[1][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnTheme, 		homeButtonPos[2][2]  * m_ratioTo320X, homeButtonPos[2][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnEvent, 		homeButtonPos[3][2]  * m_ratioTo320X, homeButtonPos[3][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		addMoveResizeAnimation( 	500, 	m_ivBtnSports, 		homeButtonPos[4][2]  * m_ratioTo320X, homeButtonPos[4][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		animSet = 
		addMoveResizeAnimation( 	500, 	m_ivBtnHoliday,	    homeButtonPos[5][2]  * m_ratioTo320X, homeButtonPos[5][3]  * m_ratioTo480Y, 115.0f * m_minRatio, 		115.0f * m_minRatio,	false) ;
		
		animSet.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation animation) {
				
//				Log.v(LOGTAG, "First Button's Animation onAnimationEnd");
				positionFirstButtons(true);

				AnimationSet animSetNew = null;
				
				m_svScroller.setVisibility(View.VISIBLE);
				
				//second buttons animate to appear!
				for(int i = 0; i < m_secondButtons.size(); i++)
				{
					TextView button = m_secondButtons.get(i);
//					button.setAlpha(1);
					button.setVisibility(View.VISIBLE);
					button.invalidate();
					
					Point pos = getElemPos(button);
					animSetNew = addMoveResizeAnimation(500, 	button,		 0.0f,  pos.y - BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_SECOND_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	true) ;
					animSetNew.startNow();
				}
				
				animSetNew.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation) {
//						Log.v(LOGTAG, "Second Button's Animation onAnimationEnd");
						
						//animation2 0.5s
						positionSecondButtons(false, null);
						scrollerContentResize(m_secondButtons.size() * BUTTON_SECOND_HEIGHT * m_ratioTo480Y);
						m_firstButton.setVisibility(View.VISIBLE);
						
						m_rlContent.invalidate();
						m_bAnimating = false;
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
//						Log.v(LOGTAG, "Second Button's Animation onAnimationRepeat");
					}

					@Override
					public void onAnimationStart(Animation animation) {
//						Log.v(LOGTAG, "Second Button's Animation onAnimationStart");
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
//				Log.v(LOGTAG, "First Button's Animation onAnimationRepeat");
			}
			@Override
			public void onAnimationStart(Animation animation) {
//				Log.v(LOGTAG, "First Button's Animation onAnimationStart");
			}
		});
		
		animSet.setDuration(500);
		animSet.startNow();
		
		//playlist
		m_lvPlaylists.stopMedia();
		m_lvPlaylists.removeAll();
		m_lvPlaylists.setVisibility(View.INVISIBLE);
		
		m_level = 2;
	}
	
	public void showThirdLevel(final View button)
	{
		m_rlContent.invalidate();
		
		if(m_level == 2)
		{
			
			m_bAnimating = true;
			
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

	        //second buttons disappears (animation 0.5 start)
	        
	        //leaves selected second button.
	        m_secondRect = getElemPosSize(tvButton);
	        tvButton.setTextSize(32.0f * m_textRatio);
	        AnimationSet animSet = addMoveResizeAnimation(500, 	tvButton, 0,   0 , 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y  - BUTTON_BORDER_WIDTH, true) ;
			animSet.startNow();
			
			//let the rest of buttons disappear!
	        for(int i = 0; i < m_secondButtons.size(); i++)
			{
	        	if(i != buttonTag)
	        	{
	        		View b = m_secondButtons.get(i);
	        		Point point = getElemPos(b);
	        		addMoveResizeAnimation(500, b,	0, point.y + BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_SECOND_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
	        	}
			}
	        
	        animSet.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationEnd(Animation animation) {
					
					positionSecondButtons(true, tvButton);

					tvButton.clearAnimation();
					setElemPosSize(tvButton, 0,   0, 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y  - BUTTON_BORDER_WIDTH);

//					Log.v(LOGTAG, "Second Button's Disappear Animation onAnimationEnd");

					//third buttons appears! animation with 0.5s duration
					m_firstButton.setVisibility(View.VISIBLE);
					AnimationSet animSetNew = null;
					
					for(int i = 0; i < m_thirdButtons.size(); i++)
					{
						View b = m_thirdButtons.get(i);
//						b.setAlpha(1);
						b.setVisibility(View.VISIBLE);
						b.invalidate();
			        	Point point = getElemPos(b);
						animSetNew = addMoveResizeAnimation(500, 	b, 	0,  point.y - (BUTTON_HIDDEN_OFFSET - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_THIRD_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	true) ;
						animSetNew.startNow();
					}
					
					if(animSetNew != null)
					{
						animSetNew.setAnimationListener(new AnimationListener()
						{
	
							@Override
							public void onAnimationEnd(Animation animation) {
								positionThirdButtons(false, null);
								
								scrollerContentResize((m_thirdButtons.size() * BUTTON_THIRD_HEIGHT + BUTTON_HEIGHT_FIXED) * m_ratioTo480Y);
								
								m_rlContent.invalidate();
								m_bAnimating = false;
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
			
			m_bAnimating = true;
			
			popOccasion();
			m_lvPlaylists.stopMedia();
			m_lvPlaylists.removeAll();
			m_lvPlaylists.setVisibility(View.INVISIBLE);
			
			//animation starts 0.5s
			AnimationSet animSet = null;
			
			
			//hide third buttons.
			for(int i = 0; i < m_thirdButtons.size(); i++)
			{
				View b = m_thirdButtons.get(i);
//				b.setAlpha(1); 
				b.setVisibility(View.VISIBLE);
				b.invalidate();
				
	        	Point point = getElemPos(b);
	        	animSet = addMoveResizeAnimation(500, 	b, 	0,  					point.y + (BUTTON_HIDDEN_OFFSET - BUTTON_HEIGHT_FIXED) * m_ratioTo480Y, 
	        												320f * m_ratioTo320X, 	BUTTON_THIRD_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
	        	animSet.startNow();
			}
			
			animSet.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationEnd(Animation animation) {

					//completed, starts this
					//remove third buttons
					for(int i = 0; i < m_thirdButtons.size(); i++)
			        {
						View b = m_thirdButtons.get(i);
						m_rlScrollContent.removeView(b);
			        }
					m_thirdButtons.clear();
					
					
					AnimationSet animSetNew = null;
					//second buttons animate to appear
			        for(int i = 0; i < m_secondButtons.size(); i++)
					{
		        		View b = m_secondButtons.get(i);
		        		
//		        		b.setAlpha(1); 
		        		b.setVisibility(View.VISIBLE);
		        		b.invalidate();
		        		
		        		animSetNew = addMoveResizeAnimation(500, b,	0,  (int)(i * BUTTON_SECOND_HEIGHT * m_ratioTo480Y) , 
		        				320f * m_ratioTo320X, BUTTON_SECOND_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	true) ;
		        		animSetNew.startNow();
					}
			        
			        ((TextView)button).setTextSize(100 * m_textRatio);
			        
			        animSetNew.setAnimationListener(new AnimationListener()
					{
						@Override
						public void onAnimationEnd(Animation animation) {
							positionSecondButtons(false, null);
							
							scrollerContentResize((m_secondButtons.size() * BUTTON_SECOND_HEIGHT) * m_ratioTo480Y);
							
							m_rlContent.invalidate();
							m_bAnimating = false;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
				
			});
			
			m_level = 2;
		}
	}
    
	
	public void updateSongsListView()
	{
		if(m_level == 4)
		{
			m_lvPlaylists.setVisibility(View.VISIBLE);
			m_lvPlaylists.showPlaylists(m_displayedPlaylists, true);
		}
	}
	
	
	private void fetchPlaylistContent(Playlist pl )
	{
		
		Producer getPlaylist = RFAPI.getSingleTone().getPlaylist(pl.m_id);
		
		if(getPlaylist == null)
    		return;
		
		getPlaylist.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				if(m_bRunning)
				{
					m_displayedPlaylists.clear();
					
					Playlist retPl = (Playlist)obj;
					m_displayedPlaylists.add(retPl);
					
					updateSongsListView();
					m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onError() {
				if(m_bRunning)
				{
					m_displayedPlaylists.clear();
					m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				}
			}
    	};
    	getPlaylist.run();
    	
		
	}
	private void fetchPlaylistsForOccasion(Occasion occasion)
	{
		Producer getOccasion = RFAPI.getSingleTone().getOccasions(occasion.m_id);
		
		if(getOccasion == null)
    		return;
		
		getOccasion.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				if(m_bRunning)
				{
					m_displayedOccasion = (Occasion) obj;
					if(m_displayedOccasion != null)
					{
						Log.v(LOGTAG, "fetchPlaylistsForOccasion getOccasion resulted");
						
						ArrayList<Playlist> arrayPlaylist = m_displayedOccasion.m_playlists;
						m_displayedPlaylists = arrayPlaylist;
						
						if(m_displayedPlaylists != null && m_level == 4)
						{
							fetchPlaylistContent(m_displayedPlaylists.get(0));
						}
					}
				}
			}

			@Override
			public void onError() {
				if(m_bRunning)
					m_pbActivityIndicator.setVisibility(View.GONE);
			}
    	};
    	getOccasion.run(); 
	}
	
	private void loadPlaylist(View button)
	{
		
		int buttonTag = 0;
		
		m_rlContent.invalidate();
		
		for(int i = 0; i < m_thirdButtons.size(); i++)
		{
			if(button == m_thirdButtons.get(i))
			{
				buttonTag = i;
				break;
			}
		}

		m_bAnimating = true;
		
		if(m_level == 3)
		{
			
			Occasion parent = m_occasionStack.get(1);
			Occasion child = parent.m_children.get(buttonTag);
			
			fetchPlaylistsForOccasion(child);
			
			final TextView tvButton = ((TextView)button);
			
			//third buttons animate to disappear
	        
			//leaves selected third button.
	        m_thirdRect = getElemPosSize(tvButton);
	        tvButton.setTextSize(32.0f * m_textRatio);
	        AnimationSet animSet = addMoveResizeAnimation(500, 	tvButton, 0,  BUTTON_HEIGHT_FIXED * m_ratioTo480Y , 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y  - BUTTON_BORDER_WIDTH, true) ;
			animSet.startNow();
			
			//let the rest of buttons disappear!
	        for(int i = 0; i < m_thirdButtons.size(); i++)
			{
	        	if(i != buttonTag)
	        	{
	        		View b = m_thirdButtons.get(i);
	        		Point point = getElemPos(b);
	        		addMoveResizeAnimation(500, b,	0, point.y + BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_THIRD_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
	        	}
			}
	        
	        animSet.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationEnd(Animation animation) {
					
					positionThirdButtons(true, tvButton);

					tvButton.clearAnimation();
					setElemPosSize(tvButton, 0,   BUTTON_HEIGHT_FIXED * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_HEIGHT_FIXED * m_ratioTo480Y  - BUTTON_BORDER_WIDTH);
					
					scrollerContentResize((BUTTON_HEIGHT_FIXED * 2) * m_ratioTo480Y);
					
					m_rlContent.invalidate();
					m_bAnimating = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
	        
	        m_level = 4;
	        m_svScroller.setEnabled(false);
		}
		else
		{
			
			m_lvPlaylists.stopMedia();
			m_lvPlaylists.setVisibility(View.INVISIBLE);
			
			final TextView tvButton = ((TextView)button);
			
	        tvButton.setTextSize(80.0f * m_textRatio);
	        AnimationSet animSet = addMoveResizeAnimation(500, 	tvButton, 	m_thirdRect.left,  		m_thirdRect.top , 
	        																320f * m_ratioTo320X, 	BUTTON_THIRD_HEIGHT * m_ratioTo480Y  - BUTTON_BORDER_WIDTH, true) ;
			animSet.startNow();
			
			//let the rest of buttons disappear!
	        for(int i = 0; i < m_thirdButtons.size(); i++)
			{
	        	if(i != buttonTag)
	        	{
	        		View b = m_thirdButtons.get(i);
	        		Point point = getElemPos(b);
	        		addMoveResizeAnimation(500, b,	0, point.y - BUTTON_HIDDEN_OFFSET * m_ratioTo480Y, 320f * m_ratioTo320X, BUTTON_THIRD_HEIGHT * m_ratioTo480Y - BUTTON_BORDER_WIDTH,	false) ;
	        	}
			}
	        
	        
	        animSet.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationEnd(Animation animation) {
					
					positionThirdButtons(false, null);

					tvButton.clearAnimation();
					setElemPosSize(tvButton, m_thirdRect.left,  m_thirdRect.top , 320f * m_ratioTo320X, BUTTON_THIRD_HEIGHT * m_ratioTo480Y  - BUTTON_BORDER_WIDTH);
					
					scrollerContentResize((BUTTON_HEIGHT_FIXED + m_thirdButtons.size() * BUTTON_THIRD_HEIGHT) * m_ratioTo480Y);
					
					m_displayedOccasion = null;
					m_displayedPlaylists = null;
					
					m_rlContent.invalidate();
					m_bAnimating = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
	        
	        m_level = 3;
		}
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
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.width = (int)width;
    	paramold.height = (int)height;
    	paramold.leftMargin = (int)left;
    	paramold.topMargin = (int)top;
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
		
		m_ivBtnNavDone.setOnClickListener(m_OnNavButtonClickListener);
		m_ivBtnNavPlaylist.setOnClickListener(m_OnNavButtonClickListener);
		m_ivBtnNavRemove.setOnClickListener(m_OnNavButtonClickListener);
		
		// indicator
		m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
		m_pbActivityIndicator.setVisibility(View.INVISIBLE);
		
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
	        m_firstButton = new RFTextView(this);
	        m_firstButton.setVisibility(View.INVISIBLE);
	        m_firstButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	        m_rlContent.addView(m_firstButton);
	        m_firstButton.setOnClickListener(m_OnFirstButtonClickListener);
	        
	    }
		
		//scroller
		if(m_svScroller == null)
		{
			m_svScroller = new RFScrollView(this);
			m_rlContent.addView(m_svScroller);
			m_svScroller.setVisibility(View.INVISIBLE);
		}
		if(m_rlScrollContent == null)
		{
			m_rlScrollContent = new RelativeLayout(this);
			m_svScroller.addView(m_rlScrollContent);
		}
		if(m_lvPlaylists == null)
		{
			m_lvPlaylists = new SongListView(this);
			m_rlContent.addView(m_lvPlaylists);
			m_lvPlaylists.setVisibility(View.INVISIBLE);
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
            	m_textRatio =  (float)m_contentWidth / 800.0f;
            	
            	int display_mode = getResources().getConfiguration().orientation;

            	if (display_mode == 1) {
            	    
            	} else {
            	    //setContentView(R.layout.main_land);
            		m_ratioTo480Y  = (float)m_contentWidth / 416.0f * 0.8f ;
            		m_minRatio = Math.min((float) m_contentHeight / 320.0f, (float)m_contentWidth / 416.0f);
            		m_textRatio = m_textRatio * 0.5f;
            		
            		//correct home buttons positions
            		for(int i = 0; i<6; i++)
            		{
            			float tmp;
            			tmp = homeButtonPos[i][0]; homeButtonPos[i][0] = homeButtonPos[i][1]; homeButtonPos[i][1] = tmp;
            			tmp = homeButtonPos[i][2]; homeButtonPos[i][2] = homeButtonPos[i][3]; homeButtonPos[i][3] = tmp;
            			
            			homeButtonPos[i][0] *= 0.9f;//(320.0f / 416.0f);
            			homeButtonPos[i][2] *= 0.9f;//(320.0f / 416.0f);
            		}
            	}
            	
            	
            	
            	// home buttons.
            	positionFirstButtons(false);
            	
            	// first button
            	setElemPosSize(m_firstButton, 		(int)(  0 * m_ratioTo320X), 	(int)(  0 * m_ratioTo480Y),  m_contentWidth, (int)(35 * m_ratioTo480Y) - BUTTON_BORDER_WIDTH);
            	m_firstButton.setTextSize(32 * m_textRatio);
            	
            	// scroller position
            	setElemPosSize(m_svScroller, 		(int)(  0 * m_ratioTo320X), 	(int)(  35 * m_ratioTo480Y),  m_contentWidth, m_contentHeight - (int)(35 * m_ratioTo480Y) );
            	
            	// songlistview
            	setElemPosSize(m_lvPlaylists, 		(int)(  0 * m_ratioTo320X), 	(int)(  70 * m_ratioTo480Y),  m_contentWidth, m_contentHeight - (int)(70 * m_ratioTo480Y) );
            	
            	
            	setButtonsHidden(true);

//            	m_pbActivityIndicator.bringToFront();
            	
            	m_rlContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        
        
    }
	
	
	
	private void setButtonsHidden(boolean hidden)
	{
//		float alpha = 0;
//		if(hidden == false)
//			alpha = 1;
//		else if(hidden == true)
//			alpha = 0;
		
		
//		m_ivBtnMood.setAlpha(alpha);
//		m_ivBtnCelebration.setAlpha(alpha);
//		m_ivBtnTheme.setAlpha(alpha);
//		m_ivBtnEvent.setAlpha(alpha);
//		m_ivBtnSports.setAlpha(alpha);
//		m_ivBtnHoliday.setAlpha(alpha);
		
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
						updateOccasionImage(false);
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
	
	protected OnClickListener m_OnNavButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			//if(m_bAnimating == false && v.getAlpha() == 1)
			if(m_bAnimating == false)
			{
				if(v == m_ivBtnNavDone)
				{
					finish();
				}
				else if (v == m_ivBtnNavPlaylist)
				{
					OccasionActivity.this.m_lvPlaylists.stopMedia();
					
					Intent intent = new Intent(OccasionActivity.this, PlaylistActivity.class);
					startActivity(intent);
				}
				else if (v == m_ivBtnNavRemove)
				{
					
				}
			}
		}
    };
    
    
	protected OnClickListener m_OnFirstButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
//			if(m_bAnimating == false && v.getAlpha() == 1)
			if(m_bAnimating == false)
			{
				if(v == m_firstButton)
				{
					animateToHomeScreen();
				}
				else
				{
					loadSecondLevel(v);
				}
			}
		}
    };
    
	protected OnClickListener m_OnSecondButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
//			if(m_bAnimating == false && v.getAlpha() == 1)
			if(m_bAnimating == false)
			{
				showThirdLevel(v);
			}
		}
    };
    
    protected OnClickListener m_OnThirdButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			Log.v(LOGTAG,"m_OnThirdButtonClickListener called");
			loadPlaylist(v);
		}
    };

    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(this.m_lvPlaylists != null)
    	{
    		//this.m_lvPlaylists.pauseMedia();
    	}
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(this.m_lvPlaylists != null)
    	{
    		this.m_lvPlaylists.updateContent();
    		this.m_lvPlaylists.resumeMedia();
    	}
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	if(m_lvPlaylists != null)
    	{
    		m_lvPlaylists.release();
    	}
    	recycleOccasionImages();
    	
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
    
    
    public class MoveResizeAnimation extends Animation {
        private View mView;
        private float mToHeight;
        private float mFromHeight;

        private float mToWidth;
        private float mFromWidth;

        private float mToX;
        private float mFromX;

        private float mToY;
        private float mFromY;

        public MoveResizeAnimation(View v, int duration, 
        		float fromWidth, float toWidth, float fromHeight, float toHeight,
        		float fromX, float toX, float fromY, float toY) 
        {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            
            mFromX = fromX;
            mToX = toX;
            
            mFromY = fromY;
            mToY = toY;
            
            mView = v;
            setDuration(duration);
        }

        private float interpol(float from, float to, float interpol)
        {
        	return (to - from) * interpol + from;
        }
        
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height = interpol(mFromHeight, mToHeight, interpolatedTime);
            float width = interpol(mFromWidth, mToWidth, interpolatedTime);

            float x = interpol(mFromX, mToX, interpolatedTime);
            float y = interpol(mFromY, mToY, interpolatedTime);
            
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            p.leftMargin = (int)x;
            p.topMargin = (int)y;
            

            mView.requestLayout();
        }
    }
}
