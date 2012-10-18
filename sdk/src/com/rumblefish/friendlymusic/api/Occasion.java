package com.rumblefish.friendlymusic.api;

import java.util.ArrayList;
import java.util.HashMap;

public class Occasion {
	public int m_id;
	public String m_name;
	public ArrayList<Occasion>	m_children;
	public ArrayList<Playlist> m_playlists;
	
	public static Occasion initWithDictionary(HashMap<String, Object > dict)
	{
		Occasion occasion = new Occasion();
		occasion.m_name = dict.get("name").toString();
		occasion.m_id = ((Integer)dict.get("id")).intValue();
		
		ArrayList<Object> children = (ArrayList<Object>)dict.get("children");
		occasion.m_children = new ArrayList<Occasion>();
		for(int i = 0; i < children.size(); i++)
		{
			occasion.m_children.add(Occasion.initWithDictionary((HashMap<String, Object>)children.get(i)));
		}
		
		ArrayList<Object> playlists = (ArrayList<Object>)dict.get("playlists");
		occasion.m_playlists = new ArrayList<Playlist>();
		for(int i = 0; i < playlists.size(); i++)
		{
			occasion.m_playlists.add(Playlist.initWithDictionary((HashMap<String, Object>)playlists.get(i)));
		}
		
		return occasion;
	}
	
}
