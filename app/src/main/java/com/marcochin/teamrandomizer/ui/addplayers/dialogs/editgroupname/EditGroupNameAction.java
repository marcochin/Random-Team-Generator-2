package com.marcochin.teamrandomizer.ui.addplayers.dialogs.editgroupname;

import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer.ui.UIAction;

class EditGroupNameAction<T> extends UIAction<T> {
    public static final int GROUP_VALIDATED = 0;

    public EditGroupNameAction(int action) {
        this(action, null, null);
    }

    public EditGroupNameAction(int action, @Nullable T data) {
        this(action, data, null);
    }

    public EditGroupNameAction(int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> EditGroupNameAction<T> groupValidated(@Nullable T data, @Nullable String msg) {
        return new EditGroupNameAction<>(GROUP_VALIDATED, data, msg);
    }
}