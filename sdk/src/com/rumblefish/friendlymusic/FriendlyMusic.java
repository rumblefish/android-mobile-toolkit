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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rumblefish.friendlymusic.api.LocalPlaylist;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.api.RFAPI.RFAPIEnv;

public class FriendlyMusic extends Activity {

	public final static String OPTIONS = "options";
	
	public final static int FMMOODMAP = 1;
	public final static int FMOCCASION = 2;
	public final static int FMEDITORSPICKS = 4;
	
	
	RelativeLayout m_rlMoodMap;
	RelativeLayout m_rlOccasion;
	RelativeLayout m_rlEditorsPick;
	
	public int m_options;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.friendlymusic);
        
        //configures rumble environment
        RFAPI.rumbleWithEnvironment(RFAPIEnv.RFAPIEnvProduction, "PUBLIC_KEY", "PASSWORD");
        
        LocalPlaylist.initPlaylist(this);
        LocalPlaylist.sharedPlaylist().readPlaylist();
        
        initView();
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void setOptions(int options)
    {
    	
    }
    
    private void initView()
    {
    	m_rlMoodMap 	= (RelativeLayout)findViewById(R.id.rlMoodMap);
    	m_rlOccasion 	= (RelativeLayout)findViewById(R.id.rlOccasion);
    	m_rlEditorsPick	= (RelativeLayout)findViewById(R.id.rlEditorsPick);
    	
    	m_rlMoodMap.setOnClickListener(m_onClickListener);
    	m_rlOccasion.setOnClickListener(m_onClickListener);
    	m_rlEditorsPick.setOnClickListener(m_onClickListener);
    }

    
    protected OnClickListener m_onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(RFAPI.getSingleTone().isInitialized() == false)
			{
				Toast.makeText(FriendlyMusic.this, R.string.toast_notloaded, Toast.LENGTH_LONG).show();
				return;
			}
			
			if(v == m_rlMoodMap)
			{
				Intent intent = new Intent(FriendlyMusic.this, MoodMap.class);
				startActivity(intent);
			}
			else if(v == m_rlOccasion)
			{
				Intent intent = new Intent(FriendlyMusic.this, OccasionActivity.class);
				startActivity(intent);
			}
			else if(v == m_rlEditorsPick)
			{
				Intent intent = new Intent(FriendlyMusic.this, CoverFlowActivity.class);
				startActivity(intent);
			}
		}
    };
    
}
