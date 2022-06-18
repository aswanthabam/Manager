package com.avc.manager;


import android.os.Bundle;
import android.view.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.widget.*;
import android.view.animation.*;
import android.view.animation.AnimationUtils;
import android.util.*;
import android.content.*;
import android.content.pm.*;
import android.*;
import com.avc.manager.Res.*;
import java.net.*;
import org.json.*;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.design.widget.*;
import android.support.v4.content.*;
import android.support.graphics.drawable.*;
import java.io.*;
import android.os.*;
import android.view.View.*;
import android.widget.GridLayout.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,PubConnect.OnUpdateResponceListener,
SpaceCleaner.OnFileChangeListener,SpaceCleaner.OnScanListener,SpaceCleaner.OnHomeMessageListener,
SpaceCleaner.OnUnCountedFileChangeListener
{
    private Toolbar toolbar;
    private DrawerLayout d;
	private NavigationView n;
	public TextView infoTxt;
	public Animation floatUp;
	public int info_i = 0;
	//public AppCompatActivity activity;
	public Me me;
	public LinearLayout GridContainer, HomeTopBox,Item1,Item2,Item3,Item4;
	private ImageView settings_icon;
    
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Manager._main_activity = this;
		
		// Initialize objects related to app and perform an update check
		
		me = new Me();
		PubConnect conn = new PubConnect(this,Me.app_id,Me.app_pass,Me.version);
		conn.setOnUpdateResponceListener(this);
		conn.checkForUpdates();
		
		// Views initialization
		
        toolbar = findViewById(R.id.mainToolbar);
        d = findViewById(R.id.mainDrawerLayout);
		n = findViewById(R.id.nav_view);
		
		Item1 =findViewById(R.id.activity_mainGridItem1); // Space cleaner
		Item2 =findViewById(R.id.activity_mainGridItem2);
		Item3 =findViewById(R.id.activity_mainGridItem3);
		Item4 =findViewById(R.id.activity_mainGridItem4);
		settings_icon = findViewById(R.id.activity_mainSettingsIcon);
		
		// Set click listeners
		
		settings_icon.setOnClickListener(new OnClickListener()
		{
			@Override public void onClick(View v)
			{
				Intent i = new Intent(Manager._main_activity,SettingsActivity.class);
				startActivity(i);
			}
		});
		ViewGroup.LayoutParams par1 = settings_icon.getLayoutParams();
		par1.width = Me.dr_size; par1.height = Me.dr_size;
		settings_icon.setLayoutParams(par1);
		
		// For grid items
		Item1.setOnClickListener(this);
		Item2.setOnClickListener(this);
		Item3.setOnClickListener(this);
		Item4.setOnClickListener(this);
		
		// set full screen and margins
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// Set to full screen
        
		
		toolbar.setPadding(0,Me.titleBarHeight+30,0,0);
		
		HomeTopBox = findViewById(R.id.activity_mainBubbleBg);
		GridContainer = findViewById(R.id.activity_mainGridItemsContainer);
        
		// Setting drawer and toolbar
		//androidx.core.util.Preconditions.checkArgumentInRange();
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        
		// Setup drawerlayout
		ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, d, toolbar, R.string.app_name, R.string.app_name);
		d.setDrawerListener(t);
		t.syncState();
		n.setNavigationItemSelectedListener(this);
		
		// Set home navigation icon and size
		
		Drawable dr = getResources().getDrawable(R.drawable.menus);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable di = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Me.dr_size, Me.dr_size, true));
		di.setTint(getResources().getColor(R.color.colorAccent));
		getSupportActionBar().setHomeAsUpIndicator(di); // Change drawer icon
		
		// Floating text initialization
		
		infoTxt = findViewById(R.id.activity_mainInfoTxt);
		floatUp = AnimationUtils.loadAnimation(this,R.anim.text_float_up);
		//floatUp.setRepeatCount();
		//floatUp.setRepeatMode(Animation.INFINITE);
		
		floatUp.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationEnd(Animation n)
			{
				new Thread(new Runnable(){
					@Override public void run()
					{
						Manager._main_activity.runOnUiThread(new Runnable()
						{
							@Override public void run()
							{
								infoTxt.setText("");
								infoTxt.startAnimation(floatUp);
							}
						});
					}
				}).start();		
			}
			@Override public void onAnimationRepeat(Animation n)
			{
				//floatUp.startNow();
			}
			@Override public void onAnimationStart(Animation n)
			{
				if(Manager.show_home.size() > 0)
				{
					infoTxt.setText(Manager.show_home.get(info_i));
					info_i++;
					if(info_i >= Manager.show_home.size())
					{
						info_i = 0;
					}
				}else info_i = 0;
			}
		});
		info_i = 0;
		infoTxt.startAnimation(floatUp);
		//floatUp.startNow();
		
		ViewGroup.LayoutParams par = GridContainer.getLayoutParams();
		par.height = Me.SystemWidth;
		GridContainer.setLayoutParams(par);
		
		// Set home top box size to 50% of screen size
		
		par = HomeTopBox.getLayoutParams();

		par.height = (Me.SystemHeight/2);
		
		HomeTopBox.setLayoutParams(par);
		
		
		Manager.status_saver = new StatusSaver(this);
		Manager.status_saver.setOnStatusScanListener(onstatusscan);
		Manager.status_saver.setOnStatusChangeListener(onstatuschange);
		if(Manager.loaded_status_saver) Manager.status_saver.getStatuses(this);
		else Utils.toast(this,"Unable to load status saver");
		
		Manager.space_cleaner = new SpaceCleaner(this);
		Manager.space_cleaner.setOnFileChangeListener(this);
		Manager.space_cleaner.setOnScanListener(this);
		Manager.space_cleaner.setOnHomeMessageListener(this);
		if(Manager.loaded_space_cleaner) Manager.space_cleaner.storageScan();
		else Utils.toast(this,"Unable to load space cleaner");
		
	}
	
	public static void itemSelection(int id,AppCompatActivity a)
	{
		Intent i;
		switch(id)
		{
			case R.id.activity_mainGridItem1:
			case R.id.nav_item1:
				// Main grid item 1 (Space Cleaner)
				i = new Intent(a,SpaceCleanerActivity.class);
				a.startActivity(i);

				break;
			case R.id.activity_mainGridItem2:
			case R.id.nav_item2:
				//Main grid item 2 (Status saver)
				i = new Intent(a,StatusSaverActivity.class);
				a.startActivity(i);
				break;
			case R.id.activity_mainGridItem3:
				// Main grid item 3
				break;
			case R.id.activity_mainGridItem4:
			case R.id.nav_item3:
				// Main grid item 4
				i = new Intent(a,SettingsActivity.class);
				a.startActivity(i);
				break;
		}
	}
	
	// Drawer Navigation item click listener
	
	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		itemSelection(p1.getItemId(),this);
		return true;
	}
	
	// Onclick listener (All in one using switch and id)
	
	@Override
	public void onClick(View p1)
	{
		itemSelection(p1.getId(),this);
	}
	
	// PubConnect : Calls when the responce of check for update is got
	@Override
	public void onUpdateResponce(JSONObject obj, PubConnect.UpdateConnect up,boolean is_connected)
	{
		if(is_connected)
		try{
			// The api gives the data in json 
			// Status will be "ok" if no error
			String status = obj.getString("status");
			if(status.equals("ok")){
				// Message contains all details of responce
				JSONObject message = obj.getJSONObject("message");
				if(message.getBoolean("available_update"))
				{
					// Etc. Etc. more about the api responce can be found in server code
					Manager.updateApp(message,up);
				}
			}else
			{

			}
		}catch(Exception e)
		{
			Log.e("UPDATE_CHECK","Couldnt connect to server");
		}
	}
	

	@Override
	protected void onResume()
	{
		// Changes to this activity listener when backed to this activity
		if(Manager.space_cleaner != null){
			Manager.space_cleaner.setOnFileChangeListener(this);
			Manager.space_cleaner.setOnScanListener(this);
			Manager.space_cleaner.setOnHomeMessageListener(this);
		}
		if(Manager.status_saver != null)
		{
			Manager.status_saver.setOnStatusChangeListener(onstatuschange);
			Manager.status_saver.setOnStatusScanListener(onstatusscan);
		}
		super.onResume();
	}
	/*
	 ****************************************
	 Status Saver Listeners of this activity
	 becuase space cleaner also use this method we want to set variables to hold those interface
	 ***************************************
	*/
	
	public StatusSaver.OnStatusScanListener onstatusscan = new StatusSaver.OnStatusScanListener()
	{

		@Override
		public void onStart(GROUPFiles all)
		{
			
		}

		@Override
		public void onEnd(GROUPFiles all)
		{
			Manager.show_home.add(Manager.status_saver_show_in_home_text.replaceAll("&size",String.valueOf(StatusSaver.files.files.size())));
			info_i = 0;
			infoTxt.setText("");
			infoTxt.startAnimation(floatUp);
		}
		
	};
	
	public StatusSaver.OnStatusChangeListener onstatuschange = new StatusSaver.OnStatusChangeListener()
	{

		@Override
		public void onAdd(GROUPFiles all)
		{
			
		}

		@Override
		public void onRemove(GROUPFiles all)
		{
			
		}

	};
	/*
	 ****************************************
	 Space Cleaner Listeners of this activity
	 ***************************************
	*/
	
	@Override
	public void onStart(GROUPFiles files)
	{
		// Storage scan started
	}

	@Override
	public void onEnd(GROUPFiles files)
	{
		// Stirage scan ended
		info_i = 0;
		infoTxt.setText("");
		Manager.show_home.add(Manager.space_cleaner_show_in_home_text.replaceAll("&size",SpaceCleaner.files.sizeSTR));
		infoTxt.startAnimation(floatUp);
	}
	
	@Override
	public void onUnCountedAdd(GROUPFiles files)
	{
		// Uncounted or effectable file is added
	}

	@Override
	public void onUnCountedRemove(GROUPFiles files)
	{
		// Uncounted or effectable file is Removed
	}
	
	@Override
	public void onAdd(GROUPFiles files)
	{
		// New fike added to space cleane
	}

	@Override
	public void onRemove(GROUPFiles files)
	{
		// File remoced
	}
	
	@Override
	public void onMessageChange(GROUPFiles f)
	{
		// The message list want to be shown in hime screen floating text is changed by spaceCleaber object
		
		//Utils.toast(this,String.valueOf(Manager.show_home.size()));
	}
	
}
