package com.itachi1706.hackathonsg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itachi1706.hackathonsg.AsyncTasks.PopulateDatabase;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.ProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.SampleData.SampleJSONProducts;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String KEY = "ProductList";

    ListView productView;
    ProductViewAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<JSONProducts> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_with_real_data);
        productView = (ListView) findViewById(R.id.lvProducts);
        adapter = new ProductViewAdapter(this, R.layout.listview_products, new ArrayList<JSONProducts>());
        productView.setAdapter(adapter);
        //productView.setItemsCanFocus(true);
        Log.d(KEY, productView.getItemsCanFocus() + "");

        swipeRefreshLayout.setOnRefreshListener(this);
        // TODO Swipe to refresh get 4 colors for the color scheme

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
        else if (id == R.id.action_populate_actual)
        {
            swipeRefreshLayout.setRefreshing(true);
            populateWithRealData();
        }
        else if (id == R.id.action_populate_sample)
        {
            swipeRefreshLayout.setRefreshing(true);
            populateWithSampleData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateWithRealData()
    {
        ProductDB db = new ProductDB(this);
        db.dropEverythingAndRebuild();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Starting Stuff");
        dialog.setMessage("Starting Stuff");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new PopulateDatabase(dialog, db, this, swipeRefreshLayout).execute();
    }

    private void populateWithSampleData()
    {
        //Drop and repopulate database
        ProductDB db = new ProductDB(this);
        SampleJSONProducts.populateDatabase(db);

        //Update adapter
        productList = db.getAllProducts();
        adapter.updateAdapter(productList);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        populateWithRealData();
    }
}
