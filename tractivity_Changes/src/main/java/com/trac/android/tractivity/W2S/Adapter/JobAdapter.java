package com.trac.android.tractivity.W2S.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.W2S.JobDetails;
import com.trac.android.tractivity.W2S.Model.JobList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobAdapter extends BaseAdapter
{
    Activity mContext;
    LayoutInflater inflater;
    private List<JobList> joblist = null;
    private ArrayList<JobList> arraylist;

    public JobAdapter(Activity  context, List<JobList> joblist)
    {
        mContext = context;
        this.joblist = joblist;
        this.arraylist = new ArrayList<JobList>();
        this.arraylist.addAll(joblist);
    }
    public class ViewHolder
    {
        TextView txtJobName;
        TextView txtDate;
        TextView txtItem;
    }
    @Override
    public int getCount()
    {
        return joblist.size();
    }

    @Override
    public JobList getItem(int position)
    {
        return joblist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.joblist, null);
            // Locate the TextViews in listview_item.xml
            holder.txtJobName = (TextView) view.findViewById(R.id.txtJobName);
            holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
            holder.txtItem = (TextView) view.findViewById(R.id.txtItem);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.txtJobName.setText(joblist.get(position).getJobname());
        holder.txtDate.setText(joblist.get(position).getJobDate());
        holder.txtItem.setText(joblist.get(position).getJobItem());

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(mContext, JobDetails.class);
                intent.putExtra("Job Name",(joblist.get(position).getJobname()));
                intent.putExtra("Job Date",(joblist.get(position).getJobDate()));
                intent.putExtra("Job Item",(joblist.get(position).getJobItem()));
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
            for (JobList wp : arraylist)
            {
                if (wp.getJobname().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    joblist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}
