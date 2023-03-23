package com.avc.manager;
import android.support.v7.app.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import android.media.*;
import android.view.View.*;
import android.view.*;
import android.content.pm.*;

public class MediaPlayerActivity extends AppCompatActivity
{
	private VideoView vid;
	private ImageView img;
	LinearLayout img_cont,vid_cont;
	public static File file;
	int seek = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_viewer);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// Set to full screen
        
		vid = findViewById(R.id.media_viewerVideoView);
		img = findViewById(R.id.media_viewerImageView);
		img_cont = findViewById(R.id.media_viewerLinearLayout2);
		vid_cont = findViewById(R.id.media_viewerLinearLayout1);
		
		if(file != null)
		{
			if(Utils.getMimeType(file.getAbsolutePath()).startsWith("video")){
				vid.setVideoPath(file.getAbsolutePath());
				img_cont.setVisibility(View.GONE);
			}
			else{
				vid_cont.setVisibility(View.GONE);
				img.setImageDrawable(Utils.getDrawable(this,file));
			}
		}
		final MediaController con = new MediaController(this);
		
		vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override public void onPrepared(MediaPlayer p)
			{
				p.setLooping(true);
				float vidW = p.getVideoWidth() / (float) Me.density;
				float vidH = p.getVideoHeight() / (float) Me.density;
				
				if(vidW > vidH)
				{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					//vid.setRotation(180);
				}
			}
		});
		vid.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v)
			{
				con.show(1300);
			}
		});
		vid.setMediaController(con);
		
		con.hide();
		vid.start();
		
	}

	@Override
	protected void onResume()
	{
		if(vid != null && file != null)
		{
			vid.setVideoPath(file.getAbsolutePath());
			vid.seekTo(seek);
			vid.start();
		}
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if(vid != null){
			vid.pause();
			seek = vid.getCurrentPosition();
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		vid.pause();
		super.onDestroy();
	}
	
}
