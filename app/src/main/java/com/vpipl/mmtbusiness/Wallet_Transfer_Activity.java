package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC14 on 3/22/2016.
 */
public class Wallet_Transfer_Activity extends AppCompatActivity {


    String TAG = "Wallet_Transfer_Activity";
    TextInputEditText edtxt_sponsor_id, edtxt_amount, txt_downlinename, txt_downlineWB;
    Button btn_request, btn_cancel;
    TelephonyManager telephonyManager;
    TextView txt_awb;
    String amount, downlinename_form_no, downlinename_id, downlinename_name, downlinename_WB, availablwalletbalance;

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
                    startActivity(new Intent(Wallet_Transfer_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Wallet_Transfer_Activity.this);
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
        setContentView(R.layout.activity_wallet_transfer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            SetupToolbar();

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            edtxt_sponsor_id = (TextInputEditText) findViewById(R.id.edtxt_sponsor_id);
            txt_downlinename = (TextInputEditText) findViewById(R.id.txt_downlinename);
            txt_downlineWB = (TextInputEditText) findViewById(R.id.txt_downlineWB);
            edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);

            btn_request = (Button) findViewById(R.id.btn_request);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);

            txt_awb = (TextView) findViewById(R.id.txt_awb);

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Transfer_Activity.this, view);
                    ValidateData();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Transfer_Activity.this, view);
                    onBackPressed();
                }
            });

            edtxt_sponsor_id.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String id = edtxt_sponsor_id.getText().toString();
                    if (id.length() == 10) {
                        executetoCheckDownlineMemberName(edtxt_sponsor_id.getText().toString());
                    }

                }
            });

            executeWalletBalanceRequest();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Transfer_Activity.this,
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

                                    txt_awb.setText("" + jsonArrayData.getJSONObject(0).getString("WBalance"));
                                }
                            } else {
                                AppUtils.alertDialog(Wallet_Transfer_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
    }

    public void executetoCheckDownlineMemberName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("LoginIDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("DownlineMemberNo", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Transfer_Activity.this, postParameters, QueryUtils.methodToGetAvailableWalletandCheckDownlineMember, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject Jobject = new JSONObject(resultData);

                            JSONArray JarrayDownLineMembeDetails = Jobject.getJSONArray("DownLineMembeDetails");
                            JSONArray JarrayDownLineWalletBalance = Jobject.getJSONArray("DownLineWalletBalance");
                            JSONArray JarrayLoginMemberWalletBalence = Jobject.getJSONArray("LoginMemberWalletBalence");

                            if (Jobject.getString("Status").equalsIgnoreCase("True")) {
                                setDownlineName(JarrayDownLineMembeDetails, JarrayDownLineWalletBalance, JarrayLoginMemberWalletBalence);
                            } else {
                                AppUtils.alertDialog(Wallet_Transfer_Activity.this, Jobject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
    }

    public void setDownlineName(JSONArray JarrayDownLineMembeDetails, JSONArray JarrayDownLineWalletBalance, JSONArray JarrayLoginMemberWalletBalence) {
        try {
            downlinename_form_no = JarrayDownLineMembeDetails.getJSONObject(0).getString("FormNo");
            downlinename_name = JarrayDownLineMembeDetails.getJSONObject(0).getString("MemName");
            txt_downlinename.setText(downlinename_name);

            downlinename_WB = JarrayDownLineWalletBalance.getJSONObject(0).getString("WBalance");
            txt_downlineWB.setText(downlinename_WB);

            availablwalletbalance = JarrayLoginMemberWalletBalence.getJSONObject(0).getString("WBalance");
            txt_awb.setText(availablwalletbalance);

        } catch (Exception ignored) {

        }
    }

    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString();
            downlinename_id = edtxt_sponsor_id.getText().toString();
            downlinename_name = txt_downlinename.getText().toString();
            downlinename_WB = txt_downlineWB.getText().toString();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }

            if (TextUtils.isEmpty(downlinename_id)) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, getResources().getString(R.string.error_required_downlineID));
                edtxt_sponsor_id.requestFocus();
            } else if ((downlinename_id).length() != 10) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, getResources().getString(R.string.error_invalid_downlineID));
                edtxt_sponsor_id.requestFocus();
            } else if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
            } else if (amt > Float.valueOf(availablwalletbalance)) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, "Insufficient Wallet Balance");
                edtxt_amount.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Wallet_Transfer_Activity.this)) {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startTransferAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
    }

    private void startTransferAmount() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transfer_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("LoginIDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("DownLineIdNo", downlinename_id));
                postParameters.add(new BasicNameValuePair("LoginFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("DownFormNo", downlinename_form_no));
                postParameters.add(new BasicNameValuePair("TransactionAmount", amount));
                postParameters.add(new BasicNameValuePair("IPAdrs", telephonyManager.getDeviceId()));
                executeTransferAmount(postParameters);

            } else {
                AppUtils.alertDialog(Wallet_Transfer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
    }

    private void executeTransferAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Transfer_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Transfer_Activity.this, postParameters, QueryUtils.methodToRequestTransferWalletAmount, TAG);
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
                                AppUtils.alertDialogWithFinish(Wallet_Transfer_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Wallet_Transfer_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
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
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Transfer_Activity.this);
        }

        System.gc();
    }
}