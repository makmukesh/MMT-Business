package com.vpipl.mmtbusiness;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.mmtbusiness.Adapters.MyOrdersList_Recharge_Adapter;
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

public class MyOrders_Recharge_Activity extends AppCompatActivity {

    public MyOrdersList_Recharge_Adapter adapter;
    String TAG = "MyOrders_Recharge_Activity";
    RecyclerView listView;
    LinearLayout layout_listView, layout_nodata;
    ArrayList<HashMap<String, String>> ordersList = new ArrayList<>();

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
        setContentView(R.layout.myorders_activity);


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            listView = (RecyclerView) findViewById(R.id.listView);
            layout_listView = (LinearLayout) findViewById(R.id.layout_listView);
            layout_nodata = (LinearLayout) findViewById(R.id.layout_nodata);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(MyOrders_Recharge_Activity.this)) {
                executeGetMyOrdersRequest();
            } else {
                showNoData();
                AppUtils.alertDialogWithFinish(MyOrders_Recharge_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    private void executeGetMyOrdersRequest() {
        try {
            if (AppUtils.isNetworkAvailable(MyOrders_Recharge_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MyOrders_Recharge_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(MyOrders_Recharge_Activity.this, postParameters, QueryUtils.methodToGet_RechargeDetails, TAG);
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
                                    getOrdersListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(MyOrders_Recharge_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    private void getOrdersListResult(JSONArray jsonArray) {
        try {
            ordersList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                map.put("OrderAmt", "" + jsonObject.getString("RAmount"));
                map.put("mobile", "" + jsonObject.getString("RCMobileNo"));
                map.put("operator", "" + jsonObject.getString("ROperator"));

                if (jsonObject.getString("RCStatus").equalsIgnoreCase("Success"))
                    map.put("OrderStatus", "Your order is successfull");
                else if (jsonObject.getString("RCStatus").equalsIgnoreCase("Request Accepted"))
                    map.put("OrderStatus", "Your order is under processing");
                else
                    map.put("OrderStatus", "Your order is Failed");

                map.put("datetime", "" + AppUtils.getDateandTimeFromAPIDate(jsonObject.getString("RDate")));

                ordersList.add(map);
            }

            if (AppUtils.showLogs) Log.v(TAG, "ShoppingordersList..." + ordersList);
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    private void showListView() {
        try {
            if (ordersList.size() > 0) {
                adapter = new MyOrdersList_Recharge_Adapter(MyOrders_Recharge_Activity.this, ordersList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrders_Recharge_Activity.this);
        }
    }


    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(MyOrders_Recharge_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(MyOrders_Recharge_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(MyOrders_Recharge_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(MyOrders_Recharge_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

            executeGetMyOrdersRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
