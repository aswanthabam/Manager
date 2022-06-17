package com.avc.manager;
import java.util.*;
import com.avc.manager.Res.*;
import java.io.*;
import android.app.*;
import org.json.*;
import android.support.v7.app.*;

// Class handles tge details about this app Manager
public class Manager
{
	public static List<String> show_home = new ArrayList<String>();	// The strings which want to.be shown in home screen
	public static AppCompatActivity _main_activity;
	/*
	 All the static and non static contebt below is 
	 the contenys needed for status saver activity
	*/
	
	//Details about space_cleaner activity
	public static StatusSaver status_saver;
	public static String status_saver_name = "";
	public static int status_saver_version = 0;
	public static Date status_saver_date = new Date();
	public static String status_saver_show_in_home_text = "";
	public static String status_saver_description = "";
	public static List<Saveable> status_saver_saveable = new ArrayList<Saveable>();;
	
	public static class Saveable
	{
		/* This class contains details about the Savable statuses */
		public String name = "";
		public List<String> folders = new ArrayList<String>();
		public List<String> files = new ArrayList<String>();
		public List<String> mimes = new ArrayList<String>();
		public List<String> except_files = new ArrayList<String>();
		public String save_to = "";
		public String icon = "";
		public boolean show_in_home = false;
		public String show_in_home_text = "";
		public String parent = "";
	}
	
	/* 
	 *******************
	 loadSpaceCleaner params = AppCompatactivity
	 load the spacecleaner activity static contents from asset or cache
	 *******************
	 */
	public static boolean loadStatusSaver(AppCompatActivity a)
	{
		try{
			//File f = new File("android:///assets/space_cleaner.json");
			InputStream fos = a.getAssets().open("status_saver.json");
			char[] b = new char[1024];
			int i;
			InputStreamReader re = new InputStreamReader(fos);
			StringWriter writer = new StringWriter();
			while(-1 !=(i = re.read(b))) writer.write(b,0,i);
			fos.close();
			writer.close();
			
			JSONObject status = new JSONObject(writer.toString());
			status_saver_name = status.getString("name");
			status_saver_version = status.getInt("version");
			status_saver_date = Me.dateFormat.parse(status.getString("date"));
			status_saver_show_in_home_text = status.getString("show-in-home-text");
			status_saver_description = status.getString("description");
			
			JSONArray savs = status.getJSONArray("saveable");
			for(i = 0;i < savs.length();i++)
			{
				JSONObject sav = savs.getJSONObject(i);
				Saveable ab = new Saveable();
				ab.name = sav.getString("name");
				List<String> mv = new ArrayList<String>();
				JSONArray fol = sav.getJSONArray("folders");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.folders = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("files");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.files = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("mimes");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.mimes = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("except-files");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.except_files = mv;
				
				ab.save_to = sav.getString("save-to");
				ab.icon = sav.getString("icon");
				ab.show_in_home = sav.getBoolean("show-in-home");
				ab.show_in_home_text = sav.getString("show-in-home-text");
				ab.parent = sav.getString("parent");
				
				status_saver_saveable.add(ab);
			}
			return true;
		}catch(Exception e)
		{
			//Utils.toast(_main_activity,e.toString());
			return false;
		}
	}
	
	
	/*
	 Below data are about all the static and non static functions and 
	 constant varuables which is needed for running.space_cleaner activity
	*/
	
	// Details about space_clearner activity
	public static SpaceCleaner space_cleaner;
	public static int space_cleaner_version = 0;		// Space cleaner file version
	public static String space_cleaner_name = "";		// Space cleaner name unnessasary
	public static Date space_cleaner_date = new Date();	// Space cleaner file last updated date
	public static List<Scanable> space_cleaner_scannable = new ArrayList<Scanable>();		// Space cleaner details about which files can be scanned
	public static String space_cleaner_show_in_home_text = "&size space available";
	
	public Manager(){}
	
	// Class containg information about Space cleaner scannable files or items
	
	public static class Scanable
	{
		public String name = "";	// Name of scannablte item (eg: Whatsapp Shared)
		public List<String> folders = new ArrayList<String>();	// List of folders which are included in this item
		public List<String> files = new ArrayList<String>();	// List of files which are included in this item
		public boolean folder_delete_all = false;	// If we wanr to delete all files inside the given folders
		public List<String> folder_delete_all_except = new ArrayList<String>();	// Files which want to ve excluded when deleating all files inside a folder the folder_delete_all want to be set to false to do this
		public boolean file_delete_all = false;		// Delete all files given in the array
		public boolean file_delete_one = false;		// Delete only one file which exists on the files array
		public String description = "";				// Description of item
		public boolean ask_before_delete = false;	// Ask before deleteing this item
		public String ask_before_info = "";			// Info want to be shown before deleteing this item
		public String parent = "";
		public boolean show_in_home = false;
		public String show_in_home_text = "";
		
		public Scanable()
		{

		}
	}
	
	/* 
	*******************
	loadSpaceCleaner params = AppCompatactivity
	load the spacecleaner activity static contents from asset or cache
	*******************
	*/
	public static boolean loadSpaceCleaner(AppCompatActivity a)
	{
		try{
			//File f = new File("android:///assets/space_cleaner.json");
			InputStream fos = a.getAssets().open("space_cleaner.json");
			char[] b = new char[1024];
			int is;
			InputStreamReader re = new InputStreamReader(fos);
			StringWriter writer = new StringWriter();
			while(-1 !=(is = re.read(b))) writer.write(b,0,is);
			fos.close();
			writer.close();

			JSONObject space = new JSONObject(writer.toString());

			Manager.space_cleaner_name = space.getString("name");
			Manager.space_cleaner_date = Me.dateFormat.parse(space.getString("date"));
			Manager.space_cleaner_version = space.getInt("version");
			Manager.space_cleaner_show_in_home_text = space.getString("show-in-home-text");

			JSONArray sp_arr = space.getJSONArray("scanable");
			for(is = 0;is < sp_arr.length();is++)
			{
				JSONObject sp_sca = sp_arr.getJSONObject(is);
				Manager.Scanable sca = new Manager.Scanable();

				sca.name = sp_sca.getString("name");
				JSONArray mc = sp_sca.getJSONArray("folders");
				for(int j = 0;j < mc.length();j++) sca.folders.add(Me.externalStorageDirectory + mc.getString(j));
				mc = sp_sca.getJSONArray("files");
				for(int j = 0;j < mc.length();j++) sca.files.add(Me.externalStorageDirectory + mc.getString(j));
				sca.folder_delete_all = sp_sca.getBoolean("folder-delete-all");
				mc = sp_sca.getJSONArray("folder-delete-all-except");
				for(int j = 0;j < mc.length();j++) sca.folder_delete_all_except.add(mc.getString(j));
				sca.file_delete_all = sp_sca.getBoolean("file-delete-all");
				sca.file_delete_one = sp_sca.getBoolean("file-delete-one");
				sca.description = sp_sca.getString("description");
				sca.ask_before_delete = sp_sca.getBoolean("ask-before-delete");
				sca.ask_before_info = sp_sca.getString("ask-before-info");
				sca.parent = sp_sca.getString("parent");
				sca.show_in_home = sp_sca.getBoolean("show-in-home");
				sca.show_in_home_text = sp_sca.getString("show-in-home-text");

				Manager.space_cleaner_scannable.add(sca);
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
