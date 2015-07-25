package com.itachi1706.hackathonsg.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.Objects.JSONGeneralProducts;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.ProductList;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.AsyncTasks
 */
public class AddToDB extends AsyncTask<JSONGeneralProducts, String, Void> {

    private ProgressDialog dialog;
    private ProductDB db;
    private Activity activity;
    private final String KEY = "ADD-TO-DB";

    public AddToDB(ProgressDialog dialog, ProductDB db, Activity activity)
    {
        this.dialog = dialog;
        this.db = db;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(JSONGeneralProducts... array) {

        JSONProducts[] products = array[0].getItems();

        for (int i = 0; i < products.length; i++)
        {
            JSONProducts prod = products[i];
            Log.d(KEY, "Importing " + i + "/" + products.length + " (" + prod.getTitle() + ")");

            publishProgress(i + "", products.length + "", "Parsing " + prod.getTitle());
            db.addToDB(prod);
        }

        return null;
    }

    /**
     * On Progress Update, update dialog
     * @param message [0] - Progress, [1] - Total, [2] - Message
     */
    @Override
    protected void onProgressUpdate(String... message){
        dialog.setTitle("Parsing to Database");
        dialog.setIndeterminate(false);
        dialog.setMessage(message[2]);
        dialog.setMax(Integer.parseInt(message[1]));
        dialog.setProgress(Integer.parseInt(message[0]));
    }

    protected void onPostExecute(Void param){
        dialog.dismiss();
        ProductList.adapter.updateAdapter(db.getAllProducts());
        ProductList.adapter.notifyDataSetChanged();
    }
}
