package com.trac.android.tractivity.backgroundtask;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.trac.android.tractivity.V1.ManagePreferenceData;

/**
 *Helper class to schedule background sync
 *
 */

public class ScheduleBackgroundTask {

	private Context mContext;
	private PendingIntent mPendingIntent;
	private AlarmManager mRecurringAlarm;
	private ManagePreferenceData mPreferenceData;

	public ScheduleBackgroundTask(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mPreferenceData = new ManagePreferenceData(mContext);
	}


	// To set alarm which triggers every 5 seconds to sync data to server
	public void setRecurringAlarm(){
		try{
			mRecurringAlarm = ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE));
			Intent serviceIntent = new Intent(mContext, TractivitySyncService.class);
			mPendingIntent = PendingIntent.getService(mContext, 100, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRecurringAlarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 5000, mPendingIntent);
			mPreferenceData.updatePreferenceFile(true);
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	// To cancel the recurring alarm and also the background service if running
	public void cancelAlarm(){
		Intent serviceIntent = new Intent(mContext, TractivitySyncService.class);
		mPendingIntent = PendingIntent.getService(mContext, 100, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		mRecurringAlarm = ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE));
		mRecurringAlarm.cancel(mPendingIntent);
		mPreferenceData.updatePreferenceFile(false);
		if (isMyServiceRunning(mContext)) // to stop service
		{
			mContext.stopService(serviceIntent);
		}
	}

	//To query OS to check whether notification service running
	private boolean isMyServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (TractivitySyncService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
