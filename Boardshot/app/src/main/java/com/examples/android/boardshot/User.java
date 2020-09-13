package com.examples.android.boardshot;

import java.util.ArrayList;

public class User {

    private String name;
    private ArrayList<Integer> levels;
    private ArrayList<Boolean> chegouFim;

    public User(String name, ArrayList<Integer> levels,ArrayList<Boolean> chegouFim) {
        this.name = name;
        this.levels = levels;
        for (int i =0;i< 12;i++ ){
            levels.add(0);
        }

        this.chegouFim = chegouFim;
        for (int i =0;i< 12;i++ ){
            chegouFim.add(false);
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

    public ArrayList<Integer> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Integer> levels) {
        this.levels = levels;
    }

    public ArrayList<Boolean> getChegouFim() {
        return chegouFim;
    }

    public void setChegouFim(ArrayList<Boolean> chegouFim) {
        this.chegouFim = chegouFim;
    }
}
