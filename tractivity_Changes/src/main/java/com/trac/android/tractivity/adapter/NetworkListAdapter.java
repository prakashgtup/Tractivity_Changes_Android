package com.trac.android.tractivity.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.configuration.NetworkMemoryDetails;
import com.trac.android.tractivity.database.DatabaseHelper;

/**
 * Adapter to populate network memory list
 *
 */
public class NetworkListAdapter extends BaseAdapter{

	private Activity mContext;
	private ArrayList<NetworkMemoryDetails> mAdapterList;
	private Boolean mShowDeleteBtn = false;
	private ListView mNetworkListRow;
	private DatabaseHelper mDatabaseHelper;
	private SimpleDateFormat mDatabaseDateFormat;

	public NetworkListAdapter(Activity context,ListView lv,ArrayList<NetworkMemoryDetails> adapterList) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mAdapterList = adapterList;
		this.mNetworkListRow = lv;
		mDatabaseHelper = DatabaseHelper.getInstance(mContext);
		mDatabaseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
	}

	public void refresh(ArrayList<NetworkMemoryDetails> fetchPlacesList,Boolean showdelete) {
		System.out.println("#--refresh notifydatasetchanged");
		this.mAdapterList = fetchPlacesList;
		mShowDeleteBtn = showdelete;
		notifyDataSetChanged();
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
			view = LayoutInflater.from(mContext).inflate(com.trac.android.tractivity.V1.R.layout.listrow_network_memory_list, null);
			viewholder = new ViewHolder();
			viewholder.mNetworkName = (TextView)view.findViewById(com.trac.android.tractivity.V1.R.id.listrow_network_nameID);
			viewholder.mModifiedDate = (TextView)view.findViewById(com.trac.android.tractivity.V1.R.id.listrow_modifiedDateID);
			viewholder.mNetworkAddress = (TextView)view.findViewById(com.trac.android.tractivity.V1.R.id.listrow_network_addressID);
			viewholder.mDeleteButton = (TextView) view.findViewById(com.trac.android.tractivity.V1.R.id.deleteBtn);
			viewholder.inner_content = (RelativeLayout) view.findViewById(R.id.inner_content);
			viewholder.front = (RelativeLayout) view
					.findViewById(R.id.front);
			viewholder.mainlayout = (RelativeLayout) view
					.findViewById(R.id.mainlayout);
			viewholder.showdelete = (ImageView) view
					.findViewById(R.id.showdelete);
			view.setTag(viewholder);
		} else{
			viewholder = (ViewHolder)view.getTag();
		}
		// To get network details from collections
		final NetworkMemoryDetails networkDetails = mAdapterList.get(position);
		viewholder.mNetworkName.setText(networkDetails.getNetwork_name());
		try {
			Date mDate = mDatabaseDateFormat.parse(networkDetails.getModified_date());
			Calendar mNetworkMemoryTime = Calendar.getInstance();
			mNetworkMemoryTime.setTime(mDate);
			Calendar mCurrentDate = Calendar.getInstance();
			long days = (mCurrentDate.getTimeInMillis() - mNetworkMemoryTime.getTimeInMillis()) / (24 * 60 * 60 * 1000);
			if(days == 0){ // For same day
				viewholder.mModifiedDate.setText(DateFormat.format("hh:mm a", mNetworkMemoryTime));
			}else if(days < 7){ // If less than a week
				switch (mNetworkMemoryTime.get(Calendar.DAY_OF_WEEK)) { 
				case Calendar.SUNDAY:
					viewholder.mModifiedDate.setText("Sunday"); 
					break;
				case Calendar.MONDAY:
					viewholder.mModifiedDate.setText("Monday");
					break;
				case Calendar.TUESDAY:
					viewholder.mModifiedDate.setText("Tuesday");
					break;
				case Calendar.WEDNESDAY:
					viewholder.mModifiedDate.setText("Wednesday");
					break;
				case Calendar.THURSDAY:
					viewholder.mModifiedDate.setText("Thursday");
					break;
				case Calendar.FRIDAY:
					viewholder.mModifiedDate.setText("Friday");
					break;
				case Calendar.SATURDAY:
					viewholder.mModifiedDate.setText("Saturday");
					break;
				}
			}else if(mNetworkMemoryTime.get(Calendar.YEAR)  == mCurrentDate.get(Calendar.YEAR)){ // For same year
				viewholder.mModifiedDate.setText(DateFormat.format("MMM dd", mNetworkMemoryTime));
			}else{ // Different year
				viewholder.mModifiedDate.setText(DateFormat.format("MMM dd,yyyy", mNetworkMemoryTime));
			} 

			viewholder.mNetworkAddress.setText(networkDetails.getNetwork_address());

			if(mShowDeleteBtn){ 
				viewholder.showdelete.setVisibility(View.VISIBLE);
				((SwipeListView) mNetworkListRow).setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				((SwipeListView) mNetworkListRow).getPositionsSelected();

				viewholder.showdelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {			
						((SwipeListView) mNetworkListRow).openAnimate(position);
					}
				});			

				viewholder.mainlayout.setClickable(false);
				viewholder.mainlayout.setEnabled(false);

				viewholder.mDeleteButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {			

						((SwipeListView) mNetworkListRow).closeAnimate(position);
						if(mDatabaseHelper.deleteNetworkListRow(String.valueOf(networkDetails.getId())) > 0){
							mAdapterList.remove(position);		
							notifyDataSetChanged(); 
						}
					}
				});

			}else{
				viewholder.showdelete.setVisibility(View.GONE);		
				viewholder.mainlayout.setClickable(true);
				viewholder.mainlayout.setEnabled(true);
				viewholder.mainlayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// To pass selected data to previous activity via intent
						Intent mExtraBundle = new Intent();
						mExtraBundle.putExtra("name", networkDetails.getNetwork_name());
						mExtraBundle.putExtra("host_ip", networkDetails.getHost_ip());
						mExtraBundle.putExtra("port", networkDetails.getPort());
						mExtraBundle.putExtra("local_path", networkDetails.getLocal_path());
						mContext.setResult(Activity.RESULT_OK, mExtraBundle);
						mContext.finish();
					}
				});
				((SwipeListView) mNetworkListRow).setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
				((SwipeListView) mNetworkListRow).setAnimationCacheEnabled(false);
				((SwipeListView) mNetworkListRow)
				.setSwipeListViewListener(new BaseSwipeListViewListener() {

					@Override
					public void onOpened(int position, boolean toRight) {

						// Called when open animation finishes
						int start = ((SwipeListView) mNetworkListRow)
								.getFirstVisiblePosition();
						int end = ((SwipeListView) mNetworkListRow).getLastVisiblePosition();
						for (int i = start; i <= end; i++) {

							if (i != position) {
								((SwipeListView) mNetworkListRow).closeAnimate(i);
							}
						}
					}
				});

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}
	// holder class to cache views to avoid reloading
	static class ViewHolder{
		private TextView mNetworkName,mNetworkAddress,mModifiedDate,mDeleteButton;
		RelativeLayout inner_content, front,mainlayout;
		ImageView showdelete;
	}
}
