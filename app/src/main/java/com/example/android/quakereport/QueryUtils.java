package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    private final static String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String JSON_URL) {
        //create url
        URL jason = createUrl(JSON_URL);
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes;

        String JSON_RESPONSE = null;
        try {
            JSON_RESPONSE = makeHttpRequst(jason);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        earthquakes = new ArrayList<>();
        Earthquake temp;


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of Earthquake objects with the corresponding data.
            JSONObject jsonObject = new JSONObject(JSON_RESPONSE);
            JSONArray jArray = jsonObject.getJSONArray("features");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i).getJSONObject("properties");

                temp = new Earthquake(jObject.getDouble("mag"),//“mag” for magnitude
                        jObject.getString("place"),//“place” for location
                        jObject.getLong("time"),//“time” for time
                        jObject.getString("url"));//"url" for link
                earthquakes.add(temp);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private static String makeHttpRequst(URL url) throws IOException {
        //if url is null return
        if (url == null) return null;
        String JSON_RESPONSE = "";//this is the response for making JSON object
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JSON_RESPONSE = readFromStreem(inputStream);

                return JSON_RESPONSE;
            } else {
                Log.e(LOG_TAG, "Connection Error");
            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Connection Error " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return JSON_RESPONSE;

    }

    private static String readFromStreem(InputStream inputStream) throws IOException {
        StringBuilder outbut = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                outbut.append(line);
                line = reader.readLine();
            }
        }

        return outbut.toString();
    }


}