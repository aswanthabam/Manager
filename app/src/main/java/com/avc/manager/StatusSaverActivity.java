package com.avc.manager;
import android.os.Bundle;
import com.avc.manager.Adapter.*;
import com.avc.manager.Res.*;
import android.widget.*;
import android.view.*;
import android.os.*;
import android.view.animation.*;
import android.view.animation.AnimationUtils;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import java.util.*;
import com.avc.manager.Adapter.StatusRVAdapter.*;
import android.support.v7.widget.*;
import android.support.v7.app.*;
import android.support.design.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.View.*;
import android.content.*;

public class StatusSaverActivity extends AppCompatActivity implements StatusRVAdapter.OnBind , StatusRVAdapter.OnDialog,
StatusSaver.OnStatusScanListener,StatusSaver.OnStatusChangeListener
{
	public RecyclerView rcView;
	public StatusRVAdapter adapter;
	public AppCompatActivity activity;
	public CoordinatorLayout main;
	public ImageView topImg,downloads_icon;
	public Toolbar toolbar;
	public int anim_o = 0;
	public TextView TotalTxt;
	public LinearLayout bar;
	public Map<VideoView,Integer> vViews = new ArrayMap<VideoView,Integer>();
	Animation anim;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(Manager.getTheme(this));
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.status_saver);
		
		// Views initialization

		main = findViewById(R.id.status_saverCoordinatorLayout);
		bar = findViewById(R.id.status_saverProgressBar);
		bar.setVisibility(View.VISIBLE);
		rcView = findViewById(R.id.status_saverRecyclerView);
		toolbar = findViewById(R.id.mainToolbar);
		TotalTxt = findViewById(R.id.status_saverTextViewTotal);
		topImg = findViewById(R.id.status_saver_preview_layoutImageView);
		downloads_icon = findViewById(R.id.status_saverDownloads);
		
		// Set toolbar
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle(Html.fromHtml("<small>Status Downloader</small>"));
		
		activity = this;
		
		// Set top menu icon size
		
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,Me.dr_size,Me.dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		// Top slideshow image animations

		anim = AnimationUtils.loadAnimation(this,R.anim.fading_animation);

		anim.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationStart(Animation a){}
			@Override public void onAnimationEnd(Animation a)
			{
				if(anim_o > Manager.thumbnails.size() -1) anim_o = 0;
				topImg.startAnimation(anim);
				topImg.setImageBitmap(adapter.getThumbnail(anim_o));
				anim_o++;
			}
			@Override public void onAnimationRepeat(Animation a){}
		});
		
		// Setting recyclerview 

		GridLayoutManager lManager = new GridLayoutManager(this,3);
		rcView.setLayoutManager((RecyclerView.LayoutManager) lManager);
		
		Manager.status_saver.setOnStatusChangeListener(this);
		Manager.status_saver.setOnStatusScanListener(this);
		Manager.status_saver.getStatuses(this);
		
		// Top view downloaded status  clock listenr
		
		downloads_icon.setOnClickListener(new OnClickListener()
		{
			@Override public void onClick(View v){
				Intent i = new Intent(activity,DownloadedStatusActivity.class);
				
				//rcView.setAdapter(null);
				startActivity(i);
			}
		});
	}
	
	
	/*
	 ************
	 Status saver listeners
	 *************
	*/
	
	@Override
	public void onStart(GROUPFiles f)
	{
		adapter = new StatusRVAdapter(activity,f);
		adapter.setOnBindListener(StatusSaverActivity.this);
		adapter.setOnDialogListener(StatusSaverActivity.this);
		adapter.parent = main;
	}

	@Override
	public void onEnd(GROUPFiles f)
	{
		f.sortFiles(GROUPFiles.SORT_BY_DATE_DESCENDING);
		new Thread(new Runnable()
			{
				@Override public void run()
				{
					Manager.thumbnails = adapter.getThumbnails();
				}
			}).start();

		runOnUiThread(new Runnable()
			{
				@Override public void run()
				{
					rcView.setAdapter(adapter);
					bar.setVisibility(View.GONE);
					rcView.setVisibility(View.VISIBLE);		
				}
			});
		// Starts the showing of top image slideshow
		new Thread(new Runnable()
			{
				@Override public void run()
				{
					while(true)
					{
						if(adapter != null)
						{
							if(Manager.thumbnails.size() < 1) continue;
							runOnUiThread(new Runnable(){
									@Override public void run()
									{
										TotalTxt.setText(adapter.files.files.size()+" Statuses Available");
										topImg.setImageBitmap(adapter.getThumbnail(anim_o));
										topImg.startAnimation(anim);
										anim_o++;
									}
								});
							break;
						}
					}
				}
			}).start();
	}
	
	@Override
	public void onAdd(GROUPFiles f)
	{
		
	}
	
	@Override
	public void onRemove(GROUPFiles f)
	{
		
	}
	
	// Options item selectiom
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
	// Status RV Adapter : calls when the dialog is shown
	@Override
	public void onDialog(ImageVideoPreviewDialog dialog)
	{
		if(dialog.isVideo())
			vViews.put(dialog.v1,0); // Add the videview to the cyrrently playing video list. to resume when the app is paused
	}
	// Calls when a view is binded
	@Override
	public void onBind(StatusRVAdapter.ViewHolder holder)
	{
		if(holder.img.isVideo())
		{
			vViews.put(holder.v2,0); 
		}
	}
	
	@Override
	protected void onPause()
	{
		Set<VideoView> j = vViews.keySet();
		Iterator<VideoView> i = j.iterator();
		while(i.hasNext())
		{
			VideoView v = i.next();
			if(v.isPlaying())
			{
				v.pause();
				vViews.replace(v,v.getCurrentPosition());// for Resuming the videos in the previous seek position
			}
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		Set<VideoView> j = vViews.keySet();
		Iterator<VideoView> i = j.iterator();
		while(i.hasNext())
		{
			VideoView v = i.next();
			if(!v.isPlaying())
			{
				//v.setVideoPath(vViews.get(v));
				v.seekTo(vViews.get(v));
				v.start();// Resume the videk
			}
		}
		super.onResume();
	}
	
}
