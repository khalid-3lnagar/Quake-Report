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

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
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

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new ErthAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        //clear the adapter of previous earthquake data
        mAdapter.clear();
        mAdapter.addAll(data);


    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

    }

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        onClickOpenLink(earthquakeListView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new Adapter(
                EarthquakeActivity.this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this).forceLoad();

    }

    private void onClickOpenLink(ListView list) {


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = (Earthquake) parent.getItemAtPosition(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(earthquake.getUrl()));
                startActivity(i);
            }
        });
    }

    private static class ErthAsyncTaskLoader extends AsyncTaskLoader<List<Earthquake>> {

        public ErthAsyncTaskLoader(Context context) {
            super(context);
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<Earthquake> loadInBackground() {

            //create url
            URL jason = createUrl(USGS_REQUEST_URL);
            // Create an empty ArrayList that we can start adding earthquakes to
            ArrayList<Earthquake> earthquakes;

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



