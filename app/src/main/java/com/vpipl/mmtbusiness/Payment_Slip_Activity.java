package com.vpipl.mmtbusiness;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.vpipl.mmtbusiness.Utils.AppUtils;

public class Payment_Slip_Activity extends AppCompatActivity {

    private static final String TAG = "Payment_Activity";
    WebView web_view;
    String url = "";

    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {

        img_menu = (ImageView) findViewById(R.id.img_nav_back);

        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_login_logout);

        img_user.setVisibility(View.GONE);

        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        url = getIntent().getStringExtra("URL");

        web_view = (WebView) findViewById(R.id.web_view);

        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setBuiltInZoomControls(true);

        web_view.getSettings().setDisplayZoomControls(false);
        web_view.getSettings().setSupportZoom(true);

        web_view.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        web_view.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        web_view.getSettings().setAllowFileAccess(true);
        web_view.getSettings().setAppCacheEnabled(true);
        web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);

        web_view.setWebViewClient(new WebViewClient()
        {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                AppUtils.showProgressDialog(Payment_Slip_Activity.this);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                AppUtils.dismissProgressDialog();
            }
        });

        if (AppUtils.isNetworkAvailable(Payment_Slip_Activity.this)) {
            web_view.loadUrl(url);
        } else {
            AppUtils.alertDialogWithFinish(Payment_Slip_Activity.this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Payment_Slip_Activity.this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (web_view.canGoBack()) {
                        web_view.goBack();
                    } else {
                        finish();
                        ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}