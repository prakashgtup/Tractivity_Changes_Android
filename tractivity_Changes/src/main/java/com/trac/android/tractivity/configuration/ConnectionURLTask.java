package com.trac.android.tractivity.configuration;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.database.Webservice;

/**
 * Async task to check the validation of host
 *
 */
public class ConnectionURLTask extends AsyncTask<String, Integer, String>{

	private Context mContext;
	private ProgressBar mProgressIndicator;
	private Webservice mTractivityWebservice;
	private TextView mProgressText,mConnectText;
	private LinearLayout mNetworkNameContainer;
	private CheckBox mRememberNetworkDetails;
	
	public ConnectionURLTask(Context context,ProgressBar progressbar,TextView progresstext,TextView connectText, LinearLayout networkNameContainer,String name, CheckBox rememberNetwork) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mProgressIndicator = progressbar;
		mProgressText = progresstext;
		mConnectText = connectText;
		mRememberNetworkDetails = rememberNetwork;
		mNetworkNameContainer = networkNameContainer;
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
			return mTractivityWebservice.ConfigDetails(params[0].trim(), params[1].trim(), params[2].trim());
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
			if(result != null && result.equalsIgnoreCase("success")){
				mProgressIndicator.setProgress(100);
				mProgressText.setText(mContext.getResources().getString(R.string.connection_established));
				mRememberNetworkDetails.setVisibility(View.VISIBLE);
				mConnectText.setVisibility(View.VISIBLE);
				mNetworkNameContainer.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(mProgressIndicator.getWindowToken(), 0);
			}else{
				mProgressIndicator.setProgress(0);
				mProgressText.setText(mContext.getResources().getString(R.string.unknown_server));
				mRememberNetworkDetails.setVisibility(View.GONE);
				mConnectText.setVisibility(View.GONE);
				mNetworkNameContainer.setVisibility(View.INVISIBLE);
			}	
		}
	}


}
