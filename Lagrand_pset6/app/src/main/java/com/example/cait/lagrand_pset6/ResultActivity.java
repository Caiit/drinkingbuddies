package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private DrinkAdapter adapter;
    private String query;
    private ArrayList<Drink> drinks;

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private GoogleApiClient googleApiClient;

    private String userName;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Insert toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Get query
        getQuery(getIntent());

        // Get data from api
        DrinkAsyncTask task = new DrinkAsyncTask(this);
        task.execute(query);

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
    }

    /*****************
     * Show results. *
     ****************/
    public void showData(ArrayList<Drink> drinks) {
        this.drinks = drinks;

        // Get the list view and fill it with the drinks
        ListView drinksListView = (ListView) findViewById(R.id.resultListView);
        adapter = new DrinkAdapter(this, R.layout.result_listview, drinks);
        drinksListView.setAdapter(adapter);

        // Set image
        for (int i = 0; i < drinks.size(); i++) {
            // Get drink
            Drink drink = drinks.get(i);
            ImageAsyncTask task = new ImageAsyncTask(this);
            task.execute(new ImageTaskParams(i, drink.getImg()));
        }
    }

    public void setImage(int pos, Bitmap img) {
        if (img != null) {
            drinks.get(pos).setBitImg(img);
        }
        adapter.notifyDataSetChanged();
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
            case android.R.id.home:

                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**************************
     * Get query from search. *
     **************************/
    @Override
    protected void onNewIntent(Intent intent) {
        getQuery(intent);
    }

    private void getQuery(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(String.valueOf(connectionResult), "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
