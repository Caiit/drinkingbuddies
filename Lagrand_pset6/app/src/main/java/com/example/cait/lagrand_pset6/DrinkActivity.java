package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The DrinkActivity shows all the information of one drink to the user.
 * The user can click the favourite button to add or delete the drink
 * from its favourite list.
 */

public class DrinkActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbRef;

    // Google API client
    private GoogleApiClient googleApiClient;

    private String query;
    private Drink drink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        setToolbar();

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        checkLoggedIn();

        getQuery(getIntent());

        // Get data from api
        DrinkAsyncTask task = new DrinkAsyncTask(this);
        task.execute(query);

        // Initialize firebase database reference
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Initialize google api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    /**
     * Show the information of a drink.
     */
    public void showData(final Drink drink) {
        this.drink = drink;
        // Set the image
        ImageView imageView = (ImageView) findViewById(R.id.drinkImg);
        Picasso.with(this).load(drink.getImg()).into(imageView);

        // Get and set the textviews
        TextView nameTV = (TextView) findViewById(R.id.drinkText);
        TextView categoryTV = (TextView) findViewById(R.id.categoryText);
        TextView alcoholicTV = (TextView) findViewById(R.id.alcoholicText);
        TextView glassTV = (TextView) findViewById(R.id.glassText);
        TextView instructionsTV = (TextView) findViewById(R.id.instructionsText);

        nameTV.setText(drink.getName());
        categoryTV.setText(drink.getCategory());
        alcoholicTV.setText(drink.getAlcoholic());
        glassTV.setText(drink.getGlass());
        instructionsTV.setText(drink.getInstructions());

        if (drink.getIngredients() != null) {
            String ingredients = "";
            String measures = "";
            for (int i = 0; i < drink.getIngredients().size(); i++) {
                ingredients += drink.getIngredients().get(i) + "\n";
                measures += drink.getMeasures().get(i) + "\n";
            }

            TextView ingredientsTV = (TextView) findViewById(R.id.ingredientsTextView);
            TextView measuresTV = (TextView) findViewById(R.id.measuresTextView);
            ingredientsTV.setText(ingredients);
            measuresTV.setText(measures);
        }

        setFavButton();
    }

    /**
     * Set the favourite button, on if the drink is in the database, off otherwise.
     */
    private void setFavButton() {
        final ImageButton favButton = (ImageButton) findViewById(R.id.favButton);
        Query query = dbRef.child(firebaseUser.getUid()).child("drinks").orderByChild("id")
                .equalTo(drink.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Drink dbDrink = snap.getValue(Drink.class);
                    if (dbDrink.getFav()) {
                        drink.setFav(true);
                        favButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                android.R.drawable.btn_star_big_on));
                    }
                    else {
                        drink.setFav(false);
                        favButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                android.R.drawable.btn_star_big_off));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database error", "onCancelled: db error", databaseError.toException());
            }
        });
    }

    /**
     * Add or remove a drink to/from the favourite database.
     */
    public void addFav(View view) {
        ImageButton favButton = (ImageButton) view;
        if (drink.getFav()) {
            drink.setFav(false);
            favButton.setImageDrawable(ContextCompat.getDrawable(this,
                    android.R.drawable.btn_star_big_off));

            Query query = dbRef.child(firebaseUser.getUid()).child("drinks")
                               .orderByChild("id").equalTo(drink.getId());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        snap.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Database error", "onCancelled: db error", databaseError.toException());
                }
            });
        }
        else {
            drink.setFav(true);
            SmallDrink smallDrink = new SmallDrink(drink.getId(), drink.getName(), drink.getImg(),
                                                   drink.getFav());
            favButton.setImageDrawable(ContextCompat.getDrawable(this,
                    android.R.drawable.btn_star_big_on));
            dbRef.child(firebaseUser.getUid()).child("drinks").push().setValue(smallDrink);
        }
    }

    /**
     * Get query from search.
     */
    private void getQuery(Intent intent) {
        query = "lookup.php?i=" + intent.getExtras().get("Id");
    }


    /**
     * Check if the user is logged in, if not, go to sign in activity.
     */
    private void checkLoggedIn() {
        if (firebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }


    /**
     * Toolbar method: set the toolbar.
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Toolbar method: set the search option in the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    /**
     * Toolbar method: handle toolbar item clicks.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.signout:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            case R.id.advancedSearch:
                startActivity(new Intent(this, ResultActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * If no connection to Google Play, show to the user.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(String.valueOf(connectionResult), "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}