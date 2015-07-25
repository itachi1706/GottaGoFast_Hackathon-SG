package com.itachi1706.hackathonsg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.ProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.SampleData.SampleJSONProducts;

import java.util.ArrayList;
import java.util.Objects;

public class ProductList extends AppCompatActivity {

    private final String KEY = "ProductList";

    ListView productView;
    ProductViewAdapter adapter;

    ArrayList<JSONProducts> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productView = (ListView) findViewById(R.id.lvProducts);
        adapter = new ProductViewAdapter(this, R.layout.listview_products, new ArrayList<JSONProducts>());
        productView.setAdapter(adapter);
        //productView.setItemsCanFocus(true);
        Log.d(KEY, productView.getItemsCanFocus() + "");

        //Set on item click listener
        productView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONProducts prod = productList.get(position);

                Intent descIntent = new Intent(ProductList.this, DetailedProductDesc.class);
                descIntent.putExtra("key", prod.getID());
                startActivity(descIntent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        //Drop and repopulate database
        ProductDB db = new ProductDB(this);
        SampleJSONProducts.populateDatabase(db);

        //Update adapter
        productList = db.getAllProducts();
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
