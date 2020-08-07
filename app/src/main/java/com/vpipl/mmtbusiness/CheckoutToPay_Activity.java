package com.vpipl.mmtbusiness;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.CheckoutToPay_Adapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckoutToPay_Activity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> deliveryAddressList = new ArrayList<>();
    public static String addressListPosition = "0";
    private static String TAG = "CheckoutToPay_Activity";

    private FrameLayout layout_cartProductList;
    private LinearLayout layout_noData;
    private ListView list_cartProducts;
    private TextView txt_addressChange;
    private TextView txt_name;
    private TextView txt_address;
    private TextView txt_mobNo;
    private TextView txt_totalItems,txt_shopping_wallet_balance ,txt_payout_wallet_balance ,txt_shopping_wallet_deduction;
    private TextView txt_subTotalAmount;
    private TextView txt_deliveryCharge;

    private TextView txt_netpayable;

    private Button btn_startShopping;
    private Button btn_payNow;

    private CheckoutToPay_Adapter adapter;
    private ViewGroup addressHeaderView = null;
    private ViewGroup addressFooterView = null;

    RadioGroup rg_paymode;
    RadioButton rb_wallet, rb_cod;

    ImageView img_nav_back;
    TextView txt_awb;

    String ComesFrom = "Other";
    String paymentMode = "Payout Wallet";

    double AWB = 0.0;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        txt_awb = (TextView) findViewById(R.id.txt_awb);
      LinearLayout  ll_wallet_balance =  findViewById(R.id.ll_wallet_balance);

        ll_wallet_balance.setVisibility(View.GONE);

        img_nav_back.setImageResource(R.drawable.ic_arrow_back_white_px);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String calculateSelectedProductSubTotalAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    private String calculateShoppingWalletDeductionAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) / 100 *
                        (Double.parseDouble(AppController.selectedProductsList.get(i).getShoppingWalletPer())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    private String calculateTotalShoppingwalletPer() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getShoppingWalletPer()))) ;
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }
    private String calculateTotalShoppingwalletAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) / 100 *
                        (Double.parseDouble(AppController.selectedProductsList.get(i).getShoppingWalletPer())));
                amount = amount + ( countAmount * ((Double.parseDouble(AppController.selectedProductsList.get(i).getQty()))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (String.format("%.2f", amount)) + "";
    }
    private String calculateSelectedProductTotalBVAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getBV())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    private String calculateSelectedProductTotalShipCharge() {
        double amount = 0.0d;
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    private String calculateSelectedProductTotalAmount() {
        double amount = 0.0d;
        try {
            amount = (Double.parseDouble(calculateSelectedProductSubTotalAmount()) + Double.parseDouble(calculateSelectedProductTotalShipCharge()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    private String calculateNetPayableAmount() {
        double amount = 0.0d;
        try {
            amount = Double.parseDouble(calculateSelectedProductTotalAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "" + (int) amount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkouttopay_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        if (getIntent().getExtras() != null) {
            ComesFrom = getIntent().getStringExtra("COMESFROM");
        }

        try {

            layout_cartProductList = findViewById(R.id.layout_cartProductList);
            layout_noData = findViewById(R.id.layout_noData);
            list_cartProducts = findViewById(R.id.list_cartProducts);
            btn_payNow = findViewById(R.id.btn_payNow);
            btn_startShopping = findViewById(R.id.btn_startShopping);

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(CheckoutToPay_Activity.this, Home_New.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });

            btn_payNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (rg_paymode.getCheckedRadioButtonId() == -1) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select Pay Mode");
                    } /*else if (AppController.selectedProductsList.get(0).getDeliveryAddressStateCode().equalsIgnoreCase("0")) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select valid Delivery Address");
                    } else if (AppController.selectedProductsList.get(0).getDeliveryAddressStateName().equalsIgnoreCase("-- No State Found --")) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select valid Delivery Address");
                    } else if (AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().equalsIgnoreCase("0")) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select valid Delivery Address");
                    }*/ else if (paymentMode.equalsIgnoreCase("Payout Wallet") && AWB < Double.parseDouble(calculateNetPayableAmount())) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Insufficient Wallet Balance to Place Order");
                    } else {

                        int selectedIdTwo = rg_paymode.getCheckedRadioButtonId();
                        RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
                        String view_detail_side = radioButtonTwo.getText().toString();

                        paymentMode = view_detail_side;

                        showPaymentConfirmationDialog();
                    }

                }
            });

            showEmptyCart();

      //      executeWalletBalanceRequest();

            executeToGetAddressesList();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void executeToGetAddressesList() {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            // UserType is by defailt D for Distributor
                            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                                if (Usertype.equalsIgnoreCase("CUSTOMER"))
                                    postParameters.add(new BasicNameValuePair("UserType", "N"));
                                else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                                    postParameters.add(new BasicNameValuePair("UserType", "D"));
                                else
                                    postParameters.add(new BasicNameValuePair("UserType", "N"));
                            } else
                                postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToGetCheckOutDeliveryAddress, TAG);
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

                                if (jsonArrayData.length() > 0) {
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else {
                                    String msz = jsonObject.getString("Message");

                                    if (msz.contains("No Address Found,Please Add New Address"))
                                        ShowDialog(msz);
                                    else
                                        AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {
            deliveryAddressList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + jsonObject.getString("MemFirstName"));
                map.put("MemLastName", "" + jsonObject.getString("MemLastName"));
                map.put("Address1", "" + jsonObject.getString("Address1"));
                map.put("Address2", "" + jsonObject.getString("Address2"));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + jsonObject.getString("CountryName"));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + jsonObject.getString("StateName"));
                map.put("District", "" + jsonObject.getString("District"));
                map.put("City", "" + jsonObject.getString("City"));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + jsonObject.getString("Address").replace("&nbsp;", " "));

                Log.e(TAG, "Address..." + jsonObject.getString("Address").replace("&nbsp;", " "));
                deliveryAddressList.add(map);
            }

            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmptyCart() {
        try {
            layout_cartProductList.setVisibility(View.GONE);
            list_cartProducts.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductSelectedCartList() {
        try {
            if (AppController.selectedProductsList.size() > 0) {
                if (deliveryAddressList.size() > 0) {
                    layout_cartProductList.setVisibility(View.VISIBLE);
                    list_cartProducts.setVisibility(View.VISIBLE);
                    layout_noData.setVisibility(View.GONE);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addressHeaderView = (ViewGroup) inflater.inflate(R.layout.addcartlist_header, list_cartProducts, false);
                    addressFooterView = (ViewGroup) inflater.inflate(R.layout.addcartlist_footer, list_cartProducts, false);

                    setHeaderDetails();

                    list_cartProducts.addHeaderView(addressHeaderView, null, false);
                    list_cartProducts.addFooterView(addressFooterView, null, false);


                    adapter = new CheckoutToPay_Adapter(CheckoutToPay_Activity.this);
                    list_cartProducts.setAdapter(adapter);
                } else {
                    showEmptyCart();
                }
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeaderDetails() {
        try {
            if (addressHeaderView != null) {
                txt_addressChange = addressHeaderView.findViewById(R.id.txt_addressChange);
                txt_addressChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CheckoutToPay_Activity.this, ChangeDeliveryAddress_Activity.class));

                    }
                });

                txt_name = addressHeaderView.findViewById(R.id.txt_name);
                txt_address = addressHeaderView.findViewById(R.id.txt_address);
                txt_mobNo = addressHeaderView.findViewById(R.id.txt_mobNo);

                txt_shopping_wallet_balance = addressFooterView.findViewById(R.id.txt_shopping_wallet_balance);
                txt_payout_wallet_balance = addressFooterView.findViewById(R.id.txt_payout_wallet_balance);
                txt_totalItems = addressFooterView.findViewById(R.id.txt_totalItems);
                txt_shopping_wallet_deduction = addressFooterView.findViewById(R.id.txt_shopping_wallet_deduction);
                txt_subTotalAmount = addressFooterView.findViewById(R.id.txt_subTotalAmount);
                txt_deliveryCharge = addressFooterView.findViewById(R.id.txt_deliveryCharge);

                txt_netpayable = addressFooterView.findViewById(R.id.txt_netpayable);

                rg_paymode = (RadioGroup) addressFooterView.findViewById(R.id.rg_paymode);
                rb_wallet = (RadioButton) addressFooterView.findViewById(R.id.rb_wallet);
                rb_cod = (RadioButton) addressFooterView.findViewById(R.id.rb_cod);

                txt_totalItems.setText(Html.fromHtml("(" + AppController.selectedProductsList.size() + ")"));
                txt_subTotalAmount.setText("₹ " + Html.fromHtml(calculateSelectedProductSubTotalAmount()));
                txt_deliveryCharge.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalShipCharge()));

                txt_netpayable.setText("₹ " + Html.fromHtml(calculateNetPayableAmount()));

                txt_shopping_wallet_deduction.setText("₹ " + Html.fromHtml(calculateTotalShoppingwalletAmount()));

                executeShoppingWalletBalanceRequest();
                executeWalletBalanceRequest();

                rg_paymode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButtonTwo = (RadioButton) findViewById(checkedId);
                        String view_detail_side = radioButtonTwo.getText().toString();

                        if (view_detail_side.equalsIgnoreCase("Cash In Bank")) {
                            paymentMode = view_detail_side;
                        } else {
                            paymentMode = view_detail_side;
                        }
                    }
                });

                setAddressValue(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddressValue(int position) {
        try {
            if (deliveryAddressList.size() > 0) {
                txt_name.setText(WordUtils.capitalizeFully(deliveryAddressList.get(position).get("MemFirstName")));
                txt_address.setText((WordUtils.capitalizeFully(Html.fromHtml(deliveryAddressList.get(position).get("Address")).toString().trim() + " Mobile : " + deliveryAddressList.get(position).get("Mobl"))).replaceAll(",", " "));
//              txt_mobNo.setText("M : "+ deliveryAddressList.get(position).get("Mobl"));

                if (AppController.selectedProductsList.size() > 0) {
                    ProductsList selectedProduct = new ProductsList();

                    selectedProduct.setID("" + AppController.selectedProductsList.get(0).getID());
                    selectedProduct.setProductType("" + AppController.selectedProductsList.get(0).getProductType());
                    selectedProduct.setcode("" + AppController.selectedProductsList.get(0).getcode());
                    selectedProduct.setUID("" + AppController.selectedProductsList.get(0).getUID());
                    selectedProduct.setName("" + AppController.selectedProductsList.get(0).getName());
                    selectedProduct.setImagePath("" + AppController.selectedProductsList.get(0).getImagePath());
                    selectedProduct.setNewMRP("" + AppController.selectedProductsList.get(0).getNewMRP());
                    selectedProduct.setNewDP("" + AppController.selectedProductsList.get(0).getNewDP());
                    selectedProduct.setBV("" + AppController.selectedProductsList.get(0).getBV());
                    selectedProduct.setDescription("" + AppController.selectedProductsList.get(0).getDescription());
                    selectedProduct.setDetail("" + AppController.selectedProductsList.get(0).getDetail());
                    selectedProduct.setKeyFeature("" + AppController.selectedProductsList.get(0).getKeyFeature());
                    selectedProduct.setDiscount("" + AppController.selectedProductsList.get(0).getDiscount());
                    selectedProduct.setDiscountPer("" + AppController.selectedProductsList.get(0).getDiscountPer());
                    selectedProduct.setIsshipChrg("" + AppController.selectedProductsList.get(0).getIsshipChrg());
                    selectedProduct.setShipCharge("" + AppController.selectedProductsList.get(0).getShipCharge());
                    selectedProduct.setCatID("" + AppController.selectedProductsList.get(0).getCatID());
                    selectedProduct.setRandomNo("" + AppController.selectedProductsList.get(0).getRandomNo());
                    selectedProduct.setQty("" + AppController.selectedProductsList.get(0).getQty());
                    selectedProduct.setBaseQty("" + AppController.selectedProductsList.get(0).getBaseQty());
                    selectedProduct.setAvailFor("" + AppController.selectedProductsList.get(0).getAvailFor());
                    selectedProduct.setOrderFor("" + AppController.selectedProductsList.get(0).getOrderFor());
                    selectedProduct.setParentProductID("" + AppController.selectedProductsList.get(0).getParentProductID());
                    selectedProduct.setsellerCode("" + AppController.selectedProductsList.get(0).getsellerCode());
                    selectedProduct.setselectedSizeId("" + AppController.selectedProductsList.get(0).getselectedSizeId());
                    selectedProduct.setselectedSizeName("" + AppController.selectedProductsList.get(0).getselectedSizeName());
                    selectedProduct.setselectedColorId("" + AppController.selectedProductsList.get(0).getselectedColorId());
                    selectedProduct.setselectedColorName("" + AppController.selectedProductsList.get(0).getselectedColorName());
                    selectedProduct.setShoppingWalletPer("" + AppController.selectedProductsList.get(0).getShoppingWalletPer());

                    selectedProduct.setDeliveryAddressID("" + deliveryAddressList.get(position).get("ID"));
                    selectedProduct.setDeliveryAddressFirstName("" + deliveryAddressList.get(position).get("MemFirstName"));
                    selectedProduct.setDeliveryAddressLastName("" + deliveryAddressList.get(position).get("MemLastName"));
                    selectedProduct.setDeliveryAddress("" + deliveryAddressList.get(position).get("Address"));
                    selectedProduct.setDeliveryAddress1("" + deliveryAddressList.get(position).get("Address1"));
                    selectedProduct.setDeliveryAddress2("" + deliveryAddressList.get(position).get("Address2"));
                    selectedProduct.setDeliveryAddressCountryID("" + deliveryAddressList.get(position).get("CountryID"));
                    selectedProduct.setDeliveryAddressCountryName("" + deliveryAddressList.get(position).get("CountryName"));
                    selectedProduct.setDeliveryAddressStateCode("" + deliveryAddressList.get(position).get("StateCode"));
                    selectedProduct.setDeliveryAddressStateName("" + deliveryAddressList.get(position).get("StateName"));
                    selectedProduct.setDeliveryAddressDistrict("" + deliveryAddressList.get(position).get("District"));
                    selectedProduct.setDeliveryAddressCity("" + deliveryAddressList.get(position).get("City"));
                    selectedProduct.setDeliveryAddressPinCode("" + deliveryAddressList.get(position).get("PinCode"));
                    selectedProduct.setDeliveryAddressEmail("" + deliveryAddressList.get(position).get("Email"));
                    selectedProduct.setDeliveryAddressMob("" + deliveryAddressList.get(position).get("Mobl"));
                    selectedProduct.setDeliveryAddressEntryType("" + deliveryAddressList.get(position).get("EntryType"));

                    AppController.selectedProductsList.set(0, selectedProduct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (adapter != null) {
                setAddressValue(Integer.parseInt(CheckoutToPay_Activity.addressListPosition));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(CheckoutToPay_Activity.this, true);
        TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(message);

        TextView textView = dialog.findViewById(R.id.txt_submit);
        textView.setText("Add New Address");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(CheckoutToPay_Activity.this, AddDeliveryAddress_Activity.class);
                intent.putExtra("ComesFrom", "CheckoutToPay_Activity");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public void showPaymentConfirmationDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure to place an Order of " + txt_netpayable.getText().toString() + " through " + paymentMode + ". Please click on Confirm to proceed ahead."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Confirm");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startPaymentRequest();
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

    private void startPaymentRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayOrder = new JSONArray();
            JSONArray jsonArrayOrderDetail = new JSONArray();

            JSONObject jsonObjectOrder = new JSONObject();
            jsonObjectOrder.put("MemFirstName", AppController.selectedProductsList.get(0).getDeliveryAddressFirstName().trim());
            jsonObjectOrder.put("MemLasttName", AppController.selectedProductsList.get(0).getDeliveryAddressLastName().trim());
            jsonObjectOrder.put("Address1", AppController.selectedProductsList.get(0).getDeliveryAddress1().trim().replaceAll(",", " "));
            jsonObjectOrder.put("Address2", "");
            jsonObjectOrder.put("StateID", AppController.selectedProductsList.get(0).getDeliveryAddressStateCode().trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateName", AppController.selectedProductsList.get(0).getDeliveryAddressStateName().trim().replaceAll(",", " "));
            jsonObjectOrder.put("District", AppController.selectedProductsList.get(0).getDeliveryAddressDistrict().trim().replaceAll(",", " "));

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                jsonObjectOrder.put("UserType", "D");
                jsonObjectOrder.put("IDType", "D");
            } else {
                jsonObjectOrder.put("UserType", "N");
                jsonObjectOrder.put("IDType", "G");
            }


            if (paymentMode.equalsIgnoreCase("Payout Wallet"))
                jsonObjectOrder.put("PayMode", "PWB");
            else
                jsonObjectOrder.put("PayMode", "CIB");


            jsonObjectOrder.put("Remarks", "");

            jsonObjectOrder.put("ChDDNo", "0");
            jsonObjectOrder.put("ChDate", "");
            jsonObjectOrder.put("BankName", "");
            jsonObjectOrder.put("BranchName", "");

            jsonObjectOrder.put("TotalDP", calculateSelectedProductSubTotalAmount().trim().replace(",", " "));
            jsonObjectOrder.put("Item", "" + AppController.selectedProductsList.size());
            jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));

            TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
            String deviceId = telephonyManager.getDeviceId() + "";

            jsonObjectOrder.put("HostIp", deviceId.trim().replace(",", " "));
            jsonObjectOrder.put("ShippingCharge", "" + calculateSelectedProductTotalShipCharge().trim().replaceAll(",", " "));

            jsonObjectOrder.put("color", AppController.selectedProductsList.get(0).getselectedColorName().trim().replace(",", " "));
            jsonObjectOrder.put("Size", AppController.selectedProductsList.get(0).getselectedSizeName().trim().replace(",", " "));
            jsonObjectOrder.put("Pack", "0");
            jsonObjectOrder.put("Packing", "0");
            jsonObjectOrder.put("OrderFor", AppController.selectedProductsList.get(0).getOrderFor().trim().replace(",", " "));
            jsonObjectOrder.put("City", AppController.selectedProductsList.get(0).getDeliveryAddressCity().trim().replace(",", " "));
            jsonObjectOrder.put("PinCode", AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().trim().replace(",", " "));
            jsonObjectOrder.put("MobileNo", AppController.selectedProductsList.get(0).getDeliveryAddressMob().trim().replace(",", " "));
            jsonObjectOrder.put("Email", AppController.selectedProductsList.get(0).getDeliveryAddressEmail().trim().replace(",", " "));
            jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("TotalBV", calculateSelectedProductTotalBVAmount().trim().replace(",", " "));

            jsonObjectOrder.put("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("ShipCharge", "" + calculateSelectedProductTotalShipCharge().trim().replaceAll(",", " "));

            jsonObjectOrder.put("AmountBeforeTDR", "" + calculateSelectedProductTotalAmount().trim().replaceAll(",", " "));
            jsonObjectOrder.put("TDRAmount", "0");
            jsonObjectOrder.put("RndOff", "0");
            jsonObjectOrder.put("TshoppingWalletPer", "" + calculateTotalShoppingwalletPer().trim().replaceAll(",", " "));
            jsonObjectOrder.put("TshoppingWalletAmt", "" + calculateTotalShoppingwalletAmount().trim().replaceAll(",", " "));

            jsonArrayOrder.put(jsonObjectOrder);

            for (int j = 0; j < AppController.selectedProductsList.size(); j++) {

                JSONObject jsonObjectDetail = new JSONObject();

                jsonObjectDetail.put("Productid", AppController.selectedProductsList.get(j).getID().trim().replace(",", " "));
                jsonObjectDetail.put("ProductName", AppController.selectedProductsList.get(j).getName().trim().replace(",", " "));
                jsonObjectDetail.put("Qty", AppController.selectedProductsList.get(j).getQty().trim().replace(",", " "));
                jsonObjectDetail.put("DP", AppController.selectedProductsList.get(j).getNewDP().trim().replace(",", " "));
                jsonObjectDetail.put("Price", AppController.selectedProductsList.get(j).getNewMRP().trim().replace(",", " "));

                double SubTotal = 0.0d;
                SubTotal = ((Double.parseDouble(AppController.selectedProductsList.get(j).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(j).getQty())));

                jsonObjectDetail.put("SubTotal", "" + (SubTotal));
                jsonObjectDetail.put("colorDetails", AppController.selectedProductsList.get(j).getselectedColorName().trim().replace(",", " "));
                jsonObjectDetail.put("SizeDetails", "" + AppController.selectedProductsList.get(j).getselectedSizeName().trim().replace(",", " "));
                jsonObjectDetail.put("PackDetails", "0");
                jsonObjectDetail.put("PackingDetails", "");
                jsonObjectDetail.put("OrderForDetails", "" + AppController.selectedProductsList.get(j).getOrderFor().trim().replace(",", " "));
                jsonObjectDetail.put("ShipChargeDetails", "" + AppController.selectedProductsList.get(j).getShipCharge().trim().replace(",", " "));
                jsonObjectDetail.put("ImageUrl", "" + AppController.selectedProductsList.get(j).getImagePath().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("BV", "" + AppController.selectedProductsList.get(j).getBV().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("UID", "" + AppController.selectedProductsList.get(j).getUID().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("IsKit", "0");
                jsonObjectDetail.put("ProdType", "" + AppController.selectedProductsList.get(j).getProductType().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("DiscountPer", "" + AppController.selectedProductsList.get(j).getDiscountPer().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("ShoppingWalletPer", "" + AppController.selectedProductsList.get(j).getShoppingWalletPer().trim().replace(",", " ").replace("\\", ""));

                jsonArrayOrderDetail.put(jsonObjectDetail);
            }

            postParameters.add(new BasicNameValuePair("Order", jsonArrayOrder.toString()));
            postParameters.add(new BasicNameValuePair("OrderDetails", "" + jsonArrayOrderDetail.toString()));

            //G for Customer and I for Distributor

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                postParameters.add(new BasicNameValuePair("OrderType", "I"));
            } else {
                postParameters.add(new BasicNameValuePair("OrderType", "G"));
            }
            postParameters.add(new BasicNameValuePair("LoginIDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
            if (paymentMode.equalsIgnoreCase("Payout Wallet")) {
                postParameters.add(new BasicNameValuePair("LoginFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("PayWalletAmount", calculateNetPayableAmount()));
                postParameters.add(new BasicNameValuePair("OrderAmount", calculateNetPayableAmount()));
                postParameters.add(new BasicNameValuePair("LoginUserName", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
            }

            executeToMakeOrderPaymentRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToMakeOrderPaymentRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            if (paymentMode.equalsIgnoreCase("Payout Wallet")) {
                                response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToAddOrderforWallet, TAG);
                            } else {
                                response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToAddOrder, TAG);
                            }

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
                                try {
                                    AppController.selectedProductsList.clear();

                                    JSONArray jsonArrayMainOrder = jsonObject.getJSONArray("MainOrder");
                                    JSONObject jsonObjectMainOrder = jsonArrayMainOrder.getJSONObject(0);

                                    Intent intent = new Intent(CheckoutToPay_Activity.this, ThanksScreenOnline_Activity.class);
                                    intent.putExtra("ORDERNUMBER", "" + jsonObjectMainOrder.getString("OrderNo"));
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                AppUtils.alertDialogWithFinish(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    public String calculateSelectedProductTotalQty() {
        int qty = 0;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                qty = qty + (Integer.parseInt(AppController.selectedProductsList.get(i).getQty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qty + "";
    }

 /*   private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this,
                                    postParameters, QueryUtils.methodToGetROI_MonthlyWalletShoppingLimit, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                                txt_awb.setText("\u20B9 " + jsonArray.getJSONObject(0).getString("WalletAmount"));

                                AWB = Double.parseDouble(jsonArray.getJSONObject(0).getString("WalletAmount"));

                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }*/

    /*Added by mukesh 16-11-2018 07:33 Pm */
    private void executeShoppingWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this,
                                    postParameters, QueryUtils.methodToGetAvailableShoppingWalletBalance, TAG);

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


                                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                                txt_shopping_wallet_balance.setText("\u20B9 " + jsonArray.getJSONObject(0).getString("WBalance"));

                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }
    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this,
                                    postParameters, QueryUtils.methodToGetAvailablePayoutWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    txt_payout_wallet_balance.setText("\u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance"));
                                    AWB = Double.parseDouble(jsonArrayData.getJSONObject(0).getString("WBalance"));
                                }
                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }
}