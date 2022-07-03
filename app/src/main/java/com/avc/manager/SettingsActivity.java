package com.avc.manager;
import android.support.v7.app.*;
import android.os.*;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import android.preference.*;
import android.graphics.drawable.*;
import android.graphics.*;
import com.avc.manager.Res.*;
import org.json.*;
import com.avc.manager.Res.PubConnect.*;
import java.net.*;
import android.util.*;
import android.view.*;

public class SettingsActivity extends AppCompatActivity 
{
	private static AppCompatActivity activity;
	private FrameLayout lay;
	private Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(Manager.getTheme(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		activity = this;
		
		lay = findViewById(R.id.settings_activityFrameLayout);
		toolbar = findViewById(R.id.mainToolbar);
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle("Settings");
		Drawable dr = getResources().getDrawable(R.drawable.btn_back);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable di = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Me.dr_size, Me.dr_size, true));
		di.setTint(getResources().getColor(Utils.getAttr(this,R.attr.colorAccent)));
		getSupportActionBar().setHomeAsUpIndicator(di); // Change drawer icon
		
		SettingFragment frag = new SettingFragment();
		getFragmentManager().beginTransaction().replace(R.id.settings_activityFrameLayout,frag).commit();
		//frag.update_app = frag.getPreferenceManager().findPreference("check_update_app");
		
		
	}
	
	public static class SettingFragment extends PreferenceFragment implements PubConnect.OnUpdateResponceListener
	{
		Preference pref1,pref2,pref3;
		SwitchPreference dark_theme;
		PreferenceScreen update_app,update_space,update_status;
		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.preferences);
			
			update_app = (PreferenceScreen) findPreference("check_update_app");
			update_space = (PreferenceScreen) findPreference("check_update_space_cleaner");
			update_status = (PreferenceScreen) findPreference("check_update_status_saver");
			dark_theme = (SwitchPreference) findPreference("theme");
			pref1 = findPreference("status_saver_version");
			pref2 = findPreference("space_cleaner_version");
			pref3 = findPreference("app_version");
			
			pref1.setSummary(String.valueOf(Manager.status_saver_version_name));
			pref2.setSummary(String.valueOf(Manager.space_cleaner_version_name));
			pref3.setSummary(Me.version_name);
			//update_app.setSummary("noting hete bri");
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			// TODO: Implement this method
			super.onActivityCreated(savedInstanceState);
			
			update_app.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
				{
					@Override public boolean onPreferenceClick(Preference pr)
					{
						Utils.toast(activity,"Checking for updates");
						PubConnect conn = new PubConnect(activity,Me.app_id,Me.app_pass,Me.version);
						conn.setOnUpdateResponceListener(SettingFragment.this);
						conn.checkForUpdates();
						return true;
					}
				});
			dark_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override public boolean onPreferenceChange(Preference p1,Object p)
				{
					Manager.setTheme((AppCompatActivity)getActivity());
					onchangetheme.onChange();
					return true;
				}
			});
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			// TODO: Implement this method
			
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
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
						if(!Manager.updateApp(message,up)) Utils.toast(activity,"Couldnt find upadte");
					}
					else
					{
						Utils.toast(activity,"Already in the latest version");
					}
				}else
				{

				}
			}catch(Exception e)
			{
				Log.e("UPDATE_CHECK","Couldnt connect to server");
				Utils.toast(activity,"No updates available");
			}
			else Utils.toast(activity,"Unable to connect at this moment");
		}
		
		public void onChange(AppCompatActivity a)
		{
			Manager.setTheme(a);
		}
		public static OnChangeTheme onchangetheme = new OnChangeTheme()
		{
			
			@Override
			public void onChange()
			{
				//onChange();
				//super.onChange((AppCompatActivity)getActivity());
			}
			
		};
		public static void setOnChangeListener(OnChangeTheme l){onchangetheme = l;}
		public interface OnChangeTheme
		{
			void onChange();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
	
	
}
