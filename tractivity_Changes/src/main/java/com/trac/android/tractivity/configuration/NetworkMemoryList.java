package com.trac.android.tractivity.configuration;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.adapter.NetworkListAdapter;
import com.trac.android.tractivity.database.DatabaseHelper;

/**
 * Activity to list network memory list
 *
 */
public class NetworkMemoryList  extends Activity{
	
	private SwipeListView mNetworkList;	
	private DatabaseHelper mSQLIteDatabaseHelper;
	private TextView mTitle,mTitleBarLeftMenu,mTitleBarRightMenu;
	private boolean mIsShowDeleteListImage = false;
	private ArrayList<NetworkMemoryDetails> mNetworkListCollection = new ArrayList<NetworkMemoryDetails>();
	private NetworkListAdapter mNetworkListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.activity_network_memory_list);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.app_custom_title_bar);

		mTitle = (TextView) findViewById(R.id.MainTitle);
		mTitleBarLeftMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleBarRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mNetworkList = (SwipeListView) findViewById(R.id.network_memory_listID);
		mTitle.setText(getResources().getString(R.string.network_memory));
		mTitleBarLeftMenu.setText(getResources().getString(R.string.back));
		mTitleBarLeftMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
		mTitleBarRightMenu.setText(getResources().getString(R.string.edit));
		mSQLIteDatabaseHelper = DatabaseHelper.getInstance(this);
		// To populate network memory list from database
		mNetworkListCollection = mSQLIteDatabaseHelper.getNetworkMemoryList();
		mNetworkListAdapter = new NetworkListAdapter(this, mNetworkList,mNetworkListCollection);
		mNetworkList.setAdapter(mNetworkListAdapter);
		
		mTitleBarLeftMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mTitleBarRightMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mNetworkListCollection.size()>0){
					if(!mIsShowDeleteListImage){
						mIsShowDeleteListImage = true;
						mNetworkListAdapter.refresh(mNetworkListCollection,mIsShowDeleteListImage);
						mTitleBarRightMenu.setText(getResources().getString(R.string.done));
					}else{
						mIsShowDeleteListImage = false;
						mNetworkListAdapter.refresh(mNetworkListCollection,mIsShowDeleteListImage);
						mTitleBarRightMenu.setText(getResources().getString(R.string.edit));
						mNetworkList.closeOpenedItems();
					}
				}	
			}
		});
		
		mNetworkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View arg1, int position,long arg3) {
				// TODO Auto-generated method stub
				// To get selected Network details
				
			}
		});
		
		dosetListviewSwipeOffset();
	}
	
	private void dosetListviewSwipeOffset() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,96, getResources().getDisplayMetrics());
		mNetworkList.setOffsetLeft(width - wt_px);
	}

}
