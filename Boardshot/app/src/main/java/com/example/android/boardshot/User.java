package com.example.android.boardshot;

public class User {

    private String name;
    private int points;
    private int levels;


    public User() {

    }

    public User(String name, int points, int levels) {
        this.name = name;
        this.points = points;
        this.levels = levels;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }
}
