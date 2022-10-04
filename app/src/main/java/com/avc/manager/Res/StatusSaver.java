package com.avc.manager.Res;
import android.support.v7.app.*;
import com.avc.manager.*;
import java.io.*;
import android.os.*;

public class StatusSaver
{
	private AppCompatActivity activity;
	public static GROUPFiles files;
	public StatusSaver(AppCompatActivity a)
	{
		activity = a;
	}
	public static void saveStatus(AppCompatActivity a,File f)
	{
		AudioExtractor ex = new AudioExtractor();
		File url = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Utils.FOLDER +"/Audio/");
		if(!url.exists()) url.mkdirs();
		String path = url.getAbsolutePath()+"/"+f.getName()+".mp3";
		try{
			ex.genVideoUsingMuxer(f.getAbsolutePath(),path,-1,-1,true,false);
		}catch(Exception e){Utils.toast(a,"Unable to save status "+e.toString());}
	}
	public void getStatuses(final AppCompatActivity a)
	{
		files = new GROUPFiles();
		files.name = Manager.status_saver_name;
		a.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				onstatusscan.onStart(files);
			}
		});
		
		
		files.setListener(new GROUPFiles.Listener()
		{
			@Override public void onAdded(GROUPFiles f)
			{
				a.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						onstatuschange.onAdd(files);
					}
				});
				
			}
			@Override public void onRemove(GROUPFiles f)
			{
				a.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						onstatuschange.onRemove(files);
					}
				});
				
			}
			@Override public void onPause(GROUPFiles f)
			{
				a.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						onstatusscan.onEnd(files);
					}
				});
				
			}
		});
		new Thread(new Runnable()
		{
			@Override public void run()
			{
				for(Manager.Saveable fi : Manager.status_saver_saveable)
				{
					boolean flag = false;
					GROUPFiles fil = new GROUPFiles();
					fil.linkedObject = fi;
					for(String fol : fi.folders)
					{
						File f = new File(fol);
						if(f.exists() && f.isDirectory())
						{
							//Utils.toast(a,fi.folders.toString());
							
							for(File fols : f.listFiles()) if(!fi.except_files.contains(fols.getName())){
								fil.add(fols);
								flag = true;
							}
						}
					}
					
					for(String fol : fi.files)
					{
						File f = new File(fol);
						if(f.exists() && f.isFile()){
							fil.add(f);
							flag = true;
						}
					}
					fil.setName(fi.name);
					fil.setParent(fi.parent);
					if(flag){
						files.add(fil);
					}
				}
				files.pause();
			}
		}).start();
	}
	
	/* 
	***************
	Listeners
	***************
	*/
	// Status scan new file added or removed listener
	public OnStatusChangeListener onstatuschange = new OnStatusChangeListener()
	{
		@Override public void onAdd(GROUPFiles all){}
		@Override public void onRemove(GROUPFiles all){}
	};
	public void setOnStatusChangeListener(OnStatusChangeListener l){onstatuschange = l;}
	public interface OnStatusChangeListener
	{
		void onAdd(GROUPFiles all);
		void onRemove(GROUPFiles all);
	}
	// The status scan started or ended listenr
	public OnStatusScanListener onstatusscan = new OnStatusScanListener()
	{
		@Override public void onStart(GROUPFiles all){}
		@Override public void onEnd(GROUPFiles all){}
	};
	public void setOnStatusScanListener(OnStatusScanListener l){onstatusscan = l;}
	public interface OnStatusScanListener
	{
		void onStart(GROUPFiles all);
		void onEnd(GROUPFiles all);
	}
}
