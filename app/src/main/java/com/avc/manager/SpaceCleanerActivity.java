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
import java.io.*;
import android.content.*;
import java.util.*;

public class SpaceCleanerActivity extends AppCompatActivity implements SpaceCleaner.OnFileChangeListener,SpaceCleaner.OnScanListener,
SpaceCleaner.OnUnCountedFileChangeListener
{
    private Toolbar toolbar;
	public int SystemWidth,SystemHeight;
	public AppCompatActivity activity;
	public TextView sizeTxt,SizeInfoText;
	public Button clear;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.space_cleaner);
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
		
		SpaceCleaner.files = new GROUPFiles();
		
		sizeTxt = findViewById(R.id.space_cleanerSizeText);
		sizeTxt.setText("");
		SizeInfoText = findViewById(R.id.space_cleanerCurrentName);
		clear = findViewById(R.id.space_cleanerClearButton);
		
		activity = this;
		
        setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,Me.dr_size,Me.dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		// Start stotage clranup scan
		Manager.space_cleaner.setOnFileChangeListener(this);
		Manager.space_cleaner.setOnScanListener(this);
		Manager.space_cleaner.storageScan();
		
		// click listenr for about size indicator below tge main size textvuew
		
		SizeInfoText.setOnClickListener(new OnClickListener()
		{
			@Override public void onClick(View v)
			{
				//for(int i = 0;i < files.all.size();i++) Utils.toast(activity,files.all.get(i).parent);
				Intent i = new Intent(activity,FileViewerActivity.class);
				startActivity(i);
			}
		});
		
		// Click listener for cleae now button
		
		clear.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				if(SpaceCleaner.files.files.size() > 0){
					for(File f : SpaceCleaner.files.files){
						f.delete();
					}
					sizeTxt.setText("All Cleared");
				}
			}
		});
		//new CacheCleaner(this);
		clear.setVisibility(View.GONE); // Hide button till.the stkrage scan is comoleted
    }
	
	// Option item selecteions
	
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
	public void onBackPressed()
	{
		// TODO: Implement this method
		super.onBackPressed();
	}
	
	/*
	****************************************
	Space Cleaner Listeners of this activity
	***************************************
	*/
	
	@Override
	public void onStart(GROUPFiles files)
	{
		// Storage scan started
		clear.setVisibility(View.GONE);
	}
	
	@Override
	public void onEnd(GROUPFiles files)
	{
		// Stirage scan ended
		sizeTxt.setText(files.sizeSTR);
		SizeInfoText.setText("View All");
		if(files.files.size() > 0)clear.setVisibility(View.VISIBLE); // Show clear now button
		else{
			sizeTxt.setText("All Cleared");
			SizeInfoText.setText("");
			clear.setVisibility(View.GONE);
		}
	}

	@Override
	public void onUnCountedAdd(GROUPFiles files)
	{
		// Uncounted or effectable file is added
		Utils.toast(this,files.name);
	}

	@Override
	public void onUnCountedRemove(GROUPFiles files)
	{
		// Uncounted or effectable file is Removed
	}
	
	@Override
	public void onAdd(GROUPFiles files)
	{
		// New fike added to space cleaner
		sizeTxt.setText(files.sizeSTR);
		SizeInfoText.setText(files.current);
	}

	@Override
	public void onRemove(GROUPFiles files)
	{
		// File removed
		sizeTxt.setText(files.sizeSTR);
		SizeInfoText.setText("");
	}
	
}
