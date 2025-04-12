package com.example.lutbattle.util;
import com.example.lutbattle.model.LUTemon;

import java.util.ArrayList;


public class GameManager {
    private static GameManager instance;
    private ArrayList<LUTemon> lutemons;
    private ArrayList<LUTemon> trainers;

    private GameManager() {
        lutemons = new ArrayList<>();
        trainers = new ArrayList<>();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void addLUTemon(LUTemon lutemon) {
        if (trainers.contains(lutemon)) {
            trainers.remove(lutemon);
        }
        lutemons.add(lutemon);
    }

    public void addTrainer(LUTemon trainer) {
        trainers.add(trainer);
    }
    public void removeTrainer(LUTemon trainer) {
        trainers.remove(trainer);
    }

    public ArrayList<LUTemon> getLUTemons() {
        return lutemons;
    }

    public ArrayList<LUTemon> getTrainers() {
        return trainers;
    }

}
