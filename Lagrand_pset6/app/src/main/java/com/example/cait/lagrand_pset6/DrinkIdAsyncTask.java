package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.cait.lagrand_pset6.HttpRequestHelper;
import com.example.cait.lagrand_pset6.ResultActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.R.attr.id;
import static android.R.attr.logo;

class DrinkIdAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ResultActivity activity;

    // Constructor
    DrinkIdAsyncTask(ResultActivity activity) {
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
            ArrayList<SmallDrink> drinks = new ArrayList<>();
            try {
                JSONObject respObj = new JSONObject(result);
                JSONArray drinksObj = respObj.getJSONArray("drinks");
                for (int i = 0; i < drinksObj.length(); i++) {
                    JSONObject drink = drinksObj.getJSONObject(i);
                    int id = Integer.parseInt(drink.getString("idDrink"));
                    String name = drink.getString("strDrink");
                    String img = drink.getString("strDrinkThumb");
                    String bitImg = null;
                    ImageAsyncTask task = new ImageAsyncTask();
                    try {
                        bitImg = task.execute(new ImageTaskParams(0, img)).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }



                    Log.d(name + ": " + bitImg, "onPostExecute: bitimg");
                    drinks.add(new SmallDrink(id, name, img, false));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.activity.showData(drinks);
        }
    }
}