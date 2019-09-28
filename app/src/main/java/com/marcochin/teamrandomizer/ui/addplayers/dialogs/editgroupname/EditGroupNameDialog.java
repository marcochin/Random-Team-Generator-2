package com.marcochin.teamrandomizer.ui.addplayers.dialogs.editgroupname;

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
import com.marcochin.teamrandomizer.ui.UIAction;

public class EditGroupNameDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = EditGroupNameDialog.class.getSimpleName();
    public static final String BUNDLE_KEY_GROUP_NAME = "group_name";

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mGroupNameEditText;
    private String mCurrentGroupName;

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
        Button positiveButton = view.findViewById(R.id.degn_positive_btn);

        cancelButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(EditGroupNameViewModel.class);
        observeLiveData();

        setupArguments(); // Make this the first setup as other setups might depend on it
        setupEditText(mGroupNameEditText);
    }

    private void setupArguments(){
        Bundle bundle = getArguments();
        if(bundle != null){
            mCurrentGroupName = bundle.getString(BUNDLE_KEY_GROUP_NAME);
        }
    }

    private void setupEditText(TextInputEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onPositiveButtonClick();
                }
                // Return true if you want to keep the keyboard open after hitting the enter button
                return true;
            }
        });

        editText.setText(mCurrentGroupName);
        editText.setSelectAllOnFocus(true); // highlights the entire edit on focus

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
            public void onChanged(UIAction<Integer> editGroupNameAction) {
                if (editGroupNameAction == null) {
                    return;
                }

                switch (editGroupNameAction.action) {
                    case EditGroupNameAction.GROUP_VALIDATED:
                        handleGroupValidatedAction(editGroupNameAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case EditGroupNameAction.SHOW_MSG:
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
            case R.id.degn_positive_btn:
                onPositiveButtonClick();
                break;

            case R.id.degn_cancel_btn:
                dismiss();
                break;
        }
    }

    private void onPositiveButtonClick(){
        if (mGroupNameEditText.getText() != null) {
            mViewModel.validateGroupName(mGroupNameEditText.getText().toString());
        }
    }

    private void handleGroupValidatedAction(UIAction<Integer> editGroupNameAction) {
        if(mGroupNameReceiver != null && mGroupNameEditText.getText() != null){
            mGroupNameReceiver.onReceiveNameFromEditGroupNameDialog(mGroupNameEditText.getText().toString());
            dismiss();
        }
    }

    private void handleShowMessageAction(UIAction<Integer> editGroupNameAction) {
        mTextInputLayout.setError(editGroupNameAction.message);
    }
}