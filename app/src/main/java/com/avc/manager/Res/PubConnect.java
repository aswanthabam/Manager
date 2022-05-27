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

public class PubConnect
{
	AppCompatActivity activity;
	String app_id,version_id,app_pass;
	private OnUpdateResponceListener onupdate = new OnUpdateResponceListener(){

		@Override
		public void onUpdateResponce(HttpURLConnection conn, String out, JSONObject obj,UpdateConnect up)
		{
			// TODO: Implement this method
		}
	};
	public PubConnect(AppCompatActivity a,String id,String pass,String vid)
	{
		activity = a; app_id = id; version_id = vid; app_pass = pass;
	}
	
	public void checkForUpdates()
	{
		new Thread(new UpdateConnect()).start();
	}
	
	public class UpdateConnect implements Runnable
	{
		public String TAG = "UPDATE_CHECK_URL_CONNECTION";
		public HttpURLConnection conn;
		public UpdateConnect This = this;
		public UpdateDialog getDialog(Me m)
		{
			return new UpdateDialog(activity,m);
		}
		@Override
		public void run()
		{
			try
			{
				List<NameValuePair> p = new ArrayList<NameValuePair>();
				p.add(new BasicNameValuePair("connecting_as","App"));
				p.add(new BasicNameValuePair("connecting_for","Updates"));
				p.add(new BasicNameValuePair("common_app_id",app_id));
				p.add(new BasicNameValuePair("version",version_id));
				p.add(new BasicNameValuePair("app_pass",app_pass));
				
				URL url = new URL("http://localhost:8000/appstore-api/api/?"+Utils.QUERYEncode(p));
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				final String out = Utils.reader(activity,conn);
				final JSONObject obj = Utils.stringToJSON(out);
				
				activity.runOnUiThread(new Runnable()
				{
					@Override public void run()
					{
						try{
							onupdate.onUpdateResponce(conn,out,obj,This);
						}catch(Exception e){
							Log.e(TAG,"Error in reading responce");
						}
					}
				});
			}catch(Exception e){
				Log.e(TAG,"Error in connecting due to ("+e.toString()+")");
			}
		}
		
		public class UpdateDialog extends Dialog
		{
			AppCompatActivity activity;
			TextView title,sub,des;
			Button btn1,btn2;
			public Me me = new Me();
			public UpdateDialog(AppCompatActivity a,Me m)
			{
				super(a);
				activity = a; me = m;
			}

			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				// TODO: Implement this method
				super.onCreate(savedInstanceState);
				setContentView(R.layout.self_update_app_dailog);
				
				getWindow().setBackgroundDrawableResource(R.drawable.light_border_background);
				title = findViewById(R.id.self_update_app_dailog_Title);
				 /*sub = findViewById(R.id.update_app_dailogSubTitle);
				 des = findViewById(R.id.update_app_dailogDescription);
				 */btn1 = findViewById(R.id.update_app_dailogBtn1);
				// btn2 = findViewById(R.id.update_app_dailogBtn2);

				 title.setText(me.update.title);
				 /*sub.setText(me.update.sub_title);
				 des.setText(me.update.description);
*/
				 btn1.setOnClickListener(new View.OnClickListener()
				 {
				 @Override public void onClick(View v)
				 {
				 dismiss();
				 }
				 });

				 btn2.setOnClickListener(new View.OnClickListener()
				 {
				 @Override public void onClick(View v)
				 {
				 dismiss();
				 }
				 });
			}
	
		}
	}
	
	public void setOnUpdateResponceListener(OnUpdateResponceListener r){
		onupdate = r;
	}
	public interface OnUpdateResponceListener
	{
		void onUpdateResponce(HttpURLConnection conn,String out,JSONObject obj,PubConnect.UpdateConnect up);
	}
}
