package com.vpipl.mmtbusiness.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.vpipl.mmtbusiness.R;

/**
 * Created by admin on 01-05-2017.
 */
public class ProgressDialogCustom_Layout extends ProgressDialog {
    WebView webView_Pg_Animation;

    public ProgressDialogCustom_Layout(Context context) {
        super(context);
    }

    public ProgressDialogCustom_Layout(Context context, int theme) {
        super(context, theme);
    }

    public static ProgressDialog getCustomProgressDialog(Context context) {
        ProgressDialogCustom_Layout dialog = new ProgressDialogCustom_Layout(context, android.R.style.Theme_Translucent);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);

        ProgressBar mProgressSpin = (ProgressBar) findViewById(R.id.progressBar);
        mProgressSpin.setIndeterminate(true);
        mProgressSpin.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);

//        try {
//            webView_Pg_Animation = (WebView) findViewById(R.id.webView_Pg_Animation);
//            webView_Pg_Animation.setBackgroundColor(Color.BLACK);
//            webView_Pg_Animation.getSettings().setJavaScriptEnabled(true);
//            webView_Pg_Animation.loadUrl("file:///android_asset/progress.html");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}