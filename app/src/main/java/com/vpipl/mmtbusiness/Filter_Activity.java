package com.vpipl.mmtbusiness;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.HorizentalSizeListViewFilter_Adapter;
import com.vpipl.mmtbusiness.Utils.CrystalRangeSeekbar;
import com.vpipl.mmtbusiness.Utils.HorizontalListView;
import com.vpipl.mmtbusiness.Utils.OnRangeSeekbarChangeListener;

public class Filter_Activity extends AppCompatActivity {

    Button btn_clearFilter, btn_apply;

    String COMESFROM = "";

    CrystalRangeSeekbar rangeSeekbar, rangeSeekbar2;
    TextView tvMin, tvMax, textMin2, textMax2;

    LinearLayout layout_listViewSize;
    TextView txt_selectSize;
    HorizontalListView listview_size;

    //for item Color
//    LinearLayout layout_listViewColor;
//    TextView txt_selectColor;

//    String selectedColorCode ="0",selectedColorId="";

//    HorizontalListView listview_color;

    String selectedSizeName = "", selectedSizeId = "";
//    ImageView selectedColorIV;


    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {

        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);        img_user = (ImageView) findViewById(R.id.img_login_logout);
//
        img_user.setVisibility(View.GONE);

        img_cart.setVisibility(View.GONE);
//
        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);
//
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                AppController.comesFromFilter = false;
                ProductListGrid_Activity.selectedminimumPrice = "0";
                ProductListGrid_Activity.selectedminimumDisc = "0";
                ProductListGrid_Activity.selectedmaximumPrice = "0";
                ProductListGrid_Activity.selectedmaximumDisc = "0";
//                ProductListGrid_Activity. selectedColorId = "0";
                ProductListGrid_Activity.selectedSizeId = "0";
                finish();
            }
        });
//
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();


        btn_clearFilter = (Button) findViewById(R.id.btn_clearFilter);
        btn_apply = (Button) findViewById(R.id.btn_apply);

        rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbar1);
        rangeSeekbar2 = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbar2);

        layout_listViewSize = (LinearLayout) findViewById(R.id.layout_listViewSize);
        txt_selectSize = (TextView) findViewById(R.id.txt_selectSize);
        listview_size = (HorizontalListView) findViewById(R.id.listview_size);

//        layout_listViewColor= (LinearLayout) findViewById(R.id.layout_listViewColor);
//        txt_selectColor= (TextView) findViewById(R.id.txt_selectColor);
//        listview_color= (HorizontalListView) findViewById(R.id.listview_color);
//
//        selectedColorIV = (ImageView) findViewById(R.id.selectedColorIV);

        // get min and max text view
        tvMin = (TextView) findViewById(R.id.textMin1);
        textMin2 = (TextView) findViewById(R.id.textMin2);

        tvMax = (TextView) findViewById(R.id.textMax1);
        textMax2 = (TextView) findViewById(R.id.textMax2);

        COMESFROM = getIntent().getStringExtra("COMESFROM");

        btn_clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.comesFromFilter = false;
                ProductListGrid_Activity.selectedminimumPrice = "0";
                ProductListGrid_Activity.selectedminimumDisc = "0";
                ProductListGrid_Activity.selectedmaximumPrice = "0";
                ProductListGrid_Activity.selectedmaximumDisc = "0";
//                ProductListGrid_Activity. selectedColorId = "0";
                ProductListGrid_Activity.selectedSizeId = "0";
                finish();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.comesFromFilter = true;
                finish();
            }
        });

        rangeSeekbar.setMaxValue(ProductListGrid_Activity.maximumPrice);
        rangeSeekbar.setMinValue(ProductListGrid_Activity.minimumPrice);

        rangeSeekbar2.setMinValue(ProductListGrid_Activity.minimumDisc);
        rangeSeekbar2.setMaxValue(ProductListGrid_Activity.maximumDisc);

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));

                ProductListGrid_Activity.selectedmaximumPrice = "" + maxValue;
                ProductListGrid_Activity.selectedminimumPrice = "" + minValue;
            }
        });

        rangeSeekbar2.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                textMin2.setText(String.valueOf(minValue));
                textMax2.setText(String.valueOf(maxValue));

                ProductListGrid_Activity.selectedmaximumDisc = "" + maxValue;
                ProductListGrid_Activity.selectedminimumDisc = "" + minValue;
            }
        });

//        if (ProductListGrid_Activity.colorList.size() > 0)
//        {
//            setColorListView();
//        }

        if (ProductListGrid_Activity.sizeList.size() > 0) {
            setSizeListView();
        }
    }

    private void setColorListView() {
//        try {
//            layout_listViewColor.setVisibility(View.VISIBLE);
//            listview_color.setAdapter(new HorizentalColorListViewFilter_Adapter(Filter_Activity.this,ProductListGrid_Activity.colorList));
//
//            listview_color.setOnItemClickListener(new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
//                {
//
//                    selectedColorCode =ProductListGrid_Activity.colorList.get(position).get("ColorCode");
//                    selectedColorId=ProductListGrid_Activity.colorList.get(position).get("ColorID");
//
//                    ProductListGrid_Activity.selectedColorId = selectedColorId;
//
//                    txt_selectColor.setText("Selected Color : ");
//                    txt_selectColor.setTextColor(getResources().getColor(R.color.color_green_text));
//
//                    findViewById(R.id.selectedColorLV).setVisibility(View.VISIBLE);
//                    selectedColorIV.setBackgroundColor(Color.parseColor(selectedColorCode));
//
//                    Log.e("COLORCODE",ProductListGrid_Activity.selectedColorId);
//                }
//            });

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void setSizeListView() {
        try {
            layout_listViewSize.setVisibility(View.VISIBLE);
            listview_size.setAdapter(new HorizentalSizeListViewFilter_Adapter(Filter_Activity.this, ProductListGrid_Activity.sizeList));
            listview_size.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    listview_size.setSelection(position);

                    selectedSizeName = ProductListGrid_Activity.sizeList.get(position).get("Size");
                    selectedSizeId = ProductListGrid_Activity.sizeList.get(position).get("SizeID");

                    ProductListGrid_Activity.selectedSizeId = selectedSizeId;

                    txt_selectSize.setText("Selected Size : " + ProductListGrid_Activity.sizeList.get(position).get("Size"));
                    txt_selectSize.setTextColor(getResources().getColor(R.color.color_green_text));

                    Log.e("Selected Size", ProductListGrid_Activity.selectedSizeId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AppController.comesFromFilter = false;
        ProductListGrid_Activity.selectedminimumPrice = "0";
        ProductListGrid_Activity.selectedminimumDisc = "0";
        ProductListGrid_Activity.selectedmaximumPrice = "0";
        ProductListGrid_Activity.selectedmaximumDisc = "0";
//                ProductListGrid_Activity. selectedColorId = "0";
        ProductListGrid_Activity.selectedSizeId = "0";
        finish();
    }
}