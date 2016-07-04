package com.trac.android.tractivity.logs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;

import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.database.RegisteredTable;
import com.trac.android.tractivity.database.Webservice;
import com.trac.android.tractivity.utils.AlertNotification;

/**
 * Asynchronous task to fetch jobs details
 *
 */

public class GetJobsTask extends AsyncTask<Integer, Void, String>{

	private Activity mContext;
	private ProgressDialog mProgressIndicator;
	private Webservice mTractivityWebservice;
	private long startTime,FinishTime;
	private String mPageFlag;
		
	public GetJobsTask(Activity context,String pageFlag) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mPageFlag = pageFlag;
		mTractivityWebservice = new Webservice(mContext);
		mProgressIndicator = new ProgressDialog(mContext);
		mProgressIndicator.setCancelable(false);
		Cursor cursor = DatabaseHelper.getInstance(context).queryTable(RegisteredTable.JobTable.TABLE_NAME, null, null, null, null);
		if(cursor != null && cursor.getCount() == 0){
			mProgressIndicator.setMessage("Get Jobs");
		}else if(cursor != null && cursor.getCount() >= 0){
			mProgressIndicator.setMessage("Updating Lists");	
		}else{
			mProgressIndicator.setMessage("Get Jobs");
		}
		cursor.close();
		
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mProgressIndicator.show();
	}
	
	
	@Override
	protected String doInBackground(Integer... params) {  // Params[0] - Logged in Employee Id
		// TODO Auto-generated method stub
		try{
			startTime = System.currentTimeMillis();
			String response = mTractivityWebservice.getjobs(params[0],DatabaseHelper.getInstance(mContext));
			FinishTime = System.currentTimeMillis();
			return response;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mProgressIndicator.dismiss();
		if(result != null){
			if(mPageFlag.equalsIgnoreCase("Log")){ // Called from Log Screen
				((LogiTrac) mContext).refreshLogViews();
			}
			// To save sync status and sync time in preference file
			SharedPreferences customSharedPreference = mContext.getSharedPreferences("iTracSharedPrefs", Context.MODE_PRIVATE);
			final SharedPreferences.Editor editor = customSharedPreference.edit();
			SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm:ss aa", Locale.US);
			Calendar mCurrentCalendar = Calendar.getInstance();
			String sync_starttime = dateFormat.format(mCurrentCalendar.getTime());
			editor.putString("last_sync_time",sync_starttime);
			// To calculate get jobs execution time
			double executed_time = (FinishTime - startTime) / 1000.000000;
			//DecimalFormat decimalFormat = new DecimalFormat("#.000000");
			editor.putString("get_jobs_execution_time",String.format("%.6f", executed_time));
			// To store total executed time
			double get_jobs_total_executed_time =  customSharedPreference.getFloat("get_jobs_total_executed_time",0);
			double current_total_time = get_jobs_total_executed_time + executed_time;
			editor.putFloat("get_jobs_total_executed_time",(float)current_total_time);
			// To increment total count
			int total_count = customSharedPreference.getInt("get_jobs_total_executed_count",0);
			total_count = total_count + 1;
			editor.putInt("get_jobs_total_executed_count",total_count);
			if(result.equalsIgnoreCase("success")){
				editor.putString("last_sync_success_time",sync_starttime);
				editor.putString("sync_status","Success");	
			}else{
				editor.putString("sync_status","Failure");
			}
		    editor.commit();
		    
		}else{
            new AlertNotification().showAlertPopup(mContext, "Error", "Unable to get jobs, try after some time.", null);			
		}
		
	}
}
