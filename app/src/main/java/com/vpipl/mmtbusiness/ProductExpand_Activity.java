package com.vpipl.mmtbusiness;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.mmtbusiness.Adapters.ExpandList_Parent_Adapter;
import com.vpipl.mmtbusiness.Adapters.ImageSliderViewPagerAdapter;
import com.vpipl.mmtbusiness.Utils.AnimatedExpandableListView;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.BadgeDrawable;
import com.vpipl.mmtbusiness.Utils.CirclePageIndicator;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.model.ExpandList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProductExpand_Activity extends AppCompatActivity {

    public String TAG = "ProductExpand_Activity";
    public ViewGroup FooterView = null, HeaderView = null;

    TextView speak;
    private static final int REQUEST_CODE = 1234;

    CirclePageIndicator imagePageIndicator;
    ViewPager image_viewPager;
    List<ExpandList> expandListing = new ArrayList<>();
    AnimatedExpandableListView expandSubCatListView;
    TextView txt_Heading;
    EditText et_search;
    int currentPage = 0;
    Timer timer;
    FrameLayout touchInterceptor;
    LinearLayout LLOTherCategories;
    String Heading_CID = "0", Heading_type = "";

    ImageView img_menu, img_cart, img_user;

    LinearLayout ll_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productexpand_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            et_search = (EditText) findViewById(R.id.et_search);

            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        AppUtils.hideKeyboardOnClick(ProductExpand_Activity.this, view);
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            expandSubCatListView = (AnimatedExpandableListView) findViewById(R.id.expandSubCatListView);

            setExpandListData();

            /*Searching by voice code*/
            speak = (TextView) findViewById(R.id.speakButton);

            final Animation myAnim = AnimationUtils.loadAnimation(ProductExpand_Activity.this, R.anim.anim_in);
            speak.startAnimation(myAnim);

            PackageManager pm = getPackageManager();
            final List <ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activities.size() == 0) {
                        //   speak.setEnabled(false);
                        // speak.setText("Recognizer not present");
                        Toast.makeText(ProductExpand_Activity.this, "Voice Recognizer not present", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startVoiceRecognitionActivity();
                    }

                }
            });

            if (activities.size() == 0)
            {
                //  speak.setEnabled(false);
                // speak.setText("Recognizer not present");
                // Toast.makeText(this, "Recognizer not present", Toast.LENGTH_SHORT).show();
            }
            et_search.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    // TODO Auto-generated method stub
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    // TODO Auto-generated method stub
                }
                @Override
                public void afterTextChanged(Editable s)
                {
                    // TODO Auto-generated method stub
                    // AppUtils.hideKeyboardOnClick(Home_Activity.this, view);


                    //    speak.setEnabled(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductExpand_Activity.this);
        }
    }
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList < String > matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty())
            {
                et_search.setText("");
                String Query = matches.get(0);
                et_search.setText(Query);
                performSearch();
                // speak.setEnabled(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setExpandListData() {
        try {
            expandListing.clear();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            FooterView = (ViewGroup) inflater.inflate(R.layout.productexpandlist_footer, expandSubCatListView, false);
            HeaderView = (ViewGroup) inflater.inflate(R.layout.productexpand_header, expandSubCatListView, false);
            expandSubCatListView.addHeaderView(HeaderView, null, false);
            expandSubCatListView.addFooterView(FooterView, null, false);

            setImageSlider();

            txt_Heading = (TextView) HeaderView.findViewById(R.id.txt_Heading);

            if (getIntent().getExtras().getString("CID").equals("")) {
                for (int i = 0; i < AppController.category1.size(); i++) {
                    if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(i).get("HID"))) {
                        txt_Heading.setText("In " + AppController.category1.get(i).get("Heading"));

                        Heading_CID = AppController.category1.get(i).get("HID");
                        Heading_type = AppController.category1.get(i).get("Type");

                        for (int j = 0; j < AppController.category2.size(); j++) {
                            if (AppController.category1.get(i).get("HID").equals(AppController.category2.get(j).get("HID"))) {
                                List<ExpandList> cat2 = new ArrayList<>();

                                for (int k = 0; k < AppController.category3.size(); k++) {
                                    List<ExpandList> cat3 = new ArrayList<>();

                                    if (AppController.category2.get(j).get("CID").equals(AppController.category3.get(k).get("CID"))) {
                                        for (int l = 0; l < AppController.category4.size(); l++) {
                                            if (AppController.category3.get(k).get("SCID").equals(AppController.category4.get(l).get("SCID"))) {
                                                cat3.add(new ExpandList(AppController.category4.get(l).get("SubCat"), "", "", AppController.category4.get(l).get("SCID2"), AppController.category4.get(l).get("Type"), "False", new ArrayList<ExpandList>()));
                                            }
                                        }
                                        cat2.add(new ExpandList(AppController.category3.get(k).get("SubCategory"), "", "", AppController.category3.get(k).get("SCID"), AppController.category3.get(k).get("Type"), "False", cat3));
                                    }
                                }
                                expandListing.add(new ExpandList(AppController.category2.get(j).get("Category"), "", "", AppController.category2.get(j).get("CID"), AppController.category2.get(j).get("Type"), "False", cat2));
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < AppController.category2.size(); i++) {
                    if (getIntent().getExtras().getString("CID").equals(AppController.category2.get(i).get("CID"))) {
                        for (int a = 0; a < AppController.category1.size(); a++) {
                            if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(a).get("HID"))) {
//                              txt_Heading.setText("In "+AppController.category1.get(a).get("Heading")+" > "+AppController.category2.get(i).get("Category"));
                                txt_Heading.setText("In " + AppController.category2.get(i).get("Category"));
                                Heading_CID = AppController.category2.get(i).get("CID");
                                Heading_type = AppController.category2.get(i).get("Type");
                            }
                        }

                        for (int j = 0; j < AppController.category3.size(); j++) {
                            if (getIntent().getExtras().getString("CID").equals(AppController.category3.get(j).get("CID"))) {
                                List<ExpandList> cat1 = new ArrayList<>();
                                for (int k = 0; k < AppController.category4.size(); k++) {
                                    if (AppController.category3.get(j).get("SCID").equals(AppController.category4.get(k).get("SCID"))) {
                                        cat1.add(new ExpandList(AppController.category4.get(k).get("SubCat"), "", "", AppController.category4.get(k).get("SCID2"), AppController.category4.get(k).get("Type"), "False", new ArrayList<ExpandList>()));
                                    }
                                }
                                expandListing.add(new ExpandList(AppController.category3.get(j).get("SubCategory"), "", "", AppController.category3.get(j).get("SCID"), AppController.category3.get(j).get("Type"), "False", cat1));
                            }
                        }
                    }
                }
            }

            txt_Heading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Heading_CID.equalsIgnoreCase("0") && !TextUtils.isEmpty(Heading_type)) {
                        Intent intent = new Intent(ProductExpand_Activity.this, ProductListGrid_Activity.class);
                        intent.putExtra("Type", "" + Heading_type);
                        intent.putExtra("categoryID", "" + Heading_CID);
                        startActivity(intent);
                    }
                }
            });

            expandSubCatListView.setAdapter(new ExpandList_Parent_Adapter(ProductExpand_Activity.this, expandListing, "ProductExpand"));
            expandSubCatListView.setGroupIndicator(null);

            for (int aaa = 0; aaa <expandListing.size(); aaa++)
            {
                if (expandListing.get(aaa).getExpandList() != null && expandListing.get(aaa).getExpandList().size() > 0)
                    expandSubCatListView.expandGroupWithAnimation(aaa);
            }


            expandSubCatListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (expandSubCatListView.isGroupExpanded(groupPosition)) {
                        expandSubCatListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        expandSubCatListView.expandGroupWithAnimation(groupPosition);
                    }

                    if (expandListing.get(groupPosition).getExpandList() != null && expandListing.get(groupPosition).getExpandList().size() == 0) {
                        try {
                            for (int j = 0; j < AppController.category1.size(); j++) {
                                if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(j).get("HID"))) {
                                    if (AppUtils.showLogs) Log.e(TAG, "in if called....");
                                    Intent intent = new Intent(ProductExpand_Activity.this, ProductListGrid_Activity.class);
                                    intent.putExtra("Type", "" + expandListing.get(groupPosition).getType());
                                    intent.putExtra("categoryID", "" + expandListing.get(groupPosition).getId());
                                    startActivity(intent);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return true;
                }
            });


            TableLayout ll = (TableLayout) FooterView.findViewById(R.id.displayLinear);

            float sp = 5;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (10 * getResources().getDisplayMetrics().scaledDensity);

            for (int aaa = 0; aaa < AppController.category2.size(); aaa++) {
                String category_name = AppController.category2.get(aaa).get("Category");
                String CID = AppController.category2.get(aaa).get("CID");

                final TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.WHITE);

                row1.setId(Integer.parseInt(CID));

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);

                A1.setText("-");
                B1.setText(category_name);

                A1.setPadding(px, px_right, px_right, px_right);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//                A1.setGravity(Gravity.CENTER);
//                B1.setGravity(Gravity.CENTER);

                A1.setTextColor(getResources().getColor(android.R.color.darker_gray));
                B1.setTextColor(getResources().getColor(R.color.app_color_grayicon));

                row1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductExpand_Activity.this, ProductExpand_Activity.class);
                        intent.putExtra("HID", "1");
                        intent.putExtra("CID", "" + row1.getId());
                        startActivity(intent);
                        finish();
                    }
                });

                row1.addView(A1);
                row1.addView(B1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(getResources().getColor(R.color.color_eeeeee));

                ll.addView(row1);
                ll.addView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSlider() {
        try {
            image_viewPager = (ViewPager) HeaderView.findViewById(R.id.pager_slider);
            imagePageIndicator = (CirclePageIndicator) HeaderView.findViewById(R.id.imagePageIndicator);

            image_viewPager.setAdapter(new ImageSliderViewPagerAdapter(ProductExpand_Activity.this));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setFillColor(getResources().getColor(R.color.color__bg_orange));
            imagePageIndicator.setStrokeColor(getResources().getColor(R.color.color_cccccc));
            imagePageIndicator.setStrokeWidth(0.5f);
            imagePageIndicator.setRadius(6 * density);
            imagePageIndicator.setViewPager(image_viewPager);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == Home_New.imageSlider.size()) {
                        currentPage = 0;
                    }
                    image_viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 500, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));

//                if (AppUtils.isNetworkAvailable(ProductExpand_Activity.this)) {
//                    setBadgeCount(ProductExpand_Activity.this, 0);
//                    executeToGetCartItemCount();

//                } else {
//
//                    AppUtils.alertDialog(ProductExpand_Activity.this, getResources().getString(R.string.txt_networkAlert));
//                }

            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
            }
            setBadgeCount(ProductExpand_Activity.this, (AppController.selectedProductsList.size()));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void executeToGetCartItemCount() {
//        try {
//            if (AppUtils.isNetworkAvailable(ProductExpand_Activity.this)) {
//                new AsyncTask<Void, Void, String>() {
//                    protected void onPreExecute() {
//                        AppUtils.showProgressDialog(ProductExpand_Activity.this);
//                    }
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        String response = "";
//                        try {
//                            List<NameValuePair> postParameters = new ArrayList<>();
//                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
//                            response = AppUtils.callWebServiceWithMultiParam(ProductExpand_Activity.this, postParameters, QueryUtils.methodToGetCartTotalCount, TAG);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return response;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String resultData) {
//                        try {
//                            AppUtils.dismissProgressDialog();
//
//                            JSONObject jsonObject = new JSONObject(resultData);
//                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
//
//                                if (jsonObject.getJSONArray("Data").length() > 0) {
//                                    if (!jsonObject.getJSONArray("Data").getJSONObject(0).getString("TotalCartCount").equals(""))
//
//                                        setBadgeCount(ProductExpand_Activity.this, Integer.parseInt(jsonObject.getJSONArray("Data").getJSONObject(0).getString("TotalCartCount")));
//
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductExpand_Activity.this);
        }
    }


    public void SetupToolbar() {
        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);        img_user = (ImageView) findViewById(R.id.img_login_logout);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);

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
                startActivity(new Intent(ProductExpand_Activity.this, AddCartCheckOut_Activity.class));
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductExpand_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductExpand_Activity.this);
            }
        });

        setBadgeCount(ProductExpand_Activity.this, 0);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }

    public void setBadgeCount(Context context, int count) {
        try {

            ImageView imageView = (ImageView) findViewById(R.id.img_cart);
//
//
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

    private void performSearch() {
        if (et_search.getText().toString().isEmpty()) {
            et_search.setError("Please Enter Search keyword.");
            et_search.requestFocus();
        } else {
            startActivity(new Intent(this, SearchProducts_Activity.class).putExtra("Keyword", et_search.getText().toString()));
        }
    }

}
