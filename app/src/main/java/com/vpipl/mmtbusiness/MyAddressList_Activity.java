package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.MyAddressList_Adapter;
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

public class MyAddressList_Activity extends AppCompatActivity {
    String TAG = "MyAddressList_Activity";

    TextView txt_addAddress, txt_addressCount;
    LinearLayout layout_addressList, layout_noData;
    ListView list_address;
    MyAddressList_Adapter adapter;

    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    public static ArrayList<HashMap<String, String>> deliveryAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changedeliveryaddress_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_addAddress = (TextView) findViewById(R.id.txt_addAddress);
            txt_addressCount = (TextView) findViewById(R.id.txt_addressCount);
            layout_addressList = (LinearLayout) findViewById(R.id.layout_addressList);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_address = (ListView) findViewById(R.id.list_address);

            txt_addAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyAddressList_Activity.this, AddDeliveryAddress_Activity.class);
                    intent.putExtra("ComesFrom", "MyAddressList_Activity");
                    startActivity(intent);
                }
            });

            executeToGetAddressesList();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAddressList_Activity.this);
        }
    }

    private void executeToGetAddressesList() {
        try {
            if (AppUtils.isNetworkAvailable(MyAddressList_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MyAddressList_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();

                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(MyAddressList_Activity.this, postParameters, QueryUtils.methodToGetCheckOutDeliveryAddress, TAG);
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
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else
                                    layout_noData.setVisibility(View.VISIBLE);
                            } else {
                                AppUtils.alertDialog(MyAddressList_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MyAddressList_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(MyAddressList_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAddressList_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {

            deliveryAddressList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + jsonObject.getString("MemFirstName"));
                map.put("MemLastName", "" + jsonObject.getString("MemLastName"));
                map.put("Address1", "" + jsonObject.getString("Address1"));
                map.put("Address2", "" + jsonObject.getString("Address2"));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + jsonObject.getString("CountryName"));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + jsonObject.getString("StateName"));
                map.put("District", "" + jsonObject.getString("District"));
                map.put("City", "" + jsonObject.getString("City"));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + jsonObject.getString("Address").replace("&nbsp;", " "));

                deliveryAddressList.add(map);
            }

            txt_addressCount.setText(deliveryAddressList.size() + " Saved Addresses");

            adapter = new MyAddressList_Adapter(MyAddressList_Activity.this, deliveryAddressList);
            list_address.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            list_address.setVisibility(View.VISIBLE);
            layout_addressList.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();

            executeToGetAddressesList();

            txt_addressCount.setText(deliveryAddressList.size() + " Saved Addresses");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();

            executeToGetAddressesList();

            txt_addressCount.setText(deliveryAddressList.size() + " Saved Addresses");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

}
