package com.avc.manager;

import android.support.v7.app.*;
import java.util.*;
import java.io.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import android.support.v7.widget.Toolbar;
import android.graphics.drawable.*;
import android.graphics.*;
import android.content.*;
import android.support.v7.widget.*;
import com.avc.manager.Adapter.*;
import com.avc.manager.Res.*;

public class FileViewerActivity extends AppCompatActivity
{
	private GROUPFiles Gfiles;
	private List<File> files;
	private Toolbar toolbar;
	public int SystemWidth,SystemHeight;
	private AppCompatActivity activity;
	private RecyclerView rcView;
	private FileRVAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(Manager.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_viewer);
		
		// Get tge groupfile intent extra and save tge objects and files
		Gfiles = SpaceCleaner.files;
		files = Gfiles.files;
		// View initialization
		
		toolbar = findViewById(R.id.mainToolbar);
		rcView = findViewById(R.id.file_viewerRecyclerView);
		
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        
		// Set home navigation icon and size

		Drawable dr = getResources().getDrawable(R.drawable.btn_back);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable di = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Me.dr_size, Me.dr_size, true));
		di.setTint(getResources().getColor(Utils.getAttr(this,R.attr.colorAccent)));
		getSupportActionBar().setHomeAsUpIndicator(di); // Change drawer icon
		
		// Setting RecyclerView adapter
		
		adapter = new FileRVAdapter(this,Gfiles.all);
		rcView.setLayoutManager(new LinearLayoutManager(this));
		rcView.setAdapter(adapter);
		
		//Utils.toast(this,Gfiles.all.toString());
	}

	@Override
	public void onBackPressed()
	{
		rcView.setAdapter(null);
		super.onBackPressed();
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
