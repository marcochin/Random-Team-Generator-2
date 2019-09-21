package com.marcochin.teamrandomizer.ui.addplayers.savegroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersViewModel;
import com.marcochin.teamrandomizer.util.ValidationUtil;

public class SaveGroupViewModel extends ViewModel {
    private MutableLiveData<SaveGroupAction<Integer>> mSaveGroupActionLiveData;

    public SaveGroupViewModel() {
        mSaveGroupActionLiveData = new MutableLiveData<>();
    }

    public void validateGroupName(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            mSaveGroupActionLiveData.setValue(SaveGroupAction.groupValidated((Integer) null, null));
        } else {
            // Show error msg
            mSaveGroupActionLiveData.setValue(
                    SaveGroupAction.showMessage((Integer) null, AddPlayersViewModel.MSG_INVALID_GROUP_NAME));
        }
    }

    LiveData<SaveGroupAction<Integer>> getSaveGroupActionLiveData() {
        return mSaveGroupActionLiveData;
    }

    void clearSaveGroupActionLiveData() {
        mSaveGroupActionLiveData.setValue(null);
    }
}
