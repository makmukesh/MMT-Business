/*
 * Copyright 2019 MMT Business. All rights reserved.
 */

package com.vpipl.mmtbusiness;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ExpandableListAdapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.CircularImageView;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashBoard_New extends AppCompatActivity {

    private static final String TAG = "DashBoard_New";

    public DrawerLayout drawer;
    CircularImageView profileImage;
    public NavigationView navigationView;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private JSONArray HeadingJarray;

    private TextView txt_welcome_name, txt_id_number, txt_available_wb;

    private TextView txt_joining_Date, txt_joining_package, txt_Status, txt_Activation_Date, txt_user_id, txt_Inv_Amount, txt_Last_Profile_Update;
    private TextView txt_welcome_name_header;
    TextView txt_total_matching_bonus,txt_ttl_direct_sale_bonus , txt_ttl_leg_level_bonus,txt_ttl_gross_incentive
            ,txt_ttl_pool_income , txt_ttl_shopping_wallet_bal  , txt_ttl_payout_wallet_balance,txt_rank_name, txt_product_bonus;

    TableLayout displayLinear;
    LinearLayout ll_rank ;

    ImageView img_nav_back;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);

        TextView textView = (TextView) findViewById(R.id.txt_back);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
                    drawer.openDrawer(navigationView);
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DashBoard_New.this, Home_New.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                View navHeaderView = navigationView.getHeaderView(0);

                txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);
                txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
                txt_available_wb = (TextView) navHeaderView.findViewById(R.id.txt_available_wb);
                profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
                LinearLayout LL_Nav = (LinearLayout) navHeaderView.findViewById(R.id.LL_Nav);

                expListView = (ExpandableListView) findViewById(R.id.left_drawer);

                txt_total_matching_bonus =  findViewById(R.id.txt_total_matching_bonus);
                txt_ttl_direct_sale_bonus =  findViewById(R.id.txt_ttl_direct_sale_bonus);
                txt_ttl_leg_level_bonus =  findViewById(R.id.txt_ttl_leg_level_bonus);
                txt_ttl_gross_incentive =  findViewById(R.id.txt_ttl_gross_incentive);
                txt_ttl_pool_income =  findViewById(R.id.txt_ttl_pool_income);
                txt_ttl_shopping_wallet_bal =  findViewById(R.id.txt_ttl_shopping_wallet_bal);
                txt_ttl_payout_wallet_balance =  findViewById(R.id.txt_ttl_payout_wallet_balance);
                txt_rank_name =  findViewById(R.id.txt_rank_name);
                txt_product_bonus =  findViewById(R.id.txt_product_bonus);
                ll_rank =  findViewById(R.id.ll_rank);

                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                txt_rank_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                 //       startActivity(new Intent(DashBoard_New.this , Pool_income_Report_Activity.class));
                    }
                });

                HeadingJarray = Splash_Activity.HeadingJarray;

                LL_Nav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(DashBoard_New.this, Profile_View_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                });

                txt_joining_Date = (TextView) findViewById(R.id.txt_joining_Date);
                txt_joining_package = (TextView) findViewById(R.id.txt_joining_package);
                txt_Status = (TextView) findViewById(R.id.txt_Status);
                txt_Activation_Date = (TextView) findViewById(R.id.txt_Activation_Date);
                txt_user_id = (TextView) findViewById(R.id.txt_user_id);
                txt_Inv_Amount = (TextView) findViewById(R.id.txt_Inv_Amount);
                txt_Last_Profile_Update = (TextView) findViewById(R.id.txt_Last_Profile_Update);

                txt_welcome_name_header = (TextView) findViewById(R.id.txt_welcome_name_header);

                txt_joining_Date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    //    startActivity(new Intent(DashBoard_New.this , Vendor_Bv_Report_Activity.class));
                    }
                });

                drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });

             //   executeGetDashBoardDetails();
                executeGetDashBoard2Details();
              /*  executeShoppingWalletBalanceRequest();
                executeWalletBalanceRequest();*/
                enableExpandableList();
                LoadNavigationHeaderItems();

            } else {
                startActivity(new Intent(DashBoard_New.this, Login_Activity.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else {
            Intent intent = new Intent(DashBoard_New.this, Home_New.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(DashBoard_New.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit from Dashboard ?"));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Exit");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    Intent intent = new Intent(DashBoard_New.this, Home_New.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeGetDashBoardDetails() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_New.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_New.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this, postParameters, QueryUtils.methodToGetDashboardDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {

                            executeGetDashBoard2Details();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                WriteValuesTables(jsonObject.getJSONArray("GetLevelSummary"));
                                WriteValuesTablesTwo(jsonObject.getJSONArray("IDInvestmentDetail"));

                            } else {
                                AppUtils.alertDialog(DashBoard_New.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }

    private void executeGetDashBoard2Details() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_New.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_New.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this, postParameters, QueryUtils.methodDashboard_New_API1, TAG);
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

                            JSONArray jsonArrayPayoutDetails = jsonObject.getJSONArray("PayoutDetails");
                            JSONArray jsonArrayMemberDetails = jsonObject.getJSONArray(" MyDetails");
                            JSONArray jsonArrayShowRankDetails = jsonObject.getJSONArray("ShowRankDetails");
                            JSONArray jsonArrayShoppingWalletDetails = jsonObject.getJSONArray("ShoppingWalletDetails");
                            JSONArray jsonArrayPayoutWalletDetail = jsonObject.getJSONArray("PayoutWalletDetail");
                            JSONArray jsonArrayPoolIncome = jsonObject.getJSONArray("PoolIncome");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayMemberDetails,jsonArrayPayoutDetails,jsonArrayShowRankDetails,jsonArrayShoppingWalletDetails,
                                        jsonArrayPayoutWalletDetail , jsonArrayPoolIncome);
                            } else {
                                AppUtils.alertDialog(DashBoard_New.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }
    private void WriteValues(JSONArray jsonArrayMemberDetails ,JSONArray jsonArrayPayoutDetails ,
                             JSONArray jsonArrayShowRankDetails ,JSONArray jsonArrayShoppingWalletDetails ,
                             JSONArray jsonArrayPayoutWalletDetail ,JSONArray jsonArrayPoolIncome) {
        try {
            DecimalFormat precision = new DecimalFormat("0.00");
            if (jsonArrayShoppingWalletDetails.length() > 0) {
                Double d = jsonArrayShoppingWalletDetails.getJSONObject(0).getDouble("WBalance");
                Log.e("d123" , "" + precision.format(d));
                txt_ttl_shopping_wallet_bal.setText("\u20B9 " +precision.format(d));
            }
            if (jsonArrayPayoutWalletDetail.length() > 0) {
                Double d = jsonArrayPayoutWalletDetail.getJSONObject(0).getDouble("WBalance");
                Log.e("d12345" , "" + precision.format(d));
                txt_ttl_payout_wallet_balance.setText("\u20B9 " +precision.format(d));
            }
            if (jsonArrayShowRankDetails.length() > 0) {
                ll_rank.setVisibility(View.VISIBLE);
                txt_rank_name.setText(jsonArrayShowRankDetails.getJSONObject(0).getString("RankName"));
            }
            else {
                ll_rank.setVisibility(View.GONE);
            }
            if (jsonArrayPoolIncome.length() > 0) {
                Double d = jsonArrayPoolIncome.getJSONObject(0).getDouble("TotalPoolIncome");
                 txt_ttl_pool_income.setText("\u20B9 " + precision.format(d));
            }
            if (jsonArrayPayoutDetails.length() > 0) {
                txt_total_matching_bonus.setText("\u20B9 " +jsonArrayPayoutDetails.getJSONObject(0).getString("totalMatchBonus"));
                txt_ttl_direct_sale_bonus.setText("\u20B9 " +jsonArrayPayoutDetails.getJSONObject(0).getString("TotalSpillIncome"));
               // txt_ttl_gross_incentive.setText("\u20B9 " +jsonArrayPayoutDetails.getJSONObject(0).getString("TotalNetIncome"));
                txt_ttl_leg_level_bonus.setText("\u20B9 " +jsonArrayPayoutDetails.getJSONObject(0).getString("TotalLevelIncome"));
            }

            Double a = Double.parseDouble(jsonArrayPoolIncome.getJSONObject(0).getString("TotalPoolIncome")) ;
            Double b = Double.parseDouble(jsonArrayPayoutDetails.getJSONObject(0).getString("totalMatchBonus")) ;
            Double c = Double.parseDouble(jsonArrayPayoutDetails.getJSONObject(0).getString("TotalSpillIncome")) ;
            Double d = Double.parseDouble(jsonArrayPayoutDetails.getJSONObject(0).getString("TotalLevelIncome")) ;

            Double res = a + b + c + d ;

            // txt_ttl_gross_incentive.setText("\u20B9 " +jsonArrayPayoutDetails.getJSONObject(0).getString("TotalNetIncome"));
            Log.e("d123" , "" + precision.format(res));
            txt_ttl_gross_incentive.setText("\u20B9 " +precision.format(res));

            if (jsonArrayMemberDetails.length() > 0) {
                JSONObject jsonObject = jsonArrayMemberDetails.getJSONObject(0);
                txt_joining_Date.setText(jsonObject.getString("JoiningDt"));
                txt_joining_package.setText(jsonObject.getString("Package"));
                txt_Status.setText(jsonObject.getString("Status"));
                txt_user_id.setText(jsonObject.getString("IdNo"));
                txt_Last_Profile_Update.setText(jsonObject.getString("LastUpdateTime"));
               String strsts = jsonObject.getString("Status") ;
               if(strsts.equalsIgnoreCase("Active")){
                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_ACTIVE_STATUS, "Y").commit();
               }
               else{
                   AppController.getSpUserInfo().edit().putString(SPUtils.USER_ACTIVE_STATUS, "N").commit();
               }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }

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
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(DashBoard_New.this, Register_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Home")) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }

                    Intent intent = new Intent(DashBoard_New.this, Home_New.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Enquiry and Complaint")) {
                    startActivity(new Intent(DashBoard_New.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Investment Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Investment_Detail_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Wallet Withdrawal Request")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdraw_Request_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Wallet Withdraw Status Report")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdrawal_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Bill Entry")) {
                    startActivity(new Intent(DashBoard_New.this, Bill_Entry_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Bill Report")) {
                    startActivity(new Intent(DashBoard_New.this, Bill_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Vendor BV Report")) {
                    startActivity(new Intent(DashBoard_New.this, Vendor_Bv_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }

                else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(DashBoard_New.this);
                }
                else if (GroupTitle.trim().equalsIgnoreCase("Share")) {
                    Share();
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
                    startActivity(new Intent(DashBoard_New.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(DashBoard_New.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Upload KYC Documents")) {
                    startActivity(new Intent(DashBoard_New.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Binary Genealogy")) {
                    startActivity(new Intent(DashBoard_New.this, Binary_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(DashBoard_New.this, Sponsor_team_details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(DashBoard_New.this, Direct_members_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Level Commission")) {
                    startActivity(new Intent(DashBoard_New.this, Level_Commission_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Level Commission Detail Report")) {
                    startActivity(new Intent(DashBoard_New.this, Level_Commission_detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Payment History")) {
                    startActivity(new Intent(DashBoard_New.this, Payment_History_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Periodical Report")) {
                    startActivity(new Intent(DashBoard_New.this, Periodical_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(DashBoard_New.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Agreement Letter")) {
                    startActivity(new Intent(DashBoard_New.this, Payment_Slip_Activity.class)
                            .putExtra("URL", "http://realtimetrading.in/Dashboard/Agreement.aspx"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Genereated/Issued Pin Details")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "Genereated/Issued Pin Details"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Topup/E-Pin Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Detail"));
                 } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Received Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Received Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(DashBoard_New.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ROI Detail Report")) {
                    startActivity(new Intent(DashBoard_New.this, ROI_detail_activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ROI Summary Report")) {
                    startActivity(new Intent(DashBoard_New.this, ROI_summary_activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Investment Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Investment_Detail_activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(DashBoard_New.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Withdrawal Request Report")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdrawal_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Withdrawal Amount Request")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdraw_Request_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Withdrawal Request")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdraw_Request_Activity.class));

                    /*Added new menus on real time to MMT Business 23-10-2018 12:50 PM*/
                }  else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Weekly_Incentive_Activity.class));
                }  else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive")) {
                    startActivity(new Intent(DashBoard_New.this, Daily_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Detail Report")) {
                    startActivity(new Intent(DashBoard_New.this, Daily_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Downline Team Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Downline_Team_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(DashBoard_New.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Request_Amount_Activity.class));}
                else if (ChildItemTitle.trim().equalsIgnoreCase("Rewards Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Rewards_Detail_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Transaction Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Transaction_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Shopping Wallet Transaction Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Transaction_Report_Shopping_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Payout Wallet Transaction Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Transaction_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Amount Withdraw Request")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdraw_Request_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Withdraw Status Report")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Withdrawal_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Launching Bonanza Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Launching_Bonanza_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Change Mobile No")) {
                    startActivity(new Intent(DashBoard_New.this, Change_Mobile_No_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Buy/Sell Products")) {
                    startActivity(new Intent(DashBoard_New.this, Buy_Sell_Products_Activity.class));
                }

                else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Transfer")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Transfer_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Transfer Report")) {
                    startActivity(new Intent(DashBoard_New.this, Wallet_Transfer_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Royalty Incentive Detail Report")) {
                    startActivity(new Intent(DashBoard_New.this, Royalty_Incentive_Detail_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Single Leg Bonus Detail")) {
                    startActivity(new Intent(DashBoard_New.this, Single_Leg_Bonus_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive")) {
                    startActivity(new Intent(DashBoard_New.this, Monthly_Incentive_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive Detailed Report")) {
                    startActivity(new Intent(DashBoard_New.this, Monthly_Incentive_Details_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Single Pool Incentive")) {
                    startActivity(new Intent(DashBoard_New.this, Pool_income_Report_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Report")) {
                    startActivity(new Intent(DashBoard_New.this, Daily_Incentive_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Detail Report")) {
                    startActivity(new Intent(DashBoard_New.this, Daily_Incentive_Detail_Activity.class));
                }
                else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Pool Details")) {
                    startActivity(new Intent(DashBoard_New.this, DailyPoolDetails_Activity.class));
                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }
    public void Share(){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody =getResources().getString(R.string.app_name)+ "\nDownload our App\n" +
                "http://tiny.cc/jzuc0y" ;

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share app using..."));
    }
    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();

        try {
            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName").trim());
            }

            listDataHeader.add("Home");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            listDataHeader.add("Bill Entry");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            listDataHeader.add("Bill Report");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add((HeadingJarray.getJSONObject(bb).getString("MenuName")).trim());
                    }
                }
                listDataHeader.add((MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), SubMenuAl);
            }
            listDataHeader.add("Share");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_welcome_name.setText("");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);
            txt_welcome_name_header.setText("Welcome, " + welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (bytecode.equalsIgnoreCase(""))
                executeGetProfilePicture();
            else
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_New.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this, postParameters, QueryUtils.methodGetImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(DashBoard_New.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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

    public void WriteValuesTables(JSONArray jsonArrayDownLineDetail) {
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        ll.removeAllViews();

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(px / 2, px / 2, px / 2, px / 2);
            params.weight = 1;

            if (jsonArrayDownLineDetail.length() > 0) {
                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);

                A1.setText("Level No");
                B1.setText("Joinings");
                C1.setText("Investment(s)");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
                B1.setTextColor(Color.WHITE);
                C1.setTextColor(Color.WHITE);

                A1.setLayoutParams(params);
                B1.setLayoutParams(params);
                C1.setLayoutParams(params);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);

                ll.addView(row1);


                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String LevelNo = jobject.getString("LevelName");
                        String Joinings = jobject.getString("TotalJoin");
                        String Investment = jobject.getString("TotalBV");

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

                        A.setText(LevelNo);
                        B.setText(Joinings);
                        C.setText(Investment);

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);

                        A.setLayoutParams(params);
                        B.setLayoutParams(params);
                        C.setLayoutParams(params);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);

                        ll.addView(row);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteValuesTablesTwo(JSONArray IDInvestmentDetail) {
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear_two);
        ll.removeAllViews();

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(px / 2, px / 2, px / 2, px / 2);
            params.weight = 1;

            if (IDInvestmentDetail.length() > 0) {
                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);

                A1.setText("Package Name");
                B1.setText("Investment Amount");
                C1.setText("Investment Date");
                D1.setText("Investment Type");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
                B1.setTextColor(Color.WHITE);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);

                A1.setLayoutParams(params);
                B1.setLayoutParams(params);
                C1.setLayoutParams(params);
                D1.setLayoutParams(params);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);

                ll.addView(row1);


                for (int i = 0; i < IDInvestmentDetail.length(); i++) {
                    try {
                        JSONObject jobject = IDInvestmentDetail.getJSONObject(i);

                        String KitName = jobject.getString("KitName");
                        String InvestmentAmt = jobject.getString("InvestmentAmt");
                        String InvestmentDate = jobject.getString("InvestmentDate");
                        String InvRemarks = jobject.getString("InvRemarks");

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

                        A.setText(KitName);
                        B.setText(InvestmentAmt);
                        C.setText(InvestmentDate);
                        D.setText(InvRemarks);

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);

                        A.setLayoutParams(params);
                        B.setLayoutParams(params);
                        C.setLayoutParams(params);
                        D.setLayoutParams(params);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);

                        ll.addView(row);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    private void executeShoppingWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_New.this)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_New.this);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this,
                                    postParameters, QueryUtils.methodToGetAvailableShoppingWalletBalance, TAG);

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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {


                                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                                txt_shopping_wallet_balance.setText("\u20B9 " + jsonArray.getJSONObject(0).getString("WBalance"));

                            } else {
                                AppUtils.alertDialog(DashBoard_New.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }
    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_New.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_New.this,
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

                                    txt_available_wb.setVisibility(View.VISIBLE);
                                    txt_available_wb.setText("Payout Wallet Balance :" + jsonArrayData.getJSONObject(0).getString("WBalance"));
                                    txt_payout_wallet_balance.setText(jsonArrayData.getJSONObject(0).getString("WBalance"));
                                }
                            } else {
                                AppUtils.alertDialog(DashBoard_New.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_New.this);
        }
    }*/

}