package com.vpipl.mmtbusiness.Utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.vpipl.mmtbusiness.AppController;
import com.vpipl.mmtbusiness.Home_New;
import com.vpipl.mmtbusiness.R;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 29-04-2017.
 */
public class AppUtils {

  public static final String IMAGE_DIRECTORY_NAME = "MMT Business";
  public static final int MEDIA_TYPE_IMAGE = 1;

  public static ProgressDialog progressDialog;
  public static boolean showLogs = true;
  public static boolean CheckOTP = true;
  public static String lastCompressedImageFileName = "";

  public static String mPANPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
  public static String mPINCodePattern = "^[1-9][0-9]{5}$";

     /*    <!--Live URL string-->
    <string name="serviceAPIURL">http://www.suhanacare.in/Webservice/Service.asmx/</string>
    <string name="ViewgenealogyURL">http://suhanacare.in/Admin/apprefTree.aspx?formno=</string>
    <string name="ViewBinarygenealogyURL">http://suhanacare.in/Admin/appBinaryTree.aspx?formno=</string>

    <string name="serviceAPIURL">https://bprime.org/webservice/service.asmx/</string>
    <string name="productImageURL">https://bprime.org/</string>
    <string name="ViewincentiveURL">https://bprime.org/Dashboard/DailyStatement.aspx?PNo=</string>
    <string name="ViewgenealogyURL">https://bprime.org/Admin/AppReftree.aspx?formno=</string>
    <string name="ViewBinarygenealogyURL">https://bprime.org/Admin/AppBinaryTree.aspx?formno=</string>*/

  public static String serviceAPIURL = "http://mmtbusiness.in//Webservice/Service.asmx/";
  public static String productImageURL = "http://mmtbusiness.in/";
  public static String ViewincentiveURL = "http://mmtbusiness.in/Dashboard/DailyStatement.aspx?PNo=";
  public static String ViewgenealogyURL = "http://mmtbusiness.in/Admin/apprefTree.aspx?formno=";
  public static String ViewBinarygenealogyURL = "http://mmtbusiness.in/Admin/appBinaryTree.aspx?formno=";

  public static String serviceAPIURL() {
    return serviceAPIURL;
  }
  public static String productImageURL() {        return productImageURL;    }
  public static String ViewincentiveURL() {        return ViewincentiveURL;    }
  public static String ViewgenealogyURL() {        return ViewgenealogyURL;    }
  public static String ViewBinarygenealogyURL() {        return ViewBinarygenealogyURL;    }


  public static void hideKeyboardOnClick(Context con, View view) {
    try {
      InputMethodManager inputManager = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(con);
    }
  }

  public static String CapsFirstLetterString(String string) {
    return WordUtils.capitalizeFully(string);
  }


  public static ViewPropertyAnimation.Animator getAnimatorImageLoading() {
    ViewPropertyAnimation.Animator animationObject = null;

    try {
      animationObject = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
          view.setAlpha(0f);

          ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
          fadeAnim.setDuration(2500);
          fadeAnim.start();
        }
      };
    } catch (Exception e) {
      e.printStackTrace();
    }

    return animationObject;
  }

  public static String isNetworkWifiMobileData(Context context) {
    String isType = "";
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

      if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
        isType = "W";
      } else if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
        isType = "M";
      } else {
        isType = "MW";
      }
    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(context);
    }
    return isType;
  }

  public static String getAppVersionName(Context context) {
    PackageManager manager = context.getPackageManager();
    String versionName = "";
    try {
      PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
      versionName = info.versionName;
    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(context);
    }
    return versionName;
  }

  public static void showProgressDialog(Context conn) {
    try {
      if (progressDialog != null) {
        if (!progressDialog.isShowing()) {
          if (!((Activity) conn).isFinishing()) {
            progressDialog.show();
          }
        }
      } else {
//                progressDialog = ProgressDialogCustom_Layout.getCustomProgressDialog(conn);

        progressDialog = new ProgressDialog(conn);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setTitle("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        progressDialog.show();

        if (!progressDialog.isShowing()) {
          //   progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
          progressDialog.show();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showExceptionDialog(Context con) {
    try {
      AppUtils.dismissProgressDialog();
      AppUtils.alertDialog(con, "Sorry, There seems to be some problem. Try again later");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getDateFromAPIDate(String date) {
    try {
      if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
      Calendar cal = Calendar.getInstance(Locale.ENGLISH);

      if (date.contains("/Date("))
        cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
      else
        cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

      date = DateFormat.format("dd-MMM-yyyy", cal).toString().trim();

      if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return date;
  }


  public static void alertDialogWithFinish(final Context context, String message) {
    try {
      final Dialog dialog = createDialog(context, true);
      TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
      dialog4all_txt.setText(message);
      dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dialog.dismiss();
          //((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
          ((Activity) context).finish();
        }
      });

      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(context);
    }
  }

  public static void dismissProgressDialog() {
    try {
      if (progressDialog != null) {
        if (progressDialog.isShowing()) {
          progressDialog.dismiss();
          progressDialog = null;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void alertDialog(Context context, String message) {
    try {
      final Dialog dialog = createDialog(context, true);
      TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
      dialog4all_txt.setText(message);
      dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dialog.dismiss();
        }
      });

      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Uri getOutputMediaFileUri(int type, String PageName, Context context) {
    // External sdcard location
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        if (AppUtils.showLogs)
          Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
        return null;
      }
    }

    // Create a media file name
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE)

    {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + time + ".jpg");
    } else {
      return null;
    }

    if (AppUtils.showLogs) Log.v(PageName + "  mediaFile", "" + mediaFile);
    if (AppUtils.showLogs) Log.v(PageName + "  uri is", "" + Uri.fromFile(mediaFile));
    if (AppUtils.showLogs)
      Log.v(PageName + "  uri is path", "" + Uri.fromFile(mediaFile).getPath());

    return Uri.fromFile(mediaFile);
//        Uri photoURI = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".provider",mediaFile);
//        return  photoURI;
  }

  public static Dialog createDialog(Context context, boolean single) {
    final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false);
    if (single)
      dialog.setContentView(R.layout.custom_dialog_one);
    else
      dialog.setContentView(R.layout.custom_dialog_two);

    return dialog;
  }

  public static Bitmap getBitmapFromString(String imageString) {
    Bitmap bitmap = null;
    try {
      byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
      bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bitmap;
  }

  public static String getPath(Uri uri, Context context) {
    if (uri == null)
      return null;

    if (AppUtils.showLogs) Log.d("URI", uri + "");
    String[] projection = {MediaStore.Images.Media.DATA};
    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
    if (cursor != null) {
      int column_index = cursor
              .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      String temp = cursor.getString(column_index);
      cursor.close();
      if (AppUtils.showLogs) Log.v("temp", "" + temp);
      return temp;
    } else
      return null;
  }

  public static Bitmap compressImage(String filePath) {
    Bitmap scaledBitmap = null;

    BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true;
    Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

    int actualHeight = options.outHeight;
    int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

    float maxHeight = 500.0f;
    float maxWidth = 500.0f;
    float imgRatio = actualWidth / actualHeight;
    float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
      if (imgRatio < maxRatio) {
        imgRatio = maxHeight / actualHeight;
        actualWidth = (int) (imgRatio * actualWidth);
        actualHeight = (int) maxHeight;
      } else if (imgRatio > maxRatio) {
        imgRatio = maxWidth / actualWidth;
        actualHeight = (int) (imgRatio * actualHeight);
        actualWidth = (int) maxWidth;
      } else {
        actualHeight = (int) maxHeight;
        actualWidth = (int) maxWidth;

      }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image

    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true;
    options.inInputShareable = true;
    options.inTempStorage = new byte[16 * 1024];

    try {
//          load the bitmap from its path
      bmp = BitmapFactory.decodeFile(filePath, options);
    } catch (OutOfMemoryError exception) {
      exception.printStackTrace();

    }
    try {
      scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
    } catch (OutOfMemoryError exception) {
      exception.printStackTrace();
    }

    float ratioX = actualWidth / (float) options.outWidth;
    float ratioY = actualHeight / (float) options.outHeight;
    float middleX = actualWidth / 2.0f;
    float middleY = actualHeight / 2.0f;

    Matrix scaleMatrix = new Matrix();
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

    Canvas canvas = new Canvas(scaledBitmap);
    canvas.setMatrix(scaleMatrix);
    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
    ExifInterface exif;
    try {
      exif = new ExifInterface(filePath);

      int orientation = exif.getAttributeInt(
              ExifInterface.TAG_ORIENTATION, 0);
      if (AppUtils.showLogs) Log.d("EXIF", "Exif: " + orientation);
      Matrix matrix = new Matrix();
      if (orientation == 6) {
        matrix.postRotate(90);
        if (AppUtils.showLogs) Log.d("EXIF", "Exif: " + orientation);
      } else if (orientation == 3) {
        matrix.postRotate(180);
        if (AppUtils.showLogs) Log.d("EXIF", "Exif: " + orientation);
      } else if (orientation == 8) {
        matrix.postRotate(270);
        if (AppUtils.showLogs) Log.d("EXIF", "Exif: " + orientation);
      }
      scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
              scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
              true);
    } catch (IOException e) {
      e.printStackTrace();
    }

    FileOutputStream out = null;
    lastCompressedImageFileName = getFilename();
    try {
      out = new FileOutputStream(lastCompressedImageFileName);

//          write the compressed bitmap at the destination specified by filename.
      scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return scaledBitmap;
  }

  public static String getFilename() {
    File file = new File(Environment.getExternalStorageDirectory().getPath(), "Real T");
    if (!file.exists()) {
      file.mkdirs();
    }
    return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    final float totalPixels = width * height;
    final float totalReqPixelsCap = reqWidth * reqHeight * 2;
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
      inSampleSize++;
    }

    return inSampleSize;
  }

  public static String getBase64StringFromBitmap(Bitmap bitmap) {
    String imageString = "";
    try {
      if (bitmap != null) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] image = stream.toByteArray();
        if (AppUtils.showLogs)
          Log.e("AppUtills", "Image Size after comress : " + image.length);
        imageString = Base64.encodeToString(image, Base64.DEFAULT);
      } else {
        imageString = "";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return imageString;
  }

  public static boolean isValidMail(String email) {
    return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
  }

  public static boolean isNetworkAvailable(Context context) {
    boolean connected = false;
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
      return connected;
    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(context);
    }
    return connected;
  }

  public static String callWebServiceWithMultiParam(Context con, List<NameValuePair> postParameters, String methodName, String pageName) {

    BufferedReader in = null;

    try {

      HttpClient client = new DefaultHttpClient();

      String result = null;

      if (AppUtils.isNetworkAvailable(con)) {

        AppUtils.printQuery(pageName + "::" + methodName + ":: Parameters ::", postParameters);

        Log.e(pageName, "Executing URL..." + AppUtils.serviceAPIURL() + methodName);

        HttpPost request = new HttpPost(AppUtils.serviceAPIURL()  + methodName);

        UrlEncodedFormEntity formEntity = null;
        try {
          formEntity = new UrlEncodedFormEntity(postParameters);
        } catch (Exception e) {
          e.printStackTrace();
        }

        request.setEntity(formEntity);

        HttpResponse response = null;
        try {

          response = client.execute(request);

        } catch (Exception e) {
          e.printStackTrace();
        }
        try {
          in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (Exception e) {
          e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        String line;
        String NL = System.getProperty("line.separator");

        try {
          while ((line = in.readLine()) != null) {
            sb.append(line).append(NL);
          }

        } catch (Exception e) {
          e.printStackTrace();
        }


        try {
          in.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

        try {
          if (AppUtils.showLogs)
            Log.e(pageName + "", "Response..." + methodName + "...." + sb.toString().trim());
          result = sb.toString().trim();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        AppUtils.alertDialog(con, con.getResources().getString(R.string.txt_networkAlert));
      }

      return result;

    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void showDialogSignOut(final Context con) {
    try {
      final Dialog dialog = AppUtils.createDialog(con, false);
      dialog.setCancelable(false);

      TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
      txt_DialogTitle.setText(Html.fromHtml(con.getResources().getString(R.string.txt_signout_message)));

      TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
      txt_submit.setText(con.getResources().getString(R.string.txt_signout_yes));
      txt_submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          try {
            dialog.dismiss();

            AppController.getSpUserInfo().edit().clear().commit();
            AppController.getSpIsLogin().edit().clear().commit();

            Intent intent = new Intent(con, Home_New.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("SendToHome",true);
            con.startActivity(intent);
            ((Activity) con).finish();

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);

      txt_cancel.setText(con.getResources().getString(R.string.txt_signout_no));
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
      AppUtils.showExceptionDialog(con);
    }
  }

  public static void printQuery(String pageName, List<NameValuePair> postParam) {
    try {
      String query = "";
      for (int i = 0; i < postParam.size(); i++) {
        query = query + " " + postParam.get(i).getName() + " : " + postParam.get(i).getValue();
      }

      if (AppUtils.showLogs) Log.e(pageName, "Executing Parameters..." + query);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  ///////////////////////////////////////////////

  public static void loadProductImage(Context conn, String imageURL, ImageView imageView) {
    try {
      Glide.with(conn)
              .load(imageURL)
              .placeholder(R.drawable.ic_no_image)
              .error(R.drawable.ic_no_image)
              .skipMemoryCache(false)
              .diskCacheStrategy(DiskCacheStrategy.ALL)
              .into(imageView);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void loadSlidingImage(Context conn, String imageURL, ImageView imageView) {
    try {
      Glide.with(conn)
              .load(imageURL)
              .skipMemoryCache(false)
              .diskCacheStrategy(DiskCacheStrategy.ALL)
              .into(imageView);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String generateRandomAlphaNumeric(int length) {
    SecureRandom rnd = new SecureRandom();
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder(length);

    try {
      for (int i = 0; i < length; i++) {
        sb.append(AB.charAt(rnd.nextInt(AB.length())));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static void loadHomePageImage(Context conn, String imageURL, ImageView imageView) {
    try {


      Glide.with(conn)
              .load(imageURL)
              .skipMemoryCache(false)
              .diskCacheStrategy(DiskCacheStrategy.ALL)
              .into(imageView);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getDateandTimeFromAPIDate(String date) {
    try {
      if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
      Calendar cal = Calendar.getInstance(Locale.ENGLISH);

      if (date.contains("/Date("))
        cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
      else
        cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

      date = DateFormat.format("hh:MM aaa, dd MMMM yyyy", cal).toString();

      if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return date;
  }
  public static String getDateandDayFromAPIDate(String date) {
    try {
      if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
      Calendar cal = Calendar.getInstance(Locale.ENGLISH);

      if (date.contains("/Date("))
        cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
      else
        cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

      date = DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();

      if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return date;
  }
  public static void setActionbarTitle(ActionBar bar, Context con) {
    try {
      bar.setDisplayShowHomeEnabled(true);
      bar.setDisplayHomeAsUpEnabled(true);
      bar.setHomeButtonEnabled(true);
      bar.setTitle("MMT Business");

      bar.setSubtitle("");

      final Drawable upArrow;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        upArrow = con.getDrawable(R.drawable.ic_arrow_back_white_px);
      } else
        upArrow = con.getResources().getDrawable(R.drawable.ic_arrow_back_white_px);

      upArrow.setColorFilter(con.getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
      bar.setHomeAsUpIndicator(upArrow);

    } catch (Exception e) {
      e.printStackTrace();
      AppUtils.showExceptionDialog(con);
    }
  }
  public static String getDateFromAPIDateTime(String date) {
    try {
      if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
      Calendar cal = Calendar.getInstance(Locale.ENGLISH);

      if (date.contains("/Date("))
        cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
      else
        cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

      date = DateFormat.format("dd-MMM-yyyy hh:mm:ss a", cal).toString();

      if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return date;
  }
}