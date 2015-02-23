package com.volbit.ColorWatch;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import java.util.Date;

/**
 * Created by Benny on 02.01.15.
 */
public class clockservice extends Service {
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        buildUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate()
    {
        String lastUpdated = DateFormat.format("k:mm:ss", new Date()).toString();
        String date = DateFormat.format("EEE dd.MM.yy", new Date()).toString();
        String timeColor;

        if ( Integer.parseInt(DateFormat.format("k", new Date()).toString()) >= 10) {
             timeColor = DateFormat.format("#kmmss", new Date()).toString();
        }else{
            //füge null hinzu, da sonst zu kurz
            timeColor = DateFormat.format("#0kmmss", new Date()).toString();
        }
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.clockwidget);

        view.setTextViewText(R.id.colorclock, lastUpdated);
        view.setTextViewText(R.id.hexcodelabel,timeColor);
        view.setTextViewText(R.id.datelabel,date);

        view.setInt(R.id.colorclock, "setBackgroundColor",
              android.graphics.Color.parseColor(timeColor));

        //view.setInt(R.id.clockbackground,"setBackgroundColor",android.graphics.Color.parseColor(timeColor));

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, AWProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

}
