package com.abdelrahman.bestmovies;

/**
 * Created by abdalrahman on 4/8/2018.
 */

public class Review {
    private  String name;
    private  String description;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {this.description = description;}
    public String toString(){return "{ name: " + this.name + " , descriptio: " + this.description + " }";}
}
