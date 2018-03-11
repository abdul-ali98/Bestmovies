package com.abdelrahman.bestmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,MyAdapter.ListItemClickListener {

    private String searchBy;
    private static final int DOWNLOAD_ID =2;
    private RecyclerView recyclerview;
    public static List<String> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview= (RecyclerView) findViewById(R.id.recyclerview_main);


        //  http://image.tmdb.org/t/p/w185/deBjt3LT3UQHRXiNX1fu28lAtK6.jpg
       // https://api.themoviedb.org/3/movie/top_rated?api_key=b19a8c83b2cf3a0f54989ccfb8d280a2
        // https://api.themoviedb.org/3/movie/popular?api_key=b19a8c83b2cf3a0f54989ccfb8d280a2

        String myUrl = buildURL();
        Toast.makeText(this, myUrl, Toast.LENGTH_LONG).show();


        if(isOnline()) {
            Bundle bundle = new Bundle();
            bundle.putString("URL", myUrl);
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
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

                if(resultJson == null || resultJson.equals("") ||
                        !(searchBy.equals(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())))){
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
                    Toast.makeText(MainActivity.this, "Error while loading", Toast.LENGTH_SHORT).show();
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
        if ( data != null && !(data.equals(""))){

            moviesList=null;
            moviesList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray result = new JSONArray(jsonObject.getString("results"));
                for(int i=0; i<result.length();i++){
                    JSONObject jsonPart = result.getJSONObject(i);
                    if(!jsonPart.isNull("poster_path")){
                        moviesList.add(jsonPart.toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "error fetching json data", Toast.LENGTH_SHORT).show();
            }

            recyclerview.setLayoutManager(new GridLayoutManager(this,2));
            MyAdapter myAdapter = new MyAdapter(this,this);
            recyclerview.setHasFixedSize(true);
            recyclerview.setAdapter(myAdapter);

            Toast.makeText(this, moviesList.size()+"", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "the data is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this, "the button is clicked", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, DetailsActivity.class);
        i.putExtra(getString(R.string.position),clickedItemIndex);
        startActivity(i);
    }

    private String buildURL(){

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        searchBy = mSharedPreferences.getString(getString(R.string.pref_sorting),getString(R.string.popular));
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(searchBy)
                .appendQueryParameter("api_key", getString(R.string.myMoviesKey));


        return builder.build().toString();
    }
}
