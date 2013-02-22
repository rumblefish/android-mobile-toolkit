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
	
	public void cancel()
	{
		if(m_task != null)
		{
			m_task.cancel(true);
		}
	}
}
