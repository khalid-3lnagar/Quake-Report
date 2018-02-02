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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    public static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        //start loader in the background

        LoaderManager Lmanager = getLoaderManager();
        Lmanager.initLoader(0, null, this).forceLoad();

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        onClickOpenLink(earthquakeListView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new Adapter(
                EarthquakeActivity.this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);


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


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new ErthAsyncTaskLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        //clear the adapter of previous earthquake data
        mAdapter.clear();
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
        ListView list = findViewById(R.id.list);
        //show text says No earthquakes found if the data the list was empty
        list.setEmptyView(EmptyViewcreator());


    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        mAdapter.clear();
    }

    public View EmptyViewcreator() {
        //this text shown if the list is empty
        TextView tmpText = this.findViewById(R.id.emptyView);
        tmpText.setText("No earthquakes found");
        return tmpText;
    }

    private static class ErthAsyncTaskLoader extends AsyncTaskLoader<List<Earthquake>> {
        private String REQUEST_URL;

        public ErthAsyncTaskLoader(Context context, String url) {

            super(context);
            REQUEST_URL = url;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<Earthquake> loadInBackground() {
            //get list of earthquake obj from the REQUEST_URL
            List<Earthquake> earthquakes = QueryUtils.extractEarthquakes(REQUEST_URL);

            return earthquakes;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



