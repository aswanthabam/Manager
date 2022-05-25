package com.avc.manager.Res;
import android.widget.*;
import androidx.appcompat.app.*;
import android.view.*;
import android.os.*;
import android.content.*;
import android.util.*;
import com.avc.manager.*;
import android.app.*;
import java.util.*;

public class HoldebleObject extends LinearLayout
{
	public AppCompatActivity activity;
	private OnClickListener onclick;
	private OnHoldListener onhold;
	private Context con;
	private long downTime = 0;
	private boolean holded = false;
	private List<View> parents = new ArrayList<View>();
	public HoldebleObject(Context a,AttributeSet attrs)
	{
		super(a,attrs);
		con = a;
		activity = (AppCompatActivity) con;
	}
	public void setToucher()
	{
		for(int i = 0;i < parents.size();i++)
		{
			View v = parents.get(i);
			v.setOnTouchListener(new View.OnTouchListener()
			{
				@Override public boolean onTouch(View v,MotionEvent m)
				{
					int s = m.getAction();
					if(s == m.ACTION_UP)
					{
						onhold.onHoldRemoved();
					}
					return true;
				}
			});
		}
	}
	public void addParent(View v)
	{
		parents.add(v);
	}
	public void addParent(List<View> v)
	{
		for(int i = 0;i < v.size();i++) parents.add(v.get(i));
	}
	public void addParent(View[] v)
	{
		for(View i: v) parents.add(i);
	}
	public void setInitListener()
	{
		onclick = new OnClickListener()
		{
			@Override public void onClick()
			{

			}
		};
		onhold = new OnHoldListener()
		{
			@Override public void onHoldRemoved()
			{

			}
			@Override public void onHold(long time)
			{

			}
		};
	}
	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		final int action = event.getAction();
		holded = false;
		if(action == event.ACTION_DOWN)
		{
			long time = SystemClock.uptimeMillis() - event.getDownTime();
			//onhold.onHold(time);
			downTime = event.getDownTime();
			new Thread(new Runnable()
			{
				@Override public void run()
				{
					boolean a = true;
					while(downTime + 500 >= SystemClock.uptimeMillis())
					{
						if(event.getAction() != event.ACTION_DOWN)
						{
							a = false;
							break;
						}else
						{
							
						}
					}
					if(a)
					{
						holded = true;
						activity.runOnUiThread(new Runnable(){
							@Override public void run()
							{
								onhold.onHold(500);
							}
						});
						
					}else holded = false;
				}
			}).start();
			
		}
		else if(action == event.ACTION_UP)
		{
			long time = SystemClock.uptimeMillis() - event.getDownTime();
			if(holded)
			{
				onhold.onHoldRemoved();
				holded = false;
			}
			else if(time < 300)
			{
				onclick.onClick();
			}
			
		}
		return true;
	}
	
	public void setOnHoldListener(OnHoldListener l)
	{
		onhold = l;
	}
	
	public void setOnClickListener(OnClickListener l)
	{
		onclick = l;
	}
	
	public interface OnHoldListener
	{
		void onHold(long time);
		void onHoldRemoved();
	}
	
	public interface OnClickListener
	{
		void onClick();
	}
}
