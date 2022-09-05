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
import android.support.v7.widget.*;
import android.widget.RadioGroup.*;
import android.app.*;
import android.support.graphics.drawable.*;

public class SpaceCleanerActivity extends AppCompatActivity implements SpaceCleaner.OnFileChangeListener,SpaceCleaner.OnScanListener,
SpaceCleaner.OnUnCountedFileChangeListener
{
    private Toolbar toolbar;
	public int SystemWidth,SystemHeight;
	public AppCompatActivity activity;
	public TextView sizeTxt,SizeInfoText;
	public Button clear;
	public RecyclerView rcView;
	public Adapter adapter;
	
	private LinearLayout cont;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		setTheme(Manager.getTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.space_cleaner);
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
		
		SpaceCleaner.files = new GROUPFiles();
		
		sizeTxt = findViewById(R.id.space_cleanerSizeText);
		sizeTxt.setText("");
		SizeInfoText = findViewById(R.id.space_cleanerCurrentName);
		clear = findViewById(R.id.space_cleanerClearButton);
		rcView = findViewById(R.id.space_cleanerRecyclerView);
		cont = findViewById(R.id.space_cleanerLineraLayoutContainer);
		
		activity = this;
		
        setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bitmap dr = ((BitmapDrawable) getResources().getDrawable(R.drawable.btn_back)).getBitmap();
		Drawable di = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(dr,Me.dr_size,Me.dr_size,true));
		getSupportActionBar().setHomeAsUpIndicator(di);
		
		adapter = new Adapter(this);
		rcView.setLayoutManager(new LinearLayoutManager(this));
		// Start stotage clranup scan
		Manager.space_cleaner.setOnFileChangeListener(this);
		Manager.space_cleaner.setOnScanListener(this);
		Manager.space_cleaner.setOnUncountedFileChangeListener(this);
		Manager.space_cleaner.storageScan();
		rcView.setAdapter(adapter);
		
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
				new Thread(new Runnable(){
					@Override public void run(){
						if(SpaceCleaner.files.files.size() > 0){
							for(final GROUPFiles f : SpaceCleaner.files.all){
								final Manager.Scanable sc = (Manager.Scanable) f.linkedObject;
								//Utils.toast(activity,String.valueOf(sc.ask_before_delete));
								if(sc.ask_before_delete)
								{
									activity.runOnUiThread(new Runnable()
										{
											@Override public void run()
											{
												
									final Dialog m = new Dialog(activity,R.style.DialogStyle);
									m.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
									m.getWindow().setGravity(Gravity.BOTTOM);
									//m.getWindow().setBackgroundDrawableResource(R.drawable.light_border_background);
									m.setContentView(R.layout.show_before_delete);
									TextView title,des,fs;
									Button can,con;
									title = m.findViewById(R.id.show_before_deleteTitle);
									des = m.findViewById(R.id.show_before_deleteDescription);
									fs = m.findViewById(R.id.show_before_deleteFiles);
									can = m.findViewById(R.id.show_before_deleteButton2);
									con = m.findViewById(R.id.show_before_deleteButton1);
									
									title.setText("Files will be deleted");
									des.setText(sc.description + "\n\n" +sc.ask_before_info + "\n\nFollowing files will be deleted. continue?");
									for(File fi:f.files)
									{
										fs.setText(fs.getText() + "\n • "+fi.getAbsolutePath());
									}
									can.setOnClickListener(new OnClickListener()
									{
										@Override public void onClick(View v)
										{
											m.dismiss();
										}
									});
									con.setOnClickListener(new OnClickListener()
									{
										@Override public void onClick(View v)
										{
											for(File fi : f.files)
											{
												fi.delete();
												
											}
											SpaceCleaner.un_counted_files.remove(f);
											SpaceCleaner.all_files.remove(f);
											rcView.setAdapter(adapter);
											m.dismiss();
											
										}
									});
									try{m.show();}catch(Exception e){}
									
										}
									});
									
								}
								else
								{
									for(File fi : f.files)
									{
										fi.delete();
									}
								}
							}
							
							SpaceCleaner.files.remove(SpaceCleaner.files.files);
							
							activity.runOnUiThread(new Runnable(){
								@Override public void run(){
									sizeTxt.setText("All Cleared");
								}
							});
						}
					}
					
				}).start();
			}
		});
		final AnimatedVectorDrawableCompat scan_anim = AnimatedVectorDrawableCompat.create(this,R.drawable.scan_animated);
		//final AnimatedVectorDrawableCompat scan_anim2 = AnimatedVectorDrawableCompat.create(this,R.drawable.scan_animated);

		cont.setBackgroundDrawable(scan_anim);

		final Animatable scan = (Animatable) cont.getBackground();

		scan_anim.registerAnimationCallback(new Animatable2Compat.AnimationCallback()
		{
			@Override public void onAnimationEnd(Drawable d)
			{
				if(!SpaceCleaner.all_files.is_paused)
					scan.start();
				else{
					cont.setBackgroundResource(R.drawable.scan);
					//((Animatable) scan_image_1.getDrawable()).start();
				}
			}
		});
		scan.start();
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
		if(SpaceCleaner.all_files.files.size() > 0)clear.setVisibility(View.VISIBLE); // Show clear now button
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
		rcView.setAdapter(adapter);
	}

	@Override
	public void onUnCountedRemove(GROUPFiles files)
	{
		// Uncounted or effectable file is Removed
		rcView.setAdapter(adapter);
	}
	
	@Override
	public void onAdd(GROUPFiles files)
	{
		// New fike added to space cleaner
		sizeTxt.setText(SpaceCleaner.files.sizeSTR);
		SizeInfoText.setText(files.current);
	}

	@Override
	public void onRemove(GROUPFiles files)
	{
		// File removed
		sizeTxt.setText(SpaceCleaner.files.sizeSTR);
		SizeInfoText.setText("");
	}
	
	public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
	{
		private AppCompatActivity activity;
		private GROUPFiles files;
		public Adapter(AppCompatActivity a)
		{
			activity = a;
			files = SpaceCleaner.un_counted_files;
		}
		@Override
		public void onBindViewHolder(final SpaceCleanerActivity.Adapter.ViewHolder h, int p2)
		{
			h.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton p1,boolean p)
				{
					if(p) SpaceCleaner.files.add(files.all.get(h.getAdapterPosition()));
					else SpaceCleaner.files.remove(files.all.get(h.getAdapterPosition()));
				}
			});
			h.txt.setText(files.all.get(h.getAdapterPosition()).name);
			h.size.setText(files.all.get(h.getAdapterPosition()).sizeSTR);
		}
		
		@Override
		public SpaceCleanerActivity.Adapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
		{
			View v = LayoutInflater.from(activity).inflate(R.layout.space_rc_view,p1,false);
			return new ViewHolder(v);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO: Implement this method
			return super.getItemId(position);
		}

		@Override
		public int getItemCount()
		{
			// TODO: Implement this method
			return files.all.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder
		{
			View v;
			CheckBox box;
			TextView txt,size;
			public ViewHolder(View vi)
			{
				super(vi);
				v = vi;
				box = v.findViewById(R.id.space_rc_viewCheckBox);
				txt = v.findViewById(R.id.space_rc_viewTextView);
				size = v.findViewById(R.id.space_rc_viewTextViewSize);
			}
		}
	}
	
}
