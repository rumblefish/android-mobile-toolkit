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
package com.rumblefish.friendlymusic.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rumblefish.friendlymusic.R;
import com.rumblefish.friendlymusic.api.Media;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.mediaplayer.StreamingMediaPlayer;
import com.rumblefish.friendlymusic.view.SongListViewAdapter.ItemButtonTypes;
import com.rumblefish.friendlymusic.view.SongListViewAdapter.ItemStatus;
import com.rumblefish.friendlymusic.view.SongListViewAdapter.SongListViewAdapterController;

public class SongListView extends ListView{

	
	public static final String LOGTAG = "SongListView";
	
	//contents
	ArrayList<Playlist> 			m_playlists = null;
	ArrayList<SongListViewItem>		m_itemlist = null;
	boolean 						m_bShowInSections = true;
	
	//media player related functions
	StreamingMediaPlayer 			m_mediaPlayer = null;
	
	
	//utils variables
	int 		m_selectedCellID;
	boolean 	m_isPlaying;
	
	// button style
	ButtonStyle	m_buttonStyle = ButtonStyle.BUTTON_CHECK;
	
	// adapter
	SongListViewAdapter m_adapter = null;
	
	//context
	Context m_context;
	
	
	// constructors
	public SongListView(Context context) {
        super(context);
        m_context = context;
        commonInit();
    }

    public SongListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        m_context = context;
        commonInit();
    }

    public SongListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
        commonInit();
    }
    
    public void setButtonStyle(ButtonStyle style)
    {
    	m_buttonStyle = style;
    }
    

    public void commonInit()
    {
    	m_playlists = null;
    	m_itemlist = null;
    	m_bShowInSections = true;
    	
    	if(m_mediaPlayer == null)
    	{
    		m_mediaPlayer = new StreamingMediaPlayer(
    				m_context,
    				m_mpCompletionListener, 
    				m_mpBufferingUpdateListener, 
    				m_mpErrorListener);
    	}
    	
    	
    	m_adapter = null;
    }
    
    
    public void updateContent()
    {
    	ImageLoader loader = ImageLoader.getInstance();
    	loader.cancel();
    	loader.clearCache();
    	loader.clearQueue();
    	
    	if(m_adapter != null)
    	{
    		m_adapter.notifyDataSetChanged();
    	}
    }
    
    public void removeAll()
    {
		stopMedia();
		if(m_adapter != null)
		{
			m_itemlist.clear();
			m_adapter.notifyDataSetChanged();
		}
		m_adapter = null;
		this.setAdapter(null);
    }
    
    //show playlists and medias in the list view
    public void showPlaylists(ArrayList<Playlist> array)
    {
    	showPlaylists(array, true);
    }
    public void showPlaylists(ArrayList<Playlist> array, boolean bShowInSections)
    {
    	m_playlists = array;
    	m_bShowInSections = bShowInSections;
    	
    	if(m_adapter != null)
    	{
    		stopMedia();
    		m_adapter.clear();
    		m_adapter = null;
    	}
    	
    	m_playlists = array;
    	
    	// prepares list of items
    	m_itemlist = new ArrayList<SongListViewItem>();
    	for(int i = 0; i < m_playlists.size(); i++)
    	{
    		Playlist playlist = m_playlists.get(i);
    		if(bShowInSections == true)
    		{
    			SongListViewItem item = new SongListViewItem();
    			item.m_bIsHeader = true;
    			item.data = playlist;
    			m_itemlist.add(item);
    		}
    		for(int j = 0; j < playlist.m_media.size(); j++)
    		{
    			SongListViewItem item = new SongListViewItem();
    			item.m_bIsHeader = false;
    			item.data = playlist.m_media.get(j);
    			m_itemlist.add(item);
    		}
    	}
    	
    	//
    	if(m_itemlist != null )
    	{
    		if(m_itemlist.size() == 0)
    		{
    			stopMedia();
    			m_adapter = null;
    			this.setAdapter(null);
    		}
    		else
    		{
	    		m_adapter = new SongListViewAdapter(m_context, R.layout.play_list_item,  m_itemlist, m_controller, 0, m_buttonStyle);
	    		m_selectedCellID = -1;
		        this.setAdapter(m_adapter);
		        this.setOnItemClickListener(m_onItemClickListener);
    		}
    	}
    }
    
    public void showMedias(ArrayList<Media> array)
    {
    	showMedias(array, false);
    	
    }
    
    public void showMedias(ArrayList<Media> array, boolean bShowInSections)
    {
    	Playlist playlist = new Playlist();
    	playlist.m_media = array;
    	m_bShowInSections = bShowInSections;
    	
    	ArrayList<Playlist> playlists = new ArrayList<Playlist>();
    	playlists.add(playlist);
    	showPlaylists(playlists, m_bShowInSections);
    }
    
    public void stopMedia()
    {
    	if(m_mediaPlayer != null)
    	{
    		m_mediaPlayer.reset();
    	}
    	m_isPlaying = false;
    	m_selectedCellID = -1;
    }
    
    public void pauseMedia()
    {
    	if(m_mediaPlayer != null)
    	{
    		m_mediaPlayer.pause();
    	}
    }
    
    public void resumeMedia()
    {
    	if(m_mediaPlayer != null)
    	{
    		m_mediaPlayer.start();
    	}
    }
    
    protected OnCompletionListener m_mpCompletionListener = new OnCompletionListener()
    {
    	public void onCompletion(MediaPlayer mp) {
    		Log.v(LOGTAG, "Song Playing is completed");
    		if(m_selectedCellID >= getFirstVisiblePosition() && 
        		m_selectedCellID <= getLastVisiblePosition())
    		{
    			final View view = getChildAt(m_selectedCellID - getFirstVisiblePosition());
    			((Activity)m_context).runOnUiThread(new Runnable()
        		{
        			public void run()
        			{
        				m_adapter.setItemStatus(view, ItemStatus.NORMAL);
        			}
        		});
    		}
    		stopMedia();
    	}
    };
    
    protected OnBufferingUpdateListener m_mpBufferingUpdateListener = new OnBufferingUpdateListener()
    {
    	public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
    		
    		if(m_adapter == null)
    			return;
    		
    		View medialayout = null;
    		if(m_selectedCellID >= getFirstVisiblePosition() && 
        			m_selectedCellID <= getLastVisiblePosition())
    		{
				medialayout = getChildAt(m_selectedCellID - getFirstVisiblePosition());
    		}
    		
    		final View finalmedialayout = medialayout;
    		
    		((Activity)m_context).runOnUiThread(new Runnable()
    		{
    			public void run()
    			{
		    		if(mp == null)
		    		{
		    			m_adapter.setItemStatus(finalmedialayout, ItemStatus.DOWNLOADING);
		    			return;
		    		}
		    		
		    		int nowPercent = (int)(((float)mp.getCurrentPosition()/mp.getDuration())*100);
		    		
		    		Log.v(LOGTAG, "onBufferingUpdate buffer percent = " + percent + "  nowPercent = " + nowPercent);
		    	
		    		if(percent < 100 && 
		    				( (m_isPlaying == false  && nowPercent + 50 > percent) || 
		    				  (m_isPlaying == true && nowPercent + 5 > percent))  )
		    		{
		    			//now loading media
		    			m_adapter.setItemStatus(finalmedialayout, ItemStatus.DOWNLOADING);
		    		}
		    		else
		    		{
		    			m_isPlaying = true;
		    			m_adapter.setItemStatus(finalmedialayout, ItemStatus.PLAYING);
		    	        mp.start();
		    		}
    			}
    		});
    		
    		
    	}
    };
    
    protected OnErrorListener m_mpErrorListener = new OnErrorListener()
    {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.v(LOGTAG, "onError what = " + what + "  extra = " + extra);
			
			
			if(m_selectedCellID >= getFirstVisiblePosition() && 
	        		m_selectedCellID <= getLastVisiblePosition())
    		{
    			final View view = getChildAt(m_selectedCellID - getFirstVisiblePosition());
    			((Activity)m_context).runOnUiThread(new Runnable()
        		{
        			public void run()
        			{
        				m_adapter.setItemStatus(view, ItemStatus.NORMAL);
        			}
        		});
    		}
			
			stopMedia();
			
			return false;
		}
    };
    
 // adapter controller
 	SongListViewAdapterController m_controller = new SongListViewAdapterController()
 	{

		@Override
		public int getSelectedItemID() {
			return m_selectedCellID;
		}

		@Override
		public SongListViewAdapter.ItemStatus getCurrentItemStatus() {
			
			if(m_isPlaying)
			{
				return SongListViewAdapter.ItemStatus.PLAYING;
			}
			else
				return SongListViewAdapter.ItemStatus.DOWNLOADING;
		}

		@Override
		public void itemClicked(ItemButtonTypes buttonType, int position) {
			if(buttonType == ItemButtonTypes.ADD)
			{
			
			}
			else if(buttonType == ItemButtonTypes.CHECK)
			{
				
			}
			else if(buttonType == ItemButtonTypes.STOP)
			{
				stopMedia();
			}
			else if(buttonType == ItemButtonTypes.REMOVE)
			{
				if(m_selectedCellID == position)
				{
					stopMedia();
				}
				m_itemlist.remove(position);
				m_adapter.notifyDataSetChanged();
			}
		}
 	};
 	
 	
 	AdapterView.OnItemClickListener m_onItemClickListener = new AdapterView.OnItemClickListener() {
	    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	    	
	        if(m_itemlist != null && position < m_itemlist.size())
	        {
	        	
	        	if(m_itemlist.get(position).m_bIsHeader)
	        		return;
	        	
	        	if(m_selectedCellID >= 0)
	        	{
	        		if(m_selectedCellID >= getFirstVisiblePosition() && 
	        			m_selectedCellID <= getLastVisiblePosition())
	        		{
	        			View view = getChildAt(m_selectedCellID - getFirstVisiblePosition());
	        			m_adapter.setItemStatus(view, ItemStatus.NORMAL);
	        		}
	        	}
	        	
	        	final Media media = (Media)(m_itemlist.get(position).data);
	        	stopMedia();
	        	
	        	m_adapter.setItemStatus(v, ItemStatus.DOWNLOADING);
	        	
		        m_selectedCellID = position;
		        
		        try
		        {
		        	m_mediaPlayer.startStreaming(media.m_previewURL.toString());
		        }
		        catch(Exception e)
		        {
		        	if(m_context instanceof Activity)
					{
						((Activity)m_context).runOnUiThread(new Runnable()
						{
							public void run()
							{
								Toast.makeText(m_context, R.string.toast_media_url_invalid, Toast.LENGTH_SHORT).show();
							}
						}
						);
					}
					e.printStackTrace();
		        }
//		        Thread thread = new Thread( new Runnable() 
//		        {
//		        	public void run()
//		        	{
//				        try {
//							//m_mediaPlayer.setDataSource(media.m_previewURL.toString());
//							//m_mediaPlayer.prepare();
//						} catch (Exception e) {
//							if(m_context instanceof Activity)
//							{
//								((Activity)m_context).runOnUiThread(new Runnable()
//								{
//									public void run()
//									{
//										Toast.makeText(m_context, R.string.toast_media_url_invalid, Toast.LENGTH_SHORT).show();
//									}
//								}
//								);
//							}
//							e.printStackTrace();
//						}
//		        	}
//		        });
//		        thread.start();
	        }
        }
    };
    
    public void release()
    {
    	
    	ImageLoader.getInstance().clearCache();
    	
    	stopMedia();
    	
		m_mediaPlayer.reset();
		m_mediaPlayer.release();
		m_mediaPlayer = null;
		m_adapter = null;
		m_playlists = null;
		m_itemlist = null;
		m_selectedCellID = -1;
		m_isPlaying = false;
    }
    
    public enum ButtonStyle
    {
    	BUTTON_CHECK,
    	BUTTON_REMOVE
    }
    
    
}
