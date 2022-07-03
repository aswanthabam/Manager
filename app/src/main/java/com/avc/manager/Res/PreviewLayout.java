package com.avc.manager.Res;
import android.widget.*;
import android.util.*;
import android.content.*;
import android.view.*;
import java.io.*;
import com.avc.manager.*;
import android.media.*;
import android.app.*;
import android.support.v7.app.*;
import android.preference.*;
import com.avc.manager.Adapter.*;
/*
 Preview layout ( Similar to imagevideo preview dialog)
 This class is a associative class and didnt extends any base class
*/
public class PreviewLayout
{
	public Context con;
	private ImageView v1;
	private VideoView v2;
	private MediaPlayer player;
	public RelativeLayout parent;
	private int SystemWidth,SystemHeight,density;
	public static final int CENTER_CROP = 10;
	public static final int AUTO_CROP = 11;
	public static final int MUTE = 10;
	public static final int UNMUTE = 11;
	public boolean preview_video;
	public boolean video = false;
	// Different type of initializers 
	// with some attrivute set to default or not 
	// Like fill,mute,loop video etc.
	public PreviewLayout(Context m,ImageView vv,VideoView vvv,int fill,int mute,boolean loop)
	{
		init(m,vv,vvv,fill,mute,loop);
	}
	public PreviewLayout(Context m,ImageView vv,VideoView vvv,int fill,int mute)
	{
		init(m,vv,vvv,fill,mute,true);
	}
	public PreviewLayout(Context m,ImageView vv,VideoView vvv,int fill)
	{
		init(m,vv,vvv,fill,MUTE,true);
	}
	public PreviewLayout(Context m,ImageView vv,VideoView vvv)
	{
		init(m,vv,vvv,AUTO_CROP,MUTE,true);
	}
	
	// Initizing function
		// the diffecnt intializers of the class will set some defaukt values and call this function to class initalizatio 
			// Here we set dufferent values like if want to loop, mute etc.
	public void init(Context m,ImageView vv,VideoView vvv,final int fill,final int mute,final boolean loop)
	{
		// Get display width and set the width to linear layout for same width and height
		Display display = ((AppCompatActivity)m).getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);
		// Store the width and height in pixels for future use
		SystemWidth = outMetrics.widthPixels;
		SystemHeight = outMetrics.heightPixels;
		density  = (int) ((AppCompatActivity)m). getResources().getDisplayMetrics().density; // Get the density of device for future use
		
		con = m;
		v1 = vv; v2 = vvv;
		v2.setMediaController(null);
		
		v2.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override public void onPrepared(MediaPlayer p)
			{
				player = p;
				
				switch(fill)
				{
					case CENTER_CROP:
						v1.setScaleType(ImageView.ScaleType.CENTER_CROP);
						float width;
						float height;
						if(parent != null)
						{
							width = parent.getWidth();
							height = parent.getHeight();
						}else{
							width = v2.getWidth();
							height = v2.getHeight();
						}
						float screenR = width/height;
						
						float vidW = p.getVideoWidth() / (float) density;
						float vidH = p.getVideoHeight() / (float) density;
						float videoR = vidW/vidH;
						
						float scaleX = width/vidW;
						float scaleY = height/vidH;
						
						if(scaleX < 1f) scaleX = 1f;
						if(scaleY < 1f) scaleY = 1f;
						
						v2.setScaleX(scaleX);
						v2.setScaleY(scaleY);
						
						
						break;
					case AUTO_CROP:

						break;
				}
				
				switch(mute)
				{
					case MUTE:
						mute();
						break;
					case UNMUTE:
						unmute();
						break;
					default: mute();
				}
				
				p.setLooping(loop);
			}
		});
		
		switch(fill)
		{
			case CENTER_CROP:
				v1.setScaleType(ImageView.ScaleType.CENTER_CROP);
				break;
			case AUTO_CROP:

				break;
		}
		
		v2.setVisibility(View.GONE);
		v1.setVisibility(View.GONE);
		preview_video = PreferenceManager.getDefaultSharedPreferences(con).getBoolean("preview_video",true);
		
	}
	
	// Returns true if the given file is video
	
	public boolean isVideo()
	{
		return video;
	}
	
	// Set the parent view
	
	public void setParent(RelativeLayout l)
	{
		parent = l;
	}
	
	// Set the video to be looped
	
	public void loop(boolean l)
	{
		if(player != null) player.setLooping(l);
	}
	
	// Resume a videi
	
	public void resume()
	{
		v2.resume();
	}
	
	// Pause a video
	
	public void pause()
	{
		v2.pause();
	}
	
	// Unmute a vudeo
	
	public void unmute()
	{
		if(player!= null)player.setVolume(100f,100f);
	}
	
	//Mute a vudeo
	
	public void mute()
	{
		if(player != null) player.setVolume(0f,0f);
	}
	
	// set to preview a file
		// Check if tge file if video or image and preview the file
	
	public boolean preview(File f)
	{
		String m = Utils.getMimeType(f.getAbsolutePath());
		if(m == null) return false;
		
		if(m.startsWith("image/"))
		{
			v1.setImageBitmap(Utils.getBitmap(con,f));
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.GONE);
			return true;
		}else if(m.startsWith("video/"))
		{
			if(!preview_video){
				v1.setImageBitmap(Manager.thumbnails.get(f));
				v1.setVisibility(View.VISIBLE);
				v2.setVisibility(View.GONE);
			}else{
				video = true;
				v2.setVideoPath(f.getAbsolutePath());
				v2.setVisibility(View.VISIBLE);
				v1.setVisibility(View.GONE);
				//v2.setAudioAttributes(new AudioAttributes());
				v2.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
				{
					@Override public void onCompletion(MediaPlayer p)
					{
						v2.start();
					}
				});
				v2.start();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}
