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

public class Wallet_Transaction_Report_Activity extends AppCompatActivity {


    String TAG = "Wallet_Transaction_Report_Activity";
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
                AppUtils.alertDialog(Wallet_Transaction_Report_Activity.this, "Selected Date Can't be After today");
            }
        }
    };
    TextView txt_WB;
    int TopRows = 25;
    TextView txt_heading ;
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
                    startActivity(new Intent(Wallet_Transaction_Report_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Wallet_Transaction_Report_Activity.this);
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
        setContentView(com.vpipl.mmtbusiness.R.layout.activity_wallet_transaction_report);

        try {
            Toolbar toolbar = (Toolbar) findViewById(com.vpipl.mmtbusiness.R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            SetupToolbar();

            txt_from_date = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_from_date);
            txt_to_date = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_to_date);

            txt_WB = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_WB);
            txt_heading = (TextView) findViewById(com.vpipl.mmtbusiness.R.id.txt_heading);

            btn_proceed = (Button) findViewById(com.vpipl.mmtbusiness.R.id.btn_proceed);
            btn_load_more = (Button) findViewById(com.vpipl.mmtbusiness.R.id.btn_load_more);

            displayLinear = (TableLayout) findViewById(com.vpipl.mmtbusiness.R.id.displayLinear);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TopRows = TopRows + 25;
                    createWalletTransactionReportRequest();

                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 25;

                    findViewById(com.vpipl.mmtbusiness.R.id.ll_showData).setVisibility(View.GONE);

                    createWalletTransactionReportRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMMM yyyy");

            txt_from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_from_date";
                    new DatePickerDialog(Wallet_Transaction_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


            txt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_date";
                    new DatePickerDialog(Wallet_Transaction_Report_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                findViewById(com.vpipl.mmtbusiness.R.id.ll_showData).setVisibility(View.GONE);

                createWalletTransactionReportRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(com.vpipl.mmtbusiness.R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }


    private void createWalletTransactionReportRequest() {


        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_date.getText().toString()));

        executeWalletTransactionReportRequest(postParameters);
    }

    private void executeWalletTransactionReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Transaction_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Transaction_Report_Activity.this,
                                    postparameters, QueryUtils.methodToGetWalletTransactionReport, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            executeWalletBalanceRequest();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    WriteValues(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Wallet_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Wallet_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Transaction_Report_Activity.this,
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
                                    String count_text = "(Available Wallet Balance " + jsonArrayData.getJSONObject(0).getString("WBalance") + " )";
                                    txt_WB.setText(count_text);
                                } else {
                                    AppUtils.alertDialog(Wallet_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Wallet_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        AppUtils.showProgressDialog(Wallet_Transaction_Report_Activity.this);

        findViewById(com.vpipl.mmtbusiness.R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

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

        A1.setText("Transaction Date");
        B1.setText("Amount");
        C1.setText("Remarks");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER_VERTICAL);
        B1.setGravity(Gravity.CENTER_VERTICAL);
        C1.setGravity(Gravity.CENTER_VERTICAL);

        A1.setTextColor(Color.BLACK);
        B1.setTextColor(Color.BLACK);
        C1.setTextColor(Color.BLACK);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(getResources().getColor(com.vpipl.mmtbusiness.R.color.app_color_green_one));

        ll.addView(row1);
        ll.addView(view);


        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String Transaction_Date = jobject.getString("DeductionDD");
                String Amount = jobject.getString("Amount");
                String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

                StringBuilder sb = new StringBuilder(Remarks);


//                int ii = 0;
//                while ((ii = sb.indexOf(" ", ii + 15)) != -1) {
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

                A.setText(Transaction_Date);
                B.setText(Amount);
                C.setText(sb.toString());


                A.setGravity(Gravity.CENTER_VERTICAL);
                B.setGravity(Gravity.CENTER_VERTICAL);
                C.setGravity(Gravity.CENTER_VERTICAL);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);

               A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);
                row.addView(C);

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

        AppUtils.dismissProgressDialog();
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
            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
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
            AppUtils.showExceptionDialog(Wallet_Transaction_Report_Activity.this);
        }
    }

}
