package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pin_details_Activity extends AppCompatActivity {

    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();
    String TAG = "Pin_details";
    TextView txt_from_joining, txt_to_joining, txt_package_Name;
    TextView txt_unused_pin_number, txt_used_pin_number, txt_total_pin_number;
    TextView txt_unused_pin_value, txt_used_pin_value, txt_total_pin_value;
    String PackageArray[];

    LinearLayout ll_joining;
    CheckBox cb_joining;
    RadioGroup rg_view_detail_for;
    Button btn_proceed;

    TableLayout displayLinear;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";
    DatePickerDialog datePickerDialog;

    public DrawerLayout drawer;
    public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;
    private CircularImageView profileImage;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(Pin_details_Activity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
            } else {
                AppUtils.alertDialog(Pin_details_Activity.this, "Selected Date Can't be After today");
            }
        }
    };

    TextView txt_heading;
    int TopRows = 100;

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
                    startActivity(new Intent(Pin_details_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Pin_details_Activity.this);
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
        setContentView(R.layout.acticity_pin_details);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_from_joining = (TextView) findViewById(R.id.txt_from_joining);
            txt_package_Name = (TextView) findViewById(R.id.txt_package_Name);
            txt_to_joining = (TextView) findViewById(R.id.txt_to_joining);
            txt_heading = (TextView) findViewById(R.id.txt_heading);
            ll_joining = (LinearLayout) findViewById(R.id.ll_joining);
            cb_joining = (CheckBox) findViewById(R.id.cb_joining);
            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            displayLinear = (TableLayout) findViewById(R.id.displayLinear);
            rg_view_detail_for = (RadioGroup) findViewById(R.id.rg_view_detail_for);

            txt_unused_pin_number = (TextView) findViewById(R.id.txt_unused_pin_number);
            txt_used_pin_number = (TextView) findViewById(R.id.txt_used_pin_number);
            txt_total_pin_number = (TextView) findViewById(R.id.txt_total_pin_number);
            txt_unused_pin_value = (TextView) findViewById(R.id.txt_unused_pin_value);
            txt_used_pin_value = (TextView) findViewById(R.id.txt_used_pin_value);
            txt_total_pin_value = (TextView) findViewById(R.id.txt_total_pin_value);

            cb_joining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_joining.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        txt_from_joining.setText("");
                        txt_to_joining.setText("");
                    }
                }
            });

            executePackageRequest();

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createEpinDetailRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");

            txt_from_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_joining";
                    showdatePicker();
                }
            });

            txt_to_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_joining";
                    showdatePicker();
                }
            });


            txt_package_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageList.size() != 0) {
                        showPackageDialog();
                        txt_package_Name.clearFocus();
                    } else {
                        executePackageRequest();
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

            if (AppUtils.isNetworkAvailable(this)) {
                createEpinDetailRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executePackageRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(Pin_details_Activity.this, postParameters, QueryUtils.methodSponsorPageFillPackage, TAG);
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getPackageResult(jsonArrayData);
                        }
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
                map.put("KitName", (jsonObject.getString("KitName")));
                PackageList.add(map);
            }

            String[] PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }
            txt_package_Name.setText(PackageArray[0]);
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
            builder.setTitle("Select Package ");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_package_Name.setText(PackageArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_details_Activity.this);
        }
    }

    private void createEpinDetailRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String view_detail_for = radioButton.getText().toString().trim();

        String Type = "0";
        if (view_detail_for.equalsIgnoreCase("Both"))
            Type = "0";
        else if (view_detail_for.equalsIgnoreCase("Used"))
            Type = "1";
        else if (view_detail_for.equalsIgnoreCase("Un-Used"))
            Type = "2";

        String packagename = txt_package_Name.getText().toString().trim();
        String packageid = "0";
        for (int i = 0; i < PackageList.size(); i++) {
            if (packagename.equals(PackageList.get(i).get("KitName"))) {
                packageid = PackageList.get(i).get("KitID");
            }
        }
        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("FromDate", "" + txt_from_joining.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + txt_to_joining.getText().toString().trim()));
        postParameters.add(new BasicNameValuePair("Status", "" + Type));
        postParameters.add(new BasicNameValuePair("Package", "" + packageid));

        executeEpinDetails(postParameters);
    }

    private void executeEpinDetails(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_details_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_details_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Pin_details_Activity.this, postparameters, QueryUtils.methodToEPinDetail, TAG);
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
                            JSONArray jsonArrayEPinDetail = jsonObject.getJSONArray("EPinDetail");
                            JSONArray jsonArrayEPinDetailValue = jsonObject.getJSONArray("EPinDetailValue");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    WriteValues(jsonArrayEPinDetail, jsonArrayEPinDetailValue);
                                } else {
                                    AppUtils.alertDialog(Pin_details_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Pin_details_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_details_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_details_Activity.this);
        }
    }

    public void WriteValues(final JSONArray jsonArrayEPinDetail, final JSONArray jsonArrayEPinDetailValue) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        try {
            if (jsonArrayEPinDetailValue.length() > 0) {
                txt_unused_pin_number.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("UnusedPin"));
                txt_used_pin_number.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("UsedPin"));
                txt_total_pin_number.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("TotalPin"));

                txt_unused_pin_value.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("UnusedAmount"));
                txt_used_pin_value.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("UsedAmount"));
                txt_total_pin_value.setText("" + jsonArrayEPinDetailValue.getJSONObject(0).getString("TotalPinAmount"));
            }


            TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
            ll.removeAllViews();

            TableRow row1 = new TableRow(this);

            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp1);
            row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

            TextView A1 = new TextView(this);
            TextView B1 = new TextView(this);
            TextView C1 = new TextView(this);

            TextView E1 = new TextView(this);
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);
            TextView I1 = new TextView(this);
            TextView J1 = new TextView(this);
            TextView K1 = new TextView(this);
            TextView L1 = new TextView(this);
            TextView M1 = new TextView(this);

            A1.setText("SNo..");
            B1.setText("PinNo");
            C1.setText("ScratchNo");
            E1.setText("Package Name");
            F1.setText("BillNo");
            G1.setText("Bill Date");
            H1.setText("ePinStatus");
            I1.setText("Used By");
            J1.setText("Name");
            K1.setText("Used Date");
            L1.setText("Pin Value");
            M1.setText("Topup");

            A1.setPadding(px, px, px, px);
            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);
            I1.setPadding(px, px, px, px);
            J1.setPadding(px, px, px, px);
            K1.setPadding(px, px, px, px);
            L1.setPadding(px, px, px, px);
            M1.setPadding(px, px, px, px);

            A1.setTypeface(typeface);
            B1.setTypeface(typeface);
            C1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);
            I1.setTypeface(typeface);
            J1.setTypeface(typeface);
            K1.setTypeface(typeface);
            L1.setTypeface(typeface);
            M1.setTypeface(typeface);

            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            A1.setGravity(Gravity.CENTER);
            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);

            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);
            I1.setGravity(Gravity.CENTER);
            J1.setGravity(Gravity.CENTER);
            K1.setGravity(Gravity.CENTER);
            L1.setGravity(Gravity.CENTER);
            M1.setGravity(Gravity.CENTER);

            A1.setTextColor(Color.WHITE);
            B1.setTextColor(Color.WHITE);
            C1.setTextColor(Color.WHITE);

            E1.setTextColor(Color.WHITE);
            F1.setTextColor(Color.WHITE);
            G1.setTextColor(Color.WHITE);
            H1.setTextColor(Color.WHITE);
            I1.setTextColor(Color.WHITE);
            J1.setTextColor(Color.WHITE);
            K1.setTextColor(Color.WHITE);
            L1.setTextColor(Color.WHITE);
            M1.setTextColor(Color.WHITE);

            row1.addView(A1);
            row1.addView(B1);
            row1.addView(C1);

            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            row1.addView(H1);
            row1.addView(I1);
            row1.addView(J1);
            row1.addView(K1);
            row1.addView(L1);
            row1.addView(M1);

            ll.addView(row1);

            for (int i = 0; i < jsonArrayEPinDetail.length(); i++) {
                try {

                    JSONObject jobject = jsonArrayEPinDetail.getJSONObject(i);

                    final String CardNo = jobject.getString("CardNo");
                    final String ScratchNo = jobject.getString("ScratchNo");
                    String ProductName = (jobject.getString("ProductName"));
                    String BillNo = jobject.getString("BillNo");
                    String BillDate = jobject.getString("BillDate");
                    String Status = jobject.getString("Status");
                    String UsedBy = jobject.getString("UsedBy");
                    String MemName = jobject.getString("MemName");
                    String UsedDate = jobject.getString("UsedDate");
                    String KitAmount = jobject.getString("KitAmount");
                    String SNo = jobject.getString("SNo");

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                    else
                        row.setBackgroundColor(getResources().getColor(R.color.table_row_two));


                    TextView A = new TextView(this);
                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);
                    TextView I = new TextView(this);
                    TextView J = new TextView(this);
                    TextView K = new TextView(this);
                    TextView L = new TextView(this);
                    final TextView M = new TextView(this);

                    A.setText(SNo);
                    B.setText(CardNo);
                    C.setText(ScratchNo);
                    E.setText(ProductName);
                    F.setText(BillNo);
                    G.setText(BillDate);
                    H.setText(Status);
                    I.setText(UsedBy);
                    J.setText(MemName);
                    K.setText(UsedDate);
                    L.setText(KitAmount);

                    if (Status.equalsIgnoreCase("UnUsed"))
                        M.setText("Topup");
                    else
                        M.setText("Used-Pin");

                    A.setGravity(Gravity.CENTER);
                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);
                    I.setGravity(Gravity.CENTER);
                    J.setGravity(Gravity.CENTER);
                    K.setGravity(Gravity.CENTER);
                    L.setGravity(Gravity.CENTER);
                    M.setGravity(Gravity.CENTER);

                    A.setPadding(px, px, px, px);
                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);
                    I.setPadding(px, px, px, px);
                    J.setPadding(px, px, px, px);
                    K.setPadding(px, px, px, px);
                    L.setPadding(px, px, px, px);
                    M.setPadding(px, px, px, px);

                    A.setTextColor(Color.BLACK);
                    B.setTextColor(Color.BLACK);
                    C.setTextColor(Color.BLACK);
                    E.setTextColor(Color.BLACK);
                    F.setTextColor(Color.BLACK);
                    G.setTextColor(Color.BLACK);
                    H.setTextColor(Color.BLACK);
                    I.setTextColor(Color.BLACK);
                    J.setTextColor(Color.BLACK);
                    K.setTextColor(Color.BLACK);
                    L.setTextColor(Color.BLACK);
                    M.setTextColor(Color.BLACK);

                    A.setTypeface(typeface);
                    B.setTypeface(typeface);
                    C.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);
                    I.setTypeface(typeface);
                    J.setTypeface(typeface);
                    K.setTypeface(typeface);
                    L.setTypeface(typeface);
                    M.setTypeface(typeface);

                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                    M.setTextColor(getResources().getColor(R.color.color_green_text));

                    M.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (M.getText().toString().trim().equalsIgnoreCase("Topup")) {
                                Intent intent = new Intent(Pin_details_Activity.this, Topup_member_Activity.class);
                                intent.putExtra("PinNumber", "" + CardNo);
                                intent.putExtra("ScratchNo", "" + ScratchNo);
                                startActivity(intent);
                            }
                        }
                    });

                    row.addView(A);
                    row.addView(B);
                    row.addView(C);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    row.addView(H);
                    row.addView(I);
                    row.addView(J);
                    row.addView(K);
                    row.addView(L);
                    row.addView(M);


                    ll.addView(row);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            AppUtils.showExceptionDialog(Pin_details_Activity.this);
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
            AppUtils.showExceptionDialog(Pin_details_Activity.this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (AppUtils.isNetworkAvailable(this)) {
            createEpinDetailRequest();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
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
                    startActivity(new Intent(Pin_details_Activity.this, Pin_Generated_issued_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer")) {
                    startActivity(new Intent(Pin_details_Activity.this, Pin_transfer_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer Detail")) {
                    startActivity(new Intent(Pin_details_Activity.this, Pin_Transfer_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Received Detail")) {
                    startActivity(new Intent(Pin_details_Activity.this, Pin_Received_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    startActivity(new Intent(Pin_details_Activity.this, DashBoard_New.class));
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
            listDataHeader.add("E-pin Transfer");
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
