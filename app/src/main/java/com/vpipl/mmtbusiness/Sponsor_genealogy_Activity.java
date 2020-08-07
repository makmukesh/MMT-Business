package com.vpipl.mmtbusiness;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

/**
 * Created by PC14 on 3/21/2016.
 */
public class Sponsor_genealogy_Activity extends AppCompatActivity {
    String TAG = "Generation_Structure";
    WebView webView_viewGenealogy;
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
                    startActivity(new Intent(Sponsor_genealogy_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Sponsor_genealogy_Activity.this);
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
        setContentView(R.layout.activity_binary_genealogy);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            TextView txt_heading = (TextView) findViewById(R.id.txt_heading);
            txt_heading.setText("Sponsor Genealogy");

            webView_viewGenealogy = (WebView) findViewById(R.id.webView_viewGenealogy);
            webView_viewGenealogy.getSettings().setJavaScriptEnabled(true);
            webView_viewGenealogy.getSettings().setBuiltInZoomControls(true);
            webView_viewGenealogy.getSettings().setDisplayZoomControls(true);
            webView_viewGenealogy.getSettings().setSupportZoom(true);

            webView_viewGenealogy.getSettings().setAppCacheMaxSize(15 * 1024 * 1024); // 5MB
            webView_viewGenealogy.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView_viewGenealogy.getSettings().setAllowFileAccess(true);
            webView_viewGenealogy.getSettings().setAppCacheEnabled(true);
            webView_viewGenealogy.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

            webView_viewGenealogy.getSettings().setLoadWithOverviewMode(true);
            webView_viewGenealogy.getSettings().setUseWideViewPort(true);

            webView_viewGenealogy.setWebViewClient(new WebViewClient() {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    AppUtils.showProgressDialog(Sponsor_genealogy_Activity.this);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url);

                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    AppUtils.dismissProgressDialog();
                }
            });

            if (AppUtils.isNetworkAvailable(Sponsor_genealogy_Activity.this)) {
                webView_viewGenealogy.loadUrl(QueryUtils.getViewgenealogyURL(Sponsor_genealogy_Activity.this));
            } else {
                AppUtils.alertDialog(Sponsor_genealogy_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Sponsor_genealogy_Activity.this);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView_viewGenealogy.canGoBack()) {
                        webView_viewGenealogy.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
            AppUtils.showExceptionDialog(Sponsor_genealogy_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Sponsor_genealogy_Activity.this);
        }

        System.gc();
    }

 }
