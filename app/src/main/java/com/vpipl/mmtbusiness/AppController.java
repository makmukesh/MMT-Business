package com.vpipl.mmtbusiness;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.vpipl.mmtbusiness.SMS.AppSignatureHelper;
import com.vpipl.mmtbusiness.Utils.SPUtils;
import com.vpipl.mmtbusiness.model.FilterList2CheckBox;
import com.vpipl.mmtbusiness.model.ProductsList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 01-05-2017.
 */
public class AppController extends Application {

    public static ArrayList<HashMap<String, String>> stateList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> bankList = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> SizeList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> ColorList = new ArrayList<>();

    public static List<ProductsList> selectedProductsList = new ArrayList<>();
    public static List<ProductsList> selectedWishList = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> category1 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category2 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category3 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category4 = new ArrayList<>();

    public static ArrayList<FilterList2CheckBox> priceFilterList = new ArrayList<>();
    public static ArrayList<FilterList2CheckBox> discountFilterList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> filterList1 = new ArrayList<>();


    public static boolean comesFromFilter = false;

    public static String FiltersCondition = "";
    public static String FiltersConditionStatic = "";

    private static AppController mInstance;
    private static SharedPreferences sp_userinfo;
    private static SharedPreferences sp_rememberuserinfo;
    private static SharedPreferences sp_isLogin;

    private static SharedPreferences sp_isInstall;
    private static SharedPreferences sp_currentSession;

    /**
     * used to get instance globally
     */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

   public static synchronized SharedPreferences getSPcurrentSession() {
        return sp_currentSession;
    }

    public static synchronized SharedPreferences getSpRememberUserInfo() {
        return sp_rememberuserinfo;
    }

    public static synchronized SharedPreferences getSpIsInstall() {
        return sp_isInstall;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpIsLogin() {
        return sp_isLogin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mInstance = this;
            /** to call initialize SharedPreferences  */
            initSharedPreferences();

            AppSignatureHelper appSignature = new AppSignatureHelper((Context) this);
            appSignature.getAppSignatures();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            sp_currentSession = getApplicationContext().getSharedPreferences(SPUtils.USER_CURRENTSESS, Context.MODE_PRIVATE);
            sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            sp_rememberuserinfo = getApplicationContext().getSharedPreferences(SPUtils.REMEMBER_USER_INFO, Context.MODE_PRIVATE);
            sp_isLogin = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);

            sp_isInstall = getApplicationContext().getSharedPreferences(SPUtils.IS_INSTALL, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}