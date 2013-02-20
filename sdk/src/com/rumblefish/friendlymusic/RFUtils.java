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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

public class RFUtils {

	public static int getColorFromFloatVal(float red, float green, float blue, float alpha)
    {
    	return Color.argb((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255));
    }
	
	
	
	public static void saveBitmapToPath(Context ctxt, String path, String filename, Bitmap bm)
	{
		File directory = new File(Environment.getExternalStorageDirectory(), path);
        if(!directory.exists())
        	directory.mkdir();
        File lastFile = new File(directory, filename);
        
        try
        {
	        if(lastFile.createNewFile()==false)
	        {
	        	lastFile.delete();
	        	lastFile.createNewFile();
	        }
	        
	        FileOutputStream ostream = new FileOutputStream(lastFile);
	        bm.compress(CompressFormat.JPEG, 100, ostream);
	        ostream.close();
        }
        catch(Exception e)
        {
        	
        }
	}
	
	public static Bitmap getBitmapFromPath(Context ctxt, String path, String filename)
	{
        File directory = new File(Environment.getExternalStorageDirectory(), path);
        if(!directory.exists())
        	directory.mkdir();
        File file = new File(directory, filename);
        return RFUtils.getBitmapFromUri(ctxt, Uri.fromFile(file));
	}
	
	public static Bitmap getBitmapFromUri(Context ctxt, Uri selectedImageURI)
    {
    	try {
            InputStream is = ctxt.getContentResolver().openInputStream(selectedImageURI);

            is = ctxt.getContentResolver().openInputStream(selectedImageURI);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
       } catch (FileNotFoundException e) {
            
       }

       return null;
    }
}
