package com.trac.android.tractivity.logs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

public class CustomAutoCompleteTextChangedListener_Activity implements TextWatcher{

	public static final String TAG = "CustomAutoCompleteTextChangedListener_Activity.java";
	Context context;
	
	public CustomAutoCompleteTextChangedListener_Activity(Context context){
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
		Log.e(TAG, "User input: " + userInput);

		NewiTrac mainActivity = ((NewiTrac) context);
		
		// query the database based on the user input
		NewiTrac.item = mainActivity.getItemsFromDb_Activity(userInput.toString());
		
		// update the adapater
		mainActivity.mActivityAdapter.notifyDataSetChanged();
		mainActivity.mActivityAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
		mainActivity.mActivity.setAdapter(mainActivity.mActivityAdapter);
		
	}

}
