package com.marcochin.teamrandomizer.ui.addplayers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.PlayerListAdapter;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment {

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private AddPlayersViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        PlayerListAdapter listAdapter = new PlayerListAdapter();
        recyclerView.setAdapter(listAdapter);

        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);

        // Observer liveData here and populate player list
    }
}
