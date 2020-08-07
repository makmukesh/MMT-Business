package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC14 on 3/21/2016.
 */
public class Profile_View_Activity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> myprofileDetailsList = new ArrayList<>();
    public static String Byte_Code;

    String TAG = "Profile_View_Activity";

    TextView txt_package, txt_memberID, txt_memberName, txt_mobileNumber, txt_email, txt_sponsorID, txt_sponsorName,
            txt_dob,txt_position, txt_phoneNumber, txt_address, txt_city, txt_district, txt_state, txt_pinCode,
            txt_bankName, txt_bankAcntNumber, txt_bankIfsc, txt_bankBranch, txt_PANNumber,
            txt_nomineeName, txt_nomineeRelation, txt_prefix, txt_nominee_dob, txt_father_name;

    Button btn_updateMyProfile;

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
                    startActivity(new Intent(Profile_View_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Profile_View_Activity.this);
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
        setContentView(R.layout.activity_profile_view);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_package = (TextView) findViewById(R.id.txt_package);
            txt_memberID = (TextView) findViewById(R.id.txt_memberID);
            txt_memberName = (TextView) findViewById(R.id.txt_memberName);
            txt_mobileNumber = (TextView) findViewById(R.id.txt_mobileNumber);
            txt_email = (TextView) findViewById(R.id.txt_email);

            txt_prefix = (TextView) findViewById(R.id.txt_prefix);
            txt_father_name = (TextView) findViewById(R.id.txt_father_name);

            txt_dob = (TextView) findViewById(R.id.txt_dob);
            txt_phoneNumber = (TextView) findViewById(R.id.txt_phoneNumber);
            txt_address = (TextView) findViewById(R.id.txt_address);
            txt_city = (TextView) findViewById(R.id.txt_city);
            txt_district = (TextView) findViewById(R.id.txt_district);
            txt_state = (TextView) findViewById(R.id.txt_state);
            txt_pinCode = (TextView) findViewById(R.id.txt_pinCode);
            txt_PANNumber = (TextView) findViewById(R.id.txt_PANNumber);

            txt_sponsorID = (TextView) findViewById(R.id.txt_SponsorID);
            txt_sponsorName = (TextView) findViewById(R.id.txt_SponsorName);
            txt_position =(TextView) findViewById(R.id.txt_position);

            txt_bankName = (TextView) findViewById(R.id.txt_bankName);
            txt_bankAcntNumber = (TextView) findViewById(R.id.txt_bankAcntNumber);
            txt_bankIfsc = (TextView) findViewById(R.id.txt_bankIfsc);
            txt_bankBranch = (TextView) findViewById(R.id.txt_bankBranch);

            txt_nomineeName = (TextView) findViewById(R.id.txt_nomineeName);
            txt_nominee_dob = (TextView) findViewById(R.id.txt_nominee_dob);
            txt_nomineeRelation = (TextView) findViewById(R.id.txt_nomineeRelation);

            btn_updateMyProfile = (Button) findViewById(R.id.btn_updateMyProfile);
            btn_updateMyProfile.setVisibility(View.GONE);

            btn_updateMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Profile_View_Activity.this, Profile_Update_Activity.class));
                        }
                    };
                    new Handler().postDelayed(runnable, 500);
                }
            });

            myprofileDetailsList.clear();

            if (AppUtils.isNetworkAvailable(Profile_View_Activity.this)) {

                if (AppController.stateList.size() == 0) {
                    executeStateRequest();
                } else if (AppController.bankList.size() == 0) {
                    executeBankRequest();
                } else {
                    executeToGetProfileInfo();
                }
            } else {
                AppUtils.alertDialog(Profile_View_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }


    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_View_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_View_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }
            executeBankRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_View_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_View_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }

            executeToGetProfileInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProfileInfo() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_View_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_View_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_View_Activity.this, postParameters, QueryUtils.methodToGetUserProfile, TAG);
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
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Profile_View_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_View_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }
    }

    private void getProfileInfo(JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FIRST_NAME, WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName"))).commit();

            myprofileDetailsList.clear();

            HashMap<String, String> map = new HashMap<>();
            map.put(SPUtils.USER_ID_NUMBER, "" + jsonArray.getJSONObject(0).getString("IDNo"));
            map.put(SPUtils.USER_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName")));
            map.put(SPUtils.USER_FATHER_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemFName")));
            map.put(SPUtils.USER_Relation_Prefix, "" + jsonArray.getJSONObject(0).getString("MemRelation"));
            map.put(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"));
            map.put(SPUtils.USER_PASSWORD, "" + jsonArray.getJSONObject(0).getString("Passw"));
            map.put(SPUtils.USER_ADDRESS, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Address1")));
            map.put(SPUtils.USER_MOBILE_NO, "" + jsonArray.getJSONObject(0).getString("Mobl"));
            map.put(SPUtils.USER_Phone_NO, "" + jsonArray.getJSONObject(0).getString("PhN1"));
            map.put(SPUtils.USER_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")));
            map.put(SPUtils.USER_GENDER, "" + jsonArray.getJSONObject(0).getString("Gen"));
            map.put(SPUtils.USER_EMAIL, "" + jsonArray.getJSONObject(0).getString("Email"));
            map.put(SPUtils.USER_CITY, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("CityName")));

            String StateName = "";
            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("StateCode").equalsIgnoreCase(AppController.stateList.get(i).get("STATECODE"))) {
                    StateName = AppController.stateList.get(i).get("State");
                }
            }
            map.put(SPUtils.USER_STATE, "" + StateName);

            map.put(SPUtils.USER_DISTRICT, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("DistrictName")));
            map.put(SPUtils.USER_PINCODE, "" + jsonArray.getJSONObject(0).getString("Pincode"));
            map.put(SPUtils.USER_PAN, "" + jsonArray.getJSONObject(0).getString("PanNo"));
            map.put(SPUtils.USER_CATEGORY, "" + jsonArray.getJSONObject(0).getString("Category"));
            map.put(SPUtils.USER_SPONSOR_ID, "" + jsonArray.getJSONObject(0).getString("UpLnId"));
            map.put(SPUtils.USER_SPONSOR_NAME, "" + jsonArray.getJSONObject(0).getString("UpLnName"));
            map.put("POSITION", "" + jsonArray.getJSONObject(0).getString("Side"));

            String BankName = "";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("BankID").equalsIgnoreCase(AppController.bankList.get(i).get("BID"))) {
                    BankName = AppController.bankList.get(i).get("Bank");
                }
            }
            map.put(SPUtils.USER_BANKNAME, "" + BankName);
            map.put(SPUtils.USER_BANKACNTNUM, "" + jsonArray.getJSONObject(0).getString("AcNo"));
            map.put(SPUtils.USER_BANKIFSC, "" + jsonArray.getJSONObject(0).getString("IFSCode"));
            map.put(SPUtils.USER_BANKBRANCH, "" + jsonArray.getJSONObject(0).getString("Fld4"));
            map.put(SPUtils.USER_NOMINEE_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("NomineeName")));
            map.put(SPUtils.USER_NOMINEE_RELATION, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Relation")));
            map.put(SPUtils.USER_NOMINEE_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")));
            map.put(SPUtils.USER_PACKAGE, "" + (jsonArray.getJSONObject(0).getString("Category")));

            myprofileDetailsList.add(map);
            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void setProfileDetails() {
        try {

            txt_package.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PACKAGE));
            txt_memberID.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_ID_NUMBER));

            txt_memberName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NAME));

            txt_mobileNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_MOBILE_NO));
            txt_email.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_EMAIL));

            txt_prefix.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix));
            txt_father_name.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_FATHER_NAME));

            txt_dob.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_DOB));
            txt_phoneNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_Phone_NO));
            txt_address.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_ADDRESS));
            txt_city.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_CITY));
            txt_district.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_DISTRICT));
            txt_state.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_STATE));
            txt_pinCode.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PINCODE));
            txt_PANNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PAN));
            txt_position.setText("" + myprofileDetailsList.get(0).get("POSITION"));
            txt_sponsorID.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_SPONSOR_ID));
            txt_sponsorName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_SPONSOR_NAME));

            txt_bankName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKNAME));
            txt_bankAcntNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKACNTNUM));
            txt_bankIfsc.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKIFSC));
            txt_bankBranch.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKBRANCH));

            txt_nomineeName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_NAME));
            txt_nominee_dob.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_DOB));
            txt_nomineeRelation.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_RELATION));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (myprofileDetailsList != null && myprofileDetailsList.size() > 0) {
                setProfileDetails();

            } else {
                if (AppUtils.isNetworkAvailable(Profile_View_Activity.this)) {
                    executeToGetProfileInfo();
                } else {
                    AppUtils.alertDialog(Profile_View_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_View_Activity.this);
        }

        System.gc();
    }
}