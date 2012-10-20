package com.rumblefish.friendlymusic.api;

import android.os.AsyncTask;


public class RequestTask extends AsyncTask<URLRequest, Integer, Long> {

	public ProducerDelegate m_delegate;
	public ResultParser m_parser;
	public String 	m_result;
	 
    protected void onProgressUpdate(Integer... progress) {
         
    }

    protected void onPostExecute(Long result) {
         if(result.intValue() == 0)
         {
        	 m_delegate.onError();
         }
         else
         {
        	 Object obj = m_parser.parse(m_result);
        	 m_delegate.onResult(obj);
         }
    }

	@Override
	protected Long doInBackground(URLRequest... params) {
		URLRequest request = params[0];
		
		m_result = WebRequest.webRequest(request);
		if(m_result == null)
			return Long.valueOf(0);
		
		return Long.valueOf(1);
	}
 }