package com.itachi1706.hackathonsg.SampleData;

import android.app.Activity;

import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.Objects.JSONProducts;

import java.util.ArrayList;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.SampleData
 */
public class SampleJSONProducts {

    public static void populateDatabase(ProductDB db)
    {
        ArrayList<JSONProducts> lel = new ArrayList<>();
        lel.add(new JSONProducts(0, "Test Normal Product", 10.0, 1));
        lel.add(new JSONProducts(1, "Test Normal Product Not Avail", 10.0, 0));
        lel.add(new JSONProducts(2, "Test Disc Product", 10.0, 9.00, 1));
        lel.add(new JSONProducts(3, "Test Disc Product Not Avail", 10.0, 9.00, 0));


        db.dropEverythingAndRebuild();
        for (JSONProducts prod : lel)
        {
            db.addToDB(prod);
        }
    }
}
