package com.trac.android.tractivity.backgroundtask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.database.RegisteredTable.TransactionTable;
import com.trac.android.tractivity.database.RegisteredTable;
import com.trac.android.tractivity.database.TransactionInfo;
import com.trac.android.tractivity.database.Webservice;
import com.trac.android.tractivity.logs.LogiTrac.EntryType;
import com.trac.android.tractivity.utils.NetworkCheck;

/**
 * Intent service which runs in background to sync data to web server
 *
 */
public class TractivitySyncService  extends IntentService{

	private DatabaseHelper dbHelper;
	private  Queue<String> syncQueue = new LinkedList<String>();
	public String TimeClmn, EventClmn, ParamClmn, GPSTIME;
	
	public TractivitySyncService() {
		super("trac");
		// TODO Auto-generated constructor stub
		dbHelper = DatabaseHelper.getInstance(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.v("Background service", "Intent service triggered");
		try {
			PrepareSyncToWebService();
            if(intent.hasExtra("stopself"))
            	stopSelf();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    // To prepare xml request for sync with server
	private void PrepareSyncToWebService() throws IOException, XmlPullParserException {
        // To get details from transaction which status includes  ==> Pending,Sending,Failed in asc order
		ArrayList<TransactionInfo> transactionList = dbHelper.getDatabaseTransactionInfo("pending_data");
		syncQueue.clear();
		SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
        if(transactionList != null && transactionList.size() > 0){
        	for(TransactionInfo tInfo : transactionList) {
    			if(tInfo.sendStatus.equals(EntryType.Pending.entryName) || tInfo.sendStatus.equals(EntryType.Failed.entryName) || tInfo.sendStatus.equals(EntryType.Sending.entryName)) {
    				TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    				String device_id = TelephonyMgr.getDeviceId();
    				if(device_id == null) {
    					device_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    				}
    				boolean enable_gps = customSharedPreference.getBoolean(getResources().getString(R.string.enablegps), true);
    				if(enable_gps){
    					//GPS Latitude
        				EventClmn = "..G1..";
        				ParamClmn = String.valueOf(tInfo.GPS_LAT);
        				syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ EventClmn + "�" + ParamClmn);
        				//GPS Longitude
        				EventClmn = "..G2..";
        				ParamClmn = String.valueOf(tInfo.GPS_LON);
        				syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ EventClmn + "�" + ParamClmn);	
    				}
                   	
    				String eventXMLName = null;
    				String eventValue = null;
    				if(tInfo.uniqueValue.equalsIgnoreCase("Start Day")) {
    					eventXMLName = "..SD..";
    					eventValue = tInfo.uniqueValue;
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("End Day")) {
    					eventXMLName = "..ED..";
    					eventValue = tInfo.uniqueValue;
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("All Lunch")) {
    					eventXMLName = "..AL..";
    					eventValue = tInfo.uniqueValue;
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("All Return")) {
    					eventXMLName = "..AR..";
    					eventValue = tInfo.uniqueValue;
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("Note")) {
    					eventXMLName = "..N..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("ETC")) {
    					eventXMLName = "..ETC..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("Material")) {
    					eventXMLName = "..MPN..";
    					String detail_value = tInfo.detailValue;
    					String[] values = detail_value.split(":");
    					String material_part_number = values[1];
    					String material_quantity = values[0];
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�" + "..MPN.." + "�" + material_part_number);
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�" + "..MQTY.." + "�" + material_quantity);
    					eventXMLName = null;
    					eventValue = null;
    				}  else if(tInfo.uniqueValue.equalsIgnoreCase("MPN")) {
    					eventXMLName = "..MPN..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("MQTY")) {
    					eventXMLName = "..MQTY..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("PN")) {
    					eventXMLName = "..PN..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("QTY")) {
    					eventXMLName = "..QTY..";
    					eventValue = tInfo.detailValue;
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				} else if(tInfo.uniqueValue.equalsIgnoreCase("Job")) {
    					//Special Case... Add multiple values to syncQueue at once
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..E.." + "�" + getIDFromString(tInfo.empCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..J.." + "�" + getIDFromString(tInfo.jobCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..P.." + "�" + getIDFromString(tInfo.itemCode));
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�"+ "..A.." + "�" + getIDFromString(tInfo.actCode));
    				}
    				if(eventXMLName != null && eventValue != null && !tInfo.uniqueValue.equalsIgnoreCase("Material")) { // For material added previously to Queue 
    					eventValue = eventValue.replaceAll("<", "&lt;");
    					eventValue = eventValue.replaceAll(">", "&gt;");
    					eventValue = eventValue.replaceAll("&", "&amp;");
    					syncQueue.add(device_id + "�" + tInfo.transactionTimestamp + "�" + eventXMLName + "�" + eventValue);
    					eventXMLName = null;
    					eventValue = null;
    				}
    				
    			}
    			// Update status to sending
        		dbHelper.updateTableField(TransactionTable.TABLE_NAME, "sendStatus", EntryType.Sending.entryName, RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " = " + tInfo.rowNumber);	
    		}
        	// Update status to sending
    		//dbHelper.updateTableField(TransactionTable.TABLE_NAME, "sendStatus", EntryType.Sending.entryName, String.format("sendStatus = '%s' OR sendStatus = '%s'", EntryType.Pending.entryName, EntryType.Failed.entryName));
    		ProcessXMLtoSyncWebService();
        }else{
        	Log.v("Sync Entry", "Nothing to sync");
        }
	}

    // To get ID
	private String getIDFromString(String var1) {
		if(var1.equalsIgnoreCase("0")){
			return var1;
		}else if(var1.contains("(") && var1.contains(")") && var1.length()>2) {
			int pre = var1.indexOf("(");
			int post = var1.indexOf(")");
			String id = "-1";
			String code = var1.substring(pre + 1, post);
			if(code.length()>0){			
				id = code;				
			}else{
				return " ";
			}
			if(!id.equalsIgnoreCase("-1")) {
				return id;
			} else {
				return " ";
			}
		} else {
			return " ";
		}
	}

	// Function to form queue values as XML structure to sync data in web service
	private void ProcessXMLtoSyncWebService() {
		try{
			// Use start tag flag setting for add start tag with XML format(String) at every iteration
			String line = null, DeviceId = null, TimeId = null, Eventcode = null, Paramcode = null, send = null;
			StringBuilder builder = new StringBuilder();
			Iterator<String> mIterator = syncQueue.iterator();
			line = "<NewDataSet>\n  ";
			builder.append(line);
			while(mIterator.hasNext()) {
				String iteratorvalue = (String) mIterator.next();
				//System.out.println("Built: " + iteratorvalue);
				String delims = "[�]";
				String[] tokens = iteratorvalue.split(delims);
				// Get the String from String[] to design Elements 
				DeviceId = tokens[0].toString();
				TimeId = tokens[1].toString();
				Eventcode = tokens[2].toString();
				Paramcode = tokens[3].toString();

				line = "<iTracEvents>\n" + "<DeviceID>" + DeviceId  + "</DeviceID>\n    <EventTime>" + TimeId +"</EventTime>\n    <EventCode>" + Eventcode + "</EventCode>\n    <EventParam>"+ Paramcode +"</EventParam>\n" + "</iTracEvents>\n";	
				builder.append(line);
			}
			// Add EndTag </NewDataset> in XML Format(line) string, and Add to Elements List (ArrayList)
			line = "</NewDataSet>\n";
			builder.append(line);
			send = builder.toString();

			if(syncQueue.size() != 0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm:ss aa", Locale.US);
				Calendar mCurrentCalendar = Calendar.getInstance();
				String sync_starttime = dateFormat.format(mCurrentCalendar.getTime());
				String syncStatus;
				if(new NetworkCheck().ConnectivityCheck(this)){
					syncStatus = new Webservice(this).receiveEvents(send);	
				}else{
					syncStatus = "Pending";
				}
				if(syncStatus != null) {
					// To save sync status in preference file
					SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Context.MODE_PRIVATE);
					final SharedPreferences.Editor editor = customSharedPreference.edit();
					String previous_sync_status = customSharedPreference.getString("sync_status", "");
					editor.putString("sync_status",syncStatus);
					editor.putString("last_sync_time",sync_starttime);
					if(!syncStatus.equalsIgnoreCase("Pending"))
					editor.putString("last_sync_success_time",sync_starttime);
					editor.commit();
					if(syncStatus.equalsIgnoreCase("Pending")) {
						dbHelper.updateTableField(TransactionTable.TABLE_NAME, "sendStatus", EntryType.Failed.entryName, String.format("sendStatus = '%s'", EntryType.Sending.entryName));
					} else {
						//Sent Successfully
						dbHelper.updateTableField( TransactionTable.TABLE_NAME, "sendStatus", EntryType.Sent.entryName, String.format("sendStatus = '%s'", EntryType.Sending.entryName));
						dbHelper.deleteRows(TransactionTable.TABLE_NAME, "sendStatus = ? AND (julianday('now') - julianday(transactionTimestamp) > 7)", EntryType.Sent.entryName);
					}
					// To broadcast results to activity
					Intent mSendBroadcastIntent = new Intent("SYNC_STATUS_ACTION");
					if(syncStatus.equalsIgnoreCase("Pending")){
						if(previous_sync_status.equalsIgnoreCase("success")){
							mSendBroadcastIntent.putExtra("sync_status", syncStatus);
						}
					}else{
						mSendBroadcastIntent.putExtra("sync_status", syncStatus);	
					}
					sendBroadcast(mSendBroadcastIntent);
				} 
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
