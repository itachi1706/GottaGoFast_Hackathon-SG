package com.itachi1706.hackathonsg.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.Objects.JSONGeneralProducts;
import com.itachi1706.hackathonsg.reference.StaticReferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.AsyncTasks
 */
public class PopulateDatabase extends AsyncTask<Void, Void, String> {

    private ProgressDialog dialog;
    private ProductDB db;
    private Activity activity;
    private Exception exception = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    private long lastUpdate;

    public PopulateDatabase(ProgressDialog dialog, ProductDB  db, Activity activity, SwipeRefreshLayout swipeRefreshLayout)
    {
        this.dialog = dialog;
        this.db = db;
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        this.lastUpdate = sp.getLong("lastQueried", 0);
    }

    @Override
    protected String doInBackground(Void... params)
    {
        String url = StaticReferences.BASE_URL + "listProduct.php?limit=-1&lastupdate=" + (lastUpdate / 1000);
        String tmp = "";

        Log.d("Populate", url);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setTitle("Downloading Product Data");
                dialog.setMessage("Polling Data from server");
            }
        });
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setConnectTimeout(StaticReferences.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(StaticReferences.HTTP_QUERY_TIMEOUT);
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                str.append(line);
            }
            in.close();
            tmp = str.toString();
        } catch (IOException e) {
            exception = e;
        }
        return tmp;
    }

    protected void onPostExecute(String json)
    {
        if (exception != null)
        {
            if (exception instanceof SocketTimeoutException) {
                Toast.makeText(activity, "Database query timed out. Retrying", Toast.LENGTH_SHORT).show();
                Log.d("Exception", "Timed out");
                new PopulateDatabase(dialog, db, activity,swipeRefreshLayout).execute();
            } else {
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Exception", exception.getMessage());
                exception.printStackTrace();
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        else
        {
            Log.d("POPULATE", json);
            Gson gson = new Gson();
            JSONGeneralProducts prodArr = gson.fromJson(json, JSONGeneralProducts.class);
            if (prodArr == null)
            {
                Toast.makeText(activity, "Something weird occurred", Toast.LENGTH_SHORT).show();
                new PopulateDatabase(dialog, db, activity, swipeRefreshLayout).execute();
                return;
            }

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            sp.edit().putLong("lastQueried", System.currentTimeMillis()).apply();

            new AddToDB(dialog, db, activity).execute(prodArr);
        }
    }
}
