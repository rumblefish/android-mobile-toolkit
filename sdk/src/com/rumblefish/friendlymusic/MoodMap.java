package com.rumblefish.friendlymusic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MoodMap extends Activity {

	RelativeLayout 	m_rlMoodMap;
	ListView		m_lvSongs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void initView()
    {
    	m_rlMoodMap = (RelativeLayout)findViewById(R.id.rlMoodMap);
    	m_lvSongs 	= (RelativeLayout)findViewById(R.id.lvSongs);
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
    
}