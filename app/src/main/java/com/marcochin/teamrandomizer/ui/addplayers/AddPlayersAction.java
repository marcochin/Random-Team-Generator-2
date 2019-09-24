package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddPlayersAction<T> {

    @NonNull
    public final AddPlayersActionType action;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum AddPlayersActionType {
        PLAYER_ADDED, PLAYER_DELETED, PLAYER_CHECKBOX_TOGGLED, CHECKBOX_BUTTON_TOGGLED,
        SHOW_DIALOG, SHOW_MSG
    }

    public AddPlayersAction(@NonNull AddPlayersActionType action) {
        this(action, null, null);
    }

    public AddPlayersAction(@NonNull AddPlayersActionType action, @Nullable T data) {
        this(action, data, null);
    }

    public AddPlayersAction(@NonNull AddPlayersActionType action, @Nullable T data, @Nullable String message) {
        this.action = action;
        this.data = data;
        this.message = message;
    }

    public static <T> AddPlayersAction<T> playerAdded(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.PLAYER_ADDED, data, msg);
    }

    public static <T> AddPlayersAction<T> playerDeleted(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.PLAYER_DELETED, data, msg);
    }

    public static <T> AddPlayersAction<T> playerCheckboxToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.PLAYER_CHECKBOX_TOGGLED, data, msg);
    }

    public static <T> AddPlayersAction<T> checkboxButtonToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.CHECKBOX_BUTTON_TOGGLED, data, msg);
    }

    public static <T> AddPlayersAction<T> showDialog(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.SHOW_DIALOG, data, msg);
    }

    public static <T> AddPlayersAction<T> showMessage(@Nullable T data, @Nullable String msg) {
        return new AddPlayersAction<>(AddPlayersActionType.SHOW_MSG, data, msg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != AddPlayersAction.class){
            return false;
        }

        AddPlayersAction<T> resource = (AddPlayersAction) obj;

        if(resource.action != this.action){
            return false;
        }

        if(this.data != null){
            if(resource.data != this.data){
                return false;
            }
        }

        if(resource.message != null){
            if(this.message == null){
                return false;
            }
            if(!resource.message.equals(this.message)){
                return false;
            }
        }

        return true;
    }
}
