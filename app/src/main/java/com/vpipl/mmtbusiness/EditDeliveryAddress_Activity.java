package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
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


public class EditDeliveryAddress_Activity extends AppCompatActivity {

    String TAG = "EditDeliveryAddress_Activity";

    Button btn_save, btn_cancel;
    TextInputEditText et_firstName, et_lastName, et_address1, et_address2, et_city, et_district, et_state, et_pinCode, et_mobileNumber, et_email;
    TelephonyManager telephonyManager;
    String state[];

    TextView txt_heading;

    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    String ID = "";

    public void SetupToolbar() {

        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);        img_user = (ImageView) findViewById(R.id.img_login_logout);

        img_user.setVisibility(View.GONE);

        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddeliveryaddress_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            executeStateRequest();

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_save = (Button) findViewById(R.id.btn_save);

            et_firstName = (TextInputEditText) findViewById(R.id.et_firstName);
            et_lastName = (TextInputEditText) findViewById(R.id.et_lastName);
            et_address1 = (TextInputEditText) findViewById(R.id.et_address1);
            et_address2 = (TextInputEditText) findViewById(R.id.et_address2);
            et_city = (TextInputEditText) findViewById(R.id.et_city);
            et_district = (TextInputEditText) findViewById(R.id.et_district);
            et_state = (TextInputEditText) findViewById(R.id.et_state);
            et_pinCode = (TextInputEditText) findViewById(R.id.et_pinCode);
            et_mobileNumber = (TextInputEditText) findViewById(R.id.et_mobileNumber);
            et_email = (TextInputEditText) findViewById(R.id.et_email);

            txt_heading = (TextView) findViewById(R.id.txt_heading);

            txt_heading.setText("Edit Address");

            et_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.stateList.size() != 0) {
                        showStateDialog();
                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(EditDeliveryAddress_Activity.this, view);
                    validateAddressRequest();
                }
            });

            setAddressValue();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }

    void setAddressValue() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            int Position = getIntent().getIntExtra("position", 0);

            et_district.setText("");

            et_firstName.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("MemFirstName"));
            et_lastName.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("MemLastName"));
            et_address1.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Address1"));
            et_address2.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Address2"));
            et_city.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("City"));
            et_state.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("StateName"));
            et_pinCode.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("PinCode"));
            et_mobileNumber.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Mobl"));
            et_email.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Email"));


            ID = MyAddressList_Activity.deliveryAddressList.get(Position).get("ID");




            et_mobileNumber.setEnabled(false);
            et_email.setEnabled(false);
        }
    }

    private void validateAddressRequest() {
        try {
            if (et_firstName.getText().toString().isEmpty()) {
                et_firstName.setError("Please Enter First Name");
                et_firstName.requestFocus();
            } else if (et_lastName.getText().toString().isEmpty()) {
                et_lastName.setError("Please Enter Last Name");
                et_lastName.requestFocus();
            } else if (et_address1.getText().toString().isEmpty()) {
                et_address1.setError("Please Enter Billing Address");
                et_address1.requestFocus();
            } else if (et_pinCode.getText().toString().isEmpty()) {
                et_pinCode.setError("Please Enter PinCode");
                et_pinCode.requestFocus();
            } else if (!et_pinCode.getText().toString().matches(AppUtils.mPINCodePattern)) {
                et_pinCode.setError(getResources().getString(R.string.error_et_mr_PINno));
                et_pinCode.requestFocus();
            } else if (et_state.getText().toString().isEmpty()) {
                et_state.setError("Please Select State");
                et_state.requestFocus();
            } else if (et_city.getText().toString().isEmpty()) {
                et_city.setError("Please Enter City");
                et_city.requestFocus();
//            } else if (et_mobileNumber.getText().toString().isEmpty()) {
//                et_mobileNumber.setError(getResources().getString(R.string.error_required_mobile_number));
//                et_mobileNumber.requestFocus();
//            } else if (et_mobileNumber.getText().toString().length() != 10) {
//                et_mobileNumber.setError(getResources().getString(R.string.error_et_mr_mobileNumber1));
//                et_mobileNumber.requestFocus();
//            } else if (!TextUtils.isEmpty(et_email.getText().toString()) && AppUtils.isValidMail(et_email.getText().toString())) {
//                et_email.setError("Please Enter Valid Email Address");
//                et_email.requestFocus();
            } else {
                startAddressRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }

    private void startAddressRequest() {
        try {
            if (AppUtils.isNetworkAvailable(EditDeliveryAddress_Activity.this)) {
                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("ID", ID));

                postParameters.add(new BasicNameValuePair("FirstName", et_firstName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("LastName", et_lastName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address1", et_address1.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address2", et_address2.getText().toString().trim().replace(",", " ")));

                String stateCode = "0";
                for (int i = 0; i < AppController.stateList.size(); i++) {
                    if (et_state.getText().toString().equalsIgnoreCase(AppController.stateList.get(i).get("State"))) {
                        stateCode = AppController.stateList.get(i).get("STATECODE");
                    }
                }

                postParameters.add(new BasicNameValuePair("StateCode", "" + stateCode.trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("District", "0"));
                postParameters.add(new BasicNameValuePair("City", et_city.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("PinCode", et_pinCode.getText().toString().trim().replace(",", " ")));

//                postParameters.add(new BasicNameValuePair("MobileNo", et_mobileNumber.getText().toString().trim().replace(",", " ")));
//                postParameters.add(new BasicNameValuePair("Email", et_email.getText().toString().trim().replace(",", " ")));

//                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
//                if (Usertype.equalsIgnoreCase("CUSTOMER"))
//                    postParameters.add(new BasicNameValuePair("UserType", "N"));
//                else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
//                    postParameters.add(new BasicNameValuePair("UserType", "D"));
//                else
//                    postParameters.add(new BasicNameValuePair("UserType", "N"));


                executeAddAddressRequest(postParameters);
            } else {
                AppUtils.alertDialog(EditDeliveryAddress_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }

    private void executeAddAddressRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(EditDeliveryAddress_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(EditDeliveryAddress_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(EditDeliveryAddress_Activity.this, postParameters, QueryUtils.methodToCheckOutEditAddress, TAG);
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
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(EditDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(EditDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {
            MyAddressList_Activity.deliveryAddressList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemFirstName")));
                map.put("MemLastName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemLastName")));
                map.put("Address1", "" + WordUtils.capitalizeFully(jsonObject.getString("Address1")));
                map.put("Address2", "" + WordUtils.capitalizeFully(jsonObject.getString("Address2")));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + WordUtils.capitalizeFully(jsonObject.getString("CountryName")));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + WordUtils.capitalizeFully(jsonObject.getString("StateName")));
                map.put("District", "" + WordUtils.capitalizeFully(jsonObject.getString("District")));
                map.put("City", "" + WordUtils.capitalizeFully(jsonObject.getString("City")));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + WordUtils.capitalizeFully(jsonObject.getString("Address").replace("&nbsp;", " ")));
                MyAddressList_Activity.deliveryAddressList.add(map);
            }

            final Dialog dialog = AppUtils.createDialog(EditDeliveryAddress_Activity.this, true);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Your delivery address is Updated successfully!!!");
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                    AppUtils.dismissProgressDialog();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(EditDeliveryAddress_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(EditDeliveryAddress_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                AppUtils.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(EditDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(EditDeliveryAddress_Activity.this, jsonObject.getString("Message"));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            state = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                state[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(state, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    et_state.setText(state[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
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
            AppUtils.showExceptionDialog(EditDeliveryAddress_Activity.this);
        }
    }
}
