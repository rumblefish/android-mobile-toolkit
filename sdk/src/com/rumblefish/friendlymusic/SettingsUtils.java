package com.rumblefish.friendlymusic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsUtils {
	
	public static final String PREFERENCE_NAME = "rumblefish_pref";
	
	public static boolean getBoolForKey(Activity ctx, String keystr, boolean defValue)
	{
		SharedPreferences settings = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(keystr, defValue);
	}
	
	public static void setBoolForKey(Activity ctx, String keystr, boolean value)
	{
		SharedPreferences settings = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putBoolean(keystr, value);
		editor.commit();
	}
}
