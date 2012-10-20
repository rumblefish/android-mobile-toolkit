package com.rumblefish.friendlymusic.api;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Occasion {
	public int m_id;
	public String m_name;
	public ArrayList<Occasion>	m_children;
	public ArrayList<Playlist> m_playlists;
	
	public static Occasion initWithDictionary(JSONObject dict)
	{
		
		Occasion occasion = new Occasion();
		
		try
		{	
			occasion.m_name = dict.getString("name");
		}
		catch(Exception e)
		{
			occasion.m_name = "";
		}
		
		try
		{	
			occasion.m_id = dict.getInt("id");
		}
		catch(Exception e)
		{
			occasion.m_id = 0;
		}
		
		try
		{	
			JSONArray children = dict.getJSONArray("children");
			occasion.m_children = getPlayOccasion(children);
		}
		catch(Exception e)
		{
			occasion.m_children = null;
		}
		
		
		try
		{	
			JSONArray playlists = dict.getJSONArray("playlists");
			occasion.m_playlists = Playlist.getPlaylistList(playlists);
		}
		catch(Exception e)
		{
			occasion.m_playlists = null;
		}
		
		return occasion;
	
	}
	
	public static ArrayList<Occasion> getPlayOccasion(JSONArray array)
	{
		ArrayList<Occasion> result = new ArrayList<Occasion>();
		for(int i = 0; i < array.length(); i++)
		{
			try
			{
				JSONObject obj = (JSONObject) array.get(i);
				Occasion newOccasion = Occasion.initWithDictionary(obj);
				if(newOccasion != null)
				{
					result.add(newOccasion);
				}
			}
			catch(Exception e)
			{
				continue;
			}
		}
		return result;
	}
}
