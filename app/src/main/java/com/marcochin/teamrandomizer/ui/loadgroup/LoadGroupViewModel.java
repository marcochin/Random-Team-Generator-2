package com.marcochin.teamrandomizer.ui.loadgroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.repository.GroupRepository;

import java.util.List;

import javax.inject.Inject;

public class LoadGroupViewModel extends ViewModel {
    private GroupRepository mGroupRepository;

    private MediatorLiveData<List<Group>> mGroupListLiveData;

    @Inject
    public LoadGroupViewModel(GroupRepository repository) {
        mGroupRepository = repository;
        mGroupListLiveData = new MediatorLiveData<>();
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

    // LiveData

    LiveData<List<Group>> getGroupsListLiveData(){
        return mGroupListLiveData;
    }
}
