package com.itachi1706.hackathonsg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.StoredProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.Barcode;
import com.itachi1706.hackathonsg.Objects.JSONGeneralStoredProducts;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.Objects.JSONStoredProducts;
import com.itachi1706.hackathonsg.libraries.barcode.IntentIntegrator;
import com.itachi1706.hackathonsg.libraries.barcode.IntentResult;
import com.itachi1706.hackathonsg.reference.ProductStorage;

import java.util.ArrayList;
import java.util.Arrays;

public class MainScreen extends AppCompatActivity {

    private final String TAG = "MainScreen";

    private FloatingActionButton fab;
    private ListView cart;

    private ArrayAdapter<String> emptySet;
    private StoredProductViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Initialize
        fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        cart = (ListView) findViewById(R.id.lvCart);

        emptySet = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[] {"No Cart Item. Why don't you get started?"});
        adapter = new StoredProductViewAdapter(this, R.layout.listview_products, new ArrayList<JSONStoredProducts>());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, ProductList.class));
            }
        });

        cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = cart.getItemAtPosition(position);
                if (o instanceof JSONStoredProducts) {
                    final JSONStoredProducts item = (JSONStoredProducts) o;
                    ProductDB db = new ProductDB(MainScreen.this);
                    final JSONProducts p = db.getJSONProductByKey(item.getKey());

                    if (p != null) {
                        if (item.isPurchased())
                        {
                            new AlertDialog.Builder(MainScreen.this).setTitle("Already Purchased")
                                    .setMessage("You have already purchased this item")
                                    .setNeutralButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent descIntent = new Intent(MainScreen.this, DetailedProductDesc.class);
                                            descIntent.putExtra("key", item.getKey());
                                            startActivity(descIntent);
                                        }
                                    }).setPositiveButton("Close", null).show();
                        } else
                        {
                            new AlertDialog.Builder(MainScreen.this).setTitle(p.getTitle())
                                    .setMessage("What do you want to do with it? (Click outside the box to cancel)")
                                    .setNegativeButton("Purchased", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ProductStorage.markPurchased(PreferenceManager.getDefaultSharedPreferences(MainScreen.this), item);
                                            updateList(PreferenceManager.getDefaultSharedPreferences(MainScreen.this));
                                        }
                                    })
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ProductStorage.removeFromCart(PreferenceManager.getDefaultSharedPreferences(MainScreen.this), item);
                                            Toast.makeText(MainScreen.this, "Removed " + p.getTitle() + " from Cart", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNeutralButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent descIntent = new Intent(MainScreen.this, DetailedProductDesc.class);
                                            descIntent.putExtra("key", item.getKey());
                                            startActivity(descIntent);
                                        }
                                    }).show();
                        }
                    } else {
                        new AlertDialog.Builder(MainScreen.this).setTitle("Error")
                                .setMessage("Unable to retrive product data. Please rebuild the database in the application settings and try again")
                                .setPositiveButton("Close", null).show();
                    }
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!ProductStorage.hasStorageData(sp))
        {
            cart.setAdapter(emptySet);
        }
        else
        {
            cart.setAdapter(adapter);

            updateList(sp);
        }

    }

    private void updateList(SharedPreferences sp)
    {
        String json = sp.getString("storedPurchases", "{\"d\":null}");
        //Populate adapter
        Gson gson = new Gson();
        JSONStoredProducts[] prodTmp = gson.fromJson(json, JSONGeneralStoredProducts.class).getStorage();
        ArrayList<JSONStoredProducts> prod = new ArrayList<>(Arrays.asList(prodTmp));

        adapter.updateAdapter(prod);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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
        } else if (id == R.id.action_view_all_products) {
            startActivity(new Intent(this, ProductList.class));
            return true;
        } else if (id == R.id.action_scan) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainScreen.this);
            intentIntegrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
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
        new AlertDialog.Builder(this).setTitle("Barcode Found!")
                .setMessage(barcode.toString())
                .setPositiveButton("Close", null)
                .show();
    }
}
