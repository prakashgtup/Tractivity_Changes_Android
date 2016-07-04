package com.trac.android.tractivity.logs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.trac.android.tractivity.V1.ManagePreferenceData;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.V1.webservices.TractivityiTrac_classvariables;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.database.RegisteredTable.ActivityTable;
import com.trac.android.tractivity.database.RegisteredTable.BudgetedActivityTable;
import com.trac.android.tractivity.database.RegisteredTable.EmployeeTable;
import com.trac.android.tractivity.database.RegisteredTable.JobTable;
import com.trac.android.tractivity.database.RegisteredTable.PhaseTable;
import com.trac.android.tractivity.utils.AlertNotification;
import com.trac.android.tractivity.utils.NetworkCheck;

import java.util.ArrayList;

/**
 * 
 * Activity to host and manage add new job entry
 *
 */

public class NewiTrac extends Activity implements OnClickListener {

	private AutoCompleteTextView mEmployee, mJobPhase;
	private Button mEmployeeButton,mJobButton,mJobPhaseButton,mActivityButton,mEmployeeClearButton,mJobClearButton,mJobPhaseClearButton,mActivityClearButton;
	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu, mEmployeeText, mJobPhaseText,mAcceptJob,mPreviousJobText;
	private static final String ALERT_JOBCODE = "Please Choose the Job";
	private static final String ALERT_ACTIVITY = "Please Choose an Activity";
	private static final String ALERT_TITLE = "Invalid";
	private TractivityiTrac_classvariables iTracObj;
	private String mreceivedemployee,mreceivedjob,mreceivedphase,mreceivedactivity;
	private DialogInterface.OnClickListener addNewJobCodeListener;
	private DialogInterface.OnClickListener addNewEmployeeCodeListener;
	private DialogInterface.OnClickListener addNewJobPhaseCodeListener;
	//New Database variables
	private ArrayList<String> queriedEmployees;
	private ArrayList<String> queriedJobs,queriedJobCode;
	private ArrayList<String> queriedPhases;
	private ArrayList<String> queriedActivities, queriedActivityCodes;
	private String selectedEmployeeID,selectedEmployee,selectedJobID,selectedJobPhaseID,selectedActivityID;
	private String selectedJob;	
	private String selectedJobPhase;	
	private String selectedActivity;
	private DatabaseHelper mDatabaseHelper;
	private boolean mEmployeeDetailsReceived; // To know whether employee details received from log screen or for fresh addition
	private boolean mJobSelected = false;
	CustomAutoCompleteView mJob,mActivity;

	// adapter for auto-complete
	ArrayAdapter<String> mJobAdapter,mActivityAdapter;


	// just to add some initial value
	static String[] item = new String[] {"Please search..."};
	// just to add some initial value

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.newitrac);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);
		// close the Keypad from page start	
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.enter_job));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.history));
		mTitleBarLeftMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
		mTitleBarRightMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh_job_selector, 0, 0, 0);
		mTitleBarRightMenu.setText("");
		// Declare & initiate the Layout attributes
		//mEmployeeSpinner = (Spinner) findViewById(R.id.employee_spinner);
		mEmployee = (AutoCompleteTextView) findViewById(R.id.employee_filler);
		mEmployeeButton = (Button) findViewById(R.id.chooseemployeenumber);
		mEmployeeClearButton = (Button) findViewById(R.id.employee_clear_btn);
		//mJobSpinner = (Spinner)findViewById(R.id.job_spinner);
		//mJob = (AutoCompleteTextView) findViewById(R.id.job_filler);
		mJobButton = (Button) findViewById(R.id.choosejobnumber);
		mJobClearButton = (Button) findViewById(R.id.job_clear_btn);
		//mJobPhaseSpinner = (Spinner) findViewById(R.id.jobphase_spinner);
		mJobPhase = (AutoCompleteTextView) findViewById(R.id.jobphase_filler);
		mJobPhaseButton = (Button) findViewById(R.id.choosejobphasenumber);
		mJobPhaseClearButton = (Button) findViewById(R.id.jobphase_clear_btn);
		//mActivitySpinner = (Spinner) findViewById(R.id.activity_spinner);
		//mActivity = (AutoCompleteTextView) findViewById(R.id.activity_filler);
		mActivityButton = (Button) findViewById(R.id.chooseactivityenumber);
		mActivityClearButton = (Button) findViewById(R.id.activity_clear_btn);
		mAcceptJob = (TextView) findViewById(R.id.accept_jobID);
		mPreviousJobText = (TextView) findViewById(R.id.previous_job_textID);
		//mActivityButton.setEnabled(false);
		//mActivityButton.setClickable(false);
		//mEmployee.setFocusable(false);

		mDatabaseHelper = DatabaseHelper.getInstance(this);
		//Load Database values
		queryEmployeeTable();
		queryJobTable();
		queryPhaseTable("0");
		queryActivityTable("-1", "-1");

		enableAdapter(mEmployee, queriedEmployees);
		//enableAdapter(mJob, queriedJobs);		
		float scale = getResources().getDisplayMetrics().density;
		int padding_right = (int) (50 * scale + 0.5f);	
		int padding_left = (int) (10 * scale + 0.5f);	
		
		
		try{     
			//mJob.setVisibility(View.GONE);
			// autocompletetextview is in activity_main.xml
			mJob = (CustomAutoCompleteView) findViewById(R.id.job_autocomplete);	
			
			// add the listener so it will tries to suggest while the user types
			mJob.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));

			// set our adapter
			mJobAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
			mJob.setAdapter(mJobAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{     
			//mJob.setVisibility(View.GONE);
			// autocompletetextview is in activity_main.xml
			mActivity = (CustomAutoCompleteView) findViewById(R.id.activity_autocomplete);

			// add the listener so it will tries to suggest while the user types
			mActivity.addTextChangedListener(new CustomAutoCompleteTextChangedListener_Activity(this));

			// set our adapter
			mActivityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
			mActivity.setAdapter(mActivityAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mEmployee.setPadding(padding_left,0,padding_right,0);
		mJob.setPadding(padding_left,0,padding_right,0);
		mJobPhase.setPadding(padding_left,0,padding_right,0);
		mActivity.setPadding(padding_left,0,padding_right,0);

		/*addNextButtonListener(mEmployee, mEmployee, mJob);
		addNextButtonListener(mJob, mEmployee, mJobPhase);
		addNextButtonListener(mJobPhase, mJob, mActivity);
		addNextButtonListener(mActivity, mJobPhase, mActivity);*/
		//addNextButtonListener(mOk, mActivity, mEmployee);

		registerForContextMenu(mEmployeeButton);
		registerForContextMenu(mJobButton);
		registerForContextMenu(mJobPhaseButton);
		registerForContextMenu(mActivityButton);

		mEmployeeButton.setOnClickListener(this);
		mEmployeeClearButton.setOnClickListener(this);
		mJobButton.setOnClickListener(this);
		mJobClearButton.setOnClickListener(this);
		mJobPhaseButton.setOnClickListener(this);
		mJobPhaseClearButton.setOnClickListener(this);
		mActivityButton.setOnClickListener(this);
		mActivityClearButton.setOnClickListener(this);
		mEmployeeText = (TextView) findViewById(R.id.EmployeeText);
		mJobPhaseText = (TextView) findViewById(R.id.JobPhaseText);

		if(queriedEmployees.size() == 1) {
			selectEmployee(queriedEmployees.get(0));
		}

		final String[] acceptableText = new String[]{"(",")","-","#"," "};

		InputFilter alphaNumericFilter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence arg0, int arg1, int arg2, Spanned arg3, int arg4, int arg5) {
				boolean good = false;
				for(int k = arg1; k < arg2; k++)
				{   
					String str0 = Character.toString(arg0.charAt(k));
					if(Character.isLetterOrDigit(arg0.charAt(k))) {
						good = true;
					}
					for(int i = 0; i < acceptableText.length; i++) {
						if(str0.equals(acceptableText[i])) {
							good = true;
						}
					}
				}
				if(arg0.length() == 0) {
					good = true;
				}
				if(good) {
					//Accept original statement
					return null;
				} else {
					return "";
				}
			}   
		};

		mEmployee.setFilters(new InputFilter[]{alphaNumericFilter});
		mJob.setFilters(new InputFilter[]{alphaNumericFilter});
		mJobPhase.setFilters(new InputFilter[]{alphaNumericFilter});
		mActivity.setFilters(new InputFilter[]{alphaNumericFilter});

		// Read the values from log screen, to show the previously selected item on new screen
		Bundle mvalues=getIntent().getExtras().getBundle("extra_value");
		mreceivedemployee = mvalues.getString("employee");
		mreceivedjob = mvalues.getString("job");
		mreceivedphase = mvalues.getString("jobphase");
		mreceivedactivity = mvalues.getString("activity");

		if(mreceivedemployee != null && mreceivedemployee.equalsIgnoreCase("")) {
			mAcceptJob.setVisibility(View.GONE);
			mPreviousJobText.setVisibility(View.GONE);
		} else {
			mAcceptJob.setVisibility(View.VISIBLE);
			mPreviousJobText.setVisibility(View.VISIBLE);
			mEmployeeDetailsReceived = true;
			mPreviousJobText.setText(getResources().getString(R.string.previous_job_text));
			mJobSelected = true;
		}
		SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		boolean jobrev = customSharedPreference.getBoolean(getResources().getString(R.string.jobrev), false);
		final boolean employee_list = customSharedPreference.getBoolean(getResources().getString(R.string.emplist), false);
		final int mEmployeeID = customSharedPreference.getInt(getResources().getString(R.string.empId), 0);
		String mEmployeeName = customSharedPreference.getString(getResources().getString(R.string.emp_name), "");

		// Show job list if user has permission
		if(jobrev == true){
		} else {
			mJobPhaseText.setVisibility(View.INVISIBLE);
			mJobPhaseButton.setVisibility(View.INVISIBLE);
			mJobPhase.setVisibility(View.INVISIBLE);
		}

		// Set permission for ShowiTracEmpList, for logged in user
		// If ShowiTracEmpList = true , allow user to select all employee , else logged in user name has to be displayed in EmpList which cannot alter by user.
		if(employee_list == true) {
			// Check logged in user has permission to see Employee list in new task, if no disable the employee list
			mEmployeeButton.setClickable(true);
			mEmployee.setClickable(true);
			if(!mreceivedemployee.equalsIgnoreCase("")) {
				selectedEmployee = mreceivedemployee;
				mEmployee.setText(mreceivedemployee);
			}
		} else {
			mEmployeeButton.setVisibility(View.INVISIBLE);			
			mEmployee.setClickable(false);
			mEmployee.setFocusable(false);
			mEmployeeText.setText("Employee Name:");
			mEmployee.setText("(" + mEmployeeID + ")" + " " + mEmployeeName);
			mreceivedemployee = selectedEmployee = "(" + mEmployeeID + ")" + " " + mEmployeeName;

		}

		mTitleBarLeftMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mTitleBarRightMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(new NetworkCheck().ConnectivityCheck(NewiTrac.this)){
					new GetJobsTask(NewiTrac.this,"New Job").execute(mEmployeeID);
				}else{
					new AlertNotification().showAlertPopup(NewiTrac.this, "No network", "Unable to get jobs, try again with network connection", null);
				}	
			}
		});


		mEmployee.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String var1 = (String) arg0.getItemAtPosition(arg2);
				selectEmployee(var1);
			}
		});

		mJob.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//String var1 = (String) arg0.getItemAtPosition(arg2);
				mJobSelected = true;
				selectJob(selectedJob);
			}
		});

		mJobPhase.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String var1 = (String) arg0.getItemAtPosition(arg2);
				selectJobPhase(var1);
			}
		});

		mActivity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//String var1 = (String) arg0.getItemAtPosition(arg2);
				selectActivity(selectedActivity);
			}
		});

		mAcceptJob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iTracObj = new TractivityiTrac_classvariables();
				iTracObj.mNewSelectedEmployee = selectedEmployee;
				iTracObj.mNewSelectedJob = selectedJob;
				iTracObj.mNewSelectedJobPhase = selectedJobPhase;
				iTracObj.mNewSelectedActivity = selectedActivity;
				Bundle bd = new Bundle();
				bd.putString("SelectedEmployee", selectedEmployee);
				bd.putString("SelectedJob", selectedJob);
				bd.putString("SelectedPhase", selectedJobPhase);
				bd.putString("SelectedActivity", selectedActivity);
				// Pass intent values to LogiTrac screen
				Intent loadedvalues = new Intent(getApplicationContext(), LogiTrac.class);
				loadedvalues.putExtras(bd);
				loadedvalues.putExtra("SelectedEmployee", selectedEmployee);
				loadedvalues.putExtra("SelectedJob", selectedJob);
				loadedvalues.putExtra("SelectedPhase", selectedJobPhase);
				loadedvalues.putExtra("SelectedActivity", selectedActivity);
				setResult(RESULT_OK, loadedvalues);
				finish();
			}
		});

		mEmployee.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String text = mEmployee.getText().toString();
				if (arg1) {
					//mEmployee.setText("");
				} else {
					//Code to check if employee exists, and if not then try to add
					if (!queriedEmployees.contains(text)) {
						int id = -1;
						try {
							id = Integer.parseInt(text.replaceAll("\\D+", ""));
						} catch (NumberFormatException e) {
						}
						if (id != -1) {
							PromptYesNoEmployee(addNewEmployeeCodeListener);
						} else {
							if (!(text.length() == 0 || text.length() == 1)) {
								ShowAlert("Employee Doesn't Exist");
							}
							if (selectedEmployee != null) {
								mEmployee.setText(selectedEmployee);
							} else {
								//mEmployee.setText("Select Employee");
							}
						}
					}

					if (text.length() > 0 && mJob.getText().toString().length() > 0) {
						enableAdapter(mActivity, queriedActivities);
					}
				}
			}
		});

		mEmployee.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mEmployee.length() > 0 && employee_list == true) {
					mEmployeeClearButton.setVisibility(View.VISIBLE);
				} else {
					mEmployeeClearButton.setVisibility(View.GONE);
					mAcceptJob.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mJob.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String text = mJob.getText().toString();
				if (arg1) {
					//mJob.setText("");
				} else {
					System.out.println(queriedJobCode);
					System.out.println(text);
					System.out.println(mJobAdapter.getCount());
					//Code to check if job exists, and if not then try to add
					if (!queriedJobs.contains(text) && !queriedJobCode.contains(text)) {
						boolean isJobAvail = false;
						if(!queriedJobs.contains(text)) {
							isJobAvail = isJobNamesAvail(text);
						}
						Log.i("isJobAvail",isJobAvail+"");
						if(isJobAvail) {
							int id = -1;
							try {
								id = Integer.parseInt(text.replaceAll("\\D+", ""));
							} catch (NumberFormatException e) {
							}
							if (id != -1) {
								PromptYesNoJob(addNewJobCodeListener);
							} else {
								if (!(text.length() == 0 || text.length() == 1)) {
									ShowAlert("Job Doesn't Exist");
									mJobSelected = false;
								}
								if (selectedJob != null) {
									mJob.setText(selectedJob);
									mJobSelected = true;
								} else {
									mJob.getText().clear();
								}
							}
						} else {
							int id = -1;
							try {
								id = Integer.parseInt(text.replaceAll("\\D+", ""));
							} catch (NumberFormatException e) {
							}
							if (id != -1) {
								PromptYesNoJob(addNewJobCodeListener);
							} else if(mJobAdapter.getCount()==1){
								mJob.setText(mJobAdapter.getItem(0));
								mJobSelected = true;
							}else {
								ShowAlert("Job Doesn't Exist");
								mJobSelected = false;
							}
						}
					} else {
						int mIndex = queriedJobCode.indexOf(selectedJob);
						if (mIndex == -1) {
							mIndex = queriedJobs.indexOf(selectedJob);
						}
						try {
							if (mIndex != -1) {
								mJob.setText(queriedJobs.get(mIndex));
								mJobSelected = true;
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							ae.printStackTrace();
						}
					}
					if (text.length() > 0 && mEmployee.getText().toString().length() > 0) {
						enableAdapter(mActivity, queriedActivities);
					}
				}
				validateJobEntry();
			}
		});

		mJob.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mJob.length() > 0) {
					mJobClearButton.setVisibility(View.VISIBLE);
				} else {
					mJobClearButton.setVisibility(View.GONE);
					mAcceptJob.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mJobPhase.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String text = mJobPhase.getText().toString();
				if (arg1) {
					//mJobPhase.setText("");
				} else {
					if (!queriedPhases.contains(text)) {
						/*if((text.length() == 0 || text.length() == 1)) {
							ShowAlert("Job Phase Doesn't Exist");
						}*/
						int id = -1;
						try {
							id = Integer.parseInt(text.replaceAll("\\D+", ""));
						} catch (NumberFormatException e) {
						}
						if (id != -1) {
							PromptYesNoJobPhase(addNewJobPhaseCodeListener);
						} else {
							if (!(text.length() == 0 || text.length() == 1)) {
								ShowAlert("JobPhase Doesn't Exist");
							}
							if (selectedJobPhase != null) {
								mJobPhase.setText(selectedJobPhase);
							} else {
								mJobPhase.getText().clear();
								//mJobPhase.setText("Select Job Phase");
							}
						}
					}
					if (text.length() > 0 && mEmployee.getText().toString().length() > 0) {
						enableAdapter(mActivity, queriedActivities);
					}
				}

			}
		});

		mJobPhase.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mJobPhase.length() > 0) {
					mJobPhaseClearButton.setVisibility(View.VISIBLE);
				} else {
					mJobPhaseClearButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		mActivity.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String text = mActivity.getText().toString();
				if (arg1) {
					//mActivity.setText("");
				} else {
					if (!queriedActivities.contains(text) && !queriedActivityCodes.contains(text)) {
						if (!(text.length() == 0 || text.length() == 1)) {
							ShowAlert("Activity Doesn't Exist");
						}
						if (selectedActivity != null) {
							mActivity.setText(selectedActivity);
						} else {
							//mActivity.setText("Select Activity");
						}
					}
				}
				validateJobEntry();
			}
		});

		mActivity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						String text = mActivity.getText().toString();
						if (!queriedActivities.contains(text) && !queriedActivityCodes.contains(text)) {
							boolean isActivityAvail = false;
							if (!queriedActivities.contains(text)) {
								isActivityAvail = isActivityNameAvail(text);
							}
							Log.i("isActivityAvail", isActivityAvail + "");
							if (isActivityAvail) {
								if (!(text.length() == 0 || text.length() == 1)) {
									ShowAlert("Activity Doesn't Exist");
								}
								if (selectedActivity != null) {
									mActivity.setText(selectedActivity);
								} else {
									//mActivity.setText("Select Activity");
								}
							} else if (mActivityAdapter.getCount() == 1) {
								mActivity.setText(mActivityAdapter.getItem(0));
							} else {
								ShowAlert("Activity Doesn't Exist");
							}
						}else {
							int mIndex = queriedActivityCodes.indexOf(selectedActivity);
							if (mIndex == -1) {
								mIndex = queriedActivities.indexOf(selectedActivity);
							}
							try {
								if (mIndex != -1) {
									mActivity.setText(queriedActivities.get(mIndex));
								}
							} catch (ArrayIndexOutOfBoundsException ae) {
								ae.printStackTrace();
							}
						}
						validateJobEntry();
						break;
					case EditorInfo.IME_ACTION_NEXT:
						// next stuff
						break;
				}
				return false;
			}
		});

		mActivity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mActivity.length()>0){
					mActivityClearButton.setVisibility(View.VISIBLE);
				}else{
					mActivityClearButton.setVisibility(View.GONE);
					mAcceptJob.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {                

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});




		/*mDetails.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
			{
				if(TPOJO.mNote == false){
					menu.add(Menu.NONE, NOTE_ITEM, 0, "Note").setEnabled(false);
		    	}else{
		    		menu.add(Menu.NONE, NOTE_ITEM, 0, "Note");
		    	}
				menu.add(Menu.NONE, NOTE_ITEM, 0, "Note");
		    	// Visibility check for sub menu (Material Quantity) 
		    	if((mpn == false) && (qty == false)){
		    		menu.add(Menu.NONE, MATERIAL_ITEM, 1,"Material Charge").setEnabled(false);
		    	}else{
		    		menu.add(Menu.NONE, MATERIAL_ITEM, 1,"Material Charge");
		    	}
		    	// Visibility check for sub menu (ETC) 
		    	if(etc == false){
		    		menu.add(Menu.NONE, ETC_ITEM, 2,"ETC").setEnabled(false);
		    	}else{
		    		menu.add(Menu.NONE, ETC_ITEM, 2,"ETC");
		    	}
		    	// Visibility check for sub menu (QTY)
		    	if((pn == false) && (qty == false)){
		    		menu.add(Menu.NONE, QTY_ITEM, 3,"Qty Completed").setEnabled(false);
		    	}else{
		    		menu.add(Menu.NONE, QTY_ITEM, 3,"Qty Completed");
		    	}
			}
		});*/

		addNewEmployeeCodeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					selectedEmployee = mEmployee.getText().toString();
					selectedEmployeeID = getIDFromString(selectedEmployee);					
					ShowAlert("Employee Prompt","Employee Code added successfully.");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					mEmployee.setText(selectedEmployee != null ? selectedEmployee : "Select Employee");
					mEmployee.requestFocus();
					mEmployee.setSelection(mEmployee.getText().length());
					break;
				}
			}
		};

		addNewJobCodeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					selectedJob = mJob.getText().toString();
					selectedJobID = getIDFromString(selectedJob);
					queryPhaseTable(selectedJobID);
					mJobSelected = true;
					ShowAlert("Job Prompt","Job Code added successfully.");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					mJob.setText(selectedJob != null ? selectedJob : "");
					mJob.requestFocus();
					mJob.setSelection(mJob.getText().length());
					break;
				}
			}
		};

		addNewJobPhaseCodeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					selectedJobPhase = mJobPhase.getText().toString();
					selectedJobPhaseID = getIDFromString(selectedJobPhase);
					queryPhaseTable(selectedJobPhaseID);
					ShowAlert("JobPhase Prompt","Job Phase Code added successfully.");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					mJobPhase.setText(selectedJobPhase != null ? selectedJobPhase : "");
					mJobPhase.requestFocus();
					mJobPhase.setSelection(mJobPhase.getText().length());
					break;
				}
			}
		};

		selectedEmployee = mreceivedemployee;
		mEmployee.setText(mreceivedemployee);
		selectedJob = mreceivedjob;
		mJob.setText(mreceivedjob);		
		//For refreshing the jobphase
		selectJob(mreceivedjob);
		selectedJobPhase = mreceivedphase;		
		mJobPhase.setText(mreceivedphase);
		selectedActivity = mreceivedactivity;
		mActivity.setText(mreceivedactivity);
        validateJobEntry();
	} // End of OnCreate

	private void selectEmployee(String empName) {
		selectedEmployeeID = getIDFromString(empName);
		selectedEmployee = empName;
		mEmployee.setText(empName);
		mEmployee.dismissDropDown();		
		/*if(mJob.getText().toString().equalsIgnoreCase("")) {
			mJob.setText("Select Job");
		}*/
		if(mJob.getText().toString().length() > 0){
			enableAdapter(mActivity, queriedActivities);
		}
		validateJobEntry();
	}

	private void selectJob(String jobName) {
		selectedJobID = getIDFromString(jobName);
		selectedJob = jobName;
		mJob.setText(jobName);
		mJob.setSelection(mJob.getText().length());
		mJob.dismissDropDown();
		if(mEmployee.getText().toString().length() > 0){
			enableAdapter(mActivity, queriedActivities);
		}
		queryPhaseTable(selectedJobID);
		enableAdapter(mJobPhase, queriedPhases);
		mJobPhaseButton.setEnabled(true);
		mJobPhaseButton.setClickable(true);
		if(queriedPhases.size() == 0) {
			if(mJobPhase.getVisibility() == View.VISIBLE){
				selectedJobPhaseID = "0";
				selectedJobPhase = "()";
				// If job phase permission granted
				//mJobPhase.setText("()");
				//mPreviousJobText.setVisibility(View.VISIBLE);
				//mPreviousJobText.setText("Auto-selected Phase");	
			}else{
				selectedJobPhase = "";
			}
		} else if(queriedPhases.size() == 1) {
			if(mJobPhase.getVisibility() == View.VISIBLE){ // If job phase permission granted
				selectedJobPhase = queriedPhases.get(0);
				selectedJobPhaseID = getIDFromString(selectedJobPhase);
				mJobPhase.setText(selectedJobPhase);
				mPreviousJobText.setVisibility(View.VISIBLE);
				if(mEmployeeDetailsReceived){ // If employee details received from log screen we should not set not set text to auto selected phase 
					mEmployeeDetailsReceived = false; 
				}else{
					mPreviousJobText.setText("Auto-selected Phase");	
				}

			}else{
				selectedJobPhase = "";
			}
		} else {
			selectedJobPhaseID = "0";
			selectedJobPhase = "";
			mJobPhase.getText().clear();
			if(mEmployeeDetailsReceived){ // If employee details received from log screen we should not set not set text to auto selected phase 
				mEmployeeDetailsReceived = false; 
			}else{
				mPreviousJobText.setText("");	
			}
			//mJobPhase.setText("Select Job Phase");
		}
		validateJobEntry();
	}

	private void selectJobPhase(String jobPhaseName) {
		selectedJobPhaseID = getIDFromString(jobPhaseName);
		selectedJobPhase = jobPhaseName;
		mJobPhase.setText(jobPhaseName);
		mJobPhase.dismissDropDown();
		queryActivityTable(selectedJobID, selectedJobPhaseID);
		enableAdapter(mActivity, queriedActivities);
		if(queriedActivities.size() == 0) {
			selectedActivityID = "0";
			selectedActivity = "";
			mActivity.setText("");
		} else if(queriedActivities.size() == 1) {
			selectedActivity = queriedActivities.get(0);
			selectedActivityID = getIDFromString(selectedActivity);
			mActivity.setText(selectedActivity);
		} else {
			/*selectedActivityID = 0;
			selectedActivity = "";*/
			//mActivity.setText("Select Activity");
		}

	}

	private void selectActivity(String actName) {
		selectedActivityID = getIDFromString(actName);
		selectedActivity = actName;
		mActivity.setText(actName);
		mActivity.dismissDropDown();
		validateJobEntry();
		if(!mPreviousJobText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.previous_job_text)))
			mPreviousJobText.setText("");
	}

	private void validateJobEntry(){
		if (mJob.getText().toString().length() > 0 && mActivity.getText().toString().length() > 0) {
			//Code to check if job exists, and if not then try to add
			//ShowAlert("mJ"+mJobSelected);
			mAcceptJob.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mAcceptJob.getWindowToken(), 0);
		} else {
			mAcceptJob.setVisibility(View.INVISIBLE);
		}
	}

	//Check the job name matches exactly the same
	private boolean isJobNamesAvail(String jobName){
		boolean isAvailable = false;
		for(String singleJob : queriedJobs){
			if(singleJob.equalsIgnoreCase(jobName)){
				isAvailable = true;
			}
		}
		return isAvailable;
	}

	//Check the Activity name matches exactly the same
	private boolean isActivityNameAvail(String activityName){
		boolean isAvailable = false;
		for(String singleActivity : queriedActivities){
			if(singleActivity.equalsIgnoreCase(activityName)){
				isAvailable = true;
			}
		}
		return isAvailable;
	}


	/**
	 * Loads Employees from the database into queriedEmployees
	 */
	private void queryEmployeeTable() {
		if(queriedEmployees == null) {
			queriedEmployees = new ArrayList<String>();
		} else {
			queriedEmployees.clear();
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		String[] columnsToReturn = {
				EmployeeTable.COLUMN_ENTRY_ID,
				EmployeeTable.COLUMN_EMPLOYEE_ID,
				EmployeeTable.COLUMN_EMPLOYEE_NAME
		};
		Cursor c = mDatabaseHelper.queryTable(db, EmployeeTable.TABLE_NAME, columnsToReturn, EmployeeTable.COLUMN_ENTRY_ID + " ASC");
		if(c.moveToFirst()) {
			do {
				String empID = c.getString(c.getColumnIndex(EmployeeTable.COLUMN_EMPLOYEE_ID));
				String empName = c.getString(c.getColumnIndex(EmployeeTable.COLUMN_EMPLOYEE_NAME));
				queriedEmployees.add(String.format("(%s) %s", empID, empName));
			} while(c.moveToNext());
		}
		c.close();
	}


	// this function is used in CustomAutoCompleteTextChangedListener.java for job filtering
	public String[] getItemsFromDb(String searchTerm){
		selectedJob = searchTerm;
		// add items on the array dynamically
		ArrayList<String> products = mDatabaseHelper.readJobFromDB(searchTerm);
		int rowCount = products.size();

		String[] item = new String[rowCount];

		System.out.println("item---->"+item);
		for (int i=0;i<rowCount;i++) {

			item[i] = products.get(i);

		}

		return item;
	}

	public String[] getItemsFromDb_Activity(String searchTerm){
		selectedActivity = searchTerm;
		// add items on the array dynamically
		ArrayList<String> products = mDatabaseHelper.readActivityFromDB(searchTerm);
		int rowCount = products.size();

		String[] item = new String[rowCount];


		for (int i=0;i<rowCount;i++) {

			item[i] = products.get(i);

		}

		return item;
	}

	/**
	 * Loads Jobs from the database into queriedJobs
	 */
	private void queryJobTable() {
		if(queriedJobs == null) {
			queriedJobs = new ArrayList<String>();
			queriedJobCode = new ArrayList<String>();
		} else {
			queriedJobs.clear();
			queriedJobCode.clear();
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		String[] columnsToReturn = {
				JobTable.COLUMN_ENTRY_ID,
				JobTable.COLUMN_JOB_IDENTITY,
				JobTable.COLUMN_JOB_DESCRIPTION
		};
		Cursor c = mDatabaseHelper.queryTable(db, JobTable.TABLE_NAME, columnsToReturn, JobTable.COLUMN_ENTRY_ID + " ASC");
		if(c.moveToFirst()) {
			do {
				String jobID = c.getString(c.getColumnIndex(JobTable.COLUMN_JOB_IDENTITY));
				String jobDesc = c.getString(c.getColumnIndex(JobTable.COLUMN_JOB_DESCRIPTION));
				queriedJobs.add(String.format("(%s) %s", jobID, jobDesc));
				queriedJobCode.add(String.format("%s", jobID));
			} while(c.moveToNext());
		}
		c.close();
	}

	/**
	 * Loads Phases from the database into queriedPhases
	 */
	private void queryPhaseTable(String jobID) {
		if(queriedPhases == null) {
			queriedPhases = new ArrayList<String>();
		} else {
			queriedPhases.clear();
		}
		String[] columnsToReturn = {
				PhaseTable.COLUMN_ENTRY_ID,
				PhaseTable.COLUMN_PHASE_IDENTITY,
				PhaseTable.COLUMN_PHASE_DESCRIPTION
		};
		String whereClause = PhaseTable.COLUMN_JOB_IDENTITY + " == ?";
		String[] whereArgs = {jobID};
		Cursor c = mDatabaseHelper.queryTable(PhaseTable.TABLE_NAME, columnsToReturn, whereClause, whereArgs, PhaseTable.COLUMN_ENTRY_ID + " ASC");
		if(c.moveToFirst()) {
			do {
				String phaseID = c.getString(c.getColumnIndex(PhaseTable.COLUMN_PHASE_IDENTITY));
				String phaseDesc = c.getString(c.getColumnIndex(PhaseTable.COLUMN_PHASE_DESCRIPTION));
				queriedPhases.add(String.format("(%s) %s", phaseID, phaseDesc));
			} while(c.moveToNext());
		}
		c.close();
	}

	/**
	 * Loads Activities from the database into queriedActivities
	 */
	private void queryActivityTable(String jobID, String phaseID) {
		System.out.println("jobID------->"+jobID+"phaseI------>"+phaseID);
		if(queriedActivities == null) {
			queriedActivities = new ArrayList<String>();
			queriedActivityCodes = new ArrayList<String>();
		} else {
			queriedActivities.clear();
			queriedActivityCodes.clear();
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		String[] columnsToReturn = {
				ActivityTable.COLUMN_ENTRY_ID,
				ActivityTable.COLUMN_ACTIVITY_IDENTITY,
				ActivityTable.COLUMN_ACTIVITY_DESCRIPTION
		};
		Cursor c = mDatabaseHelper.queryTable(db, ActivityTable.TABLE_NAME, columnsToReturn, ActivityTable.COLUMN_ENTRY_ID + " ASC");
		if(c.moveToFirst()) {
			do {
				String actID = c.getString(c.getColumnIndex(ActivityTable.COLUMN_ACTIVITY_IDENTITY));
				String actDesc = c.getString(c.getColumnIndex(ActivityTable.COLUMN_ACTIVITY_DESCRIPTION));
				queriedActivities.add(String.format("(%s) %s", actID, actDesc));
				queriedActivityCodes.add((String.format("%s", actID)));
			} while(c.moveToNext());
		}
		c.close();
		if(!jobID.equalsIgnoreCase("-1") && !phaseID.equalsIgnoreCase("-1")) {
			String[] otherColumnsToReturn = {
					BudgetedActivityTable.COLUMN_ENTRY_ID,
					BudgetedActivityTable.COLUMN_ACTIVITY_IDENTITY
			};
			String whereClause = BudgetedActivityTable.COLUMN_JOB_IDENTITY + " == ? AND " + BudgetedActivityTable.COLUMN_PHASE_IDENTITY + " == ?";
			String[] whereArgs = {jobID, phaseID};
			c = mDatabaseHelper.queryTable(BudgetedActivityTable.TABLE_NAME, otherColumnsToReturn, whereClause, whereArgs, BudgetedActivityTable.COLUMN_ENTRY_ID + " ASC");
			if(c.moveToFirst()) {
				do {
					String actID = c.getString(c.getColumnIndex(BudgetedActivityTable.COLUMN_ACTIVITY_IDENTITY));
					String[] otherOtherColumnsToReturn = {
							ActivityTable.COLUMN_ENTRY_ID,
							ActivityTable.COLUMN_ACTIVITY_DESCRIPTION
					};
					whereClause = ActivityTable.COLUMN_ACTIVITY_IDENTITY + " == ?";
					String[] otherWhereArgs = {String.valueOf(actID)};
					Cursor curs = mDatabaseHelper.queryTable(ActivityTable.TABLE_NAME, otherOtherColumnsToReturn, whereClause, otherWhereArgs, ActivityTable.COLUMN_ENTRY_ID + " ASC");
					if(curs.moveToFirst()) {
						//Only expect 1 result, no need to iterate over cursor
						do {
							String actDesc = curs.getString(curs.getColumnIndex(ActivityTable.COLUMN_ACTIVITY_DESCRIPTION));
							queriedActivities.add(String.format("(%s) %s", actID, actDesc));
							queriedActivityCodes.add((String.format("%s", actID)));
						} while(curs.moveToNext());
					}
					curs.close();
				} while(c.moveToNext());
			}
			c.close();
		}

	}

	private void onClickOk(View v) {
		if(selectedJobPhase.equalsIgnoreCase("")) {
			selectedJobPhase = "( )";
		}
		if(selectedActivity.equalsIgnoreCase("")) {
			selectedActivity = "(000) Break";
		}
		if(selectedJob.equalsIgnoreCase("") || (selectedJob.equalsIgnoreCase("Select Job"))){
			ShowAlert(ALERT_JOBCODE);
		} else if(selectedActivity.equalsIgnoreCase("Select Activity")){
			ShowAlert(ALERT_ACTIVITY);
		} else {
			iTracObj = new TractivityiTrac_classvariables();
			iTracObj.mNewSelectedEmployee = selectedEmployee;
			iTracObj.mNewSelectedJob = selectedJob;
			iTracObj.mNewSelectedJobPhase = selectedJobPhase;
			iTracObj.mNewSelectedActivity = selectedActivity;
			Bundle bd = new Bundle();
			bd.putString("SelectedEmployee", selectedEmployee);
			bd.putString("SelectedJob", selectedJob);
			bd.putString("SelectedPhase", selectedJobPhase);
			bd.putString("SelectedActivity", selectedActivity);
			// Pass intent values to LogiTrac screen
			Intent loadedvalues = new Intent(getApplicationContext(), LogiTrac.class);
			loadedvalues.putExtras(bd);
			loadedvalues.putExtra("SelectedEmployee", selectedEmployee);
			loadedvalues.putExtra("SelectedJob", selectedJob);
			loadedvalues.putExtra("SelectedPhase", selectedJobPhase);
			loadedvalues.putExtra("SelectedActivity", selectedActivity);
			setResult(RESULT_OK, loadedvalues);
			finish();
		}
	}

	/**
	 * Enable the adapter for a auto-complete text view, to pick the user's auto-complete list to choose from.(Overwrites previous adaptation)
	 * @param par1View View to register
	 * @param par2List 
	 */
	public void enableAdapter(AutoCompleteTextView par1View, ArrayList<String> par2List) {
		ArrayList<String> menuItems = new ArrayList<String>(par2List);
		String[] var1 = new String[menuItems.size()];
		for(int i = 0; i < menuItems.size(); i++) {
			String var2 = menuItems.get(i);
			var1[i] = var2;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, var1);
		par1View.setThreshold(1);
		par1View.setAdapter(adapter);
	}

	private String getIDFromString(String var1) {
		if(var1.contains("(") && var1.contains(")")) {
			int pre = var1.indexOf("(");
			int post = var1.indexOf(")");
			String id = "-1";
			try {
				id = var1.substring(pre + 1, post);
			} catch(NumberFormatException e) {
			}
			return id;
		} else {
			return "-1";
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String s = (String) item.getTitle();		
		if(queriedEmployees.contains(s)) {			
			selectEmployee(s);
		}
		if(queriedJobs.contains(s)) {
			selectJob(s);
		}
		if(queriedPhases.contains(s)) {
			if(mJob.getText().toString().length()>0){
				selectJobPhase(s);	
			}else{
				mPreviousJobText.setText("Job Name must be selected");
			}

		}
		if(queriedActivities.contains(s)) {
			selectActivity(s);	
		}

		return true;
	}

	private class iTracKeyListener implements OnKeyListener {
		TextView view;
		TextView backTargetView;
		TextView targetView;

		/**
		 * Generates the listener that moves to the "targetView" when the Next button is clicked
		 * @param view TextView to append the listener to
		 * @param backTargetView TextView to move backwards to (if null then no action is taken)
		 * @param targetView TextView to move to (if null then no action is taken)
		 */
		public iTracKeyListener(TextView arg0, TextView arg1, TextView arg2) {
			view = arg0;
			backTargetView = arg1;
			targetView = arg2;
		}

		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
			//System.out.println(arg1);
			if(arg2.getAction() == KeyEvent.ACTION_DOWN)
			{
				//System.out.println(arg1);
				if (arg1 == KeyEvent.KEYCODE_ENTER || arg1 == KeyEvent.KEYCODE_DPAD_DOWN)
				{
					view.clearFocus();
					if(targetView != null)
					{
						targetView.requestFocus();
					}
					return true;
				}
				if (arg1 == KeyEvent.KEYCODE_DPAD_UP)
				{
					view.clearFocus();
					if(backTargetView != null)
					{
						backTargetView.requestFocus();
					}
					return true;
				}
				//if (arg1 == KeyEvent.)
				{

				}
			}
			return false;
		}
	}

	/**
	 * Generates the listener that moves to the "targetView" when the Next button is clicked
	 * @param arg0 TextView to append the listener to
	 * @param arg1 TextView to move backwards to (if null then no action is taken)
	 * @param arg2 TextView to move to (if null then no action is taken)
	 */
	private void addNextButtonListener(TextView arg0, TextView arg1, TextView arg2) {
		//System.out.println("Listener registered for " + arg0.toString() + ".");
		arg0.setOnKeyListener(new iTracKeyListener(arg0, arg1, arg2));
		arg0.setFocusable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choosejobnumber:
			//if(mEmployee.getText().toString().trim().length()>0){
			/*if(!queriedEmployees.contains(mEmployee.getText().toString())){
				mJob.requestFocus();
			}else{
				openContextMenu(findViewById(v.getId()));
			}*/
			openContextMenu(findViewById(v.getId()));
			/*}else{

			}*/

			break;
		case R.id.choosejobphasenumber:		
			if(mJob.getText().toString().length() == 0){
				mAcceptJob.setVisibility(View.INVISIBLE);
				mPreviousJobText.setVisibility(View.VISIBLE);
				mPreviousJobText.setText("Job Name must be selected");
			}else{
				if(!queriedJobs.contains(mJob.getText().toString())){				
					mJobPhase.requestFocus();	
				}else{					
					openContextMenu(findViewById(v.getId()));	
				}
			}
			break;

		case R.id.chooseactivityenumber:
			if(mEmployee.getText().toString().length() == 0 && mJob.getText().toString().length() == 0){
				mAcceptJob.setVisibility(View.INVISIBLE);
				mPreviousJobText.setVisibility(View.VISIBLE);
				mPreviousJobText.setText("Emp Name and Job Name must be selected");	
			}else if(mEmployee.getText().toString().length() == 0 && mJob.getText().toString().length() != 0){
				mAcceptJob.setVisibility(View.INVISIBLE);
				mPreviousJobText.setVisibility(View.VISIBLE);
				mPreviousJobText.setText("Emp Name must be selected");	
			}else if(mEmployee.getText().toString().length() != 0 && mJob.getText().toString().length() == 0){
				mAcceptJob.setVisibility(View.INVISIBLE);
				mPreviousJobText.setVisibility(View.VISIBLE);
				mPreviousJobText.setText("Job Name must be selected");
			}else{
				openContextMenu(findViewById(v.getId()));
				/*if(!queriedEmployees.contains(mEmployee.getText().toString()) || !queriedJobs.contains(mJob.getText().toString())){
					mActivity.requestFocus();
				}else{
					openContextMenu(findViewById(v.getId()));
				}*/
			}
			break;
		case R.id.employee_clear_btn:
			mEmployee.setText("");
			mAcceptJob.setVisibility(View.INVISIBLE);
			break;
		case R.id.job_clear_btn:
			mJob.setText("");
			mAcceptJob.setVisibility(View.INVISIBLE);
			break;
		case R.id.jobphase_clear_btn:
			mJobPhase.setText("");
			break;
		case R.id.activity_clear_btn:
			mActivity.setText("");
			mAcceptJob.setVisibility(View.INVISIBLE);
			break;
		default:
			openContextMenu(findViewById(v.getId()));
			break;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(v.getId() == R.id.chooseemployeenumber) {
			menu.setHeaderTitle("Select Employee");
			for(int i = 0; i < queriedEmployees.size(); i++) {
				menu.add(Menu.NONE, i, Menu.NONE, queriedEmployees.get(i));
			}
		}
		if(v.getId() == R.id.choosejobnumber) {
			menu.setHeaderTitle("Select Job");
			for(int i = 0; i < queriedJobs.size(); i++) {
				menu.add(Menu.NONE, i, Menu.NONE, queriedJobs.get(i));
			}
		}
		if(v.getId() == R.id.choosejobphasenumber) {
			menu.setHeaderTitle("Select Job Phase");
			for(int i = 0; i < queriedPhases.size(); i++) {
				menu.add(Menu.NONE, i, Menu.NONE, queriedPhases.get(i));
			}
		}
		if(v.getId() == R.id.chooseactivityenumber) {
			menu.setHeaderTitle("Select Activity");
			for(int i = 0; i < queriedActivities.size(); i++) {
				menu.add(Menu.NONE, i, Menu.NONE, queriedActivities.get(i));
			}
		}
	}

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint","Called");
		new ScheduleBackgroundTask(this).cancelAlarm();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.gc();
		System.runFinalization();
		System.runFinalizersOnExit(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!new ManagePreferenceData(this).isSyncAlarmSet()) // IF not set, set sync alarm
			new ScheduleBackgroundTask(this).setRecurringAlarm();
		else
			Log.v("Alarm ","Already set");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	// ShowAlert for Job code and Activity Spinner, if Empty
	private void ShowAlert(final String str) {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewiTrac.this);
		builder.setMessage(str);
		builder.setTitle(ALERT_TITLE);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (str.equalsIgnoreCase("Job Doesn't Exist")){
                    mJob.getText().clear();
                    mJob.requestFocus();
                }
                if (str.equalsIgnoreCase("Employee Doesn't Exist")){
                    mEmployee.getText().clear();
                    mEmployee.requestFocus();
                }
                if (str.equalsIgnoreCase("JobPhase Doesn't Exist")){
                    mJobPhase.getText().clear();
                    mJobPhase.requestFocus();
                }
                if (str.equalsIgnoreCase("Activity Doesn't Exist")){
                    mActivity.getText().clear();
                    mActivity.requestFocus();
                }
            }
        });
		/*// Create & Instantiate a alert
		AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
		// Show the alert in the view
		alert.show();*/
       	builder.setCancelable(false);
		builder.show();
	}

	// ShowAlert for new Job code or Job Phase Added Successfully
	private void ShowAlert(String title,String subject) {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewiTrac.this);
		builder.setMessage(subject);
		builder.setTitle(title);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		// Create & Instantiate a alert
		AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
		// Show the alert in the view
		alert.show();
	}

	private void PromptYesNoJob(DialogInterface. OnClickListener arg0) {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewiTrac.this);
		builder.setTitle("Job Prompt");
		builder.setMessage("Are you sure you want to add this Job Code?");
		builder.setPositiveButton("Yes", arg0);
		builder.setNegativeButton("No", arg0);
        builder.setCancelable(false);
		builder.show();
	}

	private void PromptYesNoJobPhase(DialogInterface. OnClickListener arg0) {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewiTrac.this);
		builder.setTitle("JobPhase Prompt");
		builder.setMessage("Are you sure you want to add this Job Phase Code?");
		builder.setPositiveButton("Yes", arg0);
		builder.setNegativeButton("No", arg0);
        builder.setCancelable(false);
		builder.show();
	}

	private void PromptYesNoEmployee(DialogInterface. OnClickListener arg0) {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewiTrac.this);
		builder.setTitle("Employee Prompt");
        builder.setMessage("Are you sure you want to add this Employee Code?");
        builder.setPositiveButton("Yes", arg0);
		builder.setNegativeButton("No", arg0);
        builder.setCancelable(false);
		builder.show();
	}
}
