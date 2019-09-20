package com.marcochin.teamrandomizer.ui.addplayers.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersActionResource;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersViewModel;

import javax.inject.Inject;

public class SaveGroupDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG = "SaveGroupDialog";

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;
    private AddPlayersViewModel mViewModel;

    private TextInputLayout mTextInputLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_save_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextInputLayout = view.findViewById(R.id.dsg_group_name_text_input);
        Button cancelButton = view.findViewById(R.id.dsg_cancel_btn);
        Button saveButton = view.findViewById(R.id.dsg_save_btn);
        
        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dsg_save_btn:
                if(mTextInputLayout.getEditText() != null) {
                    mViewModel.saveGroup(mTextInputLayout.getEditText().getText().toString());
                }
                break;
                
            case R.id.dsg_cancel_btn:
                dismiss();
                break;
        }
    }

    private void observeLiveData() {
        mViewModel.getAddPlayersActionLiveData().observe(this, new Observer<AddPlayersActionResource<Integer>>() {
            @Override
            public void onChanged(AddPlayersActionResource<Integer> addPlayersActionResource) {
                if(addPlayersActionResource == null){
                    return;
                }

                switch (addPlayersActionResource.status) {
                    case SHOW_MSG:
                        handleShowMessageAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;
                }
            }
        });
    }

    private void handleShowMessageAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        mTextInputLayout.setError(addPlayersActionResource.message);
    }
}
