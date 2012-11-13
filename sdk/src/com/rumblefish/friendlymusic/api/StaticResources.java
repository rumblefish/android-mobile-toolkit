package com.rumblefish.friendlymusic.api;

import android.graphics.Point;

import com.rumblefish.friendlymusic.mediaplayer.StreamingMediaPlayer;

public class StaticResources {
	//public static MediaPlayer m_mediaPlayer = null;
	public static StreamingMediaPlayer m_mediaPlayer = null;
	
	public static Playlist	m_playlist = null;
	public static int	m_selectedCellID = -1;
	public static boolean 	m_isPlaying = false;
	public static Point	m_crosshairPos = null;
	public static int	m_selectedColor;
}
