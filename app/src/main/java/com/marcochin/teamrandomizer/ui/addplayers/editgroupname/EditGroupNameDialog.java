package com.marcochin.teamrandomizer.ui.addplayers.editgroupname;

import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marcochin.teamrandomizer.R;

public class EditGroupNameDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = EditGroupNameDialog.class.getSimpleName();

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mGroupNameEditText;

    private EditGroupNameViewModel mViewModel;

    private GroupNameReceiver mGroupNameReceiver;

    public interface GroupNameReceiver {
        void onReceiveNameFromEditGroupNameDialog(String groupName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof GroupNameReceiver){
            mGroupNameReceiver = (GroupNameReceiver) context;
        }else{
            throw new RuntimeException(
                    context.toString() + " must implement " + GroupNameReceiver.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_group_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextInputLayout = view.findViewById(R.id.degn_group_name_input_layout);
        mGroupNameEditText = view.findViewById(R.id.degn_group_name_edit_text);
        Button cancelButton = view.findViewById(R.id.degn_cancel_btn);
        Button editButton = view.findViewById(R.id.degn_update_btn);

        cancelButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

        setupEditText(mGroupNameEditText);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(EditGroupNameViewModel.class);
        observeLiveData();
    }

    private void setupEditText(TextInputEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onUpdateButtonClick();
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
        mViewModel.getActionLiveData().observe(this, new Observer<EditGroupNameAction<Integer>>() {
            @Override
            public void onChanged(EditGroupNameAction<Integer> editGroupNameAction) {
                if (editGroupNameAction == null) {
                    return;
                }

                switch (editGroupNameAction.action) {
                    case GROUP_VALIDATED:
                        handleGroupValidatedAction(editGroupNameAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case SHOW_MSG:
                        handleShowMessageAction(editGroupNameAction);
                        mViewModel.clearActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.degn_update_btn:
                onUpdateButtonClick();
                break;

            case R.id.degn_cancel_btn:
                dismiss();
                break;
        }
    }

    private void onUpdateButtonClick(){
        if (mGroupNameEditText.getText() != null) {
            mViewModel.validateGroupName(mGroupNameEditText.getText().toString());
        }
    }

    private void handleGroupValidatedAction(EditGroupNameAction<Integer> editGroupNameAction) {
        if(mGroupNameReceiver != null && mGroupNameEditText.getText() != null){
            mGroupNameReceiver.onReceiveNameFromEditGroupNameDialog(mGroupNameEditText.getText().toString());
            dismiss();
        }
    }

    private void handleShowMessageAction(EditGroupNameAction<Integer> editGroupNameAction) {
        mTextInputLayout.setError(editGroupNameAction.message);
    }
}