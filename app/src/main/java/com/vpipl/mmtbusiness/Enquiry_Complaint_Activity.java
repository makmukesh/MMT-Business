package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Enquiry_Complaint_Activity extends AppCompatActivity {

    private static final String TAG = "Enquiry_Complaint_Activity";
    TextInputEditText txt_mobile, txt_Email, txt_subject, txt_desc;

    Button btn_request;
    String mobile, Email, subject, desc;
    TelephonyManager telephonyManager;

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
                    startActivity(new Intent(Enquiry_Complaint_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Enquiry_Complaint_Activity.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_complaint);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mobile = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");

        txt_mobile = (TextInputEditText) findViewById(R.id.txt_mobile);
        txt_Email = (TextInputEditText) findViewById(R.id.txt_Email);
        txt_subject = (TextInputEditText) findViewById(R.id.txt_subject);
        txt_desc = (TextInputEditText) findViewById(R.id.txt_desc);

        txt_mobile.setText(mobile);

        btn_request = (Button) findViewById(R.id.btn_request);

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    public void ValidateData() {

        mobile = txt_mobile.getText().toString().trim();
        Email = txt_Email.getText().toString().trim();
        subject = txt_subject.getText().toString().trim();
        desc = txt_desc.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mobile)) {
            AppUtils.alertDialog(Enquiry_Complaint_Activity.this, "Please Enter Mobile Number");
            focusView = txt_mobile;
            cancel = true;
        } else if (mobile.length() != 10) {
            AppUtils.alertDialog(Enquiry_Complaint_Activity.this, "Please Enter Valid Mobile Number");
            focusView = txt_mobile;
            cancel = true;
        } else if (!TextUtils.isEmpty(Email) && AppUtils.isValidMail(Email)) {
            AppUtils.alertDialog(Enquiry_Complaint_Activity.this, "Please Enter Valid Email Address");
            focusView = txt_Email;
            cancel = true;
        } else if (TextUtils.isEmpty(subject)) {
            AppUtils.alertDialog(Enquiry_Complaint_Activity.this, "Please Enter Enquiry/Complaint Subject");
            focusView = txt_subject;
            cancel = true;
        } else if (TextUtils.isEmpty(desc)) {
            AppUtils.alertDialog(Enquiry_Complaint_Activity.this, "Please Enter Enquiry/Complaint Description");
            focusView = txt_desc;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Enquiry_Complaint_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Enquiry_Complaint_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }


    private void createRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("IdNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("Name", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
            postParameters.add(new BasicNameValuePair("MobileNo", mobile));
            postParameters.add(new BasicNameValuePair("EmailID", Email));
            postParameters.add(new BasicNameValuePair("Description", desc));
            postParameters.add(new BasicNameValuePair("Subject", subject));

            executeRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Enquiry_Complaint_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Enquiry_Complaint_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Enquiry_Complaint_Activity.this, postParameters, QueryUtils.methodEnquiryandComplaint, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                AppUtils.alertDialogWithFinish(Enquiry_Complaint_Activity.this, jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Enquiry_Complaint_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Enquiry_Complaint_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Enquiry_Complaint_Activity.this);
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
            AppUtils.showExceptionDialog(Enquiry_Complaint_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}