package com.marcochin.teamrandomizer.ui.randomize;

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
    private static final String TAG = RandomizeViewModel.class.getSimpleName();

    private int mNumberOfTeams;
    private List<Player> mPlayerList;

    private MutableLiveData<List<Team>> mTeamListLiveData;
    private MutableLiveData<UIAction<Integer>> mActionLiveData;

    public RandomizeViewModel() {
        mTeamListLiveData = new MutableLiveData<>();
        mActionLiveData = new MutableLiveData<>();
    }

    void setRandomizeParams(List<Player> playerList, int numberOfTeams) {
        mNumberOfTeams = numberOfTeams;
        mPlayerList = playerList;
    }

    void randomize() {
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

    LiveData<UIAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }
}
