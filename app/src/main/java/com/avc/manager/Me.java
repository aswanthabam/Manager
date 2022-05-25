package com.avc.manager;
import java.text.*;
import java.util.*;

public class Me
{
	public static String app_id = "1000";
	public static String version = "1";
	public static String app_pass = "avcmanager";
	public static String name = "AVC Manager";
	public static Update update = null;
	public Me(){}
	
	public class Update
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
		
		public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public Date date = null;
		
		public Date get_created_date()
		{
			try{return format.parse(created_date);}
			catch(Exception e){return null;}
		}
		
		public Date get_uploaded_date()
		{
			try{return format.parse(created_date);}
			catch(Exception e){return null;}
		}
	}
}
