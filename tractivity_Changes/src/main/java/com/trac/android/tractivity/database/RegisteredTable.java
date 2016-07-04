package com.trac.android.tractivity.database;

import android.provider.BaseColumns;

public class RegisteredTable {
	private static final String PRIMARY_KEY = " INTEGER PRIMARY KEY";
	private static final String TEXT_TYPE = " TEXT";
	
	public static abstract class EmployeeTable implements BaseColumns {
		public static final String TABLE_NAME = "Employee";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_EMPLOYEE_ID = "empID";
		public static final String COLUMN_EMPLOYEE_NAME = "empName";
		//public static final String COLUMN_WORK_GROUP = "workGroup";
		//public static final String COLUMN_SHOW_JOB_REV = "iTracShowJobRev";
		//public static final String COLUMN_SHOW_EMPLOYEE_LIST = "iTracShowEmpList";
		//public static final String COLUMN_SHOW_SUPER_LIST = "iTracShowSuperList";
		//public static final String COLUMN_ENABLE_GPS = "iTracEnableGPS";
		//public static final String COLUMN_SHOW_QTY = "ShowQTY";
		//public static final String COLUMN_SHOW_PN = "ShowPN";
		//public static final String COLUMN_SHOW_MQTY = "ShowMQTY";
		//public static final String COLUMN_SHOW_MPN = "ShowMPN";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_EMPLOYEE_ID + TEXT_TYPE + ",",
			COLUMN_EMPLOYEE_NAME + TEXT_TYPE + ",",
			//COLUMN_WORK_GROUP + TEXT_TYPE + ",",
			//COLUMN_SHOW_JOB_REV + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_EMPLOYEE_LIST + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_SUPER_LIST + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_ENABLE_GPS + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_QTY + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_PN + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_MQTY + BIT_TYPE + NOT_NULL + ",",
			//COLUMN_SHOW_MPN + BIT_TYPE + NOT_NULL + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}
	
	public static abstract class NetworkMemoryTable implements BaseColumns {
		public static final String TABLE_NAME = "NetworkMemory";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_NETWORK_NAME = "network_name";
		public static final String COLUMN_HOST_IP = "host_ip";
		public static final String COLUMN_PORT = "port";
		public static final String COLUMN_LOCAL_PATH = "local_path";
		public static final String COLUMN_NETWORK_ADDRESS = "server_address";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static final String COLUMNS = COLUMN_ENTRY_ID + PRIMARY_KEY + "," + COLUMN_NETWORK_NAME + TEXT_TYPE + "," + COLUMN_NETWORK_ADDRESS + TEXT_TYPE + " UNIQUE," + COLUMN_MODIFIED_DATE + TEXT_TYPE +","+ COLUMN_HOST_IP + TEXT_TYPE + ","+ COLUMN_PORT + TEXT_TYPE +"," + COLUMN_LOCAL_PATH + TEXT_TYPE;
	}
	
	public static abstract class JobEntryDetailsTable implements BaseColumns {
		public static final String TABLE_NAME = "JobEntryDetails";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_JOB_ENTRY_TYPE = "type";
		public static final String COLUMN_VALUE = "value";
		public static final String COLUMN_MATERIAL_QUANTITY = "material_quantity";
		public static final String COLUMN_MATERIAL_PART_NUMBER = "material_part_no";
		public static final String COLUMNS = COLUMN_ENTRY_ID + PRIMARY_KEY + "," + COLUMN_JOB_ENTRY_TYPE + TEXT_TYPE + "," + COLUMN_VALUE + TEXT_TYPE + "," + COLUMN_MATERIAL_PART_NUMBER + TEXT_TYPE+ "," + COLUMN_MATERIAL_QUANTITY + TEXT_TYPE;
	}
	
	
	public static abstract class ActivityTable implements BaseColumns {
		public static final String TABLE_NAME = "Activity";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_ACTIVITY_IDENTITY = "activityIdentity";
		public static final String COLUMN_ACTIVITY_DESCRIPTION = "activityDescription";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_ACTIVITY_IDENTITY + TEXT_TYPE + ",",
			COLUMN_ACTIVITY_DESCRIPTION + TEXT_TYPE + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}

	public static abstract class BudgetedActivityTable implements BaseColumns {
		public static final String TABLE_NAME = "BudgetedActivity";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_JOB_IDENTITY = "jobIdentity";
		public static final String COLUMN_PHASE_IDENTITY = "phaseIdentity";
		public static final String COLUMN_ACTIVITY_IDENTITY = "activityIdentity";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_JOB_IDENTITY + TEXT_TYPE + ",",
			COLUMN_PHASE_IDENTITY + TEXT_TYPE + ",",
			COLUMN_ACTIVITY_IDENTITY + TEXT_TYPE + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}

	public static abstract class JobTable implements BaseColumns {
		public static final String TABLE_NAME = "Job";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_JOB_IDENTITY = "jobIdentity";
		public static final String COLUMN_JOB_DESCRIPTION = "jobDescription";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_JOB_IDENTITY + TEXT_TYPE + ",",
			COLUMN_JOB_DESCRIPTION + TEXT_TYPE + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}
	
	public static abstract class MaterialTable implements BaseColumns {
		public static final String TABLE_NAME = "Material";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_MATERIAL_IDENTITY = "materialIdentity";
		public static final String COLUMN_MATERIAL_DESCRIPTION = "materialDescription";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_MATERIAL_IDENTITY + TEXT_TYPE + ",",
			COLUMN_MATERIAL_DESCRIPTION + TEXT_TYPE + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}

	public static abstract class PhaseTable implements BaseColumns {
		public static final String TABLE_NAME = "Phase";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_JOB_IDENTITY = "jobIdentity";
		public static final String COLUMN_PHASE_IDENTITY = "phaseIdentity";
		public static final String COLUMN_PHASE_DESCRIPTION = "phaseDescription";
		public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_JOB_IDENTITY + TEXT_TYPE + ",",
			COLUMN_PHASE_IDENTITY + TEXT_TYPE + ",",
			COLUMN_PHASE_DESCRIPTION + TEXT_TYPE + ",",
			COLUMN_MODIFIED_DATE + TEXT_TYPE
		};
	}

	public static abstract class TransactionTable implements BaseColumns {
		public static final String TABLE_NAME = "Transactions";
		public static final String COLUMN_ENTRY_ID = "id";
		public static final String COLUMN_EMPLOYEE_ID = "empID";
		public static final String COLUMN_TRANSACTION_TIMESTAMP = "transactionTimestamp";
		public static final String COLUMN_SENT_TIMESTAMP = "sentTimestamp";
		public static final String COLUMN_DURATION = "duration";
		public static final String COLUMN_UNIQUE_VALUE = "uniqueValue";
		public static final String COLUMN_EMPLOYEE_CODE = "empCode";
		public static final String COLUMN_JOB_CODE = "jobCode";
		public static final String COLUMN_PHASE_CODE = "itemCode";
		public static final String COLUMN_ACTIVITY_CODE = "actCode";
		public static final String COLUMN_DETAIL_VALUE = "detailValue";
		public static final String COLUMN_GPS_LAT = "GPS_LAT";
		public static final String COLUMN_GPS_LON = "GPS_LON";
		public static final String COLUMN_SEND_STATUS = "sendStatus";
		public static String[] columns = {
			COLUMN_ENTRY_ID + PRIMARY_KEY + ",",
			COLUMN_EMPLOYEE_ID + TEXT_TYPE + ",",
			COLUMN_TRANSACTION_TIMESTAMP + TEXT_TYPE + ",",
			COLUMN_SENT_TIMESTAMP + TEXT_TYPE + ",",
			COLUMN_DURATION + TEXT_TYPE + ",",
			COLUMN_UNIQUE_VALUE + TEXT_TYPE + ",",
			COLUMN_EMPLOYEE_CODE + TEXT_TYPE + ",",
			COLUMN_JOB_CODE + TEXT_TYPE + ",",
			COLUMN_PHASE_CODE + TEXT_TYPE + ",",
			COLUMN_ACTIVITY_CODE + TEXT_TYPE + ",",
			COLUMN_DETAIL_VALUE + TEXT_TYPE + ",",
			COLUMN_GPS_LAT + TEXT_TYPE + ",",
			COLUMN_GPS_LON + TEXT_TYPE + ",",
			COLUMN_SEND_STATUS + TEXT_TYPE
		};
	}
	
	public static String getColumnsStringFromArray(String[] strings) {
		String columnString = "";
		for(int i = 0; i < strings.length; i++) {
			columnString += strings[i];
		}
		return columnString;
	}
}
