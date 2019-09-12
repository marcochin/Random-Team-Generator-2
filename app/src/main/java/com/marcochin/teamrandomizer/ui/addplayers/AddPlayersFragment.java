package com.marcochin.teamrandomizer.ui.addplayers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener {

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private TextView mGroupNameText;
    private TextView mNumPlayersText;
    private EditText mNameEditText;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlayerListAdapter mListAdapter;
    private RecyclerView.ItemAnimator mListItemAnimator;
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
        mNumPlayersText = view.findViewById(R.id.total_players_text);
        mRecyclerView = view.findViewById(R.id.players_recycler_view);
        Button addButton = view.findViewById(R.id.add_btn);
        Button clearButton = view.findViewById(R.id.clear_btn);
        ImageButton checkboxButton = view.findViewById(R.id.checkbox_btn);

        addButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        checkboxButton.setOnClickListener(this);

        setupRecyclerView(mRecyclerView);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mListItemAnimator = recyclerView.getItemAnimator();
        recyclerView.setHasFixedSize(true);

        // Set adapter
        mListAdapter = new PlayerListAdapter();
        mListAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(mListAdapter);

        // Set layoutManager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
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
            public void onChanged(final List<Player> players) {
                // We only use this for initial population of the list and clearing the list.
                // I only do it this way because I want granular callbacks for when I've added an item,
                // deleted an item, etc..
                mListAdapter.submitList(players);

            }
        });

        mViewModel.getTotalPlayersLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mNumPlayersText.setText(getString(R.string.ph_total_players, integer.toString()));
            }
        });

        mViewModel.getListActionLiveData().observe(this, new Observer<ListActionResource<Integer>>() {
            @Override
            public void onChanged(ListActionResource<Integer> listActionResource) {
                switch (listActionResource.status) {
                    case PLAYER_ADDED:
                        handlePlayerAddedAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case PLAYER_DELETED:
                        handlePlayerDeletedAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case PLAYER_CHECKBOX_TOGGLED:
                        handlePlayerCheckboxToggledAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case CHECKBOX_BUTTON_TOGGLED:
                        handleCheckboxButtonToggledAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                mViewModel.addPlayer(mNameEditText.getText().toString());
                break;

            case R.id.clear_btn:
                mViewModel.clearAllPlayers();
                break;

            case R.id.checkbox_btn:
                mViewModel.toggleCheckBoxButton();
                break;
        }
    }

    private void handlePlayerAddedAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemInserted(listActionResource.data);

            if (mListAdapter.getItemCount() > 0) {
                mLinearLayoutManager.scrollToPosition(listActionResource.data); // Item pos
            }
            mNameEditText.setText("");
        }
    }

    private void handlePlayerDeletedAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemRemoved(listActionResource.data); // Item pos
        }
    }

    private void handlePlayerCheckboxToggledAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemChanged(listActionResource.data); // Item pos
        }
    }

    private void handleCheckboxButtonToggledAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(null);
            mListAdapter.notifyItemRangeChanged(0, listActionResource.data); // playerListSize
        }
    }


    // Anonymous Inner Classes
    private PlayerListAdapter.OnItemClickListener mOnItemClickListener = new PlayerListAdapter.OnItemClickListener() {
        @Override
        public void onCheckboxClick(int position, Player player) {
            mViewModel.togglePlayerCheckBox(position);
        }

        @Override
        public void onDeleteClick(int position, Player player) {
            mViewModel.deletePlayer(position);
        }
    };
}
