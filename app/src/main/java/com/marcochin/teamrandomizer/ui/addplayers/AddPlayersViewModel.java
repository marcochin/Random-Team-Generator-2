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
import com.marcochin.teamrandomizer.util.ListUtil;
import com.marcochin.teamrandomizer.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddPlayersViewModel extends ViewModel {
    private static final String MSG_INVALID_PLAYER_NAME = "Please enter a valid player name";
    public static final String MSG_INVALID_GROUP_NAME = "Please enter a valid group name";
    public static final String MSG_GROUP_NAME_UPDATED = "Updated";

    public static final int DIALOG_SAVE_GROUP = 1;
    public static final int DIALOG_EDIT_GROUP_NAME = 2;

    // Injected
    private GroupRepository mGroupRepository;

    private MediatorLiveData<List<Player>> mPlayerListLiveData;
    private MutableLiveData<String> mGroupNameLiveData;
    private MutableLiveData<Integer> mTotalPlayersLiveData;
    private MutableLiveData<AddPlayersAction<Integer>> mActionLiveData;

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
        mActionLiveData = new MutableLiveData<>();

        mCurrentGroup = new Group(Group.NEW_GROUP_NAME, null, System.currentTimeMillis());
        List<Player> playerList = ListUtil.csvToPlayerList(mCurrentGroup.getPlayers());
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
            player.setIncluded(true); // default new player to always be included...aww how nice

            if (!mCheckBoxButtonState.equals(CheckboxButtonState.GONE)) {
                player.setCheckboxVisible(true);
            }

            playerList.add(player);
            mActionLiveData.setValue(AddPlayersAction.playerAdded(playerList.size() - 1, null));
            mTotalPlayersLiveData.setValue(playerList.size());
        }
    }

    void deletePlayer(int pos) {
        List<Player> playerList = mPlayerListLiveData.getValue();

        if (playerList != null) {
            playerList.remove(pos);
            mActionLiveData.setValue(AddPlayersAction.playerDeleted(pos, null));
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
            mActionLiveData.setValue(AddPlayersAction.playerCheckboxToggled(pos, null));

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

            mActionLiveData.setValue(AddPlayersAction.checkboxButtonToggled(playerList.size(), null));
        }
    }

    public void setGroup(Group group) {
        mCurrentGroup = group;
    }

    void setGroupName(String groupName) {
        if(ValidationUtil.validateGroupName(groupName)) {
            mGroupNameLiveData.setValue(groupName);
            showMessage(MSG_GROUP_NAME_UPDATED);

        }else{
            showMessage(MSG_INVALID_GROUP_NAME);
        }
    }

    // Dialogs

    void showEditNameDialog(){
        if(mGroupNameLiveData.getValue() != null && !mGroupNameLiveData.getValue().equals(Group.NEW_GROUP_NAME)){
            showDialog(DIALOG_EDIT_GROUP_NAME);
        }
    }

    void showSaveDialogOrSaveGroup(String groupName) {
        // groupName comes from the textView
        if (mCurrentGroup.getId() == Group.NO_ID) {
            showDialog(DIALOG_SAVE_GROUP);

        } else {
            saveGroup(groupName);
        }
    }


    // Database operations

    void autoSaveGroup() {
        if (mCurrentGroup.getId() == Group.NO_ID) {
            insertGroup(Group.NEW_GROUP_NAME, false);

        } else {
            updateGroup(false);
        }
    }

    void saveGroup(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            if (mCurrentGroup.getId() == Group.NO_ID) {
                insertGroup(groupName, true);

            } else {
                updateGroup(true);
            }

        }else{
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
                if(resource.status == Resource.Status.SUCCESS){
                    mGroupNameLiveData.setValue(group.getName());

                    if(resource.data != null){
                        group.setId(resource.data);
                    }
                    mCurrentGroup = group;
                }

                if(showMsg){
                    showMessage(resource.message);
                }
                mPlayerListLiveData.removeSource(source);
            }
        });
    }

    private void updateGroup(final boolean showMsg) {
        final Group group = new Group(mCurrentGroup.getId(),
                mGroupNameLiveData.getValue(),
                ListUtil.playerListToCsv(mPlayerListLiveData.getValue()),
                System.currentTimeMillis());

        final LiveData<Resource<Integer>> source = mGroupRepository.updateGroup(group);

        // Piggyback on lifecycle
        mPlayerListLiveData.addSource(source, new Observer<Resource<Integer>>() {
            @Override
            public void onChanged(Resource<Integer> resource) {
                if(resource.status == Resource.Status.SUCCESS){
                    mGroupNameLiveData.setValue(group.getName());
                    mCurrentGroup = group;
                }

                if(showMsg) {
                    showMessage(resource.message);
                }
                mPlayerListLiveData.removeSource(source);
            }
        });
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

    LiveData<AddPlayersAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }

    private void showDialog(int dialog){
        mActionLiveData.setValue(AddPlayersAction.showDialog(dialog, null));
    }

    private void showMessage(String message){
        mActionLiveData.setValue(AddPlayersAction.showMessage((Integer)null, message));
    }
}
