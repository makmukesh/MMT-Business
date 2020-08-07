package com.vpipl.mmtbusiness;

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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

public class Transaction_login_Activity extends AppCompatActivity {
    private static final String TAG = "Transaction_login_Activity";

    Button button_login;
    String send_to = "";
    private TextInputEditText edtxt_trans_password;
    TelephonyManager telephonyManager;


    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_login_logout.setVisibility(View.GONE);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_login);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        send_to = getIntent().getStringExtra("SEND_TO");

        edtxt_trans_password = (TextInputEditText) findViewById(R.id.edtxt_trans_password);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        button_login = (Button) findViewById(R.id.button_login);

        edtxt_trans_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1234 || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Transaction_login_Activity.this, view);
                ValidateData();
            }
        });
    }

    private void executeForgetRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Transaction_login_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Transaction_login_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            try {
                                postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
                            } catch (Exception e) {
                                postParameters.add(new BasicNameValuePair("DeviceID", ""));
                                e.printStackTrace();
                            }
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", "Account"));
                            response = AppUtils.callWebServiceWithMultiParam(Transaction_login_Activity.this, postParameters, QueryUtils.methodToForgetPasswordMember, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jobject = new JSONObject(resultData);

                            if (jobject.length() > 0) {
                                if (jobject.getString("Status").equalsIgnoreCase("True")) {
                                    String message = jobject.getString("Message");
                                    AppUtils.alertDialogWithFinish(Transaction_login_Activity.this, "" + message);
                                } else {
                                    AppUtils.alertDialog(Transaction_login_Activity.this, jobject.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Transaction_login_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
        }
    }

    private void ValidateData() {
        edtxt_trans_password.setError(null);

        String password = edtxt_trans_password.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(Transaction_login_Activity.this, getResources().getString(R.string.error_required_password));
            edtxt_trans_password.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Transaction_login_Activity.this)) {

                executeTransLoginRequest(password);

            } else {
                AppUtils.alertDialog(Transaction_login_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeTransLoginRequest(final String passwd) {
        try {

            if (AppUtils.isNetworkAvailable(Transaction_login_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Transaction_login_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            response = AppUtils.callWebServiceWithMultiParam(Transaction_login_Activity.this, postParameters, QueryUtils.methodTransactionLogin, TAG);
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

                                if (jsonArrayData.length() != 0) {
                                    MovetoNext();
                                } else {
                                    AppUtils.alertDialog(Transaction_login_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Transaction_login_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
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
            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void MovetoNext() {
        try {
            Intent intent;

            if (send_to.equalsIgnoreCase("Genereated/Issued Pin Details")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Generated_issued_details_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_details_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Transfer")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_transfer_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Received Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Received_Report_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Transfer Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Transfer_Report_Activity.class);
             } else if (send_to.equalsIgnoreCase("E-Pin Request")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Request_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Request Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Request_Report_Activity.class);
            } else
                intent = new Intent(Transaction_login_Activity.this, DashBoard_New.class);

            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}