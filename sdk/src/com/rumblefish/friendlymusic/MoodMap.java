package com.rumblefish.friendlymusic;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MoodMap extends Activity implements OnTouchListener{

	public static final String LOGTAG = "MoodMap";
	
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
	
	ListView		m_lvSongs;
	ProgressBar		m_pbActivityIndicator;
	
	int m_rlMoodMapSize;
	int m_screenOrientation;
	
	ArrayList<Integer> m_adjacentColors;
	int	m_playingRow;
	Integer m_selectedColor;
	boolean m_isPlaying;
	boolean m_playlistIsLoading;
	int	m_playlistID;
	
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
        m_ivSelector.setVisibility(View.INVISIBLE);
        
        m_playingRow = -1;
        
        //copying playlist.plist to document folder
        //
        //
        
        m_selectedColor = 0;
        if(SettingsUtils.getBoolForKey(this, "fmisused", false) == true)
        {
        	m_ivMessage.setVisibility(View.INVISIBLE);
        }
        else
        {
        	m_ivMessage.setVisibility(View.VISIBLE);
        	SettingsUtils.setBoolForKey(this, "fmisused", true);
        }
        
        //audio session
        //
        
        
        m_pbActivityIndicator.setVisibility(View.INVISIBLE);
        
        //animations
        m_animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        m_animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        
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
    	
    	
    	m_lvSongs 	= (ListView)findViewById(R.id.lvSongs);
    	m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
    	
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
            	
            	setMoodMapElemSize(m_ivLogo,  		(int)(155 * ratioTo480),  (int)(45 * ratioTo480));
            	setMoodMapElemSize(m_ivMoodMap,  		(int)(363 * ratioTo480),  (int)(363 * ratioTo480));
            	setMoodMapElemSize(m_ivIcons,  		(int)(453 * ratioTo480),  (int)(453 * ratioTo480));
            	setMoodMapElemSize(m_ivRing,  		(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivGlow,  		(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivCrosshairs,  	(int)(407 * ratioTo480),  (int)(407 * ratioTo480));
            	setMoodMapElemSize(m_ivSelector,  	(int)(68 * ratioTo480),  (int)(68 * ratioTo480));
            	setMoodMapElemSize(m_ivMessage,  		(int)(292 * ratioTo480),  (int)(84 * ratioTo480));
            }
        });
        
        //event handler
        m_ivMoodMap.setOnTouchListener(this);

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
    	m_ringBitmap = Bitmap.createBitmap(imgView.getMeasuredWidth(), imgView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
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
    
    @Override
	public boolean onTouch(View view, MotionEvent event) {

    	if(view == m_ivMoodMap)
    	{
    		float ratioTo320 = (float)m_rlMoodMapSize / 320;
    		float curX = event.getX();
    		float ratX = curX / ratioTo320;
    		float curY = event.getY();
    		float ratY = curY / ratioTo320;
    		
    		Log.i(LOGTAG, " curX = " + curX + " curY = " + curY);
    		
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
			    	
			    	//m_ivGlow.setVisibility(View.VISIBLE);
			    	//m_ivGlow.startAnimation(m_animFadeIn);
			    	
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
				m_ivRing.setVisibility(View.INVISIBLE);
		    	m_ivRing.startAnimation(m_animFadeOut);
		    	//m_ivGlow.startAnimation(m_animFadeIn);
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
    
    private void getPlaylistFromServer()
    {
    	
    }
    
    protected OnClickListener m_onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(v == m_rlMoodMap)
			{
				
			}
		}
    };
    
    public static int getColorFromFloatVal(float red, float green, float blue, float alpha)
    {
    	return Color.argb((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255));
    }
    
    int[][] colors = 
	{
        {
			0, 
			0, 
			0,
			getColorFromFloatVal(0.2549f, 0.7647f, 0.2078f, 1.0f),   //1
			getColorFromFloatVal(0.4078f, 0.7804f, 0.1412f, 1.0f),   //2
			getColorFromFloatVal(0.5451f, 0.7922f, 0.0863f, 1.0f),   //3
			getColorFromFloatVal(0.698f, 0.8275f, 0.0157f, 1.0f),    //31
			getColorFromFloatVal(0.8353f, 0.8235f, 0.0039f, 1.0f),   //32
			getColorFromFloatVal(0.8784f, 0.7804f, 0.0039f, 1.0f),   //33
			0, 
			0, 
			0
        }, 
        {
			0, 
			0,
			getColorFromFloatVal(0.1725f, 0.7176f, 0.3176f, 1.0f),   //4
			getColorFromFloatVal(0.298f, 0.7098f, 0.2392f, 1.0f),    //5
			getColorFromFloatVal(0.4471f, 0.7176f, 0.1804f, 1.0f),   //6
			getColorFromFloatVal(0.5922f, 0.7373f, 0.1294f, 1.0f),   //7
			getColorFromFloatVal(0.7412f, 0.7647f, 0.0588f, 1.0f),   //34
			getColorFromFloatVal(0.8627f, 0.7686f, 0.0039f, 1.0f),   //35
			getColorFromFloatVal(0.902f, 0.7608f, 0.0039f, 1.0f),    //36
			getColorFromFloatVal(0.9255f, 0.7333f, 0.0039f, 1.0f),   //37
			0, 
			0
        }, 
        {
			0,
			getColorFromFloatVal(0.1098f, 0.6745f, 0.4471f, 1.0f),   //8
			getColorFromFloatVal(0.2157f, 0.6549f, 0.3529f, 1.0f),   //9
			getColorFromFloatVal(0.3373f, 0.6392f, 0.2706f, 1.0f),   //10
			getColorFromFloatVal(0.4824f, 0.6588f, 0.2157f, 1.0f),   //11
			getColorFromFloatVal(0.6275f, 0.6784f, 0.1569f, 1.0f),   //12
			getColorFromFloatVal(0.7569f, 0.6784f, 0.0941f, 1.0f),   //38
			getColorFromFloatVal(0.8784f, 0.702f, 0.0039f, 1.0f),    //39
			getColorFromFloatVal(0.9255f, 0.7176f, 0.0039f, 1.0f),   //40
			getColorFromFloatVal(0.9451f, 0.7137f, 0.0039f, 1.0f),   //41
			getColorFromFloatVal(0.9647f, 0.6902f, 0.0039f, 1.0f),   //42
			0
        },
        {
			getColorFromFloatVal(0.0627f, 0.6314f, 0.5608f, 1.0f),   //13
			getColorFromFloatVal(0.1569f, 0.6118f, 0.4745f, 1.0f),   //14
			getColorFromFloatVal(0.2706f, 0.5922f, 0.3804f, 1.0f),   //15
			getColorFromFloatVal(0.3922f, 0.5843f, 0.3059f, 1.0f),   //16
			getColorFromFloatVal(0.5255f, 0.5961f, 0.2588f, 1.0f),   //17
			getColorFromFloatVal(0.6549f, 0.6157f, 0.1882f, 1.0f),   //18
			getColorFromFloatVal(0.7608f, 0.6039f, 0.1137f, 1.0f),   //43
			getColorFromFloatVal(0.8706f, 0.6196f, 0.0431f, 1.0f),   //44
			getColorFromFloatVal(0.9333f, 0.6353f, 0.0039f, 1.0f),   //45
			getColorFromFloatVal(0.9647f, 0.6745f, 0.0039f, 1.0f),   //46
			getColorFromFloatVal(0.9765f, 0.6824f, 0.0039f, 1.0f),   //47
			getColorFromFloatVal(0.9804f, 0.6745f, 0.0078f, 1.0f),   //48
        },
        {
			getColorFromFloatVal(0.0941f, 0.5725f, 0.5961f, 1.0f),   //19
			getColorFromFloatVal(0.2157f, 0.549f, 0.498f, 1.0f),     //20
			getColorFromFloatVal(0.3176f, 0.5216f, 0.4157f, 1.0f),   //21
			getColorFromFloatVal(0.4275f, 0.5216f, 0.3412f, 1.0f),   //22
			getColorFromFloatVal(0.5608f, 0.5333f, 0.2863f, 1.0f),   //23
			getColorFromFloatVal(0.6627f, 0.5294f, 0.2078f, 1.0f),   //24
			getColorFromFloatVal(0.7647f, 0.5333f, 0.1412f, 1.0f),   //49
			getColorFromFloatVal(0.8588f, 0.5451f, 0.0706f, 1.0f),   //50
			getColorFromFloatVal(0.9529f, 0.5686f, 0.0039f, 1.0f),   //51
			getColorFromFloatVal(0.9725f, 0.5961f, 0.0039f, 1.0f),   //52
			getColorFromFloatVal(0.9804f, 0.6314f, 0.0039f, 1.0f),   //53
			getColorFromFloatVal(0.9804f, 0.651f, 0.0196f, 1.0f),    //54
        },
        {
			getColorFromFloatVal(0.1333f, 0.4941f, 0.6078f, 1.0f),   //25
			getColorFromFloatVal(0.251f, 0.4745f, 0.5294f, 1.0f),    //26
			getColorFromFloatVal(0.3569f, 0.4667f, 0.4431f, 1.0f),   //27
			getColorFromFloatVal(0.4706f, 0.4667f, 0.3725f, 1.0f),   //28
			getColorFromFloatVal(0.5765f, 0.4667f, 0.302f, 1.0f),    //29
			getColorFromFloatVal(0.6627f, 0.4667f, 0.2392f, 1.0f),   //30
			getColorFromFloatVal(0.7608f, 0.4706f, 0.1647f, 1.0f),   //55
			getColorFromFloatVal(0.8549f, 0.4824f, 0.098f, 1.0f),    //56
			getColorFromFloatVal(0.9373f, 0.4941f, 0.0353f, 1.0f),   //57
			getColorFromFloatVal(0.9804f, 0.5176f, 0.0039f, 1.0f),   //58
			getColorFromFloatVal(0.9804f, 0.549f, 0.0196f, 1.0f),    //59
			getColorFromFloatVal(0.9804f, 0.5765f, 0.0275f, 1.0f),   //60
        },
		{
			getColorFromFloatVal(0.1373f, 0.3961f, 0.6471f, 1.0f),   //91
			getColorFromFloatVal(0.2706f, 0.3961f, 0.5451f, 1.0f),   //92
			getColorFromFloatVal(0.3725f, 0.4f, 0.4627f, 1.0f),      //93
			getColorFromFloatVal(0.4784f, 0.4f, 0.3961f, 1.0f),      //94
			getColorFromFloatVal(0.5765f, 0.3961f, 0.3255f, 1.0f),   //95
			getColorFromFloatVal(0.6667f, 0.4039f, 0.2627f, 1.0f),   //96
			getColorFromFloatVal(0.7569f, 0.4118f, 0.2f, 1.0f),      //61
			getColorFromFloatVal(0.8392f, 0.4157f, 0.1333f, 1.0f),   //62
			getColorFromFloatVal(0.9255f, 0.4431f, 0.0667f, 1.0f),   //63
			getColorFromFloatVal(0.9804f, 0.4627f, 0.0196f, 1.0f),   //64
			getColorFromFloatVal(0.9804f, 0.4863f, 0.0314f, 1.0f),   //65
			getColorFromFloatVal(0.9804f, 0.4745f, 0.0275f, 1.0f),   //66
        },
        {
			getColorFromFloatVal(0.1294f, 0.298f, 0.7137f, 1.0f),    //97
			getColorFromFloatVal(0.251f, 0.3137f, 0.6118f, 1.0f),    //98
			getColorFromFloatVal(0.3882f, 0.3294f, 0.5098f, 1.0f),   //99
			getColorFromFloatVal(0.5059f, 0.3412f, 0.4431f, 1.0f),   //100
			getColorFromFloatVal(0.5843f, 0.3373f, 0.349f, 1.0f),    //101
			getColorFromFloatVal(0.6667f, 0.3373f, 0.2863f, 1.0f),   //102
			getColorFromFloatVal(0.749f, 0.3529f, 0.2235f, 1.0f),    //67
			getColorFromFloatVal(0.8235f, 0.3686f, 0.1608f, 1.0f),   //68
			getColorFromFloatVal(0.8941f, 0.3843f, 0.1059f, 1.0f),   //69
			getColorFromFloatVal(0.9529f, 0.4039f, 0.0549f, 1.0f),   //70
			getColorFromFloatVal(0.9804f, 0.3922f, 0.0275f, 1.0f),   //71
			getColorFromFloatVal(0.9804f, 0.3098f, 0.0275f, 1.0f),   //72
        },
        {
			getColorFromFloatVal(0.1529f, 0.2471f, 0.7333f, 1.0f),   //103
			getColorFromFloatVal(0.2275f, 0.2314f, 0.6784f, 1.0f),   //104
			getColorFromFloatVal(0.3647f, 0.2667f, 0.5882f, 1.0f),   //105
			getColorFromFloatVal(0.5137f, 0.298f, 0.498f, 1.0f),     //106
			getColorFromFloatVal(0.5961f, 0.2941f, 0.4039f, 1.0f),   //107
			getColorFromFloatVal(0.6588f, 0.2902f, 0.3176f, 1.0f),   //108
			getColorFromFloatVal(0.7373f, 0.298f, 0.2549f, 1.0f),    //73
			getColorFromFloatVal(0.8039f, 0.3137f, 0.2f, 1.0f),      //74
			getColorFromFloatVal(0.8627f, 0.3373f, 0.1412f, 1.0f),   //75
			getColorFromFloatVal(0.9294f, 0.3451f, 0.0824f, 1.0f),   //76
			getColorFromFloatVal(0.9804f, 0.298f, 0.0196f, 1.0f),    //77
			getColorFromFloatVal(0.9804f, 0.2627f, 0.0235f, 1.0f),   //78
        },
        {
			0,
			getColorFromFloatVal(0.2392f, 0.2f, 0.7098f, 1.0f),      //109
			getColorFromFloatVal(0.3137f, 0.2039f, 0.6784f, 1.0f),   //110
			getColorFromFloatVal(0.4627f, 0.2431f, 0.5804f, 1.0f),   //111
			getColorFromFloatVal(0.5922f, 0.2471f, 0.4627f, 1.0f),   //112
			getColorFromFloatVal(0.6667f, 0.2549f, 0.3765f, 1.0f),   //113
			getColorFromFloatVal(0.7137f, 0.2588f, 0.298f, 1.0f),    //79
			getColorFromFloatVal(0.7725f, 0.2667f, 0.2353f, 1.0f),   //80
			getColorFromFloatVal(0.8392f, 0.2784f, 0.1725f, 1.0f),   //81
			getColorFromFloatVal(0.9176f, 0.2549f, 0.0863f, 1.0f),   //82
			getColorFromFloatVal(0.9804f, 0.2118f, 0.0235f, 1.0f),   //83
			0
        },
        { 
			0, 
			0,
			getColorFromFloatVal(0.3255f, 0.1843f, 0.698f, 1.0f),    //114
			getColorFromFloatVal(0.4f, 0.1882f, 0.6627f, 1.0f),      //115
			getColorFromFloatVal(0.5569f, 0.2196f, 0.5569f, 1.0f),   //116
			getColorFromFloatVal(0.6471f, 0.2196f, 0.4314f, 1.0f),   //117
			getColorFromFloatVal(0.7176f, 0.2196f, 0.3373f, 1.0f),   //84
			getColorFromFloatVal(0.7843f, 0.2039f, 0.2353f, 1.0f),   //85
			getColorFromFloatVal(0.8667f, 0.1725f, 0.1412f, 1.0f),   //86
			getColorFromFloatVal(0.9412f, 0.1412f, 0.0667f, 1.0f),   //87
			0, 
			0
        },
        { 
			0, 
			0, 
			0,
			getColorFromFloatVal(0.3922f, 0.1647f, 0.6902f, 1.0f),   //118
			getColorFromFloatVal(0.4784f, 0.1725f, 0.6314f, 1.0f),   //119
			getColorFromFloatVal(0.6353f, 0.1765f, 0.4824f, 1.0f),   //120
			getColorFromFloatVal(0.7608f, 0.1451f, 0.3059f, 1.0f),   //88
			getColorFromFloatVal(0.8863f, 0.0824f, 0.149f, 1.0f),    //89
			getColorFromFloatVal(0.9216f, 0.0745f, 0.0863f, 1.0f),   //90
			0, 
			0, 
			0
        }
    };

}