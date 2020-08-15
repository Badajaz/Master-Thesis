package com.example.android.boardshot;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private List<Integer> levels;

    public User(String name, List<Integer> levels) {
        this.name = name;
        this.levels = levels;
        for (int i =0;i< 12;i++ ){
            levels.add(0);
        }
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
