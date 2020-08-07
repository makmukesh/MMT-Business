package com.vpipl.mmtbusiness;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.WalletTransactionList_Adapter;
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

public class Wallet_Home_Transaction_Report_Activity extends AppCompatActivity {

    public WalletTransactionList_Adapter adapter_shopping;
    public WalletTransactionList_Adapter adapter_recharge;

    String TAG = "Wallet_Home_Transaction_Report_Activity";
    RecyclerView listView_shopping, listView_roi;
    TextView txt_wb_label, txt_awb;

    LinearLayout LL_shopping_wallet, LL_roi_wallet;
    TextView txt_shopping, txt_roi;
    View View_shopping, view_roi;

    double AWB_Shopping = 0.0;
    double AWB_Recharge = 0.0;

    ArrayList<HashMap<String, String>> ShoppingordersList = new ArrayList<>();
    ArrayList<HashMap<String, String>> RechargegordersList = new ArrayList<>();

    ImageView img_menu;
    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_login_logout);

        img_user.setVisibility(View.GONE);

        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_home_transaction_report);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            listView_shopping = (RecyclerView) findViewById(R.id.listView_shopping);
            listView_roi = (RecyclerView) findViewById(R.id.listView_roi);

            txt_wb_label = (TextView) findViewById(R.id.txt_wb_label);
            txt_awb = (TextView) findViewById(R.id.txt_awb);

            LL_shopping_wallet = (LinearLayout) findViewById(R.id.LL_shopping_wallet);
            LL_roi_wallet = (LinearLayout) findViewById(R.id.LL_roi_wallet);

            txt_shopping = (TextView) findViewById(R.id.txt_shopping);
            txt_roi = (TextView) findViewById(R.id.txt_roi);

            View_shopping = (View) findViewById(R.id.View_shopping);
            view_roi = (View) findViewById(R.id.view_roi);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            RecyclerView.LayoutManager mLayoutManagerTwo = new LinearLayoutManager(getApplicationContext());

            listView_shopping.setLayoutManager(mLayoutManager);
            listView_shopping.setItemAnimator(new DefaultItemAnimator());

            listView_roi.setLayoutManager(mLayoutManagerTwo);
            listView_roi.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(Wallet_Home_Transaction_Report_Activity.this)) {

                executeShoppingWalletBalanceRequest();
                executeRechargeWalletBalanceRequest();
                executeGetShoppingWalletDetails();
                executeGetRechargeWalletDetails();

            } else {
                AppUtils.alertDialog(Wallet_Home_Transaction_Report_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }


            LL_roi_wallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDisplayView(1);
                }
            });

            LL_shopping_wallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDisplayView(0);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void executeGetShoppingWalletDetails() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Home_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Home_Transaction_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("TopRows", "1000"));
                            postParameters.add(new BasicNameValuePair("FromJD", ""));
                            postParameters.add(new BasicNameValuePair("ToJD", ""));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Home_Transaction_Report_Activity.this, postParameters, QueryUtils.methodToShoppingWalletTransactionDetail, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() > 0) {
                                    GetShoppingWalletDetailsResult(jsonArrayData);
                                }
                            } else {
                           //     AppUtils.alertDialog(Wallet_Home_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void GetShoppingWalletDetailsResult(JSONArray jsonArray) {
        try {
            ShoppingordersList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("TransDate", "" + AppUtils.getDateandDayFromAPIDate(jsonObject.getString("DeductionDD")));
                map.put("Remarks", "" + jsonObject.getString("Remarks"));
                map.put("Amount", "" + jsonObject.getString("Amount"));

                ShoppingordersList.add(map);
            }

            SetAdapterShoppingListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void SetAdapterShoppingListView() {
        try {
            if (ShoppingordersList.size() > 0) {
                adapter_shopping = new WalletTransactionList_Adapter(Wallet_Home_Transaction_Report_Activity.this, ShoppingordersList);
                listView_shopping.setAdapter(adapter_shopping);
                adapter_shopping.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void executeGetRechargeWalletDetails() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Home_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Home_Transaction_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("TopRows", "1000"));
                            postParameters.add(new BasicNameValuePair("FromJD", ""));
                            postParameters.add(new BasicNameValuePair("ToJD", ""));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Home_Transaction_Report_Activity.this, postParameters, QueryUtils.methodPayoutWalletTransactionDetail, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() > 0) {
                                    GetRechargeWalletDetailsResult(jsonArrayData);
                                }
                            } else {
                            //    AppUtils.alertDialog(Wallet_Home_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void GetRechargeWalletDetailsResult(JSONArray jsonArray) {
        try {
            RechargegordersList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("TransDate", "" + AppUtils.getDateandDayFromAPIDate(jsonObject.getString("DeductionDD")));
                map.put("Remarks", "" + jsonObject.getString("Remarks"));
                map.put("Amount", "" + jsonObject.getString("Amount"));

                RechargegordersList.add(map);
            }

            SetAdapterRechargeListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void SetAdapterRechargeListView() {
        try {
            if (RechargegordersList.size() > 0) {
                adapter_recharge = new WalletTransactionList_Adapter(Wallet_Home_Transaction_Report_Activity.this, RechargegordersList);
                listView_roi.setAdapter(adapter_recharge);
                adapter_recharge.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter_shopping != null)
                adapter_shopping.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
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
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeShoppingWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Home_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(Wallet_Home_Transaction_Report_Activity.this);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Home_Transaction_Report_Activity.this,
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

                                txt_awb.setText("\u20B9 " + jsonArray.getJSONObject(0).getString("WBalance"));

                                AWB_Shopping = Double.parseDouble(jsonArray.getJSONObject(0).getString("WBalance"));


                            } else {
                            //    AppUtils.alertDialog(Wallet_Home_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }

    private void executeRechargeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Home_Transaction_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(Wallet_Home_Transaction_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Home_Transaction_Report_Activity.this,
                                    postParameters, QueryUtils.methodToGetAvailablePayoutWalletBalance, TAG);

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
                                AWB_Recharge = Double.parseDouble(jsonArray.getJSONObject(0).getString("WBalance"));

                            } else {
                             //   AppUtils.alertDialog(Wallet_Home_Transaction_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Home_Transaction_Report_Activity.this);
        }
    }


    public void setDisplayView(int Position) {
        if (Position == 0) {
            txt_shopping.setTextColor(getResources().getColor(R.color.color__bg_orange));
            txt_roi.setTextColor(getResources().getColor(R.color.color_666666));

            View_shopping.setBackgroundColor(getResources().getColor(R.color.color__bg_orange));
            view_roi.setBackgroundColor(getResources().getColor(R.color.color_cccccc));

            listView_roi.setVisibility(View.GONE);
            listView_shopping.setVisibility(View.VISIBLE);

            txt_awb.setText("\u20B9 " + "" + AWB_Shopping);

        } else {
            txt_shopping.setTextColor(getResources().getColor(R.color.color_666666));
            txt_roi.setTextColor(getResources().getColor(R.color.color__bg_orange));

            View_shopping.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            view_roi.setBackgroundColor(getResources().getColor(R.color.color__bg_orange));

            listView_roi.setVisibility(View.VISIBLE);
            listView_shopping.setVisibility(View.GONE);

            txt_awb.setText("\u20B9 " + "" + AWB_Recharge);
        }


    }
}
