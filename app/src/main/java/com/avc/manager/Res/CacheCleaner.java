package com.avc.manager.Res;
import java.io.*;
import android.*;
import android.support.v7.app.*;
import android.content.pm.*;
import java.lang.reflect.*;
import com.avc.manager.*;

public class CacheCleaner
{
	private AppCompatActivity activity;
	
	public CacheCleaner(AppCompatActivity a)
	{
		activity = a;
		try{
			PackageManager pm = a.getPackageManager();
			Method method = pm.getClass().getMethod("freeStorageAndNotify", new Class[] { Long.TYPE, Class.forName("")});
			method.setAccessible(true);
			Utils.toast(activity,method.invoke(pm, Long.MAX_VALUE, null).toString());
			
		}catch(Exception e){
			Utils.toast(activity,"Unable to toast "+e.toString());
		}
	}
}
