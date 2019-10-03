package com.marcochin.teamrandomizer.ui.addplayers;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.ui.UIAction;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.AddPlayersListAdapter;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.clearlist.ClearListDialog;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.editgroupname.EditGroupNameDialog;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.numberofteams.NumberOfTeamsDialog;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.savegroup.SaveGroupDialog;
import com.marcochin.teamrandomizer.ui.custom.NestedCoordinatorLayout;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener{

    private static final String TAG = AddPlayersFragment.class.getSimpleName();

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private NestedCoordinatorLayout mNestedCoordinatorLayout;
    private Space mTopConstraint;
    private TextView mGroupNameText;
    private TextView mNumPlayersText;
    private EditText mNameEditText;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AddPlayersListAdapter mListAdapter;
    private RecyclerView.ItemAnimator mListItemAnimator;

    private AddPlayersViewModel mViewModel;


    /** Used to determine where to show Snackbar */
    private boolean mIsKeyboardShowing;
    private int[] mTopConstraintOriginalCoords;

    /**
     * When we show a dialog fragment with a soft keyboard on top of THIS fragment and want to
     * show a snackbar in THIS fragment after the dialog closes, it might give a false positive that
     * the keyboard is showing. Thus the snackbar might popup in the wrong location.
     * The keyboard showing is technically correct, but in the wrong context. Since it's a
     * DialogFragment on top of a fragment, onPause won't get called cause they are both in the same
     * Activity and so LiveData won't be considered inactive.
     * */
    private boolean mIsKeyboardShowingFalsePositive;

    /**
     * There is a edge case in which multiple KeyboardLayoutListeners can be add at the same time.
     * This can happen when a user resumes tha app which will trigger the onResume method of all fragments
     * hidden or not. If the user resumed to the LoadFragment and changes to the AddPlayersFragment
     * the onHiddenChanged(true) will get called and that can add another listener. So we need this
     * to keep track if it was already added or not.
     */
    private boolean mIsKeyboardLayoutListenerAdded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNestedCoordinatorLayout = view.findViewById(R.id.fap_nested_coordinator_layout);
        mTopConstraint = view.findViewById(R.id.fap_top_constraint);
        mGroupNameText = view.findViewById(R.id.fap_group_name_text);
        mNameEditText = view.findViewById(R.id.fap_name_edit_text);
        mNumPlayersText = view.findViewById(R.id.fap_total_players_text);
        mRecyclerView = view.findViewById(R.id.fap_recycler_view);
        Button randomizeButton = view.findViewById(R.id.fap_randomize_btn);
        Button addButton = view.findViewById(R.id.fap_add_btn);
        Button clearButton = view.findViewById(R.id.fap_clear_btn);
        ImageButton saveButton = view.findViewById(R.id.fap_save_btn);
        ImageButton checkboxModeButton = view.findViewById(R.id.fap_checkbox_mode_btn);

        mGroupNameText.setOnClickListener(this);
        randomizeButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        checkboxModeButton.setOnClickListener(this);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();

        setupRecyclerView(mRecyclerView);
        setupEditText(mNameEditText);

        // We load the last opened group for convenience.
        loadLastOpenedGroup();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Lifecycle method won't get called when a fragment is hidden, but will get called when user
        // hits home button etc even if fragment if hidden.

        addKeyboardLayoutListener();
    }

    @Override
    public void onPause() {
        // Lifecycle method won't get called when a fragment is hidden, but will get called when user
        // hits home button etc even if fragment if hidden.

        removeKeyboardLayoutListener();

        // 1) WHEN THIS ACTIVITY ONLY GETS PUT IN BG:
        // When RandomizeActivity comes in front of the AddPlayersFragment, onPause will get called
        // and calls removeKeyboardLayoutListener(). Therefore our mKeyboardLayoutListener will not
        // get triggered and we have to manually set this to false to sync keyboard state back
        // up or else layout might get messed up.
        // 2) WHEN ENTIRE APP GETS PUT IN BG:
        // The same happens when you move app into the background using the home button. However
        // when this Activity comes back to foreground it's a little bit different.
        // mKeyboardLayoutListener will somehow get triggered and mIsKeyboardShowing will be set to
        // false automatically.
        // NOTE: This is for the first scenario in which mIsKeyboardShowing is not set to false when
        // activity only comes back to foreground.
        onKeyboardVisibilityChanged(false);

        // Call autoSaveGroup before onPause super method is called because LiveData will go into
        // inactive state and our call won't go through. Calling autoSaveGroup in onStop is too late
        // as LiveData will go in inactive state while onPause.
        mViewModel.autoSaveGroup();

        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // onHiddenChanged will only get triggered when the fragment is hidden (switching tabs).
        // It will not get triggered when user press home button, back button etc.
        if (hidden) {
            removeKeyboardLayoutListener();
        } else {
            addKeyboardLayoutListener();
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mListItemAnimator = recyclerView.getItemAnimator();
        recyclerView.setHasFixedSize(true);

        // Set decoration
        if(getActivity() != null && getResources().getBoolean(R.bool.show_recycler_view_decoration)) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }

        // Set adapter
        mListAdapter = new AddPlayersListAdapter();
        recyclerView.setAdapter(mListAdapter);

        // Set LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        // Set RecyclerView OnItemClickListener
        mListAdapter.setOnItemClickListener(new AddPlayersListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Player player) {
                mViewModel.togglePlayerCheckBox(position);
            }

            @Override
            public void onDeleteClick(int position, Player player) {
                mViewModel.deletePlayer(position);
            }
        });
    }

    private void setupEditText(final EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // On a soft keyboard event will be null and so you don't have to worry about ACTION_UP/DOWN
                boolean hardKeyboardEnterPress = event != null
                        && event.getAction() == KeyEvent.ACTION_UP
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);

                if (actionId == EditorInfo.IME_ACTION_DONE || hardKeyboardEnterPress) {
                    onAddPlayerButtonClick();
                }
                // Return true if you want to keep the keyboard open after hitting the enter button
                return true;
            }
        });
    }

    private void loadLastOpenedGroup() {
        mViewModel.loadMostRecentGroup();
    }

    private void observeLiveData() {
        mViewModel.getGroupNameLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mGroupNameText.setText(s);
            }
        });

        mViewModel.getPlayerListLiveData().observe(this, new Observer<List<Player>>() {
            @Override
            public void onChanged(final List<Player> players) {
                // We only use this for initial population of the list and clearing the list.
                // I only do it this way because I want granular callbacks for when I've added an item,
                // deleted an item, etc..
                mListAdapter.submitList(players);

            }
        });

        mViewModel.getTotalPlayersLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mNumPlayersText.setText(getString(R.string.ph_total_players, integer));
            }
        });

        mViewModel.getActionLiveData().observe(this, new Observer<UIAction<Integer>>() {
            @Override
            public void onChanged(UIAction<Integer> addPlayersAction) {
                if (addPlayersAction == null) {
                    return;
                }

                switch (addPlayersAction.action) {
                    case AddPlayersAction.PLAYER_ADDED:
                        handlePlayerAddedAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case AddPlayersAction.PLAYER_DELETED:
                        handlePlayerDeletedAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case AddPlayersAction.PLAYER_CHECKBOX_TOGGLED:
                        handlePlayerCheckboxToggledAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case AddPlayersAction.CHECKBOX_BUTTON_TOGGLED:
                        handleCheckboxButtonToggledAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case AddPlayersAction.SHOW_DIALOG:
                        handleShowDialogAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;

                    case AddPlayersAction.SHOW_MSG:
                        handleShowMessageAction(addPlayersAction);
                        mViewModel.clearActionLiveData();
                        break;
                }
            }
        });
    }

    private void onKeyboardVisibilityChanged(boolean opened) {
//        Log.d(TAG, "onKeyboardVisibilityChanged : " + opened);
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mTopConstraint.getLayoutParams();

        if (opened) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int[] pushedUpCoords = new int[2];
                    mTopConstraint.getLocationOnScreen(pushedUpCoords);

                    // Calculate the topConstraint's original and new position difference and push
                    // the topConstraint back down
                    layoutParams.topMargin = mTopConstraintOriginalCoords[1] - pushedUpCoords[1];
                    mTopConstraint.setLayoutParams(layoutParams);

                }
            }, 250);
        } else {
            layoutParams.topMargin = 0;
            mTopConstraint.setLayoutParams(layoutParams);
        }

        mIsKeyboardShowing = opened;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fap_randomize_btn:
                mViewModel.showNumberOfTeamsDialog();
                break;

            case R.id.fap_add_btn:
                onAddPlayerButtonClick();
                break;

            case R.id.fap_clear_btn:
                mViewModel.showClearListDialog();
                break;

            case R.id.fap_save_btn:
                mViewModel.showSaveDialogOrSaveGroup();
                break;

            case R.id.fap_checkbox_mode_btn:
                mViewModel.toggleCheckBoxMode();
                break;

            case R.id.fap_group_name_text:
                mViewModel.showEditNameDialog();
                break;
        }
    }

    private void onAddPlayerButtonClick() {
        mViewModel.addPlayer(mNameEditText.getText().toString());
    }

    // Handle AddPlayersActions

    private void handlePlayerAddedAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemInserted(addPlayersAction.data);

            // Scroll to the bottom of list for quality of life
            mLinearLayoutManager.scrollToPosition(addPlayersAction.data); // data = item pos
            mNameEditText.setText("");
        }
    }

    private void handlePlayerDeletedAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemRemoved(addPlayersAction.data); // data = item pos
        }
    }

    private void handlePlayerCheckboxToggledAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemChanged(addPlayersAction.data); // data = item pos
        }
    }

    private void handleCheckboxButtonToggledAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            mRecyclerView.setItemAnimator(null);
            // We don't use notifyDataSetChanged becaue we don't want the recyclerView to flash
            mListAdapter.notifyItemRangeChanged(0, addPlayersAction.data); // data = playerListSize
        }
    }

    private void handleShowDialogAction(UIAction<Integer> addPlayersAction) {
        if (addPlayersAction.data != null) {
            FragmentManager fragmentManager = null;
            DialogFragment dialogFragment = null;
            String fragmentTag = null;

            if(getActivity() != null){
                fragmentManager = getActivity().getSupportFragmentManager();
            }

            switch (addPlayersAction.data) {
                case AddPlayersViewModel.DIALOG_SAVE_GROUP:
                    dialogFragment = new SaveGroupDialog();
                    ((SaveGroupDialog)dialogFragment).setOnSaveGroupNameListener(mOnSaveGroupNameListener);
                    fragmentTag = SaveGroupDialog.TAG;
                    break;

                case AddPlayersViewModel.DIALOG_EDIT_GROUP_NAME: {
                    Bundle bundle = new Bundle();
                    bundle.putString(EditGroupNameDialog.BUNDLE_KEY_GROUP_NAME,
                            mViewModel.getGroupNameLiveData().getValue());

                    dialogFragment = new EditGroupNameDialog();
                    dialogFragment.setArguments(bundle);
                    ((EditGroupNameDialog)dialogFragment).setOnEditGroupNameListener(mOnEditGroupNameListener);
                    fragmentTag = EditGroupNameDialog.TAG;
                    break;
                }

                case AddPlayersViewModel.DIALOG_NUMBER_OF_TEAMS: {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(NumberOfTeamsDialog.BUNDLE_KEY_PLAYERS_LIST,
                            mViewModel.getIncludedPlayersList());

                    dialogFragment = new NumberOfTeamsDialog();
                    dialogFragment.setArguments(bundle);
                    fragmentTag = NumberOfTeamsDialog.TAG;
                    break;
                }

                case AddPlayersViewModel.DIALOG_CLEAR_LIST: {
                    dialogFragment = new ClearListDialog();
                    ((ClearListDialog)dialogFragment).setOnClearListClickedListener(mOnClearListClickedListener);
                    fragmentTag = ClearListDialog.TAG;
                    break;
                }
            }

            if(fragmentManager != null && dialogFragment != null){
                dialogFragment.show(fragmentManager, fragmentTag);
            }
        }
    }

    private void handleShowMessageAction(UIAction<Integer> addPlayersAction) {
        showSnackbar(addPlayersAction.message);
    }


    // Utility

    private void showSnackbar(String message) {
        View parentView;

        if (mIsKeyboardShowing && !mIsKeyboardShowingFalsePositive) {
            parentView = mNestedCoordinatorLayout;

        } else {
            // You don't even have to pass in a coordinator layout to Snackbar as long as it's a
            // view inside a coordinator layout. Snackbar will automatically check if the view
            // passed in has a CoordinatorLayout in it's parent hierarchy.  This will find the
            // activity's CoordinatorLayout.
            parentView = mNameEditText;
            mIsKeyboardShowingFalsePositive = false;
        }
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
    }


    // Manage Listeners

    private void addKeyboardLayoutListener() {
        // ContentView is the root view of the layout of this activity/fragment
        if (!mIsKeyboardLayoutListenerAdded && getView() != null) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardLayoutListener);
            mIsKeyboardLayoutListenerAdded = true;
        }
    }

    private void removeKeyboardLayoutListener() {
        if (mIsKeyboardLayoutListenerAdded && getView() != null) {
            getView().getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardLayoutListener);
            mIsKeyboardLayoutListenerAdded = false;
        }
    }


    // Methods needed to communicate with LoadGroupFragment

    public void startNewGroup(){
        mViewModel.startNewGroup();
    }

    public void setGroup(Group group){
        mViewModel.setGroup(group, true);
    }

    public void syncGroupDeletion(int deletedGroupId){
        mViewModel.syncGroupDeletion(deletedGroupId);
    }


    // Anonymous Inner Classes

    private SaveGroupDialog.OnSaveGroupNameListener mOnSaveGroupNameListener = new SaveGroupDialog.OnSaveGroupNameListener() {
        @Override
        public void onSaveGroupNameClicked(String groupName) {
            // We set this to true here because the user needs to use the keyboard to enter the group name.
            // This is for the snackbar.
            mIsKeyboardShowingFalsePositive = true;
            mViewModel.saveGroup(groupName);
        }
    };

    private EditGroupNameDialog.OnEditGroupNameListener mOnEditGroupNameListener = new EditGroupNameDialog.OnEditGroupNameListener() {
        @Override
        public void onEditGroupNameClicked(String groupName) {
            // We set this to true here because the user needs to use the keyboard to enter the group name.
            // This is for the snackbar.
            mIsKeyboardShowingFalsePositive = true;
            mViewModel.updateGroupName(groupName);
        }
    };

    private ClearListDialog.OnClearListClickedListener mOnClearListClickedListener = new ClearListDialog.OnClearListClickedListener() {
        @Override
        public void onClearListClicked() {
            mViewModel.clearAllPlayers();
        }
    };

    /**
     * Basically the soft keyboard pushes our layout up with adjustPan so we need to push it back
     * down when the keyboard is showing. This is a way to detect if the keyboard is showing or not.
     * https://stackoverflow.com/a/26964010/5673746
     */
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Save the original coordinates of the topConstraint for so we can find the
            // position difference when it gets pushed up and adjust it back down by the
            // same amount.
            if (mTopConstraintOriginalCoords == null) {
                mTopConstraintOriginalCoords = new int[2];
                mTopConstraint.getLocationOnScreen(mTopConstraintOriginalCoords);
            }

            if (getView() != null) {
                Rect r = new Rect();
                getView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getView().getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // If keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
//                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // Keyboard is opened
                    if (!mIsKeyboardShowing) {
                        onKeyboardVisibilityChanged(true);
                    }
                } else {
                    // Keyboard is closed
                    if (mIsKeyboardShowing) {
                        onKeyboardVisibilityChanged(false);
                    }
                }
            }
        }
    };
}
