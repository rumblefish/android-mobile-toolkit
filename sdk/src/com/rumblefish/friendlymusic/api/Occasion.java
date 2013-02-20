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
