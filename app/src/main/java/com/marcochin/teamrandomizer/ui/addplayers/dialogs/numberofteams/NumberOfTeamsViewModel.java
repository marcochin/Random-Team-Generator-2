package com.marcochin.teamrandomizer.ui.addplayers.dialogs.numberofteams;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NumberOfTeamsViewModel extends ViewModel {
    public static final String TAG = NumberOfTeamsViewModel.class.getSimpleName();

    public static final String MSG_TOO_FEW_TEAMS = "You must have at least 2 teams";
    public static final String MSG_TOO_MANY_TEAMS = "The number of teams cannot exceed the number of players";
    public static final String MSG_INVALID_NUMBER = "Please enter a valid number";

    private int mNumberOfPlayers;
    private MutableLiveData<NumberOfTeamsAction<Integer>> mActionLiveData;

    public NumberOfTeamsViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    void validateNumberOfTeams(String numberOfTeams) {
        try{
            int numTeams = Integer.parseInt(numberOfTeams);

            if(numTeams < 2){
                showMessage(MSG_TOO_FEW_TEAMS);

            }else if(numTeams > mNumberOfPlayers){
                showMessage(MSG_TOO_MANY_TEAMS);
            }

        }catch (NumberFormatException e){
            Log.e(TAG, Log.getStackTraceString(e));
            showMessage(MSG_INVALID_NUMBER);
        }
    }

    void setNumberOfPlayers(int numberOfPlayers){
        mNumberOfPlayers = numberOfPlayers;
    }


    // LiveData
    LiveData<NumberOfTeamsAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }

    private void showMessage(String message){
        mActionLiveData.setValue(NumberOfTeamsAction.showMessage((Integer)null, message));
    }
}
