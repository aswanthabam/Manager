package com.avc.manager;
import java.util.*;
import java.io.*;
import android.os.*;

public class GROUPFiles
{
	// static constant varuables
	
	public static final int SORT_BY_NAME_ASCENDING = 10;
	public static final int SORT_BY_NAME_DESCENDING = 11;
	public static final int SORT_BY_DATE_ASCENDING = 12;
	public static final int SORT_BY_DATE_DESCENDING = 13;
	
	public List<File> files = new ArrayList<File>();
	public String name = "",parent = "",sizeSTR = "",current = "";
	public long size = 0;
	public float sizeKB = 0,sizeMB = 0,sizeGB = 0;
	public Object linkedObject = null; // This object is used as an extension of thia class
	
	private Listener listener = new Listener(){
		@Override public void onPause(GROUPFiles f){}
		@Override public void onRemove(GROUPFiles f){}
		@Override public void onAdded(GROUPFiles f){}
	};
	
	public List<GROUPFiles> all = new ArrayList<GROUPFiles>();
	
	// Recreate the object by seting all varuabke empty this method is used to avoid copying of cintent
	// Because of using parser
	
	public void recreate()
	{
		// Info :: This method must wanted to be called to create a new instance of this class
		files = new ArrayList<File>();
		name = "";parent = "";sizeSTR = "";current = "";
		size = 0;
		sizeKB = 0;sizeMB = 0;sizeGB = 0;
	}
	
	// Class initialization with data
	// Files as list,size,name
	public GROUPFiles(List<File> f,long siz,String na)
	{
		files = f;
		size = siz;
		name = na;
		sizeKB = Utils.toKB(siz);
		sizeMB = Utils.toMB(siz);
		sizeGB = Utils.toGB(siz);
		sizeSTR = Utils.toSTRING(siz);
		all.add(this);
	}
	// Files as list,size no name
	public GROUPFiles(List<File> f,long siz)
	{
		files = f;
		size = siz;
		sizeKB = Utils.toKB(siz);
		sizeMB = Utils.toMB(siz);
		sizeGB = Utils.toGB(siz);
		sizeSTR = Utils.toSTRING(siz);
	}
	// Files as list no size and name
	public GROUPFiles(List<File> f)
	{
		files = f;
		size = Utils.getSIZE(f);
		sizeKB = Utils.toKB(size);
		sizeMB = Utils.toMB(size);
		sizeGB = Utils.toGB(size);
		sizeSTR = Utils.toSTRING(size);
	}
	// No data
	public GROUPFiles(){}
	
	// set linkage or extenaion obhect
		// This object is used as an extension of this class this object may contain more infirmation about this groupfile
	public void setLinkage(Object m)
	{
		linkedObject = m;
	}
	
	// set file name

	public void setName(String na){name = na;}
	
	// set file parent
	
	public void setParent(String pa){parent = pa;}
	
	// Set current file
	
	public void setCurrent(String cur){current = cur;}
	
	// Add files 
	// Files as list
	public void add(List<File> m)
	{
		for(int i = 0;i < m.size();i++) if(!files.contains(m.get(i))) files.add(m.get(i));
		size = Utils.getSIZE(files);
		sizeKB = Utils.toKB(size);
		sizeMB = Utils.toMB(size);
		sizeGB = Utils.toGB(size);
		sizeSTR = Utils.toSTRING(size);
		if(listener != null) listener.onAdded(this);
	}
	// Single file
	public void add(File m)
	{
		if(!files.contains(m)) files.add(m);
		size += Utils.getSIZE(m);
		sizeKB = Utils.toKB(size);
		sizeMB = Utils.toMB(size);
		sizeGB = Utils.toGB(size);
		sizeSTR = Utils.toSTRING(size);
		if(listener != null) listener.onAdded(this);
	}
	
	// Adding grouped files
	
	public void add(GROUPFiles m)
	{
		add(m.files);
		if(!all.contains(m)) all.add(m);
		if(listener != null) listener.onAdded(this);
	}
	
	// Remove files 
	// Files as list
	public void remove(List<File> m)
	{
		for(int i = 0;i < m.size();i++) if(files.contains(m.get(i))) files.remove(m.get(i));
		size = Utils.getSIZE(files);
		sizeKB = Utils.toKB(size);
		sizeMB = Utils.toMB(size);
		sizeGB = Utils.toGB(size);
		sizeSTR = Utils.toSTRING(size);
		if(listener != null) listener.onRemove(this);
	}
	// Single file
	public void remove(File m)
	{
		if(files.contains(m)) files.remove(m);
		size -= Utils.getSIZE(m);
		sizeKB = Utils.toKB(size);
		sizeMB = Utils.toMB(size);
		sizeGB = Utils.toGB(size);
		sizeSTR = Utils.toSTRING(size);
		if(listener != null) listener.onRemove(this);
	}
	
	// grouped files
	
	public void remove(GROUPFiles m)
	{
		remove(m.files);
		if(all.contains(m)) all.remove(m);
		if(listener != null) listener.onRemove(this);
	}
	// Listener associated function to temperorily pause file accepting
	
	public void pause()
	{
		if(listener != null) listener.onPause(this);
	}
	
	// Interface
	// Listener for adding and removing changes
	public interface Listener
	{
		void onAdded(GROUPFiles f);
		void onRemove(GROUPFiles f);
		void onPause(GROUPFiles f);
	}
	// function fir setting listener
	public void setListener(Listener l)
	{
		listener = l;
	}
	
	public void sortFiles(int sort_by)
	{
		Comparator com = new Comparator<File>(){
			@Override public int compare(File f1,File f2)
			{
				// Oldest on top
				return Long.compare(f1.lastModified(),f2.lastModified());
			}
		};
		switch(sort_by){
			case SORT_BY_NAME_ASCENDING:
				
				break;
			case SORT_BY_NAME_DESCENDING:
				
				break;
			case SORT_BY_DATE_ASCENDING:
				com = new Comparator<File>(){
					@Override public int compare(File f1,File f2)
					{
						// Oldest on top
						return Long.compare(f1.lastModified(),f2.lastModified());
					}
				};
				break;
			case SORT_BY_DATE_DESCENDING:
				com = new Comparator<File>(){
					@Override public int compare(File f1,File f2)
					{
						// Newest on top
						return Long.compare(f2.lastModified(),f1.lastModified());
					}
				};
				break;
		}
		File[] fil = new File[files.size()];
		fil = files.toArray(fil);
		Arrays.sort(fil,com);
		List<File> fis = new ArrayList<File>();
		for(File f : fil) fis.add(f);
		files = fis;
	}
}
