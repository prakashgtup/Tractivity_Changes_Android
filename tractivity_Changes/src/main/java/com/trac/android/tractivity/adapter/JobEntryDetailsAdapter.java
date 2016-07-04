package com.trac.android.tractivity.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;

/**
 * Adapter to populate Job entry details
 *
 */
public class JobEntryDetailsAdapter extends BaseAdapter{

	// Initialization of variables
	private Context mContext;
	private ArrayList<HashMap<String, String>> mAdapterList;
	
		
	public JobEntryDetailsAdapter(Context context,ArrayList<HashMap<String, String>> adapterList) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
        this.mAdapterList = adapterList;		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAdapterList.size();
	}


	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mAdapterList.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}


	@Override
	public View getView(final int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		final ViewHolder viewholder;
		if (view == null){
			view = LayoutInflater.from(mContext).inflate(com.trac.android.tractivity.V1.R.layout.listrow_jobdentry_details, null);
			viewholder = new ViewHolder();
			viewholder.mJobDetailsType_header = (TextView)view.findViewById(com.trac.android.tractivity.V1.R.id.listrow_job_details_list_typeID);
			viewholder.mJobDetailsValue = (TextView)view.findViewById(com.trac.android.tractivity.V1.R.id.listrow_job_details_list_valueID);
			view.setTag(viewholder);
		} else{
			viewholder = (ViewHolder)view.getTag();
		}
		// To get job details for each list row
		HashMap<String, String> jobDetailsMap = mAdapterList.get(position);
		if(jobDetailsMap.containsKey("Header")){ // If List header [ Detail history or  Pending details]
			viewholder.mJobDetailsType_header.setText(jobDetailsMap.get("Header"));
			viewholder.mJobDetailsValue.setVisibility(View.GONE);
			viewholder.mJobDetailsType_header.setBackgroundResource(R.color.light_grey);
		}else{
			viewholder.mJobDetailsType_header.setText(jobDetailsMap.get("type"));
			viewholder.mJobDetailsValue.setVisibility(View.VISIBLE);
			viewholder.mJobDetailsValue.setText(jobDetailsMap.get("value"));
			viewholder.mJobDetailsType_header.setBackgroundResource(R.color.white_clr);
		}
		
		return view;
	}

	// holder class to cache views to avoid reloading
	static class ViewHolder{
		TextView mJobDetailsType_header,mJobDetailsValue;
		
	}
}
