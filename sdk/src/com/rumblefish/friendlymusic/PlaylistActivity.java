package com.rumblefish.friendlymusic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.rumblefish.friendlymusic.api.LocalPlaylist;
import com.rumblefish.friendlymusic.view.SongListView;
import com.rumblefish.friendlymusic.view.SongListView.ButtonStyle;

public class PlaylistActivity extends Activity{


	// member variables
	RelativeLayout	m_rlContent;
	RelativeLayout	m_rlNavBar;
	
	// navigation buttons
	ImageView	m_ivBtnNavDone;
	ImageView	m_ivBtnNavPlaylist;
	ImageView	m_ivBtnNavRemove;

	// progress bar
	ProgressBar m_pbActivityIndicator;
	
	// song list view
	SongListView m_lvSongList;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.playlist);
        
        initView();
	}
	
	private void initView()
    {
		
		m_rlContent = (RelativeLayout)findViewById(R.id.rlPlaylistContent);
		m_rlNavBar = (RelativeLayout)findViewById(R.id.rlNavBar);
		
		// Navigation Bar
		m_ivBtnNavDone = (ImageView)findViewById(R.id.ivNavBtnDone);
		m_ivBtnNavPlaylist = (ImageView)findViewById(R.id.ivNavBtnPlaylist);
		m_ivBtnNavRemove = (ImageView)findViewById(R.id.ivNavBtnRemoveAll);
		
		m_ivBtnNavDone.setOnClickListener(m_OnNavButtonClickListener);
		m_ivBtnNavPlaylist.setOnClickListener(m_OnNavButtonClickListener);
		m_ivBtnNavRemove.setOnClickListener(m_OnNavButtonClickListener);
		
		m_ivBtnNavPlaylist.setVisibility(View.INVISIBLE);
		m_ivBtnNavRemove.setVisibility(View.VISIBLE);
		
		// indicator
		m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		
		if(m_lvSongList == null)
		{
			m_lvSongList = new SongListView(this);
			m_lvSongList.setButtonStyle(ButtonStyle.BUTTON_REMOVE);
			m_rlContent.addView(m_lvSongList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			m_lvSongList.setVisibility(View.INVISIBLE);
		}
		
		loadSongList();
		
    }
	
	private void loadSongList()
	{
		m_pbActivityIndicator.setVisibility(View.INVISIBLE);
		LocalPlaylist playlist = LocalPlaylist.sharedPlaylist();
		m_lvSongList.setVisibility(View.VISIBLE);
		m_lvSongList.showMedias(playlist.m_playlist, false);
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
				
			}
			else if (v == m_ivBtnNavRemove)
			{
				LocalPlaylist playlist = LocalPlaylist.sharedPlaylist();
				playlist.clear();
				loadSongList();
			}
		}
    };
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(this.m_lvSongList != null)
    	{
    		this.m_lvSongList.pauseMedia();
    	}
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(this.m_lvSongList != null)
    	{
    		this.m_lvSongList.resumeMedia();
    		this.m_lvSongList.updateContent();
    	}
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	releaseResource();
    }
    
    private void releaseResource()
    {
    	if(m_lvSongList != null)
    	{
    		m_lvSongList.release();
    		m_lvSongList = null;
    	}
    }
	
}
