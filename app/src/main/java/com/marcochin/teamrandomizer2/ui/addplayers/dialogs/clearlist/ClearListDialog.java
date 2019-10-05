package com.marcochin.teamrandomizer2.ui.addplayers.dialogs.clearlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.marcochin.teamrandomizer2.R;
import com.marcochin.teamrandomizer2.ui.custom.dialogs.NoTitleDialogFragment;

public class ClearListDialog extends NoTitleDialogFragment implements View.OnClickListener {
    public static final String TAG = ClearListDialog.class.getSimpleName();

    private OnClearListClickedListener mOnClearListClickedListener;

    public interface OnClearListClickedListener {
        void onClearListClicked();
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

        dialogText.setText(R.string.dcl_text);
        positiveButton.setText(R.string.dcl_positive_btn);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dgtv_cancel_btn:
                dismiss();
                break;

            case R.id.dgtv_positive_btn:
                if(mOnClearListClickedListener != null) {
                    mOnClearListClickedListener.onClearListClicked();
                }
                dismiss();
                break;
        }
    }

    public void setOnClearListClickedListener(OnClearListClickedListener onClearListClickedListener){
        mOnClearListClickedListener = onClearListClickedListener;
    }
}
