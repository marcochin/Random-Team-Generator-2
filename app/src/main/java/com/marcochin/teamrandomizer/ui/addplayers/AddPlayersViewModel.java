package com.marcochin.teamrandomizer.ui.addplayers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.database.GroupDatabase;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    private static final String MSG_INVALID_NAME = "Please enter a valid name!";

    // Injected
    private GroupRepository mGroupRepository;

    private MediatorLiveData<List<Player>> mPlayerListLiveData;
    private MutableLiveData<String> mGroupNameLiveData;
    private MutableLiveData<Integer> mTotalPlayersLiveData;
    private MutableLiveData<AddPlayersActionResource<Integer>> mAddPlayersActionLiveData;

    private Group mCurrentGroup;

    private CheckboxButtonState mCheckBoxButtonState = CheckboxButtonState.GONE;

    private enum CheckboxButtonState {
        GONE, ALL_CHECKED, NONE_CHECKED
    }

    @Inject
    public AddPlayersViewModel(GroupRepository groupRepository) {
        mGroupRepository = groupRepository;
        mPlayerListLiveData = new MediatorLiveData<>();
        mGroupNameLiveData = new MutableLiveData<>();
        mTotalPlayersLiveData = new MutableLiveData<>();
        mAddPlayersActionLiveData = new MutableLiveData<>();

        List<Player> playerList = new ArrayList<>();

        //TODO remove test code
//        for (int i = 0; i < 100; i++) {
//            playerList.add(new Player(i + ""));
//        }
        mPlayerListLiveData.setValue(playerList);
        mTotalPlayersLiveData.setValue(playerList.size());
        mGroupNameLiveData.setValue(GroupDatabase.NEW_GROUP_NAME);
    }

    void addPlayer(String name) throws IllegalArgumentException {
        if (!validatePlayerName(name)) {
            throw new IllegalArgumentException(MSG_INVALID_NAME);
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

            if (mTotalPlayersLiveData.getValue() != null) {
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

                switch (mCheckBoxButtonState) {
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
        mCurrentGroup = group;
    }

    public void setGroupName(String groupName) {
        if(validateGroupName(groupName)) {
            mGroupNameLiveData.setValue(groupName);

        }else{
            Log.d("meme", MSG_INVALID_NAME);
        }
    }


    // Validations

    private boolean validatePlayerName(String playerName) {
        return playerName != null && !playerName.trim().isEmpty() && !playerName.contains(ListUtil.NAME_SEPARATOR);
    }

    private boolean validateGroupName(String groupName) {
        return groupName != null && !groupName.trim().isEmpty() && !groupName.trim().equals(GroupDatabase.NEW_GROUP_NAME);
    }


    // Database operations

    public void showNameDialogOrSaveGroup(String groupName) {
        // groupName comes from the textView
        if (groupName == null || groupName.equals(GroupDatabase.NEW_GROUP_NAME)) {
            // Bring up dialog

        } else {
            saveGroup(groupName);
        }
    }

    public void autoSaveGroup() {
        if (mCurrentGroup == null) {
            insertGroup(GroupDatabase.NEW_GROUP_NAME);

        } else if (mCurrentGroup.getId() > 0) {
            updateGroup();
        }
    }

    private void saveGroup(String groupName) {
        if (validateGroupName(groupName)) {
            if (mCurrentGroup == null || mCurrentGroup.getName().equals(GroupDatabase.NEW_GROUP_NAME)) {
                insertGroup(groupName);

            } else if (mCurrentGroup.getId() > 0) {
                updateGroup();
            }
        }else{
            // Show error
            Log.d("meme", MSG_INVALID_NAME);
        }
    }

    private void insertGroup(String groupName) {
        Group group = new Group(groupName,
                ListUtil.playerListToCsv(mPlayerListLiveData.getValue()),
                System.currentTimeMillis());

        mGroupRepository.insertGroup(group);
        //TODO need to observe
    }

    private void updateGroup() {
        mCurrentGroup.setName(mGroupNameLiveData.getValue());
        mCurrentGroup.setPlayers(ListUtil.playerListToCsv(mPlayerListLiveData.getValue()));
        mCurrentGroup.setUpdatedAt(System.currentTimeMillis());

        mGroupRepository.updateGroup(mCurrentGroup);
        //TODO need to observe
    }

    void loadMostRecentGroup() {
        // We piggyback on PlayerLists active/inactive states but also use liveData as our async
        // callback for fetching from db.
        final LiveData<Group> source = mGroupRepository.getMostRecentGroup();
        mPlayerListLiveData.addSource(source, new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                if (group != null) {
                    List<Player> playerList = ListUtil.csvToPlayerList(group.getPlayers());
                    mPlayerListLiveData.setValue(playerList);
                    mTotalPlayersLiveData.setValue(playerList.size());
                    mGroupNameLiveData.setValue(group.getName());
                    mCurrentGroup = group;
                }
                mPlayerListLiveData.removeSource(source);
            }
        });
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
        mAddPlayersActionLiveData.setValue(null);
    }
}
