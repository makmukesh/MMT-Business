package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.BadgeDrawable;
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

public class ProductDetail_Activity extends AppCompatActivity {

    public static String TAG = "ProductDetail_Activity";

    static HashMap<String, String> ProductDetails = new HashMap<>();

    TextView txt_washcareinfo, txt_productdetail, txt_deliveryreturninfo;
    WebView webView_washcarinfo, webView_productdetail, webView_view_deliveryreturninfo;

    Button btn_buyNow;

    TextView txt_ShoppingWalletPer,txt_productName, txt_productAmount, txt_productSKU;
    private TextView txt_productDiscount;
    boolean DiscDisp = false;


    ArrayList<HashMap<String, String>> imageList = new ArrayList<>();
    Boolean isBuyClick = true;

    String selectedSizeName = "NA", selectedSizeId = "0";

    ImageView img_menu, img_cart, img_user;

    TextView txt_outofstock;
    ImageView img_1, img_2, img_3, img_4, img_5, img_big;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail_activity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_ShoppingWalletPer = (TextView) findViewById(R.id.txt_ShoppingWalletPer);
            txt_productName = (TextView) findViewById(R.id.txt_productName);
            txt_productAmount = (TextView) findViewById(R.id.txt_productAmount);
            txt_productSKU = (TextView) findViewById(R.id.txt_productSKU);
            txt_productDiscount = findViewById(R.id.txt_productDiscount);

            webView_washcarinfo = (WebView) findViewById(R.id.webView_washcarinfo);
            webView_productdetail = (WebView) findViewById(R.id.webView_productdetail);
            webView_view_deliveryreturninfo = (WebView) findViewById(R.id.webView_view_deliveryreturninfo);

            txt_washcareinfo = (TextView) findViewById(R.id.txt_washcareinfo);
            txt_productdetail = (TextView) findViewById(R.id.txt_productdetail);
            txt_deliveryreturninfo = (TextView) findViewById(R.id.txt_deliveryreturninfo);


            btn_buyNow = (Button) findViewById(R.id.btn_buyNow);

            txt_outofstock = (TextView) findViewById(R.id.txt_outofstock);

            img_1 = (ImageView) findViewById(R.id.img_1);
            img_2 = (ImageView) findViewById(R.id.img_2);
            img_3 = (ImageView) findViewById(R.id.img_3);
            img_4 = (ImageView) findViewById(R.id.img_4);
            img_5 = (ImageView) findViewById(R.id.img_5);
            img_big = (ImageView) findViewById(R.id.img_big);


            txt_productdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (webView_productdetail.getVisibility() == View.GONE)
                        setSelectedTab(0);
                    else
                        setSelectedTab(4);
                }
            });

            txt_washcareinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (webView_washcarinfo.getVisibility() == View.GONE)
                        setSelectedTab(1);
                    else
                        setSelectedTab(4);
                }
            });


            txt_deliveryreturninfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (webView_view_deliveryreturninfo.getVisibility() == View.GONE)
                        setSelectedTab(2);
                    else
                        setSelectedTab(4);
                }
            });


            if (getIntent().getExtras() != null) {
                if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                    executeToGetProductDetailRequest();
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else {
                AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            btn_buyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isBuyClick = true;
                    validateAddCart();
                }
            });

            findViewById(R.id.btn_addtowishlist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBuyClick = false;
                    goToAddProductInCart();
                }
            });

            setOptionMenu();

            img_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.loadProductImage(ProductDetail_Activity.this, imageList.get(0).get("image"), img_big);
                }
            });

            img_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.loadProductImage(ProductDetail_Activity.this, imageList.get(1).get("image"), img_big);

                }
            });

            img_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.loadProductImage(ProductDetail_Activity.this, imageList.get(2).get("image"), img_big);
                }
            });

            img_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.loadProductImage(ProductDetail_Activity.this, imageList.get(3).get("image"), img_big);
                }
            });

            img_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.loadProductImage(ProductDetail_Activity.this, imageList.get(4).get("image"), img_big);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void validateAddCart() {
        try {
            goToAddProductInCart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToAddProductInCart() {
        try {

            if (AppController.selectedProductsList.size() > 0) {
                Boolean isAdded = false;
                for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                    if (AppController.selectedProductsList.get(i).getID().equals(ProductDetails.get("ProductID"))) {
                        if (AppController.selectedProductsList.get(i).getselectedSizeId().equals(selectedSizeId)) {
                            isAdded = true;
                        }
                    }
                }

                if (isAdded) {
                    if (isBuyClick) {
                        startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                    } else
                        AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product already exist in Cart. Please update quantity in Cart.");
                } else {
                    addItemInSelectedProductList();
                }
            } else {
                addItemInSelectedProductList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemInSelectedProductList() {
        try {
            boolean already_exists = false;
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                if (AppController.selectedProductsList.get(i).getName().equalsIgnoreCase(ProductDetails.get("ProductName"))) {
                    if (AppController.selectedProductsList.get(i).getselectedSizeId().equals(selectedSizeId)) {
                        already_exists = true;
                    }
                }
            }

            if (already_exists) {
                if (isBuyClick) {
                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                } else
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product already exist in Cart. Please update quantity in Cart.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);

                selectedProduct.setProductType("P");//“K” In case of Combo Product else “P” in Main Cart.
                selectedProduct.setOrderFor("WR");//this will be static WR stands for WareHouse
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setParentProductID("0");//In Case of Main Cart it would be 0. In case of Subcart it would be Combo package ID.
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductDetails.get("ProductID"));
                selectedProduct.setcode("" + ProductDetails.get("UserProdID"));
                selectedProduct.setName("" + ProductDetails.get("ProductName"));
                selectedProduct.setNewMRP("" + ProductDetails.get("NewMRP"));
                selectedProduct.setNewDP("" + ProductDetails.get("NewDP"));
                selectedProduct.setBV("" + ProductDetails.get("BV"));
                selectedProduct.setDiscountPer("" + ProductDetails.get("DiscountPer"));
                selectedProduct.setQty("1");
                selectedProduct.setBaseQty("1");
                selectedProduct.setsellerCode("" + ProductDetails.get("SellerCode"));
                selectedProduct.setCatID("" + ProductDetails.get("CatID"));
                selectedProduct.setIsshipChrg("" + ProductDetails.get("IsshipChrg"));
                selectedProduct.setShipCharge("" + ProductDetails.get("ShipCharge"));

                selectedProduct.setselectedSizeName("" + selectedSizeName);
                selectedProduct.setselectedSizeId("" + selectedSizeId);

                selectedProduct.setselectedColorName("NA");
                selectedProduct.setselectedColorId("0");
                selectedProduct.setShoppingWalletPer("" + ProductDetails.get("ShoppingWalletPer"));

//                selectedProduct.setselectedColorId("NA");
//                selectedProduct.setselectedColorId("NA");

                if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + imageList.get(0).get("image"));
                }

                AppController.selectedProductsList.add(selectedProduct);

                setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));

                if (isBuyClick) {
                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Success: You have added " + ProductDetails.get("ProductName") + " to your shopping cart!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemInSelectedWishList() {
        try {
            boolean already_exists = false;
            for (int i = 0; i < AppController.selectedWishList.size(); i++) {
                if (AppController.selectedWishList.get(i).getName().equalsIgnoreCase(ProductDetails.get("ProductName"))) {
                    if (AppController.selectedWishList.get(i).getselectedSizeId().equals(selectedSizeId)) {
                        already_exists = true;
                    }
                }
            }

            if (already_exists) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product alrady sxist in Wish list. Please update quantity in Wish list.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);

                selectedProduct.setProductType("P");//“K” In case of Combo Product else “P” in Main Cart.
                selectedProduct.setOrderFor("WR");//this will be static WR stands for WareHouse
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setParentProductID("0");//In Case of Main Cart it would be 0. In case of Subcart it would be Combo package ID.
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductDetails.get("ProductID"));
                selectedProduct.setcode("" + ProductDetails.get("UserProdID"));
                selectedProduct.setName("" + ProductDetails.get("ProductName"));
                selectedProduct.setNewMRP("" + ProductDetails.get("NewMRP"));
                selectedProduct.setNewDP("" + ProductDetails.get("NewDP"));
                selectedProduct.setBV("" + ProductDetails.get("BV"));
                selectedProduct.setDiscountPer("" + ProductDetails.get("DiscountPer"));
                selectedProduct.setQty("1");
                selectedProduct.setBaseQty("1");
                selectedProduct.setsellerCode("" + ProductDetails.get("SellerCode"));
                selectedProduct.setCatID("" + ProductDetails.get("CatID"));
                selectedProduct.setIsshipChrg("" + ProductDetails.get("IsshipChrg"));
                selectedProduct.setShipCharge("" + ProductDetails.get("ShipCharge"));

                selectedProduct.setselectedSizeName("" + selectedSizeName);
                selectedProduct.setselectedSizeId("" + selectedSizeId);

                selectedProduct.setselectedColorName("NA");
                selectedProduct.setselectedColorId("0");
//                selectedProduct.setselectedColorName("NA");
//                selectedProduct.setselectedColorId("NA");

                if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + imageList.get(0).get("image"));
                }

                AppController.selectedWishList.add(selectedProduct);

                // setBadgeCount(ProductDetail_Activity.this,(AppController.selectedWishList.size()));

                AppUtils.alertDialog(ProductDetail_Activity.this, "Success: You have added " + ProductDetails.get("ProductName") + " to your Wish list!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProductDetailRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ProductDetail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("ProductID", getIntent().getExtras().getString("productID")));

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

                            response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToGetProductDetail, TAG);
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
                                JSONArray jsonArrayProductDetail = jsonObject.getJSONArray("Data");
                                if (jsonArrayProductDetail.length() > 0) {
                                    saveProductDetails(jsonArrayProductDetail.getJSONObject(0));
                                } else {
                                    AppUtils.alertDialogWithFinish(ProductDetail_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialogWithFinish(ProductDetail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void saveProductDetails(final JSONObject jsonObjectProductDetails) {
        try {
            ProductDetails.clear();
            ProductDetails.put("ProductID", "" + jsonObjectProductDetails.getString("ProductID"));
            ProductDetails.put("UserProdID", "" + jsonObjectProductDetails.getString("UserProdID"));
            ProductDetails.put("ProductName", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductName").trim()));
            ProductDetails.put("NewMRP", "" + jsonObjectProductDetails.getString("NewMRP"));
            ProductDetails.put("NewDP", "" + jsonObjectProductDetails.getString("NewDP"));
            ProductDetails.put("BV", "" + jsonObjectProductDetails.getString("BV"));
            ProductDetails.put("DiscountPer", "" + jsonObjectProductDetails.getString("DiscountPer"));
            ProductDetails.put("ProductDesc", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductDesc")));
            ProductDetails.put("SellerCode", "" + jsonObjectProductDetails.getString("SellerCode"));
            ProductDetails.put("ProdDetail", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProdDetail")));
            ProductDetails.put("KeyFeature", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("KeyFeature")));
            ProductDetails.put("CatID", "" + jsonObjectProductDetails.getString("CatID"));
            ProductDetails.put("IsshipChrg", "" + jsonObjectProductDetails.getString("IsshipChrg"));
            ProductDetails.put("ShipCharge", "" + jsonObjectProductDetails.getString("ShipCharge"));
            ProductDetails.put("ShoppingWalletPer", "" + jsonObjectProductDetails.getString("ShoppingWalletPer"));
//            ProductDetails.put("SizeChartImg", "" + jsonObjectProductDetails.getString("SizeChartImg"));

            if (jsonObjectProductDetails.getString("IsDiscount").trim().equalsIgnoreCase("Y")) ;
            DiscDisp = true;

            imageList.clear();

            if (!jsonObjectProductDetails.getString("ImagePath").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("ImagePath"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath1").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("ImgPath1"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath2").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("ImgPath2"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath3").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("ImgPath3"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("Imgpath4").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("Imgpath4"));
                imageList.add(map);
            }

            if (AppUtils.showLogs) Log.e(TAG, "imageList..." + imageList);

            if (imageList.size() == 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + AppUtils.productImageURL() + jsonObjectProductDetails.getString("ImgPath"));
                imageList.add(map);
            }

            setProductDetails();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductDetails() {
        try {

            txt_productName.setText(ProductDetails.get("ProductName"));
            txt_productSKU.setText("SKU Code: " + ProductDetails.get("UserProdID"));

            String NewDP = "₹ " + ProductDetails.get("NewDP") + "/-";
            String NewMRP = ProductDetails.get("NewMRP");
            String DiscountPer = ProductDetails.get("DiscountPer") + "% off";
            Spannable spanString = null;

            if (DiscountPer.equalsIgnoreCase("0% off")) {
                spanString = new SpannableString("" + NewDP);
                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color__bg_orange)), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.1f), 0, NewDP.length(), 0);
            } else {
//                spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);
                spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");

                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color__bg_orange)), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.1f), 0, NewDP.length(), 0);
                StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                spanString.setSpan(boldSpan, 0, NewDP.length(), 0);

                spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
            }
            txt_productAmount.setText(spanString);
            txt_productDiscount.setText(DiscountPer);

            if (DiscDisp)
                txt_productDiscount.setVisibility(View.VISIBLE);

            webView_productdetail.loadDataWithBaseURL(null, ProductDetails.get("ProductDesc"), "text/html", "utf-8", null);
            webView_washcarinfo.loadDataWithBaseURL(null, ProductDetails.get("KeyFeature"), "text/html", "utf-8", null);
            webView_view_deliveryreturninfo.loadDataWithBaseURL(null, ProductDetails.get("DelReturnInfo"), "text/html", "utf-8", null);


            if (imageList.size() > 0) {
                //TODO Load Images

                if (imageList.size() >= 5) {
                    AppUtils.loadProductImage(this, imageList.get(4).get("image"), img_5);
                    img_5.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                    img_5.setVisibility(View.VISIBLE);
                }
                if (imageList.size() >= 4) {
                    AppUtils.loadProductImage(this, imageList.get(3).get("image"), img_4);
                    img_4.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                    img_4.setVisibility(View.VISIBLE);
                }
                if (imageList.size() >= 3) {
                    AppUtils.loadProductImage(this, imageList.get(2).get("image"), img_3);
                    img_3.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                    img_3.setVisibility(View.VISIBLE);
                }
                if (imageList.size() >= 2) {
                    AppUtils.loadProductImage(this, imageList.get(1).get("image"), img_2);
                    img_2.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                    img_2.setVisibility(View.VISIBLE);
                }
                if (imageList.size() >= 1) {
                    AppUtils.loadProductImage(this, imageList.get(0).get("image"), img_1);
                    img_1.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                    img_1.setVisibility(View.VISIBLE);
                }

                AppUtils.loadProductImage(this, imageList.get(0).get("image"), img_big);

            }
            if(!ProductDetails.get("ShoppingWalletPer").equalsIgnoreCase("")) {
                txt_ShoppingWalletPer.setVisibility(View.VISIBLE);
                txt_ShoppingWalletPer.setText("Shopping Wallet Deduction : " + ProductDetails.get("ShoppingWalletPer") + " %");
            } else {
                txt_ShoppingWalletPer.setVisibility(View.GONE);
            }
//            AppUtils.loadHomePageImage(this, ProductDetails.get("SizeChartImg"), imageView_sizechart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedTab(int selectedTab) {
        try {
            if (selectedTab == 0) {
                txt_washcareinfo.setTextColor(getResources().getColor(R.color.color_666666));
                txt_productdetail.setTextColor(getResources().getColor(android.R.color.black));
                txt_deliveryreturninfo.setTextColor(getResources().getColor(R.color.color_666666));

                txt_productdetail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_down), null);
                txt_washcareinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);
                txt_deliveryreturninfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);


                webView_productdetail.setVisibility(View.VISIBLE);
                webView_washcarinfo.setVisibility(View.GONE);
                webView_view_deliveryreturninfo.setVisibility(View.GONE);

            } else if (selectedTab == 1) {
                txt_washcareinfo.setTextColor(getResources().getColor(android.R.color.black));
                txt_productdetail.setTextColor(getResources().getColor(R.color.color_666666));
                txt_deliveryreturninfo.setTextColor(getResources().getColor(R.color.color_666666));

                txt_washcareinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_down), null);
                txt_productdetail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);
                txt_deliveryreturninfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);

                webView_productdetail.setVisibility(View.GONE);
                webView_washcarinfo.setVisibility(View.VISIBLE);
                webView_view_deliveryreturninfo.setVisibility(View.GONE);

            } else if (selectedTab == 2) {
                txt_washcareinfo.setTextColor(getResources().getColor(R.color.color_666666));
                txt_productdetail.setTextColor(getResources().getColor(R.color.color_666666));
                txt_deliveryreturninfo.setTextColor(getResources().getColor(android.R.color.black));

                txt_deliveryreturninfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_down), null);
                txt_washcareinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);
                txt_productdetail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);

                webView_productdetail.setVisibility(View.GONE);
                webView_washcarinfo.setVisibility(View.GONE);
                webView_view_deliveryreturninfo.setVisibility(View.VISIBLE);

            } else {
                txt_washcareinfo.setTextColor(getResources().getColor(R.color.color_666666));
                txt_productdetail.setTextColor(getResources().getColor(R.color.color_666666));
                txt_deliveryreturninfo.setTextColor(getResources().getColor(R.color.color_666666));

                txt_deliveryreturninfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);
                txt_washcareinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);
                txt_productdetail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_up), null);

                webView_productdetail.setVisibility(View.GONE);
                webView_washcarinfo.setVisibility(View.GONE);
                webView_view_deliveryreturninfo.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
            }

            setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            setOptionMenu();
            executeToGetProductDetailRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        selectedSizeId = "";

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    public void SetupToolbar() {
        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_login_logout);

        img_cart.setVisibility(View.VISIBLE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductDetail_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductDetail_Activity.this);
            }
        });

        setBadgeCount(ProductDetail_Activity.this, 0);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }

    public void setBadgeCount(Context context, int count) {
        try {

            ImageView imageView = (ImageView) findViewById(R.id.img_cart);

            if (imageView != null) {
                LayerDrawable icon = (LayerDrawable) imageView.getDrawable();
                //Update LayerDrawable's BadgeDrawable
                BadgeDrawable badge;// Reuse drawable if possible
                Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
                if (reuse != null && reuse instanceof BadgeDrawable) {
                    badge = (BadgeDrawable) reuse;
                } else {
                    badge = new BadgeDrawable(context);
                }
                badge.setCount("" + count);
                icon.mutate();
                icon.setDrawableByLayerId(R.id.ic_badge, badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}