package com.marcochin.teamrandomizer2.ui.addplayers.dialogs.editgroupname;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer2.ui.UIAction;
import com.marcochin.teamrandomizer2.ui.addplayers.AddPlayersViewModel;
import com.marcochin.teamrandomizer2.util.ValidationUtil;

public class EditGroupNameViewModel extends ViewModel {
    private MutableLiveData<UIAction<Integer>> mActionLiveData;

    public EditGroupNameViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    public void validateGroupName(String groupName) {
        if (ValidationUtil.validateGroupName(groupName)) {
            mActionLiveData.setValue(EditGroupNameAction.groupValidated((Integer) null, null));
        } else {
            // Show error msg
            showMessage(AddPlayersViewModel.MSG_INVALID_GROUP_NAME);
        }
    }


    // LiveData

    LiveData<UIAction<Integer>> getActionLiveData() {
        return mActionLiveData;
    }

    void clearActionLiveData() {
        mActionLiveData.setValue(null);
    }

    private void showMessage(String message){
        mActionLiveData.setValue(EditGroupNameAction.showMessage((Integer)null, message));
    }
}
