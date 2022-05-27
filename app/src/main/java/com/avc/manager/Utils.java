package com.avc.manager;

import android.widget.*;
import java.text.*;
import java.util.*;
import java.io.*;
import android.webkit.*;
import android.graphics.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.net.*;
import org.apache.http.*;
import java.net.*;
import org.json.*;
import android.support.v7.app.*;

public class Utils
{
	public AppCompatActivity activity;
	public static final String FOLDER = "AVC Manager";
	
	public Utils(AppCompatActivity a)
	{
		activity = a;
	}
	
	// Convert string to Json
	
	public static JSONObject stringToJSON(String txt)
	{
		JSONObject obj = null;
		try{
			obj = new JSONObject(txt);
		}catch(Exception e)
		{
			Log.e("ERROR","cant convert string to json ("+e.toString()+")");
		}
		return obj;
	}
	
	// read responce from httpurlconnection (web)
	
	public static String reader(AppCompatActivity context,HttpURLConnection u)
	{
		String t = "";
		int n = 0;
		char[] buffer = new char[1024*4];
		try{
			int responceCode = u.getResponseCode();
			InputStream in;
			if(responceCode == 200)
				in = new BufferedInputStream(u.getInputStream());
			else in = u.getErrorStream();
			InputStreamReader reader = new InputStreamReader(in,"UTF-8");
			StringWriter writer = new StringWriter();
			while(-1 != (n = reader.read(buffer))) writer.write(buffer,0,n);
			t = writer.toString();
			in.close();
			u.disconnect();
		}catch(Exception e){
			toast(context,"Couldnt connect to server "+e.toString());
			Log.e("HTTP_CONNECTION_ERROR_3",e.toString());
		}
		return t;
	}
	
	// Convert namw value pair to query in the form key=value&key2=value2
	
	public static String QUERYEncode(List<NameValuePair> params) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params)
		{
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}
	
	// Share a given file to app. share using device share service
	
	public static void shareFile(Context c,String title,String txt,String path,String type)
	{
		if(type == null) type = "*/*";
		if(title == null) title = "AVC Manager";
		if(txt == null) txt = "";
		if(path == null) return;
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = Uri.parse(path);
		sharingIntent.setType(type);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		sharingIntent.putExtra(Intent.EXTRA_TEXT, txt);
		sharingIntent.putExtra(Intent.EXTRA_TITLE, title);
		//toast((AppCompatActivity) c,type);
		c.startActivity(Intent.createChooser(sharingIntent, "Share using"));
		
	}
	
	// Save given file to device folder(Copy it);
	
	public static File saveToDevice(File f,String folder)
	{
		File output = null;
		String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER;
		if(folder != null)
		{
			root += "/" + folder;
		}
		File fo = new File(root);
		if(!fo.exists())
		{
			fo.mkdirs();
		}
		try{
			FileInputStream in = new FileInputStream(f);
			FileOutputStream out = new FileOutputStream(fo.getAbsolutePath() + "/" + f.getName());
			byte[] buffer = new byte[1024];
			int read;
			while((read = in.read(buffer)) != -1)
			{
				out.write(buffer,0,read);
			}
			in.close();
			
			out.flush();
			out.close();
			
			output = new File(fo.getAbsolutePath()+"/"+f.getName());
		}catch(Exception e)
		{
			Log.e("FILE_SAVE_ERROR","WHILE SAVING FILE "+f.getAbsolutePath()+"|   "+e.toString());
		}
		return output;
	}
	
	// Get drawable
	// from bitmap
	public static Drawable getDrawable(Context c,Bitmap f)
	{
		return (Drawable) new BitmapDrawable(f);
	}
	// from path
	public static Drawable getDrawable(Context c,String f)
	{
		return (Drawable) new BitmapDrawable(getBitmap(c,f));
	}
	// from file
	public static Drawable getDrawable(Context c,File f)
	{
		return (Drawable) new BitmapDrawable(getBitmap(c,f));
	}
	// from resource
	public static Drawable getDrawable(Context c,int r)
	{
		return c.getDrawable(r);
	}
	
	// Get bitmap
	// from resource
	public static Bitmap getBitmap(Context c,int f)
	{
		return BitmapFactory.decodeResource(c.getResources(),f);
	}
	// from drawable
	public static Bitmap getBitmap(Context c,Drawable f)
	{
		return ((BitmapDrawable) f).getBitmap();
	}
	// from path
	public static Bitmap getBitmap(Context c,String f)
	{
		return BitmapFactory.decodeFile(f);
	}
	// from file
	public static Bitmap getBitmap(Context c,File f)
	{
		return BitmapFactory.decodeFile(f.getAbsolutePath());
	}
	
	// Get file mime type
	
	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}
	
	// Functions to get size of files
	// Attribute single file
	public static long getSIZE(File f)
	{
		return f.length();
	}
	// Attribute file array
	public static long getSIZE(File[] f)
	{
		long si = 0;
		for(File k : f)
		{
			si += k.length();
		}
		return si;
	}
	// Attribute file list
	public static long getSIZE(List<File> f)
	{
		long si = 0;
		for(int i = 0;i < f.size();i++)
		{
			si += f.get(i).length();
		}
		return si;
	}
	
	// Convert size in long to appropriate string

	public static String toSTRING(long si)
	{
		String st = null;
		float siz = 0;
		String qu = null;
		if(toGB(si) > 0)
		{
			siz = toGB(si);
			qu = "GB";
		}
		else if(toMB(si) > 0)
		{
			siz = toMB(si);
			qu = "MB";
		}
		else if(toKB(si) > 0)
		{
			siz = toKB(si);
			qu = "KB";
		}else
		{
			siz = si;
			qu = "B";
		}
		DecimalFormat fo = new DecimalFormat("#.##");
		
		st = fo.format(siz) + " " + qu;
		return st;
	}
	
	// Convert size in long to float GB

	public static float toGB(long si)
	{
		float s = si/1000000000;
		return s;
	}
	
	// Convert size in long to float MB
	
	public static float toMB(long si)
	{
		float s = si/1000000;
		return s;
	}
	
	// Convert size in long to float KB

	public static float toKB(long si)
	{
		float s = si/1000;
		return s;
	}
	
	// Static function to make toast
	
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
	
	// Non static function to toast
	
	public void toast(final String txt)
	{
		activity.runOnUiThread(new Runnable()
			{
				@Override public void run()
				{
					Toast.makeText(activity,txt,Toast.LENGTH_LONG).show();
				}
			});
	}
}
