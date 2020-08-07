package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class HorizentalColorListViewFilter_Adapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;

    ArrayList<HashMap<String, String>> colorList;

    public HorizentalColorListViewFilter_Adapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colorList = list;
    }

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return colorList.get(position);
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
                convertView = inflater.inflate(R.layout.color_imageview_layout, parent, false);
                holder = new Holder();
                holder.img_color = (ImageView) convertView.findViewById(R.id.img_color);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.img_color.setBackgroundColor(Color.parseColor(colorList.get(position).get("ColorCode")));
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView img_color;
    }
}