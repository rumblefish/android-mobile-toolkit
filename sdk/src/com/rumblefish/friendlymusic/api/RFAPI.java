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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;



public class RFAPI {
	
	public static final String LOGTAG = "RFAPI";
	// enum variables
	public enum RFAPIEnv
	{
		RFAPIEnvSandbox,
		RFAPIEnvProduction
	}
	
	public enum RFAPIResource {
	    RFAPIResourceArtist,
	    RFAPIResourceAuthenticate,
	    RFAPIResourceCatalog,
	    RFAPIResourceClear,
	    RFAPIResourceLicense,
	    RFAPIResourceMedia,
	    RFAPIResourceOccasion,
	    RFAPIResourcePlaylist,
	    RFAPIResourcePortal,
	    RFAPIResourceSearch,
	    RFAPIResourceSFXCategory
	}
	
	public enum RFAPIVersion {
	    RFAPIVersion2
	}
	
	public enum RFAPIMethod {
	    RFAPIMethodGET,
	    RFAPIMethodPOST
	};
	
	
	////////////////////////////////////////////////////////// Member Variables Start //////////////////////////////////////////////////////////////
	RFAPIVersion version;
	RFAPIEnv environment;
	String publicKey;
	String password;
	String accessToken;
	String ipAddress;
	String lastError;
	String lastResponse;
	
	boolean bInitialized = false;
	//NSError *lastError;
	//NSHTTPURLResponse *lastResponse;
	////////////////////////////////////////////////////////// Member Variables End //////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////// Singleton Start //////////////////////////////////////////////////////////////
	public static RFAPI m_instance;
	
	public static RFAPI getSingleTone()
	{
		if(m_instance == null)
		{
			m_instance = new RFAPI();
		}
		
		return m_instance;
	}
	////////////////////////////////////////////////////////// Singleton End //////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////////////////////////////////// Utils Functions Start //////////////////////////////////////////////////////////////
	public static int RFAPI_TIMEOUT = 30000; // request timeout

	public static URLRequest requestWithURL(URL url)
	{
		URLRequest request = new URLRequest();
		request.m_serverURL = url.toString();
		request.m_timelimit = RFAPI_TIMEOUT;
		request.m_nameValuePairs = null;
		return request;
	}
	
	public URLRequest requestResource(RFAPIResource resource, RFAPIMethod method, JSONObject parameters)
	{
		URLRequest request = new URLRequest();
		
		String url = urlStringForResource(resource, parameters);
		
		request.m_serverURL = url;
		request.m_timelimit = RFAPI_TIMEOUT;
		request.m_nameValuePairs = null;
		
		return request;
	}
	
	public String urlStringForResource(RFAPIResource resource, JSONObject parameters)
	{
		String baseURL = "https://" + this.getHost() + this.pathToResource(resource);
		String query = this.queryStringFor(parameters);
		
		return baseURL + query;
	}
	
	public String getHost()
	{
	    switch (this.environment) {
	        case RFAPIEnvProduction:
	            return "api.rumblefish.com";
	        case RFAPIEnvSandbox:
	            return "sandbox.rumblefish.com";
	        default:
	            // throw unknown environment exception.
	            return "unknown.com";
	    }
	}

	String pathToResource(RFAPIResource resource)
	{
	    String path = "unknown";
	    
	    switch (resource) {
	        case RFAPIResourceArtist:
	            path = "artist";
	            break;
	        case RFAPIResourceAuthenticate:
	            path = "authenticate";
	            break;
	        case RFAPIResourceCatalog:
	            path = "catalog";
	            break;
	        case RFAPIResourceClear:
	            path = "clear";
	            break;
	        case RFAPIResourceLicense:
	            path = "license";
	            break;
	        case RFAPIResourceMedia:
	            path = "media";
	            break;
	        case RFAPIResourceOccasion:
	            path = "occasion";
	            break;
	        case RFAPIResourcePlaylist:
	            path = "playlist";
	            break;
	        case RFAPIResourcePortal:
	            path = "portal";
	            break;
	        case RFAPIResourceSearch:
	            path = "search";
	            break;
	        case RFAPIResourceSFXCategory:
	            path = "sfx_category";
	            break;
	    }
	    
	    return "/v" + RFAPI.getVersionCode(this.version) + "/" + path;
	}

	public static int getVersionCode(RFAPIVersion version)
	{
		if(version == RFAPIVersion.RFAPIVersion2)
		{
			return 2;
		}
		return 0;
	}
	
	String queryStringFor(JSONObject parameters)
	{
		String queryString = "";
		if(parameters == null)
		{
			return "";
		}
		
		try {
			parameters.put("ip", this.ipAddress);
			if(this.accessToken != null && this.accessToken.length()!=0)
			{
				parameters.put("token", this.accessToken);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		Iterator<String> keys = parameters.keys();
		while(keys.hasNext())
		{
			String key = keys.next();
			
			String value = "";
			try
			{
				value = parameters.get(key).toString();
			}
			catch(Exception e)
			{
				value = "";
			}
			
			if(queryString.length() == 0)
			{
				queryString = queryString + "?";
			}
			else
			{
				queryString = queryString + "&";
			}
			
			if(key.length() != 0 && value.length() != 0)
			{
				queryString = queryString + this.escapeString(key) + "=" + this.escapeString(value);
			}
			else if(key.length() != 0)
			{
				queryString = queryString + this.escapeString(key);
			}
			
		}
	    
	    return queryString;
	}
	
	public String escapeString(String unencodedString)
	{
		//TODO 
		//check android URL encoder works
		return URLEncoder.encode(unencodedString);
	}
	////////////////////////////////////////////////////////// Utils Functions End //////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////// API Functions Start //////////////////////////////////////////////////////////////
	public static Producer retrieveIPAddress()
	{
		URL url;
		try {
			url = new URL("http://checkip.dyndns.org/");
		} catch (MalformedURLException e) {
			return null;
		}
		
		URLRequest request = requestWithURL(url);
		Producer producer = WebRequest.producerWithURLRequest(request, new ResultParser()
		{
			@Override
			public Object parse(String data) {
		        // in the structure of 
		        // <html><head><title>Current IP Check</title></head><body>Current IP Address: 199.223.126.116</body></html>
		        
		        int ipStart = data.indexOf(": ") + 2;
		        int ipEnd =  data.indexOf("</body>");
		        
		        String ip = data.substring(ipStart, ipEnd);
		        
				return ip;
			}
		});
		return producer;
	}
	
	public static Producer apiWithEnvironment(final RFAPIEnv environment, final RFAPIVersion version, final String publicKey, final String password)
	{
		Producer producer = retrieveIPAddress();
		producer.m_delegate = new ProducerDelegate()
		{
			@Override
			public void onResult(Object obj) {
				RFAPI api = RFAPI.getSingleTone();
				api.environment = environment;
				api.version = version;
				api.publicKey = publicKey;
				api.password = password;
				api.ipAddress = "180.184.28.214";//obj.toString();
				api.bInitialized = true;
				
				Log.v(LOGTAG,"apiWithEnvironment, RFAPI initialized");
			}

			@Override
			public void onError() {
					
			}
		};
		
		return producer;
	}
	
	public static void rumbleWithEnvironment(RFAPIEnv env, String publicKey, String password)
	{
		
		if(RFAPI.getSingleTone().isInitialized() == false)
		{
			Log.v(LOGTAG,"rumbleWithEnvironment, RFAPI initialization started!");
			Producer producer = RFAPI.apiWithEnvironment(env, RFAPIVersion.RFAPIVersion2, publicKey, password);
			producer.run();
		}
	}
	
	
	public String doRequest(URLRequest request)
	{
		String resultString;
		
		lastError = null;
		lastResponse = null;
		
		resultString = WebRequest.webRequest(request);
		if(resultString == null)
		{
			lastError = "Error";
		}
		lastResponse = resultString;
		
		return resultString;
		
	}
	
	public JSONObject resource(RFAPIResource resource, JSONObject params)
	{
		URLRequest request = requestResource(resource, RFAPIMethod.RFAPIMethodGET, params);
		String response = doRequest(request);
		
		
		JSONObject object = null;
		try {
			object = new JSONObject(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;	
	}
	
	public JSONObject resource(RFAPIResource resource, String resourceId)
	{
		JSONObject params = new JSONObject();
		try {
			params.put("id", resourceId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resource(resource, params);
	}

	public JSONObject resource(RFAPIResource resource)
	{
		JSONObject param = null;
		return resource(resource, param);
	}
	
	
	public Producer getPlaylistsWithOffset(int offset)
	{
		
		if(this.bInitialized == false)
			return null;
		
		JSONObject param = new JSONObject();
		
		try {
			param.put("start", String.valueOf(offset));
			
			URLRequest request = this.requestResource(RFAPIResource.RFAPIResourcePlaylist, RFAPIMethod.RFAPIMethodGET, param);
			
			Producer producer = WebRequest.producerWithURLRequest(request, new ResultParser()
			{
				@Override
				public Object parse(String data) {
					try
					{
						JSONObject object = new JSONObject(data);
						JSONArray playlists = object.getJSONArray("playlists");
						ArrayList<Playlist> arrayPlaylist = Playlist.getPlaylistList(playlists);
						return arrayPlaylist;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return null;
				}
				
			});
			
			return producer;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Producer getPlaylist(int playlistId)
	{
		if(this.bInitialized == false)
			return null;
		
		JSONObject param = new JSONObject();
		
		try {
			param.put("id", String.valueOf(playlistId));
			URLRequest request = this.requestResource(RFAPIResource.RFAPIResourcePlaylist, RFAPIMethod.RFAPIMethodGET, param);
			
			Producer producer = WebRequest.producerWithURLRequest(request, new ResultParser()
			{
				@Override
				public Object parse(String data) {
					try
					{
						JSONObject object = new JSONObject(data);
						JSONObject dict = object.getJSONObject("playlist");
						Playlist playlist = Playlist.initWithDictionary(dict);
						return playlist;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return null;
				}
				
			});
			
			return producer;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public Producer getOccasions()
	{
		if(this.bInitialized == false)
			return null;
		
		URLRequest request = this.requestResource(RFAPIResource.RFAPIResourceOccasion, RFAPIMethod.RFAPIMethodGET, null);
		
		Producer producer = WebRequest.producerWithURLRequest(request, new ResultParser()
		{
			@Override
			public Object parse(String data) {
				try
				{
					JSONObject object = new JSONObject(data);
					JSONArray occasions = object.getJSONArray("occasions");
					ArrayList<Occasion> arrayOccasion = Occasion.getPlayOccasion(occasions);
					return arrayOccasion;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		return producer;
	}
	
	public Producer getOccasions(int occasionId)
	{
		if(this.bInitialized == false)
			return null;
		
		JSONObject param = new JSONObject();
		try {
			param.put("id", String.valueOf(occasionId));
			URLRequest request = this.requestResource(RFAPIResource.RFAPIResourceOccasion, RFAPIMethod.RFAPIMethodGET, param);
			
			Producer producer = WebRequest.producerWithURLRequest(request, new ResultParser()
			{
				@Override
				public Object parse(String data) {
					try
					{
						JSONObject object = new JSONObject(data);
						JSONObject dict = object.getJSONObject("occasion");
						Occasion occasion = Occasion.initWithDictionary(dict);
						return occasion;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return null;
				}
				
			});
			
			return producer;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Bitmap getImageAtURL(URL url)
	{
		return WebRequest.getBitmapAtURL(url);
	}
	
	public boolean isInitialized()
	{
		return bInitialized;
	}
	////////////////////////////////////////////////////////// API Functions End //////////////////////////////////////////////////////////////

}
