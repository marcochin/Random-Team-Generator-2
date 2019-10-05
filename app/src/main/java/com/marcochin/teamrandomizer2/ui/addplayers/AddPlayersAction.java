package com.marcochin.teamrandomizer2.ui.addplayers;

import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer2.ui.UIAction;

public class AddPlayersAction<T> extends UIAction<T> {
    public static final int PLAYER_ADDED = 0;
    public static final int PLAYER_DELETED = 1;
    public static final int PLAYER_CHECKBOX_TOGGLED = 2;
    public static final int CHECKBOX_BUTTON_TOGGLED = 3;

    public AddPlayersAction(int action) {
        this(action, null, null);
    }

    public AddPlayersAction(int action, @Nullable T data) {
        this(action, data, null);
    }

    public AddPlayersAction(int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> AddPlayersAction<T> playerAdded(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(PLAYER_ADDED, data, msg);
    }

    public static <T> AddPlayersAction<T> playerDeleted(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(PLAYER_DELETED, data, msg);
    }

    public static <T> AddPlayersAction<T> playerCheckboxToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(PLAYER_CHECKBOX_TOGGLED, data, msg);
    }

    public static <T> AddPlayersAction<T> checkboxButtonToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(CHECKBOX_BUTTON_TOGGLED, data, msg);
    }
}
