package com.trac.android.tractivity.login;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.database.Webservice;

/**
 * Async task to authenticate user
 *
 */
public class AuthenticateUserTask extends AsyncTask<String, Integer, String>{

	private Context mContext;
	private ProgressBar mProgressIndicator;
	private Webservice mTractivityWebservice;
	private TextView mProgressText,mLoginText;
	
	
	public AuthenticateUserTask(Context context,ProgressBar progressbar,TextView progresstext,TextView loginText) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mProgressIndicator = progressbar;
		mProgressText = progresstext;
		mLoginText = loginText;
		mTractivityWebservice = new Webservice(mContext);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mProgressIndicator.setProgress(30);
	}


	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		if(!isCancelled()){ // To check if asynctask was cancalled or not
			publishProgress(70);
			return mTractivityWebservice.LoginDetails(params[0], params[1]);
		}		
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		mProgressIndicator.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(!isCancelled()){ // Not cancelled
			if(result != null && result.equalsIgnoreCase("success")){ // Success,set visibility to Login Button
				mProgressIndicator.setProgress(100);
				mProgressText.setText(mContext.getResources().getString(R.string.password_matches));
				mLoginText.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(mProgressIndicator.getWindowToken(), 0);
			}else{ // Revoke visibility to Login
				mProgressIndicator.setProgress(0);
				mProgressText.setText(mContext.getResources().getString(R.string.password_mismatch));
				mLoginText.setVisibility(View.GONE);
			}	
		}
	}


}
