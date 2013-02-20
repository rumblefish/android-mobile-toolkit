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
	
	public static String getStringForKey(Activity ctx, String keystr, String defValue)
	{
		SharedPreferences settings = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getString(keystr, defValue);
	}
	
	public static void setStringForKey(Activity ctx, String keystr, String value)
	{
		SharedPreferences settings = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(keystr, value);
		editor.commit();
	}
}
