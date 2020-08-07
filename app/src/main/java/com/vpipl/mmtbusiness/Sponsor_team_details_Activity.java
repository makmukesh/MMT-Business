package com.vpipl.mmtbusiness;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
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

public class Sponsor_team_details_Activity extends AppCompatActivity {

    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> ACtivationWeekList = new ArrayList<>();
    String TAG = "Sponsor_team_details_Activity";
    int TopRows = 20;
    int Count = 0;
    JSONArray AllDataArray, DataArray;
    TextView txt_from_joining, txt_to_joining, txt_from_activation, txt_to_activation, txt_activation_week, txt_package_Name;
    TextView txt_left_paid_members, txt_right_paid_members, txt_left_total_members, txt_right_total_members;
    LinearLayout ll_activation, ll_joining, ll_activation_week;
    CheckBox cb_joining, cb_activation, cb_activation_week;
    Button btn_proceed, btn_load_more;
    TableLayout displayLinear;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";
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
                else if (whichdate.equalsIgnoreCase("txt_to_activation"))
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_from_activation")) {
                    txt_from_activation.setText(sdf.format(myCalendar.getTime()));
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                }
            } else {

                AppUtils.alertDialog(Sponsor_team_details_Activity.this, "Selected Date Can't be After today");
            }
        }
    };
    RadioButton rb_unused, rb_used, rb_both, rb_left, rb_right, rb_all;
    RadioGroup rg_view_detail_for, rg_side;
    TextView txt_heading, txt_count;
    String PackageArray[], ActivationWeekArray[];
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
                    startActivity(new Intent(Sponsor_team_details_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Sponsor_team_details_Activity.this);
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
        setContentView(R.layout.activity_sponsor_team_details);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            SetupToolbar();

            txt_from_joining = (TextView) findViewById(R.id.txt_from_joining);
            txt_to_joining = (TextView) findViewById(R.id.txt_to_joining);
            txt_from_activation = (TextView) findViewById(R.id.txt_from_activation);
            txt_to_activation = (TextView) findViewById(R.id.txt_to_activation);
            txt_activation_week = (TextView) findViewById(R.id.txt_activation_week);

            txt_left_paid_members = (TextView) findViewById(R.id.txt_left_paid_members);
            txt_right_paid_members = (TextView) findViewById(R.id.txt_right_paid_members);
            txt_left_total_members = (TextView) findViewById(R.id.txt_left_total_members);
            txt_right_total_members = (TextView) findViewById(R.id.txt_right_total_members);

            txt_package_Name = (TextView) findViewById(R.id.txt_package_Name);
            txt_heading = (TextView) findViewById(R.id.txt_heading);
            txt_count = (TextView) findViewById(R.id.txt_count);

            ll_activation = (LinearLayout) findViewById(R.id.ll_activation);
            ll_joining = (LinearLayout) findViewById(R.id.ll_joining);
            ll_activation_week = (LinearLayout) findViewById(R.id.ll_activation_week);

            cb_joining = (CheckBox) findViewById(R.id.cb_joining);
            cb_activation = (CheckBox) findViewById(R.id.cb_activation);
            cb_activation_week = (CheckBox) findViewById(R.id.cb_activation_week);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            btn_load_more = (Button) findViewById(R.id.btn_load_more);

            Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
            cb_joining.setTypeface(typeface);
            cb_activation.setTypeface(typeface);
            cb_activation_week.setTypeface(typeface);

            displayLinear = (TableLayout) findViewById(R.id.displayLinear);

            rg_view_detail_for = (RadioGroup) findViewById(R.id.rg_view_detail_for);
            rg_side = (RadioGroup) findViewById(R.id.rg_side);

            rb_unused = (RadioButton) findViewById(R.id.rb_unused);
            rb_used = (RadioButton) findViewById(R.id.rb_used);
            rb_both = (RadioButton) findViewById(R.id.rb_both);
            rb_left = (RadioButton) findViewById(R.id.rb_left);
            rb_right = (RadioButton) findViewById(R.id.rb_right);
            rb_all = (RadioButton) findViewById(R.id.rb_all);

            cb_joining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_joining.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        ll_activation_week.setVisibility(View.GONE);
                        txt_from_joining.setText("");
                        txt_to_joining.setText("");
                    }
                }
            });

            cb_activation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_activation.setVisibility(View.VISIBLE);
                    } else {
                        ll_activation_week.setVisibility(View.GONE);
                        ll_activation.setVisibility(View.GONE);
                        txt_from_activation.setText("");
                        txt_to_activation.setText("");
                    }
                }
            });

            cb_activation_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b) {
                        ll_activation_week.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        ll_activation.setVisibility(View.GONE);
                        ll_activation_week.setVisibility(View.GONE);
                        txt_activation_week.setText("");
                    }
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

            txt_activation_week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ACtivationWeekList.size() != 0) {
                        showWeekDialog();
                        txt_activation_week.clearFocus();
                    } else {
                        executeWeekRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 20;
                    createSponsorTeamListRequest();
                }
            });

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = TopRows + 20;
                    WriteValues();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy MMMM dd");

            txt_from_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_joining";
                    new DatePickerDialog(Sponsor_team_details_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_to_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_joining";
                    new DatePickerDialog(Sponsor_team_details_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_from_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_from_activation";
                    new DatePickerDialog(Sponsor_team_details_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_to_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_to_activation";
                    new DatePickerDialog(Sponsor_team_details_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_heading.setText(getResources().getString(R.string.sponsor_downline));

            if (AppUtils.isNetworkAvailable(this)) {

                executeWeekRequest();

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
                response = AppUtils.callWebServiceWithMultiParam(Sponsor_team_details_Activity.this, postParameters, QueryUtils.methodSponsorPageFillPackage, TAG);
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

    private void executeWeekRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(Sponsor_team_details_Activity.this, postParameters, QueryUtils.methodSponsorActivationWeek, TAG);
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getWeekResult(jsonArrayData);
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
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("KitName")));

                PackageList.add(map);
            }

            String[] PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }
            txt_package_Name.setText(PackageArray[0]);

            createSponsorTeamListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWeekResult(JSONArray jsonArray) {
        try {
            ACtivationWeekList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("SessID", jsonObject.getString("SessID"));
                map.put("SessName", WordUtils.capitalizeFully(jsonObject.getString("SessName")));

                ACtivationWeekList.add(map);
            }

            executePackageRequest();
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
            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
        }
    }

    private void showWeekDialog() {
        try {
            ActivationWeekArray = new String[ACtivationWeekList.size()];

            for (int i = 0; i < ACtivationWeekList.size(); i++) {
                ActivationWeekArray[i] = ACtivationWeekList.get(i).get("SessName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Activation Week");
            builder.setItems(ActivationWeekArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_activation_week.setText(ActivationWeekArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
        }
    }

    private void createSponsorTeamListRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);
        findViewById(R.id.txt_count).setVisibility(View.GONE);

        int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String view_detail_for = radioButton.getText().toString();

        String Type = "0";

        if (view_detail_for.equalsIgnoreCase("Both"))
            Type = "0";
        else if (view_detail_for.equalsIgnoreCase("Paid"))
            Type = "Active";
        else if (view_detail_for.equalsIgnoreCase("Un-Paid"))
            Type = "Deactive";

        int selectedIdTwo = rg_side.getCheckedRadioButtonId();
        RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
        String view_detail_side = radioButtonTwo.getText().toString();

        String side = "0";
        if (view_detail_side.equalsIgnoreCase("Left"))
            side = "1";
        else if (view_detail_side.equalsIgnoreCase("Right"))
            side = "2";
        else if (view_detail_side.equalsIgnoreCase("All"))
            side = "0";


        String packagename = txt_package_Name.getText().toString();

        String packageid = "0";
        for (int i = 0; i < PackageList.size(); i++) {
            if (packagename.equals(PackageList.get(i).get("KitName"))) {
                packageid = PackageList.get(i).get("KitID");
            }
        }

        String Weekname = txt_activation_week.getText().toString();

        String WeekValue = "0";
        for (int i = 0; i < ACtivationWeekList.size(); i++) {
            if (Weekname.equals(ACtivationWeekList.get(i).get("SessName"))) {
                WeekValue = ACtivationWeekList.get(i).get("SessID");
            }
        }

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("WeekValue", "" + WeekValue));
        postParameters.add(new BasicNameValuePair("Side", "" + side));
        postParameters.add(new BasicNameValuePair("Status", "" + Type));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("FromAD", "" + txt_from_activation.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToAD", "" + txt_to_activation.getText().toString()));
        postParameters.add(new BasicNameValuePair("PackageId", "" + packageid));

        executeMemberDownlineListRequest(postParameters);
    }

    private void executeMemberDownlineListRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Sponsor_team_details_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Sponsor_team_details_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Sponsor_team_details_Activity.this, postparameters, QueryUtils.methodToGetSponsorTeamDetail, TAG);

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
                            JSONArray jsonArrayJoiningDetails = jsonObject.getJSONArray("JoiningDetails");
                            JSONArray jsonArrayTotalRowCount = jsonObject.getJSONArray("TotalRowCount");
                            JSONArray jsonArrayDownLineDetail = jsonObject.getJSONArray("DownLineDetail");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {

                                    Count = Integer.parseInt(jsonArrayTotalRowCount.getJSONObject(0).getString("Total"));
                                    AllDataArray = jsonArrayDownLineDetail;
                                    DataArray = jsonArrayJoiningDetails;

                                    WriteValues();

                                } else {
                                    AppUtils.alertDialog(Sponsor_team_details_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Sponsor_team_details_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
        }
    }

    public void WriteValues() {

        AppUtils.showProgressDialog(Sponsor_team_details_Activity.this);

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            txt_left_paid_members.setText(DataArray.getJSONObject(0).getString("LeftBv"));
            txt_right_paid_members.setText(DataArray.getJSONObject(0).getString("RightBv"));
            txt_left_total_members.setText(DataArray.getJSONObject(0).getString("LeftJoining"));
            txt_right_total_members.setText(DataArray.getJSONObject(0).getString("RightJoining"));

            if (AllDataArray.length() > 0)
            {
                findViewById(R.id.HSV).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_count).setVisibility(View.VISIBLE);

                int length = 0;
                if (TopRows <= AllDataArray.length())
                    length = TopRows;
                else
                    length = AllDataArray.length();

                if (length == AllDataArray.length())
                    btn_load_more.setVisibility(View.GONE);

                String count_text = "(Showing " + length + " of " + Count + " records)";
                txt_count.setText(count_text);

                TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
                ll.removeAllViews();

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.WHITE);

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView F1 = new TextView(this);

                TextView H1 = new TextView(this);

                TextView J1 = new TextView(this);
                TextView K1 = new TextView(this);
                TextView L1 = new TextView(this);

                A1.setText("Level\nNo.");
                B1.setText(getString(R.string.member_id));
                C1.setText(getString(R.string.name));
                D1.setText("Joining Date");
                E1.setText(getString(R.string.activation_date));
                F1.setText("Activation\nSession");
                H1.setText("Sponsor ID");
                J1.setText("Side");
                K1.setText("Activation\nPV");
                L1.setText("Package Name");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                J1.setPadding(px, px, px, px);
                L1.setPadding(px, px, px, px);
                K1.setPadding(px, px, px_right, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                F1.setTypeface(typeface);
                H1.setTypeface(typeface);
                J1.setTypeface(typeface);
                L1.setTypeface(typeface);
                K1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER_VERTICAL);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);
                F1.setGravity(Gravity.CENTER_VERTICAL);
                H1.setGravity(Gravity.CENTER_VERTICAL);
                J1.setGravity(Gravity.CENTER_VERTICAL);
                K1.setGravity(Gravity.CENTER_VERTICAL);
                L1.setGravity(Gravity.CENTER_VERTICAL);

                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
                F1.setTextColor(Color.BLACK);
                H1.setTextColor(Color.BLACK);
                J1.setTextColor(Color.BLACK);
                K1.setTextColor(Color.BLACK);
                L1.setTextColor(Color.BLACK);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(F1);
                row1.addView(L1);
                row1.addView(H1);
                row1.addView(J1);
                row1.addView(K1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.BLACK);

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < length; i++) {
                    try {

                        JSONObject jobject = AllDataArray.getJSONObject(i);
                        String member_id = jobject.getString("IdNo");

                        String level = jobject.getString("MLevel");

                        String name = WordUtils.capitalizeFully(jobject.getString("MemName"));
                        String purchase_date = jobject.getString("DOJ");
                        String activation_date = jobject.getString("TopUpDate");
                        String sponsor_id = jobject.getString("ParentIDNo");
                        String ReferralIdNo = jobject.getString("ReferralIdNo");
                        String side = jobject.getString("Leg");
                        String JoiningBV = jobject.getString("JoiningBV");
                        String UpGrdSessId = jobject.getString("UpGrdSessId");
                        String Package_name = jobject.getString("kitname");

                        StringBuilder sb = new StringBuilder(name);

//                        int ii = 0;
//                        while ((ii = sb.indexOf(" ", ii + 10)) != -1) {
//                            sb.replace(ii, ii + 1, "\n");
//                        }

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        row.setBackgroundColor(Color.WHITE);

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView F = new TextView(this);
                        TextView H = new TextView(this);
                        TextView J = new TextView(this);
                        TextView K = new TextView(this);
                        TextView L = new TextView(this);

                        A.setText(level);
                        B.setText(member_id);
                        C.setText(sb.toString());
                        D.setText(AppUtils.getDateFromAPIDate(purchase_date));
                        E.setText(activation_date);
                        F.setText(UpGrdSessId);
                        H.setText(ReferralIdNo);
                        J.setText(side);
                        K.setText(JoiningBV);
                        L.setText(Package_name);

                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER_VERTICAL);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);
                        F.setGravity(Gravity.CENTER_VERTICAL);
                        H.setGravity(Gravity.CENTER_VERTICAL);
                        J.setGravity(Gravity.CENTER_VERTICAL);
                        K.setGravity(Gravity.CENTER_VERTICAL);
                        L.setGravity(Gravity.CENTER_VERTICAL);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        J.setPadding(px, px, px, px);
                        L.setPadding(px, px, px, px);
                        K.setPadding(px, px, px_right, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);
                        F.setTypeface(typeface);
                        H.setTypeface(typeface);
                        J.setTypeface(typeface);
                        L.setTypeface(typeface);
                        K.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(F);
                        row.addView(L);
                        row.addView(H);
                        row.addView(J);
                        row.addView(K);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view_one.setBackgroundColor(Color.parseColor("#dddddd"));

                        ll.addView(row);
                        ll.addView(view_one);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        } catch (Exception e) {
        }

        AppUtils.dismissProgressDialog();
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
            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
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
            AppUtils.showExceptionDialog(Sponsor_team_details_Activity.this);
        }
    }
}
