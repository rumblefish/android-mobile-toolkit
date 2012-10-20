package com.rumblefish.friendlymusic.api;

import java.util.List;

import org.apache.http.NameValuePair;

public class URLRequest
{
	public String m_serverURL;
	public List<NameValuePair> m_nameValuePairs;
	public int m_timelimit;
	
	public URLRequest()
	{
		m_serverURL = null;
		m_nameValuePairs = null;
		m_timelimit = 10000;
	}
}