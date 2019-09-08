package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.ui.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    // Injected
    private GroupRepository mGroupRepository;

    private MutableLiveData<List<Player>> mPlayerListLiveData;
    private MutableLiveData<String> mGroupNameLiveData;
    private MutableLiveData<UserAction> mUserActionLiveData;

    private Group mGroup;

    public enum UserAction{
        ADD_PLAYER, NONE
    }

    @Inject
    public AddPlayersViewModel(GroupRepository groupRepository) {
        mGroupRepository = groupRepository;
        mPlayerListLiveData = new MutableLiveData<>();
        mGroupNameLiveData = new MutableLiveData<>();
        mUserActionLiveData = new MutableLiveData<>();

        List<Player> playerList = new ArrayList<>();
        mPlayerListLiveData.setValue(playerList);
    }

    public void addPlayer(Player player){
        if(mPlayerListLiveData.getValue() != null) {
            List<Player> oldPlayerList = mPlayerListLiveData.getValue();
            List<Player> newPlayerList = new ArrayList<>(oldPlayerList);
            newPlayerList.add(player);
            mPlayerListLiveData.setValue(newPlayerList);

            mUserActionLiveData.setValue(UserAction.ADD_PLAYER);
        }
    }

    public void deletePlayer(Player player){
        if(mPlayerListLiveData.getValue() != null) {
            List<Player> oldPlayerList = mPlayerListLiveData.getValue();
            List<Player> newPlayerList = new ArrayList<>(oldPlayerList);
            newPlayerList.remove(player);
            mPlayerListLiveData.setValue(newPlayerList);
        }
    }

    public void setGroup(Group group){
        mGroup = group;
    }

    public void setGroupName(String groupName){
        mGroupNameLiveData.setValue(groupName);
    }

    public LiveData<Resource<Integer>> insertGroup(){
        // TODO
        return null;
    }

    public LiveData<Resource<Integer>> updateGroup(){
        // TODO
        return null;
    }

    public void getMostRecentGroup(){
        // TODO
    }

    public void clearUserActionLiveData(){
        mUserActionLiveData.setValue(UserAction.NONE);
    }

    public LiveData<List<Player>> getPlayerListLiveData() {
        return mPlayerListLiveData;
    }

    public LiveData<String> getGroupNameLiveData() {
        return mGroupNameLiveData;
    }

    public LiveData<UserAction> getUserActionLiveData(){
        return mUserActionLiveData;
    }
}
