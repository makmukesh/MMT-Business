package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Pin_details_for_transfer_Activity extends AppCompatActivity {

    private static final String TAG = "Pin_details_for_transfer_Activity";

    TableLayout displayLinear;
    String BillNo;

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

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Pin_details_for_transfer_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Pin_details_for_transfer_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_details_for_bill);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        BillNo = getIntent().getStringExtra("BillNo");


        displayLinear = (TableLayout) findViewById(R.id.displayLinear);

        if (AppUtils.isNetworkAvailable(this)) {
            executePinTransferDetails();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppUtils.isNetworkAvailable(this)) {
            executePinTransferDetails();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    private void executePinTransferDetails() {
        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        try {
            if (AppUtils.isNetworkAvailable(Pin_details_for_transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_details_for_transfer_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String type = "S";


                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("BillNo", "" + BillNo));
                            response = AppUtils.callWebServiceWithMultiParam(Pin_details_for_transfer_Activity.this, postParameters, QueryUtils.PinTransferDetails, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            if (!TextUtils.isEmpty(resultData)) {
                                JSONObject jsonObject = new JSONObject(resultData);
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                        WriteValues(jsonArrayData);
                                    } else {
                                        AppUtils.alertDialog(Pin_details_for_transfer_Activity.this, jsonObject.getString("Message"));
                                    }
                                } else {
                                    AppUtils.alertDialog(Pin_details_for_transfer_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Pin_details_for_transfer_Activity.this, "No Data Found");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_details_for_transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_details_for_transfer_Activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_end = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
        
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        ll.removeAllViews();

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);

        TextView E1 = new TextView(this);
        TextView F1 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);
        TextView I1 = new TextView(this);
        TextView J1 = new TextView(this);
        TextView K1 = new TextView(this);


        A1.setText("S.No");
        B1.setText("Transfer From ID");
        C1.setText("Transfer From Member");
        E1.setText("Transfer To ID");
        F1.setText("Transfer To Member");
        G1.setText("Package");
        H1.setText("Pin No");
        I1.setText("Scratch No");
        J1.setText("Date");
        K1.setText("Status");


        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);
        I1.setPadding(px, px, px, px);
        J1.setPadding(px, px, px, px);
        K1.setPadding(px, px, px, px);

 A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);
        I1.setTypeface(typeface);
        J1.setTypeface(typeface);
        K1.setTypeface(typeface);


        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        K1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);
        J1.setTextColor(Color.WHITE);
        K1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);
        row1.addView(K1);


        ll.addView(row1);


        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                A1.setText("S.No");
                B1.setText("Transfer From ID");
                C1.setText("Transfer From Member");
                E1.setText("Transfer To ID");
                F1.setText("Transfer To Member");
                G1.setText("Package");
                H1.setText("Pin No");
                I1.setText("Scratch No");
                J1.setText("Date");
                K1.setText("Status");

                String SNo = jobject.getString("SNo");
                String FromIdNo = (jobject.getString("FromIdNo"));
                String FromMemName = jobject.getString("FromMemName");
                String ToIdNo = jobject.getString("ToIdNo");
                String ToMemName = WordUtils.capitalizeFully(jobject.getString("ToMemName"));
                String KitName = jobject.getString("KitName");
                String PinNo = jobject.getString("PinNo");
                String ScratchNo = (jobject.getString("ScratchNo"));
                String TDate = (jobject.getString("TDate"));
                String PinStatus = (jobject.getString("PinStatus"));

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                  if (i % 2 == 0)                    row.setBackgroundColor(getResources().getColor(R.color.table_row_one));                else                    row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);

                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);
                TextView I = new TextView(this);

                TextView J = new TextView(this);
                TextView K = new TextView(this);

                A.setText(SNo);
                B.setText(FromIdNo);
                C.setText(FromMemName);
                E.setText(ToIdNo);
                F.setText(ToMemName);
                G.setText(KitName);
                H.setText(PinNo);
                I.setText(ScratchNo);
                J.setText(AppUtils.getDateFromAPIDate(TDate));
                K.setText(PinStatus);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);
                J.setGravity(Gravity.CENTER);
                K.setGravity(Gravity.CENTER);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);
                I.setPadding(px, px, px, px);
                J.setPadding(px, px, px, px);
                K.setPadding(px, px, px, px);

               A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                E.setTextColor(Color.BLACK);
                F.setTextColor(Color.BLACK);
                G.setTextColor(Color.BLACK);
                H.setTextColor(Color.BLACK);
                I.setTextColor(Color.BLACK);
                J.setTextColor(Color.BLACK);
                K.setTextColor(Color.BLACK);

                  A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);
                I.setTypeface(typeface);
                J.setTypeface(typeface);
                K.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
                row.addView(K);



                ll.addView(row);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_details_for_transfer_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_details_for_transfer_Activity.this);
        }
    }
}
