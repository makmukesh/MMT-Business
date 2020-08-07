package com.vpipl.mmtbusiness.extra;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;


/**
 * Created by PC14 on 3/23/2016.
 */
public class GetDataService extends Service
{
    String TAG="GetDataService";

    @Override
    public void onCreate()
    {
        super.onCreate();

        try
        {
            if(AppUtils.showLogs)Log.v(TAG, "onCreate is called");

            if (AppUtils.isNetworkAvailable(GetDataService.this))
            {
                if(AppUtils.showLogs)Log.v(TAG, "onCreate executeCountryRequest() called");
              //  executeHomePageSliderRequest();
               // executeGetCategoryRequest();
                executeProductListRequest();
            }
            else
            {
                stopSelf();
                if(AppUtils.showLogs)Log.v(TAG, "onCreate internet not found else called");
            }

        }
        catch(Exception e)
        {
            stopSelf();
            e.printStackTrace();
        }
    }
    private void executeProductListRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                //     AppUtils.showProgressDialog(this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("Formno", "10000001"));
                    postParameters.add(new BasicNameValuePair("WeekValue", "0" ));
                    postParameters.add(new BasicNameValuePair("Side", "0" ));
                    postParameters.add(new BasicNameValuePair("Status", "0"));
                    postParameters.add(new BasicNameValuePair("FromJD", "" ));
                    postParameters.add(new BasicNameValuePair("ToJD", "" ));
                    postParameters.add(new BasicNameValuePair("FromAD", "" ));
                    postParameters.add(new BasicNameValuePair("ToAD", "" ));
                    postParameters.add(new BasicNameValuePair("PackageId", "0" ));

                /*    0 = {BasicNameValuePair@5216} "Formno=10000001"
                    1 = {BasicNameValuePair@5217} "WeekValue=0"
                    2 = {BasicNameValuePair@5218} "Side=0"
                    3 = {BasicNameValuePair@5219} "Status=0"
                    4 = {BasicNameValuePair@5220} "FromJD="
                    5 = {BasicNameValuePair@5221} "ToJD="
                    6 = {BasicNameValuePair@5222} "FromAD="
                    7 = {BasicNameValuePair@5223} "ToAD="
                    8 = {BasicNameValuePair@5224} "PackageId=0"*/

                    response = AppUtils.callWebServiceWithMultiParam(GetDataService.this , postParameters, QueryUtils.methodToGetDownlineTeamDetail, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    //      AppUtils.dismissProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getProductListResult(jsonArrayData);
                        } else {
                            //     AppUtils.alertDialog(this, jsonObject.getString("Message"));
                        }
                    } else {
                        //    AppUtils.alertDialog(this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProductListResult(JSONArray jsonArray) {
        try {
       //     AppController.ProductListForSearch.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put("WholeSaleID", jsonObject.getString("WholeSaleID"));
                map.put("WholeSaleName", jsonObject.getString("WholeSaleName"));
                map.put("MfgCompID", jsonObject.getString("MfgCompID"));
                map.put("ManufacturerName", jsonObject.getString("ManufacturerName"));
                map.put("ProductId", jsonObject.getString("ProductId"));
                map.put("ProductCode", jsonObject.getString("ProductCode"));
                map.put("ProductName", WordUtils.capitalizeFully(jsonObject.getString("ProductName")));
                map.put("Packing", jsonObject.getString("Packing"));
                map.put("MRP", jsonObject.getString("MRP"));
                map.put("DP", jsonObject.getString("DP"));
                map.put("DispProductName", jsonObject.getString("DispProductName"));
                map.put("WholeSaleNameWidCity", jsonObject.getString("WholeSaleNameWidCity"));
              //  AppController.ProductListForSearch.add(map);
            }

            /*if(AppController.ProductListForSearch.size() > 0) {
                MyStorage myStorage = new MyStorage();
                myStorage.storeFavorites(this, AppController.ProductListForSearch);

                AppController.mymodelclassAppController = myStorage.loadFavorites(this);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            stopSelf();
            if(AppUtils.showLogs) Log.e(TAG, "onDestroy is called");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(AppUtils.showLogs)Log.v(TAG, "onStartCommand is called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
