/*******************************************************************************
 * Rumblefish Mobile Toolkit for iOS
 * 
 * Copyright 2013 Rumblefish, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Use of the Rumblefish Sandbox in connection with this file is governed by
 * the Sandbox Terms of Use found at https://sandbox.rumblefish.com/agreement
 *  
 * Use of the Rumblefish API for any commercial purpose in connection with
 * this file requires a written agreement with Rumblefish, Inc.
 ******************************************************************************/
package com.rumblefish.friendlymusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.api.Producer;
import com.rumblefish.friendlymusic.api.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.view.SongListView;
import com.rumblefish.friendlymusic.view.SongListView.ButtonStyle;

public class AlbumActivity extends Activity{

	public static final String PLAYLIST_ID_TAG	 = "playlistid";
	
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
	
	// playlist id
	int	m_playlistid;
	Playlist m_playlist;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //use same layout as playlist but with differnt styles.
        setContentView(R.layout.playlist);
        initView();
        
        //get parameters from calling activity
        Intent startingIntent = getIntent();
        m_playlistid = startingIntent.getIntExtra(PLAYLIST_ID_TAG, -1);
        if(m_playlistid == -1)
        {
        	finish();
        }
        
        if(StaticResources.m_albumPlaylist == null)
		{
        	loadSongList();
		}
		else
		{
			this.m_playlist = StaticResources.m_albumPlaylist;
			
			m_pbActivityIndicator.setVisibility(View.INVISIBLE);
			m_lvSongList.setVisibility(View.VISIBLE);
		}
        
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
		
		// indicator
		m_pbActivityIndicator = (ProgressBar)findViewById(R.id.pbActivityIndicator);
		m_pbActivityIndicator.setVisibility(View.VISIBLE);
		

		if(StaticResources.m_albumSongListView == null)
		{
			m_lvSongList = new SongListView(this);
			m_lvSongList.setButtonStyle(ButtonStyle.BUTTON_REMOVE);
			m_rlContent.addView(m_lvSongList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			m_lvSongList.setVisibility(View.INVISIBLE);
			
		}
		else
		{
			m_lvSongList = StaticResources.m_albumSongListView;
			m_rlContent.addView(m_lvSongList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			
			m_pbActivityIndicator.setVisibility(View.INVISIBLE);
			m_lvSongList.setVisibility(View.VISIBLE);
		}
		
		
    }
	
	
	private void loadSongList()
	{
		
		Producer getPlaylist = RFAPI.getSingleTone().getPlaylist(this.m_playlistid);
		
		if(getPlaylist == null)
    		return;
		
		getPlaylist.m_delegate = new ProducerDelegate()
    	{
			@Override
			public void onResult(Object obj) {
				
				if(m_bRunning == false || m_lvSongList == null)
					return;
				
				Playlist retPl = (Playlist)obj;
				m_playlist = retPl;
				m_lvSongList.setVisibility(View.VISIBLE);
				m_lvSongList.showMedias(retPl.m_media, false);
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onError() {

				if(m_bRunning == false)
					return;
				
				m_pbActivityIndicator.setVisibility(View.INVISIBLE);
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
				m_lvSongList.stopMedia();
				
				Intent intent = new Intent(AlbumActivity.this, PlaylistActivity.class);
				startActivity(intent);
			}
		}
    };
    
    @Override
    protected void onDestroy()
    {
    	if(m_lvSongList != null)
    	{
    		m_rlContent.removeView(m_lvSongList);
    	}
    	
    	super.onDestroy();
    	StaticResources.m_albumSongListView = m_lvSongList;
    	StaticResources.m_albumPlaylist = m_playlist;
    	
    	m_bRunning = false;
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(this.m_lvSongList != null)
    	{
    		//this.m_lvSongList.pauseMedia();
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
    
    private void releaseResource()
    {
    	m_bRunning = false;
    	
    	if(m_lvSongList != null)
    	{
    		m_lvSongList.release();
    		m_lvSongList = null;
    	}
    	
    	m_playlist = null;
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	m_bRunning = true;
    }
	
    
    public boolean m_bRunning = false;
}
