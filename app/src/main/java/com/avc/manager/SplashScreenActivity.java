package com.avc.manager;
import androidx.appcompat.app.*;
import android.os.*;
import android.content.*;

public class SplashScreenActivity extends AppCompatActivity
{
	public AppCompatActivity a;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		a = this;
		/*new Thread(new Runnable()
		{
			@Override public void run()
			{
				try{Thread.sleep(1000);}catch(Exception e){}
				Intent i = new Intent(a,MainActivity.class);
				startActivity(i);
				finish();
			}
		}).start();*/
	}
	
}
