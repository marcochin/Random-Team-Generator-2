package com.marcochin.teamrandomizer.ui.addplayers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListActionResource<T> {

    @NonNull
    public final ListAction status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum ListAction {
        PLAYER_ADDED, PLAYER_DELETED, PLAYER_CHECKBOX_TOGGLED, CHECKBOX_BUTTON_TOGGLED, NO_ACTION
    }

    public ListActionResource(@NonNull ListAction status) {
        this(status, null, null);
    }

    public ListActionResource(@NonNull ListAction status, @Nullable T data) {
        this(status, data, null);
    }

    public ListActionResource(@NonNull ListAction status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ListActionResource<T> playerAdded(@NonNull T data, @Nullable String msg) {
        return new ListActionResource<>(ListAction.PLAYER_ADDED, data, msg);
    }

    public static <T> ListActionResource<T> playerDeleted( @Nullable T data, @Nullable String msg) {
        return new ListActionResource<>(ListAction.PLAYER_DELETED, data, msg);
    }

    public static <T> ListActionResource<T> playerCheckboxToggled(@Nullable T data, @Nullable String msg) {
        return new ListActionResource<>(ListAction.PLAYER_CHECKBOX_TOGGLED, data, msg);
    }

    public static <T> ListActionResource<T> checkboxButtonToggled(@Nullable T data, @Nullable String msg) {
        return new ListActionResource<>(ListAction.CHECKBOX_BUTTON_TOGGLED, data, msg);
    }

    public static <T> ListActionResource<T> noAction(@Nullable T data, @Nullable String msg) {
        return new ListActionResource<>(ListAction.NO_ACTION, data, msg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != ListActionResource.class){
            return false;
        }

        ListActionResource<T> resource = (ListActionResource) obj;

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
