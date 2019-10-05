package com.marcochin.teamrandomizer2.ui.loadgroup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.marcochin.teamrandomizer2.R;
import com.marcochin.teamrandomizer2.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer2.model.Group;
import com.marcochin.teamrandomizer2.ui.UIAction;
import com.marcochin.teamrandomizer2.ui.loadgroup.adapters.LoadGroupListAdapter;
import com.marcochin.teamrandomizer2.ui.loadgroup.dialogs.DeleteGroupDialog;

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

    private OnActionListener mOnActionListener;

    public interface OnActionListener {
        void onNewGroupClicked();
        void onGroupSelected(Group group);
        void onGroupDeleted(int deletedGroupId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnActionListener){
            mOnActionListener = (OnActionListener) context;
        }else{
            throw new RuntimeException(
                    context.toString() + " must implement " + OnActionListener.class.getSimpleName());
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
                if(mOnActionListener != null){
                    mOnActionListener.onGroupSelected(group);
                }
            }

            @Override
            public void onDeleteClick(int position, Group group) {
                mViewModel.showDeleteGroupDialog(group.getId(), position);
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

                    case LoadGroupAction.SHOW_DIALOG:
                        handleShowDialogAction(uiAction);
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
                if(mOnActionListener != null) {
                    mOnActionListener.onNewGroupClicked();
                }
                break;
        }
    }

    private void handleGroupDeletedAction(UIAction<Integer> uiAction){
        if(mOnActionListener != null && uiAction.data != null) {
            mOnActionListener.onGroupDeleted(uiAction.data); // data = deleted group id
        }
    }

    @SuppressWarnings({"CastCanBeRemovedNarrowingVariableType", "SwitchStatementWithTooFewBranches"})
    private void handleShowDialogAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            FragmentManager fragmentManager = null;
            DialogFragment dialogFragment = null;
            String fragmentTag = null;

            if(getActivity() != null){
                fragmentManager = getActivity().getSupportFragmentManager();
            }

            switch (addPlayersAction.data) {
                case LoadGroupViewModel.DIALOG_DELETE_GROUP:
                    Bundle bundle = new Bundle();
                    bundle.putInt(DeleteGroupDialog.BUNDLE_KEY_GROUP_ID, mViewModel.getDeleteGroupId());
                    bundle.putInt(DeleteGroupDialog.BUNDLE_KEY_GROUP_POSITION, mViewModel.getDeleteGroupPosition());

                    dialogFragment = new DeleteGroupDialog();
                    dialogFragment.setArguments(bundle);
                    ((DeleteGroupDialog)dialogFragment).setOnDeleteGroupListener(mOnDeleteGroupListener);
                    fragmentTag = DeleteGroupDialog.TAG;
                    break;
            }

            if(fragmentManager != null && dialogFragment != null){
                dialogFragment.show(fragmentManager, fragmentTag);
            }
        }
    }

    private void handleShowMessageAction(UIAction<Integer> uiAction){
        if(uiAction.message != null) {
            Snackbar.make(mRecyclerView, uiAction.message, Snackbar.LENGTH_SHORT);
        }
    }


    // Anonymous Inner Classes

    private DeleteGroupDialog.OnDeleteGroupListener mOnDeleteGroupListener = new DeleteGroupDialog.OnDeleteGroupListener() {
        @Override
        public void onDeleteGroupClicked(int groupId, int position) {
            mViewModel.deleteGroup(groupId, position);
        }
    };
}
