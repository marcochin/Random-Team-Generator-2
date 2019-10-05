package com.marcochin.teamrandomizer.ui.loadgroup.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.ui.custom.dialogs.NoTitleDialogFragment;

public class DeleteGroupDialog extends NoTitleDialogFragment implements View.OnClickListener {
    public static final String TAG = DeleteGroupDialog.class.getSimpleName();
    public static final String BUNDLE_KEY_GROUP_ID = "group_id";
    public static final String BUNDLE_KEY_GROUP_POSITION = "group_position";

    private OnDeleteGroupListener mOnDeleteGroupListener;

    private int mDeleteGroupId;
    private int mDeleteGroupPosition;

    public interface OnDeleteGroupListener {
        void onDeleteGroupClicked(int groupId, int position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_generic_text_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dialogText = view.findViewById(R.id.dgtv_text);
        Button cancelButton = view.findViewById(R.id.dgtv_cancel_btn);
        Button positiveButton = view.findViewById(R.id.dgtv_positive_btn);

        cancelButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);

        dialogText.setText(R.string.ddg_text);
        positiveButton.setText(R.string.ddg_positive_btn);

        setupArguments();
    }

    private void setupArguments(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            mDeleteGroupId = getArguments().getInt(BUNDLE_KEY_GROUP_ID);
            mDeleteGroupPosition = getArguments().getInt(BUNDLE_KEY_GROUP_POSITION);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dgtv_cancel_btn:
                dismiss();
                break;

            case R.id.dgtv_positive_btn:
                if(mOnDeleteGroupListener != null){
                    mOnDeleteGroupListener.onDeleteGroupClicked(mDeleteGroupId, mDeleteGroupPosition);
                }
                dismiss();
                break;
        }
    }

    public void setOnDeleteGroupListener(OnDeleteGroupListener onDeleteGroupListener){
        mOnDeleteGroupListener = onDeleteGroupListener;
    }
}
