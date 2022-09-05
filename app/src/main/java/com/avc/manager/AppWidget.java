package com.avc.manager;
import android.appwidget.*;
import android.content.*;
import android.widget.*;
import com.avc.manager.Res.*;
import android.app.*;
import android.view.*;

public class AppWidget extends AppWidgetProvider
{
	
	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds)
	{
		// TODO: Implement this method
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		/*Manager.space_cleaner = new SpaceCleaner();
		Manager.space_cleaner.storageScan();
		Manager.space_cleaner.setOnScanListener(new SpaceCleaner.OnScanListener()
		{
			@Override public void onStart(GROUPFiles f)
			{
				
			}
			@Override public void onEnd(GROUPFiles f)
			{
				for (int appWidgetId : appWidgetIds) {
					updateAppWidget(context, appWidgetManager, appWidgetId);
				} 
			}
		});
		*/
		
	}

	@Override
	public void onEnabled(Context context)
	{
		// TODO: Implement this method
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context)
	{
		// TODO: Implement this method
		super.onDisabled(context);
	}
	
	private void updateAppWidget(Context context,AppWidgetManager man,int widId)
	{
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget); 
		RemoteViews v2 = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text, Manager.space_cleaner.files.sizeSTR + " Storage can be freed"); 
		views.addView(R.id.app_widgetButton,v2);
		man.updateAppWidget(widId,views);
	}
}

