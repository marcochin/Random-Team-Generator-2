package com.marcochin.teamrandomizer.ui.randomize;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.model.Team;
import com.marcochin.teamrandomizer.ui.UIAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RandomizeViewModel extends ViewModel {
    private static final String MSG_RANDOMIZING_IN_PROGRESS = "Randomizing still in progress...";
    private static final String MSG_TEAM_COPIED = "Teams copied to clipboard";
    private static final String CLIPBOARD_LABEL_TEAMS = "Teams";

    private int mNumberOfTeams;
    private List<Player> mPlayerList;

    private MutableLiveData<List<Team>> mTeamListLiveData;
    private MutableLiveData<UIAction<Integer>> mActionLiveData;

    private boolean mRandomizingInProgress;

    public RandomizeViewModel() {
        mTeamListLiveData = new MutableLiveData<>();
        mActionLiveData = new MutableLiveData<>();
    }

    void setRandomizeParams(List<Player> playerList, int numberOfTeams) {
        mNumberOfTeams = numberOfTeams;
        mPlayerList = playerList;
    }

    void randomize() {
        // range is like a for loop
        // concatMap is like flatMap but maintains order. It turns an item emission into an Observable.

        Observable.range(0, 7)
                .concatMap(new Function<Integer, ObservableSource<List<Team>>>() {
                    @Override
                    public ObservableSource<List<Team>> apply(final Integer integer) throws Exception {
                        return Observable.fromCallable(new Callable<List<Team>>() {
                            @Override
                            public List<Team> call() throws Exception {
                                return shufflePlayersIntoTeams();
                            }
                        }).delay(250, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Team>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Hide randomize button
                        mRandomizingInProgress = true;
                        mActionLiveData.setValue(RandomizeAction.changeRandomizeButtonVisiblity(View.INVISIBLE, null));
                    }

                    @Override
                    public void onNext(List<Team> teamList) {
                        mTeamListLiveData.setValue(teamList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mActionLiveData.setValue(RandomizeAction.changeRandomizeButtonVisiblity(View.VISIBLE, null));
                        mRandomizingInProgress = false;
                    }
                });
    }

    private List<Team> shufflePlayersIntoTeams() {
        if (mPlayerList != null) {
            // Shuffle list into random order
            Collections.shuffle(mPlayerList);

            List<Team> teamList = mTeamListLiveData.getValue();

            // If teamList doesn't exist yet create the team objects
            if (teamList == null) {
                teamList = new ArrayList<>();
                for (int i = 0; i < mNumberOfTeams; i++) {
                    teamList.add(new Team(i + 1));
                }
            }

            // Clear all the names from the teams
            for (int i = 0; i < teamList.size(); i++) {
                teamList.get(i).clearPlayers();
            }

            // Split players into their teams
            for (int i = 0; i < mPlayerList.size(); i++) {
                int teamNumber = i % teamList.size();
                Team team = teamList.get(teamNumber);
                team.addPlayerName(mPlayerList.get(i).getName());
            }

            return teamList;
        }
        return null;
    }

    void copyTeamsToClipboard(ClipboardManager clipboardManager) {
        if (mRandomizingInProgress) {
            mActionLiveData.setValue(RandomizeAction.showMessage((Integer) null, MSG_RANDOMIZING_IN_PROGRESS));
            return;
        }

        if (mTeamListLiveData.getValue() != null) {
            // https://stackoverflow.com/a/19253868/5673746
            ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL_TEAMS,
                    teamsToString(mTeamListLiveData.getValue()));
            clipboardManager.setPrimaryClip(clip);

            mActionLiveData.setValue(RandomizeAction.showMessage((Integer) null, MSG_TEAM_COPIED));
        }
    }

    Intent getShareIntent() {
        if (mRandomizingInProgress) {
            mActionLiveData.setValue(RandomizeAction.showMessage((Integer) null, MSG_RANDOMIZING_IN_PROGRESS));
            return null;
        }

        Intent shareIntent = null;
        if(mTeamListLiveData.getValue() != null) {
            shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, teamsToString(mTeamListLiveData.getValue()));
        }

        return shareIntent;
    }

    private String teamsToString(List<Team> teamList) {
        StringBuilder sb = new StringBuilder();

        // O(nm)
        // n = teamList
        // m = playerNamesList
        for (int i = 0; i < teamList.size(); i++) {
            sb.append("Team ");
            sb.append(i + 1);
            sb.append(":\n");

            Team team = teamList.get(i);
            List<String> playerNamesList = team.getPlayerNames();
            for (int j = 0; j < playerNamesList.size(); j++) {
                sb.append(playerNamesList.get(j));

                if (j != playerNamesList.size() - 1) {
                    sb.append(", ");
                }
            }

            if (i != teamList.size() - 1) {
                sb.append("\n\n");
            }
        }

        // String should look like this:
        // Team 1:
        // Marco, Jessica, Lucas, Tyler, Daphne
        //
        // Team2:
        // Susan, Charlie, Maxwell, Carlos, Jackie
        return sb.toString();
    }


    // LiveData
    LiveData<List<Team>> getTeamListLiveData() {
        return mTeamListLiveData;
    }

    LiveData<UIAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }
}
