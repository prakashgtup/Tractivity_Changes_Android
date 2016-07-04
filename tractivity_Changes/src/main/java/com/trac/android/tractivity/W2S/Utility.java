package com.trac.android.tractivity.W2S;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.trac.android.tractivity.W2S.Model.JobDetailList;

import java.util.ArrayList;

/**
 * Created by krishna on 6/29/2016.
 */
public class Utility
{
  public static  ArrayList<JobDetailList> arraylist = new ArrayList<JobDetailList>();
  public static  ArrayList<JobDetailList> arrchecked = new ArrayList<JobDetailList>();
  public static  ArrayList<JobDetailList> arrUnchecked = new ArrayList<JobDetailList>();
  public static ProgressDialog progressdialog;

  // Check the Network connection.
  public static boolean hasConnection(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifiNetwork = cm
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (wifiNetwork != null && wifiNetwork.isConnected())
    {
      return true;
    }
    NetworkInfo mobileNetwork = cm
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if (mobileNetwork != null && mobileNetwork.isConnected())
    {
      return true;
    }
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork != null && activeNetwork.isConnected())
    {
      return true;
    }
    return false;
  }
}
