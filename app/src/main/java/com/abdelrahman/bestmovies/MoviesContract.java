package com.abdelrahman.bestmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abdalrahman on 3/29/2018.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.abdelrahman.bestmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";


    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();


        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_RATE = "movieRate";
        public static final String COLUMN_MOVIE_RELASE_DATE = "relaseDate";
        public static final String COLUMN_MOVIE_DESCRIPTION = "movieDescription";
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_POSTER = "posterPath";


        public static Uri buildMovieUriWithID(long ID) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(ID))
                    .build();
        }
    }
}
