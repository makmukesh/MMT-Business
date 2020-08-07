package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ChangeAddressList_Adapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;


public class ChangeDeliveryAddress_Activity extends AppCompatActivity {
    String TAG = "ChangeDeliveryAddress_Activity";

    TextView txt_addAddress, txt_addressCount;
    LinearLayout layout_addressList, layout_noData;
    ListView list_address;
    ChangeAddressList_Adapter adapter;


    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changedeliveryaddress_activity);


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_addAddress = (TextView) findViewById(R.id.txt_addAddress);
            txt_addressCount = (TextView) findViewById(R.id.txt_addressCount);
            layout_addressList = (LinearLayout) findViewById(R.id.layout_addressList);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_address = (ListView) findViewById(R.id.list_address);

            txt_addAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChangeDeliveryAddress_Activity.this, AddDeliveryAddress_Activity.class);
                    intent.putExtra("ComesFrom", "AddDeliveryAddress_Activity");
                    startActivity(intent);
                }
            });

            list_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    try {
                        CheckoutToPay_Activity.addressListPosition = "" + position;
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            txt_addressCount.setText(CheckoutToPay_Activity.deliveryAddressList.size() + " Saved Addresses");

            setAddressListData();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ChangeDeliveryAddress_Activity.this);
        }
    }

    private void setAddressListData() {
        try {
            if (CheckoutToPay_Activity.deliveryAddressList.size() > 0) {
                layout_addressList.setVisibility(View.VISIBLE);
                list_address.setVisibility(View.VISIBLE);
                layout_noData.setVisibility(View.GONE);

                adapter = new ChangeAddressList_Adapter(ChangeDeliveryAddress_Activity.this);
                list_address.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                layout_addressList.setVisibility(View.GONE);
                list_address.setVisibility(View.GONE);
                layout_noData.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();

            txt_addressCount.setText(CheckoutToPay_Activity.deliveryAddressList.size() + " Saved Addresses");
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
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ChangeDeliveryAddress_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ChangeDeliveryAddress_Activity.this);
        }
    }

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

}
