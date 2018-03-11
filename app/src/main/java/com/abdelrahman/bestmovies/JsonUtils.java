package com.abdelrahman.bestmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abdalrahman on 3/5/2018.
 */

public class JsonUtils {

    private static final String POSTER_PATH ="poster_path";
    private static final String ORIGINAL_TITLE= "original_title";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE ="release_date";
    private static final String VOTE_AVERAGE ="vote_average";
    public static Movie parseMovieJson(String json){

        Movie movie = new Movie();
        try {

            JSONObject jsonObject = new JSONObject(json);
            movie.setOverview(jsonObject.getString(OVERVIEW));
            movie.setPhotoPath("http://image.tmdb.org/t/p/w342"+jsonObject.getString(POSTER_PATH));
            movie.setRelaseDate(jsonObject.getString(RELEASE_DATE));
            movie.setTitle(jsonObject.getString(ORIGINAL_TITLE));
            movie.setUserRate(jsonObject.getString(VOTE_AVERAGE));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public static String photoUrl(String json){

        try {
            JSONObject jsonObject = new JSONObject(json);
            return "http://image.tmdb.org/t/p/w185" +jsonObject.getString(POSTER_PATH);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Error";
    }
}
