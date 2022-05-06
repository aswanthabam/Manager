package com.avc.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.widget.*;
import android.view.animation.*;
import android.util.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private Toolbar toolbar;
    private DrawerLayout d;
	private NavigationView n;
	public TextView infoTxt;
	public Animation floatUp;
	int info_i = 0;
	AppCompatActivity activity;
	/*GridLayout home_grid;
	LinearLayout GridItem1,GridItem2,GridItem3,GridItem4;
    */
	LinearLayout GridContainer, HomeTopBox;
	public int SystemWidth,SystemHeight;
	private String[] info_txts = {"NMB of storage can be freed","Delete old whatsapp images","TELENMB Storage is used by telegram"};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		activity = this;
		
        toolbar = findViewById(R.id.mainToolbar);
        d = findViewById(R.id.mainDrawerLayout);
		n = findViewById(R.id.nav_view);
		
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
}
