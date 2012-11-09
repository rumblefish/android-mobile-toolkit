package com.rumblefish.friendlymusic.components;


import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
	public static final String LOGTAG = "Utils";
	
	public static int m_screenWidth;
	public static int m_screenHeight;
	
	public static float m_screenScaleX;
	public static float m_screenScaleY;
	
	public static float DEFAULT_WIDTH = 800;
	public static float DEFAULT_HEIGHT = 1280; //1232;
	
	public static void caculateScales(Activity ctxt)
	{
		Display display = ctxt.getWindowManager().getDefaultDisplay();
		m_screenWidth = display.getWidth();
		m_screenHeight = display.getHeight();
		
		m_screenScaleX = m_screenWidth / DEFAULT_WIDTH; 
		m_screenScaleY = m_screenHeight / DEFAULT_HEIGHT;
	}
	
	public static float screenScaleX()
	{
		return m_screenScaleX;
	}
	
	public static float screenScaleY()
	{
		return m_screenScaleY;
	}
	
	public static int getWorldX(float x)
    {
    	return (int)(x * m_screenScaleX);
    }
	
    public static int getWorldY(float y)
    {
    	return (int)(y * m_screenScaleY);
    }
    
    public static int getScreenX(float x)
    {
    	return (int)(x / m_screenScaleX);
    }
    
    public static int getScreenY(float y)
    {
    	return (int)(y / m_screenScaleY);
    }
    
    public static Bitmap loadScaledBitmap(Activity ctxt,int resourceId)
    {
    	try {
    		
            InputStream is = ctxt.getResources().openRawResource(resourceId); //getContentResolver().openInputStream(selectedImageURI);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bitmap;

            opts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            Log.v(LOGTAG, "height : " + opts.outHeight);
            Log.v(LOGTAG, "width : " + opts.outWidth);
            Log.v(LOGTAG, "mime : " + opts.outMimeType);
            
            BitmapFactory.Options ops2 = new BitmapFactory.Options();
            WindowManager win = ctxt.getWindowManager();
            Display disp = win.getDefaultDisplay();
            int width = disp.getWidth();
            int height = disp.getHeight();
            float w = opts.outWidth;
            //float h = opts.outHeight;

            Log.v(LOGTAG, "display width: " + width + " height: " +  height);
            int wscale = (int)(w / width);
            //int hscale = (int)(h / height);
            int scale = wscale;
            
            /*if( w > h) //landscape
            {
            	scale = (int)w / height;//hscale;
            }
            else //portrait 
            {
            	scale = wscale;
            }*/
            
            if(scale < 1)
            	scale = 1;
            
            Log.v(LOGTAG, "scale " + scale);
            
            ops2.inSampleSize = scale;

            is = ctxt.getResources().openRawResource(resourceId);
            bitmap = BitmapFactory.decodeStream(is, null, ops2);
            
            return bitmap;
       } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }

       return null;
    }
}
