package com.itachi1706.hackathonsg.reference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.reference
 */
public class ProductImageTemp {

    private static boolean checkFolderExists(Context context){
        File folder = new File(context.getExternalFilesDir(null) + File.separator + "products");
        return folder.exists() || folder.mkdir();
    }

    public static boolean saveImage(Context context, Drawable imageToSave, int key){
        //Prepare image and storage location
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "products" + File.separator;
        BitmapDrawable bd = (BitmapDrawable) imageToSave;
        Bitmap bm = bd.getBitmap();
        File image = new File(storageLocation, key + ".png");
        FileOutputStream out;
        if (!checkFolderExists(context)){
            Toast.makeText(context, "An error occurred making folder to store product data", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Log.d("FILE PATH", image.getAbsolutePath());
            if (!image.createNewFile()){
                return false;
            }
            out = new FileOutputStream(image);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.d("PRODUCT STORE", "Cached " + key + "'s onto device");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIfProductExists(Context context, int key){
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "products" + File.separator;
        File file = new File(storageLocation, key + ".png");
        final long expiryDay = 864000000;
        if (file.exists()){
            //Check if expired (10 days)
            if (System.currentTimeMillis() - file.lastModified() > expiryDay){
                boolean result = file.delete();
                if (result)
                    return false;
                return false;
            }
            return true;
        }
        return false;
    }

    public static Drawable getProduct(Context context, int key){
        if (!checkIfProductExists(context, key)){
            return null;
        }
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "products" + File.separator + key + ".png";
        return new BitmapDrawable(context.getResources(), storageLocation);
    }
}
