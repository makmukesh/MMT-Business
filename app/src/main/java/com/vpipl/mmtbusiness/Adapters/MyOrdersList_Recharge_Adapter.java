package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vpipl.mmtbusiness.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersList_Recharge_Adapter extends RecyclerView.Adapter<MyOrdersList_Recharge_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> ordersList;
    LayoutInflater inflater = null;
    String TAG = "MyOrdersList_Recharge_Adapter";
    private Context context;

    public MyOrdersList_Recharge_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myorders_recharge_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {

            holder.txt_OrderNo.setText(ordersList.get(position).get("OrderNo"));
            holder.txt_OrderAmt.setText("₹ " + ordersList.get(position).get("OrderAmt"));
            holder.txt_operator.setText("Recharge of " + WordUtils.capitalizeFully(ordersList.get(position).get("operator")));
            holder.txt_OrderStatus.setText(WordUtils.capitalizeFully(ordersList.get(position).get("OrderStatus")));
            holder.txt_datetime.setText(WordUtils.capitalizeFully(ordersList.get(position).get("datetime")));
            holder.txt_mobile.setText(WordUtils.capitalizeFully(ordersList.get(position).get("mobile")));

            if (holder.txt_OrderStatus.getText().toString().toLowerCase().contains("failed"))
                holder.txt_OrderStatus.setTextColor(Color.parseColor("#CC3000"));
            else if (holder.txt_OrderStatus.getText().toString().toLowerCase().contains("success"))
                holder.txt_OrderStatus.setTextColor(Color.parseColor("#64B760"));
            else
                holder.txt_OrderStatus.setTextColor(Color.parseColor("#FF760B"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_OrderNo, txt_operator, txt_mobile, txt_OrderAmt, txt_OrderStatus, txt_datetime;

        public MyViewHolder(View view) {
            super(view);
            txt_OrderNo = (TextView) view.findViewById(R.id.txt_OrderNo);
            txt_OrderAmt = (TextView) view.findViewById(R.id.txt_OrderAmt);
            txt_operator = (TextView) view.findViewById(R.id.txt_operator);
            txt_OrderStatus = (TextView) view.findViewById(R.id.txt_OrderStatus);
            txt_mobile = (TextView) view.findViewById(R.id.txt_mobile);
            txt_datetime = (TextView) view.findViewById(R.id.txt_datetime);
        }
    }
}