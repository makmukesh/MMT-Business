package com.vpipl.mmtbusiness.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.AppController;
import com.vpipl.mmtbusiness.EditDeliveryAddress_Activity;
import com.vpipl.mmtbusiness.R;
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


public class MyAddressList_Adapter extends BaseAdapter {
    String TAG = "ChangeAddressList_Adapter";
    Context context;
    LayoutInflater inflater = null;
    TelephonyManager telephonyManager;


    public ArrayList<HashMap<String, String>> deliveryAddressList = new ArrayList<>();

    public MyAddressList_Adapter(Context context, ArrayList<HashMap<String, String>> deliveryAddressList) {
        this.context = context;
        this.deliveryAddressList = deliveryAddressList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    @Override
    public int getCount() {
        return deliveryAddressList.size();
    }

    @Override
    public Object getItem(int position) {
        return deliveryAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.myaddresslist_adapter, parent, false);
                holder = new Holder();

                holder.imgView_editAddress = (ImageView) convertView.findViewById(R.id.imgView_editAddress);
                holder.imageView7 = (ImageView) convertView.findViewById(R.id.imageView7);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
                holder.txt_mobNo = (TextView) convertView.findViewById(R.id.txt_mobNo);

                holder.LL = (LinearLayout) convertView.findViewById(R.id.LL);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.imgView_editAddress.setVisibility(View.VISIBLE);

            holder.txt_name.setText(WordUtils.capitalizeFully(deliveryAddressList.get(position).get("MemFirstName")));
            holder.txt_address.setText(WordUtils.capitalizeFully(Html.fromHtml(deliveryAddressList.get(position).get("Address")) + ", Mobile : " + deliveryAddressList.get(position).get("Mobl")));
            holder.txt_mobNo.setText(deliveryAddressList.get(position).get("Mobl"));



            holder.LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, EditDeliveryAddress_Activity.class);
                    intent.putExtra("position",position);
                    context.startActivity(intent);
                }
            });

            
             holder.imageView7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeToDeletetAddresses(deliveryAddressList.get(position).get("ID"));
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView imgView_editAddress, imageView7;
        TextView txt_name, txt_address, txt_mobNo;
        LinearLayout LL;
    }

    private void executeToDeletetAddresses(final String ID)
    {
        try {
            if (AppUtils.isNetworkAvailable(context)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(context);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();

                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                            postParameters.add(new BasicNameValuePair("ID", ID));
                            postParameters.add(new BasicNameValuePair("IPAddress", telephonyManager.getDeviceId()));

                            response = AppUtils.callWebServiceWithMultiParam(context, postParameters, QueryUtils.methodToCheckOutDeleteAddress, TAG);
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

                                if (jsonArrayData.length() > 0)
                                {
                                   AppUtils.alertDialogWithFinish(context,"Address Successfully Deleted");
                                }
                            } else {
                                AppUtils.alertDialog(context, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(context);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(context, context.getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }
    
    
}