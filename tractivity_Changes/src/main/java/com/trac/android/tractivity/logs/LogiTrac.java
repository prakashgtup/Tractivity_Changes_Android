package com.trac.android.tractivity.logs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.trac.android.tractivity.V1.ManagePreferenceData;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.V1.menu.activities.AboutTractivity;
import com.trac.android.tractivity.V1.menu.activities.JobDetailsiTrac;
import com.trac.android.tractivity.V1.webservices.TractivityiTrac_classvariables;
import com.trac.android.tractivity.W2S.Jobs;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.database.RegisteredTable;
import com.trac.android.tractivity.database.RegisteredTable.TransactionTable;
import com.trac.android.tractivity.database.TransactionInfo;
import com.trac.android.tractivity.login.LoginPage;
import com.trac.android.tractivity.utils.AlertNotification;
import com.trac.android.tractivity.utils.NetworkCheck;
import com.trac.android.tractivity.utils.TimeInterval;

public class LogiTrac extends ExpandableListActivity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener{
	public enum EntryType {
		StartDay("Start Day"),
		EndDay("End Day"),
		AllLunch("All Lunch"),
		AllReturn("All Return"),
		Job("Job"),
		PN("PN"),
		QTY("QTY"),
		Note("Note"),
		ETC("ETC"),
		MPN("MPN"),
		MQTY("MQTY"),
		Pending("Pending"),
		Sending("Sending"),
		Sent("Sent"),
		Failed("Failed");

		public String entryName;

		EntryType(String name) {
			entryName = name;
		}

		public String getEntryName() {
			return entryName;
		}
	}

	private TextView mMainTitle, mTitleBarLeftMenu,mTitleBarRightMenu;
	private TractivityiTrac_classvariables itrac_obj = new TractivityiTrac_classvariables();
	private String mString_Time;
	private double GPSLatitutude = 0, GPSLongitude = 0;
	private static final int SECONDARY_ACTIVITY_REQUEST_CODE = 0,JOB_DETAILS_REQUEST_CODE=1;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private SimpleDateFormat databaseFormat;
	private Thread timeThread = null;
	private Timer EmpHoursTimer,JobHoursTimer;
	private String PhoneNumber = "";
	private DatabaseHelper dbHelper;
	private String mEmployeeName;
	private int mEmployeeID;
	private ImageView mTitleLogo;
	private TextView mLogType,mLogTimeText,mSyncStatusText,mBeginJob,mEnterDetails,mAllLunch,mEndDay,mEnterW2S;
	private ArrayList<LogListDetail> mLogDetailList = new ArrayList<LogListDetail>();
	private ScrollView mCustomExpandableListviewContainer;
	private LinearLayout mCustomExpandleListViewLayout;
	private String mCachedEmployeeStartTime;
	private SharedPreferences customSharedPreference;
	private int mEmployeHoursUpdatePosition = -1;
	private String mPreviousCalculateTime ="0.00";
	private boolean mIsShouldPauseTimeThread,mIsMultipleEmployee,w2s;
	private LocationClient mLocationClient;
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =	MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	private final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 2;
	private Dialog mUpdateGooglePlayServicesDialog = null;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			dbHelper = DatabaseHelper.getInstance(this);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			// Set the layout from the XML resource
			setContentView(R.layout.activity_homepage);
			// Get the window and set the desired feature / properties
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);
			// Clearing Static values which may be already used in the application, and give a new instance every time.
			// Read the GPS Location
			/*mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			mLocationListener = new MyLocationListener();
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);*/
			if(mUpdateGooglePlayServicesDialog != null && mUpdateGooglePlayServicesDialog.isShowing())
				mUpdateGooglePlayServicesDialog.dismiss();
			mLocationRequest = LocationRequest.create();
			// Use high accuracy
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			// Set the update interval to 5 seconds
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			// Set the fastest update interval to 5 second
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			mLocationClient = new LocationClient(this, this, this);
			if(checkPlayServices()){
				//sMapViewOnScrollViewFlag ="currentlocation";	//Flag to differentiate the asynch thread calling in broadcastreciever while scrolling mapview
				mLocationClient.connect();
			}
			// Simple Date time format for sync in web services
			databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
			// To get employee details from shared preference file
			customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
			mEmployeeID = customSharedPreference.getInt(getResources().getString(R.string.empId), 0);
			mIsMultipleEmployee = customSharedPreference.getBoolean(getResources().getString(R.string.emplist), false);
			w2s= customSharedPreference.getBoolean(getResources().getString(R.string.w2s),false);
			mEmployeeName = customSharedPreference.getString(getResources().getString(R.string.emp_name), "");
			mCachedEmployeeStartTime = customSharedPreference.getString(getResources().getString(R.string.hours_by_employee_start_time_cached), calculateHours(databaseFormat.format(new Date()),mPreviousCalculateTime,null));

			// Setting the Custom Fonts For Title
			mMainTitle = (TextView) findViewById(R.id.MainTitle);
			mTitleLogo = (ImageView) findViewById(R.id.title_logoID);
			mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
			mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
			mTitleBarLeftMenu.setText(getResources().getString(R.string.back));
			mTitleBarRightMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh_job_selector, 0, 0, 0);
			mTitleBarRightMenu.setText("");
			mMainTitle.setVisibility(View.GONE);
			findViewById(R.id.title_logoID).setVisibility(View.VISIBLE);
			mLogType = (TextView) findViewById(R.id.log_typeID);
			registerForContextMenu(mLogType);
			mLogTimeText = (TextView) findViewById(R.id.log_timeID);
			mSyncStatusText = (TextView) findViewById(R.id.sync_statusID);
			mBeginJob = (TextView) findViewById(R.id.begin_job_menuID);
			mEnterDetails = (TextView) findViewById(R.id.enter_details_menuID);
			mEnterW2S=(TextView) findViewById(R.id.enter_details_w2s);
			mAllLunch = (TextView) findViewById(R.id.all_lunch_menuID);
			mEndDay = (TextView) findViewById(R.id.end_day_menuID);
			mCustomExpandableListviewContainer = (ScrollView) findViewById(R.id.custom_expandable_listContainerID);
			mCustomExpandleListViewLayout = (LinearLayout) findViewById(R.id.custom_expandable_listID);

			// Register Function for managing Network connectivity throughout the view
			TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			PhoneNumber = TelephonyMgr.getDeviceId();

			if(PhoneNumber == null) {
				PhoneNumber = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
			}
			// Call Get job Acts to get data for the logged in user
			if(new NetworkCheck().ConnectivityCheck(this)){
				new GetJobsTask(this,"").execute(mEmployeeID);
			}else{
				new AlertNotification().showAlertPopup(this, "No network", "Unable to get jobs, try again with network connection", null);
			}
			// Create thread to show current time in digital 24 hour format
			Runnable runnable = new TimeRunner();
			timeThread = new Thread(runnable);
			timeThread.start();

			updateLogView("history");

			mTitleBarLeftMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mEndDay.getVisibility() == View.VISIBLE){ // If end day visible
						showAlert("Error", "Cannot logout without ending day");
					}else{ // finish activity and launch loginpage
						finish();
						Intent mLoginIntent = new Intent(LogiTrac.this,LoginPage.class);
						mLoginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(mLoginIntent);
					}
				}
			});

			// Refresh GET JOBS
			mTitleBarRightMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(new NetworkCheck().ConnectivityCheck(LogiTrac.this)){
						new GetJobsTask(LogiTrac.this,"Log").execute(mEmployeeID);
					}else{
						new AlertNotification().showAlertPopup(LogiTrac.this, "No network", "Unable to get jobs, try again with network connection", null);
					}	
				}
			});

			// Tractivity Logo
			mTitleLogo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startActivity(new Intent(LogiTrac.this,AboutTractivity.class));	
				}
			});

			// To choose type of LOG views 
			mLogType.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openContextMenu(v);
				}
			});


			mBeginJob.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent newtaskintent=new Intent(LogiTrac.this,NewiTrac.class);
					Bundle mvalues=new Bundle();
					TransactionInfo lastJob = getLastJob();
					if(lastJob != null) {
						mvalues.putString("employee", lastJob.empCode);
						mvalues.putString("job", lastJob.jobCode);
						mvalues.putString("jobphase", lastJob.itemCode);
						mvalues.putString("activity", lastJob.actCode);
					} else {
						mvalues.putString("employee", "");
						mvalues.putString("job", "");
						mvalues.putString("jobphase", "");
						mvalues.putString("activity", "");
					}
					newtaskintent.putExtra("extra_value", mvalues);
					startActivityForResult(newtaskintent,SECONDARY_ACTIVITY_REQUEST_CODE);
				}
			});

			mEnterDetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent newtaskintent=new Intent(LogiTrac.this,JobDetailsiTrac.class);
					newtaskintent.putExtra("current_job", itrac_obj.mJob);
					startActivityForResult(newtaskintent,JOB_DETAILS_REQUEST_CODE);     
				}
			});
            //W2S
			if(w2s)
			{
				 mEnterW2S.setVisibility(View.VISIBLE);
			}
			else
			{
				mEnterW2S.setVisibility(View.INVISIBLE);
			}
			mEnterW2S.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent newtaskintent=new Intent(LogiTrac.this,Jobs.class);
					startActivity(newtaskintent);
				}
			});
			mAllLunch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mAllLunch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.all_lunch)) || mAllLunch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.begin_lunch)))
						if(mIsMultipleEmployee)
							showOptionsAlert("Confirm", "All Lunch?");
						else
							showOptionsAlert("Confirm", "Begin Lunch?");
					else{// All Return
						AllReturn();
						//mAllLunch.setText(getResources().getString(R.string.all_lunch));
						setLunchTextBasedUponEmployee("return");
						mBeginJob.setVisibility(View.VISIBLE);
					}

				}
			});

			mEndDay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mAllLunch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.all_lunch)) || mAllLunch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.begin_lunch))){
						showOptionsAlert("Confirm", "End Day?");	
					}else{
						showAlert("Invalid", "On lunch");
					}

				}
			});

		}catch(Exception e){
			e.printStackTrace();
		}
	}// End of on create


	private void setLunchTextBasedUponEmployee(String current_activity) {
		// TODO Auto-generated method stub
		// To set Lunch details based upon employee type (Single Employee && Multiple Employee)
		if(mIsMultipleEmployee){
			if(current_activity.equalsIgnoreCase("lunch"))
				mAllLunch.setText(getResources().getString(R.string.all_return));
			else
				mAllLunch.setText(getResources().getString(R.string.all_lunch));
		}else{
			if(current_activity.equalsIgnoreCase("lunch"))
				mAllLunch.setText(getResources().getString(R.string.return_lunch));
			else
				mAllLunch.setText(getResources().getString(R.string.begin_lunch));
		}
	}

	private void updateLogView(String LogType) {
		//Load lists from database
		try {
			ArrayList<TransactionInfo> transactionList = dbHelper.getJobsOrderByDate("(" + mEmployeeID + ") " + mEmployeeName);

			if(transactionList.size() > 0){
				checkIfDayEventStarts(transactionList);
				LogListDetail mLogListDetail = null;
				ArrayList<EmployeeLogDetail> mLists = new ArrayList<EmployeeLogDetail>();
				boolean isEndDaySelected = false ;
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
				mLogDetailList = new ArrayList<LogListDetail>();
				String day_event = null,day_StartTime = null;
				for(TransactionInfo tInfo : transactionList) {
					/*************** Modified  **************************/
					// ArrayList<Map<String, ArrayList<EmployeeLogDetail>>>
					day_event = tInfo.uniqueValue;
					day_StartTime = tInfo.transactionTimestamp;
					if(tInfo.uniqueValue.equals(EntryType.StartDay.entryName)) { // Start Day
						mLogListDetail  = new LogListDetail();
						mLogListDetail.setGroup_item("Day Event - " + DateFormat.format("MM/dd/yyyy", dateFormatter.parse(tInfo.transactionTimestamp)));
						mLogListDetail.setStatus(tInfo.sendStatus);
						mLists = new ArrayList<EmployeeLogDetail>();
						if(LogType.equalsIgnoreCase("history") && mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.history))){  // For only History View
							EmployeeLogDetail mLogDetail = new EmployeeLogDetail();
							mLogDetail.setJobEvent(new ArrayList<String>());
							mLogDetail.setDayEvent(tInfo.uniqueValue + " - " + DateFormat.format("hh:mm:ss a", dateFormatter.parse(tInfo.transactionTimestamp)));
							mLogDetail.setStatus(tInfo.sendStatus);
							mLists.add(mLogDetail);
						}
						isEndDaySelected = false;
					}else if(tInfo.uniqueValue.equals(EntryType.EndDay.entryName)) { // End Day
						EmployeeLogDetail mLogDetail = new EmployeeLogDetail();
						if(LogType.equalsIgnoreCase("history") && mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.history))){  // For only History View
							mLogDetail.setDayEvent(tInfo.uniqueValue + " - " + DateFormat.format("hh:mm:ss a", dateFormatter.parse(tInfo.transactionTimestamp)));
							mLogDetail.setJobEvent(new ArrayList<String>());
							mLogDetail.setStatus(tInfo.sendStatus);
							mLists.add(mLogDetail);
						}
						mLogListDetail.setChild_items(mLists);
						mLogDetailList.add(mLogListDetail);	
						isEndDaySelected = true;
					}else{ // Other day entries
						EmployeeLogDetail mLogDetail = new EmployeeLogDetail();
						mLogDetail.setRow_id(tInfo.rowNumber);
						mLogDetail.setStatus(tInfo.sendStatus);
						if(tInfo.uniqueValue.equalsIgnoreCase("Job")){ // Job entry
							if(LogType.equalsIgnoreCase("history") || (LogType.equalsIgnoreCase("jobs_by_emp") && !tInfo.jobCode.equalsIgnoreCase("0"))){
								mLogDetail.setDayEvent("Job Entry" + " - " + DateFormat.format("hh:mm:ss a", dateFormatter.parse(tInfo.transactionTimestamp)));
								ArrayList<String> mJobDetails = new ArrayList<String>();
								mJobDetails.add("Employee:"+tInfo.empCode);
								mJobDetails.add("Job:"+tInfo.jobCode);								
								if(!tInfo.itemCode.equalsIgnoreCase("")) // If phase not empty
									mJobDetails.add("Phase:"+tInfo.itemCode);
								mJobDetails.add("Activity:"+tInfo.actCode);
								mLogDetail.setJobEvent(mJobDetails);
								mLists.add(mLogDetail);
							}else if(LogType.equalsIgnoreCase("hours_by_emp")){ 
								if(!tInfo.empCode.equalsIgnoreCase(("+mEmployeeID+")) && !tInfo.jobCode.equalsIgnoreCase("0") && !tInfo.actCode.equalsIgnoreCase("0")){ // Reject the end day job which is added during end day
									String time_spent[] = dbHelper.calculateEmployee_JobHours("(" + mEmployeeID + ") " + mEmployeeName,tInfo.transactionTimestamp, tInfo.rowNumber);
									if(!isEmployeeAlreadyExists(mLists, tInfo.empCode)){ // If employee not exits in same day event
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										mLogDetail.setPrevious_hour_of_day(time_spent[0]+":"+time_spent[1]+":"+time_spent[2]);
										mLogDetail.setDayEvent(calculateHourPercentage(Long.valueOf(time_spent[3]))+ "hrs - " + tInfo.empCode);
										mLists.add(mLogDetail);
									}else{ // If already exists just add the previous hour + present time spent
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										String[] total_time_spent = addEmployeeHours(mPreviousCalculateTime, time_spent);
										mLogDetail.setPrevious_hour_of_day(total_time_spent[0]+":"+total_time_spent[1]+":"+total_time_spent[2]);
										mLogDetail.setDayEvent(calculateHourPercentage(Long.valueOf(total_time_spent[3]))+ "hrs - " + tInfo.empCode);
										mLists.set(mEmployeHoursUpdatePosition, mLogDetail);
									}
									/*if(!isEmployeeAlreadyExists(mLists, tInfo.empCode)){ // Not already exists in same day event
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										mLogDetail.setEmployeeAlreadyExistsinDayEvent(false);
										String cachedEmployeeHours = processEmployeeHours(tInfo.transactionTimestamp, tInfo.empCode);
										if(cachedEmployeeHours != null){
											mLogDetail.setDayEvent(cachedEmployeeHours);
											String previous_hour = cachedEmployeeHours.substring(0, cachedEmployeeHours.indexOf('h'));
											mLogDetail.setPrevious_hour_of_day(previous_hour);
										}else{
											mLogDetail.setPrevious_hour_of_day("0.00");
										}
											mPreviousCalculateTime = cachedEmployeeHours.substring(0, cachedEmployeeHours.indexOf('h'));
											mLogDetail.setPrevious_hour_of_day(mPreviousCalculateTime);
										}else{
											mLogDetail.setPrevious_hour_of_day("0.00");	
										}
										mLists.add(mLogDetail);
									}else{// Exists
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										mLogDetail.setEmployeeAlreadyExistsinDayEvent(true);
										if(tInfo.transactionTimestamp.equalsIgnoreCase(mCachedEmployeeStartTime)){
											mLists.set(mEmployeHoursUpdatePosition, mLogDetail);
											mLogDetail.setPrevious_hour_of_day(mPreviousCalculateTime);
										}else{
											String cachedEmployeeHours = processEmployeeHours(tInfo.transactionTimestamp, tInfo.empCode);
											if(cachedEmployeeHours != null){
												mLogDetail.setPrevious_hour_of_day(cachedEmployeeHours.substring(0, cachedEmployeeHours.indexOf('h')));
												mLogDetail.setDayEvent(cachedEmployeeHours);
											}
											mLists.set(mEmployeHoursUpdatePosition, mLogDetail);
										}

									}*/
								}

							}else{ // hours by job

								if(!tInfo.empCode.equalsIgnoreCase(("+mEmployeeID+")) && !tInfo.jobCode.equalsIgnoreCase("0") && !tInfo.actCode.equalsIgnoreCase("0")){ // Reject the end day job which is added during end day
									String[] time_spent = dbHelper.calculateEmployee_JobHours("(" + mEmployeeID + ") " + mEmployeeName,tInfo.transactionTimestamp, tInfo.rowNumber);
									if(!isJobAlreadyExists(mLists, tInfo.jobCode)){ // If employee not exits in same day event
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJob(tInfo.jobCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										mLogDetail.setPrevious_hour_of_day(time_spent[0]+":"+time_spent[1]+":"+time_spent[2]);
										mLogDetail.setDayEvent(calculateHourPercentage(Long.valueOf(time_spent[3]))+ "hrs - " + tInfo.jobCode);
										mLists.add(mLogDetail);
									}else{ // If already exists just add the previous hour + present time spent
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setJob(tInfo.jobCode);
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										String[] total_time_spent = addEmployeeHours(mPreviousCalculateTime, time_spent);
										mLogDetail.setPrevious_hour_of_day(total_time_spent[0]+":"+total_time_spent[1]+":"+total_time_spent[2]);
										mLogDetail.setDayEvent(calculateHourPercentage(Long.valueOf(total_time_spent[3]))+ "hrs - " + tInfo.jobCode);
										mLists.set(mEmployeHoursUpdatePosition, mLogDetail);

									}

									/*if(!isJobAlreadyExists(mLists, tInfo.jobCode)){
										mLogDetail.setEmployeeCode(tInfo.empCode);
										mLogDetail.setJob(tInfo.jobCode);
										mLogDetail.setJobEvent(new ArrayList<String>());
										mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
										mLogDetail.setPrevious_hour_of_day("0.00");
										mLogDetail.setEmployeeAlreadyExistsinDayEvent(false);
										String cachedEmployeeHours = processJobHours(tInfo.transactionTimestamp, tInfo.jobCode);
										if(cachedEmployeeHours != null){
											mLogDetail.setDayEvent(cachedEmployeeHours);
											String previous_hour = cachedEmployeeHours.substring(0, cachedEmployeeHours.indexOf('h'));
											mLogDetail.setPrevious_hour_of_day(previous_hour);
										}else{
											mLogDetail.setPrevious_hour_of_day("0.00");
										}
										mLists.add(mLogDetail);
								}else{
									mLogDetail.setEmployeeCode(tInfo.empCode);
									mLogDetail.setJob(tInfo.jobCode);
									mLogDetail.setJobEvent(new ArrayList<String>());
									mLogDetail.setStartTimeinMillis(tInfo.transactionTimestamp);
									mLogDetail.setPrevious_hour_of_day(mPreviousCalculateTime);
									mLogDetail.setEmployeeAlreadyExistsinDayEvent(true);
									if(tInfo.transactionTimestamp.equalsIgnoreCase(mCachedEmployeeStartTime)){
										mLists.set(mEmployeHoursUpdatePosition, mLogDetail);
									}else{
										String cachedEmployeeHours = processJobHours(tInfo.transactionTimestamp, tInfo.jobCode);
										if(cachedEmployeeHours != null)
											mLogDetail.setDayEvent(cachedEmployeeHours);
										mLists.set(mEmployeHoursUpdatePosition, mLogDetail);
									}

								}*/
								}	
							}
							// To cache job details
							itrac_obj.mEmployee = tInfo.empCode;
							itrac_obj.mJob = tInfo.jobCode.contains("'")? tInfo.jobCode.replaceAll("'", "\''") : tInfo.jobCode;
							itrac_obj.mJobPhase = tInfo.itemCode.replaceAll("'", "\''");
							itrac_obj.mActivity = tInfo.actCode.replaceAll("'", "\''");

						}else{
							if(LogType.equalsIgnoreCase("history") && mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.history))){  // For only History View
								mLogDetail.setJobEvent(new ArrayList<String>());
								if(tInfo.detailValue != null){ // Job entry details
									mLogDetail.setDayEvent(tInfo.uniqueValue + " - " +tInfo.detailValue  + " - "+ DateFormat.format("hh:mm:ss a", dateFormatter.parse(tInfo.transactionTimestamp)));
									mLists.add(mLogDetail);
								}else{ // All Lunch or All Return selected
									mLogDetail.setDayEvent(tInfo.uniqueValue + " - " + DateFormat.format("hh:mm:ss a", dateFormatter.parse(tInfo.transactionTimestamp)));
									mLists.add(mLogDetail);
								}
							}
						}
						isEndDaySelected = false;
					}
				}

				if(!isEndDaySelected){ // If not selected add the job entries to day event
					mLogListDetail.setChild_items(mLists);
					mLogDetailList.add(mLogListDetail);	
					mEnterDetails.setVisibility(View.VISIBLE);
					mAllLunch.setVisibility(View.VISIBLE);
					if(day_event != null && day_event.equalsIgnoreCase("all lunch")){
						mAllLunch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_return_selector, 0, 0, 0);
						mBeginJob.setVisibility(View.INVISIBLE);
						//mAllLunch.setText("All Return");
						setLunchTextBasedUponEmployee("lunch");
					}else{
						mAllLunch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_lunch_selector, 0, 0, 0);
						mBeginJob.setVisibility(View.VISIBLE);
						//mAllLunch.setText(getResources().getString(R.string.all_lunch));
						setLunchTextBasedUponEmployee("return");
					}
					mEndDay.setVisibility(View.VISIBLE);
				}else{
					mBeginJob.setVisibility(View.VISIBLE);
					mEnterDetails.setVisibility(View.INVISIBLE);
					mAllLunch.setVisibility(View.INVISIBLE);
					mEndDay.setVisibility(View.INVISIBLE);
					setLunchTextBasedUponEmployee("return");
				}
				getExpandableListView().setVisibility(View.GONE);
				refreshCustomExpandableList(mLogDetailList);
				/*if(LogType.equalsIgnoreCase("hours_by_emp")){ // hours_by_emp
				cacheEmpHoursList(day_event,day_StartTime);
				// To store employee hours array as JSON string in preference file
				Gson gson = new Gson();
				String json = gson.toJson(mEmployeeHoursList);
				Editor mPreferenceEditor = customSharedPreference.edit();
				mPreferenceEditor.putString(getResources().getString(R.string.emp_hours_cache_list), json);
				mPreferenceEditor.commit();
				refreshCustomExpandableList(mEmployeeHoursList);
			}else if(LogType.equalsIgnoreCase("hours_by_job")){
				cacheJobHoursList();
				// To store job hours array as JSON string in preference file
				Gson gson = new Gson();
				String json = gson.toJson(mJobHoursList);
				Editor mPreferenceEditor = customSharedPreference.edit();
				mPreferenceEditor.putString(getResources().getString(R.string.job_hours_cache_list), json);
				mPreferenceEditor.commit();
				refreshCustomExpandableList(mJobHoursList);
			}else{
				refreshCustomExpandableList(mLogDetailList);	
			}*/
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * @param transactionList 
	 * @return
	 */
	private void checkIfDayEventStarts(ArrayList<TransactionInfo> transactionList) {
		// TODO Auto-generated method stub
		for(TransactionInfo transactionInfo : transactionList){
			if(transactionInfo.uniqueValue.equalsIgnoreCase(EntryType.StartDay.entryName)){
				return;
			}else{
				dbHelper.deleteRows(RegisteredTable.TransactionTable.TABLE_NAME, RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " = ?",new String[] {String.valueOf(transactionInfo.rowNumber)});
			}
		}

	}



	// To check if employee already exists in same day event
	private boolean isEmployeeAlreadyExists(ArrayList<EmployeeLogDetail> list,String employee){
		for(int i=0 ; i < list.size() ; i++){
			EmployeeLogDetail logDetail = list.get(i);
			String emp_code = logDetail.getEmployeeCode();
			if(emp_code.trim().equalsIgnoreCase(employee)){
				mEmployeHoursUpdatePosition = i;
				mPreviousCalculateTime = logDetail.getPrevious_hour_of_day();

				/*//mCachedEmployeeStartTime = logDetail.getStartTimeinMillis();
			if(logDetail.getDayEvent()!= null){
				mPreviousCalculateTime = logDetail.getDayEvent().substring(0, logDetail.getDayEvent().indexOf('h'));
				Log.i("previous time set", mPreviousCalculateTime);
			}*/
				return true;
			}
		}

		return false;
	}

	// To check if Job already exists in same day event
	private boolean isJobAlreadyExists(ArrayList<EmployeeLogDetail> list,String job){
		for(int i=0 ; i < list.size() ; i++){
			EmployeeLogDetail logDetail = list.get(i);
			String job_code = logDetail.getJob();
			if(job_code.trim().equalsIgnoreCase(job)){
				mEmployeHoursUpdatePosition = i;
				mPreviousCalculateTime = logDetail.getPrevious_hour_of_day();
				/*if(logDetail.getDayEvent()!= null){
				mPreviousCalculateTime = logDetail.getDayEvent().substring(0, logDetail.getDayEvent().indexOf('h'));
				Log.v("previous time", mPreviousCalculateTime);
			}*/
				return true;
			}
		}

		return false;
	}

	private String[] addEmployeeHours(String previous_calcalated_hour,String[] current_emp_hours){
		String[] hour_string_array;
		String hour,minute,second;
		hour_string_array = previous_calcalated_hour.split(":");
		String format = String.format("%%0%dd", 2);
		hour = String.format(format, Integer.valueOf(hour_string_array[0]));
		minute = String.format(format, Integer.valueOf(hour_string_array[1]));
		second = String.format(format, Integer.valueOf(hour_string_array[2]));
		TimeInterval time = new TimeInterval("00:00:00.000");
		time = time.add(new TimeInterval(hour+":"+minute+":"+second+".000"));
		hour = String.format(format, Integer.valueOf(current_emp_hours[0]));
		minute = String.format(format, Integer.valueOf(current_emp_hours[1]));
		second = String.format(format, Integer.valueOf(current_emp_hours[2]));
		time = time.add(new TimeInterval(hour+":"+minute+":"+second+".000"));
		int total_seconds = time.seconds + (60 * time.minutes) + (3600 * time.hours);
		String[] total_time_string = new String[4];
		total_time_string[0] = String.valueOf(time.hours);
		total_time_string[1] = String.valueOf(time.minutes);
		total_time_string[2] = String.valueOf(time.seconds);
		total_time_string[3] = String.valueOf(total_seconds);
		return total_time_string;
		/*String minutes = String.format(format, time.minutes); 
	String hours = String.format(format, time.hours);
	String total_time ;
	if(hours.equalsIgnoreCase("00")){
		total_time =  "0"+ "." + minutes;
	}else{
		total_time = hours + ":" + minutes;
	}
	return total_time; */
	}

	private String calculateHourPercentage(long elapsedTime){
		double hour_done_percentage =  ( elapsedTime / 3600.00 ) ;
		Log.v("Time percentage", elapsedTime + " " + hour_done_percentage + " ");
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.DOWN);
		String total_time_done = df.format(hour_done_percentage);
		if(hour_done_percentage >= 1){
			total_time_done =  total_time_done.replace('.', ':');	
		}
		return total_time_done;
	}



	// To calculate Employee / Job hours corresponding to start time and current time
	private String calculateHours(String transactionTime, String previous_calculated_hour, String day_StartTime){
		try {
			String[] hour_string_array;
			int hour,minute;
			if(previous_calculated_hour.contains(".")){
				hour_string_array = previous_calculated_hour.split(Pattern.quote("."));
			}else{
				hour_string_array = previous_calculated_hour.split(":");
			}
			hour = Integer.valueOf(hour_string_array[0]);
			minute = Integer.valueOf(hour_string_array[1]);
			// If employee/Job already started in same day event... then add those previous time 
			Calendar mCurrentStartTime = Calendar.getInstance();
			mCurrentStartTime.setTime(databaseFormat.parse(transactionTime));
			mCurrentStartTime.add(Calendar.HOUR_OF_DAY, - hour);
			mCurrentStartTime.add(Calendar.MINUTE, - minute);
			Log.v("calculate time", previous_calculated_hour + " " +mCurrentStartTime.getTime());
			long end;
			if(day_StartTime == null){
				end = Calendar.getInstance().getTimeInMillis();
			}else{
				end = databaseFormat.parse(day_StartTime).getTime();
			}
			long elapsedTime = end - mCurrentStartTime.getTimeInMillis();
			String format = String.format("%%0%dd", 2);
			elapsedTime = elapsedTime / 1000;
			String seconds = String.format(format, elapsedTime % 60);
			String minutes = String.format(format, (elapsedTime % 3600) / 60);
			String hours = String.format(format, elapsedTime / 3600);
			String time =  hours + ":" + minutes + ":" + seconds;
			if(hours.equalsIgnoreCase("00")){
				time =  "0"+ "." + minutes;
			}else{
				time = hours + ":" + minutes;
			}  	
			return time;
		} catch(Exception e){
			e.printStackTrace();
			return "0.00";
		}
	}

	// To add new Day entry other than JOB values..
	public void addNewUniqueEntryWithName(String uniqueValue) {
		/*dbHelper.insertDataIntoTable(String.format("NULL, '%s', '%s', '', '', '%s', NULL, NULL, NULL, NULL, NULL, '%s', '%s', '%s'",
				"(" + mEmployeeID + ") " + mEmployeeName, 
				databaseFormat.format(Calendar.getInstance().getTime()),
				uniqueValue,
				GPSLatitutude,
				GPSLongitude,
				"Pending"), TransactionTable.TABLE_NAME);*/
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(TransactionTable.COLUMN_EMPLOYEE_ID, "(" + mEmployeeID + ") " + mEmployeeName);		
		values.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, databaseFormat.format(Calendar.getInstance().getTime()));						
		values.put(TransactionTable.COLUMN_UNIQUE_VALUE, uniqueValue);
		values.put(TransactionTable.COLUMN_GPS_LAT, GPSLatitutude+"");
		values.put(TransactionTable.COLUMN_GPS_LON, GPSLongitude+"");
		values.put(TransactionTable.COLUMN_SEND_STATUS, "Pending");
		dbHelper.InsertUniqueEntryWithName(values, TransactionTable.TABLE_NAME);
		
	}

	// To add new Day entry with JOB values..
	public void addNewJobEntryWithDetails(String empCode, String jobCode, String phaseCode, String actCode) {
		// to store it for Employee/ Job Hours Calculation
		String current_time = databaseFormat.format(Calendar.getInstance().getTime());
		mCachedEmployeeStartTime = current_time;
		Editor mPreferenceEditor = customSharedPreference.edit();
		mPreferenceEditor.putString(getResources().getString(R.string.hours_by_employee_code_cached), itrac_obj.mEmployee);
		mPreferenceEditor.putString(getResources().getString(R.string.hours_by_employee_start_time_cached), current_time);
		mPreferenceEditor.commit();


		/*dbHelper.insertDataIntoTable( String.format("NULL, '%s', '%s', '', '', '%s', '%s', '%s', '%s', '%s', NULL, '%s', '%s', '%s'",
				"(" + mEmployeeID + ") " + mEmployeeName,
				current_time,
				"Job",
				empCode,
				jobCode,
				phaseCode,
				actCode,
				GPSLatitutude,
				GPSLongitude,
				"Pending"), TransactionTable.TABLE_NAME);*/
		
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(TransactionTable.COLUMN_EMPLOYEE_ID, "(" + mEmployeeID + ") " + mEmployeeName);		
		values.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, current_time);						
		values.put(TransactionTable.COLUMN_UNIQUE_VALUE, "Job");
		values.put(TransactionTable.COLUMN_EMPLOYEE_CODE, empCode);
		values.put(TransactionTable.COLUMN_JOB_CODE, jobCode);
		values.put(TransactionTable.COLUMN_PHASE_CODE, phaseCode);
		values.put(TransactionTable.COLUMN_ACTIVITY_CODE, actCode);
		values.put(TransactionTable.COLUMN_GPS_LAT, GPSLatitutude+"");
		values.put(TransactionTable.COLUMN_GPS_LON, GPSLongitude+"");
		values.put(TransactionTable.COLUMN_SEND_STATUS, "Pending");
		dbHelper.InsertNewJobEntryWithDetails(values, TransactionTable.TABLE_NAME);

	}

	public void addNewDetailEntryWithDetails(String empCode, String jobCode, String phaseCode, String actCode, String detailName, String detailValue) {
		/*dbHelper.insertDataIntoTable(String.format("NULL, '%s', '%s', '', '', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s'",
				"(" + mEmployeeID + ") " + mEmployeeName,
				mCachedEmployeeStartTime,
				detailName,
				empCode,
				jobCode,
				phaseCode,
				actCode,
				detailValue,
				GPSLatitutude,
				GPSLongitude,
				"Pending"), TransactionTable.TABLE_NAME);*/
		
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(TransactionTable.COLUMN_EMPLOYEE_ID, "(" + mEmployeeID + ") " + mEmployeeName);		
		values.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, mCachedEmployeeStartTime);						
		values.put(TransactionTable.COLUMN_UNIQUE_VALUE, detailName);
		values.put(TransactionTable.COLUMN_EMPLOYEE_CODE, empCode);
		values.put(TransactionTable.COLUMN_JOB_CODE, jobCode);
		values.put(TransactionTable.COLUMN_PHASE_CODE, phaseCode);
		values.put(TransactionTable.COLUMN_ACTIVITY_CODE, actCode);
		values.put(TransactionTable.COLUMN_DETAIL_VALUE, detailValue);
		values.put(TransactionTable.COLUMN_GPS_LAT, GPSLatitutude+"");
		values.put(TransactionTable.COLUMN_GPS_LON, GPSLongitude+"");
		values.put(TransactionTable.COLUMN_SEND_STATUS, "Pending");
		dbHelper.InsertNewDetailEntryWithDetails(values, TransactionTable.TABLE_NAME);

	}

	// Broadcast Receiver for managing network status
	private BroadcastReceiver mSyncstatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			try{
				if(intent.hasExtra("sync_status")){
					mLogTimeText.setVisibility(View.GONE);
					String status = intent.getExtras().getString("sync_status");
					mSyncStatusText.setVisibility(View.VISIBLE);
					mSyncStatusText.setText(status);
					int thread_interval = 0;
					if(status.equalsIgnoreCase("success")){
						mSyncStatusText.setTextColor(arg0.getResources().getColor(R.color.lime));
						thread_interval = 4000;
						updateLog();
					}else{
						mSyncStatusText.setTextColor(arg0.getResources().getColor(R.color.black_clr));
						thread_interval = 1000;
						updateLog();
					}
					Log.v("check", status + " " + thread_interval);
					//timeThread.wait(thread_interval);
					mIsShouldPauseTimeThread = true;

					Handler handler = new Handler();
					handler.postDelayed(new Runnable(){
						@Override
						public void run(){
							/*timeThread = new Thread(new TimeRunner());
							timeThread.start();*/
							mIsShouldPauseTimeThread = false;
						}
					}, thread_interval);
				}	
			}catch(Exception e){
				e.printStackTrace();
			}



		}
	};

	// To get last JOB
	private TransactionInfo getLastJob() {
		TransactionInfo lastJob = null;

		String[] columnsToReturn = {
				TransactionTable.COLUMN_ENTRY_ID,
				TransactionTable.COLUMN_EMPLOYEE_ID,
				TransactionTable.COLUMN_TRANSACTION_TIMESTAMP,
				TransactionTable.COLUMN_SENT_TIMESTAMP,
				TransactionTable.COLUMN_DURATION,
				TransactionTable.COLUMN_UNIQUE_VALUE,
				TransactionTable.COLUMN_EMPLOYEE_CODE,
				TransactionTable.COLUMN_JOB_CODE,
				TransactionTable.COLUMN_PHASE_CODE,
				TransactionTable.COLUMN_ACTIVITY_CODE,
				TransactionTable.COLUMN_DETAIL_VALUE,
				TransactionTable.COLUMN_GPS_LAT,
				TransactionTable.COLUMN_GPS_LON,
				TransactionTable.COLUMN_SEND_STATUS
		};
		Cursor c = dbHelper.queryTable(dbHelper.getReadableDatabase(), TransactionTable.TABLE_NAME, columnsToReturn, TransactionTable.COLUMN_ENTRY_ID + " ASC" );
		if(c.moveToFirst()) {
			do {
				HashMap<String, String> columnsToValues = new HashMap<String, String>();
				for(int i = 0; i < c.getColumnCount(); i++) {
					columnsToValues.put(c.getColumnName(i), c.getString(i));
				}
				TransactionInfo tInfo = new TransactionInfo(columnsToValues);
				if(checkTransactionDateValidity(tInfo.transactionTimestamp)){ // Valid time - For last 7 days 
					if(tInfo.uniqueValue.equalsIgnoreCase(EntryType.Job.entryName)) {
						lastJob = tInfo;
					}
					if(tInfo.uniqueValue.equals(EntryType.StartDay.entryName)) {
						lastJob = null;
					}
					if(tInfo.uniqueValue.equals(EntryType.EndDay.entryName)) {
						lastJob = null;
					}	
				}else{
					lastJob = null;
				}
				
			} while(c.moveToNext());
		}
		c.close();
		return lastJob;
	}

	// Run Digital Clock in Thread to Show Current Time
	public void TimeDisplay(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if(!mIsShouldPauseTimeThread){
					Date d = new Date();
					mLogTimeText.setVisibility(View.VISIBLE);
					mSyncStatusText.setVisibility(View.GONE);
					mString_Time = (String) DateFormat.format("hh:mm:ss a",d.getTime());
					mLogTimeText.setText(String.valueOf(mString_Time));
				}
			}
		});
	}

	// Runnable class for Digital Clock
	class TimeRunner implements Runnable{

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try{
					TimeDisplay();
					if(!mIsShouldPauseTimeThread){
						Thread.sleep(1000);	
					}else{
						Thread.sleep(5000);
					}

				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Log View Options");
		menu.add(1, 1, 0, getResources().getString(R.string.history));
		if(mIsMultipleEmployee){
			menu.add(1, 2, 0, getResources().getString(R.string.jobs_by_emp));
			menu.add(1, 3, 0, getResources().getString(R.string.hours_by_emp));
			menu.add(1, 4, 0, getResources().getString(R.string.hours_by_job));
		}else{
			menu.add(1, 2, 0, getResources().getString(R.string.my_jobs));
			menu.add(1, 3, 0, getResources().getString(R.string.my_hours));
			menu.add(1, 4, 0, getResources().getString(R.string.my_hours_by_job));	
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			mLogType.setText(getResources().getString(R.string.history));
			updateLogView("history"); 
			break;

		case 2:
			if(mIsMultipleEmployee)
				mLogType.setText(getResources().getString(R.string.jobs_by_emp));
			else
				mLogType.setText(getResources().getString(R.string.my_jobs));
			updateLogView("jobs_by_emp"); // False - Jobs by emp logs
			break;
		case 3:
			if(mIsMultipleEmployee)
				mLogType.setText(getResources().getString(R.string.hours_by_emp));
			else
				mLogType.setText(getResources().getString(R.string.my_hours));
			updateLogView("hours_by_emp");
			break;
		case 4:
			if(mIsMultipleEmployee)
				mLogType.setText(getResources().getString(R.string.hours_by_job));
			else
				mLogType.setText(getResources().getString(R.string.my_hours_by_job));
			updateLogView("hours_by_job");
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	// Start day function for Menu  
	private void StartDay() {
		// clear the bundle values 
		itrac_obj.mEmployee = " ";
		itrac_obj.mJob = " ";
		itrac_obj.mJobPhase = " ";
		itrac_obj.mActivity = " ";
		addNewUniqueEntryWithName(EntryType.StartDay.getEntryName());
		//updateLogView("history");
		updateLog();
	}
	//End Day function to display view in Log 
	private void EndDay() {
		addNewJobEntryWithDetails("("+mEmployeeID+")"+" "+ mEmployeeName, "0","" , "0");
		addNewUniqueEntryWithName(EntryType.EndDay.getEntryName());
		//updateLogView("history");
		updateLog();
	}

	// Menu option All_Return
	private void AllReturn() {
		mAllLunch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_lunch_selector, 0, 0, 0);
		addNewUniqueEntryWithName(EntryType.AllReturn.getEntryName());
		//updateLogView("history");

		// To change Employee Job Time



		updateLog();
	}

	// All_Lunch
	private void AllLunch() {
		mAllLunch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_return_selector, 0, 0, 0);
		addNewUniqueEntryWithName(EntryType.AllLunch.getEntryName());
		//updateLogView("history");
		updateLog();
	}

	// Menu option New 
	private void NewTask() {
		// Creating Group view for New Task 
		addNewJobEntryWithDetails(itrac_obj.mEmployee, itrac_obj.mJob, itrac_obj.mJobPhase, itrac_obj.mActivity);
		updateLog();
	}

	// Receiving Intent data from New Task Spinner result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);

		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case SECONDARY_ACTIVITY_REQUEST_CODE:
				if(mEndDay.getVisibility() != View.VISIBLE && mEnterDetails.getVisibility()!= View.VISIBLE && mAllLunch.getVisibility() != View.VISIBLE) // For first time add start day
					StartDay();
				Bundle extras = intent.getExtras();
				itrac_obj.mEmployee = (extras != null ? extras.getString("SelectedEmployee"):"nothing returned");
				itrac_obj.mJob = (extras != null ? extras.getString("SelectedJob"):"nothing returned").replaceAll("'", "\''");
				itrac_obj.mJobPhase = (extras != null ? extras.getString("SelectedPhase"):"nothing returned").replaceAll("'", "\''");
				itrac_obj.mActivity = (extras != null ? extras.getString("SelectedActivity"):"nothing returned").replaceAll("'", "\''");
				// pass the spinner values to new task function for Employees, jobs, phase and activity 
				NewTask();
				mEnterDetails.setVisibility(View.VISIBLE);
				mAllLunch.setVisibility(View.VISIBLE);
				mEndDay.setVisibility(View.VISIBLE);
				dbHelper.deleteJobEntryDetails();
				break;
			case JOB_DETAILS_REQUEST_CODE :
				ArrayList<HashMap<String, String>> mPendingJobDetails = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("pending_job_details");
				for(HashMap<String, String> map : mPendingJobDetails){
					if(!map.containsKey("Header")){
						addNewDetailEntryWithDetails(itrac_obj.mEmployee,itrac_obj.mJob, itrac_obj.mJobPhase, itrac_obj.mActivity, map.get("type"),map.get("value").replaceAll("'", "\''"));
					}
				}
				//updateLogView("history");
				updateLog();
				break;
			case REQUEST_CODE_RECOVER_PLAY_SERVICES : 
				if(checkPlayServices()){
					if(mUpdateGooglePlayServicesDialog != null && mUpdateGooglePlayServicesDialog.isShowing())
						mUpdateGooglePlayServicesDialog.dismiss();
					mLocationClient.connect();
				}else{
					Toast.makeText(this, "Google Play Services must be installed to get current location.",Toast.LENGTH_SHORT).show();
					//finish();	
				}

				break;
			default:
				break;
			}
		}else if(resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES){
			if(checkPlayServices()){
				if(mUpdateGooglePlayServicesDialog != null && mUpdateGooglePlayServicesDialog.isShowing())
					mUpdateGooglePlayServicesDialog.dismiss();
				mLocationClient.connect();
			}else{
				Toast.makeText(this, "Google Play Services must be installed to get current location.",Toast.LENGTH_SHORT).show();
				//finish();	
			}
		}


	}

	// To update log view depending upon options
	private void updateLog(){
		if(mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.history))){
			updateLogView("history");
		}else if(mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.jobs_by_emp)) || mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.my_jobs))){
			updateLogView("jobs_by_emp");
		}else if(mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.hours_by_emp)) || mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.my_hours))){
			updateLogView("hours_by_emp");
		}else if(mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.hours_by_job)) || mLogType.getText().toString().equalsIgnoreCase(getResources().getString(R.string.my_hours_by_job))){
			updateLogView("hours_by_job");
		}

	}


	// Location Listener class to read current GPS Location
	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			Log.v("location coordinates", loc.getLatitude() + " " + loc.getLongitude());
			GPSLatitutude = loc.getLatitude();
			GPSLongitude = loc.getLongitude();
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Log.v("location coordinates", "provider disabled");
			GPSLatitutude = 0.0;
			GPSLongitude = 0.0;
			Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			Log.v("location coordinates", "provider enabled");
			Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String arg0, int event, Bundle arg2) {
			Log.v("location coordinates", "status changed");

		}
	}

	// Preventing Default implementation of back button in log view to maintain session
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);    
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
	protected void onResume() {
		super.onResume();
		if(!new ManagePreferenceData(this).isSyncAlarmSet()) // IF not set, set sync alarm
			new ScheduleBackgroundTask(this).setRecurringAlarm();
		else
			Log.v("Alarm ","Already set");

		registerReceiver(mSyncstatusReceiver,new IntentFilter("SYNC_STATUS_ACTION"));
		
		refreshLogViews();
		
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint","Called");
		new ScheduleBackgroundTask(this).cancelAlarm();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop running timers for EmpHours and JobHours sub menus when activity goes background to save CPU usage and battery life
		if(EmpHoursTimer!=null){
			EmpHoursTimer.cancel();
			EmpHoursTimer=null;
		}if(JobHoursTimer!=null){
			JobHoursTimer.cancel();
			JobHoursTimer=null;
		}
		unregisterReceiver(mSyncstatusReceiver);

		// Stop Reading GPS values when activity goes Background
		//mLocationManager.removeUpdates(mLocationListener);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	// Life Cycle Methods
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Force stop the background thread sync every 5sec with web service
		if(EmpHoursTimer!=null){
			EmpHoursTimer.cancel();
			EmpHoursTimer=null;
		}if(JobHoursTimer!=null){
			JobHoursTimer.cancel();
			JobHoursTimer=null;
		}
		if(mLocationClient != null && mLocationClient.isConnected()){
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}
		//mLocationManager.removeUpdates(mLocationListener);
		// Unregister the broadcast receiver for receiving network notifications

	}

	// Alert pop up to show corresponding message
	private void showAlert(String title,final String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(LogiTrac.this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}


	// Alert pop up with options for confirmation
	private void showOptionsAlert(String title,final String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(LogiTrac.this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(message.equalsIgnoreCase("End Day?")){
					EndDay();
					mEnterDetails.setVisibility(View.INVISIBLE);
					mAllLunch.setVisibility(View.INVISIBLE);
					mEndDay.setVisibility(View.INVISIBLE);	
				}else{
					AllLunch();
					//mAllLunch.setText("All Return");
					setLunchTextBasedUponEmployee("lunch");
					mBeginJob.setVisibility(View.INVISIBLE);
				}
			}
		});
		builder.show();
	}

	// To refresh custom expandable list view (ScrollView with Linear Layout with Textviews)
	private void refreshCustomExpandableList(ArrayList<LogListDetail> treeList){
		mCustomExpandleListViewLayout.removeAllViews();
		for(int i=0 ; i< treeList.size() ; i++){
			final LogListDetail mCurrentDetail = treeList.get(i);
			View convertView = LayoutInflater.from(this).inflate(R.layout.expandable_list, null, false);
			final LinearLayout mMainListContainer = (LinearLayout) convertView.findViewById(R.id.main_list_containerID);
			final TextView mMainParentGroupText = (TextView) convertView.findViewById(R.id.main_parent_textID);
			mMainParentGroupText.setText(mCurrentDetail.getGroup_item());
			//Color change for not Sync or Error
			System.out.println("LogListDetail mEmployeeCode------>"+mCurrentDetail.getStatus());
			if(!mCurrentDetail.getStatus().equalsIgnoreCase("Sent")){
				mMainParentGroupText.setTextColor(getResources().getColor(R.color.red_clr));
			}else{
				mMainParentGroupText.setTextColor(getResources().getColor(R.color.black_clr));
			}
			mMainParentGroupText.setPadding(0, 5, 0, 5);
			if(treeList.size()-1 == i){
				addInnerParentViewInstantly(mCurrentDetail, mMainParentGroupText, mMainListContainer);
			}
			mMainParentGroupText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(mCurrentDetail.getChild_items() != null && mCurrentDetail.getChild_items().size()>0){
						if(mMainListContainer.getChildCount() > 1){
							mMainParentGroupText.setCompoundDrawablePadding(3);
							mMainParentGroupText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rightlistarrow, 0, 0, 0);
							mMainListContainer.removeViews(1,  mCurrentDetail.getChild_items().size());
						}else{
							addInnerParentViewInstantly(mCurrentDetail, mMainParentGroupText, mMainListContainer);
						}
					}
				}
			});
			mCustomExpandleListViewLayout.addView(convertView);
		}
		mCustomExpandableListviewContainer.post(new Runnable() {

			@Override 
			public void run() { 
				mCustomExpandableListviewContainer.fullScroll(ScrollView.FOCUS_DOWN);
			} 
		});
	}

	// To manage Custom expandable listview 
	private void addInnerParentViewInstantly(LogListDetail mCurrentDetail,TextView mainParentGroupText,LinearLayout mainListContainer){
		if(mCurrentDetail.getChild_items().size()>0){
			mainParentGroupText.setCompoundDrawablePadding(3);
			mainParentGroupText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downlistarrow, 0, 0, 0);	
		}
		for(int j=0 ; j< mCurrentDetail.getChild_items().size() ; j++){
			final EmployeeLogDetail mEmployeeDetail = mCurrentDetail.getChild_items().get(j);
			View inner_parent_convertView = LayoutInflater.from(LogiTrac.this).inflate(R.layout.inner_parent_row, null, false);
			final LinearLayout mInnerParentContainer = (LinearLayout) inner_parent_convertView.findViewById(R.id.inner_parent_containerID);
			final TextView mInnerParentText = (TextView) inner_parent_convertView.findViewById(R.id.inner_parent_TextID);
			mInnerParentText.setText(mEmployeeDetail.getDayEvent());
			mInnerParentText.setTextColor(getResources().getColor(R.color.black_clr));
			//Color change for not Sync or Error
			System.out.println("mEmployeeCode------>"+mEmployeeDetail.getStatus());
			if(!mEmployeeDetail.getStatus().equalsIgnoreCase("Sent")){
				mInnerParentText.setTextColor(getResources().getColor(R.color.red_clr));
			}			
			mInnerParentText.setPadding((int)getResources().getDimension(R.dimen.tree_view_child_padding), 5, 0, 5);
			if(j == mCurrentDetail.getChild_items().size()-1 && mEmployeeDetail.getJobEvent().size()>0){
				addChildItemsInstantly(mEmployeeDetail, mInnerParentText, mInnerParentContainer);
			}

			mInnerParentText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mEmployeeDetail.getJobEvent() != null && mEmployeeDetail.getJobEvent().size()>0){
						if(mInnerParentContainer.getChildCount() > 1){
							mInnerParentContainer.removeViews(1, mEmployeeDetail.getJobEvent().size());
							mInnerParentText.setCompoundDrawablePadding(3);
							mInnerParentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rightlistarrow, 0, 0, 0);	
						}else{
							addChildItemsInstantly(mEmployeeDetail, mInnerParentText, mInnerParentContainer);	
						}

					}else{
						new AlertNotification().showAlertPopup(LogiTrac.this, "Entry Value", mInnerParentText.getText().toString(), null);
					}
				}
			});
			mainListContainer.addView(inner_parent_convertView,1+j);
		}
	}

	// To add Job entry as child instantly to day event
	private void addChildItemsInstantly(EmployeeLogDetail mEmployeeDetail,TextView mInnerParentText,LinearLayout mInnerParentContainer){
		if(mEmployeeDetail.getJobEvent().size() > 0) {
			mInnerParentText.setCompoundDrawablePadding(3);
			mInnerParentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downlistarrow, 0, 0, 0);	
		}
		for(int k=0 ; k< mEmployeeDetail.getJobEvent().size() ; k++){
			View child_row = LayoutInflater.from(LogiTrac.this).inflate(R.layout.child_row, null, false);
			final TextView child_text = (TextView) child_row.findViewById(R.id.inner_parent_child_TextID);
			child_text.setText(mEmployeeDetail.getJobEvent().get(k));
			child_text.setTextColor(getResources().getColor(R.color.black_clr));
			//Color change for not Sync or Error
			System.out.println("mEmployeeCode------>"+mEmployeeDetail.getStatus());
			if(!mEmployeeDetail.getStatus().equalsIgnoreCase("Sent")){
				child_text.setTextColor(getResources().getColor(R.color.red_clr));
			}
			child_text.setPadding((int)getResources().getDimension(R.dimen.tree_view_inner_child_padding), 10, 0, 0);
			child_text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new AlertNotification().showAlertPopup(LogiTrac.this, "Entry Value", child_text.getText().toString(), null);
				}
			});
			mInnerParentContainer.addView(child_row,1+k);
		}
	}

	// To update log view while refreshing get jobs
	public void refreshLogViews(){
		updateLog();
	}


	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//Log.v("Location changed listener", "called" + location.getLatitude() + " " + location.getLongitude() + " ");
		GPSLatitutude = location.getLatitude();
		GPSLongitude = location.getLongitude();
	}


	/* (non-Javadoc)
	 * @see com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener#onConnectionFailed(com.google.android.gms.common.ConnectionResult)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onConnected(android.os.Bundle)
	 */
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Location mCurrentLocation = mLocationClient.getLastLocation();
		if(mCurrentLocation != null){
			GPSLatitutude = mCurrentLocation.getLatitude();
			GPSLongitude = mCurrentLocation.getLongitude();	
		}
		mLocationClient.requestLocationUpdates(mLocationRequest, LogiTrac.this);
	}


	/* (non-Javadoc)
	 * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onDisconnected()
	 */
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	// To check whether google play services available or not
	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				showErrorDialog(status);
			} else {
				Toast.makeText(this, "Unable to fetch current location.", Toast.LENGTH_LONG).show();
				//finish();
			}
			return false;
		}
		return true;
	} 

	// To show corresponding error dialog while loading google map 
	void showErrorDialog(int code) {
		mUpdateGooglePlayServicesDialog = GooglePlayServicesUtil.getErrorDialog(code, this,REQUEST_CODE_RECOVER_PLAY_SERVICES);
		mUpdateGooglePlayServicesDialog.show();
	}
	
	private boolean checkTransactionDateValidity(String timestamp){
		Date d = null;
		boolean isValidDate = false;
		try {
			SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
			d = databaseFormat.parse(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date now = new Date();
		if(d != null) {
			long timeDiffHours = ((now.getTime() - d.getTime()) / 3600000);
			if(timeDiffHours < 24 * 7) {
				isValidDate = true;
			}
		}
		return isValidDate;
	}

}
