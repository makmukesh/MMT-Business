package com.vpipl.mmtbusiness;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ExpandableListAdapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.CircularImageView;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.Utils.Utility;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PC14 on 3/22/2016.
 */
public class Pin_Request_Activity extends AppCompatActivity {

    public static DrawerLayout drawer;
    public static NavigationView navigationView;
    String TAG = "Pin_Request_Activity";
    TextInputEditText edtxt_total_pin, edtxt_amount, edtxt_account_no, edtxt_branch_name,
            edtxt_transaction_no, txt_choose_bank, txt_choose_pay_mode;
    Button btn_request;
    String bankArray[];
    String paymodeArray[] = {"Bank", "Cash"};
    TelephonyManager telephonyManager;
    String amount, transaction_no, bank_name, breanch_name, account_number, paymode = "Bank";
    TextView txt_welcome_name, txt_available_wb, txt_id_number;
    CircularImageView profileImage;
    List<EditText> allEds = new ArrayList<EditText>();
    List<TextView> allTvs = new ArrayList<TextView>();
    List<String> kitid = new ArrayList<String>();
    List<String> kitname = new ArrayList<String>();
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    ImageView iv_txn_slip;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    Bitmap bitmap ;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
                    drawer.openDrawer(navigationView);
                }
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Pin_Request_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Pin_Request_Activity.this);
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
        setContentView(R.layout.activity_pin_request_amount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            txt_choose_bank = (TextInputEditText) findViewById(R.id.txt_choose_bank);
            txt_choose_pay_mode = (TextInputEditText) findViewById(R.id.txt_choose_pay_mode);

            edtxt_total_pin = (TextInputEditText) findViewById(R.id.edtxt_total_pin);
            edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);
            edtxt_transaction_no = (TextInputEditText) findViewById(R.id.edtxt_transaction_no);
            edtxt_branch_name = (TextInputEditText) findViewById(R.id.edtxt_branch_name);
            edtxt_account_no = (TextInputEditText) findViewById(R.id.edtxt_account_no);


            iv_txn_slip = (ImageView) findViewById(R.id.iv_txn_slip);
            btn_request = (Button) findViewById(R.id.btn_request);


            txt_choose_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.bankList.size() != 0) {
                            showBankDialog();
                        } else {
                            executeBankRequest();
                        }
                    }
                }
            });

            txt_choose_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                    } else {
                        executeBankRequest();
                    }
                }
            });

            txt_choose_pay_mode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        showpaymodeDialog();
                    }
                }
            });

            txt_choose_pay_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showpaymodeDialog();
                }
            });

            txt_choose_pay_mode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().equalsIgnoreCase("Cash")) {
                        showCashLayout();
                    } else {
                        showBankLayout();
                    }
                }
            });

            iv_txn_slip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Pin_Request_Activity.this, view);
                    if (paymode.equalsIgnoreCase("Cash"))
                        ValidateDataCash();
                    else
                        ValidateData();
                }
            });

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = (TextView) navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = (LinearLayout) navHeaderView.findViewById(R.id.LL_Nav);
            expListView = (ExpandableListView) findViewById(R.id.left_drawer);


            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();


            enableExpandableList();
            LoadNavigationHeaderItems();

            executeLoadPackages();


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    public void showCashLayout() {
        findViewById(R.id.choose_bank).setVisibility(View.GONE);
        findViewById(R.id.tran_number).setVisibility(View.GONE);

        findViewById(R.id.bank_branch).setVisibility(View.GONE);
        findViewById(R.id.account_number).setVisibility(View.GONE);
    }

    public void showBankLayout() {
        findViewById(R.id.choose_bank).setVisibility(View.VISIBLE);
        findViewById(R.id.tran_number).setVisibility(View.VISIBLE);

        findViewById(R.id.bank_branch).setVisibility(View.VISIBLE);
        findViewById(R.id.account_number).setVisibility(View.VISIBLE);
    }

    private void ValidateDataCash() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();

            bank_name = txt_choose_bank.getText().toString().trim();

            account_number = edtxt_account_no.getText().toString().trim();
            breanch_name = edtxt_branch_name.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
//            } else if (TextUtils.isEmpty(bank_name)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Bank");
//                txt_choose_bank.requestFocus();
//            } else if (TextUtils.isEmpty(transaction_no)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Transaction Number is Required");
//                edtxt_transaction_no.requestFocus();
//            } else if (TextUtils.isEmpty(selectedImagePath)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Reference Receipt is Required");
//                btn_choose_file.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmountCash();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void startRequestAmountCash() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("DepositAmount", amount));
                postParameters.add(new BasicNameValuePair("PayMode", "Bank"));
                postParameters.add(new BasicNameValuePair("AccountNo", "0"));
                postParameters.add(new BasicNameValuePair("BranchName", "Cash"));
                postParameters.add(new BasicNameValuePair("BankName", "Cash"));
                postParameters.add(new BasicNameValuePair("TransactionNo", "0"));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
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
                postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
                postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                JSONArray jsonArrayOrder = new JSONArray();

                int[] qtyarr = new int[allEds.size()];
                int[] amtarr = new int[allEds.size()];

                for (int i = 0; i < allEds.size(); i++) {
                    if (allEds.get(i).getText().toString().trim().length() > 0)
                        qtyarr[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                    else
                        qtyarr[i] = 0;

                    if (allTvs.get(i).getText().toString().trim().length() > 0)
                        amtarr[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                    else
                        amtarr[i] = 0;
                }

                for (int i = 0; i < kitid.size(); i++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    jsonObjectDetail.put("KitId", "" + kitid.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("KitName", "" + kitname.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", "" + qtyarr[i]);
                    jsonObjectDetail.put("Amount", amtarr[i]);

                    jsonArrayOrder.put(jsonObjectDetail);
                }

                postParameters.add(new BasicNameValuePair("PackageDetails", jsonArrayOrder.toString().trim()));


                executeRequestAmount(postParameters);

            } else {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        prepareListDataDistributor(listDataHeader, listDataChild);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Generated_issued_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_transfer_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Transfer_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Received Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Received_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-Pin Request Details")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Request_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    startActivity(new Intent(Pin_Request_Activity.this, DashBoard_New.class));
                    finish();
                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild) {

        List<String> Empty = new ArrayList<>();
        try {

            listDataHeader.add("E-pin Detail");
            listDataHeader.add("Generated/Issued Pin Details");
            listDataHeader.add("E-pin Transfer");
            listDataHeader.add("E-pin Transfer Detail");
            listDataHeader.add("E-pin Received Detail");
            listDataHeader.add("E-Pin Request Details");
            listDataHeader.add("Logout");

            listDataChild.put(listDataHeader.get(0), Empty);
            listDataChild.put(listDataHeader.get(1), Empty);
            listDataChild.put(listDataHeader.get(2), Empty);
            listDataChild.put(listDataHeader.get(3), Empty);
            listDataChild.put(listDataHeader.get(4), Empty);
            listDataChild.put(listDataHeader.get(5), Empty);
            listDataChild.put(listDataHeader.get(6), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (bytecode.equalsIgnoreCase(""))
                executeGetProfilePicture();
            else
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();

            bank_name = txt_choose_bank.getText().toString().trim();

            account_number = edtxt_account_no.getText().toString().trim();
            breanch_name = edtxt_branch_name.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(bank_name)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Bank");
                txt_choose_bank.requestFocus();
            } else if (TextUtils.isEmpty(breanch_name)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Branch Name is Required");
                edtxt_branch_name.requestFocus();
            } else if (TextUtils.isEmpty(account_number)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Account Number is Required");
                edtxt_account_no.requestFocus();
            } else if (TextUtils.isEmpty(transaction_no)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Transaction Number is Required");
                edtxt_transaction_no.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void startRequestAmount() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("DepositAmount", amount));
                postParameters.add(new BasicNameValuePair("PayMode", "Bank"));
                postParameters.add(new BasicNameValuePair("AccountNo", "" + account_number));
                postParameters.add(new BasicNameValuePair("BranchName", "" + breanch_name));
                postParameters.add(new BasicNameValuePair("BankName", bank_name));
                postParameters.add(new BasicNameValuePair("TransactionNo", transaction_no));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
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
                postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
                postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                JSONArray jsonArrayOrder = new JSONArray();

                int[] qtyarr = new int[allEds.size()];
                int[] amtarr = new int[allEds.size()];

                for (int i = 0; i < allEds.size(); i++) {
                    if (allEds.get(i).getText().toString().trim().length() > 0)
                        qtyarr[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                    else
                        qtyarr[i] = 0;

                    if (allTvs.get(i).getText().toString().trim().length() > 0)
                        amtarr[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                    else
                        amtarr[i] = 0;
                }

                for (int i = 0; i < kitid.size(); i++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    jsonObjectDetail.put("KitId", "" + kitid.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("KitName", "" + kitname.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", "" + qtyarr[i]);
                    jsonObjectDetail.put("Amount", amtarr[i]);

                    jsonArrayOrder.put(jsonObjectDetail);
                }

                postParameters.add(new BasicNameValuePair("PackageDetails", jsonArrayOrder.toString().trim()));

                executeRequestAmount(postParameters);

//                String Bankid = "0";
//                for (int i = 0; i < AppController.bankList.size(); i++) {
//                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
//                        Bankid = AppController.bankList.get(i).get("BID");
//                    }
//                }

            } else {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_Request_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
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
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
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


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBankDialog() {

        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_bank.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void showpaymodeDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Payment Mode");
            builder.setItems(paymodeArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_pay_mode.setText(paymodeArray[item]);
                    paymode = paymodeArray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeRequestAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            if (paymode.equalsIgnoreCase("Cash"))
                                response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToRequestEpin_Cash, TAG);
                            else
                                response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToRequestEpin, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                AppUtils.alertDialogWithFinish(Pin_Request_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeLoadPackages() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToPinRequestPackage, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                WriteValues(jsonArrayData);
                            } else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArray) {
        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        allEds.clear();
        allTvs.clear();
        kitid.clear();
        kitname.clear();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);

        A1.setText("Kit Name");
        B1.setText("Kit Amount");
        C1.setText("Qty.");
        D1.setText("Amount");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);


        ll.addView(row1);


        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jobject = jsonArray.getJSONObject(i);

                int payout_number = jobject.getInt("KitId");

                String KitName = jobject.getString("KitName");
                final int KitAmount = jobject.getInt("KitAmount");

                kitid.add("" + payout_number);
                kitname.add(KitName);

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0)
                    row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                else row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                TextView A = new TextView(this);
                TextView B = new TextView(this);
                EditText C = new EditText(this);
                final TextView D = new TextView(this);

                A.setText("" + KitName);
                B.setText("" + KitAmount);
                C.setText("0");
                D.setText("0");

                C.setInputType(InputType.TYPE_CLASS_PHONE);
                C.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                C.setId(payout_number);
                D.setId(payout_number);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);

                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                allEds.add(C);
                allTvs.add(D);

                C.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int abc = 0;
                        try {
                            if (s.length() > 0)
                                abc = Math.round(Float.parseFloat(s.toString().trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        D.setText("" + (abc * KitAmount));

                        int[] strings = new int[allEds.size()];

                        for (int i = 0; i < allEds.size(); i++) {
                            if (allEds.get(i).getText().toString().trim().length() > 0)
                                strings[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                            else
                                strings[i] = 0;
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }

                        edtxt_total_pin.setText("" + sum);

                    }
                });

                D.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int[] strings = new int[allTvs.size()];

                        for (int i = 0; i < allTvs.size(); i++) {
                            strings[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }
                        edtxt_amount.setText("" + sum);
                    }
                });

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(getResources().getColor(R.color.app_color_green_one));

                ll.addView(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }

        System.gc();
    }
    /*tranaction slip */
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Pin_Request_Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(Pin_Request_Activity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
       // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      //  executePostImageUploadRequest(bitmap);
        iv_txn_slip.setImageBitmap(bitmap);

        Log.e("from camera data",imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

       // Bitmap bm=null;
        bitmap=null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      //  executePostImageUploadRequest(bm);
        iv_txn_slip.setImageBitmap(bitmap);
        String imagepath = bitmap.toString();
        Log.e("from gallery data",imagepath);
    }

}