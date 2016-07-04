package com.trac.android.tractivity.logs;

import java.util.ArrayList;

/**
 *  Getter / setter to hold loglist detail for logview
 *
 */
public class LogListDetail {
	
	private String group_item;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	private ArrayList<EmployeeLogDetail> child_items;
	
	
	public String getGroup_item() {
		return group_item;
	}
	public void setGroup_item(String group_item) {
		this.group_item = group_item;
	}
	
	public ArrayList<EmployeeLogDetail> getChild_items() {
		return child_items;
	}
	
	public void setChild_items(ArrayList<EmployeeLogDetail> child_items) {
		this.child_items = child_items;
	}

}
