package com.itachi1706.hackathonsg.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.R;

import java.util.ArrayList;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.ListViewAdapters
 */
public class ProductViewAdapter extends ArrayAdapter<JSONProducts>
{

    private ArrayList<JSONProducts> items;
    private Context context;

    public ProductViewAdapter(Context context, int textViewResourceid, ArrayList<JSONProducts> objects)
    {
        super(context, textViewResourceid, objects);
        this.items = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_products, parent, false);
        }

        JSONProducts i = items.get(position);

        ImageView productImage = (ImageView) v.findViewById(R.id.ivProducts);
        TextView productName = (TextView) v.findViewById(R.id.tvName);
        TextView productOrigPrice = (TextView) v.findViewById(R.id.tvOriginalPrice);
        TextView productDiscPrice = (TextView) v.findViewById(R.id.tvDiscountedPrice);
        TextView productAvailbility = (TextView) v.findViewById(R.id.tvAvailability);

        //Set invisible disc price
        if (productDiscPrice != null)
        {
            productDiscPrice.setVisibility(View.INVISIBLE);
        }

        if (productOrigPrice != null)
        {
            productOrigPrice.setPaintFlags(productOrigPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (i != null)
        {
            if (productImage != null)
            {
                //TODO AsyncTask get image and store it to product
            }

            if (productName != null)
            {
                productName.setText(i.getTitle());
            }

            if (productOrigPrice != null)
            {
                productOrigPrice.setText("SG$" + i.getRetailPrice());
            }

            if (productDiscPrice != null) {

                //Check if there's a disc price
                if (i.getOfferPrice() != 0)
                {
                    productDiscPrice.setText("SG$" + i.getOfferPrice());
                    productDiscPrice.setVisibility(View.VISIBLE);
                    if (productOrigPrice != null)
                    {
                        productOrigPrice.setPaintFlags(productOrigPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            }

            if (productAvailbility != null)
            {
                if (i.getStock())
                {
                    //Available
                    productAvailbility.setText("Available");
                    productAvailbility.setTextColor(Color.GREEN);
                }
                else
                {
                    //Not Available
                    productAvailbility.setText("Temporarily out of stock");
                    productAvailbility.setTextColor(Color.RED);
                }
            }
        }

        return v;

    }

    @Override
    public int getCount()
    {
        return items != null? items.size():0;
    }

    @Override
    public JSONProducts getItem(int arg0)
    {
        return items.get(arg0);
    }

    public void updateAdapter(ArrayList<JSONProducts> newArrayData)
    {
        this.items = newArrayData;
    }

}
