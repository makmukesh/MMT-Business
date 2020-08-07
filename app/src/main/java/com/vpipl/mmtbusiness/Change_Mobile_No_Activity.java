package com.vpipl.mmtbusiness;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vpipl.mmtbusiness.SMS.MySMSBroadcastReceiver;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.Utils.SmsListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Change_Mobile_No_Activity extends AppCompatActivity {

    private static final String TAG = "Change_Mobile_No_Activity";
    TextInputEditText edtxt_new_mobile, edtxt_old_mobile;
    Button button_change_mobile ,btn_updateProfileafterotp;
    String mobile, old_mobile, new_mobile ,userenterOTP,rece_otp;
    TelephonyManager telephonyManager;
    EditText ed_otp ;
    LinearLayout ll_update_mobileno_enter_otp ,ll_update_mobileno_enter_data ;
    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_login_logout.setVisibility(View.GONE);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__mobileno);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mobile = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");

        edtxt_new_mobile = (TextInputEditText) findViewById(R.id.edtxt_new_mobileno);
        edtxt_old_mobile = (TextInputEditText) findViewById(R.id.edtxt_old_mobileno);
        ll_update_mobileno_enter_otp =  findViewById(R.id.ll_update_mobileno_enter_otp);
        ll_update_mobileno_enter_data =  findViewById(R.id.ll_update_mobileno_enter_data);
        ed_otp =  findViewById(R.id.ed_otp);
        btn_updateProfileafterotp =  findViewById(R.id.btn_updateProfileafterotp);

        button_change_mobile = (Button) findViewById(R.id.btn_send_otp);

        ll_update_mobileno_enter_otp.setVisibility(View.GONE);
        ll_update_mobileno_enter_data.setVisibility(View.VISIBLE);

        button_change_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Change_Mobile_No_Activity.this, v);
                ValidateData();
            }
        });

        btn_updateProfileafterotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateDataOTP();
            }
        });

        edtxt_old_mobile.setText(AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));

        ed_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() >= 6) {
                    btn_updateProfileafterotp.setVisibility(View.VISIBLE);
                      /*  tv_otp_expired.setVisibility(View.GONE);
                        tv_resend.setVisibility(View.GONE);*/
                } else
                {
                    btn_updateProfileafterotp.setVisibility(View.GONE);
                }
            }
        });

      /*  SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        ed_otp.setText(messageText);
                        ValidateData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
        //        Toast.makeText(Change_Mobile_No_Activity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
          //      Toast.makeText(Change_Mobile_No_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


        MySMSBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        ed_otp.setText(messageText);
                        ValidateData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void ValidateData() {
        old_mobile = edtxt_old_mobile.getText().toString().trim();
        new_mobile = edtxt_new_mobile.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(old_mobile)) {
            AppUtils.alertDialog(Change_Mobile_No_Activity.this, "Please Enter Old Mobile Number");
            focusView = edtxt_old_mobile;
            cancel = true;
        }else if (old_mobile.trim().length() != 10) {
            AppUtils.alertDialog(Change_Mobile_No_Activity.this, "Invalid Old Mobile Number" );
            focusView = edtxt_old_mobile;
            cancel = true;
        } else if (TextUtils.isEmpty(new_mobile)) {
            AppUtils.alertDialog(Change_Mobile_No_Activity.this, "Please Enter New Mobile Number");
            focusView = edtxt_new_mobile;
            cancel = true;
        }else if (new_mobile.trim().length() != 10) {
            AppUtils.alertDialog(Change_Mobile_No_Activity.this, "Invalid New Mobile Number" );
            focusView = edtxt_new_mobile;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Change_Mobile_No_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createSenOTPRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Change_Mobile_No_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createSenOTPRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("OldMobileNo", old_mobile));
            postParameters.add(new BasicNameValuePair("NewMobileNo", new_mobile));
            executeSendOTPRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSendOTPRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Change_Mobile_No_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Change_Mobile_No_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Change_Mobile_No_Activity.this, postParameters, QueryUtils.methodToSendOTPOnChangeMobileNo, TAG);
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

                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                 rece_otp = ""+jsonArrayData.getJSONObject(0).getString("OTP");
                                 ll_update_mobileno_enter_otp.setVisibility(View.VISIBLE);
                                 ll_update_mobileno_enter_data.setVisibility(View.GONE);
                            } else {
                                AppUtils.alertDialog(Change_Mobile_No_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Change_Mobile_No_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Change_Mobile_No_Activity.this);
        }
    }
    public void ValidateDataOTP() {

        userenterOTP = ed_otp.getText().toString();
        //  OTP = "123456" ;
        if (TextUtils.isEmpty(userenterOTP)) {
            ed_otp.setError("OTP is Required");
            ed_otp.requestFocus();
        } else if (!rece_otp.equalsIgnoreCase(userenterOTP)) {
            ed_otp.setError("Invalid OTP");
            ed_otp.requestFocus();
            //   tv_resend.setVisibility(View.VISIBLE);
        } else {
            if (AppUtils.isNetworkAvailable(this))
            {
                createChangeMobilenoRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }
    private void createChangeMobilenoRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("OldMobileNo", old_mobile));
            postParameters.add(new BasicNameValuePair("NewMobileNo", new_mobile));
            postParameters.add(new BasicNameValuePair("EnterOTP", userenterOTP));
            postParameters.add(new BasicNameValuePair("IpAddress", telephonyManager.getDeviceId()));

            executeMobilenoRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void executeMobilenoRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Change_Mobile_No_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Change_Mobile_No_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Change_Mobile_No_Activity.this, postParameters, QueryUtils.methodToChangeMobileNo, TAG);
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
                                AppUtils.alertDialogWithFinish(Change_Mobile_No_Activity.this, jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Change_Mobile_No_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Change_Mobile_No_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Change_Mobile_No_Activity.this);
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
            AppUtils.showExceptionDialog(Change_Mobile_No_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}