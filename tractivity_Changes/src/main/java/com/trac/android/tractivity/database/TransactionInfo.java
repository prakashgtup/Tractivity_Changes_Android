package com.trac.android.tractivity.database;

import java.util.HashMap;

public class TransactionInfo {
	public int rowNumber;
	public String empID;
	public String transactionTimestamp;
	public String sentTimestamp;
	public String duration;
	public String uniqueValue;
	public String empCode;
	public String jobCode;
	public String itemCode;
	public String actCode;
	public String detailValue;
	public String GPS_LAT;
	public String GPS_LON;
	public String sendStatus;
	
	public TransactionInfo(HashMap<String, String> columnData) {
		rowNumber = Integer.parseInt(columnData.get("id"));
		empID = columnData.get("empID");
		transactionTimestamp = columnData.get("transactionTimestamp");
		sentTimestamp = columnData.get("sentTimestamp");
		duration = columnData.get("duration");
		uniqueValue = columnData.get("uniqueValue");
		empCode = columnData.get("empCode");
		jobCode = columnData.get("jobCode");
		itemCode = columnData.get("itemCode");
		actCode = columnData.get("actCode");
		detailValue = columnData.get("detailValue");
		GPS_LAT = columnData.get("GPS_LAT");
		GPS_LON = columnData.get("GPS_LON");
		sendStatus = columnData.get("sendStatus");
	}
}
