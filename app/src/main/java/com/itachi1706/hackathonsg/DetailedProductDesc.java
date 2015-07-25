package com.itachi1706.hackathonsg;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hackathonsg.AsyncTasks.GetProductImage;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.reference.ProductImageTemp;
import com.itachi1706.hackathonsg.reference.ProductStorage;
import com.itachi1706.hackathonsg.reference.StaticReferences;

import java.text.DecimalFormat;

public class DetailedProductDesc extends AppCompatActivity {

    private final String KEY = "DET-PRODDESC";
    private int key;

    private JSONProducts product;

    private ImageView productImage;
    private TextView storeName;
    private TextView orPrice;
    private TextView discPrice;
    private TextView availability;

    private FloatingActionButton addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product_desc);

        if (!this.getIntent().hasExtra("key"))
            this.finish();

        key = this.getIntent().getIntExtra("key", -1);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);



        productImage = (ImageView) findViewById(R.id.ivProduct);
        storeName = (TextView) findViewById(R.id.tvStore);
        orPrice = (TextView) findViewById(R.id.tvOPrice);
        discPrice = (TextView) findViewById(R.id.tvDPrice);
        availability = (TextView) findViewById(R.id.tvAvail);

        addToCart = (FloatingActionButton) findViewById(R.id.add_cart_fab);
        addToCart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ProductStorage.hasInserted(sp, product))
                    Toast.makeText(DetailedProductDesc.this, "Remove from Cart", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(DetailedProductDesc.this, "Add to Cart", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ProductStorage.hasInserted(sp, product))
                {
                    //Remove From Cart
                    ProductStorage.getProduct(sp, product);
                    Toast.makeText(DetailedProductDesc.this, "Removed " + product.getTitle() + " from Cart", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Add to Cart
                    ProductStorage.addNewProduct(product, sp);
                    Toast.makeText(DetailedProductDesc.this, "Added " + product.getTitle() + " to Cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (key == -1)
        {
            errorPage();
        }
        else
        {
            ProductDB db = new ProductDB(this);
            product = db.getJSONProductByKey(key);

            populateGeneralProductInfo(product);
        }
    }



    @SuppressWarnings("ConstantConditions")
    private void errorPage()
    {
        try
        {
            this.getSupportActionBar().setTitle("Unknown Item");
        } catch (NullPointerException ex)
        {
            Log.e(KEY, "Null Pointer Generated");
        }

        //productImage.setImageDrawable(this.getResources().getDrawable(R.mipmap.ic_launcher));

    }


    private void populateGeneralProductInfo(JSONProducts i)
    {
        //Reset data
        discPrice.setVisibility(View.INVISIBLE);
        orPrice.setPaintFlags(orPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        DecimalFormat df = new DecimalFormat("0.00");

        //Populate

        //Store Name
        //noinspection ConstantConditions
        this.getSupportActionBar().setTitle(i.getTitle());

        //ivProduct
        if (ProductImageTemp.checkIfProductExists(this, i.getID()))
        {
            //Get from storage
            productImage.setImageDrawable(ProductImageTemp.getProduct(this, i.getID()));
            Log.d("PRODUCT RETRIEVAL", "Retrieved " + i.getID() + "'s product from device");
        }
        else
        {
            //Get from internet
            new GetProductImage(this, productImage, i.getID()).execute(i.getImage());
        }

        //tvStore
        storeName.setText(i.getStore());

        //tvOPrice
        //orPrice.setText("SG$" + df.format(i.getRetailPrice()));
        orPrice.setText("SG" + i.getRetailPrice());

        //tvDPrice
        //Check if there's a disc price
        if (i.getOfferPrice() != null && !i.getOfferPrice().equals(""))
        {
            //discPrice.setText("SG$" + df.format(i.getOfferPrice()));
            discPrice.setText("SG" + i.getOfferPrice());
            discPrice.setVisibility(View.VISIBLE);
            orPrice.setPaintFlags(orPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //tvAvail
        if (i.getStock())
        {
            //Available
            availability.setText("Available");
            availability.setTextColor(Color.GREEN);
        }
        else
        {
            //Not Available
            availability.setText("Temporarily out of stock");
            availability.setTextColor(Color.RED);
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_product_desc, menu);
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
