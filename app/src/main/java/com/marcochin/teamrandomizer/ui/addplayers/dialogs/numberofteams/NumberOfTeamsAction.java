package com.marcochin.teamrandomizer.ui.addplayers.dialogs.numberofteams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class NumberOfTeamsAction<T> {
    @NonNull
    public final NumberOfTeamsActionType action;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum NumberOfTeamsActionType {
        TEAMS_VALIDATED, SHOW_MSG
    }

    public NumberOfTeamsAction(@NonNull NumberOfTeamsActionType action) {
        this(action, null, null);
    }

    public NumberOfTeamsAction(@NonNull NumberOfTeamsActionType action, @Nullable T data) {
        this(action, data, null);
    }

    public NumberOfTeamsAction(@NonNull NumberOfTeamsActionType action, @Nullable T data, @Nullable String message) {
        this.action = action;
        this.data = data;
        this.message = message;
    }

    public static <T> NumberOfTeamsAction<T> teamsValidated(@Nullable T data, @Nullable String msg) {
        return new NumberOfTeamsAction<>(NumberOfTeamsActionType.TEAMS_VALIDATED, data, msg);
    }

    public static <T> NumberOfTeamsAction<T> showMessage(@Nullable T data, @Nullable String msg) {
        return new NumberOfTeamsAction<>(NumberOfTeamsActionType.SHOW_MSG, data, msg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != NumberOfTeamsAction.class){
            return false;
        }

        NumberOfTeamsAction<T> resource = (NumberOfTeamsAction) obj;

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
