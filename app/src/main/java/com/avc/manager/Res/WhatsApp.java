package com.avc.manager.Res;
import androidx.appcompat.app.*;
import java.io.*;
import android.os.*;
import java.util.*;
import android.util.*;
import com.avc.manager.*;

public class WhatsApp
{
	private AppCompatActivity activity;
	private File base_file;
	public WhatsApp(AppCompatActivity a)
	{
		activity = a;
		base_file = getBaseFile();
		
	}
	
	public GROUPFiles getStatuses(GROUPFiles f)
	{
		File status = new File(base_file + "/Media/.Statuses/");
		f.setName("Whatsapp Statuses");
		f.setParent(status.getAbsolutePath());
		if(status != null && status.exists())
		{
			for(File m : status.listFiles())
			{
				if(m != null &&  m.isFile())
				{
					if(Utils.getMimeType(m.getAbsolutePath()) == null) continue;
					f.setCurrent(m.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getAbsolutePath(),""));
					f.add(m);
				}
			}
		}
		f.pause();
		return f;
	}
	
	// Get all files inside .shared folder and get its size in long 
	
	public GROUPFiles getWhatsAppShared(GROUPFiles m)
	{
		// Search for .Shared file
		
		File shared = new File(base_file + "/.Shared/");
		m.setName("WhatsApp Shared");
		m.setParent(base_file + "/.Shared/");
		if(shared != null && shared.exists())
		{
			for(File f : shared.listFiles())
			{
				if(f != null && f.isFile())
				{
					m.setCurrent(f.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getAbsolutePath(),""));
					m.add(f);
				}
			}
		}
		m.pause();
		return m;
	}
	
	// Get base file path (WhatsApp)
	
	public File getBaseFile()
	{
		String path = Environment.getExternalStorageDirectory() + "/WhatsApp/";
		File f =  new File(path);
		if(f.exists() && f.isDirectory()) return f;
		else return null;
	}
	
}
