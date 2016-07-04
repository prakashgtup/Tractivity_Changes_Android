package com.trac.android.tractivity.configuration;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.database.DatabaseHelper;
import com.trac.android.tractivity.login.LoginPage;
import com.trac.android.tractivity.utils.AlertNotification;
import com.trac.android.tractivity.utils.NetworkCheck;

/**
 * 
 * Activity to display and manage Configuration host url details
 *
 */

public class ConfigHostURLiTrac extends Activity implements TextWatcher {
	private TextView mConnectServer, mMainTitle,mProgressText,mHelpMenu,mTitleRightMenu;
	private EditText mLocalPath, mPortNumber, mHostIP,mNetworkName;
	private LinearLayout mNetworkNameContainer;
	private ImageView mNetworkMemoryList;
	private ProgressBar mProgressBar;
	private ConnectionURLTask mCheckHostUrlTask;
	private NetworkCheck mNetworkCheck;
	private CheckBox mRememberNetwork;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// Set the layout from the XML resource
		setContentView(R.layout.activity_configuration);
		// Get the window and set the desired feature / properties
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.app_custom_title_bar);

		mMainTitle = (TextView) findViewById(R.id.MainTitle);
		mHelpMenu = (TextView) findViewById(R.id.leftmenuID);
		mTitleRightMenu = (TextView) findViewById(R.id.rightmenuID);
		mMainTitle.setText(getResources().getString(R.string.server_connection));
		mHelpMenu.setText(getResources().getString(R.string.help));
		mTitleRightMenu.setVisibility(View.INVISIBLE);

		mNetworkNameContainer = (LinearLayout) findViewById(R.id.network_name_containerID);
		mNetworkName = (EditText) findViewById(R.id.network_nameID);
		mNetworkMemoryList = (ImageView) findViewById(R.id.server_details_list_imageID);
		mHostIP = (EditText) findViewById(R.id.hostIP);
		mPortNumber = (EditText) findViewById(R.id.portno);
		mLocalPath = (EditText) findViewById(R.id.localpath);
		mConnectServer = (TextView) findViewById(R.id.config_ok);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressText = (TextView) findViewById(R.id.progress_textID);
		mRememberNetwork = (CheckBox) findViewById(R.id.remember_network_checkboxID);

		mCheckHostUrlTask = new ConnectionURLTask(this, mProgressBar,mProgressText,mConnectServer,mNetworkNameContainer,mNetworkName.getText().toString(),mRememberNetwork);
		mNetworkCheck = new NetworkCheck();

		// Registering listeners for edittext
		mHostIP.addTextChangedListener(this);
		mPortNumber.addTextChangedListener(this);
		mLocalPath.addTextChangedListener(this);

		String hostIP = null, port = null, local = null,network_name = null;

		// Declare shared preferences for configuration details
		final SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Activity.MODE_PRIVATE);
		hostIP = customSharedPreference.getString("hostIP", null);
		local = customSharedPreference.getString("localPath", null);
		port = customSharedPreference.getString("port", null);
		network_name = customSharedPreference.getString("network_name", null);
		// When Launching Application as fresh copy, set default Tractivity URL
		// as IP address for (HostIp,Port no,and Local path)
		if (hostIP == null) {
			mHostIP.requestFocus();
			//mHostIP.setText("itrac.tractivity.com");
		} else {
			mHostIP.setText(hostIP);
		}
		if (port == null) {
			mPortNumber.setText("7373");
		} else {
			mPortNumber.setText(port);
		}
		if (local == null) {
			mLocalPath.setText("/Service.asmx");
		} else {
			mLocalPath.setText(local);
		}
		if (network_name != null) {
			mNetworkName.setText(network_name);
		}
		
		// OK button Listener for ConfigHostURLitrac
		mConnectServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// pushOk();
				String network_address = "http://" +mHostIP.getText().toString().trim() + ":" + mPortNumber.getText().toString().trim() + mLocalPath.getText().toString().toLowerCase(Locale.getDefault()).trim();
				// Storing network details in preference File
				SharedPreferences customSharedPreference = getSharedPreferences("iTracSharedPrefs", Context.MODE_PRIVATE);
				final SharedPreferences.Editor editor = customSharedPreference.edit();
				editor.putString("network_name",mNetworkName.getText().toString().trim());
				editor.putString("hostIP",mHostIP.getText().toString().trim());
				editor.putString("localPath",mLocalPath.getText().toString().trim());
				editor.putString("port",mPortNumber.getText().toString().trim());
				editor.putString("network_address",network_address);
				editor.commit();
				if(mRememberNetwork.isChecked()){ // If remember network is checked
					if(mNetworkName.getText().toString().trim().length() > 0){
						// Updating DB
						DatabaseHelper.getInstance(ConfigHostURLiTrac.this).InsertorUpdateNetworkListTable(mNetworkName.getText().toString(),mHostIP.getText().toString(),mPortNumber.getText().toString(),mLocalPath.getText().toString(),network_address);
						// Launch Login Activity
						startActivity(new Intent(ConfigHostURLiTrac.this,LoginPage.class));
					}else{ // IF not checked
						new AlertNotification().showAlertPopup(ConfigHostURLiTrac.this,"Network Name","Please enter a name for your network to be recognized",mNetworkName);
					}	
				}else{
					// Launch Login Activity
					startActivity(new Intent(ConfigHostURLiTrac.this,LoginPage.class));
				}
			}
		});

		mHelpMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tractivityhelp.com/itrac/"));
				startActivity(Intent.createChooser(browserIntent, "Open via"));
			}
		});

		mNetworkMemoryList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(ConfigHostURLiTrac.this,NetworkMemoryList.class),1);
			}
		});

        // To check if Corresponding day event is ended or not 
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
		if (dbHelper.isDuringDay()) { // If not ended navigate to Login -> Log screen
			startActivity(new Intent().setClass(getApplicationContext(),LoginPage.class));
		}
		dbHelper.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 1){
			Bundle mIntentExtras = data.getExtras();
			mNetworkName.setText(mIntentExtras.getString("name"));
			mHostIP.setText(mIntentExtras.getString("host_ip"));
			mPortNumber.setText(mIntentExtras.getString("port"));
			mLocalPath.setText(mIntentExtras.getString("local_path"));
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		mProgressText.setText(getResources().getString(R.string.attempting_connection));
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		try {
			if(mNetworkCheck.ConnectivityCheck(this)){
				// To cancel previous async task if running or Pending already
				if(mCheckHostUrlTask.getStatus() == Status.RUNNING || mCheckHostUrlTask.getStatus() == Status.PENDING)
					mCheckHostUrlTask.cancel(true);
				mProgressBar.setProgress(0);
				mConnectServer.setVisibility(View.GONE);
				mRememberNetwork.setVisibility(View.GONE);
				mNetworkNameContainer.setVisibility(View.INVISIBLE);
				if(mHostIP.getText().toString().trim().length() > 0 && mPortNumber.getText().toString().trim().length() > 0 && mLocalPath.getText().toString().trim().length() > 0 && mLocalPath.getText().toString().trim().endsWith(".asmx")){
					mCheckHostUrlTask = new ConnectionURLTask(this, mProgressBar,mProgressText,mConnectServer,mNetworkNameContainer,mNetworkName.getText().toString(),mRememberNetwork);
					mCheckHostUrlTask.execute(mHostIP.getText().toString(),mPortNumber.getText().toString(),mLocalPath.getText().toString());		
				}else{
					mProgressText.setText(getResources().getString(R.string.unknown_server));
				}
			}else{
				mProgressText.setText(getResources().getString(R.string.no_network));
				mProgressBar.setProgress(0);
				mConnectServer.setVisibility(View.GONE);
				mRememberNetwork.setVisibility(View.GONE);
				mNetworkNameContainer.setVisibility(View.INVISIBLE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
