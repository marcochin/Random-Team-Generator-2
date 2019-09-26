package com.marcochin.teamrandomizer.ui.loadgroup;

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
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.ui.loadgroup.adapters.GroupListAdapter;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class LoadGroupFragment extends DaggerFragment {
    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GroupListAdapter mListAdapter;

    private LoadGroupViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.fl_recycler_view);

        setupRecyclerView(mRecyclerView);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(LoadGroupViewModel.class);
        observeLiveData();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

        // Set adapter
        mListAdapter = new GroupListAdapter();
        recyclerView.setAdapter(mListAdapter);

        // Set LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        // Set RecyclerView OnItemClickListener
        mListAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Group group) {
//                mViewModel.togglePlayerCheckBox(position);
            }

            @Override
            public void onDeleteClick(int position, Group group) {
//                mViewModel.deletePlayer(position);
            }
        });
    }

    private void observeLiveData(){

    }
}
