package com.marcochin.teamrandomizer.ui.addplayers.dialogs.savegroup;

import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer.ui.UIAction;

class SaveGroupAction<T> extends UIAction<T> {
    public static final int GROUP_VALIDATED = 0;

    public SaveGroupAction(int action) {
        this(action, null, null);
    }

    public SaveGroupAction(int action, @Nullable T data) {
        this(action, data, null);
    }

    public SaveGroupAction(int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> SaveGroupAction<T> groupValidated(@Nullable T data, @Nullable String msg) {
        return new SaveGroupAction<>(GROUP_VALIDATED, data, msg);
    }
}
