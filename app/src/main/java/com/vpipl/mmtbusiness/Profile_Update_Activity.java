package com.vpipl.mmtbusiness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vpipl.mmtbusiness.SMS.MySMSBroadcastReceiver;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.Utils.Cache;
import com.vpipl.mmtbusiness.Utils.CircularImageView;
import com.vpipl.mmtbusiness.Utils.QueryUtils;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.Utils.SmsListener;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by PC14 on 3/22/2016.
 */
public class Profile_Update_Activity extends AppCompatActivity {

    static final int MEDIA_TYPE_IMAGE = 1;

    String TAG = "Profile_Update_Activity";

    String USER_MOBILE_NO = "" ;

    TextInputEditText edtxt_memberName, edtxt_father_name, edtxt_address, edtxt_district, edtxt_city, edtxt_pinCode, edtxt_mobileNumber, edtxt_phoneNumber, edtxt_email,
            edtxt_bankIfsc, edtxt_bankBranch, edtxt_bankAcntNumber, edtxt_PANNumber, edtxt_nomineeName, edtxt_nomineeRelation;
    TextInputEditText txt_dob, txt_nominee_dob, txt_prefix, txt_state, txt_bankname;

    Button btn_updateProfile,btn_updateProfilesendotp;

    ImageView iv_upload;

    CircularImageView iv_Profile_Pic;

    String stateArray[], bankArray[], selectRelationArray[];
    TelephonyManager telephonyManager;
    String onWhichDateClick = "";
    String Name, Prefix, FatherName, dob, address, state, district, city, pincode, mobile_number, phone_number, email, nominee_name, nominee_dob, nominee_relation, bank_ifsc, bank_name, bank_account_number, bank_branch_name, pan_number;
    String recieve_otp , user_otp ;

    Uri imageUri;
    Bitmap bitmap = null;
    Calendar myCalendar;
    SimpleDateFormat sdf;
    BottomSheetDialog mBottomSheetDialog;
    private String selectedImagePath = "";
    LinearLayout ll_update_profile_data ,ll_update_profile_enter_otp ;
    EditText ed_otp ;
    Button btn_updateProfileafterotp ;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (new Date().after(myCalendar.getTime())) {
                    if (onWhichDateClick.equals("et_dob")) {
                        txt_dob.setText(sdf.format(myCalendar.getTime()));
                    } else {
                        txt_nominee_dob.setText(sdf.format(myCalendar.getTime()));
                    }

                } else {
                    AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_dob));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showExceptionDialog(Profile_Update_Activity.this);
            }
        }
    };


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
                    startActivity(new Intent(Profile_Update_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Profile_Update_Activity.this);
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
        setContentView(R.layout.activity_profile_update);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            selectRelationArray = getResources().getStringArray(R.array.selectRelation);
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");

            iv_Profile_Pic = (CircularImageView) findViewById(R.id.iv_Profile_Pic);
            iv_upload = (ImageView) findViewById(R.id.iv_upload);

            txt_dob = (TextInputEditText) findViewById(R.id.txt_dob);
            txt_nominee_dob = (TextInputEditText) findViewById(R.id.txt_nominee_dob);
            txt_prefix = (TextInputEditText) findViewById(R.id.txt_prefix);
            txt_state = (TextInputEditText) findViewById(R.id.txt_state);
            txt_bankname = (TextInputEditText) findViewById(R.id.txt_bankname);

            edtxt_memberName = (TextInputEditText) findViewById(R.id.edtxt_memberName);
            edtxt_father_name = (TextInputEditText) findViewById(R.id.edtxt_father_name);
            edtxt_address = (TextInputEditText) findViewById(R.id.edtxt_address);
            edtxt_district = (TextInputEditText) findViewById(R.id.edtxt_district);
            edtxt_city = (TextInputEditText) findViewById(R.id.edtxt_city);
            edtxt_pinCode = (TextInputEditText) findViewById(R.id.edtxt_pinCode);
            edtxt_mobileNumber = (TextInputEditText) findViewById(R.id.edtxt_mobileNumber);
            edtxt_phoneNumber = (TextInputEditText) findViewById(R.id.edtxt_phoneNumber);
            edtxt_email = (TextInputEditText) findViewById(R.id.edtxt_email);
            edtxt_nomineeName = (TextInputEditText) findViewById(R.id.edtxt_nomineeName);
            edtxt_nomineeRelation = (TextInputEditText) findViewById(R.id.edtxt_nomineeRelation);
            edtxt_bankIfsc = (TextInputEditText) findViewById(R.id.edtxt_bankIfsc);
            edtxt_bankAcntNumber = (TextInputEditText) findViewById(R.id.edtxt_bankAcntNumber);
            edtxt_bankBranch = (TextInputEditText) findViewById(R.id.edtxt_bankBranch);
            edtxt_PANNumber = (TextInputEditText) findViewById(R.id.edtxt_PANNumber);

            ll_update_profile_enter_otp =  findViewById(R.id.ll_update_profile_enter_otp);
            ed_otp =  findViewById(R.id.ed_otp);
            btn_updateProfileafterotp =  findViewById(R.id.btn_updateProfileafterotp);
            ll_update_profile_data =  findViewById(R.id.ll_update_profile_data);


            ll_update_profile_enter_otp.setVisibility(View.GONE);

            edtxt_PANNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            edtxt_bankIfsc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            btn_updateProfile = (Button) findViewById(R.id.btn_updateProfile);
            btn_updateProfilesendotp = (Button) findViewById(R.id.btn_updateProfilesendotp);

            if(AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "").equalsIgnoreCase("Y")){
                btn_updateProfile.setVisibility(View.GONE);
                btn_updateProfilesendotp.setVisibility(View.VISIBLE);
            //    Toast.makeText(this, "true "+AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, ""), Toast.LENGTH_SHORT).show();
            }
            else {
           //     Toast.makeText(this, "false "+AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, ""), Toast.LENGTH_SHORT).show();
                btn_updateProfilesendotp.setVisibility(View.GONE);
                btn_updateProfile.setVisibility(View.VISIBLE);
            }

            iv_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mBottomSheetDialog.show();
                }
            });

            LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_camera);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera.setVisibility(View.GONE);
            } else {
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            imageUri = AppUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE, TAG, Profile_Update_Activity.this);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, 1);
                            mBottomSheetDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });
            }

            LinearLayout gallery = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_gallery);
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 0);
                    mBottomSheetDialog.dismiss();
                }
            });

            edtxt_bankIfsc.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtxt_bankIfsc.getRight() - edtxt_bankIfsc.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                                if (!edtxt_bankIfsc.getText().toString().trim().isEmpty()) {
                                    if (edtxt_bankIfsc.getText().toString().trim().length() == 11) {

                                        executeToGetIFSCInfo(edtxt_bankIfsc.getText().toString().trim());
                                    } else {
                                        AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_Ifsc));
                                        edtxt_bankIfsc.requestFocus();
                                    }
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_Ifsc));
                                    edtxt_bankIfsc.requestFocus();
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

           /* txt_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.stateList.size() != 0) {
                            showStateDialog();
                            txt_state.clearFocus();
                        } else {
                            executeStateRequest();
                        }
                    }
                }
            });*/

             txt_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.stateList.size() != 0) {
                            showStateDialog();
                            txt_state.clearFocus();
                        } else {
                            executeStateRequest();
                        }
                    }
                }
            });

            txt_bankname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.bankList.size() != 0) {
                            showBankDialog();
                            txt_bankname.clearFocus();
                        } else {
                            executeBankRequest();
                        }
                    }
                }
            });


            txt_prefix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        if (selectRelationArray.length != 0) {
                            showMemRelationDialog();
                            txt_prefix.clearFocus();
                        } else {
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }
            });

            btn_updateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Profile_Update_Activity.this, view);
                    validateUpdateProfileRequest();
                }
            });

            btn_updateProfilesendotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 //   ValidateDataSendOTP();
                    validateUpdateProfileRequest();
                }
            });


            txt_dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        onWhichDateClick = "et_dob";
                        new DatePickerDialog(Profile_Update_Activity.this, myDateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }

                }
            });
            /*  txt_nominee_dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        onWhichDateClick = "et_nomineeDob";
                        new DatePickerDialog(Profile_Update_Activity.this, myDateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        txt_nominee_dob.clearFocus();
                    }

                }
            });*/

            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                executeToGetProfileInfo();
            } else {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            ed_otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s.length() >= 6) {
                        btn_updateProfileafterotp.setVisibility(View.VISIBLE);
                      /*  tv_otp_expired.setVisibility(View.GONE);
                        tv_resend.setVisibility(View.GONE);*/
                    } else
                    {
                        btn_updateProfileafterotp.setVisibility(View.GONE);
                    }
                }
            });


            btn_updateProfileafterotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(Profile_Update_Activity.this, v);
                    ValidateData();

                }
            });
            /*SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
*/
            // Get an instance of SmsRetrieverClient, used to start listening for a matching
            // SMS message.
            SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

            Task<Void> task = client.startSmsRetriever();

            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //        Toast.makeText(Profile_Update_Activity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //      Toast.makeText(Profile_Update_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });


            MySMSBroadcastReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }


    }

    private void ValidateDataSendOTP() {

        USER_MOBILE_NO = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");

        if (TextUtils.isEmpty(USER_MOBILE_NO)) {
            AppUtils.alertDialog(Profile_Update_Activity.this,getResources().getString(R.string.error_required_mobile_number));
        } else if ((USER_MOBILE_NO).length() != 10) {
            AppUtils.alertDialog(Profile_Update_Activity.this,getResources().getString(R.string.error_invalid_mobile_number));
        } else {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                executeShoppingWalletBalanceRequest();
            } else {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    public void ValidateData() {

        String Otp = ed_otp.getText().toString();
        //  OTP = "123456" ;
        if (TextUtils.isEmpty(Otp)) {
            ed_otp.setError("OTP is Required");
            ed_otp.requestFocus();
        } else if (!recieve_otp.equalsIgnoreCase(Otp)) {
            ed_otp.setError("Invalid OTP");
            ed_otp.requestFocus();
         //   tv_resend.setVisibility(View.VISIBLE);
        } else {
            if (AppUtils.isNetworkAvailable(this))
            {
                /* Intent intent = new Intent(Profile_Update_Activity.this , RegisterPersonalActivity.class) ;
                intent.putExtra("str_mobileno" , edtxt_mobile.getText().toString());
                startActivity(intent);
                finish();*/
              //  validateUpdateProfileRequest();
                startUpdateProfile();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }


    private void validateUpdateProfileRequest() {
        try {

            Name = edtxt_memberName.getText().toString().trim();
            Prefix = txt_prefix.getText().toString().trim();
            FatherName = edtxt_father_name.getText().toString().trim();

            dob = txt_dob.getText().toString();
            address = edtxt_address.getText().toString().trim();
            state = txt_state.getText().toString().trim();

            district = edtxt_district.getText().toString().trim();
            city = edtxt_city.getText().toString().trim();
            pincode = edtxt_pinCode.getText().toString().trim();
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            phone_number = edtxt_phoneNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();
            nominee_name = edtxt_nomineeName.getText().toString().trim();
            nominee_dob = txt_nominee_dob.getText().toString();
            nominee_relation = edtxt_nomineeRelation.getText().toString().trim();
            bank_ifsc = edtxt_bankIfsc.getText().toString().trim();
            bank_name = txt_bankname.getText().toString().trim();
            bank_account_number = edtxt_bankAcntNumber.getText().toString().trim();
            bank_branch_name = edtxt_bankBranch.getText().toString().trim();
            pan_number = edtxt_PANNumber.getText().toString().trim();


            if(dob.equalsIgnoreCase(""))
                dob = "0" ;

            if(nominee_dob.equalsIgnoreCase(""))
                nominee_dob = "0" ;




            if (TextUtils.isEmpty(Name)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_name));
                edtxt_memberName.requestFocus();
            } /*else if (TextUtils.isEmpty(Prefix)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_fname_prefix));
            } else if (TextUtils.isEmpty(FatherName)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_fname));
                edtxt_father_name.requestFocus();
            } else if (TextUtils.isEmpty(dob)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_date));
                txt_dob.requestFocus();
            } else if (TextUtils.isEmpty(address)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_address));
                edtxt_address.requestFocus();
            } else if (TextUtils.isEmpty(city)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter City.");
                edtxt_city.requestFocus();
            } else if (TextUtils.isEmpty(district)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter District.");
                edtxt_district.requestFocus();

            } else if (TextUtils.isEmpty(state)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_state));
                txt_state.requestFocus();

            } else if (TextUtils.isEmpty(pincode)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter PinCode.");
                edtxt_pinCode.requestFocus();
            }*/ else if (!TextUtils.isEmpty(pincode) && !pincode.trim().matches(AppUtils.mPINCodePattern)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_PINno));
                edtxt_pinCode.requestFocus();
            } else if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_required_mobile_number));
                edtxt_mobileNumber.requestFocus();
            } else if (mobile_number.trim().length() != 10) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
                edtxt_mobileNumber.requestFocus();

            } else if (!TextUtils.isEmpty(phone_number) && phone_number.trim().length() != 10) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Alternate Mobile Number is Invalid");
                edtxt_phoneNumber.requestFocus();
            } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_email));
                edtxt_email.requestFocus();
            } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_PANno));
                edtxt_PANNumber.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
               // startUpdateProfile();
                ValidateDataSendOTP();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void startUpdateProfile() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();

                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("MemRelation", "" + Prefix));
                postParameters.add(new BasicNameValuePair("FatherName", "" + FatherName));
                postParameters.add(new BasicNameValuePair("DOB", "" + dob));
                postParameters.add(new BasicNameValuePair("Address", "" + address));
                postParameters.add(new BasicNameValuePair("City", "" + city));
                postParameters.add(new BasicNameValuePair("District", "" + district));

                String stateCode = "0";
                for (int i = 0; i < AppController.stateList.size(); i++) {
                    if (state.equals(AppController.stateList.get(i).get("State"))) {
                        stateCode = AppController.stateList.get(i).get("STATECODE");
                    }
                }

                postParameters.add(new BasicNameValuePair("StateCode", "" + stateCode));
                postParameters.add(new BasicNameValuePair("PinCode", "" + pincode));
                postParameters.add(new BasicNameValuePair("PhoneNo", "" + phone_number));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
                postParameters.add(new BasicNameValuePair("EMailID", "" + email));
                postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number));

                String Bankid = "0";
                for (int i = 0; i < AppController.bankList.size(); i++) {
                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
                        Bankid = AppController.bankList.get(i).get("BID");
                    }
                }
                postParameters.add(new BasicNameValuePair("BankId", "" + Bankid));
                postParameters.add(new BasicNameValuePair("BranchName", "" + bank_branch_name));
                postParameters.add(new BasicNameValuePair("AccountNo", "" + bank_account_number));
                postParameters.add(new BasicNameValuePair("IFSCCode", "" + bank_ifsc));
                postParameters.add(new BasicNameValuePair("NomineeName", "" + nominee_name));
                postParameters.add(new BasicNameValuePair("NomineeDOB", "" + nominee_dob));
                postParameters.add(new BasicNameValuePair("Relation", "" + nominee_relation));
                postParameters.add(new BasicNameValuePair("IPAddress", telephonyManager.getDeviceId()));

                executeUpdateprofileRequest(postParameters);
            } else {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeToGetIFSCInfo(final String ifscCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("IFSCCode", ifscCode));

                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodGet_BankDetail, TAG);
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
                        if (jsonArrayData.length() != 0) {

                            for (int i = 0; i < AppController.bankList.size(); i++) {
                                if (jsonArrayData.getJSONObject(0).getString("Branchcode").equals(AppController.bankList.get(i).get("BID"))) {
                                    txt_bankname.setText(AppController.bankList.get(i).get("Bank"));
                                }
                            }
                            edtxt_bankBranch.setText(jsonArrayData.getJSONObject(0).getString("Branch"));
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }

            showStateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            stateArray = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                stateArray[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_state.setText(stateArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showBankDialog() {
        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_bankname.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeToGetProfileInfo() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodToGetUserProfile, TAG);
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
                                if (jsonArrayData.length() != 0) {
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void getProfileInfo(JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemName")).commit();

            Profile_View_Activity.myprofileDetailsList.clear();
            HashMap<String, String> map = new HashMap<>();
            map.put(SPUtils.USER_ID_NUMBER, "" + jsonArray.getJSONObject(0).getString("IDNo"));

            map.put(SPUtils.USER_NAME, "" + jsonArray.getJSONObject(0).getString("MemName"));
            map.put(SPUtils.USER_FATHER_NAME, "" + jsonArray.getJSONObject(0).getString("MemFName"));
            map.put(SPUtils.USER_Relation_Prefix, "" + jsonArray.getJSONObject(0).getString("MemRelation"));
            map.put(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"));
            map.put(SPUtils.USER_PASSWORD, "" + jsonArray.getJSONObject(0).getString("Passw"));
            map.put(SPUtils.USER_ADDRESS, "" + jsonArray.getJSONObject(0).getString("Address1"));
            map.put(SPUtils.USER_MOBILE_NO, "" + jsonArray.getJSONObject(0).getString("Mobl"));
            map.put(SPUtils.USER_Phone_NO, "" + jsonArray.getJSONObject(0).getString("PhN1"));
            map.put(SPUtils.USER_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")));
            map.put(SPUtils.USER_GENDER, "" + jsonArray.getJSONObject(0).getString("Gen"));
            map.put(SPUtils.USER_EMAIL, "" + jsonArray.getJSONObject(0).getString("Email"));
            map.put(SPUtils.USER_CITY, "" + jsonArray.getJSONObject(0).getString("CityName"));

            String StateName = "";
            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("StateCode").equals(AppController.stateList.get(i).get("STATECODE"))) {
                    StateName = AppController.stateList.get(i).get("State");
                }
            }
            map.put(SPUtils.USER_STATE, "" + StateName);

            map.put(SPUtils.USER_DISTRICT, "" + jsonArray.getJSONObject(0).getString("DistrictName"));
            map.put(SPUtils.USER_PINCODE, "" + jsonArray.getJSONObject(0).getString("Pincode"));
            map.put(SPUtils.USER_PAN, "" + jsonArray.getJSONObject(0).getString("PanNo"));
            map.put(SPUtils.USER_CATEGORY, "" + jsonArray.getJSONObject(0).getString("Category"));
            map.put(SPUtils.USER_SPONSOR_ID, "" + jsonArray.getJSONObject(0).getString("UpLnId"));
            map.put(SPUtils.USER_SPONSOR_NAME, "" + jsonArray.getJSONObject(0).getString("UpLnName"));

            String BankName = "";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("BankID").equals(AppController.bankList.get(i).get("BID"))) {
                    BankName = AppController.bankList.get(i).get("Bank");
                }
            }
            map.put(SPUtils.USER_BANKNAME, "" + BankName);
            map.put(SPUtils.USER_BANKACNTNUM, "" + jsonArray.getJSONObject(0).getString("AcNo"));
            map.put(SPUtils.USER_BANKIFSC, "" + jsonArray.getJSONObject(0).getString("IFSCode"));
            map.put(SPUtils.USER_BANKBRANCH, "" + jsonArray.getJSONObject(0).getString("Fld4"));
            map.put(SPUtils.USER_NOMINEE_NAME, "" + jsonArray.getJSONObject(0).getString("NomineeName"));
            map.put(SPUtils.USER_NOMINEE_RELATION, "" + jsonArray.getJSONObject(0).getString("Relation"));
            map.put(SPUtils.USER_NOMINEE_DOB, "" + (jsonArray.getJSONObject(0).getString("NomineeAge")));

            Profile_View_Activity.myprofileDetailsList.add(map);

            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void setProfileDetails() {
        try {

            edtxt_memberName.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NAME));
            edtxt_father_name.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_FATHER_NAME));
            txt_dob.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DOB));
            edtxt_address.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_ADDRESS));
            txt_state.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_STATE));
            edtxt_district.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DISTRICT));
            edtxt_city.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_CITY));
            edtxt_pinCode.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PINCODE));
            edtxt_mobileNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_MOBILE_NO));
            edtxt_phoneNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Phone_NO));
            edtxt_email.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_EMAIL));
            edtxt_nomineeName.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_NAME));
            txt_nominee_dob.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_DOB));
            edtxt_nomineeRelation.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_RELATION));

            edtxt_bankAcntNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKACNTNUM));
            txt_bankname.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKNAME));
            edtxt_bankBranch.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKBRANCH));
            edtxt_bankIfsc.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKIFSC));
            edtxt_PANNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PAN));

            txt_prefix.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix));

            edtxt_memberName.setClickable(false);
            edtxt_memberName.setFocusable(false);
            edtxt_memberName.setFocusableInTouchMode(false);
            edtxt_memberName.setCursorVisible(false);

            edtxt_mobileNumber.setClickable(false);
            edtxt_mobileNumber.setFocusable(false);
            edtxt_mobileNumber.setFocusableInTouchMode(false);
            edtxt_mobileNumber.setCursorVisible(false);

          /*  if (!edtxt_father_name.getText().toString().trim().isEmpty()) {
                edtxt_father_name.setClickable(false);
                edtxt_father_name.setFocusable(false);
                edtxt_father_name.setFocusableInTouchMode(false);
                edtxt_father_name.setCursorVisible(false);
            }

            if (!txt_dob.getText().toString().trim().isEmpty()) {
                txt_dob.setClickable(false);
                txt_dob.setFocusable(false);
                txt_dob.setFocusableInTouchMode(false);
                txt_dob.setCursorVisible(false);
            }

            if (!edtxt_address.getText().toString().trim().isEmpty()) {
                edtxt_address.setClickable(false);
                edtxt_address.setFocusable(false);
                edtxt_address.setFocusableInTouchMode(false);
                edtxt_address.setCursorVisible(false);
            }

            if (!txt_state.getText().toString().trim().isEmpty()) {
                txt_state.setClickable(false);
                txt_state.setFocusable(false);
                txt_state.setFocusableInTouchMode(false);
                txt_state.setCursorVisible(false);
            }
            if (!txt_prefix.getText().toString().trim().isEmpty()) {
                txt_prefix.setClickable(false);
                txt_prefix.setFocusable(false);
                txt_prefix.setFocusableInTouchMode(false);
                txt_prefix.setCursorVisible(false);
            }
            if (!txt_nominee_dob.getText().toString().trim().isEmpty()) {
                txt_nominee_dob.setClickable(false);
                txt_nominee_dob.setFocusable(false);
                txt_nominee_dob.setFocusableInTouchMode(false);
                txt_nominee_dob.setCursorVisible(false);
            }
            if (!edtxt_nomineeRelation.getText().toString().trim().isEmpty()) {
                edtxt_nomineeRelation.setClickable(false);
                edtxt_nomineeRelation.setFocusable(false);
                edtxt_nomineeRelation.setFocusableInTouchMode(false);
                edtxt_nomineeRelation.setCursorVisible(false);
            }
            if (!edtxt_nomineeName.getText().toString().trim().isEmpty()) {
                edtxt_nomineeName.setClickable(false);
                edtxt_nomineeName.setFocusable(false);
                edtxt_nomineeName.setFocusableInTouchMode(false);
                edtxt_nomineeName.setCursorVisible(false);
            }
            if (!edtxt_PANNumber.getText().toString().trim().isEmpty()) {
                edtxt_PANNumber.setClickable(false);
                edtxt_PANNumber.setFocusable(false);
                edtxt_PANNumber.setFocusableInTouchMode(false);
                edtxt_PANNumber.setCursorVisible(false);
            }
            if (!edtxt_email.getText().toString().trim().isEmpty()) {
                edtxt_email.setClickable(false);
                edtxt_email.setFocusable(false);
                edtxt_email.setFocusableInTouchMode(false);
                edtxt_email.setCursorVisible(false);
            }
            if (!edtxt_phoneNumber.getText().toString().trim().isEmpty()) {
                edtxt_phoneNumber.setClickable(false);
                edtxt_phoneNumber.setFocusable(false);
                edtxt_phoneNumber.setFocusableInTouchMode(false);
                edtxt_phoneNumber.setCursorVisible(false);
            }
            */
           /* if (!edtxt_pinCode.getText().toString().trim().isEmpty()) {
                edtxt_pinCode.setClickable(false);
                edtxt_pinCode.setFocusable(false);
                edtxt_pinCode.setFocusableInTouchMode(false);
                edtxt_pinCode.setCursorVisible(false);
            }
            if (!edtxt_city.getText().toString().trim().isEmpty()) {
                edtxt_city.setClickable(false);
                edtxt_city.setFocusable(false);
                edtxt_city.setFocusableInTouchMode(false);
                edtxt_city.setCursorVisible(false);
            }
            if (!edtxt_district.getText().toString().trim().isEmpty()) {
                edtxt_district.setClickable(false);
                edtxt_district.setFocusable(false);
                edtxt_district.setFocusableInTouchMode(false);
                edtxt_district.setCursorVisible(false);
            }
            if (!edtxt_bankIfsc.getText().toString().trim().isEmpty()) {
                edtxt_bankIfsc.setClickable(false);
                edtxt_bankIfsc.setFocusable(false);
                edtxt_bankIfsc.setFocusableInTouchMode(false);
                edtxt_bankIfsc.setCursorVisible(false);
            }

            if (!edtxt_bankBranch.getText().toString().trim().isEmpty()) {
                edtxt_bankBranch.setClickable(false);
                edtxt_bankBranch.setFocusable(false);
                edtxt_bankBranch.setFocusableInTouchMode(false);
                edtxt_bankBranch.setCursorVisible(false);
            }

            if (!edtxt_bankAcntNumber.getText().toString().trim().isEmpty()) {
                edtxt_bankAcntNumber.setClickable(false);
                edtxt_bankAcntNumber.setFocusable(false);
                edtxt_bankAcntNumber.setFocusableInTouchMode(false);
                edtxt_bankAcntNumber.setCursorVisible(false);
            }

            if (!txt_bankname.getText().toString().trim().isEmpty() && !txt_bankname.getText().toString().trim().equalsIgnoreCase("-- No Bank Found --")) {
                txt_bankname.setClickable(false);
                txt_bankname.setFocusable(false);
                txt_bankname.setFocusableInTouchMode(false);
                txt_bankname.setCursorVisible(false);
            }*/

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            if (bytecode.length() > 0) {
                iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeUpdateprofileRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodToUpdateUserProfile, TAG);
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
                                if (jsonArrayData.length() != 0) {

                                    AppUtils.alertDialogWithFinish(Profile_Update_Activity.this, "" + jsonObject.getString("Message"));
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 0) {
                    if (data != null) {
                        String filepath = AppUtils.getPath(data.getData(), Profile_Update_Activity.this);

                        if (filepath.length() > 0) {
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == 1) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    public void pickImageFromGallery() {
        try {
            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            if (AppUtils.showLogs) Log.e(TAG, "Image Size before compress : " + imageFile.length());

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
                AppUtils.showExceptionDialog(Profile_Update_Activity.this);
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
                    AppUtils.alertDialog(Profile_Update_Activity.this, "Maximum image size limit 1 MB.");
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
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(Profile_Update_Activity.this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Are you sure you want to upload this image as Profile Picture ?");

            final ImageView imgView_Upload = (ImageView) dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.hideKeyboardOnClick(Profile_Update_Activity.this, v);
                    dialog.dismiss();
                    executePostProfilePictureUploadRequest();
                    iv_Profile_Pic.setImageBitmap(bitmap);
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
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executePostProfilePictureUploadRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", "PP"));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", deviceId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    Cache.getInstance().getLru().put("profileImage", AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (jsonArrayData.getJSONObject(0).getString("PhotoProof"))).commit();
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetKYCUploadRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showMemRelationDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Member Relation");
            builder.setItems(selectRelationArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_prefix.setText(selectRelationArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
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
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }

        System.gc();
    }

    /*******************OTP System**************************/
    private void executeShoppingWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this,
                                    postParameters, QueryUtils.methodToSendOTPUpdateProfile, TAG);

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
                                btn_updateProfilesendotp.setVisibility(View.GONE);
                                ll_update_profile_enter_otp.setVisibility(View.VISIBLE);
                                ll_update_profile_data.setVisibility(View.GONE);
                                ed_otp.setVisibility(View.VISIBLE);

                                recieve_otp = jsonArray.getJSONObject(0).getString("OTP");
                                AppUtils.alertDialog(Profile_Update_Activity.this, "OTP Send Succefully on your number");
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }
}