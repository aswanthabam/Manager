package com.avc.manager;
import java.util.*;
import com.avc.manager.Res.*;
import java.io.*;
import android.app.*;
import org.json.*;
import android.support.v7.app.*;
import java.util.zip.*;
import android.graphics.*;
import android.util.*;
import android.media.*;
import android.provider.*;
import android.content.*;

// Class handles tge details about this app Manager
public class Manager
{
	public static List<String> show_home = new ArrayList<String>();	// The strings which want to.be shown in home screen
	public static AppCompatActivity _main_activity;
	public static boolean loaded_status_saver = false;
	public static boolean loaded_space_cleaner = false;
	public static boolean permission_granted = true;
	public static boolean loaded_about = false;
	
	public static int about_manager_zip_version = 0;
	public static int about_status_saver_version = 0;
	public static int about_space_cleaner_version = 0;
	
	public static Map<File,Bitmap> thumbnails = new ArrayMap<File,Bitmap>();
	
	public static void collectThumbnails(GROUPFiles f){collectThumbnails(f.files);}
	public static void collectThumbnails(List<File> f){
		for(File fi:f)collectThumbnails(fi);
	}
	public static void collectThumbnails(File f)
	{
		String m = Utils.getMimeType(f.getAbsolutePath());
		if(m == null) return;
		Bitmap Thumb = null;
		if(m.startsWith("image/"))
		{
			Thumb = ThumbnailUtils.createImageThumbnail(f.getAbsolutePath(),MediaStore.Images.Thumbnails.MINI_KIND);
		}
		else if(m.startsWith("video/"))
		{
			Thumb = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(),MediaStore.Video.Thumbnails.MINI_KIND);
		}else{

		}
		Manager.thumbnails.put(f,Thumb);
	}
	/*
	 All the static and non static contebt below is 
	 the contenys needed for status saver activity
	*/
	
	//Details about space_cleaner activity
	public static StatusSaver status_saver;
	public static String status_saver_name = "";
	public static int status_saver_version = 0;
	public static String status_saver_version_name = "";
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
			File internalFile = new File(Me.internalStorage.getAbsolutePath()+"/Manager/status_saver.json");
			String con;
			boolean take_asset = false;
			JSONObject status = null;
			while(true){
				if(internalFile.exists() && !take_asset)
				{
					con = Utils.readFile(internalFile);
					status = new JSONObject(con);
					if(status.getInt("version") < about_status_saver_version)
					{
						take_asset = true;
						continue;
					}
					break;
				}else
				{
					if(decompress(a.getAssets().open("Manager.zip"),new File(Me.internalStorage.getAbsolutePath()+"/Manager/")))
					{
						con = Utils.readFile(internalFile);
						status = new JSONObject(con);
					}else return false;
					break;
				}
			}
			int i = 0;
			
			status_saver_name = status.getString("name");
			status_saver_version = status.getInt("version");
			status_saver_date = Me.dateFormat.parse(status.getString("date"));
			status_saver_show_in_home_text = status.getString("show-in-home-text");
			status_saver_description = status.getString("description");
			status_saver_version_name = status.getString("version-name");
			JSONArray savs = status.getJSONArray("saveable");
			for(i = 0;i < savs.length();i++)
			{
				JSONObject sav = savs.getJSONObject(i);
				Saveable ab = new Saveable();
				ab.name = sav.getString("name");
				List<String> mv = new ArrayList<String>();
				JSONArray fol = sav.getJSONArray("folders");
				for(int j = 0;j < fol.length();j++) mv.add(Me.externalStorageDirectory+ fol.getString(j));
				ab.folders = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("files");
				for(int j = 0;j < fol.length();j++) mv.add(Me.externalStorageDirectory + fol.getString(j));
				ab.files = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("mimes");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.mimes = mv;
				mv = new ArrayList<String>();
				fol = sav.getJSONArray("except-files");
				for(int j = 0;j < fol.length();j++) mv.add(fol.getString(j));
				ab.except_files = mv;
				
				ab.save_to = Me.externalStorageDirectory + sav.getString("save-to");
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
	public static String space_cleaner_version_name = "";// space cleaner version name
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
		public boolean show_user = false;
		public boolean count = false;
		
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
			
			File internalFile = new File(Me.internalStorage.getAbsolutePath()+"/Manager/space_cleaner.json");
			String con;
			boolean take_asset = false;
			JSONObject space = null;
			while(true){
				if(internalFile.exists() && !take_asset)
				{
					con = Utils.readFile(internalFile);
					space = new JSONObject(con);
					if(space.getInt("version") < about_space_cleaner_version)
					{
						take_asset = true;
						continue;
					}
					break;
				}else
				{
					if(decompress(a.getAssets().open("Manager.zip"),new File(Me.internalStorage.getAbsolutePath()+"/Manager/")))
					{
						con = Utils.readFile(internalFile);
						space = new JSONObject(con);
					}else return false;
					break;
				}
			}
			int is = 0;
			

			space_cleaner_name = space.getString("name");
			space_cleaner_date = Me.dateFormat.parse(space.getString("date"));
			space_cleaner_version = space.getInt("version");
			space_cleaner_show_in_home_text = space.getString("show-in-home-text");
			space_cleaner_version_name = space.getString("version-name");
			
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
				sca.show_user = sp_sca.getBoolean("show-user");
				sca.count = sp_sca.getBoolean("count");
				
				Manager.space_cleaner_scannable.add(sca);
			}
			return true;
		}catch(Exception e){
			//Utils.toast(a,e.toString());
			return false;
		}
	}
	
	/*
	 Zip file decompression
	*/
	public static boolean decompress(InputStream str,File to_folder)
	{
		ondecompress.onDecompress(str,to_folder);
		try
		{
			if(!to_folder.exists()) to_folder.mkdirs();
			else if(!to_folder.isDirectory()) return  false;
			String filename = "";
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(str));
			ZipEntry ze;
			byte[] buffer = new byte[1024];
			int count;
			
			while((ze = zis.getNextEntry()) != null)
			{
				filename = ze.getName();
				String to = to_folder.getAbsolutePath()+"/"+filename;
				if(ze.isDirectory())
				{
					File f = new File(to);
					if(!f.exists() || !f.isDirectory()) f.mkdirs();
					continue;
				}
				
				FileOutputStream fos = new FileOutputStream(to);
				while((count = zis.read(buffer)) != -1)
				{
					fos.write(buffer,0,count);
				}
				fos.close();
				zis.closeEntry();
			}
			zis.close();
			return true;
		}catch(Exception e)
		{
			Utils.toast(_main_activity,e.toString());
			return false;
		}
	}
	public static OnDecompressFileListener ondecompress = new OnDecompressFileListener()
	{

		@Override public void onDecompress(InputStream str, File to){}
	};
	public static void setOnDecompressFileListener(OnDecompressFileListener l){ondecompress = l;}
	public interface OnDecompressFileListener
	{
		void onDecompress(InputStream str,File to);
	}
	
	/*
	 Get about file
	*/
	
	public static boolean loadAbout(Context a)
	{
		try{
			String out = Utils.readFile(a.getAssets().open("about.json"));
			JSONObject about = new JSONObject(out);
			
			about_manager_zip_version = about.getInt("manager-zip-version");
			about_space_cleaner_version = about.getInt("space-cleaner-version");
			about_status_saver_version = about.getInt("status-saver-version");
			return true;
		}catch(Exception e)
		{
			return false;
		}
	}
	
	/* 
		Update App 
		This function dont itself update the app .
		This function will setup the update by changing static variables.
		This function need the json object of the responce message.
		Note: responce message is the ["message"] object of the given update responce of server.
		Note: This function doesnt check if update is available
	*/
	
	public static boolean updateApp(JSONObject message,PubConnect.UpdateConnect up)
	{
		try{
			JSONObject update = message.getJSONObject("update");
			JSONObject info = update.getJSONArray("info").getJSONObject(0);
			// Add all deatils to "Me" object (Me.Update)
			Me.update.app_id = update.getString("app_id");
			Me.update.app_name = update.getString("app_name");
			Me.update.version = update.getString("version");
			Me.update.version_id = update.getString("version_id");
			Me.update.author = update.getString("author");
			Me.update.description = update.getString("description");
			Me.update.file_name = update.getString("file_name");
			Me.update.pub_name = update.getString("pub_name");
			Me.update.more_link = update.getString("more_link");
			Me.update.uploaded_date = update.getString("uploaded_date");
			Me.update.created_date = update.getString("created_date");
			Me.update.common_app_id = update.getString("common_app_id");

			Me.update.download_link = update.getString("download_link");
			Me.update.download_link_is_redirect = update.getBoolean("download_link_is_redirect");
			Me.update.download_link_is_icedrive = update.getBoolean("download_link_is_icedrive");

			Me.update.title = info.getString("title");
			Me.update.sub_title = info.getString("sub_title");
			Me.update.update_description = info.getString("description");
			Me.update.must_update = info.getBoolean("must_update");
			// Create pubconnect dialog with update notification
			PubConnect.UpdateConnect.UpdateDialog di = up.getDialog();
			di.show(); //show
			return true;
		}catch(Exception e)
		{
			return false;
		}
	}
}
