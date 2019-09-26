package com.marcochin.teamrandomizer.ui.loadgroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.ui.Resource;

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

    void deleteGroup(int position){
        if(mGroupListLiveData.getValue() != null) {
            Group group = mGroupListLiveData.getValue().get(position);

            final LiveData<Resource<Integer>> source = mGroupRepository.deleteGroup(group);
            mGroupListLiveData.addSource(source, new Observer<Resource<Integer>>() {
                @Override
                public void onChanged(Resource<Integer> integerResource) {
                    mGroupListLiveData.removeSource(source);
                }
            });
            // Ui will update on its own because we are observing live data straight from the source.
        }
    }

    // LiveData

    LiveData<List<Group>> getGroupsListLiveData(){
        return mGroupListLiveData;
    }
}
