package com.trac.android.tractivity.V1.menu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trac.android.tractivity.V1.ManagePreferenceData;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.database.RegisteredTable.MaterialTable;
import com.trac.android.tractivity.logs.CustomAutoCompleteView;
import com.trac.android.tractivity.utils.AlertNotification;

import java.util.ArrayList;
import java.util.Calendar;

public class MaterialiTrac extends Activity implements OnClickListener{

	private EditText mQuantity;
	private Button mChooseMaterialPartnumber, mPartNoClearButton;
	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu;
	private ArrayList<String> mMaterialDetailsList = new ArrayList<String>();
	private ArrayList<String> queriedMaterialID,queriedMaterialName;
	public Calendar cal2;
	private DatabaseHelper mDatebaseHelper;
	private String selectedPartNo = null;
	CustomAutoCompleteView mPartNumber;
	// adapter for auto-complete
	ArrayAdapter<String> mPartNoAdapter;
	// just to add some initial value
	 String[] item = new String[] {"Please search..."};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.materialitrac);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.material));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.cancel));
		mTitleBarRightMenu.setText(getResources().getString(R.string.accept));
		mTitleBarRightMenu.setTypeface(null, Typeface.BOLD);
		mTitleBarLeftMenu.setOnClickListener(this);
		mTitleBarRightMenu.setOnClickListener(this);

		final String[] acceptableText = new String[]{"(",")","-","#"," ","/","\""};

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


		// Initialize xml widgets
		//mPartNumber = (EditText) findViewById(R.id.material_partnumber);
		try{
			//mJob.setVisibility(View.GONE);
			// autocompletetextview is in activity_main.xml
			mPartNumber = (CustomAutoCompleteView) findViewById(R.id.material_partnumber);
			mPartNoClearButton =  (Button) findViewById(R.id.material_clear_btn);

			// add the listener so it will tries to suggest while the user types
			mPartNumber.addTextChangedListener(new MaterialPartNoAutoCompleteTextChangedListener(this));

			// set our adapter
			mPartNoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
			mPartNumber.setAdapter(mPartNoAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mPartNumber.setFilters(new InputFilter[]{alphaNumericFilter});
		mQuantity = (EditText) findViewById(R.id.material_quantity);
		mChooseMaterialPartnumber = (Button) findViewById(R.id.choosepartnumber);
		registerForContextMenu(mChooseMaterialPartnumber);
		mChooseMaterialPartnumber.setOnClickListener(this);
		


		String[] columnsToReturn = {
				MaterialTable.COLUMN_ENTRY_ID,
				MaterialTable.COLUMN_MATERIAL_IDENTITY,
				MaterialTable.COLUMN_MATERIAL_DESCRIPTION,
				MaterialTable.COLUMN_MODIFIED_DATE
		};
		queriedMaterialID = new ArrayList<String>();
		queriedMaterialName = new ArrayList<String>();
		mDatebaseHelper = DatabaseHelper.getInstance(this);
		Cursor c = mDatebaseHelper.queryTable(mDatebaseHelper.getReadableDatabase(), MaterialTable.TABLE_NAME, columnsToReturn, MaterialTable.COLUMN_ENTRY_ID + " ASC");
		if(c.moveToFirst()) {
			do {
				String matID = c.getString(c.getColumnIndex(MaterialTable.COLUMN_MATERIAL_IDENTITY));
				String matDesc = c.getString(c.getColumnIndex(MaterialTable.COLUMN_MATERIAL_DESCRIPTION));
				mMaterialDetailsList.add(String.format("(%s) %s", matID, matDesc));
				queriedMaterialID.add(String.format("%s", matID));
				queriedMaterialName.add(String.format("%s", matDesc));
				//Toast.makeText(getApplicationContext(), matDesc, Toast.LENGTH_LONG).show();
			} while(c.moveToNext());
		}
		c.close();

		//TODO Load Materials through database
		// Load Material list entry into drop down list to choose values
		/*ReturnedMaterialDetailsSize=TractivityiTrac_WebServices.mReceivedWebServiceMaterialDetails.size();
		for(int i=0;i<TractivityiTrac_WebServices.mReceivedWebServiceMaterialDetails.size();i++){
			mMaterialDetailsList.add( "(" + TractivityiTrac_WebServices.mReceivedWebServiceMaterialDetails.get(i).get("MaterialPartnumber").trim()+ ")" + " " + TractivityiTrac_WebServices.mReceivedWebServiceMaterialDetails.get(i).get("MaterialPartnumberDescription").trim());
		}*/

		// Listener for OK button in Material Charge
		/*mOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MaterialPartValue = mPartNumber.getText().toString(); 
				MaterialQuantityValue = mQuantity.getText().toString();

				// Add Material part value to sync array list, if available
				if(MaterialPartValue.length() > 0) {
					//Adding values to mLog in LogiTrac Activity to Sync with Web service
					mMap = new HashMap<String, String>();
					cal2 = Calendar.getInstance();
					String StartedDateTime = dateFormat.format(cal2.getTime());
					String MPNValue = "MPN";
					mMap.put("StartedDateTime",StartedDateTime);
					mMap.put("MenuInfo1", MPNValue);
					mMap.put("Submenuvalue",MaterialPartValue);
					LogiTrac.mLog.add(mMap);
					LogiTrac.mSubmenuLog.add(mMap);
				} else {
				}

				// Add MQTY value to sync in web service
				if(MaterialQuantityValue.length() > 0) {
					mMap2 = new HashMap<String, String>();
					Calendar cal2;
					cal2= Calendar.getInstance();
					String StartedDateTime = dateFormat.format(cal2.getTime());
					String MQTYValue = "MQTY";
					mMap2.put("StartedDateTime",StartedDateTime);
					mMap2.put("MenuInfo1", MQTYValue);
					mMap2.put("Submenuvalue",MaterialQuantityValue);
					LogiTrac.mLog.add(mMap2);
					LogiTrac.mSubmenuLog.add(mMap2);
				} else {
				}	
				finish();
			}
		});*/

		// Cancel Button listener for Material Charge
		/*mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});*/




		/*mPartNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:

						break;
					case EditorInfo.IME_ACTION_NEXT:
						// next stuff
						String text = mPartNumber.getText().toString();
						if (!text.equalsIgnoreCase("")) {
							System.out.println(queriedMaterialID);
							System.out.println(text);
							System.out.println(mPartNoAdapter.getCount());
							if (mPartNoAdapter.getCount() == 1) {
								mPartNumber.setText(mPartNoAdapter.getItem(0));
								selectedPartNo = mPartNoAdapter.getItem(0);
							} else {
								ShowAlert("Material Doesn't Exist");
								mPartNumber.getText().clear();
								selectedPartNo = null;
							}
						} else {
							ShowAlert("Select Material");
							mPartNumber.getText().clear();
							selectedPartNo = null;
						}
						break;
				}
				return false;
			}
		});*/

		mPartNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				String text = mPartNumber.getText().toString();
				if (!text.equalsIgnoreCase("")) {
					System.out.println(queriedMaterialID);
					System.out.println(text);
					System.out.println(mPartNoAdapter.getCount());
					if(queriedMaterialID.contains(text)){
						int mIndex = queriedMaterialID.indexOf(text);
						try {
							if (mIndex != -1) {
								mPartNumber.setText(mMaterialDetailsList.get(mIndex));
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							ae.printStackTrace();
						}
					}else if (isMaterialStringAvail(text)!=-1) {
						int mIndex = isMaterialStringAvail(text);
						try {
							if (mIndex != -1) {
								mPartNumber.setText(mMaterialDetailsList.get(mIndex));
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							ae.printStackTrace();
						}
					} else {
						ShowAlert("Material Doesn't Exist");
						mPartNumber.getText().clear();
						selectedPartNo = null;
					}
				}
			}
		});

		/*mQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (mPartNumber.getText().toString().equalsIgnoreCase("")) {
					ShowAlert("Select Material");
				} else if (selectedPartNo == null) {
					ShowAlert("Material Doesn't Exist");
					mPartNumber.getText().clear();
				}
			}
		});*/

		/*mQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						if (mPartNumber.getText().toString().equalsIgnoreCase("")) {
							ShowAlert("Select Material");
							mQuantity.clearFocus();
							mPartNumber.requestFocus();
						}
						if (selectedPartNo == null) {
							ShowAlert("Material Doesn't Exist");
							mPartNumber.getText().clear();
							mQuantity.clearFocus();
							mPartNumber.requestFocus();
						}
						if (mQuantity.getText().toString().equalsIgnoreCase("")) {
							ShowAlert("Enter quantity");
							mPartNumber.clearFocus();
							mQuantity.requestFocus();
						}
						break;
					case EditorInfo.IME_ACTION_NEXT:
						// next stuff

						break;
				}
				return false;
			}
		});*/

		mPartNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mPartNumber.length() > 0) {
					mPartNoClearButton.setVisibility(View.VISIBLE);
				} else {
					mPartNoClearButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mPartNoClearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mPartNumber.setText("");
			}
		});



		SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		boolean mpn = customSharedPreference.getBoolean(getResources().getString(R.string.mpn), false);
		boolean qty = customSharedPreference.getBoolean(getResources().getString(R.string.mqty), false);


		// Check if Material part number is true, if false disable the entry
		if(mpn == true){
			if(getIntent().getExtras()!=null) {
				if (getIntent().getStringExtra(getResources().getString(R.string.material)) != null) {
					//mPartNumber.setText(getIntent().getExtras().getString(getResources().getString(R.string.material)));
					String text =getIntent().getExtras().getString(getResources().getString(R.string.material));
					Log.i("In data",text);
					if (isMaterialStringAvail(text)!=-1) {
						int mIndex = isMaterialStringAvail(text);
						try {
							if (mIndex != -1) {
								mPartNumber.setText(mMaterialDetailsList.get(mIndex));
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							ae.printStackTrace();
						}
					}
				}
			}
		} else {
			mPartNumber.setEnabled(false);
			mPartNumber.setFocusable(false);
			mPartNumber.setClickable(false);
		}
		//Check if Material Quantity is true, if false disable the entry
		if(qty == true){
			if(getIntent().getStringExtra(getResources().getString(R.string.quantity))!=null){
				mQuantity.setText(getIntent().getExtras().getString(getResources().getString(R.string.quantity)));
			}/*else{
				mQuantity.setText("1");
			}*/

		} else {
			mQuantity.setEnabled(false);
			mQuantity.setFocusable(false);
			mQuantity.setClickable(false);
		}

	}

	//Check the job name matches exactly the same
	private Integer isMaterialStringAvail(String jobName){
		int mIndex = -1;
		ArrayList<String> temp = new ArrayList<String>();
		for(int i=0;i<mMaterialDetailsList.size();i++){
			if(mMaterialDetailsList.get(i).toLowerCase().contains(jobName.toLowerCase())) {
				temp.add(mMaterialDetailsList.get(i));
				mIndex = i;
			}
		}
		if (temp.size()!=1){
			mIndex = -1;
		}
		return mIndex;
	}

	// Called whenever an item in a context menu is selected	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String s = (String) item.getTitle();
		selectedPartNo = s;
		mPartNumber.setText(s);
		return true;  
	}

	// ShowAlert for Job code and Activity Spinner, if Empty
	private void ShowAlert(final String str) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MaterialiTrac.this);
		builder.setMessage(str);
		builder.setTitle("Invalid");
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

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

	// Called when a context menu for the view 
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select Material Part Number");  
		ArrayList<String> menuItems = new ArrayList<String>(mMaterialDetailsList);
		for (int i = 0; i<menuItems.size(); i++) {
			menu.add(Menu.NONE, i, i, menuItems.get(i));
		}
	}

	// OnClick listener for context menu (MPN button to choose Material part values)
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.leftmenuID:
            finish();
			break;

		case R.id.rightmenuID:
			
			if(mPartNumber.getText().toString().trim().length() == 0){
			    new AlertNotification().showAlertPopup(MaterialiTrac.this, "Invalid action", "Select Material", mPartNumber);	
			}else if(mQuantity.getText().toString().trim().length() == 0){
				new AlertNotification().showAlertPopup(MaterialiTrac.this, "Invalid action", "Enter quantity", mQuantity);
			}else{
				Intent materialExtras = new Intent();
				materialExtras.putExtra(getResources().getString(R.string.material), mPartNumber.getText().toString());
				materialExtras.putExtra(getResources().getString(R.string.quantity), mQuantity.getText().toString());
				setResult(RESULT_OK,materialExtras);
				finish();
			}
			break;

		case R.id.choosepartnumber:
			this.openContextMenu(mChooseMaterialPartnumber);
			break;

		default:
			break;
		}


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

	public String[] getItemsFromDb_Material_PartNo(String searchTerm){
		// add items on the array dynamically
		ArrayList<String> products = mDatebaseHelper.readPartNoFromDB(searchTerm);
		int rowCount = products.size();

		String[] item = new String[rowCount];


		for (int i=0;i<rowCount;i++) {

			item[i] = products.get(i);

		}

		return item;
	}

	public class MaterialPartNoAutoCompleteTextChangedListener implements TextWatcher {

		public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
		Context context;

		public MaterialPartNoAutoCompleteTextChangedListener(Context context){
			this.context = context;
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence userInput, int start, int before, int count) {

			// if you want to see in the logcat what the user types
			Log.i(TAG, "User input: " + userInput);



			// query the database based on the user input
			item = getItemsFromDb_Material_PartNo(userInput.toString());

			// update the adapater
			mPartNoAdapter.notifyDataSetChanged();
			mPartNoAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, item);
			mPartNumber.setAdapter(mPartNoAdapter);

		}

	}

}

