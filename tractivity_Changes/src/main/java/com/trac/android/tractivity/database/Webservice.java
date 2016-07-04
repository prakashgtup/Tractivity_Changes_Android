package com.trac.android.tractivity.database;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.trac.android.tractivity.V1.R;

public class Webservice {
	private SoapSerializationEnvelope mSoapSerialization;
	private HttpTransportSE mHttpTransportSE;
	private SoapObject mSoapObject, mGetResult, mGetResultDiffGram, mGetResultNewDataset, getJobsResponse;
	private String mData;
	private Calendar cal2;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm:ss aa", Locale.US);
	private  int Timeout = 10000;
	private String mNetworkAddress;
	private Context mContext;
	private SharedPreferences customSharedPreference;
	private static final String LOGIN = "Login";
	private static final String NAMESPACE = "http://tractivity.com/services/iTrac/"; 
	private static final String GET_JOBS = "GetJobsActs";
	private static final String XML_RECEIVE_EVENTS = "XmlReceiveEvents";
	
	public Webservice(Context  context) {
		// TODO Auto-generated constructor stub
		customSharedPreference = context.getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		mNetworkAddress = customSharedPreference.getString("network_address", "");
		mContext = context;
	}

	// Load Web Services and Read Soap Response for Login Details
	public String ConfigDetails(String mChkHostIP, String mChkPortNo,String mChkLocalpath) {
		try {
			String namespace = "http://" + mChkHostIP.toString().trim(); 
			mSoapSerialization = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			mSoapSerialization.dotNet = true;
			mHttpTransportSE = new HttpTransportSE(namespace+ ":" +mChkPortNo.toString().trim() +mChkLocalpath.toString().trim(),Timeout);
			mHttpTransportSE.debug = true;
			try {
				mHttpTransportSE.call(namespace, mSoapSerialization);
				if(mHttpTransportSE != null){
					mData = "Success";
				} else {
				}
			} catch (IOException e) {
				e.printStackTrace();
				mData = "failure";
			}
		} catch(Exception e) {
			mData = "failure";
		}
		return mData;
	}
	
	// Load Web Services and Read Soap Response for Login Details
	public String LoginDetails(String mCheckUser, String mCheckpswd){
		mSoapObject = new SoapObject(NAMESPACE,LOGIN);
		mSoapObject.addProperty("loginName", mCheckUser);
		mSoapObject.addProperty("password", mCheckpswd);
		mSoapSerialization = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		mSoapSerialization.dotNet=true;
		mSoapSerialization.setOutputSoapObject(mSoapObject);
		mHttpTransportSE = new HttpTransportSE(mNetworkAddress, 5000);
		mHttpTransportSE.debug=true;
		try {
			mHttpTransportSE.call(NAMESPACE+LOGIN, mSoapSerialization);
			if(mHttpTransportSE!=null){
				mGetResult = (SoapObject) mSoapSerialization.getResponse();
				System.out.println("krishnan"+mGetResult.toString());
				String empId = String.valueOf(mGetResult.getProperty("empId")).toString();
				String loginname = String.valueOf(mGetResult.getProperty("loginName")).toString();
				String empname = String.valueOf(mGetResult.getProperty("empName")).toString();
				String workgroup = String.valueOf(mGetResult.getProperty("workGroup")).toString();
				String username = String.valueOf(mGetResult.getProperty("userName")).toString();
				String usertype = String.valueOf(mGetResult.getProperty("userType")).toString();
				String supervisor = String.valueOf(mGetResult.getProperty("supervisor")).toString();
				String manager = String.valueOf(mGetResult.getProperty("manager")).toString();
				String jobrev = String.valueOf(mGetResult.getProperty("iTracShowJobRev")).toString();
				String emplist = String.valueOf(mGetResult.getProperty("iTracShowEmpList")).toString();
				String superlist = String.valueOf(mGetResult.getProperty("iTracShowSuperList")).toString();
				String enablegps = String.valueOf(mGetResult.getProperty("iTracEnableGPS")).toString();
				String mpn = String.valueOf(mGetResult.getProperty("ShowMPN")).toString();
				String mqty = String.valueOf(mGetResult.getProperty("ShowMQTY")).toString();
				String etc = String.valueOf(mGetResult.getProperty("ShowETC")).toString();
				String qty = String.valueOf(mGetResult.getProperty("ShowQTY")).toString();
				String pn = String.valueOf(mGetResult.getProperty("ShowPN")).toString();
				String iTracW2S=String.valueOf(mGetResult.getProperty("iTracW2S")).toString();
				//String note = String.valueOf(mGetResult.getProperty("ShowNote")).toString();
				if (Integer.parseInt(empId) > 0){
					Editor mPreferenceEditor = customSharedPreference.edit();
					mPreferenceEditor.putInt(mContext.getString(R.string.empId), Integer.parseInt(empId));
					mPreferenceEditor.putString(mContext.getString(R.string.login_name), loginname);
					mPreferenceEditor.putString(mContext.getString(R.string.emp_name), empname);
					mPreferenceEditor.putString(mContext.getString(R.string.workgroup), workgroup);
					mPreferenceEditor.putString(mContext.getString(R.string.user_name), username);
					mPreferenceEditor.putString(mContext.getString(R.string.user_type), usertype);
					mPreferenceEditor.putString(mContext.getString(R.string.supervisor), supervisor);
					mPreferenceEditor.putString(mContext.getString(R.string.manager), manager);
					mPreferenceEditor.putString(mContext.getString(R.string.manager), manager);
					mPreferenceEditor.putBoolean(mContext.getString(R.string.jobrev), Boolean.parseBoolean(jobrev));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.emplist), Boolean.parseBoolean(emplist));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.superlist), Boolean.parseBoolean(superlist));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.enablegps), Boolean.parseBoolean(enablegps));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.mpn), Boolean.parseBoolean(mpn));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.mqty), Boolean.parseBoolean(mqty));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.etc), Boolean.parseBoolean(etc));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.qty), Boolean.parseBoolean(qty));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.pn), Boolean.parseBoolean(pn));
					mPreferenceEditor.putBoolean(mContext.getString(R.string.w2s), Boolean.parseBoolean(iTracW2S));
	                mPreferenceEditor.commit();				
					mData = "Success";
				} else {
					mData = "Not valid user";
				}
			}
		} catch (IOException e) {
			mData = "TimedOut";
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			mData = "URL Exception";
			e.printStackTrace();
		}
		return mData;
	}

	//Method to call get jobs
	public String getjobs(int empid,DatabaseHelper databasehelper) throws IOException, XmlPullParserException {
		mSoapObject = new SoapObject(NAMESPACE,GET_JOBS);
		mSoapObject.addProperty("empId", empid);
		mSoapSerialization = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		mSoapSerialization.dotNet = true;
		mSoapSerialization.setOutputSoapObject(mSoapObject);
		mHttpTransportSE = new HttpTransportSE(mNetworkAddress,Timeout);
		mHttpTransportSE.debug = true;
		cal2 = Calendar.getInstance();
		try {
			System.setProperty("http.keepAlive", "false");
			mHttpTransportSE.call(NAMESPACE+GET_JOBS, mSoapSerialization);
			mGetResult = (SoapObject) mSoapSerialization.getResponse();
			System.out.println(mGetResult.toString());
			mGetResultDiffGram = (SoapObject) mGetResult.getProperty("diffgram");
			System.out.println(mGetResultDiffGram.toString());
			mGetResultNewDataset = (SoapObject)mGetResultDiffGram.getProperty("NewDataSet");
			System.out.println(mGetResultNewDataset.toString());
			if(mGetResultNewDataset != null && mGetResultNewDataset.getPropertyCount() > 0){
				databasehelper.deleteRows( RegisteredTable.EmployeeTable.TABLE_NAME, null, null);
				databasehelper.deleteRows( RegisteredTable.ActivityTable.TABLE_NAME,null, null);
				databasehelper.deleteRows( RegisteredTable.BudgetedActivityTable.TABLE_NAME,null, null);
				databasehelper.deleteRows( RegisteredTable.JobTable.TABLE_NAME,null, null);
				databasehelper.deleteRows( RegisteredTable.MaterialTable.TABLE_NAME,null, null);
				databasehelper.deleteRows( RegisteredTable.PhaseTable.TABLE_NAME,null, null);
				SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS", Locale.US);
				for (int i = 0; i < mGetResultNewDataset.getPropertyCount(); i++) {
					getJobsResponse = (SoapObject) mGetResultNewDataset.getProperty(i);	
					System.out.println("getJobsResponse------------>"+getJobsResponse.toString());
					//Employee
					if(getJobsResponse.hasProperty("EmpID") && getJobsResponse.hasProperty("EmpName")) {
						String id = getJobsResponse.getPropertyAsString("EmpID");
						String name = getJobsResponse.getPropertyAsString("EmpName");
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_ID, id);
						values.put(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_NAME, name);
						values.put(RegisteredTable.EmployeeTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.EmployeeTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable(String.format("NULL, '%s', '%s', '%s'", id, name, mDateFormat.format(new Date())), RegisteredTable.EmployeeTable.TABLE_NAME);
					}
					//Job
					if(getJobsResponse.hasProperty("job") && getJobsResponse.hasProperty("dsc") && !getJobsResponse.hasProperty("jsfx")) {
						String jobCode = getJobsResponse.getPropertyAsString("job").replace("anyType{}", "");
						String jobDescription = getJobsResponse.getPropertyAsString("dsc").replace("anyType{}", "");
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.JobTable.COLUMN_JOB_IDENTITY, jobCode);
						values.put(RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION, jobDescription);
						values.put(RegisteredTable.JobTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.JobTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable( String.format("NULL, '%s', '%s', '%s'", jobCode, jobDescription, mDateFormat.format(new Date())), RegisteredTable.JobTable.TABLE_NAME);
					}
					//Phase
					if(getJobsResponse.hasProperty("job") && getJobsResponse.hasProperty("jsfx")) {					
						String jobCode = getJobsResponse.getPropertyAsString("job").replace("anyType{}", "");
						String phaseCode = getJobsResponse.getPropertyAsString("jsfx").replace("anyType{}", "");			
						String jobDescription="";
						if(getJobsResponse.hasProperty("dsc")) {
							jobDescription = getJobsResponse.getPropertyAsString("dsc").replace("anyType{}", "");
						}
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.PhaseTable.COLUMN_JOB_IDENTITY, jobCode);
						values.put(RegisteredTable.PhaseTable.COLUMN_PHASE_IDENTITY, phaseCode);
						values.put(RegisteredTable.PhaseTable.COLUMN_PHASE_DESCRIPTION, jobDescription);
						values.put(RegisteredTable.PhaseTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.PhaseTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable( String.format("NULL, '%s', '%s', '%s', '%s'", jobCode, phaseCode, jobDescription, mDateFormat.format(new Date())), RegisteredTable.PhaseTable.TABLE_NAME);
					}
					//Activity
					if(getJobsResponse.hasProperty("act") && getJobsResponse.hasProperty("dsc") && !getJobsResponse.hasProperty("job") && !getJobsResponse.hasProperty("item")){
						String actCode = getJobsResponse.getPropertyAsString("act").replace("anyType{}", "");
						String actDescription = getJobsResponse.getPropertyAsString("dsc").replace("anyType{}", "");
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY, actCode);
						values.put(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION, actDescription);
						values.put(RegisteredTable.ActivityTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.ActivityTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable( String.format("NULL, '%s', '%s', '%s'", actCode, actDescription, mDateFormat.format(new Date())), RegisteredTable.ActivityTable.TABLE_NAME);
					}
					//Budgeted Activity
					if(getJobsResponse.hasProperty("job") && getJobsResponse.hasProperty("act") && getJobsResponse.hasProperty("item")) {
						//System.out.println("Budgeted Activity----->"+getJobsResponse);
						String jobCode = getJobsResponse.getPropertyAsString("job").replace("anyType{}", "");
						String phaseCode = getJobsResponse.getPropertyAsString("item").replace("anyType{}", "");
						String actCode = getJobsResponse.getPropertyAsString("act").replace("anyType{}", "");
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.BudgetedActivityTable.COLUMN_JOB_IDENTITY, jobCode);
						values.put(RegisteredTable.BudgetedActivityTable.COLUMN_PHASE_IDENTITY, phaseCode);
						values.put(RegisteredTable.BudgetedActivityTable.COLUMN_ACTIVITY_IDENTITY, actCode);
						values.put(RegisteredTable.BudgetedActivityTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.BudgetedActivityTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable(String.format("NULL, '%s', '%s', '%s', '%s'", jobCode, phaseCode, actCode, mDateFormat.format(new Date())), RegisteredTable.BudgetedActivityTable.TABLE_NAME);
					}
					//Material
					if(getJobsResponse.hasProperty("pn") && getJobsResponse.hasProperty("dsc")){
						String matCode = getJobsResponse.getPropertyAsString("pn").replace("anyType{}", "");
						String matDescription = getJobsResponse.getPropertyAsString("dsc").replace("anyType{}", "");
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY, matCode);
						values.put(RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION, matDescription);						
						values.put(RegisteredTable.MaterialTable.COLUMN_MODIFIED_DATE, mDateFormat.format(new Date()));
						databasehelper.insertvaluesIntoTable(values, RegisteredTable.MaterialTable.TABLE_NAME);
						//databasehelper.insertDataIntoTable(String.format("NULL, '%s', '%s', '%s'", matCode, matDescription,mDateFormat.format(new Date())), RegisteredTable.MaterialTable.TABLE_NAME);
					}
				}
				mData = "Success";
				//TPOJO.SyncStatus = "Success";
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			//TPOJO.SyncStatus = "Failed";
			mData = "Host Unreachable";
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			//TPOJO.SyncStatus = "Failed";
			mData = "Host Unreachable";
			e.printStackTrace();
		}
		return mData;
	}

	public String receiveEvents(String sobj) {
		int s = 0;
		String status;
		mSoapObject = new SoapObject(NAMESPACE,XML_RECEIVE_EVENTS);
		mSoapSerialization = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		mSoapObject.addProperty("sXml", sobj);
		System.out.println("REQUEST : "+sobj);								
		mSoapSerialization.dotNet=true;
		mSoapSerialization.setOutputSoapObject(mSoapObject);
		mHttpTransportSE = new HttpTransportSE(mNetworkAddress,Timeout);
		mHttpTransportSE.debug=true;
		  try {
		        mHttpTransportSE.call(NAMESPACE+XML_RECEIVE_EVENTS, mSoapSerialization);
		        SoapPrimitive result = (SoapPrimitive) mSoapSerialization.getResponse();
		        if(result != null)
		        	Log.i("soap primitive","not null");	
		        else
		        	Log.i("soap primitive","null");
		        s = Integer.parseInt(result.toString());
		        System.out.println("RESPONSE : "+s);
				if(s == 0) {
					status = "Pending";
				} else {
					status = "Success";
				}
		    } catch (Exception e) {
		    	status = "Pending";
		        e.printStackTrace();
		    }
		  System.out.println("RESPONSE : "+status);
		return status;
	}
}
