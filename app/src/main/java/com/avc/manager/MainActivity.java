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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, PubConnect.OnUpdateResponceListener,
SpaceCleaner.OnFileChangeListener,SpaceCleaner.OnScanListener,SpaceCleaner.OnHomeMessageListener
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
	//public int SystemWidth,SystemHeight;
	//private String[] info_txts = {"NMB of storage can be freed","Delete old whatsapp images","TELENMB Storage is used by telegram"};
    
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
		
		// Check for permissions
		
		String[] per = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE
		};
		requestPermissions(per,6909);
		
		// Views initialization
		
        toolbar = findViewById(R.id.mainToolbar);
        d = findViewById(R.id.mainDrawerLayout);
		n = findViewById(R.id.nav_view);
		
		Item1 =findViewById(R.id.activity_mainGridItem1); // Space cleaner
		Item2 =findViewById(R.id.activity_mainGridItem2);
		Item3 =findViewById(R.id.activity_mainGridItem3);
		Item4 =findViewById(R.id.activity_mainGridItem4);
		
		// Set click listeners
		// For grid items
		Item1.setOnClickListener(this);
		Item2.setOnClickListener(this);
		Item3.setOnClickListener(this);
		Item4.setOnClickListener(this);
		
		// set full screen and margins
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// Set to full screen
        
		Rect rectangle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		int statusBarHeight = rectangle.top;
		int contentViewTop = 
			window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		Me.titleBarHeight= contentViewTop - statusBarHeight;
		
		toolbar.setPadding(0,Me.titleBarHeight+30,0,0);
		
		// Get display width and set the width to linear layout for same width and height

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);

		// Store the width and height in pixels for future use

		Me.SystemWidth = outMetrics.widthPixels;
		Me.SystemHeight = outMetrics.heightPixels;
		
		Me.density  = (int) getResources().getDisplayMetrics().density;
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
		
		Me.dr_size = (int) getResources().getDimension(R.dimen.topbar_drawer_icon_size)/Me.density;
		Drawable dr = getResources().getDrawable(R.drawable.menus);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable di = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Me.dr_size, Me.dr_size, true));
		di.setTint(getResources().getColor(R.color.colorAccent));
		getSupportActionBar().setHomeAsUpIndicator(di); // Change drawer icon
		
		// Floating text initialization
		
		infoTxt = findViewById(R.id.activity_mainInfoTxt);
		floatUp = AnimationUtils.loadAnimation(this,R.anim.text_float_up);
		floatUp.setRepeatCount(0);
		/*floatUp.setRepeatMode(Animation.REVERSE);
		*/
		floatUp.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationEnd(Animation n)
			{
				infoTxt.setText("");
				infoTxt.startAnimation(floatUp);
			}
			@Override public void onAnimationRepeat(Animation n)
			{
				
			}
			@Override public void onAnimationStart(Animation n)
			{
				if(info_i >= Manager.show_home.size()) info_i = 0;
				info_i++;
				infoTxt.setText(Manager.show_home.get(info_i-1));
				//Utils.toast(activity,String.valueOf(info_i)+" : "+Manager.show_home.get(info_i));
			}
		});
		info_i = 0;
		infoTxt.startAnimation(floatUp);
		
		
		ViewGroup.LayoutParams par = GridContainer.getLayoutParams();
		par.height = Me.SystemWidth;
		GridContainer.setLayoutParams(par);
		
		// Set home top box size to 50% of screen size
		
		par = HomeTopBox.getLayoutParams();

		par.height = (Me.SystemHeight/2);
		
		HomeTopBox.setLayoutParams(par);
		
		
		Me.externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		Manager.space_cleaner = new SpaceCleaner(this);
		Manager.space_cleaner.setOnFileChangeListener(this);
		Manager.space_cleaner.setOnScanListener(this);
		Manager.space_cleaner.setOnHomeMessageListener(this);
		if(Manager.loadSpaceCleaner(this)) Manager.space_cleaner.storageScan();
		else Utils.toast(this,"Unable to load space cleaner");
		
		if(Manager.loadStatusSaver(this)) Manager.status_saver = new StatusSaver(this);
		else Utils.toast(this,"Unable to load status saver");
    }
	
	// Drawer Navigation item click listener
	
	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		
		return true;
	}
	
	// Onclick listener (All in one using switch and id)
	
	@Override
	public void onClick(View p1)
	{
		Intent i;
		switch(p1.getId())
		{
			case R.id.activity_mainGridItem1:
				// Main grid item 1 (Space Cleaner)
				i = new Intent(this,SpaceCleanerActivity.class);
				startActivity(i);
				
				break;
			case R.id.activity_mainGridItem2:
				//Main grid item 2 (Status saver)
				i = new Intent(this,StatusSaverActivity.class);
				startActivity(i);
				break;
			case R.id.activity_mainGridItem3:
				// Main grid item 3
				break;
			case R.id.activity_mainGridItem4:
				// Main grid item 4
				break;
		}
	}
	
	// Function to check if a permission is granted or not(Useless)
	
	public static boolean isPermission(AppCompatActivity a,String[] permission)
	{
		boolean flag = true;
		
		for(String i : permission)
		if(ContextCompat.checkSelfPermission(a,i) == PackageManager.PERMISSION_DENIED)
		{
			flag = false;
			break;
		}
		return flag;
	}
	
	// PubConnect : Calls when the responce of check for update is got
	
	@Override
	public void onUpdateResponce(HttpURLConnection conn, String out, JSONObject obj, PubConnect.UpdateConnect up)
	{
		
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
					JSONObject update = message.getJSONObject("update");
					JSONObject info = update.getJSONArray("info").getJSONObject(0);
					// Add all deatils to "Me" object (Me.Update)
					me.update.app_id = update.getString("app_id");
					me.update.app_name = update.getString("app_name");
					me.update.version = update.getString("version");
					me.update.version_id = update.getString("version_id");
					me.update.author = update.getString("author");
					me.update.description = update.getString("description");
					me.update.file_name = update.getString("file_name");
					me.update.pub_name = update.getString("pub_name");
					me.update.more_link = update.getString("more_link");
					me.update.uploaded_date = update.getString("uploaded_date");
					me.update.created_date = update.getString("created_date");
					me.update.common_app_id = update.getString("common_app_id");
					
					me.update.download_link = update.getString("download_link");
					me.update.download_link_is_redirect = update.getBoolean("download_link_is_redirect");
					me.update.download_link_is_icedrive = update.getBoolean("download_link_is_icedrive");
					
					me.update.title = info.getString("title");
					me.update.sub_title = info.getString("sub_title");
					me.update.update_description = info.getString("description");
					me.update.must_update = info.getBoolean("must_update");
					// Create pubconnect dialog with update notification
					PubConnect.UpdateConnect.UpdateDialog di = up.getDialog(me);
					di.show(); //show
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
		super.onResume();
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
	}

	@Override
	public void onEnd(GROUPFiles files)
	{
		// Stirage scan ended
		Manager.show_home.add(Manager.space_cleaner_show_in_home_text.replaceAll("&size",files.sizeSTR));
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
