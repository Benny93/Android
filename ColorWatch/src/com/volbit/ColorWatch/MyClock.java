package com.volbit.ColorWatch;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Benny on 25.12.14.
 */


public class MyClock extends TextView {

    Calendar mCalendar;
    /*
    private final static String m12 = "h:mm:ss \n" +
            " #hhmmss";
    */
    private final static String m24 = "k:mm:ss \n" +
            " #kmmss";
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;

    public MyClock(Context context) {
        super(context);
        initClock(context);
    }

    public MyClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        Resources r = context.getResources();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar));
                //setBackgroundColor(Color.parseColor((String) DateFormat.format("#hhmmss", mCalendar)));
                setBackgroundResource(R.drawable.rounded_edittext);
                GradientDrawable drawable = (GradientDrawable) getBackground();
                drawable.setColor(Color.parseColor((String) DateFormat.format("#hhmmss", mCalendar)));
                setTextColor(Color.parseColor("#ffffff"));
                setPadding(100,100,100,100);
                setGravity(Gravity.CENTER);


                //update widget

                //ComponentName thisWidget = new ComponentName( getContext(), AWProvider );
                //AppWidgetManager.getInstance(getContext()).updateAppWidget( thisWidget, rempoteViews );

                //end update
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        /*
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
        */
        mFormat = m24;
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

}