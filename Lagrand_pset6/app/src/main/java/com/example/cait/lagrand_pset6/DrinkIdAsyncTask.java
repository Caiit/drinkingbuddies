package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The DrinkIdAsyncTask handles the search on the background.
 * It sends the query to the HttpRequestHelper and creates SmallDrink
 * objects from the obtained data.
 */

class DrinkIdAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ResultActivity activity;

    /**
     * Constructs the async task with the activity.
     */
    DrinkIdAsyncTask(ResultActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    /**
     * Show the user that the data is being obtained.
     */
    protected void onPreExecute() {
        Toast.makeText(context, "Getting data from server", Toast.LENGTH_SHORT).show();
    }

    /**
     * Obtain the data from the API.
     */
    protected String doInBackground(String... params) {
        return HttpRequestHelper.downloadFromServer(params);
    }

    /**
     * Create SmallDrink objects from the obtained data and
     * show it in the current activity to the user.
     */
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ArrayList<SmallDrink> drinks = new ArrayList<>();
        try {
            JSONObject respObj = new JSONObject(result);
            if (respObj.getString("drinks").equals("null")) {
                Toast.makeText(context, "No data was found", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray drinksObj = respObj.getJSONArray("drinks");
            for (int i = 0; i < drinksObj.length(); i++) {
                JSONObject drink = drinksObj.getJSONObject(i);
                int id = Integer.parseInt(drink.getString("idDrink"));
                String name = drink.getString("strDrink");
                String img = drink.getString("strDrinkThumb");
                drinks.add(new SmallDrink(id, name, img, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.activity.showData(drinks);
    }
}