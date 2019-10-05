package com.marcochin.teamrandomizer2.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UIAction<T> {
    public static final int SHOW_DIALOG = 100;
    public static final int SHOW_MSG = 101;

    @NonNull
    public final int action;

    @Nullable
    public final T data;

    @Nullable
    public final String message;
    
    public UIAction(@NonNull int action) {
        this(action, null, null);
    }

    public UIAction(@NonNull int action, @Nullable T data) {
        this(action, data, null);
    }

    public UIAction(@NonNull int action, @Nullable T data, @Nullable String message) {
        this.action = action;
        this.data = data;
        this.message = message;
    }

    public static <T> UIAction<T> showDialog(@NonNull T data, @Nullable String message) {
        return new UIAction<>(SHOW_DIALOG, data, message);
    }

    public static <T> UIAction<T> showMessage( @Nullable T data, @Nullable String message) {
        return new UIAction<>(SHOW_MSG, data, message);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass() || obj.getClass() != UIAction.class){
            return false;
        }

        UIAction<T> action = (UIAction) obj;

        if(action.action != this.action){
            return false;
        }

        if(this.data != null){
            if(action.data != this.data){
                return false;
            }
        }

        if(action.message != null){
            if(this.message == null){
                return false;
            }
            if(!action.message.equals(this.message)){
                return false;
            }
        }

        return true;
    }
}
