package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddPlayersActionResource<T> {

    @NonNull
    public final ListAction status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum ListAction {
        PLAYER_ADDED, PLAYER_DELETED, PLAYER_CHECKBOX_TOGGLED, CHECKBOX_BUTTON_TOGGLED, SHOW_MSG, NO_ACTION
    }

    public AddPlayersActionResource(@NonNull ListAction status) {
        this(status, null, null);
    }

    public AddPlayersActionResource(@NonNull ListAction status, @Nullable T data) {
        this(status, data, null);
    }

    public AddPlayersActionResource(@NonNull ListAction status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> AddPlayersActionResource<T> playerAdded(@NonNull T data, @Nullable String msg) {
        return new AddPlayersActionResource<>(ListAction.PLAYER_ADDED, data, msg);
    }

    public static <T> AddPlayersActionResource<T> playerDeleted(@Nullable T data, @Nullable String msg) {
        return new AddPlayersActionResource<>(ListAction.PLAYER_DELETED, data, msg);
    }

    public static <T> AddPlayersActionResource<T> playerCheckboxToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersActionResource<>(ListAction.PLAYER_CHECKBOX_TOGGLED, data, msg);
    }

    public static <T> AddPlayersActionResource<T> checkboxButtonToggled(@Nullable T data, @Nullable String msg) {
        return new AddPlayersActionResource<>(ListAction.CHECKBOX_BUTTON_TOGGLED, data, msg);
    }

    public static <T> AddPlayersActionResource<T> showMessage(@Nullable T data, @Nullable String msg) {
        return new AddPlayersActionResource<>(ListAction.SHOW_MSG, data, msg);
    }

    public static AddPlayersActionResource<Integer> noAction() {
        return new AddPlayersActionResource<>(ListAction.NO_ACTION, null, null);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != AddPlayersActionResource.class){
            return false;
        }

        AddPlayersActionResource<T> resource = (AddPlayersActionResource) obj;

        if(resource.status != this.status){
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