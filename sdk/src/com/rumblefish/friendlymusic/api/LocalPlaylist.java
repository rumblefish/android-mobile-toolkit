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
package com.rumblefish.friendlymusic.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalPlaylist {
	
	public Context m_context;
	public ArrayList<Media> m_playlist;
	public int m_count; 
	
	public static String PREFERENCE = "playlist";
	public static String KEY = "playlist";
	
	public static LocalPlaylist m_instance;
	public static void initPlaylist(Context ctx)
	{
		m_instance = new LocalPlaylist();
		m_instance.m_context = ctx;
	}
	public static LocalPlaylist sharedPlaylist()
	{
		if(m_instance == null)
		{
			m_instance = new LocalPlaylist();
		}
		return m_instance;
	}
	
	public LocalPlaylist()
	{
		m_playlist = new ArrayList<Media>();
		m_count = 0;
	}
	
	public void readPlaylist()
	{
		SharedPreferences playlistPreference = m_context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		String playlistContent = playlistPreference.getString(KEY, "");
		if(playlistContent.length() == 0)
		{
			m_playlist.clear();
		}
		else
		{
			try
			{
				JSONArray array = new JSONArray(playlistContent);
				m_playlist = Media.getMediaList(array);
			}
			catch(Exception e)
			{
				m_playlist.clear();
			}
		}
		
		
	}
	
	public void flushPlaylist()
	{
		try
		{
			JSONArray array = new JSONArray();
			for(int i = 0; i < m_playlist.size(); i++)
			{
				JSONObject obj = m_playlist.get(i).dictionaryRepresentation();
				array.put(obj);
			}
			SharedPreferences playlistPreference = m_context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
			Editor editor = playlistPreference.edit();
			editor.putString(KEY, array.toString());
			editor.commit();
		}
		catch(Exception e)
		{
			m_playlist.clear();
		}
	}
	
	public void addToPlaylist(Media media)
	{
		m_playlist.add(media);
		flushPlaylist();
	}
	
	public void removeAtIndex(int index)
	{
		m_playlist.remove(index);
		flushPlaylist();
	}
	
	public void removeFromPlaylist(Media media)
	{
		if(!m_playlist.remove(media))
		{
			for(int i = 0; i < m_playlist.size(); i++)
			{
				Media pM = m_playlist.get(i);
				if(pM.m_id == media.m_id)
				{
					m_playlist.remove(i);
					return;
				}
			}
		}
		flushPlaylist();
	}
	
	public boolean existsInPlaylist(Media media)
	{
		if(m_playlist.contains(media))
			return true;
		
		for(int i = 0; i < m_playlist.size(); i++)
		{
			Media pM = m_playlist.get(i);
			if(pM.m_id == media.m_id)
			{
				return true;
			}
		}
		return false;
	}
	
	public Media mediaAtIndex(int index)
	{
		return m_playlist.get(index);
	}
	
	public void clear()
	{
		m_playlist.clear();
		flushPlaylist();
	}
}
