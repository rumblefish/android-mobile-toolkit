/*******************************************************************************
 * Rumblefish Mobile Toolkit for Android
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setContentView(R.layout.playlist);
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
		
		
		if(StaticResources.m_plSongListView == null)
		{
			m_lvSongList = new SongListView(this);
			m_lvSongList.setButtonStyle(ButtonStyle.BUTTON_REMOVE);
			m_rlContent.addView(m_lvSongList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			m_lvSongList.setVisibility(View.INVISIBLE);
			
			loadSongList();
		}
		else
		{
			m_lvSongList = StaticResources.m_plSongListView;
			m_rlContent.addView(m_lvSongList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			
			m_pbActivityIndicator.setVisibility(View.INVISIBLE);
			m_lvSongList.setVisibility(View.VISIBLE);
		}
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
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	releaseResource();
	    	finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
    
    @Override
    protected void onPause()
    {
    	super.onPause();
//    	if(this.m_lvSongList != null)
//    	{
//    		this.m_lvSongList.pauseMedia();
//    	}
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
    	if(m_lvSongList != null)
    	{
    		m_rlContent.removeView(m_lvSongList);
    	}
    	
    	super.onDestroy();
    	StaticResources.m_plSongListView = m_lvSongList;
    }
    
    private void releaseResource()
    {
    	if(m_lvSongList != null)
    	{
    		m_lvSongList.release();
    		m_lvSongList = null;
    	}
    }
	
    
    public boolean m_bRunning = false;
}
