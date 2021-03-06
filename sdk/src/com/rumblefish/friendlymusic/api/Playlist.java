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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Playlist {
	public int m_id;
	public String m_title;
	public String m_editorial;
	public String m_strippedEditorial;
	public URL m_imageURL;
	public ArrayList<Media> m_media;
	
	public static Playlist initWithDictionary(JSONObject dict)
	{
		Playlist playlist = null;
		
		playlist = new Playlist();
		try
		{
			playlist.m_title = dict.getString("title").toString();
		}
		catch(Exception e)
		{
			playlist.m_title = "";
		}
		
		try
		{
			playlist.m_id = dict.getInt("id");
		}
		catch(Exception e)
		{
			playlist.m_id = 0;
		}
		
		try
		{
			String imageURLString = dict.getString("image_url");
			if(imageURLString!=null)
			{
				imageURLString = imageURLString.trim();
				if(imageURLString.length() > 0)
				{
					try {
						playlist.m_imageURL = new URL(imageURLString);
					} catch (MalformedURLException e) {
						playlist.m_imageURL = null;
						e.printStackTrace();
					}
				}
			}
		}
		catch(Exception e)
		{
			playlist.m_imageURL = null;
		}
		
		try
		{
			playlist.m_editorial = dict.getString("editorial");
		}
		catch(Exception e)
		{
			playlist.m_editorial = "";
		}
		
		try
		{
			JSONArray mediaList = dict.getJSONArray("media");
			playlist.m_media = Media.getMediaList(mediaList);
		}
		catch(Exception e)
		{
			playlist.m_media = null;
		}
		
		return playlist;
	}
	
	public static ArrayList<Playlist> getPlaylistList(JSONArray array)
	{
		ArrayList<Playlist> result = new ArrayList<Playlist>();
		for(int i = 0; i < array.length(); i++)
		{
			try
			{
				JSONObject obj = (JSONObject) array.get(i);
				Playlist newPlaylist = Playlist.initWithDictionary(obj);
				if(newPlaylist != null)
				{
					result.add(newPlaylist);
				}
			}
			catch(Exception e)
			{
				continue;
			}
		}
		return result;
	}
	
	
	public String strippedEditorial()
	{
		if(m_editorial == null || m_editorial.length() == 0)
		{
			return "";
		}
		
		int h2Range = m_editorial.indexOf("<h2>");
		int h3Range = m_editorial.indexOf("<h3>");
		
		if(h2Range == -1 || h3Range == -1)
		{
			return "";
		}
		
		String h2 = m_editorial.substring(h2Range + 4, m_editorial.indexOf("</h2"));
		String h3 = m_editorial.substring(h3Range + 4, m_editorial.indexOf("</h3"));
		
		return h2 + ". " + h3;
	}
	
}
