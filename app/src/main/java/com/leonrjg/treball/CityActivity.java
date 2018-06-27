package com.leonrjg.treball;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leonrjg.treball.models.CityModel;
import com.leonrjg.treball.models.JobModel;
import com.leonrjg.treball.utils.remoteJson;

import java.util.ArrayList;
import java.util.List;

import com.leonrjg.treball.utils.DataHolder;

public class CityActivity extends ActionBarActivity {

    private String API_URL = DataHolder.API_BASE_URL + "cities/";
    private ListView lvCities;
    private ProgressDialog dialog;
    private Gson gson = new Gson();
    private remoteJson remJson = new remoteJson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getText(R.string.loading_cities));

        lvCities = (ListView)findViewById(R.id.lvCities);
        new JSONTask().execute(API_URL);
    }

    public class JSONTask extends AsyncTask<String,String, List<CityModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<CityModel> doInBackground(String... params) {
            String citiesJson = remJson.get(params[0]);
            if (citiesJson == null) {
                return null;
            }
            List<CityModel> CitiesList = gson.fromJson(citiesJson, new TypeToken<List<CityModel>>(){}.getType());
            return CitiesList;
        }

        @Override
        protected void onPostExecute(final List<CityModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                ArrayList cities = new ArrayList();
                for(CityModel city : result) {
                    cities.add(city.getInfoItem());
                }
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cities);
                lvCities.setAdapter(adapter);
                lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CityModel cityModel = result.get(position);
                        DataHolder.getInstance().setCity(cityModel.city);
                        Intent intent = new Intent(CityActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getText(R.string.api_unavailable), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new JSONTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
