package com.itachi1706.hackathonsg.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.itachi1706.hackathonsg.reference.ProductImageTemp;
import com.itachi1706.hackathonsg.reference.StaticReferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.AsyncTasks
 */
public class GetProductImage extends AsyncTask<String, Void, Drawable> {

    final String KEY = "PROD-IMG";
    Exception exception = null;
    Context activity;
    ImageView image;
    int productKey;

    public GetProductImage(Context activity, ImageView image, int productKey)
    {
        this.activity = activity;
        this.image = image;
        this.productKey = productKey;
    }

    @Override
    protected Drawable doInBackground(String... urls) {
        String urlString = urls[0];
        Log.d(KEY, urlString);

        Drawable d = null;
        try {
            //Get Player Head
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(StaticReferences.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(StaticReferences.HTTP_QUERY_TIMEOUT);
            InputStream is = (InputStream) conn.getContent();
            d = Drawable.createFromStream(is, "src name");
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
        }

        return d;
    }

    protected void onPostExecute(Drawable draw)
    {
        if (exception != null)
        {
            if (exception instanceof SocketTimeoutException) {
                Toast.makeText(activity, "Database query timed out. Retrying", Toast.LENGTH_SHORT).show();
                Log.d("Exception", "Timed out");
                new GetProductImage(activity, image, productKey).execute();
            } else {
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Exception", exception.getMessage());
                exception.printStackTrace();
            }
        }
        else
        {
            image.setImageDrawable(draw);
            ProductImageTemp.saveImage(activity, draw, productKey);
            //StaticReferences.savedImages.put(productKey, draw);
        }
    }
}
