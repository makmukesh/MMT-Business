package com.vpipl.mmtbusiness;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

import static android.view.View.GONE;

public class Splash_Activity extends AppCompatActivity {

    private static final String TAG = "Splash_Activity";
    public static JSONArray HeadingJarray;

    String[] PermissionGroup = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
             Manifest.permission.GET_ACCOUNTS};

    private int versionCode;
    String version;
    String currentVersion, latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        String DeviceModel = manufacturer + " " + model;

        AppController.getSpIsInstall()
                .edit().putString(SPUtils.IS_INSTALL_DeviceModel, "" + DeviceModel)
                .putString(SPUtils.IS_INSTALL_DeviceName, "" + DeviceModel)
                .commit();
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;


            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {

                      executeApplicationStatus();
                } else {
                    AppUtils.alertDialogWithFinish(Splash_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }

            } else {
                ActivityCompat.requestPermissions(this, PermissionGroup, 84);
            }

        } catch (Exception ignored) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 84) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                executeApplicationStatus();

            } else {

                Log.d(TAG, "Some permissions are not granted ask again ");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ) {
                    showDialogOK(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(Splash_Activity.this, PermissionGroup, 84);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            });
                }

                //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false

                else {
                    AppUtils.alertDialogWithFinish(this, "Go to settings and Enable permissions");
                    //proceed with logic by disabling the related features or quit the app.
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are required for use this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void executeApplicationStatus() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToApplicationStatus, "Splash");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                System.gc();
                Runtime.getRuntime().gc();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                       // executesGetVersionRequest();
                        executesGetAppStatusRequest();
                    } else {
                        final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, true);
                        TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
                        dialog4all_txt.setText(jsonObject.getString("Message"));

                        TextView txtsubmit = (TextView) dialog.findViewById(R.id.txt_submit);
                        txtsubmit.setText("Exit");
                        txtsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory(Intent.CATEGORY_HOME);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                finish();
                                System.exit(0);
                            }
                        });
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void executesGetVersionRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Versioninfo", "" + versionCode));
                            response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToGetVersion, TAG);
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

                            if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"), jsonArrayData.getJSONObject(0).getString("AppDownloadURL"));
                            } else {
                                executeTogetDrawerMenuItems();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Splash_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    public void showUpdateDialog(String Msg, final String IsCompulsory) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Update Now");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        finish();

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(GONE);
            txt_cancel.setText("Update Later");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();

                        executeTogetDrawerMenuItems();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            if (IsCompulsory.equalsIgnoreCase("N")) {
//                txt_cancel.setVisibility(View.VISIBLE);
//            }

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {

                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        HeadingJarray = jsonObject.getJSONArray("Data");
                    }

                    executeToGetDrawerShopMenu();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void executeToGetDrawerShopMenu() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodAppMenuCategory_AllCategoryNew, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {

                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                        JSONArray jsonArrayHeadingMenu = jsonObject.getJSONArray("HeadingMenu");
                        JSONArray jsonArrayCategroyMenu = jsonObject.getJSONArray("CategroyMenu");
                        JSONArray jsonArraySubcategoryLevel1 = jsonObject.getJSONArray("SubcategoryLevel1");
                        JSONArray jsonArraySubcategoryLevel2 = jsonObject.getJSONArray("SubcategoryLevel2");

                        getHeadingMenuResult(jsonArrayHeadingMenu, jsonArrayCategroyMenu, jsonArraySubcategoryLevel1, jsonArraySubcategoryLevel2);
                    } else {
                        AppUtils.alertDialog(Splash_Activity.this, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getHeadingMenuResult(JSONArray jsonArrayHeadingMenu, JSONArray jsonArrayCategroyMenu, JSONArray jsonArraySubcategoryLevel1, JSONArray jsonArraySubcategoryLevel2) {
        try {
            AppController.category1.clear();

            for (int i = 0; i < jsonArrayHeadingMenu.length(); i++) {
                JSONObject jsonObject = jsonArrayHeadingMenu.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("Heading", AppUtils.CapsFirstLetterString(jsonObject.getString("Heading").trim().toUpperCase()));
                map.put("ImgPath", jsonObject.getString("ImgPath"));
                map.put("IsComboPack", jsonObject.getString("IsComboPack"));

                AppController.category1.add(map);
            }

            AppController.category2.clear();
            for (int i = 0; i < jsonArrayCategroyMenu.length(); i++) {
                JSONObject jsonObject = jsonArrayCategroyMenu.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", "C");
                map.put("HID", jsonObject.getString("HID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("Category", AppUtils.CapsFirstLetterString(jsonObject.getString("Category")));

                AppController.category2.add(map);
            }

            AppController.category3.clear();
            for (int i = 0; i < jsonArraySubcategoryLevel1.length(); i++) {
                JSONObject jsonObject = jsonArraySubcategoryLevel1.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("SCID", jsonObject.getString("SCID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("SubCategory", AppUtils.CapsFirstLetterString(jsonObject.getString("SubCategory")));
                AppController.category3.add(map);
            }

            AppController.category4.clear();
            for (int i = 0; i < jsonArraySubcategoryLevel2.length(); i++) {
                JSONObject jsonObject = jsonArraySubcategoryLevel2.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("SCID2", jsonObject.getString("SCID2"));
                map.put("SCID", jsonObject.getString("SCID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("SubCat", AppUtils.CapsFirstLetterString(jsonObject.getString("SubCat")));
                AppController.category4.add(map);
            }

            moveNextScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startSplash(final Intent intent) {
        try {

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveNextScreen() {
        try {
          /*  if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                startService(new Intent(getBaseContext(), GetDataService.class));
            }*/
            startSplash(new Intent(Splash_Activity.this, Home_New.class));

//            Intent intent = new Intent(Splash_Activity.this, Bill_Failed_Activity.class);
//            intent.putExtra("OrderID", "11223344556677889955");
//            intent.putExtra("MobileNo", "01482252276");
//            intent.putExtra("amount", "100");
//            intent.putExtra("operator", "BSNL");
//            startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*Updated app version process*/
    public void executesGetAppStatusRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToCheckAppRunningStatus, TAG);
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

                            if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"));
                            } else if(jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("Update")) {
                                executesGetVersionRequest();
                            } else {
                                getCurrentVersionnew();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Splash_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }
    public void showUpdateDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("OK");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        finish();
                     } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(GONE);
            txt_cancel.setTextColor(getResources().getColor(R.color.color__bg_orange));
            txt_cancel.setText("Update Later");
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
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }
    private void getCurrentVersionnew(){
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo =  pm.getPackageInfo(this.getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersionnew().execute();

    }
    private class GetLatestVersionnew extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName()).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();

            }catch (Exception e){
                //   Toast.makeText(Splash_Activity.this, "App update related issue .Please try again !" , Toast.LENGTH_SHORT).show();
                //    AppUtils.alertDialogWithFinish(Splash_Activity.this , "App update related issue .Please try again !");
                Log.e("latestversionerr" , e.getMessage());
                e.printStackTrace();

            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)){
                    if(!isFinishing()){ //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    }
                }
                else {
                   // moveNextScreen();
                    executeTogetDrawerMenuItems();
                }
            }
            else
                //   background.start();
                super.onPostExecute(jsonObject);
        }
    }
    private void showUpdateDialog(){
        final Dialog dialog = new Dialog(Splash_Activity.this, R.style.ThemeDialogCustom);
        //   dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView dialog4all_txt = dialog.findViewById(R.id.tvDescription);
        Button btnNone = dialog.findViewById(R.id.btnNone);
        ImageView iv_update_image = dialog.findViewById(R.id.iv_update_image);
        dialog4all_txt.setText("An Update is available,Please Update App from Play Store.");
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getAssets(), "update.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gifDrawable != null) {
            gifDrawable.addAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("splashscreen", "Gif animation completed");
                }
            });
            iv_update_image.setImageDrawable(gifDrawable);
        }

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

}