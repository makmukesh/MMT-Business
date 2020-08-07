/*
 * Copyright 2019 MMT Business. All rights reserved.
 */

package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.CircularImageView;
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
import java.util.Map;

public class Daily_Incentive_Detail_Activity extends AppCompatActivity {

    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Daily_Incentive_Detail_Activity";
    private TableLayout displayLinear;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;

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
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Daily_Incentive_Detail_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }

    TextView txt_income_heading ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_incentive);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            displayLinear = findViewById(R.id.displayLinear);

            if (AppUtils.isNetworkAvailable(this)) {
              //  executeLoginRequest();
                continueapp();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }


         /*   drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);*/
            txt_income_heading = findViewById(R.id.txt_income_heading);


            txt_income_heading.setText("Daily Incentive Detail Report");

      /*      listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }


    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(Daily_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Daily_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                continueapp();
                            } else {

                                Toast.makeText(Daily_Incentive_Detail_Activity.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Daily_Incentive_Detail_Activity.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
        }
    }

    public void continueapp() {

        executeMonthlyIncentiveRequest();

      //  enableExpandableList();
     //   LoadNavigationHeaderItems();
    }

    private void executeMonthlyIncentiveRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Daily_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Daily_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodToDailyIncentiveDetail, TAG);

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
                                AppUtils.dismissProgressDialog();
                                AppUtils.alertDialog(Daily_Incentive_Detail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
        }
    }

    private void WriteValues(JSONArray jsonArray) {

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        TableRow row1 = new TableRow(this);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.app_color_white));

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
        TextView Q1 = new TextView(this);
        TextView R1 = new TextView(this);
        TextView S1 = new TextView(this);
        TextView T1 = new TextView(this);
        TextView U1 = new TextView(this);
        TextView V1 = new TextView(this);
        TextView W1 = new TextView(this);
        TextView X1 = new TextView(this);
        TextView Y1 = new TextView(this);

        A1.setText("Session");
        B1.setText("Payout Date");
        C1.setText("Left New\nPoint");
        D1.setText("Right New \nPoint");
        E1.setText("Left Total \nBusiness");
        F1.setText("Right Total \nBusiness");
        G1.setText("Power Side \nBusiness");
        H1.setText("Weaker Side \nBusiness");
        I1.setText("Matched \nBusiness");
        J1.setText("Matching \nBonus");
        K1.setText("Gross \nIncentive");
        L1.setText("TDS \nAmount");
        M1.setText("Admin \nCharge");
        N1.setText("Repurchase \nCharge");
        O1.setText("Cheque \nAmount");
        P1.setText("Closing\n Balance");
        Q1.setText("Closing Balance \nReason");
        Y1.setText("Statement");

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
        Q1.setPadding(px, px, px, px);
        R1.setPadding(px, px, px, px);
        S1.setPadding(px, px, px, px);
        T1.setPadding(px, px, px, px);
        U1.setPadding(px, px, px, px);
        V1.setPadding(px, px, px, px);
        W1.setPadding(px, px, px, px);
        X1.setPadding(px, px, px, px);
        Y1.setPadding(px, px, px, px);

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
        Q1.setTypeface(typeface);
        R1.setTypeface(typeface);
        S1.setTypeface(typeface);
        T1.setTypeface(typeface);
        U1.setTypeface(typeface);
        V1.setTypeface(typeface);
        W1.setTypeface(typeface);
        X1.setTypeface(typeface);
        Y1.setTypeface(typeface);

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
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        U1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        V1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        W1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        X1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Y1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


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
        L1.setGravity(Gravity.CENTER);
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
        Q1.setGravity(Gravity.CENTER);
        R1.setGravity(Gravity.CENTER);
        S1.setGravity(Gravity.CENTER);
        T1.setGravity(Gravity.CENTER);
        U1.setGravity(Gravity.CENTER);
        V1.setGravity(Gravity.CENTER);
        W1.setGravity(Gravity.CENTER);
        X1.setGravity(Gravity.CENTER);
        Y1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.BLACK);
        B1.setTextColor(Color.BLACK);
        C1.setTextColor(Color.BLACK);
        D1.setTextColor(Color.BLACK);
        E1.setTextColor(Color.BLACK);
        F1.setTextColor(Color.BLACK);
        G1.setTextColor(Color.BLACK);
        H1.setTextColor(Color.BLACK);
        I1.setTextColor(Color.BLACK);
        J1.setTextColor(Color.BLACK);
        L1.setTextColor(Color.BLACK);
        D1.setTextColor(Color.BLACK);
        E1.setTextColor(Color.BLACK);
        F1.setTextColor(Color.BLACK);
        G1.setTextColor(Color.BLACK);
        H1.setTextColor(Color.BLACK);
        I1.setTextColor(Color.BLACK);
        J1.setTextColor(Color.BLACK);
        K1.setTextColor(Color.BLACK);
        L1.setTextColor(Color.BLACK);
        M1.setTextColor(Color.BLACK);
        N1.setTextColor(Color.BLACK);
        O1.setTextColor(Color.BLACK);
        P1.setTextColor(Color.BLACK);
        Q1.setTextColor(Color.BLACK);
        R1.setTextColor(Color.BLACK);
        S1.setTextColor(Color.BLACK);
        T1.setTextColor(Color.BLACK);
        U1.setTextColor(Color.BLACK);
        V1.setTextColor(Color.BLACK);
        W1.setTextColor(Color.BLACK);
        X1.setTextColor(Color.BLACK);
        Y1.setTextColor(Color.BLACK);

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
        row1.addView(Q1);
      /*  row1.addView(R1);
        row1.addView(S1);
        row1.addView(T1);
        row1.addView(U1);
        row1.addView(V1);
        row1.addView(W1);
        row1.addView(X1);*/
        row1.addView(Y1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);

                int payout_number = jobject.getInt("SessId");
                final String PayoutDate = jobject.getString("PayDate");
                final String LeftNewPoint = jobject.getString("LegXBV");
                final String RightNewPoint = jobject.getString("LegYBV");
                final String LeftTotalBusiness = jobject.getString("LegXBVTotal");
                final String RightTotalBusiness = jobject.getString("LegYBVTotal");
                final String PwrLegBv = jobject.getString("PwrLegBv");
                final String WkrLegBv = jobject.getString("WkrLegBv");

                final String MatchedBusiness = jobject.getString("CarryFrwrd");

                final String MatchingBonus = jobject.getString("BinaryIncome");
                final String GrossIncentive = jobject.getString("NetIncome");
                final String TDSAmount = jobject.getString("TdsAmount");
                final String AdminCharge = jobject.getString("AdminCharge");
                final String Repurchasecharge = jobject.getString("RepurchaseCharge");
                final String ChequeAmount = jobject.getString("ChqAmt");
                final String ClosingBalance = jobject.getString("ClsBal");
                final String ClsBalReason = jobject.getString("ClsBalReason");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setPadding(0, px, 0, px);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));


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
                final TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);
                TextView O = new TextView(this);
                TextView P = new TextView(this);
                TextView Q = new TextView(this);
                TextView R = new TextView(this);
                TextView S = new TextView(this);
                TextView T = new TextView(this);
                TextView U = new TextView(this);
                TextView V = new TextView(this);
                TextView W = new TextView(this);
                final TextView X = new TextView(this);
                final TextView Y = new TextView(this);

                A.setText("" + payout_number);
                B.setText(AppUtils.getDateFromAPIDate(PayoutDate));
                C.setText(LeftNewPoint);
                D.setText(RightNewPoint);
                E.setText(LeftTotalBusiness);
                F.setText(RightTotalBusiness);
                G.setText(PwrLegBv);
                H.setText(WkrLegBv);
                I.setText(MatchedBusiness);
                J.setText(MatchingBonus);
                K.setText(GrossIncentive);
                L.setText(TDSAmount);
                M.setText(AdminCharge);
                N.setText(Repurchasecharge);
                O.setText(ChequeAmount);
                P.setText(ClosingBalance);
                Q.setText(ClsBalReason);
                Y.setText("View");
                Y.setId(payout_number);

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
                Q.setPadding(px, px, px, px);
                R.setPadding(px, px, px, px);
                S.setPadding(px, px, px, px);
                T.setPadding(px, px, px, px);
                U.setPadding(px, px, px, px);
                V.setPadding(px, px, px, px);
                W.setPadding(px, px, px, px);
                X.setPadding(px, px, px, px);
                Y.setPadding(px, px, px, px);

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
                Q.setTypeface(typeface);
                R.setTypeface(typeface);
                S.setTypeface(typeface);
                T.setTypeface(typeface);
                U.setTypeface(typeface);
                V.setTypeface(typeface);
                W.setTypeface(typeface);
                X.setTypeface(typeface);
                Y.setTypeface(typeface);

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
                Q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                R.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                S.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                T.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                U.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                V.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                W.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                X.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Y.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

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
                Q.setGravity(Gravity.CENTER);
                R.setGravity(Gravity.CENTER);
                S.setGravity(Gravity.CENTER);
                T.setGravity(Gravity.CENTER);
                U.setGravity(Gravity.CENTER);
                V.setGravity(Gravity.CENTER);
                W.setGravity(Gravity.CENTER);
                X.setGravity(Gravity.CENTER);
                Y.setGravity(Gravity.CENTER);

                Y.setTextColor(Color.BLUE);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);


             //   Y.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));

                Y.setLayoutParams(params);

/*                  L.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);


                L.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));


                L.setLayoutParams(params);*/

                Y.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        executeEncryptPayoutNoRequest("" + Y.getId());
                    }
                });

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
                row.addView(Q);

                row.addView(Y);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row);
                ll.addView(view_one);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
        }
    }

/*
    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (HeadingJarray != null && HeadingJarray.length() > 0)
                prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
            else
                executeTogetDrawerMenuItems();
        }

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Home")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Home_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Daily_Incentive_Detail_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Register_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.view_profile))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Detail Report")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Daily_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Weekly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));


                } else if (ChildItemTitle.trim().equalsIgnoreCase("Genereated/Issued Pin Details")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "Genereated/Issued Pin Details"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Received Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Received Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Pin Genereated")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "Pin Genereated"));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request"));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request Detail"));


                } else if (ChildItemTitle.trim().equalsIgnoreCase("Success Orders History")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Order_Success_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Failed Orders Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Order_Failed_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase BV Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Repurchase_BV_Detail.class));
                }

*/
/*
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Genereated/Issued Pin Details")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_Generated_issued_details_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_details_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_details_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_Request_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_Request_Report_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_Request_Report_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Genereated/Issued Pin Details")) {
                    startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Pin_Generated_issued_details_Activity.class));
                }

*//*



                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }
*/

    private void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase(""))
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Daily_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this,
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
                                    String count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
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

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();
        try {
            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
            }

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
                    }
                }
                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(aa), SubMenuAl);
            }
            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Daily_Incentive_Detail_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    AppUtils.dismissProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        HeadingJarray = jsonObject.getJSONArray("Data");
                        prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeEncryptFormNoRequest(final String encryptedpayoutno) {
        try {
            if (AppUtils.isNetworkAvailable(Daily_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Daily_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("BillNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodToWebEncryption, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                String encryptedFormno = jsonObject.getString("Message");
                                String path = AppUtils.ViewincentiveURL() + encryptedpayoutno + "&FNo=" + encryptedFormno;
                                Log.e("Path", path);
                                startActivity(new Intent(Daily_Incentive_Detail_Activity.this, Incentive_Statement_Activity.class).putExtra("URL", path));
                            } else {
                                AppUtils.alertDialog(Daily_Incentive_Detail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
        }
    }

    private void executeEncryptPayoutNoRequest(final String payoutno) {
        try {
            if (AppUtils.isNetworkAvailable(Daily_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Daily_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("BillNo", payoutno));
                            response = AppUtils.callWebServiceWithMultiParam(Daily_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodToWebEncryption, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                executeEncryptFormNoRequest(jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Daily_Incentive_Detail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
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
            AppUtils.showExceptionDialog(Daily_Incentive_Detail_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}
