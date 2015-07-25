package com.itachi1706.hackathonsg;

import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.ListViewAdapters.StoredProductViewAdapter;
import com.itachi1706.hackathonsg.Objects.Barcode;
import com.itachi1706.hackathonsg.Objects.JSONStoredProducts;
import com.itachi1706.hackathonsg.libraries.barcode.IntentIntegrator;
import com.itachi1706.hackathonsg.libraries.barcode.IntentResult;
import com.itachi1706.hackathonsg.reference.ProductStorage;

import java.util.ArrayList;

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
        }


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
