package com.abdelrahman.bestmovies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private static final int DEFAULT_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        int position = intent.getIntExtra(getString(R.string.position), DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            finish();
            return;
        }
        Movie movie = JsonUtils.parseMovieJson(MainActivity.moviesList.get(position));
        try {
            ActionBar actionBar = this.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(movie.getTitle());
            }
        }catch (Exception e){
            Toast.makeText(this, "try and catch error", Toast.LENGTH_SHORT).show();
        }

        populateUI(movie);

    }

    private void populateUI(Movie movie) {
        ImageView imageView = (ImageView) findViewById(R.id.image_detail);
        Picasso.with(this).load(movie.getPhotoPath()).into(imageView);

        TextView overview = (TextView) findViewById(R.id.overview_id);
        TextView rate = (TextView) findViewById(R.id.rate_id);
        TextView date = (TextView) findViewById(R.id.date_id);

        overview.setText(movie.getOverview());
        rate.setText(movie.getUserRate()+"/10");
        date.setText(movie.getRelaseDate());

    }
}
