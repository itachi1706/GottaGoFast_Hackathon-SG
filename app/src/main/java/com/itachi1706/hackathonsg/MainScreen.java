package com.itachi1706.hackathonsg;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itachi1706.hackathonsg.Objects.Barcode;
import com.itachi1706.hackathonsg.libraries.barcode.IntentIntegrator;
import com.itachi1706.hackathonsg.libraries.barcode.IntentResult;

public class MainScreen extends AppCompatActivity {

    private final String TAG = "MainScreen";

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Initialize
        fab = (FloatingActionButton) findViewById(R.id.add_product_fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, ProductList.class));
            }
        });
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
