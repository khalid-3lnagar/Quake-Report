/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        new ErthAsyncTask().execute(USGS_REQUEST_URL);
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        onClickOpenLink(earthquakeListView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new Adapter(
                EarthquakeActivity.this, new ArrayList<card>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);


    }

    private void onClickOpenLink(ListView list) {


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                card card = (com.example.android.quakereport.card) parent.getItemAtPosition(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(card.getUrl()));
                startActivity(i);
            }
        });
    }

    private class ErthAsyncTask extends AsyncTask<String, Void, List<card>> {
        @Override
        protected List<card> doInBackground(String... strings) {


            // Don't perform the request if there are no URLs, or the first URL is null.
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            URL jason = createUrl(strings[0]);
            // Create an empty ArrayList that we can start adding earthquakes to
            ArrayList<card> earthquakes;
            String JSON_RESPONSE = null;
            try {
                JSON_RESPONSE = makeHttpRequst(jason);
            } catch (IOException e) {
                e.printStackTrace();
            }
            earthquakes = QueryUtils.extractEarthquakes(JSON_RESPONSE);

            return earthquakes
                    ;
        }

        @Override
        protected void onPostExecute(List<card> data) {
//clear the adapter of previous earthquake data
            mAdapter.clear();

            mAdapter.addAll(data);
        }


        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequst(URL url) throws IOException {
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

        private String readFromStreem(InputStream inputStream) throws IOException {
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


}



