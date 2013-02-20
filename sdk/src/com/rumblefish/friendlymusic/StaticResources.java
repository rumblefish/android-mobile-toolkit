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
package com.rumblefish.friendlymusic;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.rumblefish.friendlymusic.OccasionActivity.OccassionActivitySaveBundle;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.mediaplayer.StreamingMediaPlayer;
import com.rumblefish.friendlymusic.view.SongListView;

public class StaticResources {
	//public static MediaPlayer m_mediaPlayer = null;
	public static StreamingMediaPlayer m_mediaPlayer = null;
	
	public static Playlist	m_playlist = null;
	public static int  m_playlistID = -1;
	public static int	m_selectedCellID = -1;
	public static boolean 	m_isPlaying = false;
	public static Point	m_crosshairPos = null;
	public static int	m_selectedColor;
	
	
	//occasions activity
	public static OccassionActivitySaveBundle m_occasionBundle = null;
	
	//play list activity
	public static SongListView m_plSongListView = null;
	
	//album activity
	public static Playlist	m_albumPlaylist = null;
	public static int		m_albumPlaylistId = -1;
	public static SongListView m_albumSongListView = null;
	
	//cover flow activity
	public static ArrayList<Playlist> m_cfPlaylists = null;
	public static ArrayList<Bitmap> m_cfBitmaps = null;
	
	
}
