package com.avc.manager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import com.avc.manager.Adapter.*;
import com.avc.manager.Res.*;
import android.widget.*;
import android.view.*;
import androidx.coordinatorlayout.widget.*;
import android.os.*;
import android.view.animation.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import java.util.*;
import com.avc.manager.Adapter.StatusRVAdapter.*;

public class StatusSaverActivity extends AppCompatActivity implements StatusRVAdapter.OnBind , StatusRVAdapter.OnDialog
{
	public RecyclerView rcView;
	public StatusRVAdapter adapter;
	public AppCompatActivity activity;
	public WhatsApp whatsapp;
	public GROUPFiles f;
	public CoordinatorLayout main;
	public ImageView topImg;
	public Toolbar toolbar;
	public int anim_o = 0,SystemWidth,SystemHeight;
	public TextView TotalTxt;
	public LinearLayout bar;
	public Map<VideoView,Integer> vViews = new ArrayMap<VideoView,Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
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
		
		// Set toolbar
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle(Html.fromHtml("<small>Status Downloader</small>"));
		
		activity = this;
		
		// Get display width and set the width to linear layout for same width and height
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);
		// Store the width and height in pixels for future use
		SystemWidth = outMetrics.widthPixels;
		SystemHeight = outMetrics.heightPixels;
		int density  = (int) getResources().getDisplayMetrics().density;
		int dr_size = (int) getResources().getDimension(R.dimen.topbar_drawer_icon_size)/density;
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,dr_size,dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		// Whatsapp object
		
		whatsapp = new WhatsApp(this);
		
		// Top slideshow image animations

		final Animation anim = AnimationUtils.loadAnimation(this,R.anim.fading_animation);

		anim.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationStart(Animation a){}
			@Override public void onAnimationEnd(Animation a)
			{
				if(anim_o > adapter.thumbnails.size() -1) anim_o = 0;
				topImg.startAnimation(anim);
				topImg.setImageBitmap(adapter.getThumbnail(anim_o));
				anim_o++;
			}
			@Override public void onAnimationRepeat(Animation a){}
		});
			
		// Setting recyclerview 

		GridLayoutManager lManager = new GridLayoutManager(this,3);
		rcView.setLayoutManager((RecyclerView.LayoutManager) lManager);
		// Group status files and show them.while the completed message is got
		f = new GROUPFiles();
		f.setListener(new GROUPFiles.Listener()
		{
			@Override public void onRemove(GROUPFiles ff){}
			@Override public void onAdded(GROUPFiles ff){}
			@Override public void onPause(GROUPFiles ff)
			{
				new Thread(new Runnable()
				{
					@Override public void run()
					{
						adapter.thumbnails = adapter.getThumbnails();
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
				
				new Thread(new Runnable()
				{
					@Override public void run()
					{
						while(true)
						{
							if(adapter != null)
							{
								if(adapter.thumbnails.size() < 1) continue;
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
		});
		// Check for status files for smooth running it is done inside thread
		new Thread(new Runnable(){
			@Override public void run()
			{
				adapter = new StatusRVAdapter(activity,f);
				adapter.setOnBindListener(StatusSaverActivity.this);
				adapter.setOnDialogListener(StatusSaverActivity.this);
				adapter.parent = main;
				whatsapp.getStatuses(f);
			}
		}).start();
		
	}

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

	@Override
	public void onDialog(ImageVideoPreviewDialog dialog)
	{
		if(dialog.isVideo())
			vViews.put(dialog.v1,0);
	}
	
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
				vViews.replace(v,v.getCurrentPosition());
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
				v.start();
			}
		}
		super.onResume();
	}
	
}
