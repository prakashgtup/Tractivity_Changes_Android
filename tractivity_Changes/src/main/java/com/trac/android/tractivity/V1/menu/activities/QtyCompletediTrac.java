package com.trac.android.tractivity.V1.menu.activities;

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

public class QtyCompletediTrac extends Activity implements OnClickListener{

	private EditText mQuantityCompleted;
	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.qtycompleteditrac);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.job_details));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.cancel));
		mTitleBarRightMenu.setText(getResources().getString(R.string.accept));
		mTitleBarRightMenu.setTypeface(null, Typeface.BOLD);
		mTitleBarLeftMenu.setOnClickListener(this);
		mTitleBarRightMenu.setOnClickListener(this);
		// Initiate XML widgets
		mQuantityCompleted =  (EditText) findViewById(R.id.qty_quantity);
		SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		boolean qty = customSharedPreference.getBoolean(getResources().getString(R.string.qty), false);
		//boolean pn = customSharedPreference.getBoolean(getResources().getString(R.string.pn), false);

		// Check if Quantity entry is true, if false disable the entry
		if(qty){
			if(getIntent().hasExtra(getResources().getString(R.string.qty_completed))){
				mQuantityCompleted.setText(getIntent().getExtras().getString(getResources().getString(R.string.qty_completed)));
			}
			
		}else{
			mQuantityCompleted.setEnabled(false);
			mQuantityCompleted.setFocusable(false);
			mQuantityCompleted.setClickable(false);
		}



		// OK Button Listener
		/*mOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { 
				// Read the optional part number and quantity from Edit text
				mQuantity = mQty.getText().toString();
				mOptionalPartNumber = mQtyOptionalPartnumber.getText().toString();

				if(mQuantity.length()>0){
					mMap = new HashMap<String, String>();
					Calendar cal2;
					cal2= Calendar.getInstance();
					String StartedDateTime = dateFormat.format(cal2.getTime());
					mMap.put("StartedDateTime",StartedDateTime);
					mMap.put("MenuInfo1", "QTY");
					mMap.put("Submenuvalue",mQuantity);
					LogiTrac.mLog.add(mMap);
					LogiTrac.mSubmenuLog.add(mMap);
				}else{
				}

				if(mOptionalPartNumber.length()>0){
					mMap2 = new HashMap<String, String>();
					Calendar cal2;
					cal2= Calendar.getInstance();
					String StartedDateTime = dateFormat.format(cal2.getTime());
					mMap2.put("StartedDateTime",StartedDateTime);
					mMap2.put("MenuInfo1", "PN");
					mMap2.put("Submenuvalue",mOptionalPartNumber);
					LogiTrac.mLog.add(mMap2);
					LogiTrac.mSubmenuLog.add(mMap2);
				}
				finish();
			}
		});*/
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint","Called");
		new ScheduleBackgroundTask(this).cancelAlarm();
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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.leftmenuID:
            finish();
			break;
		case R.id.rightmenuID:
            if(mQuantityCompleted.getText().toString().length() > 0 ){
            	Intent quantityCompletedIntent = new Intent();
				quantityCompletedIntent.putExtra(getResources().getString(R.string.qty_completed), mQuantityCompleted.getText().toString());
				setResult(RESULT_OK,quantityCompletedIntent);
				finish();
            }
			break;

		default:
			break;
		}

	}

}
