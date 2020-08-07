package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Periodical_Report_Activity extends AppCompatActivity {

    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();

    String TAG = "Periodical_Report_Activity";

    TextView txt_from_joining, txt_to_joining, txt_from_activation, txt_to_activation, txt_package_Name;

    TextInputEditText txt_TotalRecord,txt_TotalBookingAmount;

    LinearLayout ll_activation, ll_joining;
    CheckBox cb_joining, cb_activation;
    Button btn_proceed;
    TableLayout displayLinear;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";
    DatePickerDialog datePickerDialog;

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_activation"))
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_from_activation")) {
                    txt_from_activation.setText(sdf.format(myCalendar.getTime()));
                }
            } else {

                AppUtils.alertDialog(Periodical_Report_Activity.this, "Selected Date Can't be After today");
            }
        }
    };

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(Periodical_Report_Activity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    TextView txt_heading;
    String PackageArray[], ActivationWeekArray[];

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
                    startActivity(new Intent(Periodical_Report_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Periodical_Report_Activity.this);
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
        setContentView(R.layout.activity_periodical_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_from_joining = (TextView) findViewById(R.id.txt_from_joining);
            txt_to_joining = (TextView) findViewById(R.id.txt_to_joining);
            txt_from_activation = (TextView) findViewById(R.id.txt_from_activation);
            txt_to_activation = (TextView) findViewById(R.id.txt_to_activation);

            txt_package_Name = (TextView) findViewById(R.id.txt_package_Name);
            txt_heading = (TextView) findViewById(R.id.txt_heading);

            ll_activation = (LinearLayout) findViewById(R.id.ll_activation);
            ll_joining = (LinearLayout) findViewById(R.id.ll_joining);

            cb_joining = (CheckBox) findViewById(R.id.cb_joining);
            cb_activation = (CheckBox) findViewById(R.id.cb_activation);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);

            txt_TotalRecord = (TextInputEditText) findViewById(R.id.txt_TotalRecord);
            txt_TotalBookingAmount = (TextInputEditText) findViewById(R.id.txt_TotalBookingAmount);

            displayLinear = (TableLayout) findViewById(R.id.displayLinear);

            cb_joining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_joining.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        txt_from_joining.setText("");
                        txt_to_joining.setText("");
                    }
                }
            });

            cb_activation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_activation.setVisibility(View.VISIBLE);
                    } else {
                        ll_activation.setVisibility(View.GONE);
                        txt_from_activation.setText("");
                        txt_to_activation.setText("");
                    }
                }
            });

            txt_package_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageList.size() != 0) {
                        showPackageDialog();
                        txt_package_Name.clearFocus();
                    } else {
                        executePackageRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createSponsorTeamListRequest();
                }
            });


            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");


            txt_from_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_joining";
                    showdatePicker();
                }
            });

            txt_to_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_joining";
                    showdatePicker();
                }
            });

            txt_from_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_activation";
                    showdatePicker();
                }
            });

            txt_to_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_activation";
                    showdatePicker();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                executePackageRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executePackageRequest() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
                AppUtils.showProgressDialog(Periodical_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(Periodical_Report_Activity.this, postParameters, QueryUtils.methodSponsorPageFillPackage, TAG);
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    AppUtils.dismissProgressDialog();

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
            PackageList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("KitID", jsonObject.getString("KitID"));
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("KitName")));

                PackageList.add(map);
            }

            String[] PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }
            txt_package_Name.setText(PackageArray[0]);

            createSponsorTeamListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPackageDialog() {
        try {
            PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Package ");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_package_Name.setText(PackageArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Periodical_Report_Activity.this);
        }
    }

    private void createSponsorTeamListRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);

        String Type = "0";

        String side = "0";

        String packagename = txt_package_Name.getText().toString().trim();

        String packageid = "0";
        for (int i = 0; i < PackageList.size(); i++) {
            if (packagename.equals(PackageList.get(i).get("KitName"))) {
                packageid = PackageList.get(i).get("KitID");
            }
        }

        String WeekValue = "0";

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_joining.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_joining.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("FromAD", "" + txt_from_activation.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("ToAD", "" + txt_to_activation.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("PackageId", "" + packageid));

        postParameters.add(new BasicNameValuePair("Searching", ""));
        postParameters.add(new BasicNameValuePair("SearchingAmount", ""));
        postParameters.add(new BasicNameValuePair("Type", ""));

        executeMemberDownlineListRequest(postParameters);
    }

    private void executeMemberDownlineListRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Periodical_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Periodical_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Periodical_Report_Activity.this, postparameters, QueryUtils.methodToGet_PeriodicalReport, TAG);

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

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                JSONArray jsonArrayTotalBookingAmount = jsonObject.getJSONArray("TotalBookingAmount");
                                JSONArray jsonArrayTotalRecord = jsonObject.getJSONArray("TotalRecord");
                                JSONArray jsonArrayPeriodicalReport = jsonObject.getJSONArray("PeriodicalReport");

                                WriteValues(jsonArrayTotalBookingAmount,jsonArrayTotalRecord, jsonArrayPeriodicalReport );

                            } else {
                                AppUtils.alertDialog(Periodical_Report_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Periodical_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Periodical_Report_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArrayTotalBookingAmount,JSONArray jsonArrayTotalRecord,JSONArray  jsonArrayPeriodicalReport)
    {

        AppUtils.showProgressDialog(Periodical_Report_Activity.this);

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {

            txt_TotalRecord.setText(jsonArrayTotalRecord.getJSONObject(0).getString("Total"));
            txt_TotalBookingAmount.setText(jsonArrayTotalBookingAmount.getJSONObject(0).getString("TotalBookingAmt"));



            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            if (jsonArrayPeriodicalReport.length() > 0)
            {
                findViewById(R.id.HSV).setVisibility(View.VISIBLE);

                TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
                ll.removeAllViews();

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView F1 = new TextView(this);
                TextView H1 = new TextView(this);
                TextView K1 = new TextView(this);
                TextView L1 = new TextView(this);

                A1.setText("S No.");
                B1.setText("Distributor ID");
                C1.setText(getString(R.string.name));
                D1.setText("Joining Date");
                E1.setText(getString(R.string.activation_date));
                F1.setText("Package Name");
                L1.setText("Sponsor ID");
                H1.setText("Booking Amount");
                K1.setText("Rank");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                L1.setPadding(px, px, px, px);
                K1.setPadding(px, px, px, px);

                  A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                F1.setTypeface(typeface);
                H1.setTypeface(typeface);
                L1.setTypeface(typeface);
                K1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);
                E1.setGravity(Gravity.CENTER);
                F1.setGravity(Gravity.CENTER);
                H1.setGravity(Gravity.CENTER);
                K1.setGravity(Gravity.CENTER);
                L1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
                B1.setTextColor(Color.WHITE);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);
                E1.setTextColor(Color.WHITE);
                F1.setTextColor(Color.WHITE);
                H1.setTextColor(Color.WHITE);
                K1.setTextColor(Color.WHITE);
                L1.setTextColor(Color.WHITE);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(F1);
                row1.addView(L1);
                row1.addView(H1);
                row1.addView(K1);

                ll.addView(row1);

                for (int i = 0; i < jsonArrayPeriodicalReport.length(); i++)
                {
                    try {

                        JSONObject jobject = jsonArrayPeriodicalReport.getJSONObject(i);

                        String member_id = jobject.getString("IdNo");

                        String name = WordUtils.capitalizeFully(jobject.getString("MemName"));
                        String purchase_date = jobject.getString("JoinDate");
                        String activation_date = jobject.getString("TopUpDate");
                        String sponsor_id = jobject.getString("ReferralIdNo");

                        String BookingAmount = jobject.getString("JoiningBV");
                        String Rank = jobject.getString("Rank");
                        String Package_name = jobject.getString("kitname");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                        else
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView F = new TextView(this);
                        TextView H = new TextView(this);
                        TextView K = new TextView(this);
                        TextView L = new TextView(this);

                        A.setText(""+(i+1));
                        B.setText(member_id);
                        C.setText(name);
                        D.setText(purchase_date);
                        E.setText(activation_date);
                        F.setText(Package_name);
                        L.setText(sponsor_id);
                        H.setText(BookingAmount);
                        K.setText(Rank);


                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        L.setPadding(px, px, px, px);
                        K.setPadding(px, px, px, px);

                          A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);
                        F.setTypeface(typeface);
                        H.setTypeface(typeface);
                        L.setTypeface(typeface);
                        K.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);
                        E.setGravity(Gravity.CENTER);
                        F.setGravity(Gravity.CENTER);
                        H.setGravity(Gravity.CENTER);
                        K.setGravity(Gravity.CENTER);
                        L.setGravity(Gravity.CENTER);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(F);
                        row.addView(L);
                        row.addView(H);
                        row.addView(K);

                        ll.addView(row);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        } catch (Exception e) {
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
            AppUtils.showExceptionDialog(Periodical_Report_Activity.this);
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
            AppUtils.showExceptionDialog(Periodical_Report_Activity.this);
        }
    }
}
