package com.avc.manager.Res;
import android.support.v7.app.*;
import com.avc.manager.*;
import java.util.*;
import java.io.*;
import android.util.*;

public class SpaceCleaner
{
	private AppCompatActivity activity;
	public static GROUPFiles files;
	public static GROUPFiles un_counted_files;
	public static GROUPFiles all_files;
	public static Map<Manager.Scanable,Integer> show_home_contributions = new ArrayMap<Manager.Scanable,Integer>();
	public SpaceCleaner(AppCompatActivity a)
	{
		activity = a;
	}
	public static long size = 0;
	// Scan for space cleanup for fast up it is done inside a thread
	
	public void storageScan()
	{
		
		files = new GROUPFiles();
		un_counted_files = new GROUPFiles();
		all_files = new GROUPFiles();
		
		activity.runOnUiThread(new Runnable(){
			@Override public void run()
			{
				onscan.onStart(files);
			}
		});
		
		new Thread(new Runnable(){
				@Override public void run()
				{
					un_counted_files.setListener(new GROUPFiles.Listener()
						{
							@Override public void onAdded(final GROUPFiles f)
							{
								all_files.add(f);
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											onuncountedfilechange.onUnCountedAdd(f);
										}
									});
							}
							@Override public void onRemove(final GROUPFiles f)
							{
								all_files.remove(f);
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											onuncountedfilechange.onUnCountedRemove(f);
										}
									});
							}
							@Override public void onPause(final GROUPFiles f){}
						});
					// Group files liatener callbacks when added,removed,paused the filss
					files.setListener(new GROUPFiles.Listener()
						{
							@Override public void onAdded(final GROUPFiles f)
							{
								all_files.add(f);
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											onfilechange.onAdd(f);

										}
									});
							}
							@Override public void onRemove(final GROUPFiles f)
							{
								all_files.remove(f);
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											onfilechange.onRemove(f);
										}
									});
							}
							@Override public void onPause(final GROUPFiles f)
							{
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											onscan.onEnd(f);
										}
									});
							}
						});
					
					// Loop through all scanable file defined this data in Me is added through the main activity
					for(final Manager.Scanable scan : Manager.space_cleaner_scannable)
					{
						boolean flag = false;
						GROUPFiles fiso = new GROUPFiles();
						fiso.setListener(new GROUPFiles.Listener(){
							@Override public void onAdded(final GROUPFiles f){
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											/*if(scan.count)files.add(f);
											else un_counted_files.add(f);*/
										}
									});
							}
							@Override public void onRemove(final GROUPFiles f){
								activity.runOnUiThread(new Runnable()
									{
										@Override public void run()
										{
											/*if(scan.count)files.remove(f);
											else un_counted_files.remove(f);*/
										}
									});
							}
							@Override public void onPause(GROUPFiles f){
								
							}
						});
						fiso.setName(scan.name);
						fiso.setParent(scan.parent);

						if(scan.folders.size() > 0)
						{
							if(scan.folder_delete_all){
								if(scanAll(fiso,scan.folders))flag = true;
							}else{
								if(scanAll(fiso,scan.folders,scan.folder_delete_all_except)) flag = true;
							}
						}
						if(scan.files.size() > 0)
						{
							if(scanFile(fiso,scan.files,scan.file_delete_one)) flag = true;
						}
						fiso.setLinkage(scan);
						if(flag){
							if(scan.count) files.add(fiso);
							else un_counted_files.add(fiso);
							if(scan.show_in_home){
								if(show_home_contributions.containsKey(scan)){
									int mj = show_home_contributions.get(scan);
									Manager.show_home.set(mj,scan.show_in_home_text.replaceAll("&size",fiso.sizeSTR));
								}else{
									Manager.show_home.add(scan.show_in_home_text.replaceAll("&size",fiso.sizeSTR));
									int mjj = Manager.show_home.size()-1;
									show_home_contributions.put(scan,mjj);
								}
								onhomemessage.onMessageChange(fiso);
							}
						}
					}
					files.pause();
					un_counted_files.pause();
				}
			}).start();
	}
	
	// Scan for the files if only one of the given file.is needed set the argument one to true

	public static boolean scanFile(GROUPFiles fi,List<String> file){return scanFile(fi,file,false);}
	public static boolean scanFile(GROUPFiles fi,String file){
		List<String> f = new ArrayList<String>();
		f.add(file);
		return scanFile(fi,f,false);
	}
	public static boolean scanFile(GROUPFiles fi,List<String> files,boolean one)
	{
		boolean flag = false;
		for(String i : files)
		{
			File j = new File(i);
			if(j.exists() && j.isFile())
			{
				fi.add(j);
				flag = true;
				if(one) break;
			}
		}
		return flag;
	}

	// Scan for all files inside the given folder/folders

	public static boolean scanAll(GROUPFiles fi,List<String> u){return scanAll(fi,u,new ArrayList<String>());}
	public static boolean scanAll(GROUPFiles fi,String u){
		List<String> f = new ArrayList<String>();
		f.add(u);
		return scanAll(fi,f,new ArrayList<String>());
	}
	public static boolean scanAll(GROUPFiles fi,List<String> u,String ex){
		List<String> f = new ArrayList<String>();
		f.add(ex);
		return scanAll(fi,u,f);
	}
	public static boolean scanAll(GROUPFiles fi,List<String> urls,List<String> except)
	{
		
		boolean flag = false;
		for(int i = 0;i < urls.size();i++)
		{
			try{
				List<File> fosi = getAllFilesInside(new File(urls.get(i)),except);
				if(fosi.size() > 0){
					fi.add(fosi);
					flag = true;
				}
			}catch(Exception e)
			{

			}
		}
		
		return flag;
	}

	// Give a list of all files inside a folder

	public static List<File> getAllFilesInside(File f,List<String> exc) throws FileNotFoundException
	{

		List<File> fi = new ArrayList<File>();
		if(!f.exists() || !f.isDirectory()) throw new FileNotFoundException("The folder doesnt exists "+f.toString());
		File[] fis = f.listFiles();

		for(File i : fis)
		{
			if(exc.contains(i.getName()))
			{
				continue;
			}
			fi.add(i);
		}
		return fi;
	}
	
	/* 
		***********
		Listeners 
		***********
	*/
	// On Uncounted file added or removed liatenr
	private OnUnCountedFileChangeListener onuncountedfilechange = new OnUnCountedFileChangeListener(){
		@Override public void onUnCountedAdd(GROUPFiles f){}
		@Override public void onUnCountedRemove(GROUPFiles f){}
	};
	public void setOnUncountedFileChangeListener(OnUnCountedFileChangeListener l){onuncountedfilechange = l;}
	public interface OnUnCountedFileChangeListener
	{
		void onUnCountedAdd(GROUPFiles files);
		void onUnCountedRemove(GROUPFiles files);
	}
	
	// On a mesaage
	private OnHomeMessageListener onhomemessage = new OnHomeMessageListener(){
		@Override public void onMessageChange(GROUPFiles f){}
	};
	public void setOnHomeMessageListener(OnHomeMessageListener l){onhomemessage = l;}
	public interface OnHomeMessageListener
	{
		void onMessageChange(GROUPFiles f);
	}
	
	// On file added or removed liatenr
	private OnFileChangeListener onfilechange = new OnFileChangeListener(){
		@Override public void onAdd(GROUPFiles f){}
		@Override public void onRemove(GROUPFiles f){}
	};
	public void setOnFileChangeListener(OnFileChangeListener l){onfilechange = l;}
	public interface OnFileChangeListener
	{
		void onAdd(GROUPFiles files);
		void onRemove(GROUPFiles files);
	}
	// On storage scan initated and finished
	private OnScanListener onscan = new OnScanListener(){
		@Override public void onStart(GROUPFiles f){}
		@Override public void onEnd(GROUPFiles f){}
	};
	public void setOnScanListener(OnScanListener l){onscan = l;}
	public interface OnScanListener
	{
		void onStart(GROUPFiles files);
		void onEnd(GROUPFiles files);
	}
}
