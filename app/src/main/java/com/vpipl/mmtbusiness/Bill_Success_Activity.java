package com.vpipl.mmtbusiness;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Bill_Success_Activity extends AppCompatActivity {

    private static final String TAG = "Bill_Success_Activity";

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_login_logout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_success);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        TextView txt_date_time = findViewById(R.id.txt_date_time);
        TextView txt_order_id = findViewById(R.id.txt_order_id);
        TextView txt_mobile_no = findViewById(R.id.txt_mobile_no);
        TextView txt_amount = findViewById(R.id.txt_amount);


        SimpleDateFormat sdf = new SimpleDateFormat("hh:MM aaa, dd MMMM yyyy");
        Calendar c = Calendar.getInstance();
        txt_date_time.setText(sdf.format(c.getTime()));

        txt_order_id.setText("Order ID : "+ getIntent().getExtras().getString("OrderID"));
        txt_mobile_no.setText(""+ getIntent().getExtras().getString("operator")+" : "+ getIntent().getExtras().getString("MobileNo"));
        txt_amount.setText("\u20B9 "+ getIntent().getExtras().getString("amount"));

    }
}