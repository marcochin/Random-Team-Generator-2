package com.marcochin.teamrandomizer.ui.addplayers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.PlayerListAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment {

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private TextView mGroupNameText;
    private EditText mNameEditText;

    private RecyclerView mRecyclerView;
    private PlayerListAdapter mListAdapter;
    private AddPlayersViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGroupNameText = view.findViewById(R.id.group_name_text);
        mNameEditText = view.findViewById(R.id.name_edit_text);
        Button addButton = view.findViewById(R.id.add_btn);

        addButton.setOnClickListener(mAddButtonClickListener);

        // Setup recyclerView
        mRecyclerView = view.findViewById(R.id.players_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = new PlayerListAdapter();
        mListAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mListAdapter);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);

        observeLiveData();
    }

    private void observeLiveData() {
        mViewModel.getGroupNameLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mGroupNameText.setText(s);
            }
        });

        mViewModel.getPlayerListLiveData().observe(this, new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> players) {
                mListAdapter.submitList(players);
            }
        });

        mViewModel.getUserActionLiveData().observe(this, new Observer<AddPlayersViewModel.UserAction>() {
            @Override
            public void onChanged(AddPlayersViewModel.UserAction userAction) {
                switch(userAction){
                    case ADD_PLAYER:
                        handleAddPlayerAction();
                        mViewModel.clearUserActionLiveData();
                        break;
                }
            }
        });
    }

    private void handleAddPlayerAction(){
        List<Player> playerList = mViewModel.getPlayerListLiveData().getValue();
        if(playerList != null && playerList.size() > 0) {
            mRecyclerView.smoothScrollToPosition(playerList.size() - 1);
        }
        mNameEditText.setText("");
    }

    private View.OnClickListener mAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mViewModel.addPlayer(new Player(mNameEditText.getText().toString()));
        }
    };

    private PlayerListAdapter.OnItemClickListener mOnItemClickListener = new PlayerListAdapter.OnItemClickListener() {
        @Override
        public void onCheckboxClick(int position, Player player) {

        }

        @Override
        public void onDeleteClick(int position, Player player) {
            mViewModel.deletePlayer(player);
        }
    };
}
