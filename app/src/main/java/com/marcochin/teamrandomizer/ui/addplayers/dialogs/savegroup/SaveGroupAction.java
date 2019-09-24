package com.marcochin.teamrandomizer.ui.addplayers.dialogs.savegroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class SaveGroupAction<T> {

    @NonNull
    public final SaveGroupActionType action;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum SaveGroupActionType {
        GROUP_VALIDATED, SHOW_MSG
    }

    public SaveGroupAction(@NonNull SaveGroupActionType action) {
        this(action, null, null);
    }

    public SaveGroupAction(@NonNull SaveGroupActionType action, @Nullable T data) {
        this(action, data, null);
    }

    public SaveGroupAction(@NonNull SaveGroupActionType action, @Nullable T data, @Nullable String message) {
        this.action = action;
        this.data = data;
        this.message = message;
    }

    public static <T> SaveGroupAction<T> groupValidated(@Nullable T data, @Nullable String msg) {
        return new SaveGroupAction<>(SaveGroupActionType.GROUP_VALIDATED, data, msg);
    }

    public static <T> SaveGroupAction<T> showMessage(@Nullable T data, @Nullable String msg) {
        return new SaveGroupAction<>(SaveGroupActionType.SHOW_MSG, data, msg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != SaveGroupAction.class){
            return false;
        }

        SaveGroupAction<T> resource = (SaveGroupAction) obj;

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
