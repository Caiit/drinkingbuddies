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
 * The DrinkAsyncTask handles the search on the background.
 * It sends the query to the HttpRequestHelper and creates Drink
 * objects from the obtained data.
 */

class DrinkAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private DrinkActivity activity;

    /**
     * Constructs the async task with the activity.
     */
    DrinkAsyncTask(DrinkActivity activity) {
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
     * Create Drink objects from the obtained data and
     * show it in the current activity to the user.
     */
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Drink drink = new Drink();
        try {
            JSONObject respObj = new JSONObject(result);
            if (respObj.getString("drinks").equals("null")) {
                Toast.makeText(context, "No data was found", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray drinksObj = respObj.getJSONArray("drinks");
            for (int i = 0; i < drinksObj.length(); i++) {
                JSONObject drinkObj = drinksObj.getJSONObject(i);
                int id = Integer.parseInt(drinkObj.getString("idDrink"));
                String name = drinkObj.getString("strDrink");
                String category = drinkObj.getString("strCategory");
                String alcoholic = drinkObj.getString("strAlcoholic");
                String glass = drinkObj.getString("strGlass");
                String instructions = drinkObj.getString("strInstructions");
                String img = drinkObj.getString("strDrinkThumb");
                ArrayList<String> ingredients = new ArrayList<>();
                ArrayList<String> measures = new ArrayList<>();
                // API always returns 15 ingredients and measures
                for (int j = 1; j < 16; j++) {
                    String ingredient = drinkObj.getString("strIngredient" + j);
                    if (!ingredient.equals("")) {
                        ingredients.add(ingredient);
                        measures.add(drinkObj.getString("strMeasure" + j));
                    }
                }
                drink = new Drink(id, name, category, alcoholic, glass, instructions, img,
                                     ingredients, measures, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.activity.showData(drink);
    }
}