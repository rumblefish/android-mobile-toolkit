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
package com.rumblefish.rfsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.rumblefish.friendlymusic.FriendlyMusic;
import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.api.RFAPI.RFAPIEnv;

public class FriendlyMusicDemoActivity extends Activity {
	
	Button m_btnGetStarted;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    setContentView(R.layout.friendlymusicdemo);
	    
	    //configures rumble environment
        RFAPI.rumbleWithEnvironment(RFAPIEnv.RFAPIEnvProduction, "PUBLIC_KEY", "PASSWORD");
        
        
        m_btnGetStarted = (Button)this.findViewById(R.id.btnGetStarted);
        m_btnGetStarted.setEnabled(false);
        
		m_bRunning = true;
        startCountDownThread();
	}
	
	
	public void onBtnClick(View view)
	{
		if(view == m_btnGetStarted)
		{
			Intent intent = new Intent(this, FriendlyMusic.class);
			startActivity(intent);
		}
		else
		{
			Intent send = new Intent(Intent.ACTION_SENDTO);
			String uriText;

			uriText = "mailto:info@friendlymusic.com" + 
			          "?subject=Suggestion" + 
			          "&body=I have a suggestion";
			uriText = uriText.replace(" ", "%20");
			Uri uri = Uri.parse(uriText);

			send.setData(uri);
			startActivity(Intent.createChooser(send, "Send mail..."));
		}
	}
    
	
	boolean m_bRunning = false;
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		m_bRunning = false;
	}
	
	public void startCountDownThread() {
		
		Runnable r = (new Runnable() {
			@Override
			public void run() {
				while(m_bRunning)
				{
					if(RFAPI.getSingleTone().isInitialized())
					{
						FriendlyMusicDemoActivity.this.runOnUiThread(
							new Runnable()
							{
								public void run()
								{
									m_btnGetStarted.setEnabled(true);
								}
							});
						return;
					}
				}
			}
		});
		
		new Thread(r).start();
	}
	
	
}
