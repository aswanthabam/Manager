package com.avc.manager;
import android.support.v7.app.*;
import android.os.*;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.*;
import com.avc.manager.Adapter.*;
import java.io.*;
import android.view.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.View.*;
import java.util.*;

public class DownloadedStatusActivity extends AppCompatActivity implements StatusRVAdapter.OnBind,StatusRVAdapter.OnDialog
{
	private AppCompatActivity activiy;
	private RecyclerView rcView;
	private StatusRVAdapter adapter;
	private GROUPFiles files;
	private LinearLayout pr;
	private Toolbar toolbar;
	public Map<VideoView,Integer> vViews = new ArrayMap<VideoView,Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloaded_status);
		
		rcView = findViewById(R.id.downloaded_statusRecyclerView);
		pr = findViewById(R.id.downloaded_statusProgressBar);
		toolbar = findViewById(R.id.mainToolbar);
		
		// Set toolbar

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle("Downloads");
		
		// Set top menu icon size
		
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,Me.dr_size,Me.dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		pr.setVisibility(View.VISIBLE);
		activiy = this;
		files = new GROUPFiles(); // Group files
		files.recreate();
		// Set up recycler view
		
		GridLayoutManager lManager = new GridLayoutManager(this,3);
		rcView.setLayoutManager(lManager);
		adapter = new StatusRVAdapter(activiy,files);
		adapter.setOnDialogListener(this);
		adapter.setOnBindListener(this);
		rcView.setAdapter(null);
		
		// Files listener
		files.setListener(new GROUPFiles.Listener()
		{
			@Override public void onAdded(GROUPFiles f){
				
			}
			@Override public void onRemove(GROUPFiles f){
				
			}
			@Override public void onPause(final GROUPFiles f){
				runOnUiThread(new Runnable(){
					@Override public void run()
					{
						// When the all status are git set the adapter and hide prograess bar too
						rcView.setAdapter(adapter);
						//Utils.toast(activiy,String.valueOf(f.files.size()));
						pr.setVisibility(View.GONE);
						rcView.setVisibility(View.VISIBLE);
					}
				});
			}
		});
		
		// Tgreaf to search for all downloaded status and ahow
		
		new Thread(new Runnable()
		{
			@Override public void run()
			{
				try
				{
					files.setName("Downloaded Statuses");
					File f = new File(Environment.getExternalStorageDirectory().toString()+"/AVC Manager/Statuses");
					files.setParent(f.getAbsolutePath());
					if(!f.exists() || !f.isDirectory())
					{
						f.mkdirs();
					}
					for(File fi : f.listFiles()){
						files.add(fi);
						
					}
					files.pause();
				}
				catch(Exception e)
				{
					Utils.toast(activiy,e.toString());
				}
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
	public void onDialog(final ImageVideoPreviewDialog di)
	{
		di.download.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v)
			{
				di.want_save = false;
				if(di.file != null && di.file.isFile() && di.file.exists()){
					di.file.delete();
					Utils.toast(activiy,"Deleted");
				}
			}
		});
		di.download.setImageResource(R.drawable.trash);
		if(di.isVideo())
			vViews.put(di.v1,0); // Add the videview to the cyrrently playing video list. to resume when the app is paused
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
