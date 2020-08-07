package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ExpandableListAdapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.CircularImageView;
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
import java.util.Map;

public class Pin_transfer_Activity extends AppCompatActivity {
    private static final String TAG = "Topup_member_Activity";
    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();
    Button btn_confirm_topup;
    private TextInputEditText edtxt_id_number, edtxt_package, edtxt_quantity, edtxt_memberName;

    private String sponsor_form_no, sponsor_name, package_name, quantity;

    private String[] PackageArray;
    private String idnumber;

    public DrawerLayout drawer;
    public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;
    private CircularImageView profileImage;

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
                    startActivity(new Intent(Pin_transfer_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Pin_transfer_Activity.this);
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
        setContentView(R.layout.activity_pin_transfer);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_id_number = (TextInputEditText) findViewById(R.id.edtxt_id_number);
        edtxt_package = (TextInputEditText) findViewById(R.id.edtxt_package);
        edtxt_quantity = (TextInputEditText) findViewById(R.id.edtxt_quantity);
        edtxt_memberName = (TextInputEditText) findViewById(R.id.edtxt_memberName);

        btn_confirm_topup = (Button) findViewById(R.id.btn_confirm_topup);

        btn_confirm_topup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Pin_transfer_Activity.this, view);
                ValidateData();
            }
        });

        edtxt_id_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String id = edtxt_id_number.getText().toString().trim();
                if (id.length() == 10) {
                    executetoCheckMemberName(edtxt_id_number.getText().toString().trim());
                }
            }
        });


        edtxt_package.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (PackageList.size() != 0) {
                        showPackageDialog();
                        edtxt_package.clearFocus();
                    } else {
                        executeLoadPackage();
                    }
                }
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.getHeaderView(0);
        txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);
        txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
        profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);


        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void executeLoadPackage() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_transfer_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                    response = AppUtils.callWebServiceWithMultiParam(Pin_transfer_Activity.this, postParameters, QueryUtils.EPinTransferLoadPackage, TAG);
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
                            getPackageResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Pin_transfer_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_transfer_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPackageResult(JSONArray jsonArray) {
        try {
            PackageList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put("KitID", jsonObject.getString("KitID"));
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("KitName")));

                PackageList.add(map);
            }

            showPackageDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPackageDialog() {
        try {
            PackageArray = new String[PackageList.size()];

            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Package");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    edtxt_package.setText(PackageArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
        }
    }

    public void executetoCheckMemberName(final String IDNo) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", "" + IDNo));
                            response = AppUtils.callWebServiceWithMultiParam(Pin_transfer_Activity.this, postParameters, QueryUtils.CheckEPinTransferMember, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONObject Jobject = new JSONObject(resultData);
                            setMemberName(Jobject);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Pin_transfer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
        }
    }

    public void setMemberName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {

                JSONArray jsonArrayData = jobject.getJSONArray("Data");

                sponsor_form_no = jsonArrayData.getJSONObject(0).getString("Formno");
                sponsor_name = jsonArrayData.getJSONObject(0).getString("MemName");

                edtxt_memberName.setText(sponsor_name);

            } else {
                edtxt_id_number.setError(jobject.getString("Message"));
            }
        } catch (Exception ignored) {
        }
    }

    private void ValidateData() {
        edtxt_id_number.setError(null);

        idnumber = edtxt_id_number.getText().toString().trim();
        quantity = edtxt_quantity.getText().toString().trim();
        package_name = edtxt_package.getText().toString().trim();

        if (TextUtils.isEmpty(idnumber)) {
            AppUtils.alertDialog(Pin_transfer_Activity.this, "Please Enter ID Number");
            edtxt_id_number.requestFocus();
        } else {
            if ((idnumber.trim()).length() != 10) {
                AppUtils.alertDialog(Pin_transfer_Activity.this, "Invalid ID Number");
                edtxt_id_number.requestFocus();
            } else if (TextUtils.isEmpty(quantity)) {
                AppUtils.alertDialog(Pin_transfer_Activity.this, "Please Enter Quantity");
                edtxt_quantity.requestFocus();
            } else if (Integer.parseInt(quantity) <= 0) {
                AppUtils.alertDialog(Pin_transfer_Activity.this, "Invalid Quantity");
                edtxt_quantity.requestFocus();
            } else {
                if (AppUtils.isNetworkAvailable(Pin_transfer_Activity.this)) {
                    showPaymentConfirmationDialog();
                } else {
                    AppUtils.alertDialog(Pin_transfer_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        }
    }
    public void showPaymentConfirmationDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you confirm to Pin Transfer of " + idnumber + " . Please click on Confirm to proceed ahead."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Confirm");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        executePinTransfer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Cancel");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executePinTransfer() {
        try {

            if (AppUtils.isNetworkAvailable(Pin_transfer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_transfer_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("LoginIDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("IDNo", idnumber));
                            postParameters.add(new BasicNameValuePair("PinQty", quantity));

                            String PackageID = "0";
                            for (int i = 0; i < PackageList.size(); i++) {
                                if (package_name.equals(PackageList.get(i).get("KitName"))) {
                                    PackageID = PackageList.get(i).get("KitID");
                                }
                            }

                            postParameters.add(new BasicNameValuePair("PackageID", PackageID));
                            postParameters.add(new BasicNameValuePair("PackageName", package_name));

                            response = AppUtils.callWebServiceWithMultiParam(Pin_transfer_Activity.this, postParameters, QueryUtils.EPinTransfer, TAG);

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
                                    // saveLoginUserInfo(jsonArrayData);
                                    AppUtils.alertDialogWithFinish(Pin_transfer_Activity.this, jsonObject.getString("Message"));
                                } else {
                                    AppUtils.alertDialogWithFinish(Pin_transfer_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Pin_transfer_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
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
            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_transfer_Activity.this);
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

                if (GroupTitle.trim().equalsIgnoreCase("E-pin Detail")) {
                    startActivity(new Intent(Pin_transfer_Activity.this, Pin_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(Pin_transfer_Activity.this, Pin_Generated_issued_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer Detail")) {
                    startActivity(new Intent(Pin_transfer_Activity.this, Pin_Transfer_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Received Detail")) {
                    startActivity(new Intent(Pin_transfer_Activity.this, Pin_Received_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    startActivity(new Intent(Pin_transfer_Activity.this, DashBoard_New.class));
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

            listDataHeader.add("Generated/Issued Pin Details");
            listDataHeader.add("E-pin Detail");
            listDataHeader.add("E-pin Transfer Detail");
            listDataHeader.add("E-pin Received Detail");
            listDataHeader.add("Logout");

            listDataChild.put(listDataHeader.get(0), Empty);
            listDataChild.put(listDataHeader.get(1), Empty);
            listDataChild.put(listDataHeader.get(2), Empty);
            listDataChild.put(listDataHeader.get(3), Empty);
            listDataChild.put(listDataHeader.get(4), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_welcome_name.setText("");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);


            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }
}