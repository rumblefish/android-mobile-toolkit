package com.rumblefish.friendlymusic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.rumblefish.friendlymusic.api.RFAPI.ProducerDelegate;
import com.rumblefish.friendlymusic.api.RFAPI.ResultParser;

public class WebRequest {

	public class URLRequest
	{
		public String m_serverURL;
		List<NameValuePair> nameValuePairs; 
	}
	
	public static Producer producerWithURLRequest(URLRequest request, ResultParser parser)
	{
		Producer prod = new Producer();
		return prod;
	}
	
	public class Producer
	{
		public ProducerDelegate m_delegate;
		public URLRequest m_request;
		public RequestTask m_task;
		public ResultParser m_parser;
		
		public Producer(URLRequest request, ResultParser parser)
		{
			m_request = request;
			m_parser = parser;
			m_task = null;
		}
		
		public void run()
		{
			if(m_task != null)
			{
				m_task.cancel(true);
			}
			
			m_task = new RequestTask();
			m_task.m_delegate = m_delegate;
			m_task.m_parser = m_parser;
			m_task.execute(m_request);
			
		}
	}

	public class RequestTask extends AsyncTask<URLRequest, Integer, Long> {

		public ProducerDelegate m_delegate;
		public ResultParser m_parser;
		 
	    protected void onProgressUpdate(Integer... progress) {
	         
	    }

	    protected void onPostExecute(Long result) {
	         
	    }

		@Override
		protected Long doInBackground(URLRequest... params) {
			URLRequest request = params[0];
			
			HttpPost httpPost = new HttpPost(request.m_serverURL);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(request.nameValuePairs));
			}
			catch(Exception e)
			{
				return Long.valueOf(0);
			}
			
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			
			try
			{
				HttpResponse response = client.execute(httpPost);
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
					return Long.valueOf(0);
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
			
			
			return Long.valueOf(1);
		}
	 }
}
