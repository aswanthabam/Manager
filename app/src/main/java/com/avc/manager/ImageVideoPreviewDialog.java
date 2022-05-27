package com.avc.manager;

import java.io.*;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import java.net.*;
import android.net.*;
import android.media.*;
import android.support.v7.app.*;

public class ImageVideoPreviewDialog extends Dialog
{
	private AppCompatActivity activity;
	public File file;
	public VideoView v1;
	public ImageView v2,Cancel,download,share;
	public LinearLayout main;
	
	private boolean video = false;
	
	public ImageVideoPreviewDialog(AppCompatActivity a,File f)
	{
		super(a);
		activity = a; file = f;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_video_prev_dialog);
		getWindow().setBackgroundDrawableResource(R.drawable.light_border_background);
		
		v1 = findViewById(R.id.image_video_prev_dialogVideoView);
		v2 = findViewById(R.id.image_video_prev_dialogImageView);
		Cancel = findViewById(R.id.image_video_prev_dialogCancel);
		main = findViewById(R.id.image_video_prev_dialogLinearLayoutMain);
		download = findViewById(R.id.image_video_prev_dialogDownload);
		share = findViewById(R.id.image_video_prev_dialogShare);
		
		Cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				dismiss();
			}
		});
		
		download.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				Utils.saveToDevice(file,"Statuses");
				Utils.toast(activity,"File saved!");
			}
		});
		
		share.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				File f = Utils.saveToDevice(file,"Statuses");
				Utils.toast(activity,"File saved!");
				Utils.shareFile(activity,"AVC Manager","Hey,\nLook at this amazing content, "+
								"I saved this status using AVC Manager Application!. AVC Manager has more features like this including\n"+
								" • Status saver\n • Space cleaner\n • Device Optimization etc..\n\n Download the app now."
				,f.getAbsolutePath(),Utils.getMimeType(f.getAbsolutePath()));
			}
		});
		
		String mime = Utils.getMimeType(file.getAbsolutePath());
		v1.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override public void onPrepared(MediaPlayer p)
			{
				p.setLooping(true);
			}
		});
		if(mime.startsWith("video/"))
		{
			video = true;
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.GONE);
			v1.setVideoPath(file.getAbsolutePath());
			v1.start();
		}else if(mime.startsWith("image/"))
		{
			v1.setVisibility(View.GONE);
			v2.setVisibility(View.VISIBLE);
			v2.setImageURI(Uri.fromFile(file));
		}
	}
	
	public boolean isVideo()
	{
		return video;
	}
}