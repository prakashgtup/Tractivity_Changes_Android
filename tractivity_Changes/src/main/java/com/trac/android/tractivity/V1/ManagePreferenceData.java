/**
 * 
 */
package com.trac.android.tractivity.V1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Helper class to save the status of sync alarm
 *
 */
public class ManagePreferenceData {

	private Context mContext;

	public ManagePreferenceData(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	// To get Preference data

	public boolean isSyncAlarmSet(){
		// To save whether alarm set or cancelled in preference file.... useful to set alarm when app opened from background
		SharedPreferences mAlarmPreference = mContext.getSharedPreferences(mContext.getResources().getString(R.string.sync_preference), Context.MODE_PRIVATE);
		return mAlarmPreference.getBoolean(mContext.getResources().getString(R.string.is_alarm_set), false);
	}


	// To update preference data
	public void updatePreferenceFile(boolean isAlarmSet){
		// To save whether alarm set or cancelled in preference file.... useful to set alarm when app opened from background
		SharedPreferences mAlarmPreference = mContext.getSharedPreferences(mContext.getResources().getString(R.string.sync_preference), Context.MODE_PRIVATE);
		Editor mPreferenceEditor = mAlarmPreference.edit();
		mPreferenceEditor.putBoolean(mContext.getResources().getString(R.string.is_alarm_set), isAlarmSet);
		mPreferenceEditor.commit();
	}

}
