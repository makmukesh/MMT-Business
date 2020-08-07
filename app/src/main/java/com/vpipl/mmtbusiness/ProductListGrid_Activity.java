package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Adapters.ProductListGrid_Adapter;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.BadgeDrawable;
import com.vpipl.mmtbusiness.Utils.DatabaseHandler;
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

public class ProductListGrid_Activity extends AppCompatActivity {
    public static String TAG = "ProductListGrid_Activity";

    public static Float maximumPrice = 0.0f;
    public static Float maximumDisc = 0.0f;
    public static Float minimumPrice = 0.0f;
    public static Float minimumDisc = 0.0f;
    public static String selectedminimumPrice = "0";
    public static String selectedminimumDisc = "0";
    public static String selectedmaximumPrice = "0";
    public static String selectedmaximumDisc = "0";

    public static String selectedSizeId = "0";
    public static ArrayList<HashMap<String, String>> sizeList = new ArrayList<>();
    LinearLayout layout_noData, layout_sort, layout_filter;
    LinearLayout layout_Data;

    GridView gridView_products;
    List<ProductsList> productList = new ArrayList<>();
    ProductListGrid_Adapter adapter;
    int checkedRadioButton = 0;
    BottomSheetDialog mBottomSheetDialog = null;

    int pageIndex = 1;
    int NumOfRows = 20;
    Boolean isLoadMore = false;
    int NoofProduct = 0;
    TextView txt_productHeading;
    JSONObject MAsterData = new JSONObject();

    DatabaseHandler db;

    ImageView img_menu, img_cart, img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productlistgrid_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            layout_filter = (LinearLayout) findViewById(R.id.layout_filter);
            layout_sort = (LinearLayout) findViewById(R.id.layout_sort);


            gridView_products = (GridView) findViewById(R.id.gridView_products);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            layout_Data = (LinearLayout) findViewById(R.id.layout_Data);

            txt_productHeading = (TextView) findViewById(R.id.txt_productHeading);

            db = new DatabaseHandler(this);

            if (getIntent().getExtras() != null) {
                if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    executeToGetProductListRequest();
                } else {
                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                    noDataFound();
                }
            } else {
                AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                noDataFound();
            }

            layout_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showBottomSheetDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            layout_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(ProductListGrid_Activity.this, Filter_Activity.class).putExtra("COMESFROM", ProductListGrid_Activity.class.getSimpleName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (AppController.comesFromFilter) {
                AppController.comesFromFilter = false;

                if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    productList.clear();
                    saveProductsListFilter(MAsterData);
                } else {
                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else {
                productList.clear();
                saveProductsList(MAsterData);
            }

            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheetDialog() {
        try {
            mBottomSheetDialog = new BottomSheetDialog(ProductListGrid_Activity.this);
            View view = getLayoutInflater().inflate(R.layout.bottomsheet_sort_layout, null);
            RadioGroup radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
            RadioButton radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
            RadioButton radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
            RadioButton radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
            RadioButton radioButton4 = (RadioButton) view.findViewById(R.id.radioButton4);
            radiogroup.check(radiogroup.getChildAt(checkedRadioButton).getId());

            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{
                                getResources().getColor(R.color.color_666666) //disabled
                                , getResources().getColor(R.color.colorPrimary) //enabled
                        }
                );

                radioButton1.setButtonTintList(colorStateList);//set the color tint list
                radioButton1.invalidate(); //could not be necessary
                radioButton2.setButtonTintList(colorStateList);//set the color tint list
                radioButton2.invalidate(); //could not be necessary
                radioButton3.setButtonTintList(colorStateList);//set the color tint list
                radioButton3.invalidate(); //could not be necessary
                radioButton4.setButtonTintList(colorStateList);//set the color tint list
                radioButton4.invalidate(); //could not be necessary
            }

            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup rg, int i) {
                    if (rg.getCheckedRadioButtonId() == rg.getChildAt(0).getId()) {
                        checkedRadioButton = 0;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(1).getId()) {
                        checkedRadioButton = 1;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(2).getId()) {
                        checkedRadioButton = 2;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(3).getId()) {
                        checkedRadioButton = 3;
                    }

                    if (mBottomSheetDialog != null) {
                        mBottomSheetDialog.dismiss();
                    }

                    if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this))
                    {
                        isLoadMore = false;
                        pageIndex = 1;
                        productList.clear();
                        productList = db.getAllProducts(checkedRadioButton);
                        setGridViewData();

                    } else {
                        AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                        noDataFound();
                    }
                }
            });

            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBottomSheetDialog = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProductListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", getIntent().getExtras().getString("Type")));
                            postParameters.add(new BasicNameValuePair("CategoryID", getIntent().getExtras().getString("categoryID")));

                            int sort = checkedRadioButton + 1;

                            postParameters.add(new BasicNameValuePair("Sort", "" + sort));
                            postParameters.add(new BasicNameValuePair("PageIndex", "" + pageIndex));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "" + NumOfRows));
                            postParameters.add(new BasicNameValuePair("FiltersCondition", ""));

                            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                            if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                                postParameters.add(new BasicNameValuePair("UserType", "D"));
                            else
                                postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(ProductListGrid_Activity.this, postParameters,
                                    QueryUtils.methodToGetProductList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            findViewById(R.id.progressBar2).setVisibility(View.GONE);

                            JSONObject jsonObject = new JSONObject(resultData);

                            MAsterData = jsonObject;

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

                                if (jsonArrayProductList.length() > 0) {
                                    NoofProduct = Integer.parseInt(jsonObject.getJSONArray("NoofProduct").getJSONObject(0).getString("Total"));
                                    if (jsonObject.getJSONArray("ProductHeadingName").length() > 0)
                                        txt_productHeading.setText("" + jsonObject.getJSONArray("ProductHeadingName").getJSONObject(0).getString("HeadName").replaceAll("&amp;", "&") + "");

                                    saveProductsList(jsonObject);
                                } else {
                                    noDataFound();
                                }
                            } else {
                                noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
        }
    }

    private void executeToGetProductLoadMoreListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", getIntent().getExtras().getString("Type")));
                            postParameters.add(new BasicNameValuePair("CategoryID", getIntent().getExtras().getString("categoryID")));
                            int sort = checkedRadioButton + 1;
                            postParameters.add(new BasicNameValuePair("Sort", "" + sort));
                            postParameters.add(new BasicNameValuePair("PageIndex", "" + pageIndex));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "" + NumOfRows));
                            postParameters.add(new BasicNameValuePair("FiltersCondition", ""));

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

                            response = AppUtils.callWebServiceWithMultiParam(ProductListGrid_Activity.this, postParameters, QueryUtils.methodToGetProductList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {


                            findViewById(R.id.progressBar2).setVisibility(View.GONE);

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");
                                if (jsonArrayProductList.length() > 0) {
                                    saveProductsList(jsonObject);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {

            JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

            if (pageIndex == 1) {
                productList.clear();
            }

            db.clearDB();

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);
                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                products.setBV("" + jsonObjectProducts.getString("BV"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
//                products.setImagePath("" + AppUtils.productImageURL() + jsonObjectProducts.getString("NewImgPath"));
                products.setImagePath(AppUtils.productImageURL() + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));

//                products.setselectedSizeId("" + jsonObjectProducts.getString("SizeIDs"));
//                products.setselectedColorId("" + jsonObjectProducts.getString("ColorIDs"));

                productList.add(products);

                db.addProducts(products);
            }

            sizeList.clear();
//            for (int bb = 0; bb < jsonArrayProductSize.length(); bb++) {
//
//                JSONObject jsonObjectProductSize = jsonArrayProductSize.getJSONObject(bb);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("SizeID", "" + jsonObjectProductSize.getString("SizeID"));
//                map.put("Size", "" + jsonObjectProductSize.getString("Size"));
//                sizeList.add(map);
//            }

            if (isLoadMore) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            } else {
                setGridViewData();
            }

            if (AppController.filterList1.size() == 0) {
                createFilterList(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
        }
    }

    private void saveProductsListFilter(final JSONObject jsonObject)
    {
        try {
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

            if (pageIndex == 1) {
                productList.clear();
            }

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);

                if (Integer.parseInt((selectedminimumPrice)) <= Integer.parseInt(jsonObjectProducts.getString("NewDP"))
                        && Integer.parseInt(jsonObjectProducts.getString("NewDP")) <= Integer.parseInt((selectedmaximumPrice))) {
                    if (Integer.parseInt((selectedminimumDisc)) <= Integer.parseInt(jsonObjectProducts.getString("DiscountPer"))
                            && Integer.parseInt(jsonObjectProducts.getString("DiscountPer")) <= Integer.parseInt((selectedmaximumPrice))) {
//                        if (selectedColorId.equalsIgnoreCase("0") && selectedSizeId.equalsIgnoreCase("0"))
                        if (selectedSizeId.equalsIgnoreCase("0")) {
                            ProductsList products = new ProductsList();
                            products.setID("" + jsonObjectProducts.getString("ProdID"));
                            products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                            products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                            products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                            products.setBV("" + jsonObjectProducts.getString("BV"));
                            products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                            products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
//                            products.setImagePath("" + AppUtils.productImageURL() + jsonObjectProducts.getString("NewImgPath"));
                            products.setImagePath("" + jsonObjectProducts.getString("NewImgPath"));
                            products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                            products.setNewDP("" + jsonObjectProducts.getString("NewDP"));

                            productList.add(products);
                        } else if (!selectedSizeId.equalsIgnoreCase("0")) {
                            if (jsonObjectProducts.getString("SizeIDs").contains(selectedSizeId))
                            {
                                ProductsList products = new ProductsList();
                                products.setID("" + jsonObjectProducts.getString("ProdID"));
                                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                                products.setBV("" + jsonObjectProducts.getString("BV"));
                                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                                products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
//                                products.setImagePath("" + AppUtils.productImageURL() + jsonObjectProducts.getString("NewImgPath"));
                                products.setImagePath("" + jsonObjectProducts.getString("NewImgPath"));
                                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));

                                productList.add(products);
                            }
                        }
                    }
                }
            }

            if (isLoadMore) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();

//                    gridView_products.post(new Runnable(){
//                        public void run() {
//                            gridView_products.setSelection(gridView_products.getCount() - 1);
//                        }});
                }
            } else {
                setGridViewData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductListGrid_Activity.this);
        }
    }

    private void createFilterList(final JSONObject jsonObject) {
        try {

            int MinPrice = 0, MaxPrice = 0, MinDiscount = 0, Maxdiscount = 0, DiffPrice = 0, PriceRangeGap = 0, PriceStartRange = 0, DiscStartRange = 0;
            JSONArray jsonArrayPriceAndDisount = jsonObject.getJSONArray("PriceAndDisount");

            MinPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinPrice"));
            MaxPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MaxPrice"));
            MinDiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinDiscount"));
            Maxdiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("Maxdiscount"));

            minimumPrice = (float) MinPrice;
            maximumPrice = (float) MaxPrice;

            maximumDisc = (float) Maxdiscount;
            minimumDisc = (float) MinDiscount;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setGridViewData() {
        try {
            layout_noData.setVisibility(View.GONE);
            layout_Data.setVisibility(View.VISIBLE);

            adapter = new ProductListGrid_Adapter(ProductListGrid_Activity.this, productList, "Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(ProductListGrid_Activity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                }
            });

            gridView_products.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    int threshold = 1;
                    int count = gridView_products.getCount();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (gridView_products.getLastVisiblePosition() >= count - threshold) {
                            if (NoofProduct != productList.size()) {
                                if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                                    isLoadMore = true;
                                    pageIndex++;
                                    executeToGetProductLoadMoreListRequest();
                                } else {
                                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noDataFound() {
        try {
            layout_filter.setVisibility(View.GONE);
            layout_sort.setVisibility(View.GONE);

            layout_noData.setVisibility(View.VISIBLE);
            layout_Data.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
            else
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));

            setBadgeCount(ProductListGrid_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (AppController.comesFromFilter)
            {
                AppController.comesFromFilter = false;

                if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    saveProductsListFilter(MAsterData);
                } else {
                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            AppUtils.dismissProgressDialog();

            DatabaseHandler sqLiteHelper = DatabaseHandler.getInstance(this);
            sqLiteHelper.clearDB();
        } catch (Exception e) {
            e.printStackTrace();
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
                startActivity(new Intent(ProductListGrid_Activity.this, AddCartCheckOut_Activity.class));
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductListGrid_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductListGrid_Activity.this);
            }
        });

        setBadgeCount(ProductListGrid_Activity.this, 0);

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