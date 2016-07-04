package com.trac.android.tractivity.logs;

import java.util.ArrayList;

/**
 * Getter/Setter class to manage employee Log details
 *
 */
public class EmployeeLogDetail {
	
	
	private String dayEvent,startTimeinMillis,calculatedTime,employeeCode,Job,previous_hour_of_day,status;
	private int row_id;
	private boolean isEmployeeAlreadyExistsinDayEvent;
	
	private ArrayList<String> jobEvent;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDayEvent() {
		return dayEvent;
	}

	public void setDayEvent(String dayEvent) {
		this.dayEvent = dayEvent;
	}

	public ArrayList<String> getJobEvent() {
		return jobEvent;
	}

	public void setJobEvent(ArrayList<String> jobEvent) {
		this.jobEvent = jobEvent;
	}

	public String getStartTimeinMillis() {
		return startTimeinMillis;
	}

	public void setStartTimeinMillis(String startTimeinMillis) {
		this.startTimeinMillis = startTimeinMillis;
	}

	public String getCalculatedTime() {
		return calculatedTime;
	}

	public void setCalculatedTime(String calculatedTime) {
		this.calculatedTime = calculatedTime;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getJob() {
		return Job;
	}

	public void setJob(String job) {
		Job = job;
	}

	public String getPrevious_hour_of_day() {
		return previous_hour_of_day;
	}

	public void setPrevious_hour_of_day(String previous_hour_of_day) {
		this.previous_hour_of_day = previous_hour_of_day;
	}

	public int getRow_id() {
		return row_id;
	}

	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}

	public boolean isEmployeeAlreadyExistsinDayEvent() {
		return isEmployeeAlreadyExistsinDayEvent;
	}

	public void setEmployeeAlreadyExistsinDayEvent(
			boolean isEmployeeAlreadyExistsinDayEvent) {
		this.isEmployeeAlreadyExistsinDayEvent = isEmployeeAlreadyExistsinDayEvent;
	}
	
}
