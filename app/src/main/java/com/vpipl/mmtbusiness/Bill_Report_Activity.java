/*
 * Copyright 2019 MMT Business. All rights reserved.
 */

package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Bill_Report_Activity extends AppCompatActivity {

    public ArrayList<HashMap<String, String>> FranchiseeList = new ArrayList<>();

    String TAG = "Bill_Report_Activity";

    TextView txt_fromdate, txt_todate;

    TextInputEditText txt_franchisee_code;

    LinearLayout ll_date;
    CheckBox cb_date;
    Button btn_proceed;
    TableLayout displayLinear;

    RadioGroup rg_status;
    RadioButton rb_approved, rb_rejected, rb_all;

    int TopRows = 25;
    String PackageArray[];

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                    txt_fromdate.setText(sdf.format(myCalendar.getTime()));
                    txt_todate.setText(sdf.format(myCalendar.getTime()));
                } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_todate.setText(sdf.format(myCalendar.getTime()));
            } else {
                AppUtils.alertDialog(Bill_Report_Activity.this, "Selected Date Can't be After today");
            }
        }
    };
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
                    startActivity(new Intent(Bill_Report_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Bill_Report_Activity.this);
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
        setContentView(R.layout.activity_bill_report);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_fromdate = (TextView) findViewById(R.id.txt_fromdate);
            txt_todate = (TextView) findViewById(R.id.txt_todate);
            txt_franchisee_code = (TextInputEditText) findViewById(R.id.txt_franchisee_code);
            ll_date = (LinearLayout) findViewById(R.id.ll_date);
            cb_date = (CheckBox) findViewById(R.id.cb_date);
            btn_proceed = (Button) findViewById(R.id.btn_proceed);
//            btn_load_more = (Button) findViewById(R.id.btn_load_more);
//            btn_load_more.setVisibility(View.GONE);
            displayLinear = (TableLayout) findViewById(R.id.displayLinear);
            rg_status = (RadioGroup) findViewById(R.id.rg_status);
            rb_approved = (RadioButton) findViewById(R.id.rb_approved);
            rb_rejected = (RadioButton) findViewById(R.id.rb_rejected);
            rb_all = (RadioButton) findViewById(R.id.rb_all);

            cb_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_date.setVisibility(View.VISIBLE);
                    } else {
                        ll_date.setVisibility(View.GONE);
                        txt_fromdate.setText("");
                        txt_fromdate.setText("");
                    }
                }
            });

            txt_franchisee_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FranchiseeList.size() != 0) {
                        showFranchiseeDialog();
                        txt_franchisee_code.clearFocus();
                    } else {
                        executeFranchiseeListRequest();
                    }
                }
            });


            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 25;
                    createBillReportRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy MMMM dd");

            txt_fromdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_joining";
                    new DatePickerDialog(Bill_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_todate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_joining";
                    new DatePickerDialog(Bill_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {

                executeFranchiseeListRequest();

            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executeFranchiseeListRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(Bill_Report_Activity.this, postParameters, QueryUtils.methodSponsorPageFillPackage, TAG);
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getPackageResult(jsonArrayData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPackageResult(JSONArray jsonArray) {
        try {
            FranchiseeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("KitID", jsonObject.getString("KitID"));
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("KitName")));

                FranchiseeList.add(map);
            }

            createBillReportRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFranchiseeDialog() {
        try {
            PackageArray = new String[FranchiseeList.size()];
            for (int i = 0; i < FranchiseeList.size(); i++) {
                PackageArray[i] = FranchiseeList.get(i).get("KitName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Franchisee ");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_franchisee_code.setText(PackageArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Report_Activity.this);
        }
    }

    private void createBillReportRequest() {

//        findViewById(R.id.ll_showData).setVisibility(View.GONE);
//        findViewById(R.id.HSV).setVisibility(View.GONE);

        int selectedId = rg_status.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String view_detail_for = radioButton.getText().toString();

        String status = "0";

        if (view_detail_for.equalsIgnoreCase("All"))
            status = "ALL";
        else if (view_detail_for.equalsIgnoreCase("Approved"))
            status = "Active";
        else if (view_detail_for.equalsIgnoreCase("Rejected"))
            status = "Deactive";

        String FranchiseeName = txt_franchisee_code.getText().toString();

        String FranchiseeCode = "0";

        for (int i = 0; i < FranchiseeList.size(); i++) {
            if (FranchiseeName.equals(FranchiseeList.get(i).get("KitName"))) {
                FranchiseeCode = FranchiseeList.get(i).get("KitID");
            }
        }

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

        executeMemberDownlineListRequest(postParameters);
    }

    private void executeMemberDownlineListRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Bill_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Bill_Report_Activity.this, postparameters, QueryUtils.methodPurchaseBillDetailForMember, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Bill_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Report_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArrayDownLineDetail) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            if (jsonArrayDownLineDetail.length() > 0) {
//                text_pg_number.setText("" + PageIndex);
//
//                if (PageIndex <= 1)
//                    button_load_less.setVisibility(View.GONE);
//                else
//                    button_load_less.setVisibility(View.VISIBLE);

                TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
                ll.removeAllViews();

                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.parseColor("#038CB2"));

                TextView A1 = new TextView(this);
//                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
//                TextView F1 = new TextView(this);
                TextView G1 = new TextView(this);

                A1.setText("Request No");
//                B1.setText("Bill No");
                C1.setText("Request Date");
                D1.setText("Franchisee Name");
                E1.setText("Amount");
//                F1.setText("Approve Date");
                G1.setText("Status");

                A1.setPadding(px, px, px, px);
//                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
//                F1.setPadding(px, px, px_right, px);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
//                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);
                E1.setGravity(Gravity.CENTER);
//                F1.setGravity(Gravity.CENTER);
                G1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
//                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);
                E1.setTextColor(Color.WHITE);
//                F1.setTextColor(Color.BLACK);
                G1.setTextColor(Color.WHITE);

                row1.addView(A1);
//                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(G1);
//                row1.addView(F1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#999999"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String RequestNo = jobject.getString("AID");
                        String CustomerName = WordUtils.capitalizeFully(jobject.getString("PartyName"));
                        String BillDate = AppUtils.getDateFromAPIDate(jobject.getString("BillDate"));
                        String BillAmount = jobject.getString("Amount");

//                      String BillNo = jobject.getString("BillNo");
//                      String Commission = jobject.getString("IsApproveDate");

//                        if (Commission.equalsIgnoreCase("null"))
//                            AppUtils.getDateFromAPIDate(jobject.getString("IsApproveDate"));
//                        else
//                            Commission = "NA";

                        String Status = jobject.getString("Status");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0,px,0,px);
                        row.setLayoutParams(lp);
                        row.setPadding(0,px,0,px);

                        if (i % 2 == 0)
                            row.setBackgroundColor(Color.parseColor("#eeeeee"));
                        else
                            row.setBackgroundColor(Color.WHITE);

                        TextView A = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView G = new TextView(this);
//                        TextView F = new TextView(this);
//                        TextView B = new TextView(this);

                        A.setText(RequestNo);
                        C.setText(BillDate);
                        D.setText(CustomerName);
                        E.setText(BillAmount);
//                      F.setText(Commission);
//                      B.setText(BillNo);

                        G.setText(Status);
                        G.setClickable(false);
                        G.setMinHeight(0);

//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, px, 0, px);
//                        G.setLayoutParams(params);

                        G.setTextColor(Color.WHITE);

                        if (G.getText().toString().equalsIgnoreCase("Approved"))
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_orange));
//                            G.setBackgroundColor(Color.parseColor("#68BA56"));
                        else if (G.getText().toString().equalsIgnoreCase("Pending"))
//                            G.setBackgroundColor(Color.parseColor("#FFAC4A"));
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_yellow));
                        else
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_red));

//                            G.setBackgroundColor(Color.parseColor("#EE5252"));

                        A.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);
                        E.setGravity(Gravity.CENTER);
                        G.setGravity(Gravity.CENTER);
//                        B.setGravity(Gravity.CENTER);
//                        F.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        G.setPadding((px), (px), (px), (px));
//                        B.setPadding(px, px, px, px);
//                        F.setPadding(px, px, px, px);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(G);
//                        row.addView(B);
//                        row.addView(F);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view_one.setBackgroundColor(Color.parseColor("#999999"));

                        ll.addView(row);
                        ll.addView(view_one);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            AppUtils.showExceptionDialog(Bill_Report_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}