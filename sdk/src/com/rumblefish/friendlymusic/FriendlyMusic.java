package com.rumblefish.friendlymusic;

import com.rumblefish.friendlymusic.api.RFAPI;
import com.rumblefish.friendlymusic.api.RFAPI.RFAPIEnv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

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
        setContentView(R.layout.friendlymusic);
        
        initView();
        
        RFAPI.rumbleWithEnvironment(RFAPIEnv.RFAPIEnvProduction, "PUBLIC_KEY", "PASSWORD");
        
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
				Intent intent = new Intent(FriendlyMusic.this, MoodMap.class);
				startActivity(intent);
			}
		}
    };
    
}