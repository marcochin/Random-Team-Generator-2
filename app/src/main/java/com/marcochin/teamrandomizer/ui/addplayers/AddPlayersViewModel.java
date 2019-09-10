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
    private MutableLiveData<ListActionResource<Integer>> mListActionLiveData;

    private CheckboxButtonState mCheckBoxButtonState = CheckboxButtonState.GONE;
    private Group mGroup;

    private enum CheckboxButtonState{
        GONE, ALL_CHECKED, NONE_CHECKED
    }

    @Inject
    public AddPlayersViewModel(GroupRepository groupRepository) {
        mGroupRepository = groupRepository;
        mPlayerListLiveData = new MutableLiveData<>();
        mGroupNameLiveData = new MutableLiveData<>();
        mListActionLiveData = new MutableLiveData<>();

        List<Player> playerList = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            playerList.add(new Player(i + ""));
        }
        mPlayerListLiveData.setValue(playerList);
    }

    public void addPlayer(Player player){
        List<Player> playerList = mPlayerListLiveData.getValue();

        if(playerList != null) {
            playerList.add(player);
            mListActionLiveData.setValue(ListActionResource.playerAdded(playerList.size() - 1 ,null));
        }
    }

    public void deletePlayer(int pos){
        List<Player> playerList = mPlayerListLiveData.getValue();

        if(playerList != null) {
            playerList.remove(pos);
            mListActionLiveData.setValue(ListActionResource.playerDeleted(pos ,null));
        }
    }

    public void clearAllPlayers(){
        if(mPlayerListLiveData.getValue() != null) {
            mPlayerListLiveData.setValue(new ArrayList<Player>());
        }
    }

    public void toggleCheckBoxButton(){
        List<Player> playerList = mPlayerListLiveData.getValue();

        if(playerList != null) {
            for(int i = 0; i <= playerList.size() - 1; i++){
                Player player = playerList.get(i);

                if(mCheckBoxButtonState == CheckboxButtonState.GONE){
                    player.setCheckboxVisible(true);
                    player.setIncluded(true);

                }else if(mCheckBoxButtonState == CheckboxButtonState.ALL_CHECKED){
                    player.setIncluded(false);

                }else{
                    player.setCheckboxVisible(false);
                }
            }

            if(mCheckBoxButtonState == CheckboxButtonState.GONE){
                mCheckBoxButtonState = CheckboxButtonState.ALL_CHECKED;

            }else if(mCheckBoxButtonState == CheckboxButtonState.ALL_CHECKED){
                mCheckBoxButtonState = CheckboxButtonState.NONE_CHECKED;

            }else if (mCheckBoxButtonState == CheckboxButtonState.NONE_CHECKED){
                mCheckBoxButtonState = CheckboxButtonState.GONE;
            }

            mListActionLiveData.setValue(ListActionResource.checkboxButtonToggled(playerList.size(),null));
        }
    }

    public void togglePlayerCheckBox(int pos){
        List<Player> playerList = mPlayerListLiveData.getValue();

        if(playerList != null) {
            Player player = playerList.get(pos);
            player.setIncluded(!player.isIncluded());
            mListActionLiveData.setValue(ListActionResource.playerCheckboxToggled(pos ,null));
        }
    }

    public void resetListActionLiveData(){
        mListActionLiveData.setValue(ListActionResource.noAction(-1, null));
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

    public LiveData<List<Player>> getPlayerListLiveData() {
        return mPlayerListLiveData;
    }

    public LiveData<String> getGroupNameLiveData() {
        return mGroupNameLiveData;
    }

    public LiveData<ListActionResource<Integer>> getListActionLiveData(){
        return mListActionLiveData;
    }
}
