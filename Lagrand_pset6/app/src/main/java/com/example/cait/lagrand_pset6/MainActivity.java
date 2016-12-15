package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV;
        public ImageButton favButton;
        public ImageView imgView;

        public DrinkViewHolder(View v) {
            super(v);
            nameTV = (TextView) itemView.findViewById(R.id.resultText);
            favButton = (ImageButton) itemView.findViewById(R.id.favouriteButton);
            imgView = (ImageView) itemView.findViewById(R.id.resultImg);
        }
    }

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // Firebase instance variables
    private DatabaseReference firebaseDatabaseReference;
    private FirebaseRecyclerAdapter<SmallDrink, DrinkViewHolder> firebaseAdapter;
    private ArrayList<SmallDrink> drinks;

    private GoogleApiClient googleApiClient;

    private String userName;
    private DrinkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Insert toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get user
        userName = "ANONYMOUS";
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            userName = firebaseUser.getDisplayName();
            TextView welcome = (TextView) findViewById(R.id.welcomeText);
            welcome.setText(userName + "! \n What are we gonna drink today?");
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        showSavedDrinks();
    }

    /**********************
     * Show saved drinks. *
     **********************/
    private void showSavedDrinks() {
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = firebaseDatabaseReference.child(firebaseUser.getUid()).child("drinks");
        Log.d(String.valueOf(query), "showSavedDrinks: query");
        drinks = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SmallDrink dbDrink = snap.getValue(SmallDrink.class);
                    drinks.add(dbDrink);
                    Log.d(String.valueOf(dbDrink.getId()), "onDataChange: dbDrink");
                }
                Log.d(String.valueOf(drinks.size()), "showSavedDrinks: drinks");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Don't do anything
            }
        });

        // Get the list view and fill it with the drinks
        ListView drinksListView = (ListView) findViewById(R.id.favListView);
        adapter = new DrinkAdapter(this, R.layout.result_listview, drinks);
        drinksListView.setAdapter(adapter);
    }

    /********************
     * Toolbar methods. *
     ********************/
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.signout:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                userName = "ANONYMOUS";
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.advancedSearch:
                startActivity(new Intent(this, ResultActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(String.valueOf(connectionResult), "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
