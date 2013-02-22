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
package com.rumblefish.friendlymusic.view.coverflow;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class BitmapAdapter extends AbstractCoverFlowImageAdapter {

    //private static final String TAG = BitmapAdapter.class.getSimpleName();
    
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
        //Log.v(TAG, "creating item " + position);
        return m_bitmaps.get(position);
    }

}
