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

public class PreviewLayout
{
	public Context con;
	//public AttributeSet attrs;
	private ImageView v1;
	private VideoView v2;
	private MediaController controller;
	private MediaPlayer player;
	public RelativeLayout parent;
	private int SystemWidth,SystemHeight,density;
	//private AttributeSet set;
	public static final int CENTER_CROP = 10;
	public static final int AUTO_CROP = 11;
	public static final int MUTE = 10;
	public static final int UNMUTE = 11;
	
	public boolean video = false;
	
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
	
	public void init(Context m,ImageView vv,VideoView vvv,final int fill,final int mute,final boolean loop)
	{
		// Get display width and set the width to linear layout for same width and height
		Display display = ((AppCompatActivity)m).getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);
		// Store the width and height in pixels for future use
		SystemWidth = outMetrics.widthPixels;
		SystemHeight = outMetrics.heightPixels;
		density  = (int) ((AppCompatActivity)m). getResources().getDisplayMetrics().density;
		
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
	}
	
	public boolean isVideo()
	{
		return video;
	}
	
	public void setParent(RelativeLayout l)
	{
		parent = l;
	}
	
	public void loop(boolean l)
	{
		if(player != null) player.setLooping(l);
	}
	
	public void resume()
	{
		v2.resume();
	}
	
	public void pause()
	{
		v2.pause();
	}
	
	public void unmute()
	{
		if(player!= null)player.setVolume(100f,100f);
	}
	
	public void mute()
	{
		if(player != null) player.setVolume(0f,0f);
	}
	
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
			
			return true;
		}
		else
		{
			return false;
		}
	}
}
