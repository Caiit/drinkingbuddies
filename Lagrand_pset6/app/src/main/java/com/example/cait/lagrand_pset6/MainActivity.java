package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV;
        public TextView categoryTV;
        public TextView alcoholicTV;
        public TextView glassTV;
        public TextView instructionsTV;
        public ImageButton favButton;

        public DrinkViewHolder(View v) {
            super(v);
            nameTV = (TextView) itemView.findViewById(R.id.resultText);
            categoryTV = (TextView) itemView.findViewById(R.id.categoryText);
            alcoholicTV = (TextView) itemView.findViewById(R.id.alcoholicText);
            glassTV = (TextView) itemView.findViewById(R.id.glassText);
            instructionsTV = (TextView) itemView.findViewById(R.id.instructionsText);
            favButton = (ImageButton) itemView.findViewById(R.id.favouriteButton);
        }
    }

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // Firebase instance variables
    private DatabaseReference firebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Drink, DrinkViewHolder> firebaseAdapter;

    private GoogleApiClient googleApiClient;

    private String userName;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Insert toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
            if (firebaseUser.getPhotoUrl() != null) {
                imgUrl = firebaseUser.getPhotoUrl().toString();
            }
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        showSavedDrinks();
    }

    /**********************
     * Show saved drinks. *
     **********************/
    private void showSavedDrinks() {
        RecyclerView drinksView = (RecyclerView) findViewById(R.id.drinksRecyclerView);
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAdapter = new FirebaseRecyclerAdapter<Drink, DrinkViewHolder>(Drink.class,
                                R.layout.result_listview, DrinkViewHolder.class, firebaseDatabaseReference.child("drinks")) {
            @Override
            protected void populateViewHolder(DrinkViewHolder viewHolder, Drink drink, int position) {
                viewHolder.nameTV.setText(drink.getName());
                viewHolder.categoryTV.setText(drink.getCategory());
                viewHolder.alcoholicTV.setText(drink.getAlcoholic());
                viewHolder.glassTV.setText(drink.getGlass());
                viewHolder.instructionsTV.setText(drink.getInstructions());
                viewHolder.favButton.setVisibility(View.GONE);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        drinksView.setLayoutManager(linearLayoutManager);
        drinksView.setAdapter(firebaseAdapter);
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
                imgUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
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
