package com.trac.android.tractivity.W2S;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;

public class JobDetailedViewPage extends Activity {


    TextView txtback;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detailed_view_page);
        txtback=(TextView)findViewById(R.id.txtbck);
        txtback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                JobDetailedViewPage.this.finish();
            }
        });
    }
}
