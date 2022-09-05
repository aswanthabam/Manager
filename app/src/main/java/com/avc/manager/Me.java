package com.avc.manager;
import java.text.*;
import java.util.*;
import java.io.*;
// Class for storing all deatils about the app itself
// Used for update purpose (Version id and othe details are there)
// An Me.Update class is available for storing the deatils about the update fetched online
public class Me
{
	public static String host = "https://avctech.000freewebhostapp.com/";
	// Some details which are constant all over the app
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static File internalStorage;
	
	// Details about the system
	//Variables
	public static String externalStorageDirectory = "";	// External storage directory of device
	// Measures
	public static int SystemWidth = 0;		// Screen width
	public static int SystemHeight = 0;		// Screen Height
	public static int titleBarHeight = 0;	// Title bar height(Top status bar height)
	public static int density = 0;			// Density of screen
	public static int dr_size = 0;			// Drawer icon or topbar menu icon. Thus is the size of that perticular image
	
	// Details about the app
	
	public static String app_id = "1000";
	public static String version = "1";
	public static String version_name = "avc_manager_beta 1.0.0";
	public static String app_pass = "avcmanager";
	public static String name = "AVC Manager";
	public static Update update = new Update();
	
	public Me(){}
	// Update class holding information about an update
	public static class Update
	{
		
		public String app_id = "";
		public String app_name = "";
		public String version = "";
		public String version_id = "";
		public String author = "";
		public String description = "";
		public String file_name = "";
		public String pub_name = "";
		public String more_link = "";
		public String uploaded_date = "";
		public String created_date = "";
		public String common_app_id = "";
		
		public String download_link = "";
		public boolean download_link_is_redirect = false;
		public boolean download_link_is_icedrive = true;
		
		public String title = "";
		public String sub_title = "";
		public String update_description = "";
		public boolean must_update = true;
		
		//public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public Date date = null;
		
		// Return the created date as "Date" objet
		
		public Date get_created_date()
		{
			try{return dateFormat.parse(created_date);}
			catch(Exception e){return null;}
		}
		
		// Retuen the uploaded date as "Date" Object
		
		public Date get_uploaded_date()
		{
			try{return dateFormat.parse(created_date);}
			catch(Exception e){return null;}
		}
	}
}
