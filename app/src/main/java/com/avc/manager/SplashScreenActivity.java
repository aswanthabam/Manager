package com.avc.manager;

import android.os.*;
import android.content.*;
import android.support.v7.app.*;
import android.app.*;
import android.*;
import android.graphics.*;
import android.view.*;
import android.util.*;
import java.io.*;
import android.widget.*;
import java.security.*;
import android.content.pm.*;
import android.view.View.*;
import android.preference.*;

/*
 Splash screen to show when the app is loading
*/
public class SplashScreenActivity extends AppCompatActivity 
implements Manager.OnDecompressFileListener
{
	public AppCompatActivity a;
	private TextView txt;
	private ProgressBar bar;
	String[] per = {
		Manifest.permission.WRITE_EXTERNAL_STORAGE,
		Manifest.permission.READ_EXTERNAL_STORAGE
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		a = this;
		final AppCompatActivity a = this;
		txt = findViewById(R.id.splashscreenTextView1);
		bar = findViewById(R.id.splashscreenProgressBar1);
		bar.setVisibility(View.GONE);
		permRun(per);
		
	}
	public void permRun(final String[] per)
	{
		Manager.permission_granted = true;
		Thread r = new Thread(new Runnable()
		{
			@Override public void run()
			{
				for(String p : per)
				{
					if(checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED)
					{
						Manager.permission_granted = false;
					}
				}
			}
		});
		r.start();
		try{r.join();}catch(Exception e){}
		if(Manager.permission_granted)
		{
			
			new Thread(new Runnable()
			{
				@Override public void run()
				{
					//try{Thread.sleep(3000);}catch(Exception e){}

					/* Requedt permissions */

					/* Set external and internal storage directory */

					Me.externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
					Me.internalStorage = getFilesDir();

					/* Set title bar height */

					Rect rectangle = new Rect();
					Window window = getWindow();
					window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
					int statusBarHeight = rectangle.top;
					int contentViewTop = 
						window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
					Me.titleBarHeight= contentViewTop - statusBarHeight;

					/* Set system width,height and density of screen */

					// Get display width and set the width to linear layout for same width and height
					Display display = getWindowManager().getDefaultDisplay();
					DisplayMetrics outMetrics = new DisplayMetrics ();
					display.getMetrics(outMetrics);
					// Store the width and height in pixels for future use
					Me.SystemWidth = outMetrics.widthPixels;
					Me.SystemHeight = outMetrics.heightPixels;
					Me.density  = (int) getResources().getDisplayMetrics().density;
					Me.dr_size = (int) getResources().getDimension(R.dimen.topbar_drawer_icon_size)/Me.density;	// Top menu icon(show home as up indicator) size

					/* Load json data files */
					Manager.setOnDecompressFileListener(SplashScreenActivity.this);
					
					Manager.loaded_about = Manager.loadAbout(a);
					if(Manager.loaded_about){
						Manager.loaded_space_cleaner = Manager.loadSpaceCleaner(a);
						Manager.loaded_status_saver = Manager.loadStatusSaver(a);
						SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(a);
						if(Manager.loaded_status_saver)pre.edit().putInt("status_saver_version",Manager.status_saver_version).commit();
						if(Manager.loaded_space_cleaner) pre.edit().putInt("space_cleaner_version",Manager.space_cleaner_version).commit();
						/* Start activity */
						
						Intent i = new Intent(a,MainActivity.class);
						startActivity(i);
						finish();
					}else{
						a.runOnUiThread(new Runnable()
						{
							@Override public void run()
							{
								txt.setText("Oops! Unable to start app");
							}
						});
						Utils.toast(a,"Unable to load");
					}
				}
			}).start();
		}
		else{
			txt.setText("Granting permission");
			final Dialog d = new Dialog(this);
			d.getWindow().setBackgroundDrawableResource(R.drawable.light_border_background);
			d.setContentView(R.layout.request_permission);
			Button con = d.findViewById(R.id.requestpermissionButton1);
			TextView t = d.findViewById(R.id.requestpermissionTextView1);
			
			con.setOnClickListener(new OnClickListener()
			{
				@Override public void onClick(View v)
				{
					d.dismiss();
					requestPermissions(per,6909);
				}
			});
			d.setOnDismissListener(new DialogInterface.OnDismissListener()
			{
				@Override public void onDismiss(DialogInterface i)
				{
					requestPermissions(per,6909);
				}
			});
			t.setText(R.string.request_permission);
			d.show();
		}
		
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(requestCode == 6909)
		{
			boolean flag = true;
			for(int i : grantResults)if(i != PackageManager.PERMISSION_GRANTED) flag = false;
			if(flag)
				permRun(per);
			else{
				Utils.toast(this,"Permission denied!");
				finish();
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	@Override
	public void onDecompress(InputStream str, File to)
	{
		runOnUiThread(new Runnable()
		{
			@Override public void run(){
				int i = 0;
				txt.setText("Initializing");
			}
		});
	}
	
}
