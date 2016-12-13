package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.cait.lagrand_pset6.HttpRequestHelper;
import com.example.cait.lagrand_pset6.ResultActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.R.attr.logo;

class DrinkAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ResultActivity activity;

    // Constructor
    DrinkAsyncTask(ResultActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    protected void onPreExecute() {
        Toast.makeText(context, "Getting data from server", Toast.LENGTH_SHORT).show();
    }


    protected String doInBackground(String... params) {
        return HttpRequestHelper.downloadFromServer(params);
    }

    protected void onProgressUpdate(String... params) {
        // This method is never used
        Toast.makeText(context, "Still going strong", Toast.LENGTH_SHORT).show();
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.length() == 0) {
            Toast.makeText(context, "No data was found", Toast.LENGTH_SHORT).show();
        }
        else {
            ArrayList<Drink> drinks = new ArrayList<>();
            try {
                JSONObject respObj = new JSONObject(result);
                JSONArray drinksObj = respObj.getJSONArray("drinks");
                for (int i = 0; i < drinksObj.length(); i++) {
                    JSONObject drink = drinksObj.getJSONObject(i);
                    int id = Integer.parseInt(drink.getString("idDrink"));
                    String name = drink.getString("strDrink");
                    String category = drink.getString("strCategory");
                    String alcoholic = drink.getString("strAlcoholic");
                    String glass = drink.getString("strGlass");
                    String instructions = drink.getString("strInstructions");
                    String img = drink.getString("strDrinkThumb");
                    String dateModified = drink.getString("dateModified");
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> measures = new ArrayList<>();
                    // API always returns 15 ingredients and measures
                    for (int j = 1; j < 16; j++) {
                        String ingredient = drink.getString("strIngredient" + j);
                        if (!ingredient.equals("")) {
                            ingredients.add(ingredient);
                            measures.add(drink.getString("strMeasure" + j));
                        }
                    }

                    drinks.add(new Drink(id, name, category, alcoholic, glass, instructions, img,
                                         ingredients, measures, dateModified, false));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.activity.showData(drinks);
        }
    }
}