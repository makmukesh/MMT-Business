package com.vpipl.mmtbusiness;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpipl.mmtbusiness.Adapters.ExpandableListAdapter;
import com.vpipl.mmtbusiness.QR_Code.ReaderActivity;
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

/**
 * Created by admin on 01-07-2017.
 */

public class Home_New extends AppCompatActivity {

    private static final String TAG = "Home_New";
    LinearLayout ll_home_top_background ;


    static public DrawerLayout drawer;

    static public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    private TextView txt_login, txt_register, txt_welcomename;

    LinearLayout ll_mobile, ll_dth, ll_datacard, ll_electricity, ll_landline, ll_gas, ll_water, ll_broadband;

    LinearLayout layout_profile , ll_home_home;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_white));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_white));
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }
            }
        });

        ImageView img_smily = (ImageView) findViewById(R.id.img_smily);

        img_smily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Home_New.this, Profile_View_Activity.class));
            }
        });
    }

    LinearLayout LLBottom;

    public static ArrayList<HashMap<String, String>> imageSlider = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);

            ll_home_top_background = (LinearLayout) navHeaderView.findViewById(R.id.ll_home_top_background);
            txt_welcomename = (TextView) navHeaderView.findViewById(R.id.txt_welcomename);
            txt_register = (TextView) navHeaderView.findViewById(R.id.txt_register);
            txt_login = (TextView) navHeaderView.findViewById(R.id.txt_login);

            expListView = (ExpandableListView) findViewById(R.id.left_drawer);

            ll_mobile = (LinearLayout) findViewById(R.id.ll_mobile);
            ll_dth = (LinearLayout) findViewById(R.id.ll_dth);
            ll_datacard = (LinearLayout) findViewById(R.id.ll_datacard);
            ll_electricity = (LinearLayout) findViewById(R.id.ll_electricity);
            ll_landline = (LinearLayout) findViewById(R.id.ll_landline);
            ll_gas = (LinearLayout) findViewById(R.id.ll_gas);
            ll_water = (LinearLayout) findViewById(R.id.ll_water);
            ll_broadband = (LinearLayout) findViewById(R.id.ll_broadband);

            layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
            ll_home_home = (LinearLayout) findViewById(R.id.ll_home_home);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            LLBottom = (LinearLayout) findViewById(R.id.LLBottom);

            txt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home_New.this, Register_Activity.class));
                }
            });

            txt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, DashBoard_New.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));
                }
            });

            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_white));
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_white));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

            enableExpandableList();

            executeToGetBestSellerProducts();

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                txt_login.setText("Dashboard");
                txt_welcomename.setText("Hi " + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "") + " !");

            } else {
                txt_login.setText("Login");
                txt_welcomename.setText("Hi There !");
            }

            layout_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Profile_View_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));
                }
            });

            ll_home_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        startActivity(new Intent(Home_New.this, Home_New.class));
                }
            });

            findViewById(R.id.Lay_passbook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Wallet_Home_Transaction_Report_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            findViewById(R.id.txt_passbook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Wallet_Home_Transaction_Report_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });
            findViewById(R.id.Lay_kyc_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            findViewById(R.id.txt_kyc_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            findViewById(R.id.Lay_payout_wallet_tranactions).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Wallet_Transaction_Report_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            findViewById(R.id.txt_payout_wallet_tranactions).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Wallet_Transaction_Report_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });


            ll_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_Mobile_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_dth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_DTH_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_broadband.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_BroadBand_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_water.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Water_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_datacard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_Datacard_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });


            ll_electricity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Electricity_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_landline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Landline_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            ll_gas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Gas_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                }
            });

            executeGetCurrentSessid();
            executeBankRequest();
            executeStateRequest();
            executeToGetImageSlider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else {
            showExitDialog();
        }
    }

    public void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(Home_New.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit?"));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    Intent intent = new Intent(Home_New.this, CloseActivity.class);
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

    private void enableExpandableList() {

        List<String> Empty = new ArrayList<>();

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        List<String> cat2 = new ArrayList<>();

        List<String> Recharge = new ArrayList<>();
        Recharge.add("Mobile");
        Recharge.add("DTH");
        Recharge.add("Datacard");
        Recharge.add("Electricity");
        Recharge.add("Metro");
        Recharge.add("Gold");
        Recharge.add("Landline");
        Recharge.add("Gas");
        Recharge.add("Fee");
        Recharge.add("Financial");
        Recharge.add("Water");
        Recharge.add("Broadband");


        List<String> Booking = new ArrayList<>();
        Booking.add("Movies");
        Booking.add("Bus");
        Booking.add("Flights");
        Booking.add("Trains");
        Booking.add("Parks");
        Booking.add("Hotels");
        Booking.add("Free Movies");
        Booking.add("Events");
        Booking.add("Car & Bike");
        Booking.add("Gift Cards");


        for (int i = 0; i < AppController.category1.size(); i++) {
            for (int j = 0; j < AppController.category2.size(); j++) {
                if (AppController.category1.get(i).get("HID").equals(AppController.category2.get(j).get("HID"))) {
                    cat2.add((AppController.category2.get(j).get("Category")));
                }
            }
        }

        listDataHeader.add("Shop on MMT Business");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), cat2);

        listDataHeader.add("Recharge or Pay for");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Recharge);

        listDataHeader.add("Book on MMT Business");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Booking);

       /* listDataHeader.add("Super Fashion Sale");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        listDataHeader.add("Online Partners Offers");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);*/

      /*  listDataHeader.add("Company Commission");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);*/

        listDataHeader.add("Help & Support");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        listDataHeader.add("Share");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            listDataHeader.add("My Recharge Transactions");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            listDataHeader.add("My Shopping Orders");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

/*
            listDataHeader.add("Enquiry & Complaint");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
*/

            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
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
                    startActivity(new Intent(Home_New.this, Register_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Enquiry & Complaint")) {
                    startActivity(new Intent(Home_New.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Share")) {
                    Share();
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Company Commission")) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, ReaderActivity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }else if (GroupTitle.trim().equalsIgnoreCase("Help & Support")) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Send_Query_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("My Recharge Transactions")) {
                    startActivity(new Intent(Home_New.this, MyOrders_Recharge_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("My Shopping Orders")) {
                    startActivity(new Intent(Home_New.this, MyOrders_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Home_New.this);
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                String GroupItemTitle = listDataHeader.get(groupPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase("Mobile")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_Mobile_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("DTH")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_DTH_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Water")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Water_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Gas")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Gas_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Landline")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Landline_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Electricity")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_Electricity_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Datacard")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Recharge_Datacard_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Broadband")) {

                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(Home_New.this, Bill_BroadBand_Activity.class));
                    else
                        startActivity(new Intent(Home_New.this, Login_Activity.class));

                } else if (GroupItemTitle.trim().equalsIgnoreCase("Shop on MMT Business")) {
                    Intent intent = new Intent(Home_New.this, ProductExpand_Activity.class);
                    String CID = "";
                    String HID = "";

                    for (int i = 0; i < AppController.category2.size(); i++) {
                        String category = AppController.category2.get(i).get("Category");
                        if ((ChildItemTitle.equalsIgnoreCase(category))) {
                            CID = AppController.category2.get(i).get("CID");
                            HID = AppController.category2.get(i).get("HID");
                        }
                    }

                    intent.putExtra("HID", "" + HID);
                    intent.putExtra("CID", "" + CID);
                    startActivity(intent);
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
                "https://urlzs.com/uA6sL" ;

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share app using..."));
    }

    private void executeToGetBestSellerProducts() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_New.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();

                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                        postParameters.add(new BasicNameValuePair("UserType", "D"));
                    else
                        postParameters.add(new BasicNameValuePair("UserType", "N"));

                    response = AppUtils.callWebServiceWithMultiParam(Home_New.this, postParameters, QueryUtils.methodHotSellingProducts, TAG);
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
                        if (jsonObject.getJSONArray("Data").length() > 0) {
                            DrawBestSellingProducts(jsonObject.getJSONArray("Data"));
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void DrawBestSellingProducts(JSONArray Jarray) {

        LLBottom.removeAllViews();
        LLBottom.setVisibility(View.VISIBLE);

     /*   Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (5 * density), (int) (10 * density), (int) (5 * density), (int) (10 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, 0);

        LinearLayout.LayoutParams textparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);

       // LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (200 * density));
        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (150 * density), (int) (150 * density));

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                JSONObject Jobject = Jarray.getJSONObject(i);
                int ProdID = Jobject.getInt("ProdID");

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));

                String NewMRP = Jobject.getString("NewMRP");
                String Discount = Jobject.getString("DiscountPer");
                String NDP = Jobject.getString("NewDP");
                String imgpath = Jobject.getString("NewImgPath");
                String ImagePath = AppUtils.productImageURL() + imgpath;

                boolean DiscDisp = Jobject.getBoolean("DiscDisp");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setLayoutParams(layoutParams);

*//*                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (200 * density));*//*

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (150 * density));
                LL.setMinimumWidth((int) (150 * density));
                LL.setBackground(getResources().getDrawable(R.drawable.bg_white_round_rectangle));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_white_round_rectangletrans));
                imageView.setLayoutParams(imageparams);

                loadProductImage(ImagePath, imageView);

             *//*   TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);*//*
                TextView tvproductname = new TextView(getApplicationContext());

                textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textparams.setMargins(5, (int) (5 * density), 5, 5);

                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);

                final LinearLayout LL_MRP = new LinearLayout(getApplicationContext());
                LL_MRP.setOrientation(LinearLayout.HORIZONTAL);
                LL_MRP.setLayoutParams(textparams);

                TextView tvproductmrp = new TextView(getApplicationContext());
                tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tvproductmrp.setLayoutParams(textparams2);
                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setTypeface(typeface);

                String NewDP = "₹ " + " " + NDP + "/-";
                String DiscountPer = Discount + "% off";
                Spannable spanString;

                if (DiscountPer.equalsIgnoreCase("0% off") || DiscountPer.equalsIgnoreCase("0.0% off")) {
                    spanString = new SpannableString("" + NewDP);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_green_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                } else {
                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_green_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    spanString.setSpan(boldSpan, 0, NewDP.length(), 0);
                    spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
                }
                tvproductmrp.setText(spanString);

                LL_MRP.addView(tvproductmrp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);

                TextView newtag = new TextView(getApplicationContext());
                newtag.setBackgroundColor(getResources().getColor(R.color.color__bg_orange));
                newtag.setPadding(paddingDp / 2, paddingDp / 2, paddingDp / 2, paddingDp / 2);
                newtag.setText(DiscountPer);
                newtag.setTextColor(Color.WHITE);
                newtag.setLayoutParams(params);
                newtag.setTypeface(typeface);

                LL.setId(ProdID);
                LL.addView(imageView);
                LL.addView(LL_MRP);
                LL.addView(tvproductname);

                FL.addView(LL);

                if (DiscDisp)
                    FL.addView(newtag);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home_New.this, ProductDetail_Activity.class);
                        intent.putExtra("productID", "" + LL.getId());
                        startActivity(intent);
                    }
                });

                LLBottom.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, 0);

        LinearLayout.LayoutParams textparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams2.weight = 1.0f;
        textparams2.gravity = Gravity.RIGHT | Gravity.END;

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (150 * density), (int) (150 * density));

        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                JSONObject Jobject = Jarray.getJSONObject(i);
                int ProdID = Jobject.getInt("ProdID");

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));

                String NewMRP = Jobject.getString("NewMRP");
                String Discount = Jobject.getString("DiscountPer");
                String NDP = Jobject.getString("NewDP");
                String imgpath = Jobject.getString("NewImgPath");
                String BV = Jobject.getString("BV");
                String ImagePath = AppUtils.productImageURL() + imgpath;

                boolean DiscDisp = Jobject.getBoolean("DiscDisp");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (150 * density));
                LL.setMinimumWidth((int) (150 * density));
                LL.setBackground(getResources().getDrawable(R.drawable.bg_home_dash_rectangle));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_home_dash_rectangle));

                //    imageView.setBackground(getResources().getDrawable(R.drawable.bg_home_dash_rectangle));
                imageView.setLayoutParams(imageparams);
                //    imageView.setBackground(getResources().getDrawable(R.drawable.bg_home_dash_rectangle));

                imageView.setPadding((int) (10 * density),(int) (10 * density),(int) (10 * density),(int) (10 * density));

                loadProductImage(ImagePath, imageView);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));
                view.setPadding( 15,  0,  15,  0);


/*                TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);
                tvproductname.setGravity(Gravity.CENTER);*/

                TextView tvproductname = new TextView(getApplicationContext());

                textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textparams.setMargins(5, (int) (5 * density), 5, 5);

                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);


                textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textparams.setMargins(5, (int) (5 * density), 5, 2);

                final LinearLayout LL_MRP = new LinearLayout(getApplicationContext());
                LL_MRP.setOrientation(LinearLayout.HORIZONTAL);
                LL_MRP.setLayoutParams(textparams);


                TextView tvproductmrp = new TextView(getApplicationContext());
                tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setTypeface(typeface);
                tvproductmrp.setTextSize(10);
                tvproductmrp.setGravity(Gravity.CENTER);

                String NewDP = " ₹ " + " " + NDP + "/-";
                String DiscountPer = Discount + "% off";
                Spannable spanString;

                if (DiscountPer.equalsIgnoreCase("0% off") || DiscountPer.equalsIgnoreCase("0.0% off")) {
                    spanString = new SpannableString("" + NewDP);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                } else {
                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    spanString.setSpan(boldSpan, 0, NewDP.length(), 0);
                    spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
                }

                String sourceString = "<b>" + spanString + "</b> ";
                tvproductmrp.setText(Html.fromHtml(sourceString));
                tvproductmrp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tvproductmrp.setText(spanString);


                TextView tvBV = new TextView(getApplicationContext());
                tvBV.setLayoutParams(textparams2);
                tvBV.setMaxLines(1);
                tvBV.setTypeface(typeface);
                tvBV.setGravity(Gravity.END);
                tvBV.setTextSize(10);
                tvBV.setTextColor(getResources().getColor(android.R.color.black));
                tvBV.setText("BV:" + BV +" ");


                LL_MRP.addView(tvproductmrp);

                if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                    LL_MRP.addView(tvBV);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);

                TextView newtag = new TextView(getApplicationContext());
                newtag.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                newtag.setPadding(paddingDp / 5, paddingDp / 5, paddingDp / 5, paddingDp / 5);
                newtag.setText(DiscountPer);
                newtag.setTextColor(Color.WHITE);
                newtag.setLayoutParams(params);
                newtag.setTypeface(typeface);

                LL.setId(ProdID);
                LL.addView(imageView);
                LL.addView(view);
                LL.addView(LL_MRP);
                LL.addView(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Home_New.this, ProductDetail_Activity.class);
                        intent.putExtra("productID", "" + LL.getId());
                        startActivity(intent);
                    }
                });

                FL.addView(LL);

                if (DiscDisp)
                    FL.addView(newtag);


                    LLBottom.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadProductImage(String imageURL, ImageView imageView) {
        try {
            if (!Home_New.this.isFinishing()) {
                Glide.with(Home_New.this)
                        .load(imageURL)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .fallback(R.drawable.ic_no_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .animate(AppUtils.getAnimatorImageLoading())
                        .into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                txt_login.setText("Dashboard");
            else
                txt_login.setText("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                txt_login.setText("Dashboard");
            else
                txt_login.setText("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void executeGetCurrentSessid() {
        try {
            if (AppUtils.isNetworkAvailable(Home_New.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Home_New.this,
                                    postParameters, QueryUtils.methodToGet_CurrentSession, TAG);

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

                                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                    AppController.getSPcurrentSession().edit().putString(SPUtils.current_sess, jsonArray.getJSONObject(0).getString("SessID")).commit();
                                }
                            } else {
                                AppUtils.alertDialog(Home_New.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Home_New.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Home_New.this);
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
           //     AppUtils.showProgressDialog(Home_New.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_New.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
               //             AppUtils.alertDialog(Home_New.this, jsonObject.getString("Message"));
                        }
                    } else {
                 //       AppUtils.alertDialog(Home_New.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
               //     e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }
        } catch (Exception e) {
       //     e.printStackTrace();
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
             //   AppUtils.showProgressDialog(Home_New.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_New.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                   //         AppUtils.alertDialog(Home_New.this, jsonObject.getString("Message"));
                        }
                    } else {
                 //       AppUtils.alertDialog(Home_New.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
             //       e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }
        } catch (Exception e) {
       //     e.printStackTrace();
        }
    }
    private void executeToGetImageSlider() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
           //     AppUtils.showProgressDialog(Home_New.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_New.this, postParameters, QueryUtils.methodHomePageSlider, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                 //   AppUtils.dismissProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        getImageSliderResult(jsonObject.getJSONArray("Data"));
                    } else {
                        AppUtils.alertDialog(Home_New.this, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void getImageSliderResult(JSONArray jsonArrayData) {

        try {
            imageSlider.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                final JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Images", jsonObject.getString("Images"));
                imageSlider.add(map);
            }

        //    setImageSlider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}