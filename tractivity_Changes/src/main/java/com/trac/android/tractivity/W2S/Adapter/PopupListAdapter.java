package com.trac.android.tractivity.W2S.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trac.android.tractivity.V1.R;
import com.trac.android.tractivity.W2S.Model.popList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishna on 6/28/2016.
 */
public class PopupListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<popList> joblist = null;
    private ArrayList<popList> arraylist;

    public PopupListAdapter(Context context, List<popList> joblist) {
        mContext = context;
        this.joblist = joblist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<popList>();
        this.arraylist.addAll(joblist);
    }

    public class ViewHolder {
        TextView txtPopuplist;

    }

    @Override
    public int getCount() {
        return joblist.size();
    }

    @Override
    public popList getItem(int position) {
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
            view = inflater.inflate(R.layout.popuplist, null);
            // Locate the TextViews in listview_item.xml
            holder.txtPopuplist = (TextView) view.findViewById(R.id.textView6);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.txtPopuplist.setText(joblist.get(position).getPopuplist());


        return view;
    }
}