/**
 * 
 */
package com.trac.android.tractivity.V1.menu.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.utils.AlertNotification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 * Activity to display and manage about tractivity details
 *
 */
public class AboutTractivity extends Activity{

	private TextView mAppVersionText,mSyncStatusText,mActiveURLText,mLastSyncAttemptText,mLastSyncSuccessText,mDBSizeText,mElementCountText,mGetJobsTimeText,mGetJobsAverareTimeText,mSendDatabaseText,mBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		// Referencing views
		mAppVersionText = (TextView) findViewById(R.id.about_app_version_textID);
		mSyncStatusText = (TextView) findViewById(R.id.about_sync_status_textID);
		mActiveURLText = (TextView) findViewById(R.id.about_active_url_textID);
		mLastSyncAttemptText = (TextView) findViewById(R.id.about_last_sync_attempt_textID);
		mLastSyncSuccessText = (TextView) findViewById(R.id.about_sync_success_textID);
		mDBSizeText = (TextView) findViewById(R.id.about_db_size_textID);
		mElementCountText = (TextView) findViewById(R.id.about_element_count_textID);
		mGetJobsTimeText = (TextView) findViewById(R.id.about_get_jobs_time_textID);
		mGetJobsAverareTimeText = (TextView) findViewById(R.id.about_get_jobs_average_textID);
		mSendDatabaseText = (TextView) findViewById(R.id.about_send_db_textID);
		mBack = (TextView) findViewById(R.id.about_back_textID);
		try {
			// App version
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pInfo.versionName;
			mAppVersionText.setText("Version: " + version);
			SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Context.MODE_PRIVATE);
			// Sync status
			mSyncStatusText.setText("Sync Status: "+customSharedPreference.getString("sync_status", ""));
			// Active Url
			mActiveURLText.setText("Active URL: "+customSharedPreference.getString("network_address", ""));
			// last sync attempt
			mLastSyncAttemptText.setText("Last Sync Attempt: "+customSharedPreference.getString("last_sync_time", ""));
			// Last sync success
			mLastSyncSuccessText.setText("Last Sync Success: "+customSharedPreference.getString("last_sync_success_time", ""));
			// Db size
			final File database  = getDatabasePath(DatabaseHelper.DATABASE_NAME);
			mDBSizeText.setText("DB Size: " + database.length()/1024.00 + " KB");
			// element count
			mElementCountText.setText("Element Count: " + DatabaseHelper.getInstance(this).getTotalEntries());
			// Get Jobs time
			mGetJobsTimeText.setText("GetJobs Time: " + customSharedPreference.getString("get_jobs_execution_time", ""));
			// Get Jobs Averate time
			float total_execution_time = customSharedPreference.getFloat("get_jobs_total_executed_time", 0);
			int total_count = customSharedPreference.getInt("get_jobs_total_executed_count", 0);
			DecimalFormat decimalFormat = new DecimalFormat("#.000000");
			mGetJobsAverareTimeText.setText("GetJobs Avg. Time: "+decimalFormat.format(total_execution_time/total_count));
			
			mSendDatabaseText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
						try {
						String strFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
						File file = new File(strFile);
						if (!file.exists())
						file.mkdirs();
						strFile = strFile + "/iTrac.db";
						File copyDB = new File(strFile);
						copyFileUsingFileStreams(database, copyDB);
						Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.setType("plain/text");
						emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "techsupport@tractivity.com" });
						Calendar mCurrentDate = Calendar.getInstance();
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Database Report File: "+ (mCurrentDate.get(Calendar.DAY_OF_MONTH))+(mCurrentDate.get(Calendar.MONTH)+1)+String.valueOf(mCurrentDate.get(Calendar.YEAR)).substring(2, 4));
						emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + strFile));
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Reason for sending database...");
						startActivity(emailIntent);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						new AlertNotification().showAlertPopup(AboutTractivity.this, "Information", "Please insert sd card to send database", null);
					}
				}
			});
			
			mBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void copyFileUsingFileStreams(File source, File dest)  throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}


}
