package com.vpipl.mmtbusiness;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Send_Query_Activity extends AppCompatActivity {

    private static final String TAG = "Forget_Password_Activity";

    TextInputEditText edtxt_username, edtxt_mobileNumber, edtxt_userEmail, edtxt_user_Query ,txt_subject;
    Button button_send;
    String userid, name, mobile, email, query ,subject;
    TelephonyManager telephonyManager;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);


        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
        img_login_logout.setImageResource(R.drawable.icon_logout_white);

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
                    startActivity(new Intent(Send_Query_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Send_Query_Activity.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_query);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

    //    AppUtils.setActionbarTitle(getSupportActionBar(), Send_Query_Activity.this);


        edtxt_username = (TextInputEditText) findViewById(R.id.edtxt_username);
        edtxt_userEmail = (TextInputEditText) findViewById(R.id.edtxt_userEmail);
        edtxt_mobileNumber = (TextInputEditText) findViewById(R.id.edtxt_mobileNumber);
        edtxt_user_Query = (TextInputEditText) findViewById(R.id.edtxt_user_Query);
        txt_subject = (TextInputEditText) findViewById(R.id.txt_subject);

        button_send = (Button) findViewById(R.id.button_send);

        edtxt_username.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
        edtxt_mobileNumber.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
        edtxt_userEmail.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Send_Query_Activity.this, v);
                ValidateData();
            }
        });
    }

    public void ValidateData() {


        name = edtxt_username.getText().toString();
        mobile = edtxt_mobileNumber.getText().toString();
        email = edtxt_userEmail.getText().toString();
        query = edtxt_user_Query.getText().toString();
        subject = txt_subject.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(mobile)) {
            edtxt_mobileNumber.setError("Please Enter Mobile Number");
            focusView = edtxt_mobileNumber;
            cancel = true;
        } else if (mobile.trim().length() != 10) {
            edtxt_mobileNumber.setError("Invalid Mobile Number");
            focusView = edtxt_mobileNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            edtxt_userEmail.setError("Please Enter Email Address");
            focusView = edtxt_userEmail;
            cancel = true;
        } else if (AppUtils.isValidMail(email)) {
            edtxt_userEmail.setError(getResources().getString(R.string.error_invalid_email));
            focusView = edtxt_userEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(subject)) {
            txt_subject.setError("Please Enter Query");
            focusView = txt_subject;
            cancel = true;
        }else if (TextUtils.isEmpty(query)) {
            edtxt_user_Query.setError("Please Enter Query");
            focusView = edtxt_user_Query;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Send_Query_Activity.this)) {
                executeSendQueryRequest();
            } else {
                AppUtils.alertDialog(Send_Query_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeSendQueryRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Send_Query_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Send_Query_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;

                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Login_IDNo","" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Name", "" +name));
                            postParameters.add(new BasicNameValuePair("Contact_No","" + mobile));
                            postParameters.add(new BasicNameValuePair("Email","" + email));
                            postParameters.add(new BasicNameValuePair("Enquiry","" +query));
                            postParameters.add(new BasicNameValuePair("Subject", "" +subject));
                            response = AppUtils.callWebServiceWithMultiParam(Send_Query_Activity.this, postParameters, QueryUtils.methodSendMailForEnquiry, TAG);

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
                                    AppUtils.alertDialogWithFinish(Send_Query_Activity.this, jobject.getString("Message"));

                                } else {
                                    AppUtils.alertDialog(Send_Query_Activity.this, jobject.getString("Message"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Send_Query_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Send_Query_Activity.this);
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
            AppUtils.showExceptionDialog(Send_Query_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void         ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(Send_Query_Activity.this, true);
        TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(message);

        TextView textView = (TextView) dialog.findViewById(R.id.txt_submit);
        textView.setText("Login");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                AppController.getSpUserInfo().edit().clear().commit();
                AppController.getSpIsLogin().edit().clear().commit();

                Intent intent = new Intent(Send_Query_Activity.this, Login_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}
