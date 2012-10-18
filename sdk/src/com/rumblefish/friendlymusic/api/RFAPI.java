package com.rumblefish.friendlymusic.api;

import android.os.AsyncTask;


public class RFAPI {
	
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

	public static RFAPI m_instance;
	
	public RFAPI getSingleTone()
	{
		if(m_instance == null)
		{
			m_instance = new RFAPI();
		}
		
		return m_instance;
	}
	
	
		
	public abstract class ResultParser
	{
		public abstract Object parse(String data);
	}
	
	public interface ProducerDelegate
	{
		public abstract void onResult();
		public abstract void onError();
	}
}
