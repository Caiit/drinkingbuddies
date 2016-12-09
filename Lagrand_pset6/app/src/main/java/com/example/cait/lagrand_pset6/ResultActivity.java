package com.example.cait.lagrand_pset6;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    DrinkAdapter adapter;
    String query;
    ArrayList<Drink> drinks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Insert toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get query
        getQuery(getIntent());

        // Get data from api
        DrinkAsyncTask task = new DrinkAsyncTask(this);
        task.execute(query);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
