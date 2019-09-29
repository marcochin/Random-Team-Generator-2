package com.marcochin.teamrandomizer.ui.randomize;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomizeViewModel extends ViewModel {
    private int mNumberOfTeams;
    private List<Player> mPlayerList;

    private MutableLiveData<List<Team>> mTeamListLiveData;

    public RandomizeViewModel() {
        mTeamListLiveData = new MutableLiveData<>();
    }

    void setRandomizeParams(List<Player> playerList, int numberOfTeams) {
        mNumberOfTeams = numberOfTeams;
        mPlayerList = playerList;
    }

    void randomize() {
        if (mPlayerList != null) {
            // Shuffle list into random order
            Collections.shuffle(mPlayerList);

            List<Team> teamList = mTeamListLiveData.getValue();

            // If teamList doesn't exist yet create the team objects
            if(teamList == null){
                teamList = new ArrayList<>();
                for (int i = 0; i < mNumberOfTeams; i++) {
                    teamList.add(new Team(i + 1));
                }
            }

            // Clear all the names from the teams
            for(int i = 0; i < teamList.size(); i++){
                teamList.get(i).clearPlayers();
            }

            // Split players into their teams
            for (int i = 0; i < mPlayerList.size(); i++) {
                int teamNumber = i % teamList.size();
                Team team = teamList.get(teamNumber);
                team.addPlayerName(mPlayerList.get(i).getName());
            }

            mTeamListLiveData.setValue(teamList);
        }
    }


    // LiveData
    LiveData<List<Team>> getTeamListLiveData() {
        return mTeamListLiveData;
    }
}
