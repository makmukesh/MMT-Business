package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class WalletTransactionList_Adapter extends RecyclerView.Adapter<WalletTransactionList_Adapter.MyViewHolder> {

    public ArrayList<HashMap<String, String>> ordersList;
    LayoutInflater inflater = null;
    String TAG = "MyOrdersList_Adapter";
    private Context context;

    private String olddate = "";


    public WalletTransactionList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.wallet_transaction_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {


            if (olddate.equalsIgnoreCase(ordersList.get(position).get("TransDate"))) {
                holder.LL_date_time.setVisibility(View.GONE);
            } else {
                holder.LL_date_time.setVisibility(View.VISIBLE);
            }

            holder.txt_date_time.setText(ordersList.get(position).get("TransDate"));
            holder.txt_trans_amt.setText("₹ " + ordersList.get(position).get("Amount"));
            holder.txt_trans_desc.setText(WordUtils.capitalizeFully(ordersList.get(position).get("Remarks")));

            olddate = ordersList.get(position).get("TransDate");

            if (holder.txt_trans_amt.getText().toString().contains("-")) {
                holder.txt_trans_type.setText("Amount Paid");
                holder.txt_trans_amt.setTextColor(Color.parseColor("#FFD92729"));
            } else {
                holder.txt_trans_type.setText("Amount Received");
                holder.txt_trans_amt.setTextColor(Color.parseColor("#FF52A834"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date_time, txt_trans_type, txt_trans_desc, txt_trans_amt;
        LinearLayout LL_date_time;

        public MyViewHolder(View view) {
            super(view);

            txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
            txt_trans_type = (TextView) view.findViewById(R.id.txt_trans_type);
            txt_trans_desc = (TextView) view.findViewById(R.id.txt_trans_desc);
            txt_trans_amt = (TextView) view.findViewById(R.id.txt_trans_amt);

            LL_date_time = (LinearLayout) view.findViewById(R.id.LL_date_time);
        }
    }
}