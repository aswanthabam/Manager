package com.avc.manager.Adapter;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
import java.io.*;
import android.widget.*;
import com.avc.manager.*;
import java.text.*;

public class FileRVAdapter extends RecyclerView.Adapter<FileRVAdapter.ViewHolder>
{
	
	private AppCompatActivity activity;
	private List<GROUPFiles> files;
	public DecimalFormat fo = new DecimalFormat("#.##");
	
	public FileRVAdapter(AppCompatActivity a,List<GROUPFiles> f)
	{
		activity = a; files = f;
	}
	
	// ViewHolder class
	
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		ImageView img;
		TextView head,sub;
		
		public ViewHolder(View v)
		{
			super(v);
			
			// Initializing views
			
			img = v.findViewById(com.avc.manager.R.id.file_rc_viewImageView);
			head = v.findViewById(com.avc.manager.R.id.file_rc_viewHead);
			sub = v.findViewById(com.avc.manager.R.id.file_rc_viewSub);
			
		}
	}
	
	// Onbind view holder
	
	@Override
	public void onBindViewHolder(ViewHolder holder,int p2)
	{
		GROUPFiles file = files.get(p2);
		holder.head.setText(file.name);
		holder.sub.setText(file.sizeSTR+" | " + file.parent);
	}
	
	// Oncreate view holder
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// Inflate file rc view layiut
		View v = LayoutInflater.from(activity).inflate(R.layout.file_rc_view,p1,false);
		
		return new ViewHolder(v);
	}
	
	// Get item view type
	@Override
	public int getItemViewType(int position)
	{
		return position;
	}
	// get item id
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	// Get item count
	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return files.size();
	}
	
}
