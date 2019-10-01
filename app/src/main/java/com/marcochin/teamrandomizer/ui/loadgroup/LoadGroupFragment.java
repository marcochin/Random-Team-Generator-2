package com.marcochin.teamrandomizer.ui.loadgroup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.ui.UIAction;
import com.marcochin.teamrandomizer.ui.loadgroup.adapters.LoadGroupListAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class LoadGroupFragment extends DaggerFragment implements View.OnClickListener{
    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private LoadGroupListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private TextView mNoSavedGroupsText;

    private LoadGroupViewModel mViewModel;

    private OnActionReceiver mOnActionReceiver;

    public interface OnActionReceiver{
        void onNewGroupRequested();
        void onGroupSelected(Group group);
        void onGroupDeleted(int deletedGroupId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnActionReceiver){
            mOnActionReceiver = (OnActionReceiver) context;
        }else{
            throw new RuntimeException(
                    context.toString() + " must implement " + OnActionReceiver.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.fl_recycler_view);
        mNoSavedGroupsText = view.findViewById(R.id.fl_no_saved_groups_text);
        ViewGroup newGroupButton = view.findViewById(R.id.fl_new_group_btn);

        newGroupButton.setOnClickListener(this);

        setupRecyclerView(mRecyclerView);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(LoadGroupViewModel.class);
        observeLiveData();
        loadAllGroups();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

        // Set adapter
        mListAdapter = new LoadGroupListAdapter();
        recyclerView.setAdapter(mListAdapter);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set RecyclerView OnItemClickListener
        mListAdapter.setOnItemClickListener(new LoadGroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Group group) {
                if(mOnActionReceiver != null){
                    mOnActionReceiver.onGroupSelected(group);
                }
            }

            @Override
            public void onDeleteClick(int position, Group group) {
                mViewModel.deleteGroup(group.getId(), position);
            }
        });
    }

    private void loadAllGroups() {
        mViewModel.loadAllGroups();
    }

    private void observeLiveData(){
        mViewModel.getGroupsListLiveData().observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                mListAdapter.submitList(groups);

                if(groups == null || groups.isEmpty()){
                    mNoSavedGroupsText.setVisibility(View.VISIBLE);
                }else{
                    mNoSavedGroupsText.setVisibility(View.INVISIBLE);
                }
            }
        });

        mViewModel.getActionLiveData().observe(this, new Observer<UIAction<Integer>>() {
            @Override
            public void onChanged(UIAction<Integer> uiAction) {
                if (uiAction == null) {
                    return;
                }

                switch (uiAction.action) {
                    case LoadGroupAction.GROUP_DELETED:
                        handleGroupDeletedAction(uiAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case LoadGroupAction.SHOW_MSG:
                        handleShowMessageAction(uiAction);
                        mViewModel.clearActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_new_group_btn:
                if(mOnActionReceiver != null) {
                    mOnActionReceiver.onNewGroupRequested();
                }
                break;
        }
    }

    private void handleGroupDeletedAction(UIAction<Integer> uiAction){
        if(mOnActionReceiver != null && uiAction.data != null) {
            mOnActionReceiver.onGroupDeleted(uiAction.data); // data = deleted group id
        }
    }

    private void handleShowMessageAction(UIAction<Integer> uiAction){
        if(uiAction.message != null) {
            Snackbar.make(mRecyclerView, uiAction.message, Snackbar.LENGTH_SHORT);
        }
    }
}
