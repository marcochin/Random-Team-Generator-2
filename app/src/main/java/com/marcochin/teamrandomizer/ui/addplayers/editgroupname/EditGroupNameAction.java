package com.marcochin.teamrandomizer.ui.addplayers.editgroupname;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditGroupNameAction<T> {

    @NonNull
    public final EditGroupNameActionType action;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum EditGroupNameActionType {
        GROUP_VALIDATED, SHOW_MSG
    }

    public EditGroupNameAction(@NonNull EditGroupNameActionType action) {
        this(action, null, null);
    }

    public EditGroupNameAction(@NonNull EditGroupNameActionType action, @Nullable T data) {
        this(action, data, null);
    }

    public EditGroupNameAction(@NonNull EditGroupNameActionType action, @Nullable T data, @Nullable String message) {
        this.action = action;
        this.data = data;
        this.message = message;
    }

    public static <T> EditGroupNameAction<T> groupValidated(@Nullable T data, @Nullable String msg) {
        return new EditGroupNameAction<>(EditGroupNameAction.EditGroupNameActionType.GROUP_VALIDATED, data, msg);
    }

    public static <T> EditGroupNameAction<T> showMessage(@Nullable T data, @Nullable String msg) {
        return new EditGroupNameAction<>(EditGroupNameAction.EditGroupNameActionType.SHOW_MSG, data, msg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != EditGroupNameAction.class){
            return false;
        }

        EditGroupNameAction<T> resource = (EditGroupNameAction) obj;

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