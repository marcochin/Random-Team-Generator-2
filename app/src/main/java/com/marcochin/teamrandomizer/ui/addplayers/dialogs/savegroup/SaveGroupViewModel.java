package com.marcochin.teamrandomizer.ui.addplayers.dialogs.savegroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersViewModel;
import com.marcochin.teamrandomizer.util.ValidationUtil;

public class SaveGroupViewModel extends ViewModel {
    private MutableLiveData<SaveGroupAction<Integer>> mActionLiveData;

    public SaveGroupViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    void validateGroupName(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            mActionLiveData.setValue(SaveGroupAction.groupValidated((Integer) null, null));
        } else {
            // Show error msg
            showMessage(AddPlayersViewModel.MSG_INVALID_GROUP_NAME);
        }
    }


    // LiveData

    LiveData<SaveGroupAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }

    private void showMessage(String message){
        mActionLiveData.setValue(SaveGroupAction.showMessage((Integer)null, message));
    }
}
