package com.itachi1706.hackathonsg.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itachi1706.hackathonsg.Objects.JSONProducts;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.Database
 */
public class ProductDB extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;

    //DB Name
    public static final String DB_NAME = "hackathon.db";

    //DB Table
    public static final String TABLE_PRODUCT = "products";

    //DB Keys
    public static final String PRODUCT_KEY = "key";
    public static final String PRODUCT_TITLE = "title";
    public static final String PRODUCT_RETAIL_PRICE = "retailprice";
    public static final String PRODUCT_OFFER_PRICE = "offerprice";
    public static final String PRODUCT_IMAGE = "image";
    public static final String PRODUCT_STOCK = "stock";
    public static final String PRODUCT_STORE = "store";
    public static final String PRODUCT_BARCODE = "barcode";

    public ProductDB(Context context)
    {
        super(context, context.getExternalFilesDir(null) + File.separator + DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "(" + PRODUCT_KEY + " INTEGER PRIMARY KEY,"
                + PRODUCT_TITLE + " TEXT," + PRODUCT_RETAIL_PRICE + " TEXT," + PRODUCT_OFFER_PRICE + " TEXT,"
                + PRODUCT_IMAGE + " TEXT," + PRODUCT_STOCK + " INTEGER," + PRODUCT_STORE + " TEXT," + PRODUCT_BARCODE + " TEXT);";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public void dropEverythingAndRebuild()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    private void addFromJSON(JSONProducts products)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_KEY, products.getID());
        cv.put(PRODUCT_IMAGE, products.getImage());
        cv.put(PRODUCT_TITLE, products.getTitle());
        cv.put(PRODUCT_RETAIL_PRICE, products.getRetailPrice());
        cv.put(PRODUCT_OFFER_PRICE, products.getOfferPrice());
        cv.put(PRODUCT_STOCK, products.getStockInt());
        cv.put(PRODUCT_STORE, products.getStore());
        cv.put(PRODUCT_BARCODE, products.getBarcode());
        db.insert(TABLE_PRODUCT, null, cv);
        db.close();
    }

    private void updateJSON(JSONProducts products)
    {
        String filter = PRODUCT_KEY + "=" + products.getID();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_IMAGE, products.getImage());
        cv.put(PRODUCT_TITLE, products.getTitle());
        cv.put(PRODUCT_RETAIL_PRICE, products.getRetailPrice());
        cv.put(PRODUCT_OFFER_PRICE, products.getOfferPrice());
        cv.put(PRODUCT_STOCK, products.getStockInt());
        cv.put(PRODUCT_STORE, products.getStore());
        cv.put(PRODUCT_BARCODE, products.getBarcode());
        db.update(TABLE_PRODUCT, cv, filter, null);
        db.close();
    }

    public boolean checkIfExistAlready(JSONProducts products)
    {
        int key = products.getID();
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_KEY + "=" + key + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count != 0;
    }

    /**
     * If record isn't present in DB, add it
     * @param products Object to check
     */
    public void addToDB(JSONProducts products)
    {
        if (checkIfExistAlready(products)) {
            updateJSON(products);
        }
        addFromJSON(products);
    }

    /**
     * Internal method to parse cursor for easy edit next time
     * @param cursor cursor object
     * @return product object
     */
    private JSONProducts generateProductFromCursor(Cursor cursor)
    {
        JSONProducts prod = new JSONProducts();
        prod.setId(cursor.getInt(0));
        prod.setTitle(cursor.getString(1));
        prod.setRetailprice(cursor.getString(2));
        prod.setOfferprice(cursor.getString(3));
        prod.setImage(cursor.getString(4));
        prod.setStock(cursor.getInt(5));
        prod.setStore(cursor.getString(6));
        prod.setBarcode(cursor.getString(7));
        return prod;
    }

    /**
     * Gets all products out from the database
     * @return List of all products
     */
    public ArrayList<JSONProducts> getAllProducts()
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONProducts> results = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do
            {
                JSONProducts prod = generateProductFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return results;
    }

    /**
     * Return a Single Product Item
     * @param key Product item
     * @return product object
     */
    public JSONProducts getJSONProductByKey(int key)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_KEY + "=" + key + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        JSONProducts prod = new JSONProducts();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                prod = generateProductFromCursor(cursor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return prod;
    }

    /**
     * Get All products by the name
     * @param name name of product
     * @return list of products with the same name
     */
    public ArrayList<JSONProducts> getAllProductsByName(String name)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_TITLE + "=" + name + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONProducts> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                JSONProducts prod = generateProductFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }

    /**
     * Get All products by the barcode
     * @param barcode barcode of product
     * @return list of products with the same barcode
     */
    public ArrayList<JSONProducts> getAllProductsByBarcode(String barcode)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_BARCODE + "=" + barcode + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONProducts> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                JSONProducts prod = generateProductFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }

    public boolean isEmpty()
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count == 0;
    }


}
