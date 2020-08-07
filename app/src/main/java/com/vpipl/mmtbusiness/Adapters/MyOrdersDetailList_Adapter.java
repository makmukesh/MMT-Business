package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersDetailList_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<HashMap<String, String>> ordersList;

    ArrayList<HashMap<String, String>> ordersHeaderandFooterlList = new ArrayList<>();

    LayoutInflater inflater = null;
    String TAG = "MyOrdersDetailList_Adapter";
    private Context context;

    private int TYPE_HEADER = 0;
    private int TYPE_ITEM = 1;
    private int TYPE_FOOTER = 2;

    public MyOrdersDetailList_Adapter(Context con, ArrayList<HashMap<String, String>> list, ArrayList<HashMap<String, String>> list2) {
        ordersList = list;
        ordersHeaderandFooterlList = list2;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new MyViewHolder(inflater.inflate(R.layout.myordersdetails_adapter, parent, false));

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorderdetailsheader_layout, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thankyoufooter_layout, parent, false);
            return new FooterViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myordersdetails_adapter, parent, false);
            return new MyViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        try {

            if (holder1 instanceof MyViewHolder) {
                position = (position - 1);
                MyViewHolder holder = (MyViewHolder) holder1;

                holder.txt_productName.setText(WordUtils.capitalizeFully(ordersList.get(position).get("ProductName")));
                holder.txt_productAmount.setText("Price : ₹ " + Html.fromHtml(ordersList.get(position).get("Netamount")));
                holder.txt_productQty.setText("Qty : " +Html.fromHtml(ordersList.get(position).get("Qty")));
                AppUtils.loadProductImage(context, ordersList.get(position).get("ImgPath"), holder.imageView_product);

            } else if (holder1 instanceof HeaderViewHolder) {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder1;

                headerHolder.txt_orderNo.setText(ordersHeaderandFooterlList.get(0).get("OrderNo"));
                headerHolder.txt_orderDate.setText((ordersHeaderandFooterlList.get(0).get("ODate")));
                headerHolder.txt_orderStatus.setText(ordersHeaderandFooterlList.get(0).get("OrderStatus"));
                headerHolder.txt_orderAmount.setText(ordersHeaderandFooterlList.get(0).get("TotalAmount"));
                headerHolder.txt_PayMode.setText(ordersHeaderandFooterlList.get(0).get("PayMode"));

            } else if (holder1 instanceof FooterViewHolder) {
                FooterViewHolder footerHolder = (FooterViewHolder) holder1;

                footerHolder.txt_name.setText(WordUtils.capitalizeFully(ordersHeaderandFooterlList.get(0).get("Name")));
                footerHolder.txt_address.setText(WordUtils.capitalizeFully(ordersHeaderandFooterlList.get(0).get("Address1") + ", " +
                        ordersHeaderandFooterlList.get(0).get("City") + ", " +
                        ordersHeaderandFooterlList.get(0).get("StateName") + ", " +
                        ordersHeaderandFooterlList.get(0).get("PinCode")));
                footerHolder.txt_mobNo.setText("MobileNo : " + WordUtils.capitalizeFully(ordersHeaderandFooterlList.get(0).get("Mobl")));
                footerHolder.txt_email.setText("Email : " + WordUtils.capitalizeFully(ordersHeaderandFooterlList.get(0).get("Email")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public int getItemCount() {
//        return ordersList.size();
//    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    private boolean isPositionFooter(int position) {
        return position == ordersList.size() + 1;
    }


    @Override
    public int getItemCount() {
        return ordersList.size() + 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_productName, txt_productQty, txt_productAmount;
        ImageView imageView_product;

        public MyViewHolder(View view) {
            super(view);

            txt_productName = (TextView) view.findViewById(R.id.txt_productName);
            txt_productQty = (TextView) view.findViewById(R.id.txt_productQty);
            txt_productAmount = (TextView) view.findViewById(R.id.txt_productAmount);
            imageView_product = (ImageView) view.findViewById(R.id.imageView_product);

        }
    }


    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name, txt_address, txt_mobNo, txt_email;

        public FooterViewHolder(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_address = (TextView) itemView.findViewById(R.id.txt_address);
            txt_mobNo = (TextView) itemView.findViewById(R.id.txt_mobNo);
            txt_email = (TextView) itemView.findViewById(R.id.txt_email);

        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txt_orderNo, txt_orderDate, txt_orderStatus, txt_orderAmount, txt_PayMode;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            txt_orderNo = (TextView) itemView.findViewById(R.id.txt_orderNo);
            txt_orderDate = (TextView) itemView.findViewById(R.id.txt_orderDate);
            txt_orderStatus = (TextView) itemView.findViewById(R.id.txt_orderStatus);
            txt_orderAmount = (TextView) itemView.findViewById(R.id.txt_orderAmount);
            txt_PayMode = (TextView) itemView.findViewById(R.id.txt_PayMode);
        }
    }
}