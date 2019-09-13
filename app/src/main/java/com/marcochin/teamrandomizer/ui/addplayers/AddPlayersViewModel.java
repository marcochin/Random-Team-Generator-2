package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.ui.Constants;
import com.marcochin.teamrandomizer.ui.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    private static final String MSG_INVALID_NAME = "Please enter a valid name!";

    // Injected
    private GroupRepository mGroupRepository;

    private MutableLiveData<List<Player>> mPlayerListLiveData;
    private MutableLiveData<String> mGroupNameLiveData;
    private MutableLiveData<Integer> mTotalPlayersLiveData;
    private MutableLiveData<AddPlayersActionResource<Integer>> mAddPlayersActionLiveData;

    private Group mGroup;

    private CheckboxButtonState mCheckBoxButtonState = CheckboxButtonState.GONE;

    private enum CheckboxButtonState {
        GONE, ALL_CHECKED, NONE_CHECKED
    }

    @Inject
    public AddPlayersViewModel(GroupRepository groupRepository) {
        mGroupRepository = groupRepository;
        mPlayerListLiveData = new MutableLiveData<>();
        mGroupNameLiveData = new MutableLiveData<>();
        mTotalPlayersLiveData = new MutableLiveData<>();
        mAddPlayersActionLiveData = new MutableLiveData<>();

        List<Player> playerList = new ArrayList<>();

        //TODO remove test code
        for (int i = 0; i < 100; i++) {
            playerList.add(new Player(i + ""));
        }
        mPlayerListLiveData.setValue(playerList);
        mTotalPlayersLiveData.setValue(playerList.size());
    }

    void addPlayer(String name) {
        if(!validatePlayerName(name)){
            mAddPlayersActionLiveData.setValue(AddPlayersActionResource.showMessage((Integer)null, MSG_INVALID_NAME));
            return;
        }

        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            Player player = new Player(name);
            player.setIncluded(true); // default new player to always be included...aww how nice

            if (!mCheckBoxButtonState.equals(CheckboxButtonState.GONE)) {
                player.setCheckboxVisible(true);
            }

            playerList.add(player);
            mAddPlayersActionLiveData.setValue(AddPlayersActionResource.playerAdded(playerList.size() - 1, null));
            mTotalPlayersLiveData.setValue(playerList.size());
        }
    }

    void deletePlayer(int pos) {
        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            playerList.remove(pos);
            mAddPlayersActionLiveData.setValue(AddPlayersActionResource.playerDeleted(pos, null));
            mTotalPlayersLiveData.setValue(playerList.size());
        }
    }

    void clearAllPlayers() {
        mPlayerListLiveData.setValue(new ArrayList<Player>());
        mTotalPlayersLiveData.setValue(0);
    }

    void togglePlayerCheckBox(int pos) {
        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            Player player = playerList.get(pos);
            player.setIncluded(!player.isIncluded());
            mAddPlayersActionLiveData.setValue(AddPlayersActionResource.playerCheckboxToggled(pos, null));

            if(mTotalPlayersLiveData.getValue() != null) {
                if (player.isIncluded()) {
                    mTotalPlayersLiveData.setValue(mTotalPlayersLiveData.getValue() + 1);
                } else {
                    mTotalPlayersLiveData.setValue(mTotalPlayersLiveData.getValue() - 1);
                }
            }
        }
    }

    void toggleCheckBoxButton() {
        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            if (mCheckBoxButtonState == CheckboxButtonState.GONE) {
                mCheckBoxButtonState = CheckboxButtonState.ALL_CHECKED;
                // Don't need to set total players here because GONE -> ALL CHECKED wont change the total

            } else if (mCheckBoxButtonState == CheckboxButtonState.ALL_CHECKED) {
                mCheckBoxButtonState = CheckboxButtonState.NONE_CHECKED;
                mTotalPlayersLiveData.setValue(0);

            } else if (mCheckBoxButtonState == CheckboxButtonState.NONE_CHECKED) {
                mCheckBoxButtonState = CheckboxButtonState.GONE;
                mTotalPlayersLiveData.setValue(playerList.size());
            }

            for (int i = 0; i <= playerList.size() - 1; i++) {
                Player player = playerList.get(i);

                switch(mCheckBoxButtonState){
                    case GONE:
                        player.setCheckboxVisible(false);
                        player.setIncluded(true);
                        break;

                    case ALL_CHECKED:
                        player.setCheckboxVisible(true);
                        player.setIncluded(true);
                        break;

                    case NONE_CHECKED:
                        player.setIncluded(false);
                        break;
                }
            }

            mAddPlayersActionLiveData.setValue(AddPlayersActionResource.checkboxButtonToggled(playerList.size(), null));
        }
    }

    public void setGroup(Group group) {
        mGroup = group;
    }

    public void setGroupName(String groupName) {
        mGroupNameLiveData.setValue(groupName);
    }

    // Validations
    private boolean validatePlayerName(String name){
        return name != null && !name.trim().isEmpty() && !name.contains(Constants.NAME_SEPARATOR);
    }


    // Database operations
    public LiveData<Resource<Integer>> insertGroup() {
        // TODO
        return null;
    }

    public LiveData<Resource<Integer>> updateGroup() {
        // TODO
        return null;
    }

    public void getMostRecentGroup() {
        // TODO
    }


    // LiveData
    LiveData<List<Player>> getPlayerListLiveData() {
        return mPlayerListLiveData;
    }

    LiveData<String> getGroupNameLiveData() {
        return mGroupNameLiveData;
    }

    LiveData<Integer> getTotalPlayersLiveData() {
        return mTotalPlayersLiveData;
    }

    LiveData<AddPlayersActionResource<Integer>> getAddPlayersActionLiveData() {
        return mAddPlayersActionLiveData;
    }

    void clearAddPlayersActionLiveData() {
        mAddPlayersActionLiveData.setValue(AddPlayersActionResource.<Integer>noAction());
    }
}
