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

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener{

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private TextView mGroupNameText;
    private EditText mNameEditText;

    private LinearLayoutManager mLinearLayoutManager;
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
        Button clearButton = view.findViewById(R.id.clear_btn);
        ImageButton checkboxButton = view.findViewById(R.id.checkbox_btn);
        RecyclerView recyclerView = view.findViewById(R.id.players_recycler_view);

        addButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        checkboxButton.setOnClickListener(this);

        setupRecyclerView(recyclerView);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();
    }

    private void setupRecyclerView(RecyclerView recyclerView){
        mListAdapter = new PlayerListAdapter();
        mListAdapter.setOnItemClickListener(mOnItemClickListener);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(mListAdapter);
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
            public void onChanged(List<Player> players) {
                // Don't rely on this update list when there are list actions cause we aren't using
                // this with the use case it was intended for.
                mListAdapter.submitList(players);
            }
        });

        mViewModel.getListActionLiveData().observe(this, new Observer<ListActionResource<Integer>>() {
            @Override
            public void onChanged(ListActionResource<Integer> listActionResource) {
                switch(listActionResource.status){
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
        switch (view.getId()){
            case R.id.add_btn:
                mViewModel.addPlayer(new Player(mNameEditText.getText().toString()));
                break;

            case R.id.clear_btn:
                mViewModel.clearAllPlayers();
                break;

            case R.id.checkbox_btn:
                mViewModel.toggleCheckBoxButton();
                break;
        }
    }

    private void handlePlayerAddedAction(ListActionResource<Integer> listActionResource){
        if(listActionResource.data != null) {
            mListAdapter.notifyItemInserted(listActionResource.data);
            if (mListAdapter.getItemCount() > 0) {
                mLinearLayoutManager.scrollToPosition(listActionResource.data);
            }
            mNameEditText.setText("");
        }
    }

    private void handlePlayerDeletedAction(ListActionResource<Integer> listActionResource){
        if(listActionResource.data != null) {
            mListAdapter.notifyItemRemoved(listActionResource.data);
        }
    }

    private void handlePlayerCheckboxToggledAction(ListActionResource<Integer> listActionResource){
        if(listActionResource.data != null) {
            mListAdapter.notifyItemChanged(listActionResource.data);
        }
    }

    private void handleCheckboxButtonToggledAction(ListActionResource<Integer> listActionResource){
        if(listActionResource.data != null) {
            mListAdapter.notifyItemRangeChanged(0, 15);
        }
    }

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
