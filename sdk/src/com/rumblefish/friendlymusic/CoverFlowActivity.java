package com.rumblefish.friendlymusic;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.api.Producer;
import com.rumblefish.friendlymusic.api.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.view.ImageLoader;
import com.rumblefish.friendlymusic.view.coverflow.BitmapAdapter;
import com.rumblefish.friendlymusic.view.coverflow.CoverFlow;
import com.rumblefish.friendlymusic.view.coverflow.ReflectingImageAdapter;

public class CoverFlowActivity  extends Activity {
	
	public static final String 	LOGTAG = "CoverFlowActivity";

	// member variables
	RelativeLayout	m_rlContent;
	RelativeLayout	m_rlNavBar;
	
	// text views
	TextView	m_tvTitle;
	TextView	m_tvSubTitle;
	
	// navigation buttons
	ImageView	m_ivBtnNavDone;
	ImageView	m_ivBtnNavPlaylist;
	ImageView	m_ivBtnNavRemove;
	
	CoverFlow	m_cfCoverflow;
	
	//progress bar
	ProgressBar m_pbActivityIndicator;
	
	//Arraylist
	ArrayList<Playlist>	m_playlists;
	ArrayList<Bitmap> m_bitmaps;
	
	//screen size
	int m_contentWidth;
	int m_contentHeight;
	float m_ratioTo320X;
	float m_ratioTo480Y;
	float m_minRatio;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.coverflow);
        
        initView();
	}
	
	
	private void initView()
    {
		
		m_rlContent = (RelativeLayout)findViewById(R.id.rlCoverflowContent);
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
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		
		// text views
		m_tvTitle =  (TextView)findViewById(R.id.tvTitle);
		m_tvTitle.setTextColor(Color.WHITE);
		m_tvSubTitle =  (TextView)findViewById(R.id.tvSubTitle);
		m_tvSubTitle.setVisibility(View.INVISIBLE);
		
		m_cfCoverflow = (CoverFlow)findViewById(R.id.cfCoverflow);
		
		final ViewTreeObserver vto = m_rlContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	m_contentWidth = m_rlContent.getWidth();
            	m_contentHeight = m_rlContent.getHeight();
            	m_ratioTo320X = (float)m_contentWidth / 320.0f;
            	m_ratioTo480Y = (float)m_contentHeight / 416.0f;
            	
            	m_minRatio = Math.min(m_ratioTo320X, m_ratioTo480Y);
            	
            	int display_mode = getResources().getConfiguration().orientation;

            	if (display_mode == 1) {
            	    
            	} else {
            	    //setContentView(R.layout.main_land);
            		m_ratioTo480Y  = (float)m_contentWidth / 416.0f * 0.8f ;
            		m_minRatio = Math.min((float) m_contentHeight / 320.0f, (float)m_contentWidth / 416.0f);
            	}
            	
            	m_cfCoverflow.setImageWidth(m_minRatio * 150);
        		m_cfCoverflow.setImageHeight(m_minRatio * 150);
        		m_cfCoverflow.setReflectionGap(2);
        		m_cfCoverflow.setImageReflectionRatio(0.3f);
        		m_cfCoverflow.setWithReflection(true);
            	m_rlContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	
            	if(StaticResources.m_cfBitmaps != null)
            	{
            		m_bitmaps = StaticResources.m_cfBitmaps;
            		
            		if(m_bitmaps.size() > 0)
            		{
            			//cover flow update
            			setupCoverFlow(m_bitmaps);
            			//text update
            			m_cfCoverflow.setSelection(m_playlists.size() / 2 , true);
            		}
            	}
            }
        });
		
		if(StaticResources.m_cfPlaylists != null)
		{
			this.m_playlists = StaticResources.m_cfPlaylists;
			if(StaticResources.m_cfBitmaps == null)
			{
				downloadPlaylistImages();
			}
		}
		else
		{
			StaticResources.m_cfBitmaps = null;
			getPlaylistFromServer();
		}
    }
	
	//cover flow
    private void setupCoverFlow(ArrayList<Bitmap> bitmaps) {
        BaseAdapter coverImageAdapter;
        coverImageAdapter = new ReflectingImageAdapter(new BitmapAdapter(this, bitmaps));
        m_cfCoverflow.setAdapter(coverImageAdapter);
        setupListeners(m_cfCoverflow);
    }

    /**
     * Sets the up listeners.
     * 
     * @param mCoverFlow
     *            the new up listeners
     */
    private void setupListeners(final CoverFlow mCoverFlow) {
        mCoverFlow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
                Intent intent = new Intent(CoverFlowActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.PLAYLIST_ID_TAG, m_playlists.get(position).m_id);
                startActivity(intent);
            }

        });
        mCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView< ? > parent, final View view, final int position, final long id) {
            	m_tvTitle.setText(m_playlists.get(position).m_title);
            	//m_tvSubTitle.setText(m_playlists.get(position).m_strippedEditorial);
            }

            @Override
            public void onNothingSelected(final AdapterView< ? > parent) {
                
            }
        });
    }
	
	private void downloadPlaylistImages()
	{
		if(m_playlists.size() == 0)
		{
			finishedDownloading();
		}
		else
		{
			//if(m_downloadWatcher.isAlive())
			m_bRunning = true;
			m_downloadWatcher.start();
			
			for(int i = 0 ; i < m_playlists.size(); i++)
			{
				Playlist pl = m_playlists.get(i);
				//Log.v(LOGTAG, m_playlists.get(i).m_title + " => " + pl.m_imageURL.toString());
				ImageLoader.getInstance().load(null, pl.m_imageURL.toString(), true);
			}
			
			
		}
	}
	
	private void finishedDownloading()
	{
		m_pbActivityIndicator.setVisibility(View.INVISIBLE);
		
		m_bitmaps = new ArrayList<Bitmap>();
		for(int i = m_playlists.size() -1 ; i >= 0; i--)
		{
			Playlist pl = m_playlists.get(i);
			Bitmap bm = ImageLoader.getInstance().get(pl.m_imageURL.toString());
			if(bm != null)
			{
				m_bitmaps.add(0, bm);
			}
			else
			{
				m_playlists.remove(i);
			}
		}
		
		if(m_bitmaps.size() > 0)
		{
			//cover flow update
			setupCoverFlow(m_bitmaps);
			//text update
			m_cfCoverflow.setSelection(m_playlists.size() / 2 , true);
		}
	}
	
	private void getPlaylistFromServer()
	{
		Producer getPlaylist = RFAPI.getSingleTone().getPlaylistsWithOffset(0);
		if(getPlaylist == null)
    		return;
		
		getPlaylist.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				
				
				Log.v(LOGTAG, "getPlaylist result");
				
				@SuppressWarnings("unchecked")
				ArrayList<Playlist> arrayPlaylist = (ArrayList<Playlist>) obj;
				for(int i = arrayPlaylist.size() - 1; i >= 0 ; i--)
				{
					Playlist playlist = arrayPlaylist.get(i);
					
					if(playlist.m_imageURL == null)
					{
						arrayPlaylist.remove(i);
					}
				}
				
				m_playlists = arrayPlaylist;
				
				downloadPlaylistImages();
			}

			@Override
			public void onError() {
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
				Toast.makeText(CoverFlowActivity.this, R.string.toast_connection_fail, Toast.LENGTH_LONG).show();
				getPlaylistFromServer();
			}
    	};
    	getPlaylist.run();
	}
	
	protected OnClickListener m_OnNavButtonClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(v == m_ivBtnNavDone)
			{
				releaseResource();
				finish();
			}
			else if (v == m_ivBtnNavPlaylist)
			{
				Intent intent = new Intent(CoverFlowActivity.this, PlaylistActivity.class);
				startActivity(intent);
			}
			else if (v == m_ivBtnNavRemove)
			{
				
			}
		}
    };
    
    private void recycleCoverflowImages()
    {
    	if(m_bitmaps != null)
    	{
	    	for(int i = 0; i < m_bitmaps.size(); i++)
	    	{
	    		Bitmap bm = m_bitmaps.get(i);
	    		if(bm != null && bm.isMutable())
	    			bm.recycle();
	    	}
	    	m_bitmaps.clear();
    	}
    	
    	ImageLoader loader = ImageLoader.getInstance();
    	loader.clearCache();
    }

    boolean m_bRunning = false; 
	Thread m_downloadWatcher = new Thread(new Runnable()
	{
		public void run()
		{
			ImageLoader loader = ImageLoader.getInstance();
			while(m_bRunning == true)
			{
				try {
					Thread.sleep((long) (200));
					Log.v(LOGTAG, "Sleeped 200ms waiting another");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				
				if(loader.isBusy() == false)
				{
					break;
				}
			}
			
			CoverFlowActivity.this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					finishedDownloading();
				}
			});
		}
	});
	
    @Override
    protected void onDestroy()
    {
    	m_bRunning = false;
    	super.onDestroy();	
    	
    	StaticResources.m_cfPlaylists = this.m_playlists;
    	StaticResources.m_cfBitmaps = this.m_bitmaps;
    }
    
    private void releaseResource()
    {
    	recycleCoverflowImages();
    	this.m_bitmaps = null;
    	this.m_playlists = null;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	releaseResource();
	    	finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
    
}
