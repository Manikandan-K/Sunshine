package com.example.sunshine.app;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sunshine.app.data.WeatherContract;

public class ForecastFragment extends Fragment {

    ForecastAdapter mForecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("ForecastFragment", "inside on options select" + item.getItemId());
        if (item.getItemId() == R.id.action_refresh) {
            Log.d("ForecastFragment", "clicked refresh");
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

//        updateWeather();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, weatherDataAdapter.getItem(i));
//                startActivity(intent);
//            }
//        });
        listView.setAdapter(mForecastAdapter);
        return rootView;
    }

    private void updateWeather() {
        String location = getPreferenceValue(R.string.pref_location_key, R.string.pref_location_default_value);
        String unit = getPreferenceValue(R.string.pref_unit_key, R.string.pref_unit_default);
        new FetchWeatherTask(getActivity()).execute(location, unit);
    }

    private String getPreferenceValue(int keyValue, int defaultValueKey) {
        String key = getString(keyValue);
        String defaultValue = getString(defaultValueKey);
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(key, defaultValue);
    }

}

