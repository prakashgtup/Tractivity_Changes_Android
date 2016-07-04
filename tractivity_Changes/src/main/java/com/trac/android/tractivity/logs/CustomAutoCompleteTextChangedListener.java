package com.trac.android.tractivity.logs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

public class CustomAutoCompleteTextChangedListener implements TextWatcher{

	public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
	Context context;
	
	public CustomAutoCompleteTextChangedListener(Context context){
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

		NewiTrac mainActivity = ((NewiTrac) context);
		
		// query the database based on the user input
		NewiTrac.item = mainActivity.getItemsFromDb(userInput.toString());
		
		// update the adapater
		mainActivity.mJobAdapter.notifyDataSetChanged();
		mainActivity.mJobAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
		mainActivity.mJob.setAdapter(mainActivity.mJobAdapter);
		
	}

}
