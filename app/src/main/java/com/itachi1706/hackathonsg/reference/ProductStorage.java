package com.itachi1706.hackathonsg.reference;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.itachi1706.hackathonsg.Objects.JSONGeneralStoredProducts;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.Objects.JSONStoredProducts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kenneth on 26/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.reference
 */
public class ProductStorage {

    public static void addNewProduct(JSONProducts product, SharedPreferences prefs)
    {
        int key = product.getID();
        //boolean purchase = false;
        long date = System.currentTimeMillis();

        JSONArray arr = getExistingJSONString(prefs);
        JSONObject obj = new JSONObject();
        JSONObject main = new JSONObject();

        try
        {
            obj.put("key", key);
            obj.put("isPurchased", false);
            obj.put("dateStored", date);
            arr.put(obj);
            main.put("storage", arr);
            prefs.edit().putString("storedPurchases", main.toString()).apply();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void addNewProduct(JSONStoredProducts product, SharedPreferences prefs)
    {
        int key = product.getKey();
        boolean purchase = product.isPurchased();
        long date = product.getDateStored();

        JSONArray arr = getExistingJSONString(prefs);
        JSONObject obj = new JSONObject();
        JSONObject main = new JSONObject();

        try
        {
            obj.put("key", key);
            obj.put("isPurchased", purchase);
            obj.put("dateStored", date);
            arr.put(obj);
            main.put("storage", arr);
            prefs.edit().putString("storedPurchases", main.toString()).apply();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private static JSONArray getExistingJSONString(SharedPreferences pref) {
        String json = pref.getString("storedPurchases", null);
        if (json == null) {
            return new JSONArray();
        }
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getJSONArray("storage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void updateProductsList(ArrayList<JSONProducts> newProducts, SharedPreferences pref)
    {
        pref.edit().remove("stored").apply();
        if (newProducts.size() != 0)
        {
            for (JSONProducts prod : newProducts)
            {
                addNewProduct(prod, pref);
            }
        }
    }

    public static void updateProductsList(SharedPreferences pref, ArrayList<JSONStoredProducts> newProducts)
    {
        pref.edit().remove("stored").apply();
        if (newProducts.size() != 0)
        {
            for (JSONStoredProducts prod : newProducts)
            {
                addNewProduct(prod, pref);
            }
        }
    }

    public static boolean hasStorageData(SharedPreferences preferences)
    {
        String check = preferences.getString("storedPurchases", "wot");

        return !check.equals("wot");
    }

    public static boolean hasInserted(SharedPreferences preferences, JSONProducts prods)
    {
        String check = preferences.getString("storedPurchases", "wot");

        if (check.equals("wot")) return false;

        Gson gson = new Gson();
        JSONGeneralStoredProducts productArray = gson.fromJson(check, JSONGeneralStoredProducts.class);

        JSONStoredProducts[] products = productArray.getStorage();

        for (JSONStoredProducts prod : products)
        {
            if (prod.getKey() == prods.getID() && !prod.isPurchased()) return true;
        }

        return false;
    }

    public static void markPurchased(SharedPreferences preferences, JSONStoredProducts product)
    {
        String check = preferences.getString("storedPurchases", "wot");

        if (check.equals("wot")) return;

        Gson gson = new Gson();
        JSONGeneralStoredProducts productArray = gson.fromJson(check, JSONGeneralStoredProducts.class);

        JSONStoredProducts[] products = productArray.getStorage();

        for (JSONStoredProducts prod : products)
        {
            if (prod.getKey() == product.getKey() && !prod.isPurchased())
            {
                prod.setIsPurchased(true);

                ArrayList<JSONStoredProducts> newArray = new ArrayList<>(Arrays.asList(products));
                updateProductsList(preferences, newArray);
                return;
            }
        }
    }

    public static void removeFromCart(SharedPreferences preferences, JSONStoredProducts product)
    {
        String check = preferences.getString("storedPurchases", "wot");

        if (check.equals("wot")) return;

        Gson gson = new Gson();
        JSONGeneralStoredProducts productArray = gson.fromJson(check, JSONGeneralStoredProducts.class);

        JSONStoredProducts[] products = productArray.getStorage();
        ArrayList<JSONStoredProducts> newArray = new ArrayList<>(Arrays.asList(products));

        for (JSONStoredProducts prod : newArray)
        {
            if (prod.getKey() == product.getKey() && !prod.isPurchased())
            {
                newArray.remove(prod);
                updateProductsList(preferences, newArray);
                return;
            }
        }
    }

    public static JSONStoredProducts getProduct(SharedPreferences preferences, JSONProducts product)
    {
        String check = preferences.getString("storedPurchases", "wot");

        if (check.equals("wot")) return null;

        Gson gson = new Gson();
        JSONGeneralStoredProducts productArray = gson.fromJson(check, JSONGeneralStoredProducts.class);

        JSONStoredProducts[] products = productArray.getStorage();
        ArrayList<JSONStoredProducts> newArray = new ArrayList<>(Arrays.asList(products));

        for (JSONStoredProducts prod : newArray)
        {
            if (prod.getKey() == product.getID() && !prod.isPurchased())
            {
                return prod;
            }
        }
        return null;
    }
}
