package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class ImageAsyncTask extends AsyncTask<ImageTaskParams, Integer, String> {
    private Context context;
    private ResultActivity activity;
    private int pos;

    // Constructor
    ImageAsyncTask(ResultActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    protected void onPreExecute() {
        // I left this part out, because the data was obtained really quick and thus the toast
        // was still shown while the data was already on the screen
//        Toast.makeText(context, "Getting image", Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(ImageTaskParams... params) {
        String img = params[0].img;
        this.pos = params[0].pos;
        if (img == null || img.equals("null")) return null;
        URL url;
        try {
            url = new URL(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        String imgString = null;
        try {
            // Create bitmap image of imgurl and convert to string to be able to save it in firebase
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
           imgString = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgString;
    }

    protected void onProgressUpdate(String... params) {
        // This method is never used
        Toast.makeText(context, "Still going strong", Toast.LENGTH_SHORT).show();
    }

    protected void onPostExecute(String imgString) {
        super.onPostExecute(imgString);
        this.activity.setImage(pos, imgString);
    }
}