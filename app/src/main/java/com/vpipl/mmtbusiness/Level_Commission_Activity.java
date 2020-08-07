package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;

public class Level_Commission_Activity extends AppCompatActivity {

    public ArrayList<HashMap<String, String>> SessionList = new ArrayList<>();

    String TAG = "Level_Commission_Activity";

    TextView txt_package_Name;

    Button btn_proceed;
    TableLayout displayLinear;

    TextView txt_heading;
    String PackageArray[];

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
                    startActivity(new Intent(Level_Commission_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Level_Commission_Activity.this);
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
        setContentView(R.layout.activity_level_commission);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_package_Name = (TextView) findViewById(R.id.txt_package_Name);
            txt_heading = (TextView) findViewById(R.id.txt_heading);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);

            displayLinear = (TableLayout) findViewById(R.id.displayLinear);

            txt_package_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SessionList.size() != 0) {
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
                    createLevelCommissionRequest();
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

    private void executePackageRequest()
    {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Level_Commission_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(Level_Commission_Activity.this, postParameters, QueryUtils.methodToGet_MonthSession, TAG);
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
            SessionList.clear();

            HashMap<String, String> map_all = new HashMap<>();
            map_all.put("KitID", "0");
            map_all.put("KitName","ALL");
            SessionList.add(map_all);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("KitID", jsonObject.getString("SessId"));
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("Session")));

                SessionList.add(map);
            }

            String[] PackageArray = new String[SessionList.size()];
            for (int i = 0; i < SessionList.size(); i++) {
                PackageArray[i] = SessionList.get(i).get("KitName");
            }
            txt_package_Name.setText(PackageArray[0]);

            createLevelCommissionRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPackageDialog() {
        try {
            PackageArray = new String[SessionList.size()];
            for (int i = 0; i < SessionList.size(); i++) {
                PackageArray[i] = SessionList.get(i).get("KitName");
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
            AppUtils.showExceptionDialog(Level_Commission_Activity.this);
        }
    }

    private void createLevelCommissionRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);

        String packagename = txt_package_Name.getText().toString().trim();

        String packageid = "0";
        for (int i = 0; i < SessionList.size(); i++) {
            if (packagename.equals(SessionList.get(i).get("KitName"))) {
                packageid = SessionList.get(i).get("KitID");
            }
        }


        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("SessionID", "" + packageid));


        executeLevelCommissionRequest(postParameters);
    }

    private void executeLevelCommissionRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Level_Commission_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Level_Commission_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Level_Commission_Activity.this, postparameters, QueryUtils.methodToGet_LevelCommission, TAG);

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



                            if (jsonObject.getString("Status").equalsIgnoreCase("True"))
                            {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Level_Commission_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Level_Commission_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Level_Commission_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArrayData)
    {

        AppUtils.showProgressDialog(Level_Commission_Activity.this);

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            if (jsonArrayData.length() > 0) {
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
                B1.setText("Session");
                C1.setText("Designation");
                D1.setText("Commission Amount");
                E1.setText("TDS %");
                F1.setText("TDS Amount");
                L1.setText("Admin Charge %");
                H1.setText("Admin Charge");
                K1.setText("Gross Incentive");

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

                for (int i = 0; i < jsonArrayData.length(); i++) {
                    try {

                        JSONObject jobject = jsonArrayData.getJSONObject(i);

                        String Session = jobject.getString("Session");
                        String Designation = (jobject.getString("Designation"));
                        String CommissionAmount = jobject.getString("CommissionAmt");
                        String TDS = jobject.getString("TDSPer");
                        String AdminChargePer = jobject.getString("AdminChargePer");
                        String AdminCharge = jobject.getString("AdminCharge");
                        String GrossIncentive = jobject.getString("NetIncome");
                        String TDSAmount = jobject.getString("TDSAmount");

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
                        B.setText(Session);
                        C.setText(Designation);
                        D.setText(CommissionAmount);
                        E.setText(TDS);
                        F.setText(TDSAmount);
                        L.setText(AdminChargePer);
                        H.setText(AdminCharge);
                        K.setText(GrossIncentive);

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
            AppUtils.showExceptionDialog(Level_Commission_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}
