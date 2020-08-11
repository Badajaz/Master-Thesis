package com.example.android.boardshot;

public class User {

    private String name;
    private int points;
    private String levels;

    public User(String name, int points, String levels) {
        this.name = name;
        this.points = points;
        this.levels = levels;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }
}
