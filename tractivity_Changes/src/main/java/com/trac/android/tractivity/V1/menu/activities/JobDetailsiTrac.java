/**
 * 
 */
package com.trac.android.tractivity.V1.menu.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trac.android.tractivity.V1.ManagePreferenceData;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.adapter.JobEntryDetailsAdapter;
import com.trac.android.tractivity.backgroundtask.ScheduleBackgroundTask;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.utils.AlertNotification;

/**
 * Activity to display and manage job details
 *
 */
public class JobDetailsiTrac  extends Activity implements OnClickListener{

	private TextView mMainTitle,mTitleBarLeftMenu,mTitleBarRightMenu,mCurrentJob,mEnterNote,mEnterMaterial,mEnterETC,mEnterQty;
	private final int ENTER_NOTES_REQUEST_CODE = 0,ENTER_MATERIALS_REQUEST_CODE = 1, ENTER_ETC_REQUEST_CODE = 2,ENTER_QTY_REQUEST_CODE = 3; 
	private ListView mJobDetailsListview;
	private DatabaseHelper mDatabaseHelper;
	private ArrayList<HashMap<String, String>> mJobEntryDetailsList,mPreviousJobEntryDetailsList;
	private JobEntryDetailsAdapter mJobEntryAdapter;
	private boolean mIsListHeaderAdded = false;
	private int mRowId=-1,mSelectedPosition=-1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.activity_jobdetails);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.job_details));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.cancel));
		mTitleBarRightMenu.setText(getResources().getString(R.string.accept));
		mTitleBarRightMenu.setTypeface(null, Typeface.BOLD);
		mCurrentJob = (TextView) findViewById(R.id.current_job_textID);
		mEnterNote = (TextView) findViewById(R.id.job_details_add_notesID);
		mEnterMaterial = (TextView) findViewById(R.id.job_details_add_materialID);
		mEnterETC = (TextView) findViewById(R.id.job_details_add_etcID);
		mEnterQty = (TextView) findViewById(R.id.job_details_add_qtyID);
		mJobDetailsListview = (ListView) findViewById(R.id.job_details_listID);
		mDatabaseHelper = DatabaseHelper.getInstance(this);
		// Adding click listeners for textview
		mTitleBarLeftMenu.setOnClickListener(this);
		mTitleBarRightMenu.setOnClickListener(this);
		mEnterNote.setOnClickListener(this);
		mEnterMaterial.setOnClickListener(this);
		mEnterETC.setOnClickListener(this);
		mEnterQty.setOnClickListener(this);
		mJobEntryDetailsList = new ArrayList<HashMap<String,String>>();
		mPreviousJobEntryDetailsList = new ArrayList<HashMap<String,String>>();
		if(getIntent().hasExtra("current_job")){ // To get job name from itent extras
			mCurrentJob.setText(getIntent().getExtras().getString("current_job"));
		}

		mPreviousJobEntryDetailsList  = mDatabaseHelper.getPreviousJobEntries(mPreviousJobEntryDetailsList);
		mJobEntryAdapter = new JobEntryDetailsAdapter(this, mPreviousJobEntryDetailsList);
		mJobDetailsListview.setAdapter(mJobEntryAdapter);
		
		SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		final boolean mpn = customSharedPreference.getBoolean(getResources().getString(R.string.mpn), false);
		final boolean qty = customSharedPreference.getBoolean(getResources().getString(R.string.qty), false);
		final boolean etc = customSharedPreference.getBoolean(getResources().getString(R.string.etc), false);
		//final boolean pn = customSharedPreference.getBoolean(getResources().getString(R.string.pn), false);
		
		// Visibility check for sub menu (Material Quantity) 
    	if((mpn == false) && (qty == false)){
    		mEnterMaterial.setVisibility(View.INVISIBLE);
    	}
    	
    	// Visibility check for sub menu (ETC) 
    	if(etc == false){
    		mEnterETC.setVisibility(View.INVISIBLE);
    	}
    	// Visibility check for sub menu (QTY)
    	if(qty == false){
    		mEnterQty.setVisibility(View.INVISIBLE);
    	}

		mJobDetailsListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view, int position,long arg3) {

				HashMap<String, String> mSelectedPositionMap = (HashMap<String, String>) adapterview.getItemAtPosition(position);
				if(!mSelectedPositionMap.containsKey("Header")){
					if(mSelectedPositionMap.get("status").equalsIgnoreCase("new_job_entries")){
						mRowId = Integer.valueOf(mSelectedPositionMap.get("id"));
						mSelectedPosition = position;
						Intent mDetailEntryIntent;
						String type = mSelectedPositionMap.get("type");
						if(type.equalsIgnoreCase("Note")){
							mDetailEntryIntent = new Intent(JobDetailsiTrac.this,NoteiTrac.class);
							mDetailEntryIntent.putExtra(getResources().getString(R.string.note), mSelectedPositionMap.get("value"));
							startActivityForResult(mDetailEntryIntent, ENTER_NOTES_REQUEST_CODE);
						}else if(type.equalsIgnoreCase("Material")){
							mDetailEntryIntent = new Intent(JobDetailsiTrac.this,MaterialiTrac.class);
							mDetailEntryIntent.putExtra(getResources().getString(R.string.material), mSelectedPositionMap.get("material_part_number"));
							mDetailEntryIntent.putExtra(getResources().getString(R.string.quantity), mSelectedPositionMap.get("material_quantity"));
							startActivityForResult(mDetailEntryIntent, ENTER_MATERIALS_REQUEST_CODE);
						}else if(type.equalsIgnoreCase("ETC")){
							mDetailEntryIntent = new Intent(JobDetailsiTrac.this,ETCiTrac.class);
							mDetailEntryIntent.putExtra(getResources().getString(R.string.etc), mSelectedPositionMap.get("value"));
							startActivityForResult(mDetailEntryIntent, ENTER_ETC_REQUEST_CODE);
						}else if(type.equalsIgnoreCase("QTY")){
							mDetailEntryIntent = new Intent(JobDetailsiTrac.this,QtyCompletediTrac.class);
							mDetailEntryIntent.putExtra(getResources().getString(R.string.qty_completed), mSelectedPositionMap.get("value"));
							startActivityForResult(mDetailEntryIntent, ENTER_QTY_REQUEST_CODE);
						}
					}else{
						new AlertNotification().showAlertPopup(JobDetailsiTrac.this, "Information", "You cannot edit this item", null);
					}
				}
			}
		});

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			long inserted_row;
			switch (requestCode) {
			case ENTER_NOTES_REQUEST_CODE:
				if(mRowId == -1){ // add
					mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
					inserted_row = mDatabaseHelper.InsertJobEntryDetails("Note", extras.getString(getResources().getString(R.string.note)),"",""); 
					if(inserted_row != -1){
						refreshJobEntryList(String.valueOf(inserted_row),"Note", extras.getString(getResources().getString(R.string.note)), "", "");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to save data please try after sometime", null);
					}	 
				}else{  // update
					if(mDatabaseHelper.updateJobEntryField(mRowId, extras.getString(getResources().getString(R.string.note)), "", "") > 0){
						updateJobEntryDetailList(mSelectedPosition, mRowId, "Note",  extras.getString(getResources().getString(R.string.note)), "", "");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to update data please try after sometime", null);
					}
					mRowId = -1;
				}

				break;
			case ENTER_MATERIALS_REQUEST_CODE:
				if(mRowId == -1){ // add
					mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
					inserted_row = mDatabaseHelper.InsertJobEntryDetails("Material", extras.getString(getResources().getString(R.string.quantity)) + ":"+extras.getString(getResources().getString(R.string.material)),extras.getString(getResources().getString(R.string.material)),extras.getString(getResources().getString(R.string.quantity)));
					if(inserted_row != -1){
						refreshJobEntryList(String.valueOf(inserted_row),"Material", extras.getString(getResources().getString(R.string.quantity)) + ":"+extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.quantity)));
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to save data please try after sometime", null);
					}
				}else{ // update
					if(mDatabaseHelper.updateJobEntryField(mRowId, extras.getString(getResources().getString(R.string.quantity)) + ":"+extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.quantity))) > 0){
						updateJobEntryDetailList(mSelectedPosition, mRowId, "Material",  extras.getString(getResources().getString(R.string.quantity)) + ":"+extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.material)), extras.getString(getResources().getString(R.string.quantity)));
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to update data please try after sometime", null);
					}
					mRowId = -1;
				}

				break;
			case ENTER_ETC_REQUEST_CODE:
				if(mRowId == -1){ // add
					mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
					inserted_row = mDatabaseHelper.InsertJobEntryDetails("ETC", extras.getString(getResources().getString(R.string.time_left)),"","");
					if(inserted_row != -1){
						refreshJobEntryList(String.valueOf(inserted_row),"ETC", extras.getString(getResources().getString(R.string.time_left)),"","");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to save data please try after sometime", null);
					}
				}else{ // update
					if(mDatabaseHelper.updateJobEntryField(mRowId, extras.getString(getResources().getString(R.string.time_left)), "", "") > 0){
						updateJobEntryDetailList(mSelectedPosition, mRowId, "ETC",  extras.getString(getResources().getString(R.string.time_left)), "", "");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to update data please try after sometime", null);
					}
					mRowId = -1;
				}

				break;
			case ENTER_QTY_REQUEST_CODE:
				if(mRowId == -1){ // add
					mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
					inserted_row = mDatabaseHelper.InsertJobEntryDetails("QTY", extras.getString(getResources().getString(R.string.qty_completed)),"","");
					if(inserted_row != -1){
						refreshJobEntryList(String.valueOf(inserted_row),"QTY", extras.getString(getResources().getString(R.string.qty_completed)),"","");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to save data please try after sometime", null);
					}
				}else{ // Update
					if(mDatabaseHelper.updateJobEntryField(mRowId, extras.getString(getResources().getString(R.string.qty_completed)), "", "") > 0){
						updateJobEntryDetailList(mSelectedPosition, mRowId, "QTY",  extras.getString(getResources().getString(R.string.qty_completed)), "", "");
					}else{
						new AlertNotification().showAlertPopup(this, "Error", "Unable to update data please try after sometime", null);
					}
					mRowId = -1;
				}

				break;
			default:
				break;
			}
		}
	}



	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.leftmenuID:
			mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
			if(mJobEntryDetailsList.size() == 0){
				finish();
			}else{
				showOptionsAlert("Information", "Discard your unsaved details?");
			}
			break;

		case R.id.rightmenuID:
			mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
			Intent mPendingJobDetails = new Intent();
			mPendingJobDetails.putExtra("pending_job_details", mJobEntryDetailsList);
			setResult(RESULT_OK,mPendingJobDetails);
			finish();
			break;
		case R.id.job_details_add_notesID:
			startActivityForResult(new Intent(this,NoteiTrac.class), ENTER_NOTES_REQUEST_CODE);
			break;
		case R.id.job_details_add_materialID:
			startActivityForResult(new Intent(this,MaterialiTrac.class), ENTER_MATERIALS_REQUEST_CODE);

			break;
		case R.id.job_details_add_etcID:
			startActivityForResult(new Intent(this,ETCiTrac.class), ENTER_ETC_REQUEST_CODE);
			break;
		case R.id.job_details_add_qtyID:
			startActivityForResult(new Intent(this,QtyCompletediTrac.class), ENTER_QTY_REQUEST_CODE);
			break;

		default:
			break;
		}
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
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		Log.v("On UserLeave Hint","Called");
		new ScheduleBackgroundTask(this).cancelAlarm();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		mJobEntryDetailsList.removeAll(mPreviousJobEntryDetailsList);
		if(mJobEntryDetailsList.size() == 0){
			finish();
		}else{
			showOptionsAlert("Information", "Discard your unsaved details?");
		}
	}

	private void refreshJobEntryList(String id,String type,String value,String material_part_number,String material_quantity){
		if(!mIsListHeaderAdded){
			HashMap<String,String> headerMap = new HashMap<String, String>();
			headerMap.put("Header", "Pending Details");
			mJobEntryDetailsList.add(headerMap);
			mIsListHeaderAdded = true;
		}
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("id", id);
		mMap.put("type", type);
		mMap.put("value", value);
		mMap.put("material_part_number", material_part_number);
		mMap.put("material_quantity", material_quantity);
		mMap.put("status", "new_job_entries");
		mJobEntryDetailsList.add(mMap);
		for(int i=0;i<mPreviousJobEntryDetailsList.size();i++){
			mJobEntryDetailsList.add(mPreviousJobEntryDetailsList.get(i));
		}
		mJobEntryAdapter = new JobEntryDetailsAdapter(this, mJobEntryDetailsList);
		mJobDetailsListview.setAdapter(mJobEntryAdapter);
	}


	private void updateJobEntryDetailList(int position,int id,String type,String value,String material_part_number,String material_quantity){
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("id", String.valueOf(id));
		mMap.put("type", type);
		mMap.put("value", value);
		mMap.put("material_part_number", material_part_number);
		mMap.put("material_quantity", material_quantity);
		mMap.put("status", "new_job_entries");
		mJobEntryDetailsList.set(position, mMap);
		mJobEntryAdapter.notifyDataSetChanged();
	}

	// Alert pop up with options for confirmation
	private void showOptionsAlert(String title,final String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(JobDetailsiTrac.this);
		builder.setTitle("Information");
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
				for(HashMap<String, String> map : mJobEntryDetailsList){
					String id = map.get("id");
					mDatabaseHelper.deleteJobEntryRow(id);
				}
				finish();
			}
		});
		builder.show();
	}
}
