package com.trac.android.tractivity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.logs.LogiTrac;
import com.trac.android.tractivity.utils.NetworkCheck;

/**
 * 
 * Activity to host and manage Login functionalities
 *
 */

public class LoginPage extends Activity implements TextWatcher{
	
	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu,mProgressText,mLogin;
	private EditText mUsername,mPassword;
	private ProgressBar mProgressBar;
	private NetworkCheck mNetworkCheck;
	private AuthenticateUserTask mLoginTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.activity_login);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);		
		// Hide virtual keyboard when login page open
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// Referencing views
		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.employee_login));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.config));
		mTitleBarRightMenu.setVisibility(View.INVISIBLE);
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mProgressText = (TextView) findViewById(R.id.progress_textID);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mLogin = (TextView) findViewById(R.id.login_textID);
		mLoginTask = new AuthenticateUserTask(this, mProgressBar, mProgressText, mLogin);
		// Registering for text change listener
		mUsername.addTextChangedListener(this);
		mPassword.addTextChangedListener(this);
		// Clear password when opening a screen
		mPassword.getText().clear();
		mNetworkCheck = new NetworkCheck();

		// To check day event is ended or not
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
		if(dbHelper.isDuringDay()) {
			new ScheduleBackgroundTask(this).setRecurringAlarm();
			startActivity(new Intent().setClass(getApplicationContext(),LogiTrac.class));
			finish();
		}
		dbHelper.close();
		
        mTitleBarLeftMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
        mLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new ScheduleBackgroundTask(LoginPage.this).setRecurringAlarm();
				startActivity(new Intent().setClass(LoginPage.this,LogiTrac.class));	
			}
		});
		
		
	}

	// Life cycle methods for login page
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//mPassword.getText().clear();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		 mProgressText.setText(getResources().getString(R.string.attempting_login));
	}

	
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		try {
			if(mNetworkCheck.ConnectivityCheck(this)){ // Network check
				// To cancel asyntask if its in running or pending state 
				if(mLoginTask.getStatus() == Status.RUNNING || mLoginTask.getStatus() == Status.PENDING)
					mLoginTask.cancel(true);
				mProgressBar.setProgress(0);
				mLogin.setVisibility(View.GONE);
				
				if(mUsername.getText().toString().trim().length() > 0 && mPassword.getText().toString().length() > 0){
					mLoginTask = new AuthenticateUserTask(this, mProgressBar, mProgressText, mLogin);
					// call webservice to authenticate entered Login credentials
					mLoginTask.execute(mUsername.getText().toString().trim(),mPassword.getText().toString());		
				}else{
					mProgressText.setText("");
				}
			}else{
				mProgressText.setText(getResources().getString(R.string.no_network));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
