/*
 * Copyright 2019 MMT Business. All rights reserved.
 */

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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
 * Created by PC14 on 3/22/2016.
 */
public class Bill_Entry_Activity extends AppCompatActivity {

    String TAG = "Bill_Entry_Activity";

    TextInputEditText edtxt_franchisee_code;
    TextInputEditText edtxt_amount;
    TextInputEditText edtxt_remark, edtxt_scan_qr;

    TextView edtxt_franchisee_name;

    public ArrayList<HashMap<String, String>> FranchiseeList = new ArrayList<>();

    Button btn_submit2, btn_verify;

    String bill_number = "0", amount, remark, mobile, FCode, OTP;

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
                    startActivity(new Intent(Bill_Entry_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Bill_Entry_Activity.this);
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
        setContentView(R.layout.activity_bill_entry);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            edtxt_scan_qr = (TextInputEditText) findViewById(R.id.edtxt_scan_qr);
            edtxt_franchisee_code = (TextInputEditText) findViewById(R.id.edtxt_franchisee_code);
            edtxt_franchisee_name = (TextView) findViewById(R.id.edtxt_franchisee_name);
            edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);
            edtxt_remark = (TextInputEditText) findViewById(R.id.edtxt_remark);

            btn_submit2 = (Button) findViewById(R.id.btn_submit2);
            btn_verify = (Button) findViewById(R.id.btn_verify);

            btn_submit2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Bill_Entry_Activity.this, view);

                    if (TextUtils.isEmpty(edtxt_remark.getText().toString())) {
                        edtxt_remark.setError("Enter OTP");
                        edtxt_remark.requestFocus();
                    } else if (edtxt_remark.getText().toString().trim().length() != 6) {
                        edtxt_remark.setError("Invalid OTP");
                        edtxt_remark.requestFocus();
                    } else if (!edtxt_remark.getText().toString().trim().equalsIgnoreCase(OTP)) {
                        edtxt_remark.setError("Invalid OTP");
                        edtxt_remark.requestFocus();
                    } else
                        btn_submit2.setVisibility(View.GONE);

                        startUpdateRequest();

                }
            });

            btn_verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Bill_Entry_Activity.this, view);
                    startOTPRequest();
                }
            });


            edtxt_amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.toString().length() > 0) {
                        Double amount = Double.parseDouble(s.toString());

                        if (amount > 0) {
                            edtxt_franchisee_code.setEnabled(true);
                            edtxt_scan_qr.setEnabled(true);
                        } else {
                            edtxt_franchisee_code.setEnabled(false);
                            edtxt_scan_qr.setEnabled(false);
                        }
                    } else {
                        edtxt_franchisee_code.setEnabled(false);
                        edtxt_scan_qr.setEnabled(false);
                    }
                }
            });

            edtxt_franchisee_code.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (edtxt_franchisee_code.getText().toString().trim().length() == 10) {
                        executeValidateFranchisee(edtxt_franchisee_code.getText().toString().trim());
                    } else
                    {
                        edtxt_franchisee_name.setText("");
                        mobile = "";
                        FCode = "";
                        btn_verify.setVisibility(View.GONE);
                        edtxt_franchisee_name.setVisibility(View.GONE);
                    }

                }
            });

            edtxt_scan_qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readQr(v);
                }
            });

//            findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

//                    amount = edtxt_amount.getText().toString();
//
//                    if (TextUtils.isEmpty(amount)) {
//                        edtxt_amount.setError("Amount is Required");
//                        edtxt_amount.requestFocus();
//                    } else if (Float.parseFloat(amount) <= 0) {
//                        edtxt_amount.setError("Amount should be grater than zero");
//                        edtxt_amount.requestFocus();
//                    } else {
//                        edtxt_franchisee_code.setVisibility(View.VISIBLE);
//                    }
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void executeFranchiseeList() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Bill_Entry_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Bill_Entry_Activity.this, postParameters, QueryUtils.methodToFranchiseeList, TAG);
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
                            getFranchiseeResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeValidateFranchisee(final String FranchiseeCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Bill_Entry_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("FranchiseeCode", FranchiseeCode));
                    response = AppUtils.callWebServiceWithMultiParam(Bill_Entry_Activity.this, postParameters, QueryUtils.methodCheckFranchiseeCode, TAG);
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
                            getFranchiseeValidateResult(jsonArrayData);
                        } else {
                            edtxt_franchisee_name.setText("");
                            mobile = "";
                            FCode = "";
                            btn_verify.setVisibility(View.GONE);
                            edtxt_franchisee_name.setVisibility(View.GONE);
                            AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        edtxt_franchisee_name.setText("");
                        btn_verify.setVisibility(View.GONE);
                        edtxt_franchisee_name.setVisibility(View.GONE);
                        mobile = "";
                        FCode = "";
                        AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getFranchiseeValidateResult(JSONArray jsonArray) {
        try {

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            edtxt_franchisee_name.setText(jsonObject.getString("PartyName"));
            mobile = jsonObject.getString("MobileNo");
            FCode = jsonObject.getString("UserPartyCode");
            btn_verify.setVisibility(View.VISIBLE);
            edtxt_franchisee_name.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFranchiseeResult(JSONArray jsonArray) {
        try {
            FranchiseeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("UserPartyCode", jsonObject.getString("UserPartyCode"));
                map.put("PartyName", WordUtils.capitalizeFully(jsonObject.getString("PartyName")));

                FranchiseeList.add(map);
            }

            showStateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            final String[] stateArray = new String[FranchiseeList.size()];
            for (int i = 0; i < FranchiseeList.size(); i++) {
                stateArray[i] = FranchiseeList.get(i).get("PartyName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Franchisee");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    edtxt_franchisee_code.setText(stateArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void validateUpdateProfileRequest() {
        try {

            amount = edtxt_amount.getText().toString();
            remark = edtxt_remark.getText().toString();

            if (TextUtils.isEmpty(amount)) {
                edtxt_amount.setError("Amount is Required");
                edtxt_amount.requestFocus();
            } else if (Float.parseFloat(amount) <= 0) {
                edtxt_amount.setError("Amount should be grater than zero");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(FCode)) {
                edtxt_franchisee_code.setError("Enter/Scan Franchisee Code");
                edtxt_franchisee_code.requestFocus();
            } else if (TextUtils.isEmpty(mobile)) {
                edtxt_franchisee_code.setError("Enter Franchisee Code");
                edtxt_franchisee_code.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Bill_Entry_Activity.this)) {
                AppUtils.alertDialog(Bill_Entry_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startOTPRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void startOTPRequest() {
        try {

            amount = edtxt_amount.getText().toString();

            if (TextUtils.isEmpty(amount)) {
                edtxt_amount.setError("Amount is Required");
                edtxt_amount.requestFocus();
            } else if (Float.parseFloat(amount) <= 0) {
                edtxt_amount.setError("Amount should be grater than zero");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(edtxt_franchisee_code.getText().toString().trim()) && TextUtils.isEmpty(edtxt_scan_qr.getText().toString().trim())) {
                AppUtils.alertDialog(Bill_Entry_Activity.this, "Enter or Scan Franchisee Code");
            } else if (TextUtils.isEmpty(mobile)) {
                AppUtils.alertDialog(Bill_Entry_Activity.this, "Enter or Scan Franchisee Code");
            } else {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("MemberName", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile));
                postParameters.add(new BasicNameValuePair("Amount", "" + amount));

                if (AppUtils.isNetworkAvailable(Bill_Entry_Activity.this)) {
                    executesendOTPRequest(postParameters);
                } else {
                    AppUtils.alertDialog(Bill_Entry_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void startUpdateRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Entry_Activity.this)) {

//                String Bankid = "0";
//                for (int i = 0; i < FranchiseeList.size(); i++) {
//                    if (edtxt_franchisee_code.getText().toString().equalsIgnoreCase(FranchiseeList.get(i).get("PartyName"))) {
//                        Bankid = FranchiseeList.get(i).get("UserPartyCode");
//                    }
//                }
                List<NameValuePair> postParameters = new ArrayList<>();
//                postParameters.add(new BasicNameValuePair("FormNo", "123456"));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("FCode", "" + FCode));
                postParameters.add(new BasicNameValuePair("BillNo", "" + bill_number));
                postParameters.add(new BasicNameValuePair("Amount", "" + amount));
                postParameters.add(new BasicNameValuePair("OTP", "" + OTP));

                executeUpdateprofileRequest(postParameters);
            } else {
                AppUtils.alertDialog(Bill_Entry_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void executesendOTPRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Entry_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Bill_Entry_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Bill_Entry_Activity.this, postParameters, QueryUtils.methodSendOTPApprovePruchaseBill, TAG);
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
                                OTP = jsonArrayData.getJSONObject(0).getString("OTP");
                                Log.e("OTP Code for  bill" , OTP) ;
                                AppUtils.alertDialog(Bill_Entry_Activity.this, "An OTP has been Sent to Franchisee's Mobile, Enter OTP to continue");
                                edtxt_remark.setVisibility(View.VISIBLE);
                                btn_submit2.setVisibility(View.VISIBLE);

                                edtxt_amount.setEnabled(false);
                                edtxt_scan_qr.setEnabled(false);
                                edtxt_franchisee_code.setEnabled(false);

                            } else {
                                AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                                edtxt_remark.setVisibility(View.GONE);
                                btn_submit2.setVisibility(View.GONE);

                                edtxt_amount.setEnabled(true);
                                edtxt_scan_qr.setEnabled(true);
                                edtxt_franchisee_code.setEnabled(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
    }

    private void executeUpdateprofileRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Bill_Entry_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Bill_Entry_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Bill_Entry_Activity.this, postParameters, QueryUtils.methodToAddPurchaseBill, TAG);
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
                                alertDialogWithFinish(Bill_Entry_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Bill_Entry_Activity.this, jsonObject.getString("Message"));
                                btn_submit2.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
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
            AppUtils.showExceptionDialog(Bill_Entry_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void readQr(View view) {

        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "Franchisee Code Not Found", Toast.LENGTH_LONG).show();
                edtxt_scan_qr.setText("");
                edtxt_scan_qr.requestFocus();
                btn_verify.setVisibility(View.GONE);
            } else {
                edtxt_scan_qr.setText(result.getContents());
                edtxt_scan_qr.requestFocus();
                executeValidateFranchisee(edtxt_scan_qr.getText().toString().trim());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void generateQr(View view) {
      //  Intent intent = new Intent(this, QrGenerate.class);
      //  startActivity(intent);
    }

    public void alertDialogWithFinish(final Context context, String message) {
        try {
            final Dialog dialog = AppUtils.createDialog(context, true);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(new Intent(Bill_Entry_Activity.this, Bill_Report_Activity.class));
                    finish();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}