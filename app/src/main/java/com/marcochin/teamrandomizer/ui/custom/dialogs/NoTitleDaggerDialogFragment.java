package com.marcochin.teamrandomizer.ui.custom.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerDialogFragment;

/**
 * Some devices will add a title to the DialogFragment, some won't.
 * This is for the devices that show a title.
 */
public class NoTitleDaggerDialogFragment extends DaggerDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Request a window without a title
        // https://stackoverflow.com/a/15279400/5673746
        if(dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }
}
