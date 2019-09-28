package com.marcochin.teamrandomizer.ui.randomize;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Player;

import java.util.List;

import dagger.android.support.DaggerAppCompatActivity;

public class RandomizeActivity extends DaggerAppCompatActivity {
    public static final String BUNDLE_KEY_PLAYER_LIST = "player_list";
    public static final String BUNDLE_KEY_NUMBER_OF_TEAMS = "number_of_teams";

    private RandomizeViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(RandomizeViewModel.class);
        observeLiveData();

        setupArguments();
    }

    private void setupArguments(){
        List<Player> playerList = getIntent().getParcelableArrayListExtra(BUNDLE_KEY_PLAYER_LIST);
        int numberOfTeams = getIntent().getIntExtra(BUNDLE_KEY_NUMBER_OF_TEAMS, 2);

        mViewModel.setRandomizeParams(playerList, numberOfTeams);
    }

    private void observeLiveData() {

    }
}
