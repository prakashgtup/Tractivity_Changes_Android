package com.trac.android.tractivity.W2S;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.W2S.Adapter.JobAdapter;
import com.trac.android.tractivity.W2S.Adapter.JobDetailsAdapter;
import com.trac.android.tractivity.W2S.Adapter.PopupListAdapter;
import com.trac.android.tractivity.W2S.Model.JobDetailList;
import com.trac.android.tractivity.W2S.Model.JobList;
import com.trac.android.tractivity.W2S.Model.popList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class JobDetails extends Activity {

    String[] jobId, jobNumber, jobDescription, jobQuality, jobArea, jobStatus;
    PopupWindow popupwindow;
    ArrayList<popList> arrayPopList = new ArrayList<popList>();
    ListView listViewJob;
    public static JobDetailsAdapter adapter;
    EditText edtSearch;
    Handler handlerJobDetails;
    TextView txtback, txtSearch, txtBarcodeScan,txtNoJobFound;
    CheckBox jobscheckbox;
    Spinner spinnerSortid, spinnerMovedItem,spinnerShowselected;
    boolean[] checkboxJob;
    String [] strSortName,strMoveItem,strCheckedJobs;
    Point p;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        txtback = (TextView) findViewById(R.id.txtbck);
        txtNoJobFound=(TextView)findViewById(R.id.txtNojobsFound);
        Utility.arrchecked.clear();
        spinnerSortid = (Spinner) findViewById(R.id.spinnersortid);
        spinnerMovedItem = (Spinner) findViewById(R.id.spinnerMovedItem);
        spinnerShowselected=(Spinner)findViewById(R.id.spinnerShowselected);
        strSortName= new String[]{"Sort by Id",
                    "Sort by Stage",
                    "Sort by Area",
        };
        ArrayAdapter<String> adapter_sortid = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strSortName);
        adapter_sortid
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortid.setAdapter(adapter_sortid);
        strMoveItem= new String[]{"Move Selected item to","WIP",
                    "Stored",
                    "Completed",
                    "Shipped",
                    "Installed"
        };
        ArrayAdapter<String> adapter_moveItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strMoveItem);
        adapter_moveItem
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovedItem.setAdapter(adapter_moveItem);
        spinnerMovedItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"show",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        strCheckedJobs =new String[]{"Show All",
                    "Show Selected",
                    "Show Unselected",

        };
        txtSearch = (TextView) findViewById(R.id.txtSearch);
        txtBarcodeScan = (TextView) findViewById(R.id.barcode_txt);
        jobscheckbox = (CheckBox) findViewById(R.id.jobCheckbox);

        ArrayAdapter<String> adapter_showselected = new ArrayAdapter<String>(JobDetails.this,android.R.layout.simple_spinner_item,strCheckedJobs);
        adapter_showselected.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShowselected.setAdapter(adapter_showselected);


        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobDetails.this.finish();
            }
        });
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearch.length() > 0) {
                    edtSearch.setText("");
                }
            }
        });
        txtBarcodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBarcodeScannerAlert();
            }
        });
        jobId = new String[]{"#1", "#2", "#3", "#4"};
        jobNumber = new String[]{"W235", "W236", "W237", "W238"};
        jobDescription = new String[]{"Description : Standard Tall is in good condition", "Description : Standard Tall is in normal condition", "Description : zzzzzzzzz", "Description : yyyyyyyyy"};
        jobQuality = new String[]{"Qty 1", " Qty 2", " Qty 3", "Qty 4"};
        jobArea = new String[]{"Area 1", "Area 2", "Area 3", "Area 4"};
        jobStatus = new String[]{"INSTALLED", "SHIPPED", "SHIPPED", "INSTALLED"};
        checkboxJob = new boolean[]{false, false, false, false};
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        listViewJob = (ListView) findViewById(R.id.listView);
        Utility.arraylist.clear();
        jobdetails();
        handlerJobDetails = new Handler()
        {
            public void handleMessage(Message msg)
            {

                switch (msg.getData().getInt("status"))
                {
                    case 1:
                        adapter = new JobDetailsAdapter(JobDetails.this, Utility.arraylist);
                        // Binds the Adapter to the ListView
                        listViewJob.setAdapter(adapter);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:
                        break;
                }

            }

        };
        //Pass results to ListViewAdapter Class

        //Log.i("Arraylist",""+Utility.arraylist.size());
        listViewJob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CheckBox checkbox = (CheckBox) v.getTag(R.id.chckBox);

            }
        });
        //Show Checked and unchecked
        jobscheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {

                      spinnerShowselected.performClick();
//                    for (JobDetailList jb : Utility.arrchecked) {
//
//                        Log.i("Checked Item", "" + jb.getJobarea());
//                    }
//                    adapter = new JobDetailsAdapter(JobDetails.this, Utility.arrchecked);
//                    listViewJob.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }
            }
        });
        jobscheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.i("Check","Check");

                for (JobDetailList jb : Utility.arrchecked)
                {

                        Log.i("Checked Item", "" + jb.getJobarea());
                }
//                adapter = new JobDetailsAdapter(JobDetails.this, Utility.arrchecked);
//                listViewJob.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });
        // Locate the EditText in listview_main.xml
        // Capture Text in EditText
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
                if(Utility.arraylist.size()==0)
                {
                    listViewJob.setVisibility(View.INVISIBLE);
                    txtNoJobFound.setVisibility(View.VISIBLE);
                }
                else
                {
                    listViewJob.setVisibility(View.VISIBLE);
                    txtNoJobFound.setVisibility(View.INVISIBLE);
                }
                if (edtSearch.length() > 0)
                {
                    txtSearch.setBackgroundResource(R.drawable.edit_delete);

                }
                else
                {
                    txtSearch.setBackgroundResource(R.drawable.search_black);
                    Log.i("Arraylist",""+Utility.arraylist.size());
                }
            }


            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }


            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if (checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        jobscheckbox = (CheckBox) findViewById(R.id.jobCheckbox);
        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        // jobscheckbox.getLocationOnScreen(location);
        //  btnMovedItem.getLocationOnScreen(location);
        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private void showPopup(final Activity context, Point p, String status) {



        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_element);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.left_arrow_popup, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        TextView close = (TextView) layout.findViewById(R.id.txtDone);
        final ListView listPopup = (ListView) layout.findViewById(R.id.listView);

        arrayPopList.clear();
        for (int i = 0; i < strCheckedJobs.length; i++) {
            popList popuplist = new popList(strCheckedJobs[i]);
            // Binds all strings into an array
            arrayPopList.add(popuplist);
        }
        final PopupListAdapter popupListAdapter = new PopupListAdapter(this, arrayPopList);
        listPopup.setAdapter(popupListAdapter);
        listPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                for (JobDetailList jb : Utility.arrchecked) {

                    Log.i("Checked Item", "" + jb.getJobarea());

                }
                adapter = new JobDetailsAdapter(getApplicationContext(), Utility.arrchecked);
                // Binds the Adapter to the ListView
                listViewJob.setAdapter(adapter);
                popup.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        View container = (View) popup.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wl = (WindowManager.LayoutParams) container.getLayoutParams();
        wl.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wl.dimAmount = 0.5f;
        wm.updateViewLayout(container, wl);
    }

    // Alert pop up with options for confirmation
    private void showBarcodeScannerAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JobDetails.this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getResources().getString(R.string.scan_barcode_alert_message));
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                IntentIntegrator integrator = new IntentIntegrator(JobDetails.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        builder.setNegativeButton("Bluetooth Device", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            // handle scan result
            if (scanResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + scanResult.getContents(), Toast.LENGTH_LONG).show();
            }
            Log.i("data------>", scanResult.toString());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void jobdetails()
    {

        if(Utility.hasConnection(JobDetails.this))
        {

//            try {
//                String strJsonResponse = "";
//                InputStream inputStream = openFileInput("Jobs.txt");
//                if ( inputStream != null ) {
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String receiveString = "";
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    while ( (receiveString = bufferedReader.readLine()) != null ) {
//                        stringBuilder.append(receiveString);
//                    }
//
//                    inputStream.close();
//                    strJsonResponse = stringBuilder.toString();
//                    Log.i("Json Format","Json"+strJsonResponse);
//                }
//            }
//            catch (FileNotFoundException e) {
//                Log.e("login activity", "File not found: " + e.toString());
//            } catch (IOException e) {
//                Log.e("login activity", "Can not read file: " + e.toString());
//            }
            final String strJobDetails="{\"Jobdetails\": [\n" +
                    " {\"jobid\":\"#1\",\"jobproductid\":\"W234\",\"jobdescription\":\"Standard Tall is in good condition\",\"area\":\"Area 1\",\"qulity\":\"1\",\"status\":\"Installed\"}, \n" +
                    " {\"jobid\":\"#2\",\"jobproductid\":\"W235\",\"jobdescription\":\"Standard Tall is in normal condition\",\"area\":\"Area 2\",\"qulity\":\"2\",\"status\":\"Shipped\"}\n" +
                    " ]\n" +
                    "}";
            // Toast.makeText(getBaseContext(),"file read"+temp.toString(),Toast.LENGTH_SHORT).show();
            Log.i("File","File"+strJobDetails.toString());
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jobj = new JSONObject(strJobDetails);
                        Log.i("JsonResponse",""+jobj.toString());
                        JSONArray array=jobj.getJSONArray("Jobdetails");
                        Log.i("Jsonarray",""+array.length());
                        for(int i=0;i<array.length();i++)
                        {
                            JSONObject obj=array.getJSONObject(i);
                            Log.i("Object",""+obj.toString());
                            JobDetailList list=new JobDetailList();
                            list.setJobnumber(obj.getString("jobproductid"));
                            list.setJobid(obj.getString("jobid"));
                            list.setJobdescription(obj.getString("jobdescription"));
                            list.setJobqulity(obj.getString("qulity"));
                            list.setJobarea(obj.getString("area"));
                            list.setJobstatus(obj.getString("status"));
                            Utility.arraylist.add(list);
                        }
                        Message msgobj;
                        msgobj = handlerJobDetails.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt("status", 1);
                        msgobj.setData(bundle);
                        handlerJobDetails.sendMessage(msgobj);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }).start();
        }
    }
}
