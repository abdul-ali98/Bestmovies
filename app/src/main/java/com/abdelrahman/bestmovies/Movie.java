package com.abdelrahman.bestmovies;

/**
 * Created by abdalrahman on 3/5/2018.
 */

public class Movie {
    private String photoPath; //poster_path
    private String title; //original_title
    private String overview; //overview
    private String relaseDate; //release_date
    private String userRate; //vote_average

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelaseDate() {
        return relaseDate;
    }

    public void setRelaseDate(String relaseDate) {
        this.relaseDate = relaseDate;
    }

    public String getUserRate() {
        return userRate;
    }

    public void setUserRate(String userRate) {
        this.userRate = userRate;
    }

}
