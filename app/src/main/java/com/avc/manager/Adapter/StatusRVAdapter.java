package com.avc.manager.Adapter;

import android.view.*;
import android.widget.*;
import com.avc.manager.*;
import android.graphics.*;
import java.io.*;
import android.media.*;
import android.provider.*;
import android.graphics.drawable.*;
import java.util.*;
import android.os.*;
import android.view.View.*;
import com.avc.manager.Res.*;
import android.util.*;
import android.support.v7.widget.*;
import android.support.v7.app.*;
import android.support.design.widget.*;
/*
 Adapter for Recycler View showing all statuses
*/
public class StatusRVAdapter extends RecyclerView.Adapter<StatusRVAdapter.ViewHolder>
{
	
	public GROUPFiles files;
	private AppCompatActivity activity;
	private ImageVideoPreviewDialog di;
	public boolean dialog = false;
	public CoordinatorLayout parent;
	public Map<File,Bitmap> thumbnails = new ArrayMap<File,Bitmap>();
	// Initialize listener
	private OnDialog ondialog = new OnDialog()
	{
		@Override
		public void onDialog(ImageVideoPreviewDialog dialog)
		{
			// TODO: Implement this method
		}
	};
	
	private OnBind onbind = new OnBind()
	{
		@Override public void onBind(ViewHolder h)
		{
			
		}
	};
	
	public StatusRVAdapter(AppCompatActivity a,GROUPFiles f)
	{
		files = f;
		activity = a;
	}
	// Function ti get thumbnail from.tge Map
	public Bitmap getThumbnail(int i)
	{
		File f = files.files.get(i);
		if(thumbnails.get(f) != null) return thumbnails.get(f);
		return thumbnails.get(f);
	}
	// Get thumbnail of a file from previously collected mao
	
	public Bitmap getThumbnail(File f)
	{
		if(thumbnails.get(f) != null) return thumbnails.get(f);
		return thumbnails.get(f);
	}
	
	// View holder
	
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public HoldebleObject main;
		public PreviewLayout img;
		public int ad_pos;
		public File fi;
		public ImageView v1;
		public VideoView v2;
		public RelativeLayout cont;
		
		ViewHolder(View v)
		{
			super(v);
			
			// Initialize views
			
			main = v.findViewById(com.avc.manager.R.id.status_rv_viewLinearLayout);
			v1 = v.findViewById(R.id.preview_layoutImageView);
			v2 = v.findViewById(R.id.preview_layoutVideoView);
			cont = v.findViewById(R.id.status_rv_viewRelativeLayout);
			
			main.setClickable(true);
			
			if(parent != null) main.addParent(parent);
			
			main.setOnClickListener(new HoldebleObject.OnClickListener()
			{
				@Override public void onClick()
				{
					// Show dialog when clicked
					showDialog(fi);
				}
			});
			
			main.setOnHoldListener(new HoldebleObject.OnHoldListener()
			{
				@Override public void onHold(long time)
				{
					showDialog(fi);
					main.addParent(di.main);
				}
				@Override public void onHoldRemoved()
				{
					hideDialog();
				}
			});
			
		}
	}
	// ViewHolder creaded
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		View v = LayoutInflater.from(p1.getContext()).inflate(com.avc.manager.R.layout.status_rv_view,p1,false);
		ViewHolder h = new ViewHolder(v);
		
		return new ViewHolder(v);
	}
	// Binded view holder
	@Override
	public void onBindViewHolder(final ViewHolder p1, final int p2)
	{
		p1.ad_pos = p2;
		p1.fi = files.files.get(p2);
		setThumbnail(p1,p2); // Set the thumbnails 
		
	}
	
	// Function to show the imagevideoprevuew dialog for a file
	
	public void  showDialog(File f)
	{
		di = new ImageVideoPreviewDialog(activity,f);
		di.show();
		ondialog.onDialog(di);
	}
	
	// hide the dialog
	
	public void  hideDialog()
	{
		di.dismiss();
	}
	
	// Set tge thumbnail for the image
	
	public void setThumbnail(ViewHolder h,int j)
	{
		final ViewHolder holder = h;
		final int i = j;
		Thread d = new Thread(new Runnable()
		{
			@Override public void run()
			{
				activity.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						// Use prevuew layout to previee tbe files
						holder.img = new PreviewLayout(activity,holder.v1,holder.v2,PreviewLayout.CENTER_CROP,PreviewLayout.MUTE,true);
						holder.img.setParent(holder.cont);
						holder.img.preview(files.files.get(i));
						onbind.onBind(holder); // callback onbind
					}
				});
				
			}
		});
		d.start();
	}
	
	// Get thumbnails of all files and retur  a map of file and bitmap
	
	public Map<File,Bitmap> getThumbnails()
	{
		
		for(int i = 0;i < files.files.size();i++)
		{
			File f = files.files.get(i);
			String m = Utils.getMimeType(f.getAbsolutePath());
			if(m == null) return null;
			Bitmap Thumb = null;
			if(m.startsWith("image/"))
			{
				Thumb = ThumbnailUtils.createImageThumbnail(f.getAbsolutePath(),MediaStore.Images.Thumbnails.MINI_KIND);
			}
			else if(m.startsWith("video/"))
			{
				Thumb = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(),MediaStore.Video.Thumbnails.MINI_KIND);
			}else{
				
			}
			thumbnails.put(f,Thumb);
		}
		return thumbnails;
	}
	// Get item view type
	@Override
	public int getItemViewType(int position)
	{
		return position;
	}
	// Get item count
	@Override
	public int getItemCount()
	{
		return files.files.size();
	}
	// get item id
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	// Set on dialog listener
	public void setOnDialogListener(OnDialog d)
	{
		ondialog = d;
	}
	// OnDualov
	public interface OnDialog
	{
		void onDialog(ImageVideoPreviewDialog dialog);
	}
	// Set on bind lister
	public void setOnBindListener(OnBind b)
	{
		onbind = b;
	}
	// Onbind
	public interface OnBind
	{
		void onBind(ViewHolder holder);
	}
	
}
