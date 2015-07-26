package com.itachi1706.hackathonsg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.itachi1706.hackathonsg.AsyncTasks.PopulateDatabase;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.ProductViewCompactAdapter;
import com.itachi1706.hackathonsg.ListViewAdapters.SimilarProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.Barcode;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.SampleData.SampleJSONProducts;
import com.itachi1706.hackathonsg.libraries.barcode.IntentIntegrator;
import com.itachi1706.hackathonsg.libraries.barcode.IntentResult;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String KEY = "ProductList";
    private final String TAG = "ProductList";

    ListView productView;
    public static SimilarProductViewAdapter adapter;
    public static ProductViewCompactAdapter adapterCompact;
    public static boolean isCompact;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText searchField;

    public static ArrayList<JSONProducts> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_with_real_data);
        productView = (ListView) findViewById(R.id.lvProducts);
        searchField = (EditText) findViewById(R.id.etProductsFilter);

        adapter = new SimilarProductViewAdapter(this, R.layout.listview_products, new ArrayList<JSONProducts>());
        adapterCompact = new ProductViewCompactAdapter(this, R.layout.listview_products_compact, new ArrayList<JSONProducts>());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sp.getBoolean("compactList", false))
        {
            productView.setAdapter(adapter);
            isCompact = false;
        }
        else
        {
            productView.setAdapter(adapterCompact);
            isCompact = true;
        }

        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                Log.d("TextWatcher", "Query: " + query);
                ProductDB db = new ProductDB(ProductList.this);
                ArrayList<JSONProducts> list = db.getProductsByQuery(query);
                if (list != null){
                    Log.d("TextWatcher", "Finished Search. Size: " + list.size());
                    adapter.updateAdapter(list);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        searchField.addTextChangedListener(inputWatcher);


        swipeRefreshLayout.setOnRefreshListener(this);
        // TODO Swipe to refresh get 4 colors for the color scheme

        //Set on item click listener
        productView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONProducts prod = (JSONProducts) productView.getItemAtPosition(position);
                //JSONProducts prod = productList.get(position);

                Intent descIntent = new Intent(ProductList.this, DetailedProductDesc.class);
                descIntent.putExtra("key", prod.getID());
                startActivity(descIntent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        ProductDB db = new ProductDB(this);
        productList = db.getAllProducts();
        if (productList.size() > 0)
        {
            if (!isCompact) {
                productView.setAdapter(adapter);
                adapter.updateAdapter(productList);
                adapter.notifyDataSetChanged();
            } else {
                productView.setAdapter(adapterCompact);
                adapterCompact.updateAdapter(productList);
                adapterCompact.notifyDataSetChanged();
            }
        }
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
            startActivity(new Intent(this, MainSettings.class));
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
        } else if (id == R.id.action_scan) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ProductList.this);
            intentIntegrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateWithRealData()
    {
        ProductDB db = new ProductDB(this);
        //db.dropEverythingAndRebuild();

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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove("lastQueried").apply();

        //Update adapter
        productList = db.getAllProducts();
        if (!isCompact) {
            adapter.updateAdapter(productList);
            adapter.notifyDataSetChanged();
        } else {
            adapterCompact.updateAdapter(productList);
            adapterCompact.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        populateWithRealData();
    }

    /**
     * Intent Result from the Intent Integrator
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d(TAG, "Parsing Barcode data");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null){
            Log.d(TAG, "Found valid barcode data");

            Barcode barcode = new Barcode(result.getFormatName(), result.getContents());
            barcode.setToString(result.toString());
            processBarCode(barcode);
            //resultView.setText(result.toString());
        }
        Log.d(TAG, "Parse Completed");
    }

    /**
     * Processes the barcode and display it onto a textview
     * @param barcode the barcode object
     */
    public void processBarCode(Barcode barcode){
        searchField.setText(barcode.getContents());
    }
}
