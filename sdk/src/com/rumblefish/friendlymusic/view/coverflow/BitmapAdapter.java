package com.rumblefish.friendlymusic.view.coverflow;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class BitmapAdapter extends AbstractCoverFlowImageAdapter {

    private static final String TAG = BitmapAdapter.class.getSimpleName();
    
    public final Context context;
    ArrayList<Bitmap>	m_bitmaps;

    /**
     * Creates the adapter with default set of resource images.
     * 
     * @param context
     *            context
     */
    public BitmapAdapter(final Context context, ArrayList<Bitmap> bitmaps) {
        super();
        this.context = context;
        m_bitmaps = bitmaps;
    }
    
    

    @Override
    public synchronized int getCount() {
        return m_bitmaps.size();
    }

    @Override
    protected Bitmap createBitmap(final int position) {
        Log.v(TAG, "creating item " + position);
        return m_bitmaps.get(position);
    }

}