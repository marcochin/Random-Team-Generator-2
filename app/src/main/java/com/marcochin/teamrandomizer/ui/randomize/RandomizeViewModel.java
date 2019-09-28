package com.marcochin.teamrandomizer.ui.randomize;

import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Player;

import java.util.Collections;
import java.util.List;

public class RandomizeViewModel extends ViewModel {
    private List<Player> mPlayerList;
    private int mNumberOfTeams;

    void setRandomizeParams(List<Player> playerList, int numberOfTeams){
        mPlayerList = playerList;
        mNumberOfTeams = numberOfTeams;
    }

    void randomize(){
        if(mPlayerList != null){
            // Shuffle list into random order
            Collections.shuffle(mPlayerList);


        }
    }
}
