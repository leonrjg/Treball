package com.leonrjg.treball;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonrjg.treball.models.JobModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DetailActivity extends ActionBarActivity {

    private ImageView ivJobIcon;
    private TextView tvJob;
    private TextView tvCompany;
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvDescription;
    private Button goOffer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUpUIViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String json = bundle.getString("jobModel");
            final JobModel jobModel = new Gson().fromJson(json, JobModel.class);

            ImageLoader.getInstance().displayImage(jobModel.getImage(), ivJobIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            tvJob.setText(jobModel.getTitle());
            Typeface font = Typeface.createFromAsset(getAssets(), "renner.ttf");
            tvJob.setTypeface(font);

            tvCompany.setText(jobModel.getCompany());
            tvLocation.setText("Lugar: " + jobModel.getAddress());
            tvDate.setText("Publicación: " + jobModel.getPostDate());
            tvDescription.setText(jobModel.getDescription());

            goOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobModel.getLink()));
             startActivity(intent);
            }
        });

        }

    }

    private void setUpUIViews() {
        ivJobIcon = (ImageView)findViewById(R.id.ivIcon);
        tvJob = (TextView)findViewById(R.id.tvJob);
        tvCompany = (TextView)findViewById(R.id.tvCompany);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvLocation = (TextView)findViewById(R.id.tvLocation);
        tvDescription = (TextView)findViewById(R.id.tvDescription);
        goOffer = (Button)findViewById(R.id.goOffer);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
