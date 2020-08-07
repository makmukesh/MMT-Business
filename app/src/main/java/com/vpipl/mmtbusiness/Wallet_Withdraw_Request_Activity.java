/*
 * Copyright 2019 MMT Business. All rights reserved.
 */

package com.vpipl.mmtbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
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
import java.util.List;


public class Wallet_Withdraw_Request_Activity extends AppCompatActivity {

    private String TAG = "Wallet_Withdraw_Request_Activity";

    private TextInputEditText edtxt_amount;
    private TextInputEditText edittext_remarks;

    private Button btn_request,btn_reset;

    private TelephonyManager telephonyManager;

    private String amount;
    private String remarks;

    TextView txt_awb;
    double AWB = 0.0;

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
                    startActivity(new Intent(Wallet_Withdraw_Request_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Wallet_Withdraw_Request_Activity.this);
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
        setContentView(R.layout.activity_wallet_withdraw_request);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            SetupToolbar();

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            edtxt_amount = findViewById(R.id.edtxt_amount);
            edittext_remarks = findViewById(R.id.edittext_remarks);
            txt_awb = findViewById(R.id.txt_awb);
            btn_reset=findViewById(R.id.btn_reset);
            btn_request = findViewById(R.id.btn_request);

            if (AppUtils.isNetworkAvailable(Wallet_Withdraw_Request_Activity.this)) {
                executeWalletBalanceRequest();
                executeToGetProfileInfo();
            } else {
                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Withdraw_Request_Activity.this, view);
                    ValidateData();
                    //  alertDialogWithFinish(Wallet_Withdraw_Request_Activity.this, "thanks" );
                }
            });
            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtxt_amount.setText("0.00");
                    edittext_remarks.setText(" ");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            remarks = edittext_remarks.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            }/* else if (amt < 10) {
                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, "Invalid Amount ,Amount gretaer then 10 ");
                edtxt_amount.requestFocus();
            }*/ else if (AWB < Double.parseDouble(amount)) {
                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, "Insufficient Wallet Balance");
            } else {
                startRequestWithdrawAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

    private void startRequestWithdrawAmount() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Withdraw_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("Name", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
                postParameters.add(new BasicNameValuePair("ReqAmount", amount));
                postParameters.add(new BasicNameValuePair("ReqRemarks", remarks));
                postParameters.add(new BasicNameValuePair("RequestIP", telephonyManager.getDeviceId()));

                executeRequestWithdrawAmount(postParameters);

            } else {
                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

    private void executeRequestWithdrawAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Withdraw_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Withdraw_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Withdraw_Request_Activity.this, postParameters, QueryUtils.methodToWalletAmountWithdrawRequest, TAG);
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
                             //   AppUtils.alertDialogWithFinish(Wallet_Withdraw_Request_Activity.this, "" + jsonObject.getString("Message"));
                                  alertDialogWithFinish(Wallet_Withdraw_Request_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Withdraw_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Withdraw_Request_Activity.this,
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

                                txt_awb.setText("Payout Wallet Balance : \u20B9 " + jsonArray.getJSONObject(0).getString("WBalance"));

                                AWB = Double.parseDouble(jsonArray.getJSONObject(0).getString("WBalance"));


                            } else {
                                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, jsonObject.getString("Message"));
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

    /*djlfjkskfjkjffkjedfjklfhk;ljsdf;hlsdfkjlds;fsdlfdk;sojfkdlfdioj*/

    public Dialog createDialog(Context context, boolean single) {
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_wallet_withdra_req_one);
        return dialog;
    }

    public void alertDialogWithFinish(final Context context ,String msg) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView txt_req_amt = (TextView) dialog.findViewById(R.id.txt_req_amt);
            TextView txt_req_to = (TextView) dialog.findViewById(R.id.txt_req_to);
            TextView txt_req_bank_name = (TextView) dialog.findViewById(R.id.txt_req_bank_name);
            TextView txt_req_ifsc_code = (TextView) dialog.findViewById(R.id.txt_req_ifsc_code);
            TextView txt_req_branch_name = (TextView) dialog.findViewById(R.id.txt_req_branch_name);
            TextView txt_req_msg = (TextView) dialog.findViewById(R.id.txt_req_msg);

            txt_req_msg.setText(msg);
            txt_req_amt.setText("\u20B9 " + amount);
            txt_req_to.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")+"\n("+ AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")  +")");
            txt_req_bank_name.setText(AppController.getSpUserInfo().getString(SPUtils.USER_BANKNAME, ""));
            txt_req_ifsc_code.setText(AppController.getSpUserInfo().getString(SPUtils.USER_BANKIFSC, ""));
            txt_req_branch_name.setText(AppController.getSpUserInfo().getString(SPUtils.USER_BANKBRANCH, ""));

            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    ((Activity) context).finish();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    private void executeToGetProfileInfo() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Withdraw_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        //    AppUtils.showProgressDialog(Wallet_Withdraw_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Withdraw_Request_Activity.this, postParameters, QueryUtils.methodToGetUserProfile, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            //     AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonArrayData.length() != 0) {
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Wallet_Withdraw_Request_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

    private void getProfileInfo(JSONArray jsonArray) {
        try {
            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_BANKBRANCH, jsonArray.getJSONObject(0).getString("BankBranchName"))
                    .putString(SPUtils.USER_BANKNAME, jsonArray.getJSONObject(0).getString("Bank"))
                    .putString(SPUtils.USER_BANKIFSC, jsonArray.getJSONObject(0).getString("IFSCode"))
                    .putString(SPUtils.USER_BANKACNTNUM, jsonArray.getJSONObject(0).getString("AcNo"))
                    .putString(SPUtils.USER_FIRST_NAME, WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName"))).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Withdraw_Request_Activity.this);
        }
    }

}