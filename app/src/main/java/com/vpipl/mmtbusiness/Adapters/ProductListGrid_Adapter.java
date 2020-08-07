package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.model.ProductsList;

import java.util.List;

import jp.shts.android.library.TriangleLabelView;

public class ProductListGrid_Adapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;

    List<ProductsList> productList;
    String comesFrom = "";
    protected RequestManager glideManager;

    public ProductListGrid_Adapter(Context context, List<ProductsList> productList, String comesFrom) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productList = productList;
        this.comesFrom = comesFrom;
        glideManager = Glide.with(context);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
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

                convertView = inflater.inflate(R.layout.productgrid_adapter, parent, false);
                holder = new Holder();
                holder.txt_productName = (TextView) convertView.findViewById(R.id.txt_productName);
                holder.txt_productAmount = (TextView) convertView.findViewById(R.id.txt_productAmount);
                holder.txt_productBV = (TextView) convertView.findViewById(R.id.txt_productBV);
                holder.txt_productNewLabel = (TriangleLabelView) convertView.findViewById(R.id.txt_productNewLabel);
                holder.imageView_product = (ImageView) convertView.findViewById(R.id.imageView_product);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            StringBuilder sb = new StringBuilder(productList.get(position).getName().trim());
            int ii = 0;
            while ((ii = sb.indexOf(" ", ii + 20)) != -1) {
                sb.replace(ii, ii + 1, "\n");
            }

            holder.txt_productName.setText(sb.toString());

            holder.txt_productBV.setText("");
            holder.txt_productBV.setVisibility(View.GONE);

            String NewDP = "₹ " + productList.get(position).getNewDP() + "/-";
            String NewMRP = productList.get(position).getNewMRP();
            String DiscountPer = productList.get(position).getDiscountPer() + "% off";
            Spannable spanString = null;

            if (DiscountPer.equalsIgnoreCase("0% off")) {
                spanString = new SpannableString("" + NewDP);
                spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, NewDP.length(), 0);

            } else {
              spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);
//                spanString = new SpannableString("" + NewDP + "  " + NewMRP);

                spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                spanString.setSpan(boldSpan, 0, NewDP.length(), 0);

                spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(Color.GRAY), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
            }
            holder.txt_productAmount.setText(spanString);

            glideManager
                    .load(productList.get(position).getImagePath())
                    .placeholder(R.drawable.ic_no_image)
                    .skipMemoryCache(false)
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView_product);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return convertView;
    }

    private static class Holder {
        TextView txt_productName, txt_productAmount, txt_productBV;
        TriangleLabelView txt_productNewLabel;
        ImageView imageView_product;
    }
}