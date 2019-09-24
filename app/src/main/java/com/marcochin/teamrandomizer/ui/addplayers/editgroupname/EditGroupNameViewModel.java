package com.marcochin.teamrandomizer.ui.addplayers.editgroupname;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersViewModel;
import com.marcochin.teamrandomizer.util.ValidationUtil;

public class EditGroupNameViewModel extends ViewModel {
    private MutableLiveData<EditGroupNameAction<Integer>> mActionLiveData;

    public EditGroupNameViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    public void validateGroupName(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            mActionLiveData.setValue(EditGroupNameAction.groupValidated((Integer) null, null));
        } else {
            // Show error msg
            mActionLiveData.setValue(
                    EditGroupNameAction.showMessage((Integer) null, AddPlayersViewModel.MSG_INVALID_GROUP_NAME));
        }
    }

    LiveData<EditGroupNameAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }
}
