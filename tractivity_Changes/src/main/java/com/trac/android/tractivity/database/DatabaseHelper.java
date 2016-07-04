package com.trac.android.tractivity.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.trac.android.tractivity.configuration.NetworkMemoryDetails;
import com.trac.android.tractivity.database.RegisteredTable.ActivityTable;
import com.trac.android.tractivity.database.RegisteredTable.EmployeeTable;
import com.trac.android.tractivity.database.RegisteredTable.TransactionTable;
import com.trac.android.tractivity.logs.LogiTrac.EntryType;
import com.trac.android.tractivity.utils.TimeInterval;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "iTrac.db";

	// Singleton db instance	
	private static DatabaseHelper mDBInstance = null;

	public static DatabaseHelper getInstance(Context ctx) {

		if (mDBInstance == null) {
			mDBInstance = new DatabaseHelper(ctx,DATABASE_NAME, null, DATABASE_VERSION);
		}
		return mDBInstance;
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create required tables 
		createTable(db, RegisteredTable.NetworkMemoryTable.TABLE_NAME, RegisteredTable.NetworkMemoryTable.COLUMNS);
		createTable(db, RegisteredTable.EmployeeTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.EmployeeTable.columns));
		createTable(db, RegisteredTable.ActivityTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.ActivityTable.columns));
		createTable(db, RegisteredTable.BudgetedActivityTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.BudgetedActivityTable.columns));
		createTable(db, RegisteredTable.JobTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.JobTable.columns));
		createTable(db, RegisteredTable.MaterialTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.MaterialTable.columns));
		createTable(db, RegisteredTable.PhaseTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.PhaseTable.columns));
		createTable(db, RegisteredTable.TransactionTable.TABLE_NAME, RegisteredTable.getColumnsStringFromArray(RegisteredTable.TransactionTable.columns));
		createTable(db, RegisteredTable.JobEntryDetailsTable.TABLE_NAME, RegisteredTable.JobEntryDetailsTable.COLUMNS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Initially drop all the tables and then recreate it during upgradation
		dropTable( db,RegisteredTable.NetworkMemoryTable.TABLE_NAME);
		dropTable( db,RegisteredTable.EmployeeTable.TABLE_NAME);
		dropTable(db, RegisteredTable.ActivityTable.TABLE_NAME);
		dropTable( db,RegisteredTable.BudgetedActivityTable.TABLE_NAME);
		dropTable( db,RegisteredTable.JobTable.TABLE_NAME);
		dropTable( db,RegisteredTable.MaterialTable.TABLE_NAME);
		dropTable( db,RegisteredTable.PhaseTable.TABLE_NAME);
		dropTable( db,RegisteredTable.TransactionTable.TABLE_NAME);
		dropTable( db,RegisteredTable.JobEntryDetailsTable.TABLE_NAME);
		onCreate(db);
	}


	/**
	 * Creates the given table with the given database.
	 * @param db Use dbHelper.getWritableDatabase() to get db
	 * @param tableName Desired table name
	 * @param columns String of columns, including the commas in between them. EX: "id INTEGER PRIMARY KEY, empName TEXT"
	 */
	public void createTable(SQLiteDatabase db,String tableName, String columns) {
		String query = "CREATE TABLE " + tableName + " (" + columns + ");";
		db.execSQL(query);
	}

	public void dropTable(SQLiteDatabase db,String tableName) {
		String query = "DROP TABLE IF EXISTS " + tableName + ";";
		db.execSQL(query);
	}

	/*public void insertDataIntoTable(String data, String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "INSERT INTO " + tableName + " VALUES (" + data + ")";
		db.execSQL(query);		
	}*/
	
	public void insertvaluesIntoTable(HashMap<String, String> values,String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		//Employee
		if(tableName.equalsIgnoreCase(RegisteredTable.EmployeeTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_ID, values.get(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_ID));
			contentvalues.put(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_NAME, values.get(RegisteredTable.EmployeeTable.COLUMN_EMPLOYEE_NAME));
			contentvalues.put(RegisteredTable.EmployeeTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.EmployeeTable.COLUMN_MODIFIED_DATE));
		}
		//Job
		if(tableName.equalsIgnoreCase(RegisteredTable.JobTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.JobTable.COLUMN_JOB_IDENTITY, values.get(RegisteredTable.JobTable.COLUMN_JOB_IDENTITY));
			contentvalues.put(RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION, values.get(RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION));
			contentvalues.put(RegisteredTable.JobTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.JobTable.COLUMN_MODIFIED_DATE));
		}
		//Phase
		if(tableName.equalsIgnoreCase(RegisteredTable.PhaseTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.PhaseTable.COLUMN_JOB_IDENTITY, values.get(RegisteredTable.PhaseTable.COLUMN_JOB_IDENTITY));
			contentvalues.put(RegisteredTable.PhaseTable.COLUMN_PHASE_IDENTITY, values.get(RegisteredTable.PhaseTable.COLUMN_PHASE_IDENTITY));
			contentvalues.put(RegisteredTable.PhaseTable.COLUMN_PHASE_DESCRIPTION, values.get(RegisteredTable.PhaseTable.COLUMN_PHASE_DESCRIPTION));
			contentvalues.put(RegisteredTable.PhaseTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.PhaseTable.COLUMN_MODIFIED_DATE));
		}
		//Activity
		if(tableName.equalsIgnoreCase(RegisteredTable.ActivityTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY, values.get(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY));
			contentvalues.put(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION, values.get(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION));
			contentvalues.put(RegisteredTable.ActivityTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.ActivityTable.COLUMN_MODIFIED_DATE));
		}
		//Budgeted Activity
		if(tableName.equalsIgnoreCase(RegisteredTable.BudgetedActivityTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.BudgetedActivityTable.COLUMN_JOB_IDENTITY, values.get(RegisteredTable.BudgetedActivityTable.COLUMN_JOB_IDENTITY));
			contentvalues.put(RegisteredTable.BudgetedActivityTable.COLUMN_PHASE_IDENTITY, values.get(RegisteredTable.BudgetedActivityTable.COLUMN_PHASE_IDENTITY));
			contentvalues.put(RegisteredTable.BudgetedActivityTable.COLUMN_ACTIVITY_IDENTITY, values.get(RegisteredTable.BudgetedActivityTable.COLUMN_ACTIVITY_IDENTITY));
			contentvalues.put(RegisteredTable.BudgetedActivityTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.BudgetedActivityTable.COLUMN_MODIFIED_DATE));
		}
		//Material
		if(tableName.equalsIgnoreCase(RegisteredTable.MaterialTable.TABLE_NAME)){
			contentvalues.put(RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY, values.get(RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY));
			contentvalues.put(RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION, values.get(RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION));
			contentvalues.put(RegisteredTable.MaterialTable.COLUMN_MODIFIED_DATE, values.get(RegisteredTable.MaterialTable.COLUMN_MODIFIED_DATE));
		}		
		db.insert(tableName, null, contentvalues);
	}
	
	// To insert new Day entry other than JOB values..
	public void InsertUniqueEntryWithName(HashMap<String, String> values,String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(TransactionTable.COLUMN_EMPLOYEE_ID, values.get(TransactionTable.COLUMN_EMPLOYEE_ID));
		contentvalues.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, values.get(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP));
		contentvalues.put(TransactionTable.COLUMN_UNIQUE_VALUE, values.get(TransactionTable.COLUMN_UNIQUE_VALUE));
		contentvalues.put(TransactionTable.COLUMN_GPS_LAT, values.get(TransactionTable.COLUMN_GPS_LAT));
		contentvalues.put(TransactionTable.COLUMN_GPS_LON, values.get(TransactionTable.COLUMN_GPS_LON));
		contentvalues.put(TransactionTable.COLUMN_SEND_STATUS, values.get(TransactionTable.COLUMN_SEND_STATUS));
		db.insert(tableName, null, contentvalues);
	}
	
	// To insert new Day entry with JOB values..
	public void InsertNewJobEntryWithDetails(HashMap<String, String> values,String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(TransactionTable.COLUMN_EMPLOYEE_ID, values.get(TransactionTable.COLUMN_EMPLOYEE_ID));
		contentvalues.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, values.get(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP));
		contentvalues.put(TransactionTable.COLUMN_UNIQUE_VALUE, values.get(TransactionTable.COLUMN_UNIQUE_VALUE));		
		contentvalues.put(TransactionTable.COLUMN_EMPLOYEE_CODE, values.get(TransactionTable.COLUMN_EMPLOYEE_CODE));
		contentvalues.put(TransactionTable.COLUMN_JOB_CODE, values.get(TransactionTable.COLUMN_JOB_CODE));
		contentvalues.put(TransactionTable.COLUMN_PHASE_CODE, values.get(TransactionTable.COLUMN_PHASE_CODE));
		contentvalues.put(TransactionTable.COLUMN_ACTIVITY_CODE, values.get(TransactionTable.COLUMN_ACTIVITY_CODE));
		contentvalues.put(TransactionTable.COLUMN_GPS_LAT, values.get(TransactionTable.COLUMN_GPS_LAT));
		contentvalues.put(TransactionTable.COLUMN_GPS_LON, values.get(TransactionTable.COLUMN_GPS_LON));
		contentvalues.put(TransactionTable.COLUMN_SEND_STATUS, values.get(TransactionTable.COLUMN_SEND_STATUS));
		db.insert(tableName, null, contentvalues);
	}
	
	// To insert New Detail Entry With Details..
	public void InsertNewDetailEntryWithDetails(HashMap<String, String> values,String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(TransactionTable.COLUMN_EMPLOYEE_ID, values.get(TransactionTable.COLUMN_EMPLOYEE_ID));
		contentvalues.put(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, values.get(TransactionTable.COLUMN_TRANSACTION_TIMESTAMP));
		contentvalues.put(TransactionTable.COLUMN_UNIQUE_VALUE, values.get(TransactionTable.COLUMN_UNIQUE_VALUE));		
		contentvalues.put(TransactionTable.COLUMN_EMPLOYEE_CODE, values.get(TransactionTable.COLUMN_EMPLOYEE_CODE));
		contentvalues.put(TransactionTable.COLUMN_JOB_CODE, values.get(TransactionTable.COLUMN_JOB_CODE));
		contentvalues.put(TransactionTable.COLUMN_PHASE_CODE, values.get(TransactionTable.COLUMN_PHASE_CODE));
		contentvalues.put(TransactionTable.COLUMN_ACTIVITY_CODE, values.get(TransactionTable.COLUMN_ACTIVITY_CODE));
		contentvalues.put(TransactionTable.COLUMN_DETAIL_VALUE, values.get(TransactionTable.COLUMN_DETAIL_VALUE));
		contentvalues.put(TransactionTable.COLUMN_GPS_LAT, values.get(TransactionTable.COLUMN_GPS_LAT));
		contentvalues.put(TransactionTable.COLUMN_GPS_LON, values.get(TransactionTable.COLUMN_GPS_LON));
		contentvalues.put(TransactionTable.COLUMN_SEND_STATUS, values.get(TransactionTable.COLUMN_SEND_STATUS));
		db.insert(tableName, null, contentvalues);
	}

	// To insert network info to db
	public void InsertorUpdateNetworkListTable(String name,String host,String port,String local_path,String address){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_NAME, name);
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_HOST_IP, host);
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_PORT, port);
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_LOCAL_PATH, local_path);
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_ADDRESS, address);
		contentvalues.put(RegisteredTable.NetworkMemoryTable.COLUMN_MODIFIED_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS", Locale.US).format(new Date()));
		db.insertWithOnConflict(RegisteredTable.NetworkMemoryTable.TABLE_NAME, null, contentvalues, SQLiteDatabase.CONFLICT_REPLACE);
	}

	// To insert job details.
	public long InsertJobEntryDetails(String type,String value,String material_part_no,String material_quantity){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_JOB_ENTRY_TYPE, type);
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_VALUE,value);
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_PART_NUMBER,material_part_no);
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_QUANTITY, material_quantity);
		return db.insert(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, null, contentvalues);
	}

	// To get total entries from database
	public int getTotalEntries(){
		int total_count = 0;
		Cursor cursor;
		cursor = queryTable(RegisteredTable.NetworkMemoryTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.EmployeeTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.ActivityTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.BudgetedActivityTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.MaterialTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.PhaseTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.TransactionTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.NetworkMemoryTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		cursor = queryTable(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, null, null, null, null);
		total_count = total_count + cursor.getCount();
		cursor.close();
		return total_count;
	}
	
	/*
     * Read Job records related to the search term
     */
    public ArrayList<String> readJobFromDB(String searchTerm) {

    	ArrayList<String> recordsList = new ArrayList<String>();
        
        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        //Cursor cursor = db.rawQuery(sql, null);
        String[] columnsToReturn = {
        		RegisteredTable.JobTable.COLUMN_ENTRY_ID,
        		RegisteredTable.JobTable.COLUMN_JOB_IDENTITY,
        		RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION
		};
        String filterSearchTerm="";
        if(searchTerm.contains("(")){
        	filterSearchTerm=searchTerm.replace("(", " ");        	
        }else if(searchTerm.contains(")")){
        	filterSearchTerm=searchTerm.replace(")", " ");
        }/*else if(searchTerm.contains("-")){
        	filterSearchTerm=searchTerm.replace("-", " ");
        }*/else if(searchTerm.contains("#")){
        	filterSearchTerm=searchTerm.replace("#", " ");
        }else{
        	filterSearchTerm=searchTerm;
        }
        System.out.println("searchTerm trimmed------>"+filterSearchTerm.trim());
        Cursor cursor =db.query(true, RegisteredTable.JobTable.TABLE_NAME, columnsToReturn, RegisteredTable.JobTable.COLUMN_JOB_IDENTITY + " LIKE ?" +" OR "+RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION + " LIKE ?",
                new String[] {"%"+ filterSearchTerm.trim()+ "%","%"+ filterSearchTerm.trim()+ "%" }, null, null, EmployeeTable.COLUMN_ENTRY_ID + " ASC",
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {                
            	String empID = cursor.getString(cursor.getColumnIndex(RegisteredTable.JobTable.COLUMN_JOB_IDENTITY));
				String empName = cursor.getString(cursor.getColumnIndex(RegisteredTable.JobTable.COLUMN_JOB_DESCRIPTION));
				// add to list
                recordsList.add(String.format("(%s) %s", empID, empName));

            } while (cursor.moveToNext());
        }
        System.out.println("Serched Id-------->"+recordsList.toString());
        cursor.close();
        db.close();

        // return the list of records
        return recordsList;
    }
    
    public ArrayList<String> readActivityFromDB(String searchTerm) {

    	ArrayList<String> recordsList_New = new ArrayList<String>();
        
        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        //Cursor cursor = db.rawQuery(sql, null);
        String[] columnsToReturn = {
        		RegisteredTable.ActivityTable.COLUMN_ENTRY_ID,
        		RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY,
        		RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION
		};
        String filterSearchTerm="";
        if(searchTerm.contains("(")){
        	filterSearchTerm=searchTerm.replace("(", " ");        	
        }else if(searchTerm.contains(")")){
        	filterSearchTerm=searchTerm.replace(")", " ");
        }/*else if(searchTerm.contains("-")){
        	filterSearchTerm=searchTerm.replace("-", " ");
        }*/else if(searchTerm.contains("#")){
        	filterSearchTerm=searchTerm.replace("#", " ");
        }else{
        	filterSearchTerm=searchTerm;
        }
        System.out.println("searchTerm trimmed------>"+filterSearchTerm.trim());
        Cursor cursor =db.query(true, RegisteredTable.ActivityTable.TABLE_NAME, columnsToReturn, RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY + " LIKE ?" +" OR "+RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION + " LIKE ?",
                new String[] {"%"+ filterSearchTerm.trim()+ "%","%"+ filterSearchTerm.trim()+ "%" }, null, null, ActivityTable.COLUMN_ENTRY_ID + " ASC",
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {                
            	String empID = cursor.getString(cursor.getColumnIndex(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_IDENTITY));
				String empName = cursor.getString(cursor.getColumnIndex(RegisteredTable.ActivityTable.COLUMN_ACTIVITY_DESCRIPTION));
				// add to list
				recordsList_New.add(String.format("(%s) %s", empID, empName));

            } while (cursor.moveToNext());
        }
		// add elements to al, including duplicates
		HashSet hs = new HashSet();
		hs.addAll(recordsList_New);
		recordsList_New.clear();
		recordsList_New.addAll(hs);
        System.out.println("Serched Id-------->"+recordsList_New.toString());
        cursor.close();
        db.close();

        // return the list of records
        return recordsList_New;
    }

	/*
     * Read Part No records related to the search term
     */
	public ArrayList<String> readPartNoFromDB(String searchTerm) {

		ArrayList<String> recordsList = new ArrayList<String>();

		SQLiteDatabase db = this.getWritableDatabase();

		// execute the query
		//Cursor cursor = db.rawQuery(sql, null);
		String[] columnsToReturn = {
				RegisteredTable.MaterialTable.COLUMN_ENTRY_ID,
				RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY,
				RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION
		};
		String filterSearchTerm="";
		if(searchTerm.contains("(")){
			filterSearchTerm=searchTerm.replace("(", " ");
		}else if(searchTerm.contains(")")){
			filterSearchTerm=searchTerm.replace(")", " ");
		}/*else if(searchTerm.contains("-")){
        	filterSearchTerm=searchTerm.replace("-", " ");
        }*/else if(searchTerm.contains("#")){
			filterSearchTerm=searchTerm.replace("#", " ");
		}else{
			filterSearchTerm=searchTerm;
		}
		System.out.println("searchTerm trimmed------>"+filterSearchTerm.trim());
		Cursor cursor =db.query(true, RegisteredTable.MaterialTable.TABLE_NAME, columnsToReturn, RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY + " LIKE ?" +" OR "+RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION + " LIKE ?",
				new String[] {"%"+ filterSearchTerm.trim()+ "%","%"+ filterSearchTerm.trim()+ "%" }, null, null, RegisteredTable.MaterialTable.COLUMN_ENTRY_ID + " ASC",
				null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String empID = cursor.getString(cursor.getColumnIndex(RegisteredTable.MaterialTable.COLUMN_MATERIAL_IDENTITY));
				String empName = cursor.getString(cursor.getColumnIndex(RegisteredTable.MaterialTable.COLUMN_MATERIAL_DESCRIPTION));
				// add to list
				recordsList.add(String.format("(%s) %s", empID, empName));

			} while (cursor.moveToNext());
		}
		System.out.println("Serched Id-------->"+recordsList.toString());
		cursor.close();
		db.close();

		// return the list of records
		return recordsList;
	}



	public Cursor queryTable(SQLiteDatabase db, String tableToQuery, String[] columnsToReturn, String sortOrder) {
		Cursor c = db.query(tableToQuery,
				columnsToReturn,
				null,
				null,
				null,
				null,
				sortOrder);
		return c;
	}

	public Cursor queryTable(String tableToQuery, String[] columnsToReturn, String whereClause, String[] whereArgs, String sortOrder) {
		Cursor c = getReadableDatabase().query(tableToQuery,
				columnsToReturn,
				whereClause,
				whereArgs,
				null,
				null,
				sortOrder);
		return c;
	}

	public void updateTableField(String tableName, String field, String replacementValue, String condition) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "UPDATE " + tableName + " SET " + field + " = '" + replacementValue + "' WHERE " + condition;
		db.execSQL(query);

	}

	public int updateJobEntryField(int id,String value, String material_part_number, String material_quantity) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_VALUE,value);
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_PART_NUMBER,material_part_number);
		contentvalues.put(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_QUANTITY, material_quantity);
		return db.update(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, contentvalues, RegisteredTable.JobEntryDetailsTable.COLUMN_ENTRY_ID + " =?", new String[]{String.valueOf(id)});
	}

	/**
	 * @param condition The WHERE clause to check. "?" values will be replaced by the condition args.
	 * @param conditionArgs
	 */
	public void deleteRows(String tableName, String condition, String... conditionArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, condition, conditionArgs);
	}

	public int deleteNetworkListRow(String... conditionArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(RegisteredTable.NetworkMemoryTable.TABLE_NAME, RegisteredTable.NetworkMemoryTable.COLUMN_ENTRY_ID + "=?", conditionArgs);
	}

	public int deleteJobEntryRow(String... conditionArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, RegisteredTable.NetworkMemoryTable.COLUMN_ENTRY_ID + "=?", conditionArgs);
	}

	public int deleteJobEntryDetails() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, null, null);
	}
	
	public boolean isDuringDay() {
		boolean duringDay = false;
		try {
			ArrayList<TransactionInfo> transactionList = getDatabaseTransactionInfo("all_data");

			for(TransactionInfo tInfo : transactionList) {
				if(tInfo.uniqueValue.equals(EntryType.StartDay.entryName)) {
					duringDay = true;
				}
				if(tInfo.uniqueValue.equals(EntryType.EndDay.entryName)) {
					duringDay = false;
				}
			}
		} catch(Exception e) {
              e.printStackTrace();
		}
		return duringDay;
	}

	public ArrayList<TransactionInfo> getDatabaseTransactionInfo(String type) {
		ArrayList<TransactionInfo> transactionList = new ArrayList<TransactionInfo>();

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
		//String whereClause = "(strftime('%s', 'now') / 3600 - strftime('%s', transactionTimestamp) / 3600) < 24 * 7";
		//String[] whereArgs = null;
		//Cursor c = queryTable(getReadableDatabase(), TransactionTable.TABLE_NAME, columnsToReturn, TransactionTable.COLUMN_ENTRY_ID + " ASC");
		Cursor c;
		if(type.equalsIgnoreCase("all_data")){
			c  = queryTable(getReadableDatabase(), TransactionTable.TABLE_NAME, columnsToReturn, TransactionTable.COLUMN_ENTRY_ID + " ASC");
		}else{ // Sync pending data ....status can be "Pending" or "Failed" or "Sending"
			c = getReadableDatabase().query(TransactionTable.TABLE_NAME,columnsToReturn, TransactionTable.COLUMN_SEND_STATUS + "=? or " + TransactionTable.COLUMN_SEND_STATUS  + "=? or " + TransactionTable.COLUMN_SEND_STATUS  + "=?" ,new String[]{EntryType.Pending.entryName,EntryType.Failed.entryName,EntryType.Sending.entryName},	null,null,TransactionTable.COLUMN_ENTRY_ID + " ASC");	
		}

		if(c.moveToFirst()) {
			do {
				HashMap<String, String> columnsToValues = new HashMap<String, String>();
				for(int i = 0; i < c.getColumnCount(); i++) {
					columnsToValues.put(c.getColumnName(i), c.getString(i));
				}
				TransactionInfo tInfo = new TransactionInfo(columnsToValues);
				Date d = null;
				try {
					SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
					d = databaseFormat.parse(tInfo.transactionTimestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date now = new Date();
				if(d != null) {
					long timeDiffHours = ((now.getTime() - d.getTime()) / 3600000);
					if(timeDiffHours < 24 * 7) {
						transactionList.add(tInfo);
					}
				}
			} while(c.moveToNext());
		}
		c.close();
		return transactionList;
	}

	public ArrayList<TransactionInfo> getJobsOrderByDate(String employeeID) {



		ArrayList<TransactionInfo> transactionList = new ArrayList<TransactionInfo>();

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
		//String whereClause = "(strftime('%s', 'now') / 3600 - strftime('%s', transactionTimestamp) / 3600) < 24 * 7";
		//String[] whereArgs = null;
		//Cursor c = queryTable(getReadableDatabase(), TransactionTable.TABLE_NAME, columnsToReturn, TransactionTable.COLUMN_ENTRY_ID + " ASC");
		//Cursor c  = getReadableDatabase().query(RegisteredTable.TransactionTable.TABLE_NAME, columnsToReturn, RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP+">DATE('now','-7 days')", null, RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, null, RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP);
		Cursor c = getReadableDatabase().query(false, RegisteredTable.TransactionTable.TABLE_NAME, columnsToReturn,RegisteredTable.TransactionTable.COLUMN_EMPLOYEE_ID + " like ? AND " + RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP+">DATE('now','-7 days')", new String[]{employeeID}, null, null, RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP, null);
		
		int count = c.getCount();

		if(count > 0) {

			if(c.moveToFirst()) {
				do {
					HashMap<String, String> columnsToValues = new HashMap<String, String>();
					for(int i = 0; i < c.getColumnCount(); i++) {
						columnsToValues.put(c.getColumnName(i), c.getString(i));
					}
					TransactionInfo tInfo = new TransactionInfo(columnsToValues);
					Date d = null;
					try {
						SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
						d = databaseFormat.parse(tInfo.transactionTimestamp);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Date now = new Date();
					if(d != null) {
						long timeDiffHours = ((now.getTime() - d.getTime()) / 3600000);
						if(timeDiffHours < 24 * 7) {
							transactionList.add(tInfo);
						}
					}
				} while(c.moveToNext());
			}
			c.close();
		}else{

		}
		// To remove if any entries present before start day of that particlar day event
		for(int i=0;i<transactionList.size();i++){
			TransactionInfo transactionInfo = transactionList.get(i); 
			if(transactionInfo.uniqueValue.equalsIgnoreCase(EntryType.StartDay.entryName)){
				i = transactionList.size();	
			}else{
				deleteRows(RegisteredTable.TransactionTable.TABLE_NAME, RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " = ?",new String[] {String.valueOf(transactionInfo.rowNumber)});
				transactionList.remove(i);
			}
		}

		return transactionList;
	}

	/*	*//**
	 * 
	 *//*
	private void formatTransactionDetails() {
		// TODO Auto-generated method stub
		for(TransactionInfo transactionInfo : transactionList){
			if(transactionInfo.uniqueValue.equalsIgnoreCase(EntryType.StartDay.entryName)){
				return;
			}else{
				dbHelper.deleteRows(RegisteredTable.TransactionTable.TABLE_NAME, RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " = ?",new String[] {String.valueOf(transactionInfo.rowNumber)});

			}
		}
	}*/

	// To get network memory list
	public ArrayList<NetworkMemoryDetails> getNetworkMemoryList(){
		ArrayList<NetworkMemoryDetails> network_list = new ArrayList<NetworkMemoryDetails>();
		String[] columns_network_details = new String[]{RegisteredTable.NetworkMemoryTable.COLUMN_ENTRY_ID,RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_NAME,RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_ADDRESS,RegisteredTable.NetworkMemoryTable.COLUMN_MODIFIED_DATE,RegisteredTable.NetworkMemoryTable.COLUMN_HOST_IP,RegisteredTable.NetworkMemoryTable.COLUMN_PORT,RegisteredTable.NetworkMemoryTable.COLUMN_LOCAL_PATH};
		Cursor cursor = getReadableDatabase().query(RegisteredTable.NetworkMemoryTable.TABLE_NAME, columns_network_details, null, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				NetworkMemoryDetails networkDetails = new NetworkMemoryDetails();
				networkDetails.setId(cursor.getInt(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_ENTRY_ID)));
				networkDetails.setNetwork_name(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_NAME)));
				networkDetails.setNetwork_address(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_NETWORK_ADDRESS)));
				networkDetails.setModified_date(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_MODIFIED_DATE)));
				networkDetails.setHost_ip(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_HOST_IP)));
				networkDetails.setPort(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_PORT)));
				networkDetails.setLocal_path(cursor.getString(cursor.getColumnIndex(RegisteredTable.NetworkMemoryTable.COLUMN_LOCAL_PATH)));
				network_list.add(networkDetails);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return network_list;
	}

	public ArrayList<HashMap<String, String>> getPreviousJobEntries(ArrayList<HashMap<String,String>> jobEntryDetailsList){
		String[] columns_job_entries = new String[]{RegisteredTable.JobEntryDetailsTable.COLUMN_ENTRY_ID,RegisteredTable.JobEntryDetailsTable.COLUMN_ENTRY_ID,RegisteredTable.JobEntryDetailsTable.COLUMN_JOB_ENTRY_TYPE,RegisteredTable.JobEntryDetailsTable.COLUMN_VALUE,RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_PART_NUMBER,RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_QUANTITY};
		Cursor cursor = getReadableDatabase().query(RegisteredTable.JobEntryDetailsTable.TABLE_NAME, columns_job_entries,	null, null, null, null, null);

		if(cursor != null && cursor.getCount() > 0){
			HashMap<String,String> headerMap = new HashMap<String, String>();
			headerMap.put("Header", "Details History");
			jobEntryDetailsList.add(headerMap);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					HashMap<String, String> mMap = new HashMap<String, String>();
					mMap.put("id", cursor.getString(cursor.getColumnIndex(RegisteredTable.JobEntryDetailsTable.COLUMN_ENTRY_ID)));
					mMap.put("type", cursor.getString(cursor.getColumnIndex(RegisteredTable.JobEntryDetailsTable.COLUMN_JOB_ENTRY_TYPE)));
					mMap.put("value", cursor.getString(cursor.getColumnIndex(RegisteredTable.JobEntryDetailsTable.COLUMN_VALUE)));
					mMap.put("material_part_number", cursor.getString(cursor.getColumnIndex(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_PART_NUMBER)));
					mMap.put("material_quantity", cursor.getString(cursor.getColumnIndex(RegisteredTable.JobEntryDetailsTable.COLUMN_MATERIAL_QUANTITY)));
					mMap.put("status", "previous_job_entries");
					jobEntryDetailsList.add(mMap);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		return jobEntryDetailsList;
	}


	// To calculate employee hours and job hours
	public String[] calculateEmployee_JobHours(String login_employee_id,String job_startTime,int row_id){
		try{
			String[] columnsToReturn = {TransactionTable.COLUMN_TRANSACTION_TIMESTAMP,TransactionTable.COLUMN_UNIQUE_VALUE};
			// To get all entries after the specified column ID to calculate time spend till now...
			Cursor cursor  = getReadableDatabase().query(true, RegisteredTable.TransactionTable.TABLE_NAME, columnsToReturn,RegisteredTable.TransactionTable.COLUMN_EMPLOYEE_ID + " like ? AND " + RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " > ? AND ("+RegisteredTable.TransactionTable.COLUMN_UNIQUE_VALUE + " like ? OR " +RegisteredTable.TransactionTable.COLUMN_UNIQUE_VALUE + " like ? OR " +RegisteredTable.TransactionTable.COLUMN_UNIQUE_VALUE + " like ?)", new String[]{login_employee_id,String.valueOf(row_id),"All Lunch","All Return","Job"}, null, null ,RegisteredTable.TransactionTable.COLUMN_ENTRY_ID + " ASC" , null);
			String total_time = null;
			SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
			if(cursor.getCount() > 0){
				String lunch_starttime = null , next_job_start_time = null;
				boolean is_lunch_selected =false,is_returned = false,is_next_job_selected = false;
				if (cursor.moveToFirst()) {
					do {
						String unique_value = cursor.getString(cursor.getColumnIndex(RegisteredTable.TransactionTable.COLUMN_UNIQUE_VALUE));
						String time_stamp = cursor.getString(cursor.getColumnIndex(RegisteredTable.TransactionTable.COLUMN_TRANSACTION_TIMESTAMP));

						if(unique_value.equalsIgnoreCase("Job")){ // If job presents then another job entry added so stop calculating time
							next_job_start_time = time_stamp;
							is_next_job_selected = true;
							cursor.moveToLast();		
						}else{
							is_next_job_selected = false;
							if(unique_value.equalsIgnoreCase("All lunch")){
								lunch_starttime = time_stamp;
								is_lunch_selected = true;
								is_returned = false;
							}else if(unique_value.equalsIgnoreCase("All Return")){
								long end = databaseFormat.parse(time_stamp).getTime();
								long start = databaseFormat.parse(lunch_starttime).getTime();
								String lunch_time = calculateTimeDifference(end, start);
								if(total_time != null){ // If not null ... Same job assigned for two or more time...just sum it the time spent
									TimeInterval time = new TimeInterval("00:00:00.000");
									time = time.add(new TimeInterval(total_time+".000"));
									time = time.add(new TimeInterval(lunch_time+".000"));
									total_time = time.hours + ":" + time.minutes + ":" + time.seconds;
								}else{
									total_time = lunch_time;
								}
								is_returned = true; 

							}
						}

					} while (cursor.moveToNext());
				}

				if(is_lunch_selected && is_returned){ // If both selected
					long end = 0;
					if(is_next_job_selected){
						end = databaseFormat.parse(next_job_start_time).getTime();
					}else{
						end = System.currentTimeMillis();
					}
					long start = databaseFormat.parse(job_startTime).getTime();
					String total_time_done = calculateTimeDifference(end, start);
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
					Date d1 = df.parse(total_time);
					Date d2 = df.parse(total_time_done);
					return formatTotaltimeSpent(d2.getTime(), d1.getTime());
				}else if(is_lunch_selected){
					if(total_time == null){ // Only lunch selected
						long end = databaseFormat.parse(lunch_starttime).getTime();
						long start = databaseFormat.parse(job_startTime).getTime();
						return formatTotaltimeSpent(end, start);
					}else{ // multiple lunch selected
						long end = databaseFormat.parse(lunch_starttime).getTime();
						long start = databaseFormat.parse(job_startTime).getTime();
						String lunch_time = calculateTimeDifference(end, start);
						SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
						Date d1 = df.parse(total_time);
						Date d2 = df.parse(lunch_time);
						return formatTotaltimeSpent(d2.getTime(), d1.getTime());
					}
					
				}else if(!is_lunch_selected && !is_returned){ // Nothing selected
					long end = databaseFormat.parse(next_job_start_time).getTime();
					long start = databaseFormat.parse(job_startTime).getTime();
					return formatTotaltimeSpent(end, start);
				}else{
					return new String[]{"0","0","0","0"};	
				}
				
			}else{
				long end = System.currentTimeMillis();
				long start = databaseFormat.parse(job_startTime).getTime();
				return formatTotaltimeSpent(end, start);
			}
		}catch(Exception e){
			e.printStackTrace();	
			return new String[]{"0","0","0","0"};
		}
	}


	// To calculate time difference 
	private String calculateTimeDifference(long end_timestamp,long start_timestamp){
		try{
			long elapsedTime = end_timestamp - start_timestamp;
			String format = String.format("%%0%dd", 2);
			elapsedTime = elapsedTime / 1000;
			String seconds = String.format(format, elapsedTime % 60);
			String minutes = String.format(format, (elapsedTime % 3600) / 60);
			String hours = String.format(format, elapsedTime / 3600);
			String total_time_done = hours + ":" + minutes + ":" + seconds;
			return total_time_done;
		}catch(Exception e){
			e.printStackTrace();	
			return "0:00";
		}
	}
	
	// To format time
	private String[] formatTotaltimeSpent(long end,long start){
		long elapsedTime = end - start;
		String format = String.format("%%0%dd", 2);
		elapsedTime = elapsedTime / 1000;
		Log.i("ellapsed time", end + " "  + start + " " +elapsedTime + " ");
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, (elapsedTime % 3600) / 60);
		String hours = String.format(format, elapsedTime / 3600);
		String[] time_string = new String[4];
		time_string[0] = hours;
		time_string[1] = minutes;
		time_string[2] = seconds;
		time_string[3] = String.valueOf(elapsedTime);
		return time_string;
	}


}
