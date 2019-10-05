package com.marcochin.teamrandomizer2.ui.loadgroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer2.model.Group;
import com.marcochin.teamrandomizer2.persistence.repository.GroupRepository;
import com.marcochin.teamrandomizer2.ui.Resource;
import com.marcochin.teamrandomizer2.ui.UIAction;

import java.util.List;

import javax.inject.Inject;

public class LoadGroupViewModel extends ViewModel {
    public static final int DIALOG_DELETE_GROUP = 1;

    private GroupRepository mGroupRepository;

    private int mDeleteGroupId;
    private int mDeleteGroupPosition;

    private MediatorLiveData<List<Group>> mGroupListLiveData;
    private MutableLiveData<UIAction<Integer>> mActionLiveData;

    @Inject
    public LoadGroupViewModel(GroupRepository repository) {
        mGroupRepository = repository;
        mGroupListLiveData = new MediatorLiveData<>();
        mActionLiveData = new MutableLiveData<>();
    }

    void loadAllGroups() {
        // We piggyback on PlayerLists active/inactive states but also use liveData as our async
        // callback for fetching from db.
        final LiveData<List<Group>> source = mGroupRepository.getAllGroups();

        mGroupListLiveData.addSource(source, new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                mGroupListLiveData.setValue(groups);
            }
        });
    }

    void deleteGroup(final int groupId, int position) {
        if (mGroupListLiveData.getValue() != null) {
            Group group = mGroupListLiveData.getValue().get(position);

            final LiveData<Resource<Integer>> source = mGroupRepository.deleteGroup(group);
            mGroupListLiveData.addSource(source, new Observer<Resource<Integer>>() {
                @Override
                public void onChanged(Resource<Integer> integerResource) {
                    if (integerResource.status == Resource.Status.SUCCESS) {
                        // Update group item UI
                        mActionLiveData.setValue(LoadGroupAction.groupDeleted(groupId, null));

                    } else if (integerResource.status == Resource.Status.ERROR) {
                        showMessage(integerResource.message);
                    }
                    mGroupListLiveData.removeSource(source);
                }
            });
            // Ui will update on its own because we are observing live data straight from the source.
        }
    }


    // Getters

    int getDeleteGroupId(){
        return mDeleteGroupId;
    }

    int getDeleteGroupPosition(){
        return mDeleteGroupPosition;
    }


    // Dialogs

    void showDeleteGroupDialog(int groupId, int position){
        mDeleteGroupId = groupId;
        mDeleteGroupPosition = position;
        showDialog(DIALOG_DELETE_GROUP);
    }


    // LiveData

    LiveData<List<Group>> getGroupsListLiveData() {
        return mGroupListLiveData;
    }

    LiveData<UIAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }


    // Utility

    @SuppressWarnings("SameParameterValue")
    private void showDialog(int dialog) {
        mActionLiveData.setValue(LoadGroupAction.showDialog(dialog, null));
    }

    private void showMessage(String message) {
        mActionLiveData.setValue(LoadGroupAction.showMessage((Integer) null, message));
    }
}
