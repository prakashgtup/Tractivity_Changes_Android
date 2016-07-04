package com.trac.android.tractivity.V1.menu.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
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

public class NoteiTrac extends Activity implements OnClickListener {

	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu;
	private EditText mNote;
	private SimpleDateFormat dateFormat;
	private HashMap<String, String> mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.noteitrac);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.note));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.cancel));
		mTitleBarRightMenu.setText(getResources().getString(R.string.accept));
		mTitleBarRightMenu.setTypeface(null, Typeface.BOLD);
		mTitleBarLeftMenu.setOnClickListener(this);
		mTitleBarRightMenu.setOnClickListener(this);
		mNote = (EditText)findViewById(R.id.note);
		dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

		if(getIntent().hasExtra(getResources().getString(R.string.note))){
			mNote.setText(getIntent().getExtras().getString(getResources().getString(R.string.note)));
		}
		
		/*// OK Button Listener for note
		mOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickOk();
			}
		});

		// Cancel button listener 
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});*/

		/*mNote.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						onClickOk();
						return true;
					}
				}
				return false;
			}
		});*/
	}

	private void onClickOk() {
		if(mNote.getText().length()>0){
			Calendar cal3;
			cal3= Calendar.getInstance();
			String StartedDateTime = dateFormat.format(cal3.getTime());
			mMap = new HashMap<String, String>();
			mMap.put("StartedDateTime",StartedDateTime);
			mMap.put("MenuInfo1", "Note");
			mMap.put("Submenuvalue",mNote.getText().toString());
			
		}else{
		}
		finish();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!new ManagePreferenceData(this).isSyncAlarmSet()) // IF not set, set sync alarm
			new ScheduleBackgroundTask(this).setRecurringAlarm();
		else
			Log.v("Alarm ","Already set");
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint","Called");
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
			if(mNote.getText().toString().trim().length()>0){
				Intent mNotesDataIntent = new Intent();
				mNotesDataIntent.putExtra(getResources().getString(R.string.note), mNote.getText().toString().trim());
				setResult(RESULT_OK,mNotesDataIntent);	
				
			}else{
				setResult(RESULT_CANCELED);
			}
			finish();
			break;

		default:
			break;
		}	
	}

}


