package com.rumblefish.friendlymusic;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rumblefish.friendlymusic.api.LocalPlaylist;
import com.rumblefish.friendlymusic.api.Media;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.api.Producer;
import com.rumblefish.friendlymusic.api.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.api.StaticResources;
import com.rumblefish.friendlymusic.mediaplayer.StreamingMediaPlayer;

public class MoodMap extends Activity implements OnTouchListener{

	public static final String LOGTAG = "MoodMap";
	
	public enum ItemStatus
	{
		NORMAL,
		DOWNLOADING,
		PLAYING
	}
	
	public boolean m_bRunning = false;
	
	RelativeLayout 	m_rlMoodMap;
	ImageView	m_ivSurround;
	ImageView	m_ivBtnDone;
	ImageView	m_ivBtnFilters;
	ImageView	m_ivBtnPlaylist;
	ImageView	m_ivLogo ;
	ImageView	m_ivIcons;
	ImageView	m_ivMoodMap;
	ImageView	m_ivGlow;
	ImageView	m_ivRing;
	ImageView	m_ivCrosshairs;
	ImageView	m_ivSelector;
	ImageView	m_ivMessage;
	ImageView	m_ivFilterMessage;
	
	ListView		m_lvSongs;
	ProgressBar		m_pbActivityIndicator;
	MediaArrayAdapter m_lvSongsAdapter = null;
	
	int m_rlMoodMapSize;
	int m_screenOrientation;
	
	ArrayList<Integer> m_adjacentColors;
	int	m_playingRow = -1;
	boolean m_isPlaying = false;
	boolean m_playlistIsLoading;
	int	m_playlistID = -1;
	int m_selectedCellID = -1;
	Integer m_selectedColor;
	Point m_crosshairPos;
	
	//MediaPlayer m_mediaPlayer = null;
	StreamingMediaPlayer m_mediaPlayer = null;
	
	Playlist m_playlist;
	
	int[][] idArray = 
		{
			{0,  0,  0,  1,  2,  3, 31, 32, 33,  0,  0,  0},
            {0,  0,  4,  5,  6,  7, 34, 35, 36, 37,  0,  0},
            {0,  8,  9, 10, 11, 12, 38, 39, 40, 41, 42,  0},
           {13, 14, 15, 16, 17, 18, 43, 44, 45, 46, 47, 48},
           {19, 20, 21, 22, 23, 24, 49, 50, 51, 52, 53, 54},
           {25, 26, 27, 28, 29, 30, 55, 56, 57, 58, 59, 60},
           {91, 92, 93, 94, 95, 96, 61, 62, 63, 64, 65, 66},
           {97, 98, 99,100,101,102, 67, 68, 69, 70, 71, 72},
          {103,104,105,106,107,108, 73, 74, 75, 76, 77, 78},
            {0,109,110,111,112,113, 79, 80, 81, 82, 83,  0},
            {0,  0,114,115,116,117, 84, 85, 86, 87,  0,  0},
            {0,  0,  0,118,119,120, 88, 89, 90,  0,  0,  0}
		};
	
	
	//Animations
	Animation m_animFadeIn;
	Animation m_animFadeOut;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.moodmap);
        
        initView();        
        
        //init variables
        m_adjacentColors = new ArrayList<Integer>();
        
        
        m_ivGlow.setVisibility(View.INVISIBLE);
        m_ivRing.setVisibility(View.INVISIBLE);
        
//        if(StaticResources.m_selectedColor != 0)
//        {
//        	m_ivGlow.setVisibility(View.VISIBLE);
//        	m_ivRing.setVisibility(View.VISIBLE);
//        }
        
        if(StaticResources.m_crosshairPos != null)
        {
        	setMoodMapElemPos(m_ivSelector, StaticResources.m_crosshairPos.x, StaticResources.m_crosshairPos.y);
        	m_crosshairPos = StaticResources.m_crosshairPos;
        }
        else
        {
        	m_ivSelector.setVisibility(View.INVISIBLE);
        }
        
        m_playingRow = -1;
        
        

        //animations
        m_animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        m_animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        
        //copying playlist.plist to document folder
        //
        //
        m_selectedColor = StaticResources.m_selectedColor;
        if(SettingsUtils.getBoolForKey(this, "fmisused", false) == true)
        {
        	m_ivMessage.setVisibility(View.INVISIBLE);
        }
        else
        {
        	m_ivMessage.setVisibility(View.VISIBLE);
        	m_ivMessage.startAnimation(m_animFadeIn);
        	SettingsUtils.setBoolForKey(this, "fmisused", true);
        }
        
        m_ivFilterMessage.setVisibility(View.INVISIBLE);
        
        //audio session
        //
        
        
        m_pbActivityIndicator.setVisibility(View.INVISIBLE);
        
        
        //media player
        if(StaticResources.m_mediaPlayer != null)
        	m_mediaPlayer = StaticResources.m_mediaPlayer;
        else
        {
        	//m_mediaPlayer = new MediaPlayer();
        	m_mediaPlayer = new StreamingMediaPlayer(this, m_mpCompletionListener, m_mpBufferingUpdateListener,  m_mpErrorListener);
        }
        
//        m_mediaPlayer.setOnCompletionListener(m_mpCompletionListener);
//        m_mediaPlayer.setOnBufferingUpdateListener(m_mpBufferingUpdateListener);
//        m_mediaPlayer.setOnErrorListener(m_mpErrorListener);
        
        if(StaticResources.m_playlist != null)
        {
        	this.m_selectedCellID = StaticResources.m_selectedCellID;
        	m_isPlaying = StaticResources.m_isPlaying;
        	updatePlaylist(StaticResources.m_playlist);
        }
        
        updatePlaylistBtn();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    
    private void setMoodMapElemSize(View view, int width,int height)
    {
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.width = width;
    	paramold.height = height;
    	view.setLayoutParams(paramold);
    }
    
    private void setMoodMapElemPos(View view, int left, int top)
    {
    	RelativeLayout.LayoutParams paramold = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	paramold.setMargins(left - paramold.width / 2, top - paramold.height / 2, 0, 0);
    	view.setLayoutParams(paramold);
    }
    
    private void initView()
    {
    	m_screenOrientation = getResources().getConfiguration().orientation;
    	
    	m_rlMoodMap = (RelativeLayout)findViewById(R.id.rlMoodMap);
    	m_ivSurround 	= (ImageView)findViewById(R.id.ivBgSurround);
    	m_ivBtnDone 	= (ImageView)findViewById(R.id.ivBtnDone);
    	m_ivBtnFilters 	= (ImageView)findViewById(R.id.ivBtnFilters);
    	m_ivBtnPlaylist 	= (ImageView)findViewById(R.id.ivBtnPlaylist);
    	m_ivLogo 	= (ImageView)findViewById(R.id.ivLogo);
    	m_ivIcons 	= (ImageView)findViewById(R.id.ivIcons);
    	m_ivMoodMap	= (ImageView)findViewById(R.id.ivMoodMap);
    	m_ivGlow 	= (ImageView)findViewById(R.id.ivGlow);
    	m_ivRing 	= (ImageView)findViewById(R.id.ivRing);
    	m_ivCrosshairs 	= (ImageView)findViewById(R.id.ivCrosshairs);
    	m_ivSelector 	= (ImageView)findViewById(R.id.ivSelector);
    	m_ivMessage 	= (ImageView)findViewById(R.id.ivMessage);
    	m_ivFilterMessage 	= (ImageView)findViewById(R.id.ivFilterMessage);
    	
    	
    	m_lvSongs 	= (ListView)findViewById(R.id.lvSongs);
    	m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
    	m_pbActivityIndicator.setVisibility(View.INVISIBLE);
    	
    	ViewTreeObserver vto = m_rlMoodMap.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	
            	int width = m_rlMoodMap.getWidth();
            	int height = m_rlMoodMap.getHeight();
            	
            	m_rlMoodMapSize = Math.min(width, height);
            	
            	//set mood map's size; adjust according to the screen orientation
            	LinearLayout.LayoutParams params;
            	if(m_screenOrientation == Configuration.ORIENTATION_PORTRAIT)
            		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_rlMoodMapSize);
            	else
            		params = new LinearLayout.LayoutParams(m_rlMoodMapSize, LinearLayout.LayoutParams.MATCH_PARENT);
            	
            	m_rlMoodMap.setLayoutParams(params);
            	
            	float ratioTo480 = (float)m_rlMoodMapSize / 480;
            	//set mood map's elements' sizes, keep ratio to the original iPhone mood map size;
            	setMoodMapElemSize(m_ivBtnDone,  		(int)(160 * ratioTo480),  (int)(160 * ratioTo480));
            	setMoodMapElemSize(m_ivBtnFilters,  	(int)(160 * ratioTo480),  (int)(160 * ratioTo480));
            	setMoodMapElemSize(m_ivBtnPlaylist,  	(int)(160 * ratioTo480),  (int)(160 * ratioTo480));
            	
            	setMoodMapElemSize(m_ivLogo,  			(int)(155 * ratioTo480),  (int)(45 * ratioTo480));
            	setMoodMapElemSize(m_ivMoodMap,  		(int)(363 * ratioTo480),  (int)(363 * ratioTo480));
            	setMoodMapElemSize(m_ivIcons,  			(int)(453 * ratioTo480),  (int)(453 * ratioTo480));
            	setMoodMapElemSize(m_ivRing,  			(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivGlow,  			(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivCrosshairs,  	(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivSelector,  		(int)(68 * ratioTo480),  (int)(68 * ratioTo480));
            	setMoodMapElemSize(m_ivMessage,  		(int)(292 * ratioTo480),  (int)(84 * ratioTo480));
            	setMoodMapElemSize(m_ivFilterMessage,  		(int)(292 * ratioTo480),  (int)(84 * ratioTo480));
            	
            	if(StaticResources.m_selectedColor != 0)
            	{
            		ringImageByFillingColor(StaticResources.m_selectedColor);
            	}
            	
            	if(StaticResources.m_playlistID != -1 && StaticResources.m_playlist==null)
            	{
            		m_playlistID = StaticResources.m_playlistID;
            		getPlaylistFromServer(); 
            	}
            	
            	m_rlMoodMap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        
        //event handler
        m_ivMoodMap.setOnTouchListener(this);
        
        m_ivBtnDone.setOnClickListener(m_onClickListener);
        m_ivBtnFilters.setOnClickListener(m_onClickListener);
        m_ivBtnPlaylist.setOnClickListener(m_onClickListener);
        
    }

    Bitmap m_ringBitmap = null;
    private void ringImageByFillingColor( int color)
    {
    	ImageView imgView = m_ivRing;
    	
    	if(m_ringBitmap != null)
    	{
    		if(m_ringBitmap.isMutable() == true)
    			m_ringBitmap.recycle();
    	}
    	
    	float ratioTo320 = (float)m_rlMoodMapSize / 320;
    	float ratioTo480 = (float)m_rlMoodMapSize / 480;
    	m_ringBitmap = Bitmap.createBitmap((int)(407 * ratioTo480),  (int)(407 * ratioTo480), Bitmap.Config.ARGB_8888); //Bitmap.createBitmap(imgView.getMeasuredWidth(), imgView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(m_ringBitmap);
    	Paint paint = new Paint();
    	paint.setColor(color);
    	paint.setStyle(Style.FILL);
    	
    	
    	float marginOutX = 15 * ratioTo320;
    	float marginOutY = 14 * ratioTo320;
    	float marginInX = 25 * ratioTo320;
    	float marginInY = 24 * ratioTo320;
    	/*
    	 * paint.setStyle(Style.STROKE);
    	 * paint.setStrokeWidth((marginInX - marginOutX));
    	 * canvas.drawOval(new RectF( (marginOutX + marginInX) / 2, (marginOutY + marginInY) / 2, m_ringBitmap.getWidth() - (marginOutX + marginInX) / 2 , m_ringBitmap.getHeight() - (marginOutY + marginInY) / 2  ), paint);
    	*/
    	
    	Path path = new Path();
    	path.addOval(new RectF( marginOutX, marginOutY, m_ringBitmap.getWidth() - marginOutX, m_ringBitmap.getHeight() - marginOutY ), Path.Direction.CW);
    	path.addOval(new RectF( marginInX, marginInY, m_ringBitmap.getWidth() - marginInX, m_ringBitmap.getHeight() - marginInY ), Path.Direction.CCW);
    	
    	canvas.drawPath(path, paint);
    	
    	/*paint.setColor(0);
    	float marginInX = 25 * ratioTo320;
    	float marginInY = 24 * ratioTo320;
    	canvas.drawOval(new RectF( marginInX, marginInY, m_ringBitmap.getWidth() - marginInX, m_ringBitmap.getHeight() - marginInY ), paint);*/
    	
    	imgView.setImageBitmap(m_ringBitmap);
    }
    
    
    
    @Override
	public boolean onTouch(View view, MotionEvent event) {

    	if(view == m_ivMoodMap)
    	{
    		float ratioTo320 = (float)m_rlMoodMapSize / 320;
    		float curX = event.getX();
    		float ratX = curX / ratioTo320;
    		float curY = event.getY();
    		float ratY = curY / ratioTo320;
    		
    		//Log.i(LOGTAG, " curX = " + curX + " curY = " + curY);
    		
			switch(event.getAction() & MotionEvent.ACTION_MASK)
			{
			case MotionEvent.ACTION_DOWN:
				float d = android.util.FloatMath.sqrt((float)Math.pow(121.0f- ratX, 2) + (float)Math.pow(121.0f - ratY, 2));
			    if( d <= 121.0f)
			    {
			    	m_ivMessage.setVisibility(View.INVISIBLE);
			    	m_ivSelector.setVisibility(View.VISIBLE);
			    	setMoodMapElemPos(m_ivSelector, (int)curX, (int)curY);
			    	
			    	
			    	m_ivRing.setVisibility(View.VISIBLE);
			    	m_ivRing.startAnimation(m_animFadeIn);
			    	
			    	m_ivGlow.setVisibility(View.VISIBLE);
			    	m_ivGlow.startAnimation(m_animFadeIn);
			    	
			    	colorOfPoint(ratX, ratY);
			    	ringImageByFillingColor(m_selectedColor);
			    	
			    }
				break;
			case MotionEvent.ACTION_MOVE:
				d = android.util.FloatMath.sqrt((float)Math.pow(121.0f- ratX, 2) + (float)Math.pow(121.0f - ratY, 2));
			    if( d <= 121.0f)
			    {
			    	setMoodMapElemPos(m_ivSelector, (int)curX, (int)curY);
			    	colorOfPoint(ratX, ratY);
			    	ringImageByFillingColor(m_selectedColor);
			    }
				break;
			case MotionEvent.ACTION_UP:
				if(m_crosshairPos == null)
					m_crosshairPos = new Point();
				m_crosshairPos.x = (int)curX;
				m_crosshairPos.y = (int)curY;
				
				m_ivRing.setVisibility(View.INVISIBLE);
		    	m_ivGlow.setVisibility(View.INVISIBLE);
		    	
		    	m_ivRing.startAnimation(m_animFadeOut);
		    	m_ivGlow.startAnimation(m_animFadeOut);
		    	
		    	
		    	d = android.util.FloatMath.sqrt((float)Math.pow(121.0f- ratX, 2) + (float)Math.pow(121.0f - ratY, 2));
			    if( d <= 121.0f)
			    {
			    	// get the ID
			        int x = (int)(ratX/20.166);
			        int y = (int)(ratY/20.166);
			        m_playlistID = idArray[y][x];
			        m_playingRow = -1;
			        getPlaylistFromServer();
			    }
				break;
			}
    	}
		return true;
    	
	}
    
    private void updatePlaylistBtn()
    {
    	if(LocalPlaylist.sharedPlaylist().m_playlist.size() == 0)
    	{
    		m_ivBtnPlaylist.setSelected(false);
    	}
    	else
    	{
    		m_ivBtnPlaylist.setSelected(true);
    	}
    }
    private void updatePlaylist(Playlist playlist)
    {
    	if(m_lvSongsAdapter != null)
    	{
    		m_lvSongsAdapter.clear();
    		m_lvSongsAdapter = null;
    	}
    	
    	m_playlist = playlist;
    	//this.m_lvSongs.re
    	if(m_playlist != null && m_playlist.m_media != null && m_playlist.m_media.size() > 0)
    	{
    		m_lvSongsAdapter = new MediaArrayAdapter(this, R.layout.play_list_item, m_playlist.m_media);
	        m_lvSongs.setAdapter(m_lvSongsAdapter);
	        m_lvSongs.setOnItemClickListener(m_onItemClickListener);
    	}
    }
    
    Producer m_getMedia = null;
    private void getPlaylistFromServer()
    {
    	RFAPI api = RFAPI.getSingleTone();
    	m_getMedia = api.getPlaylist(m_playlistID + 187);
    	if(m_getMedia == null)
    		return;
    	
    	m_isPlaying = false;
    	if(m_mediaPlayer != null)
    	{
    		m_mediaPlayer.reset();
    	}
    	m_pbActivityIndicator.setVisibility(View.VISIBLE);
    	m_ivBtnDone.setEnabled(false);
    	
    	m_getMedia.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				if(m_bRunning == false)
					return;
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				updatePlaylist((Playlist) obj);
				
//				if(m_playlist.m_media.size() > 0)
//				{
//					int row = (m_playingRow == -1 ? 0 : m_playingRow);
//					m_lvSongs.setSelection(m_playingRow);
//				}
				
				m_ivBtnDone.setEnabled(true);
			}

			@Override
			public void onError() {
				if(m_bRunning == false)
					return;
				
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				m_ivBtnDone.setEnabled(true);
			}
    	};
    	m_getMedia.run();
    }
    
    protected void releaseResource()
    {
    	if( m_mediaPlayer != null)
    	{
    		m_mediaPlayer.reset();
    		m_mediaPlayer = null;
    	}
		m_crosshairPos = null;
		m_playlistID = -1;
		m_playlist = null;
		m_selectedCellID = -1;
		m_selectedColor = 0;
		m_isPlaying = false;
    	
		if( m_getMedia != null )
			m_getMedia.cancel();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	releaseResource();
	    	m_bRunning = false;
	    	setResult(RESULT_OK);
	    	finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	m_bRunning = true;
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(this.m_lvSongsAdapter != null)
    		this.m_lvSongsAdapter.notifyDataSetChanged();
    	this.updatePlaylistBtn();
    	
    	if(m_mediaPlayer != null)
    		m_mediaPlayer.start();
    	
    }
    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(m_mediaPlayer != null)
    	{
    		//m_mediaPlayer.pause();
    	}
    }
    
    @Override
    protected void onDestroy()
    {
    	m_bRunning = false;
    	
    	super.onDestroy();
    	
    	StaticResources.m_mediaPlayer = m_mediaPlayer;
    	StaticResources.m_crosshairPos = m_crosshairPos;
    	StaticResources.m_playlistID = m_playlistID;
    	StaticResources.m_playlist = m_playlist;
    	StaticResources.m_selectedCellID = m_selectedCellID;
		StaticResources.m_selectedColor = m_selectedColor;
		StaticResources.m_isPlaying = m_isPlaying;
		

		if( m_getMedia != null )
			m_getMedia.cancel();
    }
    
    protected OnClickListener m_onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(v == m_ivBtnDone)
			{
				releaseResource();
				setResult(RESULT_OK);
		    	finish();
			}
			else if(v == m_ivBtnFilters)
			{
				m_ivFilterMessage.setVisibility(View.VISIBLE);
				
				m_ivBtnFilters.setSelected(false);
				Animation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
				fadeIn.setDuration(1000);

				Animation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
				fadeOut.setStartOffset(1000);
				fadeOut.setDuration(1000);

				AnimationSet animation = new AnimationSet(false); //change to false
				animation.addAnimation(fadeIn);
				animation.addAnimation(fadeOut);
				animation.setAnimationListener(new AnimationListener()
				{

					@Override
					public void onAnimationEnd(Animation animation) {
						m_ivFilterMessage.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
				m_ivFilterMessage.setAnimation(animation);
				
				/*if(m_ivBtnFilters.isSelected())
				{
					m_ivBtnFilters.setSelected(false);
				}
				else
					m_ivBtnFilters.setSelected(true);*/
				
			}
			else if(v == m_ivBtnPlaylist)
			{
				//launch playlist activity
				stopMedia();
				
				Intent intent = new Intent(MoodMap.this, PlaylistActivity.class);
				startActivity(intent);
			}
		}
    };
    
    
    protected void setItemStatus(final View view, final ItemStatus status)
    {
    	MoodMap.this.runOnUiThread(new Runnable()
		{
			public void run()
			{
		    	if(view != null)
		    	{
		    		View medialayout = view;
			        
			        TextView tvIndexLabel = (TextView)medialayout.findViewById(R.id.tvIndexLabel);
			        ProgressBar pbSongPB = (ProgressBar)medialayout.findViewById(R.id.pbSongProgressBar);
			        ImageView ivBtnStop = (ImageView)medialayout.findViewById(R.id.ivBtnStop);
			        
			        if(status == ItemStatus.NORMAL)
			        {
			        	tvIndexLabel.setVisibility(View.VISIBLE);
			        	pbSongPB.setVisibility(View.INVISIBLE);
			        	ivBtnStop.setVisibility(View.INVISIBLE);
			        }
			        else if(status == ItemStatus.PLAYING)
			        {
			        	tvIndexLabel.setVisibility(View.INVISIBLE);
			        	pbSongPB.setVisibility(View.INVISIBLE);
			        	ivBtnStop.setVisibility(View.VISIBLE);
			        }
			        else if(status == ItemStatus.DOWNLOADING)
			        {
			        	tvIndexLabel.setVisibility(View.INVISIBLE);
			        	pbSongPB.setVisibility(View.VISIBLE);
			        	ivBtnStop.setVisibility(View.INVISIBLE);
			        }
		    	}
			}
		});
    }
    
    AdapterView.OnItemClickListener m_onItemClickListener = new AdapterView.OnItemClickListener() {
	    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	    	
	        if(m_playlist != null && m_playlist.m_media != null && position < m_playlist.m_media.size())
	        {
	        	if(m_selectedCellID >= 0)
	        	{
	        		if(m_selectedCellID >= m_lvSongs.getFirstVisiblePosition() && 
	        			m_selectedCellID <= m_lvSongs.getLastVisiblePosition())
	        		{
	        			View view = m_lvSongs.getChildAt(m_selectedCellID - m_lvSongs.getFirstVisiblePosition());
	        			setItemStatus(view, ItemStatus.NORMAL);
	        		}
	        	}
	        	
	        	final Media media = m_playlist.m_media.get(position);
	        	stopMedia();
	        	
	        	setItemStatus(v, ItemStatus.DOWNLOADING);
	        	
	        	m_playingRow = position;
		        m_selectedCellID = position;
		        
		        try {
					m_mediaPlayer.startStreaming(media.m_previewURL.toString());
				} catch (IOException e) {
					MoodMap.this.runOnUiThread(new Runnable()
					{
						public void run()
						{
							Toast.makeText(MoodMap.this, R.string.toast_media_url_invalid, Toast.LENGTH_SHORT).show();
						}
					}
					);
					e.printStackTrace();
				}
		        
//		        Thread thread = new Thread( new Runnable() 
//		        {
//		        	public void run()
//		        	{
//				        try {
//							m_mediaPlayer.setDataSource(media.m_previewURL.toString());
//							m_mediaPlayer.prepare();
//						} catch (Exception e) {
//							
//							MoodMap.this.runOnUiThread(new Runnable()
//							{
//								public void run()
//								{
//									Toast.makeText(MoodMap.this, R.string.toast_media_url_invalid, Toast.LENGTH_SHORT).show();
//								}
//							}
//							);
//							e.printStackTrace();
//						}
//		        	}
//		        });
//		        thread.start();
		        
	        }
        }
    };
    
    private class MediaArrayAdapter extends ArrayAdapter<Media>
    {
    	Context context;
    	ArrayList<Media> m_media;
    	int m_resourceId ;
    	
	    public MediaArrayAdapter(Activity activity, int resourceId, ArrayList<Media> arraylist)
	    {
	        super(activity, resourceId, arraylist);
	        context = activity;
	        m_media = arraylist;
	        m_resourceId = resourceId;
	    }
	
	    public View getView(int i, View view, ViewGroup viewgroup)
	    {
	        View medialayout = view;
	        if(medialayout == null)
	        {
	        	medialayout = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(m_resourceId, null);
	        }
	        
	        final View finalMediaView = medialayout;
	        final Media media = (Media)m_media.get(i);
	        
	        final TextView tvIndexLabel = (TextView)medialayout.findViewById(R.id.tvIndexLabel);
	        final ProgressBar pbSongPB = (ProgressBar)medialayout.findViewById(R.id.pbSongProgressBar);
	        final ImageView ivBtnStop = (ImageView)medialayout.findViewById(R.id.ivBtnStop);
	        final TextView tvTitleLabel = (TextView)medialayout.findViewById(R.id.tvTitleLabel);
	        final TextView tvColorBar = (TextView)medialayout.findViewById(R.id.tvColorBar);
	        final ImageView ivBtnAdd = (ImageView)medialayout.findViewById(R.id.ivBtnAdd);
	        final ImageView ivBtnCheck = (ImageView)medialayout.findViewById(R.id.ivBtnCheck);
	        
	        tvIndexLabel.setText(String.valueOf(i + 1));
	        tvTitleLabel.setText(media.m_title);
	        tvColorBar.setBackgroundColor(m_selectedColor);
	        
	        tvIndexLabel.setVisibility(View.VISIBLE);
	        tvTitleLabel.setVisibility(View.VISIBLE);
	        tvColorBar.setVisibility(View.VISIBLE);
	        pbSongPB.setVisibility(View.INVISIBLE);
	        ivBtnStop.setVisibility(View.INVISIBLE);
	        
	        if(LocalPlaylist.sharedPlaylist().existsInPlaylist(media))
	        {
	        	ivBtnAdd.setVisibility(View.INVISIBLE);
	        	ivBtnCheck.setVisibility(View.VISIBLE);
	        }
	        else
	        {
	        	ivBtnAdd.setVisibility(View.VISIBLE);
	        	ivBtnCheck.setVisibility(View.INVISIBLE);
	        }
	        
	        if(m_selectedCellID == i)
	        {
	        	if(m_isPlaying)
	        	{
	        		//playing current item
	        		setItemStatus(finalMediaView, ItemStatus.PLAYING);
	        	}
	        	else
	        	{
	        		// downloading
	        		setItemStatus(finalMediaView, ItemStatus.DOWNLOADING);
	        	}
	        }
	        else
	        {
	        	setItemStatus(finalMediaView, ItemStatus.NORMAL);
	        }
	        
	        
	        View.OnClickListener clicklistener = new View.OnClickListener()
		    {
				@Override
				public void onClick(View v) {
					if(v == ivBtnAdd)
					{
						ivBtnCheck.setVisibility(View.VISIBLE);
						ivBtnAdd.setVisibility(View.INVISIBLE);
						LocalPlaylist.sharedPlaylist().addToPlaylist(media);
						updatePlaylistBtn();
					}
					else if(v == ivBtnCheck)
					{
						ivBtnCheck.setVisibility(View.INVISIBLE);
						ivBtnAdd.setVisibility(View.VISIBLE);
						LocalPlaylist.sharedPlaylist().removeFromPlaylist(media);
						updatePlaylistBtn();
					}
					else if(v == ivBtnStop)
					{
						stopMedia();
						setItemStatus(finalMediaView, ItemStatus.NORMAL);
					}
				}
		    };
		    
		    ivBtnAdd.setOnClickListener(clicklistener);
		    ivBtnCheck.setOnClickListener(clicklistener);
		    ivBtnStop.setOnClickListener(clicklistener);
		    
	        return medialayout;
	    }
	    
	    
    }
    
    protected void stopMedia()
    {
    	if(m_mediaPlayer != null)
    	{
    		m_mediaPlayer.reset();
    	}
    	m_isPlaying = false;
    	m_selectedCellID = -1;
    }
    
    protected OnCompletionListener m_mpCompletionListener = new OnCompletionListener()
    {
    	public void onCompletion(MediaPlayer mp) {
    		Log.v(LOGTAG, "Song Playing is completed");
    		if(m_selectedCellID >= m_lvSongs.getFirstVisiblePosition() && 
        		m_selectedCellID <= m_lvSongs.getLastVisiblePosition())
    		{
    			View view = m_lvSongs.getChildAt(m_selectedCellID - m_lvSongs.getFirstVisiblePosition());
    			setItemStatus(view, ItemStatus.NORMAL);
    		}
    		stopMedia();
    	}
    };
    
    protected OnBufferingUpdateListener m_mpBufferingUpdateListener = new OnBufferingUpdateListener()
    {
    	public void onBufferingUpdate(MediaPlayer mp, int percent) {
    		
    		View medialayout = null;
    		if(m_selectedCellID >= m_lvSongs.getFirstVisiblePosition() && 
        			m_selectedCellID <= m_lvSongs.getLastVisiblePosition())
    		{
				medialayout = m_lvSongs.getChildAt(m_selectedCellID - m_lvSongs.getFirstVisiblePosition());
    		}
    		
    		if(mp == null)
    		{
    			//now loading media
    	        setItemStatus(medialayout, ItemStatus.DOWNLOADING);
    	        return;
    		}
    		
    		int nowPercent = (int)(((float)mp.getCurrentPosition()/mp.getDuration())*100);
    		
    		Log.v(LOGTAG, "onBufferingUpdate buffer percent = " + percent + "  nowPercent = " + nowPercent);
    		
    		
	        
    		
    		if(percent < 100 && 
    				( (m_isPlaying == false  && nowPercent + 50 > percent) || 
    				  (m_isPlaying == true && nowPercent + 5 > percent))  )
    		{
    			//now loading media
    	        setItemStatus(medialayout, ItemStatus.DOWNLOADING);
    		}
    		else
    		{
    			m_isPlaying = true;
    			setItemStatus(medialayout, ItemStatus.PLAYING);
    	        mp.start();
    		}
    	}
    };
    
    protected OnErrorListener m_mpErrorListener = new OnErrorListener()
    {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.v(LOGTAG, "onError what = " + what + "  extra = " + extra);
			
			
			if(m_selectedCellID >= m_lvSongs.getFirstVisiblePosition() && 
	        		m_selectedCellID <= m_lvSongs.getLastVisiblePosition())
    		{
    			View view = m_lvSongs.getChildAt(m_selectedCellID - m_lvSongs.getFirstVisiblePosition());
    			setItemStatus(view, ItemStatus.NORMAL);
    		}
			
			stopMedia();
			
			return false;
		}
    	
    };
    
    
    private void colorOfPoint(float ptX, float ptY)
    {
    	int x = (int)(ptX/20.166f);
        int y = (int)(ptY/20.166f);
        
        while (colors[y][x] == 0) {     //going into valid area
            if (x >= 6) {
                x--;
            } else {
                x++;
            }
            if (y >= 6) {
                y--;
            } else {
                y++;
            }
        }

        m_selectedColor = colors[y][x];
        
        //setting adjacent colors
        ArrayList<Integer> array = new ArrayList<Integer>();
        x--;    //2
        y--;
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
            array.add(colors[y][x]);
        }
        x++;    //3
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
            array.add(colors[y][x]);
        }
        x++;    //4
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
            array.add(colors[y][x]);
        }
        y++;    //5
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
            array.add(colors[y][x]);
        }
        x-=2;    //6
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
        	array.add(colors[y][x]);
        }
        y++;    //7
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
        	array.add(colors[y][x]);
        }
        x++;    //8
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
        	array.add(colors[y][x]);
        }
        x++;    //9
        if (x >= 0 && x <= 11 && y >= 0 && y <= 11) {
        	array.add(colors[y][x]);
        }
        
        m_adjacentColors.clear();
        for(int i = 0; i < array.size(); i++)
        {
        	if(array.get(i).intValue() != 0)
        	{
        		m_adjacentColors.add(array.get(i));
        	}
        }
	}
    
    int[][] colors = 
	{
        {
			0, 
			0, 
			0,
			RFUtils.getColorFromFloatVal(0.2549f, 0.7647f, 0.2078f, 1.0f),   //1
			RFUtils.getColorFromFloatVal(0.4078f, 0.7804f, 0.1412f, 1.0f),   //2
			RFUtils.getColorFromFloatVal(0.5451f, 0.7922f, 0.0863f, 1.0f),   //3
			RFUtils.getColorFromFloatVal(0.698f, 0.8275f, 0.0157f, 1.0f),    //31
			RFUtils.getColorFromFloatVal(0.8353f, 0.8235f, 0.0039f, 1.0f),   //32
			RFUtils.getColorFromFloatVal(0.8784f, 0.7804f, 0.0039f, 1.0f),   //33
			0, 
			0, 
			0
        }, 
        {
			0, 
			0,
			RFUtils.getColorFromFloatVal(0.1725f, 0.7176f, 0.3176f, 1.0f),   //4
			RFUtils.getColorFromFloatVal(0.298f, 0.7098f, 0.2392f, 1.0f),    //5
			RFUtils.getColorFromFloatVal(0.4471f, 0.7176f, 0.1804f, 1.0f),   //6
			RFUtils.getColorFromFloatVal(0.5922f, 0.7373f, 0.1294f, 1.0f),   //7
			RFUtils.getColorFromFloatVal(0.7412f, 0.7647f, 0.0588f, 1.0f),   //34
			RFUtils.getColorFromFloatVal(0.8627f, 0.7686f, 0.0039f, 1.0f),   //35
			RFUtils.getColorFromFloatVal(0.902f, 0.7608f, 0.0039f, 1.0f),    //36
			RFUtils.getColorFromFloatVal(0.9255f, 0.7333f, 0.0039f, 1.0f),   //37
			0, 
			0
        }, 
        {
			0,
			RFUtils.getColorFromFloatVal(0.1098f, 0.6745f, 0.4471f, 1.0f),   //8
			RFUtils.getColorFromFloatVal(0.2157f, 0.6549f, 0.3529f, 1.0f),   //9
			RFUtils.getColorFromFloatVal(0.3373f, 0.6392f, 0.2706f, 1.0f),   //10
			RFUtils.getColorFromFloatVal(0.4824f, 0.6588f, 0.2157f, 1.0f),   //11
			RFUtils.getColorFromFloatVal(0.6275f, 0.6784f, 0.1569f, 1.0f),   //12
			RFUtils.getColorFromFloatVal(0.7569f, 0.6784f, 0.0941f, 1.0f),   //38
			RFUtils.getColorFromFloatVal(0.8784f, 0.702f, 0.0039f, 1.0f),    //39
			RFUtils.getColorFromFloatVal(0.9255f, 0.7176f, 0.0039f, 1.0f),   //40
			RFUtils.getColorFromFloatVal(0.9451f, 0.7137f, 0.0039f, 1.0f),   //41
			RFUtils.getColorFromFloatVal(0.9647f, 0.6902f, 0.0039f, 1.0f),   //42
			0
        },
        {
			RFUtils.getColorFromFloatVal(0.0627f, 0.6314f, 0.5608f, 1.0f),   //13
			RFUtils.getColorFromFloatVal(0.1569f, 0.6118f, 0.4745f, 1.0f),   //14
			RFUtils.getColorFromFloatVal(0.2706f, 0.5922f, 0.3804f, 1.0f),   //15
			RFUtils.getColorFromFloatVal(0.3922f, 0.5843f, 0.3059f, 1.0f),   //16
			RFUtils.getColorFromFloatVal(0.5255f, 0.5961f, 0.2588f, 1.0f),   //17
			RFUtils.getColorFromFloatVal(0.6549f, 0.6157f, 0.1882f, 1.0f),   //18
			RFUtils.getColorFromFloatVal(0.7608f, 0.6039f, 0.1137f, 1.0f),   //43
			RFUtils.getColorFromFloatVal(0.8706f, 0.6196f, 0.0431f, 1.0f),   //44
			RFUtils.getColorFromFloatVal(0.9333f, 0.6353f, 0.0039f, 1.0f),   //45
			RFUtils.getColorFromFloatVal(0.9647f, 0.6745f, 0.0039f, 1.0f),   //46
			RFUtils.getColorFromFloatVal(0.9765f, 0.6824f, 0.0039f, 1.0f),   //47
			RFUtils.getColorFromFloatVal(0.9804f, 0.6745f, 0.0078f, 1.0f),   //48
        },
        {
			RFUtils.getColorFromFloatVal(0.0941f, 0.5725f, 0.5961f, 1.0f),   //19
			RFUtils.getColorFromFloatVal(0.2157f, 0.549f, 0.498f, 1.0f),     //20
			RFUtils.getColorFromFloatVal(0.3176f, 0.5216f, 0.4157f, 1.0f),   //21
			RFUtils.getColorFromFloatVal(0.4275f, 0.5216f, 0.3412f, 1.0f),   //22
			RFUtils.getColorFromFloatVal(0.5608f, 0.5333f, 0.2863f, 1.0f),   //23
			RFUtils.getColorFromFloatVal(0.6627f, 0.5294f, 0.2078f, 1.0f),   //24
			RFUtils.getColorFromFloatVal(0.7647f, 0.5333f, 0.1412f, 1.0f),   //49
			RFUtils.getColorFromFloatVal(0.8588f, 0.5451f, 0.0706f, 1.0f),   //50
			RFUtils.getColorFromFloatVal(0.9529f, 0.5686f, 0.0039f, 1.0f),   //51
			RFUtils.getColorFromFloatVal(0.9725f, 0.5961f, 0.0039f, 1.0f),   //52
			RFUtils.getColorFromFloatVal(0.9804f, 0.6314f, 0.0039f, 1.0f),   //53
			RFUtils.getColorFromFloatVal(0.9804f, 0.651f, 0.0196f, 1.0f),    //54
        },
        {
			RFUtils.getColorFromFloatVal(0.1333f, 0.4941f, 0.6078f, 1.0f),   //25
			RFUtils.getColorFromFloatVal(0.251f, 0.4745f, 0.5294f, 1.0f),    //26
			RFUtils.getColorFromFloatVal(0.3569f, 0.4667f, 0.4431f, 1.0f),   //27
			RFUtils.getColorFromFloatVal(0.4706f, 0.4667f, 0.3725f, 1.0f),   //28
			RFUtils.getColorFromFloatVal(0.5765f, 0.4667f, 0.302f, 1.0f),    //29
			RFUtils.getColorFromFloatVal(0.6627f, 0.4667f, 0.2392f, 1.0f),   //30
			RFUtils.getColorFromFloatVal(0.7608f, 0.4706f, 0.1647f, 1.0f),   //55
			RFUtils.getColorFromFloatVal(0.8549f, 0.4824f, 0.098f, 1.0f),    //56
			RFUtils.getColorFromFloatVal(0.9373f, 0.4941f, 0.0353f, 1.0f),   //57
			RFUtils.getColorFromFloatVal(0.9804f, 0.5176f, 0.0039f, 1.0f),   //58
			RFUtils.getColorFromFloatVal(0.9804f, 0.549f, 0.0196f, 1.0f),    //59
			RFUtils.getColorFromFloatVal(0.9804f, 0.5765f, 0.0275f, 1.0f),   //60
        },
		{
			RFUtils.getColorFromFloatVal(0.1373f, 0.3961f, 0.6471f, 1.0f),   //91
			RFUtils.getColorFromFloatVal(0.2706f, 0.3961f, 0.5451f, 1.0f),   //92
			RFUtils.getColorFromFloatVal(0.3725f, 0.4f, 0.4627f, 1.0f),      //93
			RFUtils.getColorFromFloatVal(0.4784f, 0.4f, 0.3961f, 1.0f),      //94
			RFUtils.getColorFromFloatVal(0.5765f, 0.3961f, 0.3255f, 1.0f),   //95
			RFUtils.getColorFromFloatVal(0.6667f, 0.4039f, 0.2627f, 1.0f),   //96
			RFUtils.getColorFromFloatVal(0.7569f, 0.4118f, 0.2f, 1.0f),      //61
			RFUtils.getColorFromFloatVal(0.8392f, 0.4157f, 0.1333f, 1.0f),   //62
			RFUtils.getColorFromFloatVal(0.9255f, 0.4431f, 0.0667f, 1.0f),   //63
			RFUtils.getColorFromFloatVal(0.9804f, 0.4627f, 0.0196f, 1.0f),   //64
			RFUtils.getColorFromFloatVal(0.9804f, 0.4863f, 0.0314f, 1.0f),   //65
			RFUtils.getColorFromFloatVal(0.9804f, 0.4745f, 0.0275f, 1.0f),   //66
        },
        {
			RFUtils.getColorFromFloatVal(0.1294f, 0.298f, 0.7137f, 1.0f),    //97
			RFUtils.getColorFromFloatVal(0.251f, 0.3137f, 0.6118f, 1.0f),    //98
			RFUtils.getColorFromFloatVal(0.3882f, 0.3294f, 0.5098f, 1.0f),   //99
			RFUtils.getColorFromFloatVal(0.5059f, 0.3412f, 0.4431f, 1.0f),   //100
			RFUtils.getColorFromFloatVal(0.5843f, 0.3373f, 0.349f, 1.0f),    //101
			RFUtils.getColorFromFloatVal(0.6667f, 0.3373f, 0.2863f, 1.0f),   //102
			RFUtils.getColorFromFloatVal(0.749f, 0.3529f, 0.2235f, 1.0f),    //67
			RFUtils.getColorFromFloatVal(0.8235f, 0.3686f, 0.1608f, 1.0f),   //68
			RFUtils.getColorFromFloatVal(0.8941f, 0.3843f, 0.1059f, 1.0f),   //69
			RFUtils.getColorFromFloatVal(0.9529f, 0.4039f, 0.0549f, 1.0f),   //70
			RFUtils.getColorFromFloatVal(0.9804f, 0.3922f, 0.0275f, 1.0f),   //71
			RFUtils.getColorFromFloatVal(0.9804f, 0.3098f, 0.0275f, 1.0f),   //72
        },
        {
			RFUtils.getColorFromFloatVal(0.1529f, 0.2471f, 0.7333f, 1.0f),   //103
			RFUtils.getColorFromFloatVal(0.2275f, 0.2314f, 0.6784f, 1.0f),   //104
			RFUtils.getColorFromFloatVal(0.3647f, 0.2667f, 0.5882f, 1.0f),   //105
			RFUtils.getColorFromFloatVal(0.5137f, 0.298f, 0.498f, 1.0f),     //106
			RFUtils.getColorFromFloatVal(0.5961f, 0.2941f, 0.4039f, 1.0f),   //107
			RFUtils.getColorFromFloatVal(0.6588f, 0.2902f, 0.3176f, 1.0f),   //108
			RFUtils.getColorFromFloatVal(0.7373f, 0.298f, 0.2549f, 1.0f),    //73
			RFUtils.getColorFromFloatVal(0.8039f, 0.3137f, 0.2f, 1.0f),      //74
			RFUtils.getColorFromFloatVal(0.8627f, 0.3373f, 0.1412f, 1.0f),   //75
			RFUtils.getColorFromFloatVal(0.9294f, 0.3451f, 0.0824f, 1.0f),   //76
			RFUtils.getColorFromFloatVal(0.9804f, 0.298f, 0.0196f, 1.0f),    //77
			RFUtils.getColorFromFloatVal(0.9804f, 0.2627f, 0.0235f, 1.0f),   //78
        },
        {
			0,
			RFUtils.getColorFromFloatVal(0.2392f, 0.2f, 0.7098f, 1.0f),      //109
			RFUtils.getColorFromFloatVal(0.3137f, 0.2039f, 0.6784f, 1.0f),   //110
			RFUtils.getColorFromFloatVal(0.4627f, 0.2431f, 0.5804f, 1.0f),   //111
			RFUtils.getColorFromFloatVal(0.5922f, 0.2471f, 0.4627f, 1.0f),   //112
			RFUtils.getColorFromFloatVal(0.6667f, 0.2549f, 0.3765f, 1.0f),   //113
			RFUtils.getColorFromFloatVal(0.7137f, 0.2588f, 0.298f, 1.0f),    //79
			RFUtils.getColorFromFloatVal(0.7725f, 0.2667f, 0.2353f, 1.0f),   //80
			RFUtils.getColorFromFloatVal(0.8392f, 0.2784f, 0.1725f, 1.0f),   //81
			RFUtils.getColorFromFloatVal(0.9176f, 0.2549f, 0.0863f, 1.0f),   //82
			RFUtils.getColorFromFloatVal(0.9804f, 0.2118f, 0.0235f, 1.0f),   //83
			0
        },
        { 
			0, 
			0,
			RFUtils.getColorFromFloatVal(0.3255f, 0.1843f, 0.698f, 1.0f),    //114
			RFUtils.getColorFromFloatVal(0.4f, 0.1882f, 0.6627f, 1.0f),      //115
			RFUtils.getColorFromFloatVal(0.5569f, 0.2196f, 0.5569f, 1.0f),   //116
			RFUtils.getColorFromFloatVal(0.6471f, 0.2196f, 0.4314f, 1.0f),   //117
			RFUtils.getColorFromFloatVal(0.7176f, 0.2196f, 0.3373f, 1.0f),   //84
			RFUtils.getColorFromFloatVal(0.7843f, 0.2039f, 0.2353f, 1.0f),   //85
			RFUtils.getColorFromFloatVal(0.8667f, 0.1725f, 0.1412f, 1.0f),   //86
			RFUtils.getColorFromFloatVal(0.9412f, 0.1412f, 0.0667f, 1.0f),   //87
			0, 
			0
        },
        { 
			0, 
			0, 
			0,
			RFUtils.getColorFromFloatVal(0.3922f, 0.1647f, 0.6902f, 1.0f),   //118
			RFUtils.getColorFromFloatVal(0.4784f, 0.1725f, 0.6314f, 1.0f),   //119
			RFUtils.getColorFromFloatVal(0.6353f, 0.1765f, 0.4824f, 1.0f),   //120
			RFUtils.getColorFromFloatVal(0.7608f, 0.1451f, 0.3059f, 1.0f),   //88
			RFUtils.getColorFromFloatVal(0.8863f, 0.0824f, 0.149f, 1.0f),    //89
			RFUtils.getColorFromFloatVal(0.9216f, 0.0745f, 0.0863f, 1.0f),   //90
			0, 
			0, 
			0
        }
    };

}