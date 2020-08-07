package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Weekly_Incentive_Statement_Activity extends AppCompatActivity {

    String TAG = "Monthly_Incentive_Stmt";

    TextView txt_payout_no, txt_period, txt_name, txt_id_number, txt_address, txt_mobileNumber,
            txt_performance_bonus, txt_total_earnings, txt_tds_amount, txt_admin_charges, txt_total_deductions,
            txt_amount_payble, txt_brought_forward, txt_total_amount_payble, txt_carried_forward, txt_net_payble, txt_Royalty_Incentive,
            txt_Weaker_side,txt_power_side;

    String payout_number = "";

    public void getViews() {
        txt_payout_no = (TextView) findViewById(R.id.txt_payout_no);
        txt_period = (TextView) findViewById(R.id.txt_period);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_id_number = (TextView) findViewById(R.id.txt_id_number);
        txt_address = (TextView) findViewById(R.id.txt_address);
        txt_mobileNumber = (TextView) findViewById(R.id.txt_mobileNumber);
        txt_performance_bonus = (TextView) findViewById(R.id.txt_performance_bonus);
        txt_Royalty_Incentive = (TextView) findViewById(R.id.txt_Royalty_Incentive);

        txt_total_earnings = (TextView) findViewById(R.id.txt_total_earnings);
        txt_tds_amount = (TextView) findViewById(R.id.txt_tds_amount);
        txt_admin_charges = (TextView) findViewById(R.id.txt_admin_charges);
        txt_total_deductions = (TextView) findViewById(R.id.txt_total_deductions);
        txt_amount_payble = (TextView) findViewById(R.id.txt_amount_payble);
        txt_brought_forward = (TextView) findViewById(R.id.txt_brought_forward);
        txt_total_amount_payble = (TextView) findViewById(R.id.txt_total_amount_payble);
        txt_carried_forward = (TextView) findViewById(R.id.txt_carried_forward);
        txt_net_payble = (TextView) findViewById(R.id.txt_net_payble);

        txt_power_side = (TextView) findViewById(R.id.txt_power_side);
        txt_Weaker_side = (TextView) findViewById(R.id.txt_Weaker_side);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_incentive_statement);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
                payout_number = "" + getIntent().getIntExtra("PAYOUT", 0);


            getViews();

            if (AppUtils.isNetworkAvailable(this)) {
                executeMonthlyIncentiveRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }


    }


    private void executeMonthlyIncentiveRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Weekly_Incentive_Statement_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Weekly_Incentive_Statement_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("SessionID", "" + payout_number));
                            response = AppUtils.callWebServiceWithMultiParam(Weekly_Incentive_Statement_Activity.this, postParameters, QueryUtils.methodtoWeeklyStatement, TAG);

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
                                WriteValues(jsonArrayData);
                            } else {
                                AppUtils.alertDialog(Weekly_Incentive_Statement_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Weekly_Incentive_Statement_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Weekly_Incentive_Statement_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArray) {

        try {

            findViewById(R.id.LLShowData).setVisibility(View.VISIBLE);

            JSONObject jobject = jsonArray.getJSONObject(0);

            int payout_number = jobject.getInt("PayoutNo");

            String FromDate = jobject.getString("FromDate");
            String ToDate = jobject.getString("ToDate");
            String Period = WordUtils.capitalizeFully(FromDate + " To " + ToDate);

            String MemName = WordUtils.capitalizeFully(jobject.getString("MemName"));
            String IDNo = jobject.getString("IdNo");
            String Address1 = jobject.getString("Address");
            String Address = WordUtils.capitalizeFully(Address1);
            String Mobl = jobject.getString("Mobl");

            String Performance = jobject.getString("BinaryIncome");
            String Royalty = jobject.getString("SpillIncome");

            String NetIncome = jobject.getString("NetIncome");

            String TdsAmount = jobject.getString("TdsAmount");
            String AdminCharge = jobject.getString("AdminCharge");
            String Deduction = jobject.getString("Deduction");

            String Brought_Forward = jobject.getString("Prevbal");
            String Carried_Forward = jobject.getString("ClsBal");

            String amount_payable = jobject.getString("ChqAmt");
            String Total_Amount_Payble = jobject.getString("ChqAmt");
            String Net_Payble = jobject.getString("ChqAmt");

            String WkrLegBv = jobject.getString("WkrLegBv");
            String PwrLegBv = jobject.getString("PwrLegBv");

            txt_payout_no.setText("" + payout_number);

            txt_period.setText(Period);

            txt_name.setText(MemName);
            txt_id_number.setText(IDNo);
            txt_address.setText(Address);

            txt_mobileNumber.setText(Mobl);
            txt_performance_bonus.setText(Performance);
            txt_Royalty_Incentive.setText(Royalty);

            txt_total_earnings.setText(NetIncome);

            txt_tds_amount.setText(TdsAmount);
            txt_admin_charges.setText(AdminCharge);
            txt_total_deductions.setText(Deduction);

            txt_amount_payble.setText(amount_payable);
            txt_brought_forward.setText(Brought_Forward);
            txt_total_amount_payble.setText(Total_Amount_Payble);
            txt_carried_forward.setText(Carried_Forward);
            txt_net_payble.setText(Net_Payble);

            txt_Weaker_side.setText(WkrLegBv);
            txt_power_side.setText(PwrLegBv);

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
            AppUtils.showExceptionDialog(Weekly_Incentive_Statement_Activity.this);
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
            AppUtils.showExceptionDialog(Weekly_Incentive_Statement_Activity.this);
        }
    }
}
