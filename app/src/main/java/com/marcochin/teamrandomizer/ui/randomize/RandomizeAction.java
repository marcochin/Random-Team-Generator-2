package com.marcochin.teamrandomizer.ui.randomize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer.ui.UIAction;

public class RandomizeAction<T> extends UIAction<T> {
    public static final int CHANGE_RANDOMIZE_BUTTON_VISIBILITY = 0;

    public RandomizeAction(@NonNull int action) {
        super(action);
    }

    public RandomizeAction(@NonNull int action, @Nullable T data) {
        super(action, data);
    }

    public RandomizeAction(@NonNull int action, @Nullable T data, @Nullable String message) {
        super(action, data, message);
    }

    public static <T> RandomizeAction<T> changeRandomizeButtonVisiblity(@Nullable T data, @Nullable String msg) {
        return new RandomizeAction<>(CHANGE_RANDOMIZE_BUTTON_VISIBILITY, data, msg);
    }
}
