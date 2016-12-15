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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private DrinkAdapter adapter;
    private String query;
    private ArrayList<Drink> drinks;
    private int currentViewId;



    private static final List<String> CATEGORIES = Arrays.asList("Ordinary Drink", "Cocktail",
            "Soft Drink / Soda", "Milk / Float / Shake", "Other/Unknown", "Cocoa", "Shot",
            "Coffee / Tea", "Homemade Liqueur", "Punch / Party Drink", "Beer");
    private static final List<String> GLASSES = Arrays.asList("Highball glass", "Cocktail glass",
            "Old-fashioned glass", "Collins glass", "Pousse cafe glass", "Red wine glass",
            "Whiskey sour glass", "Champagne flute", "Cordial glass", "Irish coffee cup",
            "White wine glass", "Brandy snifter", "Parfait glass", "vote",
            "Margarita/Coupette glass", "Shot glass", "Coffee mug", "Punch bowl",
            "Hurricane glass", "Pitcher", "Pint glass", "Mason jar", "Beer mug",
            "Wine Glass", "Beer pilsner", "Sherry glass");

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private GoogleApiClient googleApiClient;

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
        DrinkIdAsyncTask task = new DrinkIdAsyncTask(this);
        task.execute(query);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Go to handle search if advanced search was currently used
        if (currentViewId > 100) {
            handleSearch(findViewById(currentViewId));
        }
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
            ImageAsyncTask task = new ImageAsyncTask(this, null);
            task.execute(new ImageTaskParams(i, drink.getImg()));
        }
    }

    public void setImage(int pos, String imgString) {
        if (imgString != null) {
            drinks.get(pos).setBitImg(imgString);
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
                startActivity(new Intent(this, SignInActivity.class));
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

    /**************************
     * Get query from search. *
     **************************/
    @Override
    protected void onNewIntent(Intent intent) {
        getQuery(intent);
    }

    private void getQuery(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = "search.php?s=" + intent.getStringExtra(SearchManager.QUERY);
            currentViewId = -1;
            // Don't show advanced search
            LinearLayout advanced = (LinearLayout) findViewById(R.id.advancedSearchView);
            advanced.setVisibility(View.GONE);
        }
        else {
            query = "random.php";
            currentViewId = R.id.randomButton;
        }
    }

    public void handleSearch(View view) {
        currentViewId = view.getId();
        // Get type of search from radio buttons
        RadioButton button = (RadioButton) view;
        RadioGroup group = (RadioGroup) findViewById(R.id.extraGroupButton);
        String query = (String) button.getTag();

        if ((((RadioButton)findViewById(R.id.categoryButton)).isChecked() ||
                ((RadioButton)findViewById(R.id.glassButton)).isChecked())) {

            // Only add new buttons if view is currently gone or switches between category
            // and glass
            if (group.getVisibility() == View.GONE ||
                    (query.equals("c") && group.getTag().equals("g")) ||
                    (query.equals("g") && group.getTag().equals("c"))) {
                group.setVisibility(View.VISIBLE);

                // Set radio button filters
                List<String> groupItems;
                String tag;
                if (((RadioButton)findViewById(R.id.categoryButton)).isChecked()) {
                    groupItems = CATEGORIES;
                    tag = "c";
                }
                else {
                    groupItems = GLASSES;
                    tag ="g";
                }

                group.setTag(tag);

                group.removeAllViews();

                for (String item : groupItems) {
                    RadioButton itemButton = new RadioButton(this);
                    itemButton.setText(item);
                    itemButton.setTag("filter.php?" + tag + "=" + item);
                    itemButton.setTextSize(12);
                    itemButton.setOnClickListener(this);
                    group.addView(itemButton);
                }
            }
        }
        else {
            group.setVisibility(View.GONE);
        }

        if (query.equals("c") || query.equals("g")) {
            // Don't search api
            return;
        }

        // Get data from api
        DrinkIdAsyncTask task = new DrinkIdAsyncTask(this);
        task.execute(query);
    }

    @Override
    public void onClick(View view) {
        handleSearch(view);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(String.valueOf(connectionResult), "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    // Override save instances to save the story after rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("view", currentViewId);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        currentViewId = (int) savedInstanceState.getSerializable("view");
    }
}