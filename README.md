
# Rumblefish Android Toolkit(RAT)

Copyright 2012, Rumblefish, Inc.

## License

This software is provided for free and is distributed under the Apache 2.0 license. See the license file(License.txt) for more details. The latest version of the Rumblefish Android Toolkit can be downloaded at https://github.com/rumblefish/android-mobile-toolkit

## Terms of Use

Use of Rumblefish API is governed by the [Rumblefish API agreement](https://sandbox.rumblefish.com/agreement) and [Rumblefish Branding Requirements](https://sandbox.rumblefish.com/branding).

## What is the Rumblefish Android Toolkit (RAT)? 

The RAT demonstrates how to interact with Rumblefish’s API to search for and play music from Rumblefish’s music catalog. The RAT contains examples of how to browse by mood, playlist, and occasion as well as play tracks.
For more info on Rumblefish’s API, checkout https://sandbox.rumblefish.com for documentation and examples.

## What Can I Do With the RAT? 

Build music licensing into your android apps! The RAT is configured to use Rumblefish’s sandbox API environment which contains a limited number of Rumblefish tracks and
can not issue commercial licenses or delivery high quality tracks for download. Contact us at developers@rumblefish.com when you are ready to set up a production
portal to enable these features.

## Hacking on the code 
This repository uses git submodules to pull in its dependencies.  **Make sure to perform a recursive submodule initialization after cloning.**

`git clone git@github.com:rumblefish/android-mobile-toolkit.git git submodule update --init --recursive`


The `demo/` directory contains a demo project that uses the SDK. The `sdk/` directory contains the SDK project itself. Assuming the submodules in your clone are up-to-date, you should be able to simply build and hack on either project in eclipse in the usual manner. 
You need [Android SDK](http://developer.android.com/sdk/index.html) to be installed in your development environment. 
See following instructions to how to use RAT and how to build a test project in Eclipse. 
[Here](http://developer.android.com/tools/projects/index.html) is showing how to organize project with libraries.

- RAT itself is a library project.
And also can be compiled into executable binary by changing project type in project property settings.

- Create a new project which will use RAT.
- Add reference to the library project
  * Right click on the project created in Package Explorer and Click Properties
  * Go to Android page.
  * Click Add button in library area and select RumbleFish Android Toolkit project.
  * Click OK to finish.
- Configure AndroidManifest.xml
  * RAT can be run on all of devices which is running android 2.2 above. So you can set android:minSdkVersion to 8 or above.
  * Add internet permission as it uses internet connection for interaction with sandbox server.
  
   `<uses-permission android:name="android.permission.INTERNET" />`
  		
  * Add activities
  
	<activity android:name="com.rumblefish.friendlymusic.FriendlyMusic"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
	
	<activity android:name="com.rumblefish.friendlymusic.MoodMap"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
	
	<activity android:name="com.rumblefish.friendlymusic.OccasionActivity"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
	
	<activity android:name="com.rumblefish.friendlymusic.CoverFlowActivity"
		android:screenOrientation="landscape"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
	
	<activity android:name="com.rumblefish.friendlymusic.PlaylistActivity"
		android:configChanges="orientation|keyboardHidden"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
	
	<activity android:name="com.rumblefish.friendlymusic.AlbumActivity"
		android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
	</activity>
       	
- You have been granted access to RFAPI class which contains all of RumbleFish API functions.
  * To initialize connection to server, add following code
  
	`RFAPI.rumbleWithEnvironment(RFAPIEnv.RFAPIEnvProduction, "PUBLIC_KEY", "PASSWORD");`
  		
  * FriendlyMusic is the main Activity for RAT and you can simply send intent to show the activity. Please note that opening main activity after the RFAPI is initialized, you can check this by calling
    
	`RFAPI.getSingleTone().isInitialized()`

