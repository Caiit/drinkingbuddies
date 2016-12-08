package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.data;

public class DrinkAdapter extends ArrayAdapter<Drink> {

    Context context;

    public DrinkAdapter(Context context, int resource, ArrayList<Drink> drinks) {
        super(context, resource, drinks);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Drink drink = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview, parent, false);
        }

        // Set onclick listener to show details of an item when clicked
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout details = (LinearLayout) view.findViewById(R.id.detailsLayout);
                if (details.getVisibility() == View.GONE) {
                    details.setVisibility(View.VISIBLE);
                }
                else {
                    details.setVisibility(View.GONE);
                }
            }
        });

        // Set the data
        showImage(convertView, drink.getBitImg());

        TextView nameTV = (TextView) convertView.findViewById(R.id.resultText);
        TextView categoryTV = (TextView) convertView.findViewById(R.id.categoryText);
        nameTV.setText(drink.getName());
        categoryTV.setText(drink.getCategory());
        return convertView;
    }

    public void showImage(View view, Bitmap img) {
        ImageView image = (ImageView) view.findViewById(R.id.resultImg);
        image.setImageBitmap(img);
    }
}
