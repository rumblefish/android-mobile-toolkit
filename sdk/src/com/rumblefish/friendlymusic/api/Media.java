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
package com.rumblefish.friendlymusic.api;

import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Media
{
	public String m_title;
	public String m_albumTitle;
	public String m_genre;
	public boolean m_isExplicit;
	public int		m_id;
	public URL	m_previewURL;
	
	public static String getStringFromObject(JSONObject obj, String key)
	{
		try
		{
			return obj.getString(key);
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	public static Media initWithDictionary(JSONObject dict)
	{
		
		Media media = new Media();
		
		media.m_title = getStringFromObject(dict, "title");
		
		try
		{
			media.m_albumTitle = ((JSONObject)dict.getJSONObject("album")).getString("title");
		}
		catch(Exception e)
		{
			media.m_albumTitle = "";
		}
		
		media.m_genre = getStringFromObject(dict, "genre");
		
		try
		{
			media.m_isExplicit = ((Boolean)dict.get("explicit")).booleanValue();
		}
		catch(Exception e)
		{
			media.m_isExplicit = false;
		}
		
		try
		{
			media.m_id = ((Integer)dict.get("id")).intValue();
		}
		catch(Exception e)
		{
			media.m_id = 0;
		}
		
		
		try {
			media.m_previewURL = new URL(dict.get("preview_url").toString());
		} catch (Exception e) {
			media.m_previewURL = null;
		}
		
		return media;
		
	}
	
	public JSONObject dictionaryRepresentation()
	{
		try
		{
			JSONObject dict = new JSONObject();
			dict.put("id", m_id);
			dict.put("title", m_title);
			dict.put("preview_url", m_previewURL.toString());
			return dict;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static ArrayList<Media> getMediaList(JSONArray array)
	{
		ArrayList<Media> result = new ArrayList<Media>();
		for(int i = 0; i < array.length(); i++)
		{
			try
			{
				JSONObject obj = (JSONObject) array.get(i);
				Media newMedia = Media.initWithDictionary(obj);
				if(newMedia != null)
				{
					result.add(newMedia);
				}
			}
			catch(Exception e)
			{
				continue;
			}
		}
		return result;
	}
	
	public int hash()
	{
		return m_id;
	}
	
	public boolean isEqual(Object obj)
	{
		if(obj.getClass() == Media.class)
		{
			if(((Media)obj).m_id == this.m_id)
			{
				return true;
			}
		}
		return false;
	}
}
