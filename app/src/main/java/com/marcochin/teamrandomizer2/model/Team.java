package com.marcochin.teamrandomizer2.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int teamNumber;
    private List<String> playerNames;

    public Team(int teamNumber) {
        this.teamNumber = teamNumber;
        playerNames = new ArrayList<>();
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public void addPlayerName(String name){
        playerNames.add(name);
    }

    public void clearPlayers(){
        playerNames.clear();
    }
}
