package com.vpipl.mmtbusiness.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Specifies the WelcomeScreen Item List functionality
 */
public class ProductDetailImageSliderViewPagerAdapter extends PagerAdapter {
    public static ArrayList<HashMap<String, String>> imageList;

    static String product_id = "";
    public Context context;
    public LayoutInflater inflater;
    public FragmentTransaction ft;
    public static ImageZoomDialog frag;

    public ProductDetailImageSliderViewPagerAdapter(Context con, ArrayList<HashMap<String, String>> list, String Pid) {
        this.context = con;
        imageList = list;
        this.product_id = Pid;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView swipeImageView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.product_swipeimage_layout, container, false);

        swipeImageView = (ImageView) itemView.findViewById(R.id.swipeImageView);

        try {

            AppUtils.loadSlidingImage(context, imageList.get(position).get("image"), swipeImageView);


            container.addView(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        frag = new ImageZoomDialog();
                        frag.show(ft, "dialog");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

    public static class ImageZoomDialog extends DialogFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.ImageZoomDialog);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.imagezoomviewpager, container, false);

            final ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewPager);
            viewPager.setAdapter(new ImageZoomAdapter(getContext()));

            ImageButton leftNav = (ImageButton) root.findViewById(R.id.left_nav);
            ImageButton rightNav = (ImageButton) root.findViewById(R.id.right_nav);

            // Images left navigation
            leftNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    if (tab > 0) {
                        tab--;
                        viewPager.setCurrentItem(tab);
                    } else if (tab == 0) {
                        viewPager.setCurrentItem(tab);
                    }
                }
            });

            // Images right navigatin
            rightNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    tab++;
                    viewPager.setCurrentItem(tab);
                }
            });

            final ImageView btnClose = (ImageView) root.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (frag != null)
                            frag.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return root;
        }
    }

    public static class ImageZoomAdapter extends PagerAdapter {
        Context context;
        LayoutInflater inflater;

        public ImageZoomAdapter(Context con) {
            this.context = con;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final TouchImageView imgDisplay;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.imagezoomviewpagerlayout, container, false);
            imgDisplay = (TouchImageView) itemView.findViewById(R.id.imgDisplay);

            try {

                    AppUtils.loadSlidingImage(context, imageList.get(position).get("image"), imgDisplay);

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
}