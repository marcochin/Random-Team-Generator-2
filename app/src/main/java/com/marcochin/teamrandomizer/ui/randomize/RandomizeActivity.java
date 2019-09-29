package com.marcochin.teamrandomizer.ui.randomize;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.model.Team;
import com.marcochin.teamrandomizer.ui.randomize.adapters.RandomizeListAdapter;

import java.util.List;

public class RandomizeActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_PLAYER_LIST = "player_list";
    public static final String BUNDLE_KEY_NUMBER_OF_TEAMS = "number_of_teams";

    private RandomizeViewModel mViewModel;
    private RandomizeListAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);

        RecyclerView recyclerView = findViewById(R.id.ar_recycler_view);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(RandomizeViewModel.class);
        observeLiveData();

        setupArguments(); // Make this the first setup as other setups might depend on it
        setupRecyclerView(recyclerView);

        mViewModel.randomize();
    }

    private void setupArguments(){
        List<Player> playerList = getIntent().getParcelableArrayListExtra(BUNDLE_KEY_PLAYER_LIST);
        int numberOfTeams = getIntent().getIntExtra(BUNDLE_KEY_NUMBER_OF_TEAMS, 2);

        mViewModel.setRandomizeParams(playerList, numberOfTeams);
    }

    private void setupRecyclerView(RecyclerView recyclerView){
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(null); // TODO

        // Set adapter
        mListAdapter = new RandomizeListAdapter(this, null);
        recyclerView.setAdapter(mListAdapter);

        // Set LayoutManager
        int columnCount = getResources().getInteger(R.integer.ar_grid_column_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void observeLiveData() {
        mViewModel.getTeamListLiveData().observe(this, new Observer<List<Team>>() {
            @Override
            public void onChanged(List<Team> teamList) {
                mListAdapter.setList(teamList);
                mListAdapter.notifyDataSetChanged();
                // TODO maybe use notifyRangeCHanged if it blinks
            }
        });
    }
}
