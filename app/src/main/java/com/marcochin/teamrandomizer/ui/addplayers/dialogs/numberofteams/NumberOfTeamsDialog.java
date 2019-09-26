package com.marcochin.teamrandomizer.ui.addplayers.dialogs.numberofteams;

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
import com.marcochin.teamrandomizer.model.Player;

import java.util.ArrayList;

public class NumberOfTeamsDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = NumberOfTeamsDialog.class.getSimpleName();
    public static final String BUNDLE_KEY_PLAYERS_LIST = "players_list";

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mNumberOfTeamsEditText;
    private TextView mTotalPlayerText;

    private NumberOfTeamsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_number_of_teams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextInputLayout = view.findViewById(R.id.dnot_number_of_teams_input_layout);
        mNumberOfTeamsEditText = view.findViewById(R.id.dnot_number_of_teams_edit_text);
        mTotalPlayerText = view.findViewById(R.id.dnot_total_players_text);
        Button cancelButton = view.findViewById(R.id.dnot_cancel_btn);
        Button positiveButton = view.findViewById(R.id.dnot_positive_btn);

        cancelButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);

        // Retrieve the viewModel
        // We don't need to inject our view model with anything so we don't need the factory
        mViewModel = ViewModelProviders.of(this).get(NumberOfTeamsViewModel.class);
        observeLiveData();

        setupArguments();
        setupEditText(mNumberOfTeamsEditText);
    }

    private void setupArguments(){
        Bundle bundle = getArguments();
        if(bundle != null){
            ArrayList<Player> playerList = bundle.getParcelableArrayList(BUNDLE_KEY_PLAYERS_LIST);

            if(playerList != null) {
                mViewModel.setPlayerList(playerList);
            }
        }
    }

    private void setupEditText(TextInputEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
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

    private void observeLiveData(){
        mViewModel.getPlayerListLiveData().observe(this, new Observer<ArrayList<Player>>() {
            @Override
            public void onChanged(ArrayList<Player> players) {
                if(players != null) {
                    mTotalPlayerText.setText(getString(R.string.ph_total_players, Integer.toString(players.size())));
                }
            }
        });

        mViewModel.getActionLiveData().observe(this, new Observer<NumberOfTeamsAction<Integer>>() {
            @Override
            public void onChanged(NumberOfTeamsAction<Integer> numberOfTeamsAction) {
                if (numberOfTeamsAction == null) {
                    return;
                }

                switch (numberOfTeamsAction.action) {
                    case TEAMS_VALIDATED:
                        handleTeamsValidatedAction(numberOfTeamsAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case SHOW_MSG:
                        handleShowMessageAction(numberOfTeamsAction);
                        mViewModel.clearActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dnot_positive_btn:
                onPositiveButtonClick();
                break;

            case R.id.dnot_cancel_btn:
                dismiss();
                break;
        }
    }

    private void onPositiveButtonClick(){
        if (mNumberOfTeamsEditText.getText() != null) {
            mViewModel.validateNumberOfTeams(mNumberOfTeamsEditText.getText().toString());
        }
    }


    private void handleTeamsValidatedAction(NumberOfTeamsAction<Integer> numberOfTeamsAction) {
        // Start Randomize Activity
    }

    private void handleShowMessageAction(NumberOfTeamsAction<Integer> numberOfTeamsAction) {
        mTextInputLayout.setError(numberOfTeamsAction.message);
    }
}
