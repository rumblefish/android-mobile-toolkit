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
