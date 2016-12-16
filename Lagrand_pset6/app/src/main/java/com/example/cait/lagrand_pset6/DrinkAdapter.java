package com.example.cait.lagrand_pset6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The DrinkAdapter handles showing the drinks in a listview.
 */

class DrinkAdapter extends ArrayAdapter<SmallDrink> {

    private Context context;
    private Activity activity;
    
    private DatabaseReference dbRef;
    private String userId;

    DrinkAdapter(Activity activity, int resource, ArrayList<SmallDrink> drinks) {
        super(activity.getApplicationContext(), resource, drinks);
        this.activity = activity;
        this.context = activity.getApplicationContext();

        // Connect to database
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.result_listview, parent,
                                                               false);
        }

        // Get the drink at this position
        final SmallDrink drink = getItem(position);

        // Set onclick listener to show details of a drink when clicked
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to drink activity
                Intent goToDrink = new Intent(context, DrinkActivity.class);
                goToDrink.putExtra("Id", drink.getId());
                context.startActivity(goToDrink);
            }
        });

        // Set the data of the drink
        ImageView imageView = (ImageView) convertView.findViewById(R.id.resultImg);
        Picasso.with(activity).load(drink.getImg()).into(imageView);
        TextView nameTV = (TextView) convertView.findViewById(R.id.resultText);
        nameTV.setText(drink.getName());

        handleFav(drink, convertView);

        return convertView;
    }

    /**
     * Handle the favourite button: set the favourite button on if the
     * drink is in the database and off otherwise. When the button is
     * clicked, add or remove the drink to/from the database.
     */
    private void handleFav(final SmallDrink drink, View convertView) {
        // Set favourite button on or off
        final ImageButton favButton = (ImageButton) convertView.findViewById(R.id.favouriteButton);
        favButton.setImageDrawable(ContextCompat.getDrawable(context,
                                                             android.R.drawable.btn_star_big_off));
        Query query = dbRef.child(userId).child("drinks").orderByChild("id").equalTo(drink.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SmallDrink dbDrink = snap.getValue(SmallDrink.class);
                    if (dbDrink.getFav()) {
                        favButton.setImageDrawable(ContextCompat.getDrawable(context,
                                android.R.drawable.btn_star_big_on));
                    }
                    else {
                        favButton.setImageDrawable(ContextCompat.getDrawable(context,
                                android.R.drawable.btn_star_big_off));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database error", "onCancelled: db error", databaseError.toException());
            }
        });

        // Handle favourite with database
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drink.getFav()) {
                    drink.setFav(false);
                    favButton.setImageDrawable(ContextCompat.getDrawable(context,
                            android.R.drawable.btn_star_big_off));

                    Query query = dbRef.child(userId).child("drinks").orderByChild("id")
                                       .equalTo(drink.getId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                snap.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("Database error", "onCancelled: db error",
                                    databaseError.toException());
                        }
                    });
                }
                else {
                    drink.setFav(true);favButton.setImageDrawable(ContextCompat.getDrawable(context,
                            android.R.drawable.btn_star_big_on));
                    dbRef.child(userId).child("drinks").push().setValue(drink);
                }
            }
        });
    }
}
