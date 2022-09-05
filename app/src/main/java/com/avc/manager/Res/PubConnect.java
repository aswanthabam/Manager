package com.avc.manager.Res;

import java.net.*;
import android.util.*;
import org.apache.http.*;
import java.util.*;
import org.apache.http.message.*;
import com.avc.manager.*;
import java.io.*;
import org.json.*;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.support.v7.app.*;
import android.content.*;
import android.net.*;
import android.database.*;
import java.text.*;
import android.content.res.*;
import android.graphics.*;
import android.support.v4.content.*;

/*
 Class for connection to the server and related works
*/

public class PubConnect
{
	AppCompatActivity activity;
	String app_id,version_id,app_pass;
	private OnUpdateResponceListener onupdate = new OnUpdateResponceListener(){

		@Override
		public void onUpdateResponce(JSONObject obj,UpdateConnect up,boolean a)
		{
			// TODO: Implement this method
		}
	};
	public PubConnect(AppCompatActivity a,String id,String pass,String vid)
	{
		activity = a; app_id = id; version_id = vid; app_pass = pass;
	}
	
	// starts checking for upadets
	
	public void checkForUpdates()
	{
		new Thread(new UpdateConnect()).start();
	}
	
	// Class for checking for update and related works
	
	public class UpdateConnect implements Runnable
	{
		public String TAG = "UPDATE_CHECK_URL_CONNECTION";
		public HttpURLConnection conn;
		public UpdateConnect This = this;
		public UpdateDialog getDialog()
		{
			return new UpdateDialog(activity);
		}
		@Override
		public void run()
		{
			try
			{
				// Add initial values to send as get request
				List<NameValuePair> p = new ArrayList<NameValuePair>();
				p.add(new BasicNameValuePair("connecting_as","App"));
				p.add(new BasicNameValuePair("connecting_for","Updates"));
				p.add(new BasicNameValuePair("common_app_id",app_id));
				p.add(new BasicNameValuePair("version",version_id));
				p.add(new BasicNameValuePair("app_pass",app_pass));
				// Parse the url and get the output
				URL url = new URL(Me.host+"appstore-api/api/?"+Utils.QUERYEncode(p));
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				final String out = Utils.reader(activity,conn);
				final JSONObject obj = Utils.stringToJSON(out);// convert the output to json
				
				activity.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						try{
							onupdate.onUpdateResponce(obj,This,true); // callback the listenr
						}catch(Exception e){
							Log.e(TAG,"Error in reading responce");
						}
					}
				});
			}catch(Exception e){
				onupdate.onUpdateResponce(new JSONObject(),This,false);
				Log.e(TAG,"Error in connecting due to ("+e.toString()+")");
			}
		}
		
		// Class extends dialog
			// The dialog which wants to be shown if update availabke
		
		public class UpdateDialog extends Dialog
		{
			public AppCompatActivity activity;
			public TextView title,sub,des,pro_text;
			public Button btn1,btn2;
			public LinearLayout cont;
			public ProgressBar bar;
			
			public UpdateDialog(AppCompatActivity a)
			{
				super(a);
				activity = a; 
			}

			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				// TODO: Implement this method
				super.onCreate(savedInstanceState);
				setContentView(R.layout.self_update_app_dailog);
				
				getWindow().setBackgroundDrawableResource(R.drawable.light_border_background);
				
				// Initialize views
				
				title = findViewById(R.id.self_update_app_dailog_Title);
				sub = findViewById(R.id.self_update_app_dailog_SubTitle);
				des = findViewById(R.id.self_update_app_dailog_Description);
				btn1 = findViewById(R.id.self_update_app_dailogButton1);
				btn2 = findViewById(R.id.self_update_app_dailogButton2);
				
				cont = findViewById(R.id.self_update_app_dailogLinearLayout); // View conatining tgw download progresa bar
				bar = findViewById(R.id.self_update_app_dailogProgressBar);
				pro_text = findViewById(R.id.self_update_app_dailogTopProgressText);
				
				cont.setVisibility(View.GONE);
				
				title.setText(Me.update.title);
				sub.setText(Me.update.sub_title);
				des.setText(Me.update.description);
				
				// If the file is already downloaded and found inside internal stirage
				// Direct setuo the install button
				
				
				btn1.setOnClickListener(new View.OnClickListener()
				{
					@Override public void onClick(View v)
					{
						if(Me.update.must_update) activity.finish();
						else{
							dismiss();
						}
					}
				});

				btn2.setOnClickListener(new View.OnClickListener()
				{
					@Override public void onClick(View v)
				 	{
						try{
							// Set the lrogress bar visible
							cont.setVisibility(View.VISIBLE);
							// Setup download manager
							final DownloadManager.Request d = new DownloadManager.Request(Uri.parse(Me.update.download_link));
							d.setDescription("Version "+Me.update.version);
							d.setTitle("Downloading update ("+Me.update.file_name+")");
							
							// setup the file and folder to which the update want to be saved
							
							File f = new File(Environment.getExternalStorageDirectory()+"/AVC Manager/Updates");
							if(!f.exists()) f.mkdirs();
							for(File df : f.listFiles()) df.delete();
							final File fi = new File(f.getAbsolutePath()+"/"+Me.update.file_name);
							// Decimal point to 2decimal for download percentage indication
							final DecimalFormat fo = new DecimalFormat("#.##");
							
							d.setDestinationUri(Uri.fromFile(fi));
							// Start a threaf and a loop which continuosly note tge progress of download
							new Thread(new Runnable()
							{
								@Override public void run()
								{
									final DownloadManager m = (DownloadManager) activity.getSystemService(activity.DOWNLOAD_SERVICE);
									final long requestId = m.enqueue(d);
									
									DownloadManager.Query qu = new DownloadManager.Query();
									qu.setFilterById(requestId);
									boolean downloading = true;
									
									while(downloading){
										Cursor cu = m.query(qu);
										cu.moveToFirst();
										int byd = cu.getInt(cu.getColumnIndex(m.COLUMN_BYTES_DOWNLOADED_SO_FAR));
										int byt = cu.getInt(cu.getColumnIndex(m.COLUMN_TOTAL_SIZE_BYTES));
										int st = cu.getInt(cu.getColumnIndex(DownloadManager.COLUMN_STATUS));
										
										final double dl_progress = ((double)byd / (double)byt) * 100;

										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												// Set the current percentage to progress bar and textvuew
												bar.setProgress((int) dl_progress);
												pro_text.setText("Downloading... ("+fo.format(dl_progress)+"%)");
											}
										});
										if (st == DownloadManager.STATUS_SUCCESSFUL) {
											// The download is success full
											downloading = false;
											activity.runOnUiThread(new Runnable(){
												@Override public void run()
												{
													bar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
													pro_text.setText("Download Successfull! Install updates");
													pro_text.setTextColor(Color.GREEN);
													setButtonInstall(fi);
												}
											});
											break;
										}else if(st == DownloadManager.STATUS_FAILED)
										{
											// Download failed
											activity.runOnUiThread(new Runnable(){
												@Override public void run()
												{
													//bar.setBackgroundColor(R.color.colorControlNormal);
													bar.setProgressTintList(ColorStateList.valueOf(Color.RED));
													pro_text.setText("Download failed!");
													pro_text.setTextColor(Color.RED);
												}
											});
											break;
										}
									}
								}
							}).start();
							
						}catch(Exception e)
						{
							cont.setVisibility(View.GONE);
							Utils.toast(activity,"Unable to download update "+e.toString());
						}
						
				 	}
					
				});
				if(!Me.update.must_update){
					btn2.setText("Later");
					btn2.setOnClickListener(new View.OnClickListener()
					{
						@Override public void onClick(View v)
						{
							dismiss();
						}
					});
				}
				// If the app must want to be updated the app will close if dissmissed
				setOnDismissListener(new OnDismissListener(){
					@Override public void onDismiss(DialogInterface d){
						if(Me.update.must_update) activity.finish();
					}
				});
				// if the fike.is found in internal storage 
				// Install button is shown
				File f = new File(Environment.getExternalStorageDirectory()+"/AVC Manager/Updates/"+Me.update.file_name);
				if(f.exists())
				{
					setButtonInstall(f); // Call function
				}
			}
			
			// Function to shiw install.button and set click lister to tge button to open 
				// Install window
			
			public void setButtonInstall(final File f)
			{
				btn2.setText("Install");
				btn2.setOnClickListener(new View.OnClickListener(){
					@Override public void onClick(View v)
					{
						Uri uri = FileProvider.getUriForFile(activity,"com.avc.manager.provider",f);
						String type = Utils.getMimeType(f.getAbsolutePath());
						Intent install = new Intent(Intent.ACTION_VIEW);
						install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						install.setDataAndType(uri,type);
						activity.startActivity(install);
						
						//Utils.shareFile(activity,"Open apk using","Install updates android",uri.getPath(),man.getMimeTypeForDownloadedFile(id));
					}
				});
			}
		}
	}
	// Listers
	public void setOnUpdateResponceListener(OnUpdateResponceListener r){
		onupdate = r;
	}
	public interface OnUpdateResponceListener
	{
		void onUpdateResponce(JSONObject obj,PubConnect.UpdateConnect up,boolean is_connected);
	}
}
