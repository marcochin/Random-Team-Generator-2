package com.marcochin.teamrandomizer.ui.randomize;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RandomizeViewModel extends ViewModel {
    private int mNumberOfTeams;
    private List<Player> mPlayerList;

    private MutableLiveData<List<Team>> mTeamListLiveData;

    public RandomizeViewModel(@Named("players_list") List<Player> playerList,
                              @Named("number_of_teams") int numberOfTeams) {

        mPlayerList = playerList;
        mNumberOfTeams = numberOfTeams;
        mTeamListLiveData = new MutableLiveData<>();
    }

    void setRandomizeParams(List<Player> playerList, int numberOfTeams) {
        mNumberOfTeams = numberOfTeams;
        mPlayerList = playerList;
    }

    void randomize() {
        // TODO Might have to show something in the beginning before delay animation
        // or user might see blank screen

        Observable.range(0, 5)
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
                        // Dispose maybe if user goes to onStop
                        // TODO Hide randomize button
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
                        // TODO Show randomize again button
                        // Also show randomize again button when observer is disposed
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


    // LiveData
    LiveData<List<Team>> getTeamListLiveData() {
        return mTeamListLiveData;
    }
}
