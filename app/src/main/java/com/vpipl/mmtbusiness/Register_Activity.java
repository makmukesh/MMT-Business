package com.vpipl.mmtbusiness;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.Utils.SmsListener;
import com.vpipl.mmtbusiness.Utils.SmsReceiver;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Register_Activity extends AppCompatActivity {

    private static final String TAG = "Register_Activity";

    String sponsor_id, firstname, dob, address, mobile_number, pan_number, password, sponsor_form_no, sponsor_name, OTP_received_from_server, OTP_entered_by_user;

    boolean accepet_conditions = false;
    TextView txt_terms_conditions, txt_sponsor_name;
    CheckBox cb_accept;
    LinearLayout ll_button_submit;
    Button btn_submit, btn_cancel, btn_register, btn_resend_otp;
    ScrollView scrollView;
    RadioGroup rg_side;
    Calendar myCalendar;
    SimpleDateFormat sdf;

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                txt_select_date.setText(sdf.format(myCalendar.getTime()));
            } else {
                txt_select_date.requestFocus();
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_dob));
            }
        }
    };
    TelephonyManager telephonyManager;
    private TextInputEditText edtxt_sponsor_id, edtxt_firstname, edtxt_address, edtxt_mobile, edtxt_pan_number,
            edtxt_password, txt_select_date, edtxt_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView img_nav_back = findViewById(R.id.img_nav_back);
        ImageView img_login_logout = findViewById(R.id.img_login_logout);
        img_nav_back.setVisibility(View.GONE);
        img_login_logout.setVisibility(View.GONE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppUtils.setActionbarTitle(getSupportActionBar(), Register_Activity.this);

        edtxt_sponsor_id = (TextInputEditText) findViewById(R.id.edtxt_sponsor_id);
        edtxt_firstname = (TextInputEditText) findViewById(R.id.edtxt_firstname);
        edtxt_address = (TextInputEditText) findViewById(R.id.edtxt_address);
        edtxt_mobile = (TextInputEditText) findViewById(R.id.edtxt_mobile);
        edtxt_pan_number = (TextInputEditText) findViewById(R.id.edtxt_pan_number);
        edtxt_password = (TextInputEditText) findViewById(R.id.edtxt_password);
        txt_select_date = (TextInputEditText) findViewById(R.id.txt_select_date);
        edtxt_otp = (TextInputEditText) findViewById(R.id.edtxt_otp);
        txt_sponsor_name = (TextView) findViewById(R.id.txt_sponsor_name);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        rg_side = (RadioGroup) findViewById(R.id.rg_side);
        txt_terms_conditions = (TextView) findViewById(R.id.txt_terms_conditions);

        txt_terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.terms_conditions));
            }
        });

        cb_accept = (CheckBox) findViewById(R.id.cb_accept);

        ll_button_submit = (LinearLayout) findViewById(R.id.ll_button_submit);
        ll_button_submit.setVisibility(View.GONE);

        myCalendar = Calendar.getInstance();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        sdf = new SimpleDateFormat("yyyy MMMM dd");
        txt_select_date.setText(sdf.format(myCalendar.getTime()));

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_resend_otp = (Button) findViewById(R.id.btn_resend_otp);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                ValidateData();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                ValidateDataWithOTP();
            }
        });

        btn_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                ValidateData();
            }
        });

        cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accepet_conditions = b;

                if (cb_accept.isChecked()) {
                    ll_button_submit.setVisibility(View.VISIBLE);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    });
                } else {
                    ll_button_submit.setVisibility(View.GONE);
                }
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
                    executetoCheckSponsorName(edtxt_sponsor_id.getText().toString());
                }
            }
        });

        edtxt_sponsor_id.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));
        sponsor_form_no = AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        sponsor_name = AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "");
        txt_sponsor_name.setText(sponsor_name);
        txt_sponsor_name.setVisibility(View.VISIBLE);

        txt_select_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(Register_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_otp.setText(messageText);
                    }
                } catch (Exception ignored) {
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void ValidateData() {
        sponsor_id = edtxt_sponsor_id.getText().toString();
        firstname = edtxt_firstname.getText().toString();
        dob = txt_select_date.getText().toString();
        address = edtxt_address.getText().toString();
        mobile_number = edtxt_mobile.getText().toString();
        pan_number = edtxt_pan_number.getText().toString();
        password = edtxt_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sponsor_id)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if ((sponsor_id).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(firstname)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_name));
            focusView = edtxt_firstname;
            cancel = true;
        } else if (TextUtils.isEmpty(dob)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_et_mr_date));
            focusView = txt_select_date;
            cancel = true;
        } else if (TextUtils.isEmpty(address)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_et_mr_address));
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if ((mobile_number).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if (!TextUtils.isEmpty(pan_number)) {
            if (!pan_number.matches(AppUtils.mPANPattern)) {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_PANno));
                focusView = edtxt_pan_number;
                cancel = true;
            }
        } else if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        } else if (TextUtils.isEmpty(sponsor_form_no)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        /*if (AppUtils.CheckOTP)
                            executeSendOTP();
                        else
                            createRegistrationRequest();*/
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void ValidateDataWithOTP() {
        sponsor_id = edtxt_sponsor_id.getText().toString();
        firstname = edtxt_firstname.getText().toString();
        dob = txt_select_date.getText().toString();
        address = edtxt_address.getText().toString();
        mobile_number = edtxt_mobile.getText().toString();
        pan_number = edtxt_pan_number.getText().toString();
        password = edtxt_password.getText().toString();
        OTP_entered_by_user = edtxt_otp.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sponsor_id)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if ((sponsor_id).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(firstname)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_name));
            focusView = edtxt_firstname;
            cancel = true;
        } else if (TextUtils.isEmpty(dob)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_et_mr_date));
            focusView = txt_select_date;
            cancel = true;
        } else if (TextUtils.isEmpty(address)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_et_mr_address));
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if ((mobile_number).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if (!TextUtils.isEmpty(pan_number)) {
            if (!pan_number.matches(AppUtils.mPANPattern)) {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_PANno));
                focusView = edtxt_pan_number;
                cancel = true;
            }
        } else if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        } else if (TextUtils.isEmpty(sponsor_form_no)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(OTP_entered_by_user)) {
            AppUtils.alertDialog(Register_Activity.this, "Please Enter OTP");
            focusView = edtxt_otp;
            cancel = true;
        } else if (!OTP_entered_by_user.equalsIgnoreCase(OTP_received_from_server)) {
            AppUtils.alertDialog(Register_Activity.this, "Invalid / Incorrect OTP");
            focusView = edtxt_otp;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            int selectedIdTwo = rg_side.getCheckedRadioButtonId();
            RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
            String view_detail_side = radioButtonTwo.getText().toString();

            String side = "1";

            if (view_detail_side.equalsIgnoreCase("Left"))
                side = "1";
            else if (view_detail_side.equalsIgnoreCase("Right"))
                side = "2";

            postParameters.add(new BasicNameValuePair("SponsorFormNo", "" + sponsor_form_no));
            postParameters.add(new BasicNameValuePair("Name", "" + firstname.trim()));
            postParameters.add(new BasicNameValuePair("DOB", "" + dob));
            postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));
            postParameters.add(new BasicNameValuePair("Leg", "" + side));
            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
            postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number.trim()));
            postParameters.add(new BasicNameValuePair("Password", "" + password));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            postParameters.add(new BasicNameValuePair("DeviceID", "" + telephonyManager.getDeviceId()));
            postParameters.add(new BasicNameValuePair("PageName", "Register_Activity"));

            executeMemberRegistrationRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodtoNewJoining, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONArray JArray = new JSONArray(resultData);
                            JSONObject object = JArray.getJSONObject(0);

                            if (object.length() > 0) {
                                if (object.getString("Status").equalsIgnoreCase("True")) {
                                    MovetoNext(new Intent(Register_Activity.this, WelcomeLetter_Activity.class).putExtra("Form Number", object.getString("formno")));
                                } else {
                                    if (object.getString("Message").contains("System.Data.SqlClient.SqlException"))
                                        AppUtils.alertDialog(Register_Activity.this, object.getString("Message"));
                                        //  AppUtils.alertDialog(Register_Activity.this, "Maximum Downline Limit Reached for this Sponsor,Please Change sponsor Id.");
                                    else
                                        AppUtils.alertDialog(Register_Activity.this, object.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Register_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void executeSendOTP() {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodtoSendOTP, TAG);
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

                            if (jsonObject.getString("Status").equalsIgnoreCase("True"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                String managestock = jsonArray.getJSONObject(0).getString("OTP");

                                OTP_received_from_server = managestock;

                                findViewById(R.id.ll_button_register).setVisibility(View.VISIBLE);
                                findViewById(R.id.ll_button_submit).setVisibility(View.GONE);
                            } else {
                                AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
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
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void executetoCheckSponsorName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SponsorID", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodCheckSponsor, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONArray jsonArrayData = new JSONArray(resultData);
                            JSONObject Jobject = jsonArrayData.getJSONObject(0);

                            setSponsorName(Jobject);


                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    public void setSponsorName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {

                sponsor_form_no = jobject.getString("FormNo");
                sponsor_name = jobject.getString("MemName");
                txt_sponsor_name.setText(sponsor_name);
                txt_sponsor_name.setVisibility(View.VISIBLE);

            } else {
                txt_sponsor_name.setText(jobject.getString("Message"));
                txt_sponsor_name.setVisibility(View.VISIBLE);
                edtxt_sponsor_id.requestFocus();
            }
        } catch (Exception ignored) {

        }
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }
}