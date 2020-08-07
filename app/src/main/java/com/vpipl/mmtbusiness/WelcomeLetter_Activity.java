package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

/**
 * Created by PC14 on 3/21/2016.
 */
public class WelcomeLetter_Activity extends AppCompatActivity {
    String TAG = "WelcomeLetter_Activity";

    TextView txt_id, txt_name, txt_address, txt_city, txt_pincode, txt_joiningDate,
            txt_sponsorId,txt_placeunderid, txt_package, txt_amount;

    String Form_number;

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
                    startActivity(new Intent(WelcomeLetter_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(WelcomeLetter_Activity.this);
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
        setContentView(R.layout.activity_welcome_letter);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Form_number = getIntent().getStringExtra("Form Number");
            } else
                Form_number = AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");

            txt_id = (TextView) findViewById(R.id.txt_id);
            txt_name = (TextView) findViewById(R.id.txt_name);
            txt_address = (TextView) findViewById(R.id.txt_address);
            txt_city = (TextView) findViewById(R.id.txt_city);
            txt_pincode = (TextView) findViewById(R.id.txt_pincode);
            txt_joiningDate = (TextView) findViewById(R.id.txt_joiningDate);
            txt_sponsorId = (TextView) findViewById(R.id.txt_sponsorId);
            txt_placeunderid=(TextView) findViewById(R.id.txt_placeunderid);

            txt_package = (TextView) findViewById(R.id.txt_package);
            txt_amount = (TextView) findViewById(R.id.txt_amount);


            if (AppUtils.isNetworkAvailable(WelcomeLetter_Activity.this)) {
                executeToGetWelcomeUserInfo();
            } else {
                AppUtils.alertDialog(WelcomeLetter_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
        }
    }

    private void executeToGetWelcomeUserInfo() {
        try {
            if (AppUtils.isNetworkAvailable(WelcomeLetter_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(WelcomeLetter_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", Form_number));
                            response = AppUtils.callWebServiceWithMultiParam(WelcomeLetter_Activity.this, postParameters, QueryUtils.methodWelcomeLetter, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
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
                                    setDetails(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(WelcomeLetter_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(WelcomeLetter_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
        }
    }

    private void setDetails(JSONArray jsonArray) {
        try {
            txt_id.setText("" + jsonArray.getJSONObject(0).getString("IDNo"));
            txt_name.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemFirstName")));
            txt_city.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("City")));
            txt_address.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Address1")));
            txt_pincode.setText("" + jsonArray.getJSONObject(0).getString("Pincode"));
            txt_joiningDate.setText("" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("DOJ")));
            txt_sponsorId.setText("" + jsonArray.getJSONObject(0).getString("RefIdNo"));
            txt_placeunderid.setText(""+ jsonArray.getJSONObject(0).getString("UpLnIdNo"));
            txt_package.setText("" + jsonArray.getJSONObject(0).getString("Category"));
            txt_amount.setText("" + jsonArray.getJSONObject(0).getString("KitAmount"));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
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
            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
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
            AppUtils.showExceptionDialog(WelcomeLetter_Activity.this);
        }

        System.gc();
    }
}
