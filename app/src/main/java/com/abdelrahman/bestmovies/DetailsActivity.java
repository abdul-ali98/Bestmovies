package com.abdelrahman.bestmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        TrailsAdapter.ListItemClickListener{

    public static final int DEFAULT_POSITION = -1;
    private final int BTN_OFF =17301515; // the star is off
    private final int BTN_ON =17301516;  // the star is ON
    private ImageView imageView;
    private List<Movie> myList;
    private TextView overview;
    private TextView rate;
    private ImageButton imageButton;
    private TextView date;
    private Movie movie;
    private RecyclerView reviewsRecyclerView;
    private RecyclerView trailsRecyclerView;
    private static final int DOWNLOAD_ID =2;
    private static final int DOWNLOAD_ID_TRAILS =10;
    private int position ;
    private String titlePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

            Intent intent = getIntent();
            if (intent == null) {
                finish();
            }

             position = intent.getIntExtra(getString(R.string.position), DEFAULT_POSITION);
             titlePref = intent.getStringExtra(MainActivity.PREE_ID);


        initUI();


        if (position == DEFAULT_POSITION && (titlePref.equals("")|| titlePref==null)) {
            finish();
            return;
        }
        if(!isOnline() || getString(R.string.fav_key).equals(
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString(getString(R.string.pref_sorting),""))){
            Log.i("Details", " error in movie");
            movie = populatOfflineMode(titlePref);

            // make the Reviews Invisible as the user is offline
            findViewById(R.id.reviews_text).setVisibility(View.GONE);

            return;
        }
        else {
            movie = JsonUtils.parseMovieJson(MainActivity.moviesList.get(position));
            Log.i("Details",movie.getOverview());
            changeTitle(movie.getTitle());
        }
        populateUI(movie,true);

        addTrails();
    }


    private void changeTitle(String theTitle) {
        try {
            ActionBar actionBar = this.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(theTitle);
            }
        } catch (Exception e) {
            Toast.makeText(this, "try and catch error", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI(){

        imageView           = (ImageView)    findViewById(R.id.image_detail);
        overview            = (TextView)     findViewById(R.id.overview_id);
        rate                = (TextView)     findViewById(R.id.rate_id);
        date                = (TextView)     findViewById(R.id.date_id);
        imageButton         = (ImageButton)  findViewById(R.id.fav_button);
        reviewsRecyclerView = (RecyclerView) findViewById(R.id.reviews_id);
        trailsRecyclerView  = (RecyclerView) findViewById(R.id.trails_id);
    }
    private void populateUI(Movie movie,boolean isOnline) {

        if(isOnline || isOnline()) {

            Picasso.with(this).load(movie.getPhotoPath()).into(imageView);
            findViewById(R.id.reviews_text).setVisibility(View.VISIBLE);
            findViewById(R.id.reviews_text).setVisibility(View.VISIBLE);
            populatReviews(movie);
        }else{
            Picasso.with(this).load(R.drawable.fff).into(imageView);
        }
        overview.setText(movie.getOverview());
        rate.setText(movie.getUserRate()+"/10");
        date.setText(movie.getRelaseDate());

        if(isOnline) {
            if (check()) {
                imageButton.setImageResource(BTN_ON);
                populatReviews(movie);
                return;
            }

            populatReviews(movie);
            imageButton.setImageResource(BTN_OFF);
        } else{
            imageButton.setImageResource(BTN_ON);
        }

        Log.i("DetailsActivity","populate UI");

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private void populatReviews(Movie movie) {


        if(isOnline()) {
            Bundle bundle = new Bundle();
            bundle.putString("URL", "https://api.themoviedb.org/3/movie/"
                    + movie.getMovieID()+
                    "/reviews?&api_key=b19a8c83b2cf3a0f54989ccfb8d280a2");
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> moviesSearchLoader = loaderManager.getLoader(DOWNLOAD_ID);
            if (moviesSearchLoader == null) {
                loaderManager.initLoader(DOWNLOAD_ID, bundle, this);
            } else {
                loaderManager.restartLoader(DOWNLOAD_ID, bundle, this);
            }
        }else{
            Toast.makeText(this, "you aren't online", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Review> fetchReviews(String resultJson) {

        List<Review> reviewsList = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray result = new JSONArray(jsonObject.getString("results"));

            for(int i=0 ; i < result.length() ; i++){

                JSONObject jsonPart = result.getJSONObject(i);
                Review review = new Review();
                review.setDescription(jsonPart.getString("content"));
                review.setName(jsonPart.getString("author"));
                reviewsList.add(review);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(reviewsList.size() < 1 ){
            ((TextView)findViewById(R.id.reviews_text)).setText("No reviews available");
        }
        return reviewsList;
    }


    synchronized private boolean check(){

        Cursor cursor = null;
        myList = new ArrayList<Movie>();

        try {
            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
            String[] projection = new String[]{
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME,
                    MoviesContract.MoviesEntry._ID,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER
            };

            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    sortOrder);

            if (cursor != null) {

                cursor.moveToFirst();
                for (int i = cursor.getCount(); i > 0; i--) {
                    Movie moviesData = new Movie();

                    moviesData.setMovieID(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
                    moviesData.setRelaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE)));
                    moviesData.setUserRate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE)));
                    moviesData.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION)));
                    moviesData.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME)));
                    moviesData.setDatabaseID(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID)));
                    moviesData.setPhotoPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER)));

                    myList.add(moviesData);
                    cursor.moveToNext();
                }

            }
        }catch (Exception e){
            Toast.makeText(this, "error while loading database", Toast.LENGTH_SHORT).show();
        }finally {
            cursor.close();
        }

        boolean isExist = false;
        if(myList.size()>0) {
            for (int i = 0; i < myList.size(); i++) {
                if (myList.get(i).getMovieID() == movie.getMovieID()) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }


    public void favButton(View view){

        if(check()){
            int mRowsDeleted = 0;


       mRowsDeleted  =  getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
               MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
               new String[]{String.valueOf(movie.getMovieID())});
            if(mRowsDeleted >0)
            imageButton.setImageResource(BTN_OFF);
        }
        else{
            ContentValues contentValues = new ContentValues();

            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION,movie.getOverview());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME,movie.getTitle());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,movie.getMovieID());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER,movie.getPhotoPath());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE,movie.getUserRate());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE,movie.getRelaseDate());

            Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI,contentValues);
            imageButton.setImageResource(BTN_ON);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            String resultJson;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }

                if(resultJson == null || resultJson.equals("")){
                    forceLoad();
                    return;
                }
            }

            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString("URL");

                if(searchQueryUrlString== null || searchQueryUrlString.equals("")){
                    return null;
                }
                try {
                    // here is our code to start downloading
                    resultJson="";
                    URL url = new URL(searchQueryUrlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    int data = reader.read();
                    while (data != -1){
                        char current =(char) data;
                        resultJson += current;
                        data = reader.read();
                    }

                    return resultJson;
                }
                catch (IOException e){
                    Toast.makeText(DetailsActivity.this, "Error while loading", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                resultJson= data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        if(loader.getId()== DOWNLOAD_ID ) {
            if (data != null && !(data.equals(""))) {
                reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                ReviewsAdapter moviesAdapter = new ReviewsAdapter(fetchReviews(data));
                reviewsRecyclerView.setHasFixedSize(true);
                reviewsRecyclerView.setAdapter(moviesAdapter);
            }
        }else if(loader.getId() == DOWNLOAD_ID_TRAILS){
            trailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            TrailsAdapter trailsAdapter = new TrailsAdapter(fetchTrails(data), DetailsActivity.this);
            trailsRecyclerView.setHasFixedSize(true);
            trailsRecyclerView.setAdapter(trailsAdapter);
        }
    }

    private List<String> fetchTrails(String data) {

        List <String> trailsList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));

            for(int i=0; i<jsonArray.length();i++){

                JSONObject jsonPart = jsonArray.getJSONObject(i);
                trailsList.add(jsonPart.getString("key"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailsList;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private Movie populatOfflineMode(String searchWithTitle){
        Cursor cursor = null;
        myList = new ArrayList<Movie>();

        try {
            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
            String[] projection = new String[]{
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME,
                    MoviesContract.MoviesEntry._ID,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER
            };

            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    sortOrder);

            if (cursor != null) {

                cursor.moveToFirst();
                for (int i = cursor.getCount(); i > 0; i--) {
                    Movie moviesData = new Movie();

                    moviesData.setMovieID(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
                    moviesData.setRelaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE)));
                    moviesData.setUserRate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE)));
                    moviesData.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION)));
                    moviesData.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME)));
                    moviesData.setDatabaseID(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID)));
                    moviesData.setPhotoPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER)));

                    myList.add(moviesData);
                    cursor.moveToNext();
                }

            }
        }catch (Exception e){
            Toast.makeText(this, "error while loading database", Toast.LENGTH_SHORT).show();
        }finally {
            cursor.close();
        }

        for (int i=0; i<myList.size(); i++ ){
            if(myList.get(i).getTitle().equals(searchWithTitle)) {
                populateUI(myList.get(i), false);
                changeTitle(myList.get(i).getTitle());
                return myList.get(i);
            }
        }
        Log.i("Details","I'm going to return null");
        return null;
    }
    private void addTrails() {
        if(isOnline()) {
            Bundle bundle = new Bundle();
            bundle.putString("URL", "https://api.themoviedb.org/3/movie/"
                    + movie.getMovieID()+
                    "/videos?&api_key=b19a8c83b2cf3a0f54989ccfb8d280a2");
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> moviesSearchLoader = loaderManager.getLoader(DOWNLOAD_ID_TRAILS);
            if (moviesSearchLoader == null) {
                loaderManager.initLoader(DOWNLOAD_ID_TRAILS, bundle, this);
            } else {
                loaderManager.restartLoader(DOWNLOAD_ID_TRAILS, bundle, this);
            }
        }else{
            Toast.makeText(this, "you aren't online", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(String url) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.youtube.com/watch?v="+url));
        startActivity(i);
    }

}
