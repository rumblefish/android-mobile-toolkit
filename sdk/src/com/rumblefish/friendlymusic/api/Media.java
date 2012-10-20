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