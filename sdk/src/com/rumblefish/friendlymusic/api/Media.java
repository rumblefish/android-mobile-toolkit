package com.rumblefish.friendlymusic.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Media
{
	public String m_title;
	public String m_albumTitle;
	public String m_genre;
	public boolean m_isExplicit;
	public int		m_id;
	public URL	m_previewURL;
	
	public static Media initWithDictionary(HashMap<String, Object > dict)
	{
		Media media = new Media();
		media.m_title = dict.get("title").toString();
		media.m_albumTitle = ((HashMap<String, Object>)dict.get("album")).get("title").toString();
		media.m_genre = dict.get("genre").toString();
		media.m_isExplicit = ((Boolean)dict.get("explicit")).booleanValue();
		media.m_id = ((Integer)dict.get("id")).intValue();
		
		try {
			media.m_previewURL = new URL(dict.get("preview_url").toString());
		} catch (MalformedURLException e) {
			media.m_previewURL = null;
			e.printStackTrace();
		}
		
		return media;
	}
	
	public HashMap<String, Object> dictionaryRepresentation()
	{
		HashMap<String, Object> dict = new HashMap<String, Object>();
		dict.put("id", m_id);
		dict.put("title", m_title);
		dict.put("preview_url", m_previewURL.toString());
		return dict;
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