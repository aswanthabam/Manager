package com.avc.manager;
import java.util.*;
import java.io.*;
import android.os.*;

public class GROUPFiles
{
	public List<File> files = new ArrayList<File>();
	public String name = "",parent = "",sizeSTR = "",current = "";
	public long size = 0;
	public float sizeKB = 0,sizeMB = 0,sizeGB = 0;
	private Listener listener;
	public List<GROUPFiles> all = new ArrayList<GROUPFiles>();
	
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
	
}
