package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ThanksOrderDetail_Adapter;
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

public class ThanksScreenOnline_Activity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
    private String TAG = "Thanks_Activity";
    private LinearLayout layout_noData;
    private LinearLayout layout_listView;
    private Button btn_startShopping;
    private Button btn_shopMore;
    private ListView list_orderDetails;
    private ThanksOrderDetail_Adapter adapter;
    private ViewGroup thankyouHeaderView = null;
    private ViewGroup thankyouFooterView = null;

    private TextView txt_orderNumber;
    private TextView txt_orderDate;
    private TextView txt_orderAmount;
    private TextView txt_deliveryStatus;
    private TextView txt_totalAmount;
    private TextView txt_deliveryCharge;

    private TextView txt_name;
    private TextView txt_address;
    private TextView txt_mobNo;
    private TextView txt_email;

    private LinearLayout lay_bv;


    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);
        img_login_logout.setVisibility(View.GONE);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white_px));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome();
            }
        });
    }

    String ORDERNUMBER = "";
    ArrayList<HashMap<String, String>> ordersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanks_screen_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        layout_listView = (LinearLayout) findViewById(R.id.layout_listView);
        layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
        list_orderDetails = (ListView) findViewById(R.id.list_orderDetails);
        btn_startShopping = (Button) findViewById(R.id.btn_startShopping);

        AppController.selectedProductsList.clear();

        if (getIntent().getExtras() != null) {
            ORDERNUMBER = getIntent().getExtras().getString("ORDERNUMBER");

            if (AppUtils.isNetworkAvailable(ThanksScreenOnline_Activity.this)) {

                executeGetMyOrdersDetailsRequest();

            } else {
                AppUtils.alertDialogWithFinish(ThanksScreenOnline_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } else {
            showNoDataLayout();
        }

        btn_startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToHome();
            }
        });
    }


    private void executeGetMyOrdersDetailsRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ThanksScreenOnline_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ThanksScreenOnline_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderNo", ORDERNUMBER));
                            response = AppUtils.callWebServiceWithMultiParam(ThanksScreenOnline_Activity.this, postParameters, QueryUtils.methodToThanksPage, TAG);
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

                                JSONArray jsonArraordersList = jsonObject.getJSONArray("MainOrder");
                                JSONArray jsonArrayorderDetails = jsonObject.getJSONArray("OrderDetails");

                                getOrdersDetailListResult(jsonArrayorderDetails, jsonArraordersList);
                                cartClearAll();
                            } else {
                                AppUtils.alertDialog(ThanksScreenOnline_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ThanksScreenOnline_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getOrdersDetailListResult(JSONArray jsonArrayorderDetails, JSONArray jsonArraordersList)
    {
        try {
            orderDetailsList.clear();

            for (int i = 0; i < jsonArrayorderDetails.length(); i++)
            {

                JSONObject jsonObject = jsonArrayorderDetails.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("ProductName", "" + jsonObject.getString("ProductName"));
                map.put("Size", "" + jsonObject.getString("Size"));
                map.put("Color", "" + jsonObject.getString("Color"));
                map.put("Quantity", "" + jsonObject.getString("Quantity"));
                map.put("NetAmount", "" + jsonObject.getString("NetAmount"));
                map.put("ImageUrl", "" + jsonObject.getString("ImageUrl"));

                orderDetailsList.add(map);
            }

            ordersList.clear();

            for (int i = 0; i < jsonArraordersList.length(); i++)
            {
                JSONObject jsonObject = jsonArraordersList.getJSONObject(i);

                if (ORDERNUMBER.equalsIgnoreCase(jsonObject.getString("OrderNo")))
                {
                    HashMap<String, String> map = new HashMap<>();

                    map.put("DeliveryAddressName", "" + jsonObject.getString("MemFirstName") + " " + jsonObject.getString("MemLastName"));

                    map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                    map.put("OrderAmt", "" + jsonObject.getString("OrderAmt"));
                    map.put("Name", "" + jsonObject.getString("MemFirstName"));
                    map.put("Address", "" + jsonObject.getString("Address1")+" "+ jsonObject.getString("Address2")+" "+ jsonObject.getString("City"));
                    map.put("PinCode", "" + jsonObject.getString("PinCode"));
                    map.put("Mobl", "" + jsonObject.getString("Mobl"));
                    map.put("Email", "" + jsonObject.getString("EMail"));

                    if (jsonObject.getString("IsConfirm").equalsIgnoreCase("N"))
                        map.put("OrderStatus", "Confirmation Pending");
                    else
                        map.put("OrderStatus", "Confirmed");

                    map.put("OrderVia", "" + jsonObject.getString("OrderThru"));
                    map.put("ODate", "" + jsonObject.getString("OrderDate"));
                    map.put("OrderQvp", "" + jsonObject.getString("OrderCvp"));

                    map.put("Shipping", "" + jsonObject.getString("Shipping"));
                    map.put("ChAmt", "" + jsonObject.getString("ChAmt"));

                    ordersList.add(map);

                    createParametersforsendSMS(jsonObject.getString("ChAmt"), jsonObject.getString("IDNo"));

                    createParametersforsendMail(jsonObject.getString("UserType"),
                            jsonObject.getString("EMail"),
                            jsonObject.getString("MemFirstName") + " " + jsonObject.getString("MemLastName"),
                            jsonObject.getString("IDNo"),
                            jsonObject.getString("UserName"),
                            jsonObject.getString("Passwd")

                    );
                }
            }

            if (ordersList.size() > 0) {
                showOrderListView();
            } else {
                showNoDataLayout();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ThanksScreenOnline_Activity.this);
        }
    }

    private void createParametersforsendSMS(String NetPayable, String IDNo) {

        List<NameValuePair> postParameters = new ArrayList<>();

        postParameters.add(new BasicNameValuePair("Name", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
        postParameters.add(new BasicNameValuePair("OrderNo", ORDERNUMBER));
        postParameters.add(new BasicNameValuePair("Mobl", AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
        postParameters.add(new BasicNameValuePair("OrderAmt", NetPayable));
        postParameters.add(new BasicNameValuePair("IDNo", IDNo));

        executeSendSMS(postParameters);
    }

    private void createParametersforsendMail(String UserType, String EMail, String Name, String IdNo, String UserName, String Passwd) {

        List<NameValuePair> postParameters = new ArrayList<>();

        postParameters.add(new BasicNameValuePair("UserType", UserType));
        postParameters.add(new BasicNameValuePair("EMail", EMail));
        postParameters.add(new BasicNameValuePair("Name", Name));
        postParameters.add(new BasicNameValuePair("IdNo", IdNo));
        postParameters.add(new BasicNameValuePair("UserName", UserName));
        postParameters.add(new BasicNameValuePair("Passwd", Passwd));
        postParameters.add(new BasicNameValuePair("OrderNo", ORDERNUMBER));

        executeSendMail(postParameters);
    }

    private void executeSendSMS(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(ThanksScreenOnline_Activity.this, postParameters, QueryUtils.methodToSendSMSForOrder, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
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

    private void executeSendMail(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(ThanksScreenOnline_Activity.this, postParameters, QueryUtils.methodToSendMailForOrder, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
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


    private void showOrderListView() {
        try {
            layout_listView.setVisibility(View.VISIBLE);
            layout_noData.setVisibility(View.GONE);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thankyouHeaderView = (ViewGroup) inflater.inflate(R.layout.thankyouheader_layout, list_orderDetails, false);
            setHeaderDetails();

            thankyouFooterView = (ViewGroup) inflater.inflate(R.layout.thankyoufooter_layout, list_orderDetails, false);
            setFooterDetails();

            adapter = new ThanksOrderDetail_Adapter(ThanksScreenOnline_Activity.this);
            list_orderDetails.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeaderDetails() {
        try {
            if (thankyouHeaderView != null) {
                txt_orderNumber = thankyouHeaderView.findViewById(R.id.txt_orderNumber);
                txt_orderDate = thankyouHeaderView.findViewById(R.id.txt_orderDate);

                txt_totalAmount = thankyouHeaderView.findViewById(R.id.txt_totalAmount);

                txt_deliveryCharge = thankyouHeaderView.findViewById(R.id.txt_deliveryCharge);
               txt_orderAmount = thankyouHeaderView.findViewById(R.id.txt_orderAmount);

                lay_bv = thankyouHeaderView.findViewById(R.id.lay_bv);

                txt_deliveryStatus = thankyouHeaderView.findViewById(R.id.txt_deliveryStatus);

                btn_shopMore = thankyouHeaderView.findViewById(R.id.btn_shopMore);
                btn_shopMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveToHome();
                    }
                });

                txt_orderNumber.setText("" + ordersList.get(0).get("OrderNo"));
                txt_orderDate.setText("" + AppUtils.getDateFromAPIDate(ordersList.get(0).get("ODate")));

                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

//
                txt_totalAmount.setText(Html.fromHtml("&#8377 " + ordersList.get(0).get("OrderAmt") + "/-"));
                txt_deliveryCharge.setText(Html.fromHtml("&#8377 " + ordersList.get(0).get("Shipping") + "/-"));
                txt_orderAmount.setText(Html.fromHtml("&#8377 " + ordersList.get(0).get("ChAmt") + "/-"));

                txt_deliveryStatus.setText(ordersList.get(0).get("OrderStatus"));

                if (ordersList.get(0).get("OrderStatus").contains("Pending"))
                    txt_deliveryStatus.setTextColor(getResources().getColor(R.color.app_color_red));
                else
                    txt_deliveryStatus.setTextColor(getResources().getColor(R.color.color__bg_orange));

                list_orderDetails.addHeaderView(thankyouHeaderView, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFooterDetails() {
        try {
            if (thankyouFooterView != null) {
                txt_name = thankyouFooterView.findViewById(R.id.txt_name);
                txt_address = thankyouFooterView.findViewById(R.id.txt_address);
                txt_mobNo = thankyouFooterView.findViewById(R.id.txt_mobNo);
                txt_email = thankyouFooterView.findViewById(R.id.txt_email);

                txt_name.setText(WordUtils.capitalizeFully(ordersList.get(0).get("DeliveryAddressName")));
                txt_address.setText(WordUtils.capitalizeFully(ordersList.get(0).get("Address"))+" "+ordersList.get(0).get("PinCode"));
                txt_mobNo.setText("MobileNo : " + WordUtils.capitalizeFully(ordersList.get(0).get("Mobl")));
                txt_email.setText("Email : " + WordUtils.capitalizeFully(ordersList.get(0).get("Email")));

                list_orderDetails.addFooterView(thankyouFooterView, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataLayout() {
        try {
            layout_listView.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToHome() {
        try {
            AppController.selectedProductsList.clear();

            Intent intent = new Intent(ThanksScreenOnline_Activity.this, Home_New.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (AppUtils.showLogs) Log.v(TAG, "onKeyDown key called.....");
            moveToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (AppUtils.showLogs) Log.v(TAG, "onDestroy() called.....");
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ThanksScreenOnline_Activity.this);
        }
    }

    private void cartClearAll() {
        try {
            AppController.selectedProductsList.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

