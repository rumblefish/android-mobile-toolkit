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
        	 if(obj == null)
        	 {
        		 m_delegate.onError();
        	 }
        	 else
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
