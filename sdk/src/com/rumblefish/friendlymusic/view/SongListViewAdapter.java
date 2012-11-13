package com.rumblefish.friendlymusic.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rumblefish.friendlymusic.R;
import com.rumblefish.friendlymusic.api.LocalPlaylist;
import com.rumblefish.friendlymusic.api.Media;
import com.rumblefish.friendlymusic.api.Playlist;
import com.rumblefish.friendlymusic.view.SongListView.ButtonStyle;

public class SongListViewAdapter extends ArrayAdapter<SongListViewItem>
{
	Context context;
	ArrayList<SongListViewItem> m_list;
	
	int m_resourceId ;
	int m_barColor = 0;
	ButtonStyle m_buttonStyle = ButtonStyle.BUTTON_CHECK;
	
	
	SongListViewAdapterController m_controller;
	
	
	public int getMediaItemPos(SongListViewItem item)
	{
		int itemidx = m_list.indexOf(item);
		int i;
		for(i = itemidx; i >= 0; i--)
		{
			SongListViewItem listItem = m_list.get(i);
			if(listItem.m_bIsHeader)
			{
				break;
			}
		}
		return itemidx - i;
	}
	
    public SongListViewAdapter(Context activity, int resourceId, ArrayList<SongListViewItem> arraylist, SongListViewAdapterController controller, int barColor)
    {
        super(activity, resourceId, arraylist);
        context = activity;
        m_list = arraylist;
        m_resourceId = resourceId;
        m_controller = controller;
        m_barColor = barColor;
    }
    
    public SongListViewAdapter(Context activity, int resourceId, ArrayList<SongListViewItem> arraylist, SongListViewAdapterController controller, int barColor, ButtonStyle style)
    {
        super(activity, resourceId, arraylist);
        context = activity;
        m_list = arraylist;
        m_resourceId = resourceId;
        m_controller = controller;
        m_barColor = barColor;
        m_buttonStyle = style;
    }


	public View getView(int i, View view, ViewGroup viewgroup)
    {
        View itemlayout = view;
        SongListViewItem item = m_list.get(i);

    	if(item.m_bIsHeader)
    	{
    		itemlayout = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.play_list_head_item, null);
    	}
    	else
    	{
    		itemlayout = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.play_list_item, null);
    	}
        
        if(item.m_bIsHeader)
        {
        	final View finalPlaylistView = itemlayout;
        	TextView tvTitleLabel = (TextView)finalPlaylistView.findViewById(R.id.tvTitleLabel);
	        ImageView ivHeader = (ImageView)finalPlaylistView.findViewById(R.id.ivHeaderImg);
	        
	        Playlist playlist = (Playlist)item.data;
	        tvTitleLabel.setText(playlist.m_title);
	        ImageLoader.getInstance().load(ivHeader, playlist.m_imageURL.toString(), true);
        }
        else
        {
	        final View finalMediaView = itemlayout;
	        final Media media = (Media)item.data;
	        
	        final TextView tvIndexLabel = (TextView)itemlayout.findViewById(R.id.tvIndexLabel);
	        final ProgressBar pbSongPB = (ProgressBar)itemlayout.findViewById(R.id.pbSongProgressBar);
	        final ImageView ivBtnStop = (ImageView)itemlayout.findViewById(R.id.ivBtnStop);
	        final TextView tvTitleLabel = (TextView)itemlayout.findViewById(R.id.tvTitleLabel);
	        final TextView tvColorBar = (TextView)itemlayout.findViewById(R.id.tvColorBar);
	        final ImageView ivBtnAdd = (ImageView)itemlayout.findViewById(R.id.ivBtnAdd);
	        final ImageView ivBtnCheck = (ImageView)itemlayout.findViewById(R.id.ivBtnCheck);
	        final int position = i;
	        if(m_buttonStyle == ButtonStyle.BUTTON_REMOVE)
	        {
	        	ivBtnCheck.setImageResource(R.drawable.btn_remove);
	        }
	        
	        int itempos = getMediaItemPos(item);
	        tvIndexLabel.setText(String.valueOf(itempos));
	        tvTitleLabel.setText(media.m_title);
	        if(m_barColor == -1)
	        	tvColorBar.setVisibility(View.INVISIBLE);
	        else
	        {
	        	tvColorBar.setVisibility(View.VISIBLE);
	        	tvColorBar.setBackgroundColor(m_barColor);
	        }
	        
	        tvIndexLabel.setVisibility(View.VISIBLE);
	        tvTitleLabel.setVisibility(View.VISIBLE);
	        tvColorBar.setVisibility(View.VISIBLE);
	        pbSongPB.setVisibility(View.INVISIBLE);
	        ivBtnStop.setVisibility(View.INVISIBLE);
	        
	        if(LocalPlaylist.sharedPlaylist().existsInPlaylist(media))
	        {
	        	ivBtnAdd.setVisibility(View.INVISIBLE);
	        	ivBtnCheck.setVisibility(View.VISIBLE);
	        }
	        else
	        {
	        	ivBtnAdd.setVisibility(View.VISIBLE);
	        	ivBtnCheck.setVisibility(View.INVISIBLE);
	        }
	        
	        if(m_controller.getSelectedItemID() == i)
	        {
	        	if(m_controller.getCurrentItemStatus() == ItemStatus.PLAYING)
	        	{
	        		//playing current item
	        		setItemStatus(finalMediaView, ItemStatus.PLAYING);
	        	}
	        	else
	        	{
	        		// downloading
	        		setItemStatus(finalMediaView, ItemStatus.DOWNLOADING);
	        	}
	        }
	        else
	        {
	        	setItemStatus(finalMediaView, ItemStatus.NORMAL);
	        }
	        
	        
	        View.OnClickListener clicklistener = new View.OnClickListener()
		    {
				@Override
				public void onClick(View v) {
					if(v == ivBtnAdd)
					{
						ivBtnCheck.setVisibility(View.VISIBLE);
						ivBtnAdd.setVisibility(View.INVISIBLE);
						LocalPlaylist.sharedPlaylist().addToPlaylist(media);
						
						m_controller.itemClicked(ItemButtonTypes.ADD, position);
					}
					else if(v == ivBtnCheck)
					{
						ivBtnCheck.setVisibility(View.INVISIBLE);
						ivBtnAdd.setVisibility(View.VISIBLE);
						LocalPlaylist.sharedPlaylist().removeFromPlaylist(media);
						if(m_buttonStyle == ButtonStyle.BUTTON_REMOVE)
						{
							m_controller.itemClicked(ItemButtonTypes.REMOVE, position);
						}
						else
						{
							m_controller.itemClicked(ItemButtonTypes.CHECK, position);
						}
					}
					else if(v == ivBtnStop)
					{
						setItemStatus(finalMediaView, ItemStatus.NORMAL);
						m_controller.itemClicked(ItemButtonTypes.STOP, position);
					}
				}
		    };
		    
		    ivBtnAdd.setOnClickListener(clicklistener);
		    ivBtnCheck.setOnClickListener(clicklistener);
		    ivBtnStop.setOnClickListener(clicklistener);
        }
        
        return itemlayout;
    }
    
    
    
    public void setItemStatus(View view, ItemStatus status)
    {
    	if(view != null)
    	{
    		View medialayout = view;
	        
	        TextView tvIndexLabel = (TextView)medialayout.findViewById(R.id.tvIndexLabel);
	        ProgressBar pbSongPB = (ProgressBar)medialayout.findViewById(R.id.pbSongProgressBar);
	        ImageView ivBtnStop = (ImageView)medialayout.findViewById(R.id.ivBtnStop);
	        
	        if(status == ItemStatus.NORMAL)
	        {
	        	tvIndexLabel.setVisibility(View.VISIBLE);
	        	pbSongPB.setVisibility(View.INVISIBLE);
	        	ivBtnStop.setVisibility(View.INVISIBLE);
	        }
	        else if(status == ItemStatus.PLAYING)
	        {
	        	tvIndexLabel.setVisibility(View.INVISIBLE);
	        	pbSongPB.setVisibility(View.INVISIBLE);
	        	ivBtnStop.setVisibility(View.VISIBLE);
	        }
	        else if(status == ItemStatus.DOWNLOADING)
	        {
	        	tvIndexLabel.setVisibility(View.INVISIBLE);
	        	pbSongPB.setVisibility(View.VISIBLE);
	        	ivBtnStop.setVisibility(View.INVISIBLE);
	        }
    	}
    }
    
    public enum ItemStatus
	{
		NORMAL,
		DOWNLOADING,
		PLAYING
	}
    
    public enum ItemButtonTypes
	{
		ADD,
		CHECK,
		REMOVE,
		STOP
	}
    
    public interface SongListViewAdapterController
    {
    	public abstract int getSelectedItemID();
    	public abstract ItemStatus getCurrentItemStatus();
    	public abstract void itemClicked(ItemButtonTypes buttonType, int position);
    }
    
    
}