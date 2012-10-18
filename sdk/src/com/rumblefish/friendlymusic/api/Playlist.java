package com.rumblefish.friendlymusic.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Playlist {
	public int m_id;
	public String m_title;
	public String m_editorial;
	public String m_strippedEditorial;
	public URL m_imageURL;
	public ArrayList<Media> m_media;
	
	public static Playlist initWithDictionary(HashMap<String, Object > dict)
	{
		Playlist playlist = new Playlist();
		playlist.m_title = dict.get("title").toString();
		playlist.m_id = ((Integer)dict.get("id")).intValue();
		
		String imageURLString =  (dict.get("image_url")).toString();
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
		
		playlist.m_editorial = dict.get("editorial").toString();
		ArrayList<Object> mediaList = (ArrayList<Object>)dict.get("media");
		
		playlist.m_media = new ArrayList<Media>();
		for(int i = 0; i < mediaList.size(); i++)
		{
			playlist.m_media.add(Media.initWithDictionary((HashMap<String, Object>)mediaList.get(i)));
		}
		
		return playlist;
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
