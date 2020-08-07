package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class HorizentalSizeListView_Adapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;

    ArrayList<HashMap<String, String>> sizeList;

    public HorizentalSizeListView_Adapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sizeList = list;
    }

    @Override
    public int getCount() {
        return sizeList.size();
    }

    @Override
    public Object getItem(int position) {
        return sizeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.size_textview_layout, parent, false);
                holder = new Holder();
                holder.txt_size = (TextView) convertView.findViewById(R.id.txt_size);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_size.setText(sizeList.get(position).get("SizeName"));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_size;
    }
}
