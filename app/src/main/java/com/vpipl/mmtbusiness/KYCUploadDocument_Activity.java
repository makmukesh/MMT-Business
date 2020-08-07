package com.vpipl.mmtbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC14 on 12-Apr-16.
 */
public class KYCUploadDocument_Activity extends AppCompatActivity {
    public static final String TAG = "KYCUploadDocument";
    //for image uploading
    public static final int RESULT_GALLERY = 0;
    //    layout_photoProof;
    public static final int CAMERA_REQUEST = 1;
    //    imgView_photoProof;
    static final int MEDIA_TYPE_IMAGE = 1;

    LinearLayout layout_AdrsProf, layout_IdProf, layout_profile_Photo, layout_Cancelled_Cheque;
    ImageView imgView_AdrsProf, imgView_IdProf, imgView_profile_Photo, imgView_Cancelled_Cheque;

    String whichUpload = "";
    Uri imageUri;
    Bitmap bitmap = null;

    Boolean isAddressProof = false, isIdProof = false, isCancelled_Cheque = false;

    BottomSheetDialog mBottomSheetDialog;

    //    isPhotoProof = false;
    private String selectedImagePath = "";

    String heading = "View";
    TextView txt_heading;

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
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(KYCUploadDocument_Activity.this);
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
        setContentView(R.layout.activity_kyc_document);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            layout_AdrsProf = (LinearLayout) findViewById(R.id.layout_AdrsProf);
            layout_IdProf = (LinearLayout) findViewById(R.id.layout_IdProf);
            layout_profile_Photo = (LinearLayout) findViewById(R.id.layout_profile_Photo);
            layout_Cancelled_Cheque = (LinearLayout) findViewById(R.id.layout_Cancelled_Cheque);

            imgView_AdrsProf = (ImageView) findViewById(R.id.imgView_AdrsProf);
            imgView_IdProf = (ImageView) findViewById(R.id.imgView_IdProf);
            imgView_profile_Photo = (ImageView) findViewById(R.id.imgView_profile_Photo);
            imgView_Cancelled_Cheque = (ImageView) findViewById(R.id.imgView_Cancelled_Cheque);

            mBottomSheetDialog = new BottomSheetDialog(KYCUploadDocument_Activity.this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            heading = getIntent().getStringExtra("HEADING");
            txt_heading = (TextView) findViewById(R.id.txt_heading);

            if (heading.equalsIgnoreCase("View")) {
                txt_heading.setText("View KYC Documents");
                findViewById(R.id.textView).setVisibility(View.GONE);
                findViewById(R.id.textView5).setVisibility(View.GONE);
            } else
                txt_heading.setText("Upload KYC Documents");

            if (heading.equalsIgnoreCase("Update")) {
                layout_AdrsProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAddressProof) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "AP";
                            mBottomSheetDialog.show();
                        }
                    }
                });

                layout_IdProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isIdProof) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "IP";
                            mBottomSheetDialog.show();
                        }
                    }
                });


                layout_Cancelled_Cheque.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isCancelled_Cheque) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "CC";
                            mBottomSheetDialog.show();
                        }
                    }
                });


                layout_profile_Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        whichUpload = "PP";
                        mBottomSheetDialog.show();
                    }
                });

            }

            LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_camera);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera.setVisibility(View.GONE);
            } else {
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageUri = AppUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE, TAG, KYCUploadDocument_Activity.this);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA_REQUEST);
                        mBottomSheetDialog.dismiss();
                    }
                });
            }

            LinearLayout gallery = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_gallery);
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_GALLERY);
                    ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    mBottomSheetDialog.dismiss();
                }
            });

            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                executeGetImageLoadRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    private void executeGetImageLoadRequest() {
        try {
            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(KYCUploadDocument_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));

                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "*"));

                            response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                    isAddressProof = true;
                                    imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                }

                                if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                    isIdProof = true;
                                    imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                }

                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().trim().equals("")) {
                                    imgView_profile_Photo.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().trim()));
                                }

                                if (!jsonArrayData.getJSONObject(0).getString("CancelChq").toString().trim().equals("")) {
                                    isCancelled_Cheque = true;
                                    imgView_Cancelled_Cheque.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("CancelChq").toString().trim()));
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RESULT_GALLERY) {
                    if (data != null) {
                        String filepath = AppUtils.getPath(data.getData(), KYCUploadDocument_Activity.this);

                        if (filepath.length() > 0) {
                            if (AppUtils.showLogs) Log.d("filepath", "filepath " + filepath);
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == CAMERA_REQUEST) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    public void pickImageFromGallery() {
        try {
            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            if (AppUtils.showLogs) Log.e(TAG, "Image Size before comress : " + imageFile.length());

            try {
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate -= 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate -= 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate -= 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
            }

            // Set the Image in ImageView after decoding the String
            matrix.postRotate(rotate);
            Bitmap user_image = BitmapFactory.decodeFile(selectedImagePath);
            if (AppUtils.showLogs)
                Log.e(TAG, "original user_image.getWidth()" + user_image.getWidth() + " user_image.getHeight()" + user_image.getHeight());

            if (imageFile.length() > 10000) {
                bitmap = AppUtils.compressImage(selectedImagePath);
                if (AppUtils.showLogs)
                    Log.e(TAG, "compress bitmap.getWidth()" + bitmap.getWidth() + " bitmap.getHeight()" + bitmap.getHeight());
                File fileSize = new File(AppUtils.lastCompressedImageFileName);
                if (AppUtils.showLogs)
                    Log.e(TAG, "Image Size after in what comress : " + fileSize.length());

                if (fileSize.length() > 1050000) {
                    AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Maximum image size limit 1MB.");
                } else {
                    showUploadImageDailog(bitmap);
                }
            } else {
                int width = 0, height = 0;
                if (user_image.getWidth() > 500) {
                    width = 500;
                } else {
                    width = user_image.getWidth();
                }
                if (user_image.getHeight() > 500) {
                    height = 500;
                } else {
                    height = user_image.getHeight();
                }

                bitmap = Bitmap.createBitmap(user_image, 0, 0, width, height, matrix, true);
                if (AppUtils.showLogs)
                    Log.e(TAG, "compress bitmap.getWidth()" + bitmap.getWidth() + " bitmap.getHeight()" + bitmap.getHeight());
                showUploadImageDailog(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(KYCUploadDocument_Activity.this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            if (whichUpload.equals("AP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Address Proof?");
            } else if (whichUpload.equals("IP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Identity Proof?");
            } else if (whichUpload.equals("CC")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Cancelled Cheque?");
            } else if (whichUpload.equals("PP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Photo?");
            }


            final ImageView imgView_Upload = (ImageView) dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(KYCUploadDocument_Activity.this, v);
                    dialog.dismiss();
                    executePostImageUploadRequest();

                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
           txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    private void executePostImageUploadRequest() {
        try {
            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(KYCUploadDocument_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", whichUpload));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", deviceId));
                            } catch (Exception ignored) {
                            }

                            response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                if (whichUpload.equals("AP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                        isAddressProof = true;
                                        imgView_AdrsProf.setImageResource(android.R.color.transparent);
                                        imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                    }
                                } else if (whichUpload.equals("IP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                        isIdProof = true;
                                        imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                    }
                                } else if (whichUpload.equals("PP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().trim().equals("")) {
                                        imgView_profile_Photo.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().trim()));
                                    }

                                } else if (whichUpload.equals("CC")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("CancelChq").toString().trim().equals("")) {
                                        isCancelled_Cheque = true;
                                        imgView_Cancelled_Cheque.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("CancelChq").toString().trim()));
                                    }
                                }

                            } else {
                                AppUtils.alertDialog(KYCUploadDocument_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }

        System.gc();
    }
}