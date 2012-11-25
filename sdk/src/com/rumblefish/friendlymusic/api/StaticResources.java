package com.rumblefish.friendlymusic.api;

import android.graphics.Point;

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
	
	//play list activity
	public static SongListView m_plSongListView = null;
	
	//album activity
	public static Playlist	m_albumPlaylist = null;
	public static int		m_albumPlaylistId = -1;
	public static SongListView m_albumSongListView = null;
	
}
