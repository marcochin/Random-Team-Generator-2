package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.ui.Resource;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    private MutableLiveData<Group> mGroupLiveData;

    private GroupRepository mGroupRepository;

    @Inject
    public AddPlayersViewModel(GroupRepository groupRepository) {
        mGroupRepository = groupRepository;
    }

    public LiveData<Resource<Integer>> insertGroup(){
        return null;
    }

    public LiveData<Resource<Integer>> updateGroup(){
        return null;
    }

    public LiveData<Group> getGroupLiveData() {
        return mGroupLiveData;
    }
}
