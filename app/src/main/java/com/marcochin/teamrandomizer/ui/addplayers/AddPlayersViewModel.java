package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.repository.GroupRepository;
import com.marcochin.teamrandomizer.ui.Resource;
import com.marcochin.teamrandomizer.ui.UIAction;
import com.marcochin.teamrandomizer.util.ListUtil;
import com.marcochin.teamrandomizer.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    private static final String MSG_INVALID_PLAYER_NAME = "Please enter a valid name";
    public static final String MSG_INVALID_GROUP_NAME = "Please enter a valid group name";
    public static final String MSG_TOO_FEW_PLAYERS = "You must have at least 2 players";

    public static final int DIALOG_SAVE_GROUP = 1;
    public static final int DIALOG_EDIT_GROUP_NAME = 2;
    public static final int DIALOG_NUMBER_OF_TEAMS = 3;

    private static final int MIN_PLAYERS_FOR_RANDOMIZATION = 2;

    // Injected
    private GroupRepository mGroupRepository;

    private MediatorLiveData<ArrayList<Player>> mPlayerListLiveData;
    private MutableLiveData<String> mGroupNameLiveData;
    private MutableLiveData<Integer> mTotalPlayersLiveData;
    private MutableLiveData<UIAction<Integer>> mActionLiveData;

    private ArrayList<Player> mIncludedPlayersList;
    private Group mCurrentGroup;

    /**
     * If you press the home button immediately when you open the app, it will trigger autoSave
     * while it is trying to loadMostRecentGroup and will cause a crash. This is to prevent that.
     */
    private boolean mLoadingMostRecentGroup;

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
        mActionLiveData = new MutableLiveData<>();
        mIncludedPlayersList = new ArrayList<>();

        mCurrentGroup = Group.createNewGroup();
        ArrayList<Player> playerList = ListUtil.csvToPlayerList(mCurrentGroup.getPlayers());
        //TODO remove test code
//        for (int i = 0; i < 100; i++) {
//            playerList.add(new Player(i + ""));
//        }
        mPlayerListLiveData.setValue(playerList);
        mTotalPlayersLiveData.setValue(playerList.size());
        mGroupNameLiveData.setValue(mCurrentGroup.getName());

    }

    void addPlayer(String name) {
        if (!ValidationUtil.validatePlayerName(name)) {
            showMessage(MSG_INVALID_PLAYER_NAME);
            return;
        }

        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            Player player = new Player(name);

            // If checkBox state is on ALL_CHECKED or NONE_CHECKED we need to show the checkbox
            if (!mCheckBoxButtonState.equals(CheckboxButtonState.GONE)) {
                player.setCheckboxVisible(true);
            }

            playerList.add(player);

            // Update player item UI
            mActionLiveData.setValue(AddPlayersAction.playerAdded(playerList.size() - 1, null));
            // Update total player's UI
            mTotalPlayersLiveData.setValue(playerList.size());
        }
    }

    void deletePlayer(int pos) {
        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            playerList.remove(pos);

            // Update player item UI
            mActionLiveData.setValue(AddPlayersAction.playerDeleted(pos, null));
            // Update total player's UI
            mTotalPlayersLiveData.setValue(playerList.size());
        }
    }

    void clearAllPlayers() {
        // Update RecyclcerView UI
        mPlayerListLiveData.setValue(new ArrayList<Player>());
        // Update total player's UI
        mTotalPlayersLiveData.setValue(0);
    }

    void togglePlayerCheckBox(int pos) {
        // If checkBox button state is gone, we disallow toggling a player's included state
        if (mCheckBoxButtonState == CheckboxButtonState.GONE) {
            return;
        }

        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            Player player = playerList.get(pos);

            // Toggle player's included state
            player.setIncluded(!player.isIncluded());

            // Update the player item UI
            mActionLiveData.setValue(AddPlayersAction.playerCheckboxToggled(pos, null));

            // Update the total player's UI
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

        if (playerList != null && !playerList.isEmpty()) {
            // Switching checkBox states
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

            // Update player items to match the checkBox state
            for (Player player : playerList) {
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

            // Update the playerList UI
            mActionLiveData.setValue(AddPlayersAction.checkboxButtonToggled(playerList.size(), null));
        }
    }

    public void startNewGroup() {
        if (mCurrentGroup.getName().equals(Group.NEW_GROUP_NAME)) {
            // The current group is a new group
            // so we just carry over the id and clear everything else
            Group group = Group.createNewGroup();
            group.setId(mCurrentGroup.getId());
            setGroup(group, false);

        } else {
            // The current group is a saved group
            // so we try to pull the new group from the db if there is one.
            // If it exists we just carry over the id and clear everything else
            // If it doesn't exist we just create a new group ourselves.
            final LiveData<Group> source = mGroupRepository.getTheNewGroup();
            mPlayerListLiveData.addSource(source, new Observer<Group>() {
                @Override
                public void onChanged(Group group) {
                    if (group != null) {
                        Group newGroup = Group.createNewGroup();
                        newGroup.setId(group.getId());

                        // setGroup autosaves only if the prevGroup is not a new group
                        setGroup(newGroup, true);

                    } else {
                        // setGroup autosaves only if the prevGroup is not a new group
                        setGroup(Group.createNewGroup(), true);
                    }

                    mPlayerListLiveData.removeSource(source);
                }
            });
        }
    }

    public void setGroup(Group group, boolean autoSavePrevGroup) {
        // Don't auto save if it's a "new" group
        if (autoSavePrevGroup && !mCurrentGroup.getName().equals(Group.NEW_GROUP_NAME)) {
            updateGroup(false, false, GroupRepository.UpdateMessage.TYPE_SAVE);
        }

        ArrayList<Player> playerList = ListUtil.csvToPlayerList(group.getPlayers());
        mPlayerListLiveData.setValue(playerList); // Update playerList UI
        mTotalPlayersLiveData.setValue(playerList.size()); // Update totalPLayer's UI
        mGroupNameLiveData.setValue(group.getName()); // Update groupName UI

        mCurrentGroup = group;
    }

    public void syncGroupDeletion(int deletedGroupId) {
        if (deletedGroupId == mCurrentGroup.getId()) {
            startNewGroup();
        }
    }

    ArrayList<Player> getIncludedPlayersList() {
        return mIncludedPlayersList;
    }

    // Dialogs

    void showEditNameDialog() {
        if (mGroupNameLiveData.getValue() != null && !mGroupNameLiveData.getValue().equals(Group.NEW_GROUP_NAME)) {
            showDialog(DIALOG_EDIT_GROUP_NAME);
        }
    }

    void showSaveDialogOrSaveGroup() {
        // groupName comes from the textView
        if (mCurrentGroup.getName().equals(Group.NEW_GROUP_NAME)) {
            showDialog(DIALOG_SAVE_GROUP);

        } else {
            saveGroup(mGroupNameLiveData.getValue());
        }
    }

    void showNumberOfTeamsDialog() {
        List<Player> playerList = mPlayerListLiveData.getValue();

        // Extracting the included players
        if (playerList != null) {
            mIncludedPlayersList.clear();

            for (Player player : playerList) {
                if (player.isIncluded()) {
                    mIncludedPlayersList.add(player);
                }
            }
        }

        // Check if we have enough included players
        if (mIncludedPlayersList.size() < MIN_PLAYERS_FOR_RANDOMIZATION) {
            showMessage(MSG_TOO_FEW_PLAYERS);
        } else {
            showDialog(DIALOG_NUMBER_OF_TEAMS);
        }
    }


    // Database operations

    void autoSaveGroup() {
        // If app is starting and trying to load the most recent group.
        // We can't let auto save activate or else it will override the most recent group.
        if (mLoadingMostRecentGroup) {
            return;
        }

        if (mCurrentGroup.getId() == Group.NO_ID) {
            insertGroup(Group.NEW_GROUP_NAME, false);

        } else {
            updateGroup(false);
        }
    }

    void saveGroup(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            if (mCurrentGroup.getName().equals(Group.NEW_GROUP_NAME)) {
                insertGroup(groupName, true);

            } else {
                updateGroup(true);
            }

        } else {
            showMessage(MSG_INVALID_GROUP_NAME);
        }
    }

    private void insertGroup(String groupName, final boolean showMsg) {
        final Group group = new Group(groupName,
                ListUtil.playerListToCsv(mPlayerListLiveData.getValue()),
                System.currentTimeMillis());

        final LiveData<Resource<Integer>> source = mGroupRepository.insertGroup(group);

        // Piggyback on lifecycle
        mPlayerListLiveData.addSource(source, new Observer<Resource<Integer>>() {
            @Override
            public void onChanged(Resource<Integer> resource) {
                if (resource.status == Resource.Status.SUCCESS) {
                    // Update group name UI
                    mGroupNameLiveData.setValue(group.getName());

                    // Save the rowId that wes returned to us so we can use it to update the group later on
                    if (resource.data != null) {
                        group.setId(resource.data);
                    }
                    mCurrentGroup = group;
                }

                if (showMsg) {
                    showMessage(resource.message);
                }
                mPlayerListLiveData.removeSource(source);
            }
        });
    }

    private void updateGroup(boolean showMsg) {
        updateGroup(true, showMsg, GroupRepository.UpdateMessage.TYPE_SAVE);
    }

    private void updateGroup(final boolean allowSuccessCallback, final boolean showMsg, GroupRepository.UpdateMessage msgType) {
        final Group group = new Group(mCurrentGroup.getId(),
                mGroupNameLiveData.getValue(),
                ListUtil.playerListToCsv(mPlayerListLiveData.getValue()),
                System.currentTimeMillis());

        final LiveData<Resource<Integer>> source = mGroupRepository.updateGroup(group, msgType);

        // Piggyback on lifecycle
        mPlayerListLiveData.addSource(source, new Observer<Resource<Integer>>() {
            @Override
            public void onChanged(Resource<Integer> resource) {
                // Sometimes we don't want successCallback to execute because it the mCurrentGroup
                // might override mCurrentGroup setGroup().
                if (allowSuccessCallback && resource.status == Resource.Status.SUCCESS) {
                    mGroupNameLiveData.setValue(group.getName());
                    mCurrentGroup = group;
                }

                if (showMsg) {
                    showMessage(resource.message);
                }
                mPlayerListLiveData.removeSource(source);
            }
        });
    }

    void updateGroupName(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            mGroupNameLiveData.setValue(groupName);
            updateGroup(true, true, GroupRepository.UpdateMessage.TYPE_UPDATE);

        } else {
            showMessage(MSG_INVALID_GROUP_NAME);
        }
    }

    void loadMostRecentGroup() {
        mLoadingMostRecentGroup = true;

        // We piggyback on PlayerLists active/inactive states but also use liveData as our async
        // callback for fetching from db.
        final LiveData<Group> source = mGroupRepository.getMostRecentGroup();
        mPlayerListLiveData.addSource(source, new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                if (group != null) {
                    setGroup(group, false);
                }
                mPlayerListLiveData.removeSource(source);
                mLoadingMostRecentGroup = false;
            }
        });
    }


    // LiveData

    LiveData<ArrayList<Player>> getPlayerListLiveData() {
        return mPlayerListLiveData;
    }

    LiveData<String> getGroupNameLiveData() {
        return mGroupNameLiveData;
    }

    LiveData<Integer> getTotalPlayersLiveData() {
        return mTotalPlayersLiveData;
    }

    LiveData<UIAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }

    private void showDialog(int dialog) {
        mActionLiveData.setValue(AddPlayersAction.showDialog(dialog, null));
    }

    private void showMessage(String message) {
        mActionLiveData.setValue(AddPlayersAction.showMessage((Integer) null, message));
    }
}
