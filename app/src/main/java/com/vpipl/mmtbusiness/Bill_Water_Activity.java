package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

public class Bill_Water_Activity extends AppCompatActivity {

    private static final String TAG = "Bill_Water_Activity";

    private ArrayList<HashMap<String, String>> ServiceProviderList = new ArrayList<>();

    private Button btn_proceed;

    private String service_type = "WATER";

    private TextInputEditText edtxt_mobileNumber, txt_operator, edtxt_amount;

    TelephonyManager telephonyManager;

    ImageView img_nav_back;
    TextView txt_awb;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        txt_awb = (TextView) findViewById(R.id.txt_awb);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    double AWB = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_water);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        edtxt_mobileNumber = (TextInputEditText) findViewById(R.id.edtxt_mobileNumber);
        edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);
        txt_operator = (TextInputEditText) findViewById(R.id.txt_operator);


        btn_proceed = (Button) findViewById(R.id.btn_proceed);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateData();
            }
        });

        txt_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceProviderList.size() != 0) {
                    showOperatorDialog();
                } else {
                    executeServiceProviderList(service_type);
                }
            }
        });


        executeWalletBalanceRequest();
        executeServiceProviderList(service_type);
    }

    private void showOperatorDialog() {
        try {
            final String[] stateArray = new String[ServiceProviderList.size()];
            for (int i = 0; i < ServiceProviderList.size(); i++) {
                stateArray[i] = WordUtils.capitalizeFully(ServiceProviderList.get(i).get("OperatorName"));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Board");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    txt_operator.setText(stateArray[item]);

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
        }
    }

    private void executeServiceProviderList(final String ServiceType) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Bill_Water_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("Type", ServiceType));
                    response = AppUtils.callWebServiceWithMultiParam(Bill_Water_Activity.this, postParameters, QueryUtils.methodToGetServiceProviderList, TAG);
                } catch (Exception ignored) {

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
                        if (jsonArrayData.length() != 0) {
                            getServiceProviderList(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Bill_Water_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Bill_Water_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

    private void getServiceProviderList(JSONArray jsonArray) {
        try {
            ServiceProviderList.clear();

            txt_operator.setText("");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put("OperatorName", jsonObject.getString("SERVICE_NAME"));
                map.put("OPCode", jsonObject.getString("SERVICE_KEY"));

                ServiceProviderList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {
        String mobile_number = edtxt_mobileNumber.getText().toString();
        String operator = txt_operator.getText().toString();
        String amount = edtxt_amount.getText().toString();


        if (TextUtils.isEmpty(operator)) {
            AppUtils.alertDialog(Bill_Water_Activity.this, "Please Select Board");
        } else  if (TextUtils.isEmpty(mobile_number)) {
            AppUtils.alertDialog(Bill_Water_Activity.this, "Please Enter Consumer Number");
        } else if (TextUtils.isEmpty(amount)) {
            AppUtils.alertDialog(Bill_Water_Activity.this, "Please Enter Amount");
        } else if (Double.parseDouble(amount) <= 0) {
            AppUtils.alertDialog(Bill_Water_Activity.this, "Please Enter Valid Amount");
        } else if (AWB < Double.parseDouble(amount)) {
            AppUtils.alertDialog(Bill_Water_Activity.this, "Insufficient Wallet Balance");
        } else {
            if (AppUtils.isNetworkAvailable(Bill_Water_Activity.this)) {
                showPaymentConfirmationDialog();
            } else {
                AppUtils.alertDialog(Bill_Water_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    public void showPaymentConfirmationDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you confirm to Pay Your Water Bill of " + edtxt_amount.getText().toString() + " through Wallet Amount. Please click on Confirm to proceed ahead."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Confirm");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startRechargeRequest();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Cancel");
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

    private void startRechargeRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Water_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();

                String OperatorCode = "0";
                for (int i = 0; i < ServiceProviderList.size(); i++) {
                    if (txt_operator.getText().toString().equalsIgnoreCase(ServiceProviderList.get(i).get("OperatorName"))) {
                        OperatorCode = ServiceProviderList.get(i).get("OPCode");
                    }
                }

                postParameters.add(new BasicNameValuePair("LoginUserName", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
                postParameters.add(new BasicNameValuePair("IDNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("SessId", "" + AppController.getSPcurrentSession().getString(SPUtils.current_sess, "")));
                postParameters.add(new BasicNameValuePair("RechargeType", "" + service_type));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + edtxt_mobileNumber.getText().toString().trim()));
                postParameters.add(new BasicNameValuePair("OperatorCode", "" + OperatorCode));
                postParameters.add(new BasicNameValuePair("Amount", "" + edtxt_amount.getText().toString().trim()));
                postParameters.add(new BasicNameValuePair("OperatorName", "" + txt_operator.getText().toString()));

                postParameters.add(new BasicNameValuePair("HostIP", telephonyManager.getDeviceId()));
//
                executeRechargeRequest(postParameters);

            } else {
                AppUtils.alertDialog(Bill_Water_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Water_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Bill_Water_Activity.this,
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


                                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                                txt_awb.setText("\u20B9 " + jsonArray.getJSONObject(0).getString("WBalance"));

                                AWB = Double.parseDouble(jsonArray.getJSONObject(0).getString("WBalance"));


                            } else {
                                AppUtils.alertDialog(Bill_Water_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
        }
    }

    private void executeRechargeRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Water_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(Bill_Water_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Bill_Water_Activity.this, postParameters, QueryUtils.methodToDoRecharge, TAG);
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
                                JSONObject jsonObject1 = jsonArrayData.getJSONObject(0);

                                Intent intent = new Intent(Bill_Water_Activity.this, Bill_Success_Activity.class);
                                intent.putExtra("OrderID", jsonObject1.getString("AGENTID"));
                                intent.putExtra("MobileNo", edtxt_mobileNumber.getText().toString());
                                intent.putExtra("amount", edtxt_amount.getText().toString());
                                intent.putExtra("operator", txt_operator.getText().toString());
                                startActivity(intent);
                                finish();

                            } else if (jsonObject.getString("Status").equalsIgnoreCase("API False")) {

                                JSONObject jsonObject1 = jsonArrayData.getJSONObject(0);

                                Intent intent = new Intent(Bill_Water_Activity.this, Bill_Failed_Activity.class);
                                intent.putExtra("OrderID", jsonObject1.getString("AGENTID"));
                                intent.putExtra("MobileNo", edtxt_mobileNumber.getText().toString());
                                intent.putExtra("amount", edtxt_amount.getText().toString());
                                intent.putExtra("operator", txt_operator.getText().toString());
                                startActivity(intent);
                                finish();

                            } else
                                AppUtils.alertDialog(Bill_Water_Activity.this, jsonObject.getString("Message"));

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Water_Activity.this);
        }
    }


}