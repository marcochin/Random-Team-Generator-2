package com.marcochin.teamrandomizer2.ui.addplayers.dialogs.numberofteams;

import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer2.ui.UIAction;

class NumberOfTeamsAction<T> extends UIAction<T> {
    public static final int TEAMS_VALIDATED = 0;

    public NumberOfTeamsAction(int action) {
        this(action, null, null);
    }

    public NumberOfTeamsAction(int action, @Nullable T data) {
        this(action, data, null);
    }

    public NumberOfTeamsAction(int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> NumberOfTeamsAction<T> teamsValidated(@Nullable T data, @Nullable String msg) {
        return new NumberOfTeamsAction<>(TEAMS_VALIDATED, data, msg);
    }
}
