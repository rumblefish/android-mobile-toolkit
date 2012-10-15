package com.rumblefish.friendlymusic;

import android.app.Activity;
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
        setContentView(R.layout.activity_main);
        
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
    }

    
    protected OnClickListener m_onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) {
			if(v == m_rlMoodMap)
			{
				
			}
			else if(v == m_rlOccasion)
			{
				
			}
			else if(v == m_rlEditorsPick)
			{
				
			}
		}
    };
    
}