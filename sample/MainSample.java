package com.itachi1706.qr;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    DatabaseHandler db = new DatabaseHandler(this);

    private Button scanBtn;
    private Button qrBtn;
    private Button bcBtn;
    private EditText inputText;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.inputText = (EditText)this.findViewById(R.id.editText1);
        this.text = (TextView)this.findViewById(R.id.textView1);
        this.qrBtn = (Button)this.findViewById(R.id.buttonQR);
        this.bcBtn = (Button)this.findViewById(R.id.buttonBC);
        this.bcBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Manual: ", "Manually inputed a barcode...");
                String tmpBC = inputText.getText().toString();
                Log.d("Manual: ", "Inserting to DB");
                db.addCode(tmpBC, "CODE_39", null, null, 1);
            }
        });
        this.qrBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Manual: ", "Manually inputed a QR code...");
                String tmpBC = inputText.getText().toString();
                Log.d("Manual: ", "Inserting to DB");
                db.addCode(tmpBC, "QR_CODE", null, null, 2);
            }
        });
        this.scanBtn = (Button)this.findViewById(R.id.scan);
        this.scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.d("Button:", "Launching scanner...");
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
                Log.d("Button: ", "Scan completed.");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Result
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d("Result: ", "Parsing result...");
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null){
            Log.d("Result: ", "There is a scan result! Checking content...");
            String contents = scanResult.getContents();
            if (contents != null){
                Log.d("Result: ", "Content found! Appending string...");
                text.setText(scanResult.toString());
                Log.d("Insert: ", "Obtaining values content...");
                String con = scanResult.getContents();
                Log.d("Insert: ", "Obtaining values format...");
                String forma = scanResult.getFormatName();
                Log.d("Insert: ", "Obtaining values ecl...");
                String ecl = scanResult.getErrorCorrectionLevel();
                Log.d("Insert: ", "Obtaining values orientation...");
                Integer ori = scanResult.getOrientation();
                Log.d("Insert: ", "Checking if qr or barcode");
                if (forma == "QR_CODE"){
                    Log.d("Insert: ", "Insert QR to DB...");
                    db.addCode(con, forma, ecl, ori, 2);
                } else {
                    Log.d("Insert: ", "Inserting BC to DB...");
                    db.addCode(con, forma, ecl, ori, 1);
                    Log.d("Insert: ", "Inserted into DB");
                }
            }
        }
    }
}
