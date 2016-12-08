package com.example.cait.lagrand_pset6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class ImageAsyncTask extends AsyncTask<ImageTaskParams, Integer, Bitmap> {
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

    protected Bitmap doInBackground(ImageTaskParams... params) {
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
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    protected void onProgressUpdate(String... params) {
        // This method is never used
        Toast.makeText(context, "Still going strong", Toast.LENGTH_SHORT).show();
    }

    protected void onPostExecute(Bitmap bmp) {
        super.onPostExecute(bmp);

//        if (bmp == null) {
//            Toast.makeText(context, "No image was found", Toast.LENGTH_SHORT).show();
//        }
        this.activity.setImage(pos, bmp);
    }
}
