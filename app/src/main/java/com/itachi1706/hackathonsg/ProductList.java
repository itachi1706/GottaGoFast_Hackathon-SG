package com.itachi1706.hackathonsg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.ProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.SampleData.SampleJSONProducts;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity {

    ListView productView;
    ProductViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productView = (ListView) findViewById(R.id.lvProducts);
        adapter = new ProductViewAdapter(this, R.layout.listview_products, new ArrayList<JSONProducts>());
        productView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        //Drop and repopulate database
        ProductDB db = new ProductDB(this);
        SampleJSONProducts.populateDatabase(db);

        //Update adapter
        ArrayList<JSONProducts> productList = db.getAllProducts();
        adapter.updateAdapter(productList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_populate_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
