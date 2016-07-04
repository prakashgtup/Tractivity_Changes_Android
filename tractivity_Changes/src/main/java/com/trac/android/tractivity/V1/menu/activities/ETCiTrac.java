package com.trac.android.tractivity.V1.menu.activities;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.trac.android.tractivity.V1.ManagePreferenceData;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;

public class ETCiTrac extends Activity implements OnClickListener {

	private EditText mHour;
	private TextView mMainTitle, mTitleBarLeftMenu, mTitleBarRightMenu;
	public Calendar cal2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.etcitrac);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.etc_header));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.cancel));
		mTitleBarRightMenu.setText(getResources().getString(R.string.accept));
		mTitleBarRightMenu.setTypeface(null, Typeface.BOLD);
		mTitleBarLeftMenu.setOnClickListener(this);
		mTitleBarRightMenu.setOnClickListener(this);

		

		mHour = (EditText) findViewById(R.id.etc_hour);


		SharedPreferences customSharedPreference = getSharedPreferences(
				"iTracSharedPrefs", Activity.MODE_PRIVATE);

		// Check if ETC entry is true, if false disable the entry
		if (customSharedPreference.getBoolean(getResources().getString(R.string.etc), false) == true) {
			if(getIntent().hasExtra(getResources().getString(R.string.etc)))
				mHour.setText(getIntent().getExtras().getString(getResources().getString(R.string.etc)));

		} else {
			mHour.setEnabled(false);
			mHour.setClickable(false);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!new ManagePreferenceData(this).isSyncAlarmSet()) // IF not set, set
			// sync alarm
			new ScheduleBackgroundTask(this).setRecurringAlarm();
		else
			Log.v("Alarm ", "Already set");
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint", "Called");
		new ScheduleBackgroundTask(this).cancelAlarm();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.leftmenuID:
			finish();
			break;
		case R.id.rightmenuID:
			if(mHour.getText().toString().trim().length() != 0){
				Intent estimatedTimeHostIntent = new Intent();
				estimatedTimeHostIntent.putExtra(getResources().getString(R.string.time_left), mHour.getText().toString());
				setResult(RESULT_OK,estimatedTimeHostIntent);
				finish();
			}
			break;

		default:
			break;
		}
	}

}
