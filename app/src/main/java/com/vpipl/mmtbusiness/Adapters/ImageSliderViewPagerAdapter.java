package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.mmtbusiness.Home_New;
import com.vpipl.mmtbusiness.ProductDetail_Activity;
import com.vpipl.mmtbusiness.ProductExpand_Activity;
import com.vpipl.mmtbusiness.ProductListGrid_Activity;
import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;


/**
 * Specifies the WelcomeScreen Item List functionality
 */
public class ImageSliderViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public ImageSliderViewPagerAdapter(Context con) {
        this.context = con;
    }

    @Override
    public int getCount() {
        return Home_New.imageSlider.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView swipeImageView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.home_swipeimage_layout, container, false);
        swipeImageView = (ImageView) itemView.findViewById(R.id.swipeImageView);

        try {

            try {
                AppUtils.loadSlidingImage(context, Home_New.imageSlider.get(position).get("Images"), swipeImageView);
            } catch (Exception e) {
               e.printStackTrace();
            }

            swipeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        String App_Category = Home_New.imageSlider.get(position).get("App_Category");
                        String App_Level_1SubCategory = Home_New.imageSlider.get(position).get("App_Level_1SubCategory");
                        String App_Level_2SubCategory = Home_New.imageSlider.get(position).get("App_Level_2SubCategory");
                        String App_ProductID = Home_New.imageSlider.get(position).get("App_ProductID");

                        if (!App_ProductID.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(context, ProductDetail_Activity.class);
                            intent.putExtra("productID", "" + App_ProductID);
                            context.startActivity(intent);
                        } else if (!App_Level_2SubCategory.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(context, ProductListGrid_Activity.class);
                            intent.putExtra("Type", "D");
                            intent.putExtra("categoryID", "" + App_Level_2SubCategory);
                            context.startActivity(intent);
                        } else if (!App_Level_1SubCategory.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(context, ProductListGrid_Activity.class);
                            intent.putExtra("Type", "S");
                            intent.putExtra("categoryID", "" + App_Level_1SubCategory);
                            context.startActivity(intent);
                        } else if (!App_Category.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(context, ProductExpand_Activity.class);
                            intent.putExtra("HID", "1");
                            intent.putExtra("CID", "" + App_Category);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            container.addView(itemView);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}