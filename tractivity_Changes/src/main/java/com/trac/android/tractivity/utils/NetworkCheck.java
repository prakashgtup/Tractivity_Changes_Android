package com.trac.android.tractivity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 
 * A generic class to check the availability of network connection
 *
 */

public class NetworkCheck
{
  public boolean mNetworkConnectingFlag = false;
  public Context mNetworkContext;
  public ConnectivityManager mNetworkConnection;
  public NetworkInfo info;
  public int speed;
  public WifiManager wifi;
  public WifiInfo wifiInfo;
  
  public boolean ConnectivityCheck(Context context) {
		mNetworkContext=context;
		//Network connectivity check
		mNetworkConnection = (ConnectivityManager) mNetworkContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(mNetworkConnection.getActiveNetworkInfo()!=null && mNetworkConnection.getActiveNetworkInfo().isAvailable() && mNetworkConnection.getActiveNetworkInfo().isConnectedOrConnecting()){
			if(mNetworkConnection.getActiveNetworkInfo().isConnected()){
				mNetworkConnectingFlag=true;
			}else if(mNetworkConnection.getActiveNetworkInfo().isFailover()){
				mNetworkConnectingFlag=false;
			}
		}else{
			mNetworkConnectingFlag=false;
		}
		
		return mNetworkConnectingFlag;
	}
}

