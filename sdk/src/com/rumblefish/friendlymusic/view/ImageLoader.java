package com.rumblefish.friendlymusic.view;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	static private ImageLoader _instance;

	static public ImageLoader getInstance() {
		if (_instance == null) {
			_instance = new ImageLoader();
		}
		return _instance;
	}

	private HashMap<String, Bitmap> _urlToBitmap;
	private Queue<Group> _queue;
	private DownloadThread _thread;
	private Bitmap _missing;
	private boolean _busy;

	public static final boolean SHOWLOG = false;
	/**
	 * Constructor
	 */
	private ImageLoader() {
		_urlToBitmap = new HashMap<String, Bitmap>();
		_queue = new LinkedList<Group>();
		_busy = false;
	}

	public boolean isBusy()
	{
		Iterator<Group> it = _queue.iterator();
		
		if(_busy == true)
			return true;
		else if(it.hasNext())
			return true;
			
		return false;
	}
	
	public Bitmap get(String url) {
		Bitmap res = _urlToBitmap.get(url);
		if(res != null)
			return res;
		
		Iterator<String> iter = _urlToBitmap.keySet().iterator();
		while(iter.hasNext())
		{
			String keyurl = iter.next();
			if(keyurl.equals(url))
			{
				return _urlToBitmap.get(keyurl);
			}
			
		}
		return null;
	}

	public void load(ImageView image, String url) {
		load(image, url, false);
	}

	public void load(ImageView image, String url, boolean cache) {
		if (get(url) != null) {
			if (image != null) {
				if(SHOWLOG)
				{
					Log.v("ImageLoader", "Image loaded " + url + " imageview.id = " + image + " bitmap.id = " + get(url));
				}
				image.setImageBitmap(get(url));
			}
		} else {
			if(image != null)
				image.setImageBitmap(null);
			queue(image, url, cache);
		}
	}

	public void queue(ImageView image, String url, boolean cache) {
		Iterator<Group> it = _queue.iterator();
		if (image != null) {
			while (it.hasNext()) {
				Group group = it.next();
				if (group.image!=null && group.image.equals(image)) {
					it.remove();
					//break;
				}
			}
		}
		
		it = _queue.iterator();
		if (url != null) {
			while (it.hasNext()) {
				Group group = it.next();
				if (group.url!=null && group.url.equals(url)) {
					it.remove();
					//break;
				}
			}
		}
		
		_queue.add(new Group(image, url, null, cache));
		loadNext();
	}

	public void clearQueue() {
		_queue = new LinkedList<Group>();
	}

	public void clearCache() {
		_urlToBitmap = new HashMap<String, Bitmap>();
	}

	public void cancel() {
		clearQueue();
		if (_thread != null) {
			_thread.disconnect();
			_thread = null;
		}
	}

	public void setMissingBitmap(Bitmap bitmap) {
		_missing = bitmap;
	}

	private void loadNext() {
		Iterator<Group> it = _queue.iterator();
		if (!_busy && it.hasNext()) {
			_busy = true;
			Group group = it.next();
			it.remove();
			// double check image availability
			if (get(group.url) != null) {
				if (group.image != null) {
					if(SHOWLOG)
					{
						Log.v("ImageLoader", "Setting image image.id = " + group.image + " bitmap.id = " + group.bitmap + " url = " + group.url);
					}
					group.image.setImageBitmap(get(group.url));
				}
				_busy = false;
				loadNext();
			} else {
				_thread = new DownloadThread(group);
				_thread.start();
			}
		}
	}

	private void onLoad() {
		if (_thread != null) {
			Group group = _thread.group;
			if (group.bitmap != null) {
				if (group.cache) {
					if(get(group.url)!=null)
						_urlToBitmap.remove(group.url);
					
					_urlToBitmap.put(group.url, group.bitmap);
				}
				if (group.image != null) {
					if(SHOWLOG)
					{
						Log.v("ImageLoader", "Setting image image.id = " + group.image + " bitmap.id=" + group.bitmap);
					}
					group.image.setImageBitmap(group.bitmap);
				}
			} else if (_missing != null) {
				if (group.image != null) {
					
					Log.v("ImageLoader", "Setting image image.id = " + group.image + " bitmap.id=" + _missing);
					group.image.setImageBitmap(_missing);
				}
			}
		}
		_thread = null;
		_busy = false;
		loadNext();
	}

	private class Group {
		public Group(ImageView image, String url, Bitmap bitmap, boolean cache) {
			this.image = image;
			this.url = url;
			this.bitmap = bitmap;
			this.cache = cache;
		}

		public ImageView image;
		public String url;
		public Bitmap bitmap;
		public boolean cache;
	}

	private class DownloadThread extends Thread {
		final Handler threadHandler = new Handler();
		final Runnable threadCallback = new Runnable() {
			public void run() {
				onLoad();
			}
		};
		private HttpURLConnection _conn;
		public Group group;

		public DownloadThread(Group group) {
			this.group = group;
		}

		@Override
		public void run() {
			InputStream inStream = null;
			_conn = null;
			try {
				if(SHOWLOG)
				{
					Log.v("ImageLoader", "Download Started " + group.url);
				}
				String url = group.url;
				url = url.replace(" ", "%20");
				_conn = (HttpURLConnection) new URL(url).openConnection();
				_conn.setDoInput(true);
				_conn.connect();
				inStream = _conn.getInputStream();
				group.bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				_conn.disconnect();
				inStream = null;
				_conn = null;
				if(SHOWLOG)
				{
					Log.v("ImageLoader", "Download ended " + group.url + " bitmap.id = " + group.bitmap);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				// nothing
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception ex) {
				}
			}
			disconnect();
			inStream = null;
			_conn = null;
			threadHandler.post(threadCallback);
		}

		public void disconnect() {
			if (_conn != null) {
				_conn.disconnect();
			}
		}
	}
}
