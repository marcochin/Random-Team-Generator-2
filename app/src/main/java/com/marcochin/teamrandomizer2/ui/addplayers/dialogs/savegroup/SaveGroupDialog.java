package com.marcochin.teamrandomizer2.ui.addplayers.dialogs.savegroup;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marcochin.teamrandomizer2.R;
import com.marcochin.teamrandomizer2.ui.UIAction;
import com.marcochin.teamrandomizer2.ui.custom.dialogs.NoTitleDialogFragment;

public class SaveGroupDialog extends NoTitleDialogFragment implements View.OnClickListener {
    public static final String TAG = SaveGroupDialog.class.getSimpleName();

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mGroupNameEditText;

    private SaveGroupViewModel mViewModel;

    private OnSaveGroupNameListener mOnSaveGroupNameListener;

    public interface OnSaveGroupNameListener {
        void onSaveGroupNameClicked(String groupName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_generic_edit_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextInputLayout = view.findViewById(R.id.dget_text_input_layout);
        mGroupNameEditText = view.findViewById(R.id.dget_edit_text);
        Button cancelButton = view.findViewById(R.id.dget_cancel_btn);
        Button positiveButton = view.findViewById(R.id.dget_positive_btn);

        cancelButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(SaveGroupViewModel.class);
        observeLiveData();

        setupEditText(mGroupNameEditText);
        positiveButton.setText(R.string.dsg_positive_btn);
        mTextInputLayout.setHint(getString(R.string.dsg_hint));
    }

    private void setupEditText(TextInputEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean hardKeyboardEnterPress = event != null
                        && event.getAction() == KeyEvent.ACTION_UP
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);

                if (actionId == EditorInfo.IME_ACTION_DONE || hardKeyboardEnterPress) {
                    onPositiveButtonClick();
                }
                // Return true if you want to keep the keyboard open after hitting the enter button
                return true;
            }
        });

        // Some phones focus editText automatically, some don't.
        // Add this here for the phones that don't
        editText.requestFocus();

        // Some phones show the keyboard automatically when editText is programmatically focused. Some don't.
        // Show soft keyboard here for the phones that don't.
        if(getDialog() != null && getDialog().getWindow() != null){
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private void observeLiveData() {
        mViewModel.getActionLiveData().observe(this, new Observer<UIAction<Integer>>() {
            @Override
            public void onChanged(UIAction<Integer> saveGroupAction) {
                if (saveGroupAction == null) {
                    return;
                }

                switch (saveGroupAction.action) {
                    case SaveGroupAction.GROUP_VALIDATED:
                        handleGroupValidatedAction(saveGroupAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case SaveGroupAction.SHOW_MSG:
                        handleShowMessageAction(saveGroupAction);
                        mViewModel.clearActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dget_positive_btn:
                onPositiveButtonClick();
                break;

            case R.id.dget_cancel_btn:
                dismiss();
                break;
        }
    }

    private void onPositiveButtonClick(){
        if (mGroupNameEditText.getText() != null) {
            mViewModel.validateGroupName(mGroupNameEditText.getText().toString());
        }
    }

    private void handleGroupValidatedAction(UIAction<Integer> saveGroupAction) {
        if(mOnSaveGroupNameListener != null && mGroupNameEditText.getText() != null){
            mOnSaveGroupNameListener.onSaveGroupNameClicked(mGroupNameEditText.getText().toString());
            dismiss();
        }
    }

    private void handleShowMessageAction(UIAction<Integer> saveGroupAction) {
        mTextInputLayout.setError(saveGroupAction.message);
    }

    public void setOnSaveGroupNameListener(OnSaveGroupNameListener onSaveGroupNameListener){
        mOnSaveGroupNameListener = onSaveGroupNameListener;
    }
}
