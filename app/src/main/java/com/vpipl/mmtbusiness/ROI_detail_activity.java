package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ROI_detail_activity extends AppCompatActivity {

    String TAG = "ROI_detail_activity";

    Button btn_proceed;

    TextInputEditText txt_SelectPlan;
    TextInputEditText txt_select_kit;
    TextInputEditText txt_select_Status;

    public ArrayList<HashMap<String, String>> PlanList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> KitList = new ArrayList<>();


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
                    startActivity(new Intent(ROI_detail_activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ROI_detail_activity.this);
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
        setContentView(R.layout.activity_roi_summary_detail);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            txt_SelectPlan = (TextInputEditText) findViewById(R.id.txt_SelectPlan);
            txt_select_kit = (TextInputEditText) findViewById(R.id.txt_select_kit);
            txt_select_Status = (TextInputEditText) findViewById(R.id.txt_select_Status);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);

            txt_select_Status.setText("ALL");


            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(ROI_detail_activity.this, view);
                    createROIDetailRequest();
                }
            });

            txt_select_Status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStatusDialog();
                    txt_select_Status.setError(null);
                }
            });


            txt_SelectPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PlanList.size() > 0) {
                        showPlanDialog();
                        txt_select_Status.setError(null);
                    }
                }
            });

            txt_select_kit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (KitList.size() > 0) {
                        showKitDialog();
                        txt_select_Status.setError(null);
                    }

                }
            });

            if (AppUtils.isNetworkAvailable(this))
                executePlanRequest();
            else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void showStatusDialog() {
        try {

            final String[] AgentArray = {"ALL", "Paid", "UnPaid"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Status");
            builder.setItems(AgentArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_Status.setText(AgentArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }


    private void createROIDetailRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();


        String Plan = txt_SelectPlan.getText().toString().trim();
        String PlanID = "0";
        for (int i = 0; i < PlanList.size(); i++) {
            if (Plan.equalsIgnoreCase(PlanList.get(i).get("PlanName"))) {
                PlanID = PlanList.get(i).get("PlanId");
            }
        }

        String PackageID = "0";
        String Package = txt_select_kit.getText().toString().trim();

        for (int i = 0; i < KitList.size(); i++) {
            if (Package.equalsIgnoreCase(KitList.get(i).get("KitName"))) {
                PackageID = KitList.get(i).get("KitId");
            }
        }

        String status = txt_select_Status.getText().toString();
        String TypeStatus = "0";
        if (status.equalsIgnoreCase("ALL"))
            TypeStatus = "0";
        else if (status.equalsIgnoreCase("Paid"))
            TypeStatus = "Y";
        else
            TypeStatus = "N";

        postParameters.add(new BasicNameValuePair("LoginIDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));

        postParameters.add(new BasicNameValuePair("TypeStatus", TypeStatus));
        postParameters.add(new BasicNameValuePair("PlanID", PlanID));
        postParameters.add(new BasicNameValuePair("PackageID", PackageID));
        executeROIDetailsRequest(postParameters);
    }

    private void executeROIDetailsRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(ROI_detail_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ROI_detail_activity.this);

                        System.gc();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(ROI_detail_activity.this, postparameters, QueryUtils.methodLoad_ROIDetail, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ROI_detail_activity.this);
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
                                    if (jsonArrayData.length() > 0)

                                        WriteValues(jsonArrayData);

                                    else
                                        AppUtils.alertDialog(ROI_detail_activity.this, "No Data Found");
                                } else {
                                    AppUtils.alertDialog(ROI_detail_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(ROI_detail_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ROI_detail_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

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
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);
        TextView I1 = new TextView(this);
        TextView J1 = new TextView(this);
        TextView K1 = new TextView(this);
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);
        TextView O1 = new TextView(this);
        TextView P1 = new TextView(this);

        A1.setText("Plan");
        B1.setText("Package");
        C1.setText("Inst. No.");
        D1.setText("Inc.(%)");
        E1.setText("Income");
        F1.setText("TDS (%)");
        G1.setText("TDS Amount");
        H1.setText("Admin Per(%)");
        I1.setText("Admin Charge");
        J1.setText("Net Income");
        K1.setText("Due Date");
        L1.setText("Remarks");
        M1.setText("Paid Status");
        N1.setText("Paid Date");
        O1.setText("Paid By");
        P1.setText("Narration");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);
        I1.setPadding(px, px, px, px);
        J1.setPadding(px, px, px, px);
        K1.setPadding(px, px, px, px);
        L1.setPadding(px, px, px, px);
        M1.setPadding(px, px, px, px);
        N1.setPadding(px, px, px, px);
        O1.setPadding(px, px, px, px);
        P1.setPadding(px, px, px, px);


        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);
        I1.setTypeface(typeface);
        J1.setTypeface(typeface);
        K1.setTypeface(typeface);
        L1.setTypeface(typeface);
        M1.setTypeface(typeface);
        N1.setTypeface(typeface);
        O1.setTypeface(typeface);
        P1.setTypeface(typeface);


        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        K1.setGravity(Gravity.CENTER);
        L1.setGravity(Gravity.CENTER);
        M1.setGravity(Gravity.CENTER);
        N1.setGravity(Gravity.CENTER);
        O1.setGravity(Gravity.CENTER);
        P1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);
        J1.setTextColor(Color.WHITE);
        K1.setTextColor(Color.WHITE);
        L1.setTextColor(Color.WHITE);
        M1.setTextColor(Color.WHITE);
        N1.setTextColor(Color.WHITE);
        O1.setTextColor(Color.WHITE);
        P1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);
        row1.addView(K1);
        row1.addView(L1);
        row1.addView(M1);
        row1.addView(N1);
        row1.addView(O1);
        row1.addView(P1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);


        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);


                String Plan = jobject.getString("PlanName");
                String Package = jobject.getString("KitName");
                String InstNo = jobject.getString("InstNo");
                String Inc = jobject.getString("IncPer");
                String Income = jobject.getString("Income");
                String TDS = jobject.getString("TDSPer");
                String TDSAmount = jobject.getString("TDSAmount");
                String AdminPer = jobject.getString("AdminPer");
                String AdminCharge = jobject.getString("AdminCharge");
                String NetIncome = jobject.getString("NetIncome");
                String DueDate = jobject.getString("DispDueDate");
                String Remarks = jobject.getString("Remarks");
                String PaidStatus = jobject.getString("PaidStatus1");
                String PaidDate = jobject.getString("DispPaidDate");
                String PaidBy = jobject.getString("PaidBy");
                String Narration = jobject.getString("Narration");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0)
                    row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                else row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);
                TextView I = new TextView(this);
                TextView J = new TextView(this);
                TextView K = new TextView(this);
                TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);
                TextView O = new TextView(this);
                TextView P = new TextView(this);


                A.setText(Plan);
                B.setText(Package);
                C.setText(InstNo);
                D.setText(Inc);
                E.setText(Income);
                F.setText(TDS);
                G.setText(TDSAmount);
                H.setText(AdminPer);
                I.setText(AdminCharge);
                J.setText(NetIncome);
                K.setText(DueDate);
                L.setText(Remarks);
                M.setText(PaidStatus);
                N.setText(PaidDate);
                O.setText(PaidBy);
                P.setText(Narration);


                if (N.getText().toString().equalsIgnoreCase("null"))
                    N.setText("");


                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);
                I.setPadding(px, px, px, px);
                J.setPadding(px, px, px, px);
                K.setPadding(px, px, px, px);
                L.setPadding(px, px, px, px);
                M.setPadding(px, px, px, px);
                N.setPadding(px, px, px, px);
                O.setPadding(px, px, px, px);
                P.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);
                I.setTypeface(typeface);
                J.setTypeface(typeface);
                K.setTypeface(typeface);
                L.setTypeface(typeface);
                M.setTypeface(typeface);
                N.setTypeface(typeface);
                O.setTypeface(typeface);
                P.setTypeface(typeface);

                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);
                E.setTextColor(Color.BLACK);
                F.setTextColor(Color.BLACK);
                G.setTextColor(Color.BLACK);
                H.setTextColor(Color.BLACK);
                I.setTextColor(Color.BLACK);
                J.setTextColor(Color.BLACK);
                K.setTextColor(Color.BLACK);
                L.setTextColor(Color.BLACK);
                M.setTextColor(Color.BLACK);
                N.setTextColor(Color.BLACK);
                O.setTextColor(Color.BLACK);
                P.setTextColor(Color.BLACK);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                P.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);
                J.setGravity(Gravity.CENTER);
                K.setGravity(Gravity.CENTER);
                L.setGravity(Gravity.CENTER);
                M.setGravity(Gravity.CENTER);
                N.setGravity(Gravity.CENTER);
                O.setGravity(Gravity.CENTER);
                P.setGravity(Gravity.CENTER);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
                row.addView(K);
                row.addView(L);
                row.addView(M);
                row.addView(N);
                row.addView(O);
                row.addView(P);

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
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void executePlanRequest() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtils.showProgressDialog(ROI_detail_activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(ROI_detail_activity.this, postParameters, QueryUtils.methodToROIPlan, TAG);
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
                            getPlanResult(jsonArrayData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPlanResult(JSONArray jsonArray) {
        try {

            PlanList.clear();

            HashMap<String, String> map_zero = new HashMap<>();
            map_zero.put("PlanId", "0");
            map_zero.put("PlanName", "ALL");
            PlanList.add(map_zero);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("PlanId", jsonObject.getString("PlanId"));
                map.put("PlanName", (jsonObject.getString("PlanName")));
                PlanList.add(map);
            }

            txt_SelectPlan.setText("ALL");

            String Bankid = "0";
            for (int i = 0; i < PlanList.size(); i++) {
                if (txt_SelectPlan.getText().toString().equalsIgnoreCase(PlanList.get(i).get("PlanName"))) {
                    Bankid = PlanList.get(i).get("PlanId");
                }
            }

            executeKitRequest(Bankid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPlanDialog() {
        try {
            final String[] PackageArray = new String[PlanList.size()];
            for (int i = 0; i < PlanList.size(); i++) {
                PackageArray[i] = PlanList.get(i).get("PlanName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Plan");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_SelectPlan.setText(PackageArray[item]);

                    String Bankid = "0";
                    for (int i = 0; i < PlanList.size(); i++) {
                        if (PackageArray[item].equalsIgnoreCase(PlanList.get(i).get("PlanName"))) {
                            Bankid = PlanList.get(i).get("PlanId");
                        }
                    }

                    executeKitRequest(Bankid);

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }

    private void executeKitRequest(final String PlanType) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtils.showProgressDialog(ROI_detail_activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("PlanType", "" + PlanType));
                response = AppUtils.callWebServiceWithMultiParam(ROI_detail_activity.this, postParameters, QueryUtils.methodToROIReport_KitMaster, TAG);
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
                            getKitResult(jsonArrayData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getKitResult(JSONArray jsonArray) {
        try {

            KitList.clear();

            HashMap<String, String> map_zero = new HashMap<>();
            map_zero.put("KitId", "0");
            map_zero.put("KitName", "ALL");
            KitList.add(map_zero);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("KitId", jsonObject.getString("KitId"));
                map.put("KitName", (jsonObject.getString("KitName")));
                KitList.add(map);
            }

            txt_select_kit.setText("ALL");

            createROIDetailRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKitDialog() {
        try {
            final String[] PackageArray = new String[KitList.size()];
            for (int i = 0; i < KitList.size(); i++) {
                PackageArray[i] = KitList.get(i).get("KitName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Kit");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_kit.setText(PackageArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }
}