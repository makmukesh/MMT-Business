package com.vpipl.mmtbusiness;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.mmtbusiness.Adapters.MyOrdersDetailList_Adapter;
import com.vpipl.mmtbusiness.Adapters.MyOrdersList_Adapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyOrdersDetails_Activity extends AppCompatActivity {
    String TAG = "MyOrdersDetails_Activity";
    RecyclerView listView;
    LinearLayout layout_listView, layout_nodata;

    MyOrdersDetailList_Adapter adapter;
    ArrayList<HashMap<String, String>> ordersDetailList = new ArrayList<>();
    ArrayList<HashMap<String, String>> ordersHeaderandFooterlList = new ArrayList<>();

//    TextView txt_orderNo, txt_orderDate, txt_orderStatus, txt_orderAmount, txt_OrderBV, txt_PayMode;
    LinearLayout lay_bv;

    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);        img_user = (ImageView) findViewById(R.id.img_login_logout);

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
        setContentView(R.layout.myordersdetails_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();
            SetupToolbar();

            listView = (RecyclerView) findViewById(R.id.listView);
            layout_listView = (LinearLayout) findViewById(R.id.layout_listView);
            layout_nodata = (LinearLayout) findViewById(R.id.layout_nodata);

//            txt_orderNo = (TextView) findViewById(R.id.txt_orderNo);
//            txt_orderDate = (TextView) findViewById(R.id.txt_orderDate);
//            txt_orderStatus = (TextView) findViewById(R.id.txt_orderStatus);
//            txt_orderAmount = (TextView) findViewById(R.id.txt_orderAmount);
//            txt_PayMode = (TextView) findViewById(R.id.txt_PayMode);
//            txt_OrderBV = (TextView) findViewById(R.id.txt_OrderBV);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            lay_bv = (LinearLayout) findViewById(R.id.lay_bv);


            if (AppUtils.isNetworkAvailable(MyOrdersDetails_Activity.this)) {
                executeGetMyOrdersDetailsRequest();
            } else {
                showNoData();
                AppUtils.alertDialog(MyOrdersDetails_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            int position = getIntent().getExtras().getInt("position");

//            txt_orderNo.setText(MyOrdersList_Adapter.ShoppingordersList.get(position).get("OrderNo"));
//            txt_orderDate.setText((MyOrdersList_Adapter.ShoppingordersList.get(position).get("ODate")));
//            txt_orderStatus.setText(WordUtils.capitalizeFully(MyOrdersList_Adapter.ShoppingordersList.get(position).get("OrderStatus")));
//            txt_orderAmount.setText("₹ " + MyOrdersList_Adapter.ShoppingordersList.get(position).get("TotalAmount"));
//            txt_PayMode.setText(MyOrdersList_Adapter.ShoppingordersList.get(position).get("PayMode"));

            ordersHeaderandFooterlList.clear();

            HashMap<String, String> map = new HashMap<>();
            map.put("OrderNo", "" + MyOrdersList_Adapter.ordersList.get(position).get("OrderNo"));
            map.put("ODate", "" + MyOrdersList_Adapter.ordersList.get(position).get("ODate"));
            map.put("OrderStatus", "" + WordUtils.capitalizeFully(MyOrdersList_Adapter.ordersList.get(position).get("OrderStatus")));
            map.put("TotalAmount", "₹ " + MyOrdersList_Adapter.ordersList.get(position).get("TotalAmount"));
            map.put("PayMode", MyOrdersList_Adapter.ordersList.get(position).get("PayMode"));
            map.put("Name", MyOrdersList_Adapter.ordersList.get(position).get("Name"));
            map.put("Address1", MyOrdersList_Adapter.ordersList.get(position).get("Address1"));
            map.put("City", MyOrdersList_Adapter.ordersList.get(position).get("City"));
            map.put("PinCode", MyOrdersList_Adapter.ordersList.get(position).get("PinCode"));
            map.put("StateName", MyOrdersList_Adapter.ordersList.get(position).get("StateName"));
            map.put("Email", MyOrdersList_Adapter.ordersList.get(position).get("Email"));
            map.put("Mobl", MyOrdersList_Adapter.ordersList.get(position).get("Mobl"));

            ordersHeaderandFooterlList.add(map);

//            txt_OrderBV.setText("");
//            lay_bv.setVisibility(View.GONE);

//            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
//
//            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
//                lay_bv.setVisibility(View.VISIBLE);
//                txt_OrderBV.setText(MyOrdersList_Adapter.ShoppingordersList.get(getIntent().getExtras().getInt("position")).get("OrderQvp"));
//            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    private void executeGetMyOrdersDetailsRequest() {
        try {
            if (AppUtils.isNetworkAvailable(MyOrdersDetails_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MyOrdersDetails_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderNo", MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo")));
                            response = AppUtils.callWebServiceWithMultiParam(MyOrdersDetails_Activity.this, postParameters, QueryUtils.methodToGetViewOrdersDetails, TAG);
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
                                    getOrdersDetailListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(MyOrdersDetails_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrdersDetailListResult(JSONArray jsonArray) {
        try {
            ordersDetailList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));

                map.put("ProductID", "" + jsonObject.getString("ProductID"));
                map.put("ProductName", "" + jsonObject.getString("ProductName"));
                map.put("Size", "" + jsonObject.getString("Size"));
                map.put("Color", "" + jsonObject.getString("Color"));
                map.put("Qty", "" + jsonObject.getString("Qty"));
                map.put("Netamount", "" + jsonObject.getString("Netamount"));

                String path = jsonObject.getString("ImgPath");
//                if (!path.startsWith(AppUtils.productImageURL()))
//                    path = AppUtils.productImageURL() + path;

                map.put("ImgPath", "" + path);
                map.put("ProdStatus", "" + jsonObject.getString("ProdStatus"));

//                map.put("bv", ""+jsonObject.getString("bv"));
//                map.put("ProdType", ""+jsonObject.getString("ProdType"));
//                map.put("DP", ""+jsonObject.getString("DP"));
//                map.put("CVP", ""+jsonObject.getString("CVP"));
//                map.put("ShipCharges", ""+jsonObject.getString("ShipCharges"));
//                map.put("totalAmt", ""+jsonObject.getString("totalAmt"));

                ordersDetailList.add(map);
            }

            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    private void showListView() {
        try {
            if (ordersDetailList.size() > 0) {
                adapter = new MyOrdersDetailList_Adapter(MyOrdersDetails_Activity.this, ordersDetailList, ordersHeaderandFooterlList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
                AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }
}
