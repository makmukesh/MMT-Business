package com.vpipl.mmtbusiness.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vpipl.mmtbusiness.model.ProductsList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ProductsMaster";
    // Contacts table name
    private static final String TABLE_Products = "ProductsList";

    // Contacts Table Columns names
    private static final String KEY_ProdID = "id";
    private static final String KEY_ProductName = "ProductName";
    private static final String KEY_ProductCode = "ProductCode";
    private static final String KEY_Discount = "Discount";
    private static final String KEY_BV = "BV";
    private static final String KEY_DiscountPer = "DiscountPer";
    private static final String KEY_NewDisp = "NewDisp";
    private static final String KEY_NewImgPath = "NewImgPath";
    private static final String KEY_NewMRP = "NewMRP";
    private static final String KEY_NewDP = "NewDP";

    private static final String KEY_SizeID = "SizeID";

    private static final String KEY_ColorID = "ColorID";

    private static DatabaseHandler sqLiteHelper = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (sqLiteHelper == null)
            return new DatabaseHandler(context);
        else
            return sqLiteHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_Products +
                "(" + KEY_ProdID + " INTEGER PRIMARY KEY,"
                + KEY_ProductName + " TEXT,"
                + KEY_ProductCode + " TEXT,"
                + KEY_Discount + " TEXT,"
                + KEY_BV + " TEXT,"
                + KEY_DiscountPer + " TEXT,"
                + KEY_NewDisp + " TEXT,"
                + KEY_NewImgPath + " TEXT,"
                + KEY_NewMRP + " TEXT,"
                + KEY_NewDP + " TEXT,"
                + KEY_SizeID + " TEXT,"
//                + KEY_SizeID2 + " TEXT,"
//                + KEY_SizeID3 + " TEXT,"
//                + KEY_SizeID4 + " TEXT,"
                + KEY_ColorID + " TEXT"
//                + KEY_ColorID2 + " TEXT,"
//                + KEY_ColorID3 + " TEXT,"
//                + KEY_ColorID4 + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Products);
        onCreate(db);
    }

    // Adding new contact
    public void addProducts(ProductsList productsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ProdID, productsList.getID());
        values.put(KEY_ProductName, productsList.getName());
        values.put(KEY_ProductCode, productsList.getcode());
        values.put(KEY_Discount, productsList.getDiscount());
        values.put(KEY_BV, productsList.getBV());
        values.put(KEY_DiscountPer, productsList.getDiscountPer());
        values.put(KEY_NewDisp, productsList.getIsProductNew());
        values.put(KEY_NewImgPath, productsList.getImagePath());
        values.put(KEY_NewMRP, productsList.getNewMRP());
        values.put(KEY_NewDP, productsList.getNewDP());
        values.put(KEY_SizeID, productsList.getselectedSizeId());
        values.put(KEY_ColorID, productsList.getselectedColorId());

        db.insert(TABLE_Products, null, values);
        db.close();
    }

    // Getting All Contacts
    public List<ProductsList> getAllProducts(int sortcondition) {
        List<ProductsList> productsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_ProdID + " DESC";

        if (sortcondition == 1) {
            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_NewDP + " ASC";
        } else if (sortcondition == 2) {
            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_NewDP + " DESC";
        } else if (sortcondition == 3) {
            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_DiscountPer + " DESC";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductsList products = new ProductsList();

                products.setID((cursor.getString(0)));
                products.setName(cursor.getString(1));
                products.setcode(cursor.getString(2));
                products.setDiscount(cursor.getString(3));
                products.setBV(cursor.getString(4));
                products.setDiscountPer(cursor.getString(5));
                products.setIsProductNew(cursor.getString(6));
                products.setImagePath(cursor.getString(7));
                products.setNewMRP(cursor.getString(8));
                products.setNewDP(cursor.getString(9));
                products.setselectedSizeId(cursor.getString(10));
                products.setselectedColorId(cursor.getString(11));

                productsList.add(products);

            } while (cursor.moveToNext());
        }
        return productsList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Products;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_Products); //delete all rows in a table
        db.close();
    }
}