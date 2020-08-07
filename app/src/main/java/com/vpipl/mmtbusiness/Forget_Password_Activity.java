package com.vpipl.mmtbusiness;

import android.app.Dialog;
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
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Forget_Password_Activity extends AppCompatActivity {

    private static final String TAG = "Forget_Password_Activity";
    TextInputEditText edtxt_userid;
    Button button_submit;
    String userid;
    TelephonyManager telephonyManager;
    TextView txt_login;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_login_logout.setVisibility(View.GONE);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        edtxt_userid = (TextInputEditText) findViewById(R.id.edtxt_userid);
        txt_login = (TextView) findViewById(R.id.txt_login);
        button_submit = (Button) findViewById(R.id.button_submit);


        edtxt_userid.setHint("User ID");


        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Forget_Password_Activity.this, v);
                ValidateData();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Forget_Password_Activity.this, Login_Activity.class);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();

            }
        });
    }

    public void ValidateData() {
        userid = edtxt_userid.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(userid)) {
            AppUtils.alertDialog(Forget_Password_Activity.this, "Please Enter Username.");
            focusView = edtxt_userid;
            cancel = true;
        } else if (userid.trim().length() != 10) {
            AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.error_invalid_user_id));
            focusView = edtxt_userid;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Forget_Password_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        executeForgetRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeForgetRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Forget_Password_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Forget_Password_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;

                        List<NameValuePair> postParameters = new ArrayList<>();
                        postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
                        try {

                            postParameters.add(new BasicNameValuePair("IDNo", userid));
                            postParameters.add(new BasicNameValuePair("Type","Login"));
                            response = AppUtils.callWebServiceWithMultiParam(Forget_Password_Activity.this, postParameters, QueryUtils.methodToForgetPasswordMember, TAG);

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
                                    ShowDialog(message);

                                } else {
                                    AppUtils.alertDialog(Forget_Password_Activity.this, jobject.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Forget_Password_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Forget_Password_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Forget_Password_Activity.this);
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
            AppUtils.showExceptionDialog(Forget_Password_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(Forget_Password_Activity.this, true);
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

                Intent intent = new Intent(Forget_Password_Activity.this, Login_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}
