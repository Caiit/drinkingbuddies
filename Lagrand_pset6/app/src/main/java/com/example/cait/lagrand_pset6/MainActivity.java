package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The MainActivity shows the favourite drinks of the user.
 * The user can click the favourite button to delete the drink
 * from its favourite list.
 */

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // Google API client
    private GoogleApiClient googleApiClient;

    private String userName;
    private DrinkAdapter adapter;
    private ArrayList<SmallDrink> drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        checkLoggedIn();

        // Initialize google api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        showSavedDrinks();
    }

    /**
     * Show the information of the saved drinks in the database.
     */
    private void showSavedDrinks() {
        DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = firebaseDatabaseReference.child(firebaseUser.getUid()).child("drinks");
        drinks = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                drinks.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SmallDrink dbDrink = snap.getValue(SmallDrink.class);
                    drinks.add(dbDrink);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database error", "onCancelled: db error", databaseError.toException());
            }
        });

        // Get the list view and fill it with the drinks
        ListView drinksListView = (ListView) findViewById(R.id.favListView);
        adapter = new DrinkAdapter(this, R.layout.result_listview, drinks);
        drinksListView.setAdapter(adapter);
    }

    /**
     * Check if the user is logged in, if not, go to sign in activity.
     */
    private void checkLoggedIn() {
        if (firebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            userName = firebaseUser.getDisplayName();
            TextView welcome = (TextView) findViewById(R.id.welcomeText);
            welcome.setText(userName + "! \n What are we gonna drink today?");
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
                userName = "ANONYMOUS";
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
