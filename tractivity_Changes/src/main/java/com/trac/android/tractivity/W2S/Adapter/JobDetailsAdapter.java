package com.trac.android.tractivity.W2S.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.W2S.Model.JobDetailList;
import com.trac.android.tractivity.W2S.JobDetailedViewPage;
import com.trac.android.tractivity.W2S.Model.JobList;
import com.trac.android.tractivity.W2S.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobDetailsAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater inflater;
    private List<JobDetailList> joblist = null;
    private ArrayList<JobDetailList> arraylist;
    boolean[] checkBoxState;
    public JobDetailsAdapter(Context context, List<JobDetailList> joblist)
    {
        mContext = context;
        this.joblist = joblist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<JobDetailList>();
        checkBoxState=new boolean[joblist.size()];
        this.arraylist.addAll(joblist);
    }
    public class ViewHolder
    {
        TextView txtJobid,txtJobNumber,txtJobDescription,txtQuality,txtArea,txtStatus,txtInfo;
        CheckBox chckBox;

    }
    @Override
    public int getCount()
    {
        return joblist.size();
    }

    @Override
    public JobDetailList getItem(int position)
    {
        return joblist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        JobDetailList items = joblist.get(pos);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.jobdetails, null);
            // Locate the TextViews in listview_item.xml
            holder.txtJobid = (TextView) view.findViewById(R.id.txtJobId);
            holder.txtJobNumber = (TextView) view.findViewById(R.id.txtjobNumber);
            holder.txtJobDescription = (TextView) view.findViewById(R.id.txtDescription);
            holder.txtQuality = (TextView) view.findViewById(R.id.txtQtyNumber);
            holder.txtArea = (TextView) view.findViewById(R.id.txtArea);
            holder.txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            holder.txtInfo=(TextView)view.findViewById(R.id.txtInfo);
            holder.chckBox=(CheckBox)view.findViewById(R.id.chckBox);
            holder.chckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {

                    if(buttonView.isChecked())
                    {
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        joblist.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                        Log.i("Jobid",""+joblist.get(getPosition).getJobid());
                        Utility.arrchecked.add(joblist.get(getPosition));
                    }
                    else
                    {
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        joblist.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                        Log.i("Jobid",""+joblist.get(getPosition).getJobid());
                    }
                }
            });

            view.setTag(holder);
            view.setTag(R.id.chckBox, holder.chckBox);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.chckBox.setTag(position);
        // Set the results into TextViews
        holder.txtJobid.setText(joblist.get(position).getJobid());
        holder.txtJobNumber.setText(joblist.get(position).getJobnumber());
        holder.txtJobDescription.setText(joblist.get(position).getJobdescription());
        holder.txtQuality.setText("Qty "+joblist.get(position).getJobqulity());
        holder.txtArea.setText(joblist.get(position).getJobarea());
        holder.txtStatus.setText(joblist.get(position).getJobstatus());
        holder.chckBox.setChecked(joblist.get(position).isSelected());
        holder.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, JobDetailedViewPage.class);
                mContext.startActivity(intent);
            }
        });
        return view;
    }
    // Filter Class
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        joblist.clear();
        if (charText.length() == 0)
        {
            joblist.addAll(arraylist);
        }
        else
        {
            for (JobDetailList wp : arraylist)
            {
                if (wp.getJobid().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    joblist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
