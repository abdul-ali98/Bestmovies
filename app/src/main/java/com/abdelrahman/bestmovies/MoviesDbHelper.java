package com.abdelrahman.bestmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abdalrahman on 3/29/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                        MoviesContract.MoviesEntry._ID                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_NAME          + " TEXT NOT NULL, "                     +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION   + " TEXT NOT NULL, "                     +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID            + " INTEGER NOT NULL, "                  +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_RATE          + " TEXT NOT NULL, "                     +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER        + " TEXT NOT NULL, "                     +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_RELASE_DATE   + " TEXT NOT NULL " +");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
