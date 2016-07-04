package com.trac.android.tractivity.W2S;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.W2S.Adapter.JobAdapter;
import com.trac.android.tractivity.W2S.Model.JobDetailList;
import com.trac.android.tractivity.W2S.Model.JobList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

public class Jobs extends Activity
{
    private static final int READ_BLOCK_SIZE = 100;
    String[] jobname;
    String[] jobdate;
    String[] jobitem;
    ArrayList<JobList> arraylist = new ArrayList<JobList>();
    ListView list;
    JobAdapter adapter;
    Handler handlerJob;
    TextView txtNoJobsFound;
    EditText edtSearch;
    TextView txtback,txtSearch;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        txtback=(TextView)findViewById(R.id.txtbck);
        txtSearch=(TextView)findViewById(R.id.txtSearch);
        txtNoJobsFound=(TextView)findViewById(R.id.txtNojobsFound);
        txtback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Jobs.this.finish();
            }
        });
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (edtSearch.length() > 0)
                {
                    edtSearch.setText("");
                }
            }
        });
        jobs();
        handlerJob = new Handler()
        {
            public void handleMessage(Message msg)
            {

                switch (msg.getData().getInt("status"))
                {
                    case 1:
                        adapter = new JobAdapter(Jobs.this, arraylist);
                        list.setAdapter(adapter);
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
        jobname = new String[]{"Job 1", "Job 2", "Job 3","Job 4"};
        jobdate= new String[]{"20/11/14", "20/11/15", "20/11/16","20/11/14"};
        jobitem=new String[]{"3 items, 3 stages", "2 items, 2 stages", "1 items, 1 stages","3 items, 3 stages"};
        edtSearch=(EditText)findViewById(R.id.edtSearch);
        list = (ListView) findViewById(R.id.listView);
        arraylist.clear();
//        for (int i = 0; i < jobname.length; i++)
//        {
//            JobList jobdeatils=new JobList(jobname[i],jobitem[i],jobdate[i]);
//            // Binds all strings into an array
//            arraylist.add(jobdeatils);
//        }
        // Pass results to ListViewAdapter Class


        // Locate the EditText in listview_main.xml
        // Capture Text in EditText
        edtSearch.addTextChangedListener(new TextWatcher()
        {


            public void afterTextChanged(Editable arg0)
            {
                // TODO Auto-generated method stub
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
                if(arraylist.size()==0)
                {
                    list.setVisibility(View.INVISIBLE);
                    txtNoJobsFound.setVisibility(View.VISIBLE);
                }
                else
                {
                    list.setVisibility(View.VISIBLE);
                    txtNoJobsFound.setVisibility(View.INVISIBLE);
                }
                if (edtSearch.length() > 0)
                {
                    txtSearch.setBackgroundResource(R.drawable.edit_delete);
                }
                else
                {
                    txtSearch.setBackgroundResource(R.drawable.search_black);
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
    //Service for jobs
    public void jobs()
    {

        if(Utility.hasConnection(Jobs.this))
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
            final String strJobs="{\"Jobs\": [\n" +
                    " {\"jobid\":\"1\",\"jobname\":\"job1\",\"jobitems\":\"3 items,3 stages\",\"date\":\"06/30/2016\"}, \n" +
                    " {\"jobid\":\"2\",\"jobname\":\"job2\",\"jobitems\":\"1 items,1 stages\",\"date\":\"07/30/2016\"}\n" +
                    " ]\n" +
                    "}";
           // Toast.makeText(getBaseContext(),"file read"+temp.toString(),Toast.LENGTH_SHORT).show();
            Log.i("File","File"+strJobs.toString());
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jobj = new JSONObject(strJobs);
                        Log.i("JsonResponse",""+jobj.toString());
                        JSONArray array=jobj.getJSONArray("Jobs");
                        Log.i("Jsonarray",""+array.length());
                        for(int i=0;i<array.length();i++)
                        {
                            JSONObject obj=array.getJSONObject(i);
                            Log.i("Object",""+obj.toString());
                            JobList list=new JobList();
                            list.setJobname(obj.getString("jobname"));
                            Log.i("Job Name",""+obj.getString("jobname"));
                            list.setJobDate(obj.getString("date"));
                            list.setJobitem(obj.getString("jobitems"));
                            arraylist.add(list);
                        }
                        Message msgobj;
                        msgobj = handlerJob.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt("status", 1);
                        msgobj.setData(bundle);
                        handlerJob.sendMessage(msgobj);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }).start();
        }
    }
//    void OpenFileDialog(String file) {
//
//        //Read file in Internal Storage
//        FileInputStream fis;
//        String content = "";
//        try {
//            fis = openFileInput(file);
//            byte[] input = new byte[fis.available()];
//            while (fis.read(input) != -1) {
//            }
//            content += new String(input);
//            Log.i("json Format",""+content);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

