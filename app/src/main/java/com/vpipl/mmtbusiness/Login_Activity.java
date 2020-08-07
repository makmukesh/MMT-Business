package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Login_Activity extends AppCompatActivity {

    private static final String TAG = "Login_Activity";

    Button button_login, btn_new_registration;
    TextView txt_forgot_password;
    CheckBox cb_login_rememberMe;

    LinearLayout root_layout;
    private TextInputEditText edtxt_userid_member, edtxt_password_member;


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
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        root_layout = (LinearLayout) findViewById(R.id.root_layout);

        edtxt_userid_member = (TextInputEditText) findViewById(R.id.edtxt_userid_member);
        edtxt_password_member = (TextInputEditText) findViewById(R.id.edtxt_password_member);

        button_login = (Button) findViewById(R.id.button_login);

        cb_login_rememberMe = (CheckBox) findViewById(R.id.cb_login_rememberMe);

        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);

        btn_new_registration = (Button) findViewById(R.id.btn_new_registration);

        if (AppController.getSpRememberUserInfo().getBoolean(SPUtils.IS_REMEMBER_User, false)) {
            cb_login_rememberMe.setChecked(true);

            String useridmember = AppController.getSpRememberUserInfo().getString(SPUtils.IS_REMEMBER_ID_Member, "");

            edtxt_userid_member.setText(useridmember);

        }

        edtxt_password_member.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1234 || id == EditorInfo.IME_NULL) {
                    AppUtils.hideKeyboardOnClick(Login_Activity.this, textView);
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Login_Activity.this, view);
                ValidateData();
            }
        });

        btn_new_registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Login_Activity.this, v);
                MovetoNext(new Intent(Login_Activity.this, Register_Activity.class));
            }
        });

        txt_forgot_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Login_Activity.this, v);
                MovetoNext(new Intent(Login_Activity.this, Forget_Password_Activity.class));
                finish();
            }
        });
    }

    private void ValidateData() {

        String userid;
        String password;

        userid = edtxt_userid_member.getText().toString().trim();
        password = edtxt_password_member.getText().toString().trim();

        if (TextUtils.isEmpty(userid)) {
            edtxt_userid_member.setError("Please Enter Username.");
        } else {
            if (TextUtils.isEmpty(password)) {
                edtxt_password_member.setError(getResources().getString(R.string.error_required_password));
            } else {
                if (AppUtils.isNetworkAvailable(Login_Activity.this)) {
                    executeLoginRequest(userid, password);
                } else {
                    AppUtils.alertDialog(Login_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest(final String userId, final String passwd) {
        try {
            if (AppUtils.isNetworkAvailable(Login_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Login_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", userId));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Login_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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
                                //TODO Different Messages
                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
                                } else {
                                    Snackbar.make(root_layout, jsonObject.getString("Message"), Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(root_layout, jsonObject.getString("Message"), Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Login_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Activity.this);
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
            AppUtils.showExceptionDialog(Login_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

            System.gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().runFinalization();
            System.runFinalization();

            String memberuserid = edtxt_userid_member.getText().toString().trim();

            AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, true).putString(SPUtils.IS_REMEMBER_ID_Member, memberuserid).commit();

            AppController.getSpUserInfo().edit().clear().commit();

                AppController.getSpUserInfo().edit()
                        .putString(SPUtils.USER_TYPE, "DISTRIBUTOR")
                        .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                        .putString(SPUtils.USER_PASSWORD, jsonArray.getJSONObject(0).getString("Passw"))
                        .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                        .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemFirstName"))
                        .putString(SPUtils.USER_LAST_NAME, jsonArray.getJSONObject(0).getString("MemLastName"))
                        .putString(SPUtils.USER_MOBILE_NO, jsonArray.getJSONObject(0).getString("Mobl"))
                        .putString(SPUtils.USER_KIT_NAME, jsonArray.getJSONObject(0).getString("KitName"))
                        .putString(SPUtils.USER_KIT_ID, jsonArray.getJSONObject(0).getString("KitID"))
                        .putString(SPUtils.USER_EMAIL, jsonArray.getJSONObject(0).getString("EMail"))
                        .putString(SPUtils.USER_ACTIVE_STATUS, jsonArray.getJSONObject(0).getString("activestatus"))
                        .putString(SPUtils.USER_DOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("Doj"))).commit();

                MovetoNext(new Intent(Login_Activity.this, Home_New.class));

                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Activity.this);
        }
    }

    private void MovetoNext(Intent intent) {
        try {
            startSplash(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}