package com.vpipl.mmtbusiness;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Wallet_Request_Status_Report_Activity extends AppCompatActivity {


    String TAG = "Wallet_Request_Status_Report_Activity";
    TextView txt_from_date, txt_to_date;
    Button btn_proceed, btn_load_more;
    TableLayout displayLinear;

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

                if (whichdate.equalsIgnoreCase("txt_from_date"))
                    txt_from_date.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_date"))
                    txt_to_date.setText(sdf.format(myCalendar.getTime()));

            } else {

                AppUtils.alertDialog(Wallet_Request_Status_Report_Activity.this, "Selected Date Can't be After today");
            }
        }
    };
    int TopRows = 25;
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
                    startActivity(new Intent(Wallet_Request_Status_Report_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Wallet_Request_Status_Report_Activity.this);
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
        setContentView(com.vpipl.mmtbusiness.R.layout.activity_wallet_request_status_report);

        try {
            Toolbar toolbar = (Toolbar) findViewById(com.vpipl.mmtbusiness.R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            SetupToolbar();

            txt_from_date = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_from_date);
            txt_to_date = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_to_date);

            btn_proceed = (Button) findViewById(com.vpipl.mmtbusiness.R.id.btn_proceed);
            btn_load_more = (Button) findViewById(com.vpipl.mmtbusiness.R.id.btn_load_more);

            displayLinear = (TableLayout) findViewById(com.vpipl.mmtbusiness.R.id.displayLinear);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TopRows = TopRows + 25;
                    createWalletRequestStatusRequest();

                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 25;
                    createWalletRequestStatusRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy MMMM dd");

            txt_from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_from_date";
                    new DatePickerDialog(Wallet_Request_Status_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


            txt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_date";
                    new DatePickerDialog(Wallet_Request_Status_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                createWalletRequestStatusRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(com.vpipl.mmtbusiness.R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Request_Status_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Status_Report_Activity.this,
                                    postParameters, QueryUtils.methodToGetAvailablePayoutWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    String count_text = "Wallet Balance " + jsonArrayData.getJSONObject(0).getString("WBalance");
//                                    txt_available_wb.setText(count_text);
//                                    txt_available_wb.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWalletRequestStatusRequest() {

        findViewById(com.vpipl.mmtbusiness.R.id.HSV).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_date.getText().toString()));

        executeWalletRequestTatusReportRequest(postParameters);
    }

    private void executeWalletRequestTatusReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Request_Status_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Request_Status_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Status_Report_Activity.this,
                                    postparameters, QueryUtils.methodToGetWallet_Request_Status_Report, TAG);
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
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    WriteValues(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Wallet_Request_Status_Report_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Wallet_Request_Status_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Request_Status_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Status_Report_Activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(com.vpipl.mmtbusiness.R.id.HSV).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (10 * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = (TableLayout) findViewById(com.vpipl.mmtbusiness.R.id.displayLinear);
        ll.removeAllViews();

        TableRow row1 = new TableRow(this);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
        
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(Color.WHITE);

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);
        TextView E1 = new TextView(this);
        TextView F1 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);

        A1.setText("Requested\nAmount");
        B1.setText("Bank Name");
        C1.setText("Transaction No");
        D1.setText("Transaction Date");
        E1.setText("Request Date/Time");
        F1.setText("Requested From IP");
        G1.setText("Request Status");
        H1.setText("Response Remarks");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);

          A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER_VERTICAL);
        B1.setGravity(Gravity.CENTER_VERTICAL);
        C1.setGravity(Gravity.CENTER_VERTICAL);
        D1.setGravity(Gravity.CENTER_VERTICAL);
        E1.setGravity(Gravity.CENTER_VERTICAL);
        F1.setGravity(Gravity.CENTER_VERTICAL);
        G1.setGravity(Gravity.CENTER_VERTICAL);
        H1.setGravity(Gravity.CENTER_VERTICAL);

        A1.setTextColor(Color.BLACK);
        B1.setTextColor(Color.BLACK);
        C1.setTextColor(Color.BLACK);
        D1.setTextColor(Color.BLACK);
        E1.setTextColor(Color.BLACK);
        F1.setTextColor(Color.BLACK);
        G1.setTextColor(Color.BLACK);
        H1.setTextColor(Color.BLACK);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(getResources().getColor(com.vpipl.mmtbusiness.R.color.app_color_green_one));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String Requested_Amount = jobject.getString("RequestAmt");
                String Bank_Name = WordUtils.capitalizeFully(jobject.getString("BankName"));
                String Transaction_No = jobject.getString("TransactionNo");
                String Transaction_Date = jobject.getString("DepositDate");
                String Request_Date_Time = jobject.getString("ReqRTS");
                String Requested_From_IP = jobject.getString("HostIP");
                String Request_Status = WordUtils.capitalizeFully(jobject.getString("ReqStatus"));
                String Response_Remarks = WordUtils.capitalizeFully(jobject.getString("ResponseRemarks"));

                StringBuilder sb = new StringBuilder(Bank_Name);

//                int ii = 0;
//                while ((ii = sb.indexOf(" ", ii + 8)) != -1) {
//                    sb.replace(ii, ii + 1, "\n");
//                }

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0) {
                    row.setBackgroundColor(getResources().getColor(com.vpipl.mmtbusiness.R.color.color_table_row));
                } else
                    row.setBackgroundColor(Color.WHITE);


                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);

                A.setText(Requested_Amount);
                B.setText(sb.toString());
                C.setText(Transaction_No);
                D.setText(AppUtils.getDateFromAPIDate(Transaction_Date));
                E.setText(AppUtils.getDateFromAPIDate(Request_Date_Time));
                F.setText(Requested_From_IP);
                G.setText(Request_Status);
                H.setText(Response_Remarks);

                A.setGravity(Gravity.CENTER_VERTICAL);
                B.setGravity(Gravity.CENTER_VERTICAL);
                C.setGravity(Gravity.CENTER_VERTICAL);
                D.setGravity(Gravity.CENTER_VERTICAL);
                E.setGravity(Gravity.CENTER_VERTICAL);
                F.setGravity(Gravity.CENTER_VERTICAL);
                G.setGravity(Gravity.CENTER_VERTICAL);
                H.setGravity(Gravity.CENTER_VERTICAL);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);

               A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(getResources().getColor(com.vpipl.mmtbusiness.R.color.app_color_green_one));

                ll.addView(row);
                if (i < jarray.length() - 1)
                    ll.addView(view_one);
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
            AppUtils.showExceptionDialog(Wallet_Request_Status_Report_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Status_Report_Activity.this);
        }
    }
}
