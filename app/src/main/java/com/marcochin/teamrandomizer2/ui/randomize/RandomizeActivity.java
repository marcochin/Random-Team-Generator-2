package com.marcochin.teamrandomizer2.ui.randomize;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.marcochin.teamrandomizer2.R;
import com.marcochin.teamrandomizer2.model.Player;
import com.marcochin.teamrandomizer2.model.Team;
import com.marcochin.teamrandomizer2.ui.UIAction;
import com.marcochin.teamrandomizer2.ui.randomize.adapters.RandomizeListAdapter;

import java.util.List;

public class RandomizeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String BUNDLE_KEY_PLAYER_LIST = "player_list";
    public static final String BUNDLE_KEY_NUMBER_OF_TEAMS = "number_of_teams";

    private RandomizeViewModel mViewModel;
    private RandomizeListAdapter mListAdapter;

    private Button mRandomizeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);

        RecyclerView recyclerView = findViewById(R.id.ar_recycler_view);
        ImageButton backButton = findViewById(R.id.ar_back_btn);
        ImageButton shareButton = findViewById(R.id.ar_share_btn);
        ViewGroup copyButton = findViewById(R.id.ar_copy_btn);
        mRandomizeButton = findViewById(R.id.ar_randomize_again_btn);

        backButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        copyButton.setOnClickListener(this);
        mRandomizeButton.setOnClickListener(this);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(RandomizeViewModel.class);
        observeLiveData();

        setupArguments(); // Make this the first setup as other setups might depend on it
        setupRecyclerView(recyclerView);

        mViewModel.randomize();
    }

    private void setupArguments() {
        List<Player> playerList = getIntent().getParcelableArrayListExtra(BUNDLE_KEY_PLAYER_LIST);
        int numberOfTeams = getIntent().getIntExtra(BUNDLE_KEY_NUMBER_OF_TEAMS, 2);

        mViewModel.setRandomizeParams(playerList, numberOfTeams);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

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
            }
        });

        mViewModel.getActionLiveData().observe(this, new Observer<UIAction<Integer>>() {
            @Override
            public void onChanged(UIAction<Integer> uiAction) {
                switch (uiAction.action){
                    case RandomizeAction.CHANGE_RANDOMIZE_BUTTON_VISIBILITY:
                        handleChangeRandomizeButtonVisibilityAction(uiAction);
                        break;

                    case RandomizeAction.SHOW_MSG:
                        handleShowMessageAction(uiAction);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ar_randomize_again_btn:
                mViewModel.randomize();
                break;

            case R.id.ar_back_btn:
                onBackPressed();
                break;

            case R.id.ar_copy_btn:
                mViewModel.copyTeamsToClipboard((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
                break;

            case R.id.ar_share_btn:
                Intent shareIntent = mViewModel.getShareIntent();
                if(shareIntent != null) {
                    startActivity(Intent.createChooser(shareIntent, null));
                }
                break;
        }
    }

    private void handleChangeRandomizeButtonVisibilityAction(UIAction<Integer> uiAction) {
        if (uiAction.data != null && uiAction.data == View.VISIBLE) {
            mRandomizeButton.setVisibility(View.VISIBLE);
        }else{
            mRandomizeButton.setVisibility(View.INVISIBLE);
        }
    }

    private void handleShowMessageAction(UIAction<Integer> uiAction){
        if(uiAction.message != null) {
            Snackbar.make(mRandomizeButton, uiAction.message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
