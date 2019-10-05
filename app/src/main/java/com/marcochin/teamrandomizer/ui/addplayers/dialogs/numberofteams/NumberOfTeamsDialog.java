package com.marcochin.teamrandomizer.ui.addplayers.dialogs.numberofteams;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.persistence.PreferenceConstants;
import com.marcochin.teamrandomizer.ui.UIAction;
import com.marcochin.teamrandomizer.ui.custom.dialogs.NoTitleDaggerDialogFragment;
import com.marcochin.teamrandomizer.ui.randomize.RandomizeActivity;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

public class NumberOfTeamsDialog extends NoTitleDaggerDialogFragment implements View.OnClickListener {
    public static final String TAG = NumberOfTeamsDialog.class.getSimpleName();
    public static final String BUNDLE_KEY_PLAYERS_LIST = "players_list";

    @Inject
    SharedPreferences mSharedPreferences;

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

        setupArguments(); // Make this the first setup as other setups might depend on it
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

        // Try to retrieve previously used numberOfTeams value from prefs
        int numberOfTeams = mSharedPreferences.getInt(PreferenceConstants.PREF_KEY_NUMBER_OF_TEAMS,
                PreferenceConstants.PREF_DEFAULT_NUMBER_OF_TEAMS);
        editText.setText(String.format(Locale.US, "%d", numberOfTeams));

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
                    mTotalPlayerText.setText(getString(R.string.ph_total_players, players.size()));
                }
            }
        });

        mViewModel.getActionLiveData().observe(this, new Observer<UIAction<Integer>>() {
            @Override
            public void onChanged(UIAction<Integer> numberOfTeamsAction) {
                if (numberOfTeamsAction == null) {
                    return;
                }

                switch (numberOfTeamsAction.action) {
                    case NumberOfTeamsAction.TEAMS_VALIDATED:
                        handleTeamsValidatedAction(numberOfTeamsAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case NumberOfTeamsAction.SHOW_MSG:
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


    private void handleTeamsValidatedAction(UIAction<Integer> numberOfTeamsAction) {
        // Start Randomize Activity
        Intent randomizeActivityIntent = new Intent(getActivity(), RandomizeActivity.class);

        // Pass the included players to RandomizeActivity
        randomizeActivityIntent.putParcelableArrayListExtra(RandomizeActivity.BUNDLE_KEY_PLAYER_LIST,
                mViewModel.getPlayerListLiveData().getValue());

        if(numberOfTeamsAction.data != null) {
            // Pass the included the number of teams to RandomizeActivity
            randomizeActivityIntent.putExtra(RandomizeActivity.BUNDLE_KEY_NUMBER_OF_TEAMS,
                    (int) numberOfTeamsAction.data); // data = number of teams

            // Save number of teams in prefs to auto-fill editText next time
            mSharedPreferences.edit().putInt(PreferenceConstants.PREF_KEY_NUMBER_OF_TEAMS, numberOfTeamsAction.data).apply();
        }
        startActivity(randomizeActivityIntent);
        dismiss();
    }

    private void handleShowMessageAction(UIAction<Integer> numberOfTeamsAction) {
        mTextInputLayout.setError(numberOfTeamsAction.message);
    }
}
