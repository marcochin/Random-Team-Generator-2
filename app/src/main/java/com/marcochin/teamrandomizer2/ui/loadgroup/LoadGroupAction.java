package com.marcochin.teamrandomizer2.ui.loadgroup;

import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer2.ui.UIAction;

public class LoadGroupAction <T> extends UIAction<T> {
    public static final int GROUP_DELETED = 0;

    public LoadGroupAction(int action) {
        this(action, null, null);
    }

    public LoadGroupAction(int action, @Nullable T data) {
        this(action, data, null);
    }

    public LoadGroupAction(int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> LoadGroupAction<T> groupDeleted(@Nullable T data, @Nullable String msg) {
        return new LoadGroupAction<>(GROUP_DELETED, data, msg);
    }
}
