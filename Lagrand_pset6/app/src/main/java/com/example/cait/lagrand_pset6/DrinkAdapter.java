package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DrinkAdapter extends ArrayAdapter<Drink> {

    Context context;

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;

    private DatabaseReference firebaseDatabaseReference;

    public DrinkAdapter(Context context, int resource, ArrayList<Drink> drinks) {
        super(context, resource, drinks);
        this.context = context;

        // Connect to database
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Drink drink = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_listview, parent, false);
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
        TextView alcoholicTV = (TextView) convertView.findViewById(R.id.alcoholicText);
        TextView glassTV = (TextView) convertView.findViewById(R.id.glassText);
        TextView instructionsTV = (TextView) convertView.findViewById(R.id.instructionsText);
        nameTV.setText(drink.getName());
        categoryTV.setText(drink.getCategory());
        alcoholicTV.setText(drink.getAlcoholic());
        glassTV.setText(drink.getGlass());
        instructionsTV.setText(drink.getInstructions());

        String ingredients = "";
        String measures = "";
        for (int i = 0; i < drink.getIngredients().size(); i++) {
            ingredients += drink.getIngredients().get(i) + "\n";
            measures += drink.getMeasures().get(i) + "\n";
        }

        TextView ingredientsTV = (TextView) convertView.findViewById(R.id.ingredientsTextView);
        TextView measuresTV = (TextView) convertView.findViewById(R.id.measuresTextView);
        ingredientsTV.setText(ingredients);
        measuresTV.setText(measures);

        // Handle favourite button

        final ImageButton favButton = (ImageButton) convertView.findViewById(R.id.favouriteButton);
        Query query = firebaseDatabaseReference.child(userId).child("drinks").orderByChild("id").equalTo(drink.getId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Drink dbDrink = snap.getValue(Drink.class);
                    if (dbDrink.getFav()) {
                        favButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    }
                    else {
                        favButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_star_big_off));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Don't do anything
            }
        });


        // Add favourite to database
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drink.getFav()) {
                    drink.setFav(false);
                    favButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_star_big_off));
                }
                else {
                    drink.setFav(true);
                    favButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_star_big_on));
                }
                firebaseDatabaseReference.child(userId).child("drinks").push().setValue(drink);
            }
        });

        return convertView;
    }

    public void showImage(View view, String imgString) {
        if (imgString != null) {
            ImageView image = (ImageView) view.findViewById(R.id.resultImg);
            byte[] imageAsBytes = Base64.decode(imgString.getBytes(), Base64.DEFAULT);
            image.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }
}
