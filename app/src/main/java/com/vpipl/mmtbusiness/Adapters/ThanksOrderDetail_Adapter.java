package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.ThanksScreenOnline_Activity;
import com.vpipl.mmtbusiness.Utils.AppUtils;


public class ThanksOrderDetail_Adapter extends BaseAdapter {
    String TAG = "ThanksOrderDetail_Adapter";
    private Context context;
    private LayoutInflater inflater = null;

    public ThanksOrderDetail_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ThanksScreenOnline_Activity.orderDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return ThanksScreenOnline_Activity.orderDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.thanks_orderdetail_adapter, parent, false);
                holder = new Holder();
                holder.layout_normalProduct = convertView.findViewById(R.id.layout_normalProduct);

                holder.imageView_product = convertView.findViewById(R.id.imageView_product);
                holder.txt_productName = convertView.findViewById(R.id.txt_productName);
                holder.txt_productQty = convertView.findViewById(R.id.txt_productQty);
                holder.txt_productTotalPrice = convertView.findViewById(R.id.txt_productAmount);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

//            if(ThanksScreenOnline_Activity.orderDetailsList.get(position).get("IsKit").equals("N"))
//            {
            holder.layout_normalProduct.setVisibility(View.VISIBLE);

            holder.txt_productName.setText("" + ThanksScreenOnline_Activity.orderDetailsList.get(position).get("ProductName"));
            holder.txt_productQty.setText("Qty : " + ThanksScreenOnline_Activity.orderDetailsList.get(position).get("Quantity"));
            holder.txt_productTotalPrice.setText(Html.fromHtml("Total Amount : &#8377 " + ThanksScreenOnline_Activity.orderDetailsList.get(position).get("NetAmount")));

            AppUtils.loadProductImage(context, ThanksScreenOnline_Activity.orderDetailsList.get(position).get("ImageUrl"), holder.imageView_product);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView imageView_product;
        TextView txt_productName, txt_productQty, txt_productTotalPrice;

        LinearLayout layout_normalProduct;
    }
}