package com.leonrjg.treball;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.design.widget.BottomNavigationView;
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
import com.leonrjg.treball.models.CategoryModel;
import com.leonrjg.treball.models.CityModel;
import com.leonrjg.treball.models.JobModel;
import com.leonrjg.treball.utils.DataHolder;
import com.leonrjg.treball.utils.remoteJson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private final String API_URL = DataHolder.API_BASE_URL;
    private ListView lvJobs;
    private ProgressDialog dialog;
    private Gson gson = new Gson();
    private remoteJson remJson = new remoteJson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(R.drawable.treball);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getText(R.string.loading_offers));
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);

        lvJobs = (ListView)findViewById(R.id.lvJobs);
        new JSONTask().execute();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
          new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_listing:
                        new JSONTask().execute();
                        return true;
                    case R.id.action_categories:
                        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_city:
                        Intent intentCat = new Intent(MainActivity.this, CityActivity.class);
                        startActivity(intentCat);
                        return true;
                }
                return true;
            }
        });
    }

    public class JSONTask extends AsyncTask<String,String, List<JobModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<JobModel> doInBackground(String... params) {
            DataHolder dh = DataHolder.getInstance();
            String jobsUrl = dh.getJobsUrl();
            dh.setSearch(null);
            String jobsJson = remJson.get(jobsUrl);
            if (jobsJson == null) {
                return null;
            }
            List<JobModel> JobsList = gson.fromJson(jobsJson, new TypeToken<List<JobModel>>(){}.getType());
            return JobsList;
        }

        @Override
        protected void onPostExecute(final List<JobModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                JobAdapter adapter = new JobAdapter(getApplicationContext(), R.layout.row, result);
                lvJobs.setAdapter(adapter);
                lvJobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JobModel jobModel = result.get(position);
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("jobModel", new Gson().toJson(jobModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getText(R.string.api_unavailable), Toast.LENGTH_SHORT).show();
            }
        }
    }



    public class JobAdapter extends ArrayAdapter{

        private List<JobModel> jobModelList;
        private int resource;
        private LayoutInflater inflater;
        public JobAdapter(Context context, int resource, List<JobModel> objects) {
            super(context, resource, objects);
            jobModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivJobIcon = convertView.findViewById(R.id.ivIcon);
                holder.tvJob = convertView.findViewById(R.id.tvJob);
                holder.tvCompany = convertView.findViewById(R.id.tvCompany);
                holder.tvLocation = convertView.findViewById(R.id.tvLocation);
                holder.tvDate = convertView.findViewById(R.id.tvDate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Typeface font = Typeface.createFromAsset(getAssets(), "renner.ttf");
            holder.tvJob.setTypeface(font);

            final ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().displayImage(jobModelList.get(position).getImage(), holder.ivJobIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    finalHolder.ivJobIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivJobIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivJobIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivJobIcon.setVisibility(View.INVISIBLE);
                }
            });

            holder.tvJob.setText(jobModelList.get(position).getTitle());
            holder.tvCompany.setText(jobModelList.get(position).getCompany());
            holder.tvLocation.setText("Lugar: " + jobModelList.get(position).getAddress());
            holder.tvDate.setText("Publicación: " + jobModelList.get(position).getPostDate());
            return convertView;
        }


        class ViewHolder{
            private ImageView ivJobIcon;
            private TextView tvJob;
            private TextView tvCompany;
            private TextView tvLocation;
            private TextView tvDate;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getText(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DataHolder.getInstance().setSearch(query);
                new JSONTask().execute();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return true; }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            DataHolder dh = DataHolder.getInstance();
            dh.setCity(null);
            dh.setCategoryId(null);
            dh.setSearch(null);
            new JSONTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
