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
//import android.support.design.widget.AnimationUtils;
import android.support.design.widget.*;
import android.support.v4.content.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, PubConnect.OnUpdateResponceListener
{
    private Toolbar toolbar;
    private DrawerLayout d;
	private NavigationView n;
	public TextView infoTxt;
	public Animation floatUp;
	int info_i = 0;
	AppCompatActivity activity;
	Me me;
	/*GridLayout home_grid;
	LinearLayout GridItem1,GridItem2,GridItem3,GridItem4;
    */
	
	LinearLayout GridContainer, HomeTopBox,Item1,Item2,Item3,Item4;
	public int SystemWidth,SystemHeight;
	private String[] info_txts = {"NMB of storage can be freed","Delete old whatsapp images","TELENMB Storage is used by telegram"};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		activity = this;
		me = new Me();
		PubConnect conn = new PubConnect(this,Me.app_id,Me.app_pass,Me.version);
		conn.setOnUpdateResponceListener(this);
		conn.checkForUpdates();
		
		String[] per = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE
		};
		requestPermissions(per,6909);
		if(isPermission(this,per))
		{
			
		}
		
        toolbar = findViewById(R.id.mainToolbar);
        d = findViewById(R.id.mainDrawerLayout);
		n = findViewById(R.id.nav_view);
		
		Item1 =findViewById(R.id.activity_mainGridItem1); // Space cleaner
		Item2 =findViewById(R.id.activity_mainGridItem2);
		Item3 =findViewById(R.id.activity_mainGridItem3);
		Item4 =findViewById(R.id.activity_mainGridItem4);
		
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
		int titleBarHeight= contentViewTop - statusBarHeight;
		
		toolbar.setPadding(0,titleBarHeight+30,0,0);
		
		// Get display width and set the width to linear layout for same width and height

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);

		// Store the width and height in pixels for future use

		SystemWidth = outMetrics.widthPixels;
		SystemHeight = outMetrics.heightPixels;
		
		int density  = (int) getResources().getDisplayMetrics().density;
		HomeTopBox = findViewById(R.id.activity_mainBubbleBg);
		GridContainer = findViewById(R.id.activity_mainGridItemsContainer);
        
		// Setting drawer and toolbar
		//androidx.core.util.Preconditions.checkArgumentInRange();
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        
		ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, d, toolbar, R.string.app_name, R.string.app_name);
		d.setDrawerListener(t);
		t.syncState();
		n.setNavigationItemSelectedListener(this);
		
		int dr_size = (int) getResources().getDimension(R.dimen.topbar_drawer_icon_size)/density;
		Drawable dr = getResources().getDrawable(R.drawable.menus);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable di = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, dr_size, dr_size, true));
		di.setTint(getResources().getColor(R.color.colorAccent));
		getSupportActionBar().setHomeAsUpIndicator(di); // Change drawer icon
		
		// Floating text initialization
		
		infoTxt = findViewById(R.id.activity_mainInfoTxt);
		floatUp = AnimationUtils.loadAnimation(this,R.anim.text_float_up);
		floatUp.setRepeatCount(Animation.INFINITE);
		floatUp.setRepeatMode(Animation.REVERSE);
		
		floatUp.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationEnd(Animation n)
			{
				infoTxt.setText("");
				if(info_i == info_txts.length) info_i = 0;
				infoTxt.startAnimation(floatUp);
			}
			@Override public void onAnimationRepeat(Animation n)
			{
				infoTxt.setText(info_txts[info_i]);
				info_i++;
			}
			@Override public void onAnimationStart(Animation n)
			{
				if(info_i == info_txts.length) info_i = 0;
				infoTxt.setText(info_txts[info_i]);
				info_i++;
			}
		});
		
		infoTxt.startAnimation(floatUp);
		
		
		ViewGroup.LayoutParams par = GridContainer.getLayoutParams();
		par.height = SystemWidth;
		GridContainer.setLayoutParams(par);
		
		// Set home top box size to 50% of screen size
		
		par = HomeTopBox.getLayoutParams();

		par.height = (SystemHeight/2);
		
		HomeTopBox.setLayoutParams(par);
		
    }
	
	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		
		return true;
	}
	public static void toast(final AppCompatActivity a,final String txt)
	{
		a.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				Toast.makeText(a,txt,Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onClick(View p1)
	{
		Intent i;
		switch(p1.getId())
		{
			case R.id.activity_mainGridItem1:
				i = new Intent(this,SpaceCleanerActivity.class);
				
				startActivity(i);
				break;
			case R.id.activity_mainGridItem2:
				i = new Intent(this,StatusSaverActivity.class);
				
				startActivity(i);
				break;
			case R.id.activity_mainGridItem3:

				break;
			case R.id.activity_mainGridItem4:

				break;
		}
	}
	
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

	@Override
	public void onUpdateResponce(HttpURLConnection conn, String out, JSONObject obj, PubConnect.UpdateConnect up)
	{
		
		try{
			String status = obj.getString("status");
			if(status.equals("ok")){
				JSONObject message = obj.getJSONObject("message");
				Utils.toast(this,message.toString());
				if(message.getBoolean("available_update"))
				{
					JSONObject update = message.getJSONObject("update");
					JSONObject info = update.getJSONArray("info").getJSONObject(0);
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
					PubConnect.UpdateConnect.UpdateDialog di = up.getDialog(me);
					di.show();
				}
			}else
			{
				Utils.toast(this,"Unable ti find updates");
			}
		}catch(Exception e)
		{
			Utils.toast(this,e.toString());
		}
	}
	
}
