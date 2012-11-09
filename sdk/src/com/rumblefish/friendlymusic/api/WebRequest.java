package com.rumblefish.friendlymusic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class WebRequest {

	public static final String LOGTAG = "WebRequest";
	
	public static Producer producerWithURLRequest(URLRequest request, ResultParser parser)
	{
		Producer prod = new Producer(request, parser);
		return prod;
	}
	
	public static String webRequest(URLRequest request)
	{
		HttpPost httpPost = null;
		HttpGet httpGet = null;
		if(request.m_nameValuePairs != null)
		{
			httpPost = new HttpPost(request.m_serverURL);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(request.m_nameValuePairs));
			}
			catch(Exception e)
			{
				return null;
			}
		}
		else
		{
			httpGet = new HttpGet(request.m_serverURL);
			Log.v(LOGTAG, request.m_serverURL);
		}
		
		
		HttpClient client = getNewHttpClient(request.m_timelimit);
		
		StringBuilder builder = new StringBuilder();
		
		try
		{
			HttpResponse response;
			if(request.m_nameValuePairs != null)
				response = client.execute(httpPost);
			else
				response = client.execute(httpGet);
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				try { 
					content.close(); 
				} catch (IOException e) { 
					
				} 
			} else {
				Log.e("WebRequest", "Failed to download file");
				return null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally
		{
			client.getConnectionManager().closeExpiredConnections(); 
			client.getConnectionManager().closeIdleConnections(0, TimeUnit.NANOSECONDS); 
		}
		
		String resultString = builder.toString();
		try
		{
			if(resultString == null || resultString.length() == 0)
			{
				return null;
			}
			return resultString;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static Bitmap getBitmapAtURL(URL url)
	{
		InputStream inStream = null;
		HttpURLConnection _conn = null;
		Bitmap bitmap = null;
		try {
			
			_conn = (HttpURLConnection) url.openConnection();
			_conn.setDoInput(true);
			_conn.connect();
			inStream = _conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inStream);
			inStream.close();
			_conn.disconnect();
			inStream = null;
			_conn = null;
		} catch (Exception ex) {
			// nothing
		}
		if (inStream != null) {
			try {
				inStream.close();
			} catch (Exception ex) {
			}
		}
		if(_conn != null)
		{
			_conn.disconnect();
		}
		
		return bitmap;
	}
	
	public static HttpClient getNewHttpClient(int timelimit) {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
	        
			
	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        HttpConnectionParams.setConnectionTimeout(params, timelimit);
			HttpConnectionParams.setSoTimeout(params, timelimit);
			

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return new DefaultHttpClient();
	    }
	}
	
}
