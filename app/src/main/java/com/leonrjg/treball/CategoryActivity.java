package com.leonrjg.treball;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leonrjg.treball.models.CategoryModel;
import com.leonrjg.treball.utils.DataHolder;
import com.leonrjg.treball.utils.remoteJson;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends ActionBarActivity {

    private String API_URL = DataHolder.API_BASE_URL + "categories/";
    private ListView lvCategories;
    private ProgressDialog dialog;
    private Gson gson = new Gson();
    private remoteJson remJson = new remoteJson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getText(R.string.loading_categories));

        lvCategories = (ListView)findViewById(R.id.lvCategories);
        new JSONTask().execute(API_URL);
    }

    public class JSONTask extends AsyncTask<String,String, List<CategoryModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<CategoryModel> doInBackground(String... params) {
            String categoriesJson = remJson.get(params[0]);
            if (categoriesJson == null) {
                return null;
            }
            List<CategoryModel> CategoriesList = gson.fromJson(categoriesJson, new TypeToken<List<CategoryModel>>(){}.getType());
            return CategoriesList;
        }

        @Override
        protected void onPostExecute(final List<CategoryModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                ArrayList categories = new ArrayList();
                for(CategoryModel category : result) {
                    categories.add(category.getInfoItem());
                }
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
                lvCategories.setAdapter(adapter);
                lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CategoryModel categoryModel = result.get(position);
                        DataHolder.getInstance().setCategoryId(categoryModel.id);
                        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
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
