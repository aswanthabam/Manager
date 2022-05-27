package com.avc.manager;

import android.os.Bundle;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.util.*;
import com.avc.manager.Res.*;
import android.widget.*;
import android.view.View.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
public class SpaceCleanerActivity extends AppCompatActivity 
{
    private Toolbar toolbar;
	public int SystemWidth,SystemHeight;
	public AppCompatActivity activity;
	public TextView sizeTxt,SizeInfoText;
	public GROUPFiles files = new GROUPFiles();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.space_cleaner);
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
		
		sizeTxt = findViewById(R.id.space_cleanerSizeText);
		sizeTxt.setText("");
		SizeInfoText = findViewById(R.id.space_cleanerCurrentName);
		
		activity = this;
		
		// Get display width and set the width to linear layout for same width and height

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);

		// Store the width and height in pixels for future use

		SystemWidth = outMetrics.widthPixels;
		SystemHeight = outMetrics.heightPixels;

		int density  = (int) getResources().getDisplayMetrics().density;
		
        setSupportActionBar(toolbar);
		
		int dr_size = (int) getResources().getDimension(R.dimen.topbar_drawer_icon_size)/density;
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,dr_size,dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		storageScan();
		
		SizeInfoText.setOnClickListener(new OnClickListener()
		{
			@Override public void onClick(View v)
			{
				for(int i = 0;i < files.all.size();i++) Utils.toast(activity,files.all.get(i).parent);
			}
		});
    }
	
	public void storageScan()
	{
		new Thread(new Runnable(){
			@Override public void run()
			{
				WhatsApp wh = new WhatsApp(activity);
				GROUPFiles g = new GROUPFiles();
				g.setListener(new GROUPFiles.Listener()
				{
					@Override public void onAdded(final GROUPFiles f)
					{
						runOnUiThread(new Runnable()
						{
							@Override public void run()
							{
								sizeTxt.setText(f.sizeSTR);
								SizeInfoText.setText(f.current);
							}
						});
						try{Thread.sleep(5);}catch(Exception e){}
					}
					@Override public void onRemove(final GROUPFiles f)
					{
						runOnUiThread(new Runnable()
						{
							@Override public void run()
							{
								sizeTxt.setText(f.sizeSTR);
								SizeInfoText.setText("");
							}
						});
						try{Thread.sleep(5);}catch(Exception e){}
					}
					@Override public void onPause(final GROUPFiles f)
					{
						runOnUiThread(new Runnable()
						{
							@Override public void run()
							{
								sizeTxt.setText(f.sizeSTR);
								SizeInfoText.setText("View All");
							}
						});
					}
				});
				wh.getWhatsAppShared(g);
				files.add(g);
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
	
	
}
