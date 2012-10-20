package com.rumblefish.friendlymusic.api;

public interface ProducerDelegate
{
	public abstract void onResult(Object obj);
	public abstract void onError();
}