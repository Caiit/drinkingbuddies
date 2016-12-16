package com.example.cait.lagrand_pset6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The HttpRequestHelper handles the interaction with the API
 * http://www.thecocktaildb.com/
 */

class HttpRequestHelper {

    // API URL for searching the api
    private static final String URLSTRING = "http://www.thecocktaildb.com/api/json/v1/1/";

    /**
     * Get the information from the API.
     */
    static synchronized String downloadFromServer(String... params) {
        // Declare return string result
        String result = "";

        // Get query from argument
        String query = params[0];

        // Turn string into url
        URL url;
        try {
            url = new URL(URLSTRING + query);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return result;
        }

        // Make the connection
        HttpURLConnection connection;
        try {
            // Open connection, set request method
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get response code
            Integer responseCode = connection.getResponseCode();

            // If 200-300, read inputstream
            if (200 >= responseCode && responseCode <= 299) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                                                       connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    result += line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}