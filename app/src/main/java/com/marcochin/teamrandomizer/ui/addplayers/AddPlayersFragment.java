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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.PlayerListAdapter;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.SaveGroupDialog;
import com.marcochin.teamrandomizer.ui.customclasses.NestedCoordinatorLayout;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener {
    private static final String TAG = "AddPlayersFragment";

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private NestedCoordinatorLayout mNestedCoordinatorLayout;
    private Space mTopConstraint;
    private TextView mGroupNameText;
    private TextView mNumPlayersText;
    private EditText mNameEditText;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlayerListAdapter mListAdapter;
    private RecyclerView.ItemAnimator mListItemAnimator;
    private AddPlayersViewModel mViewModel;

    private boolean mIsKeyboardShowing;
    private int[] mTopConstraintOriginalCoords;

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
        mRecyclerView = view.findViewById(R.id.fap_players_recycler_view);
        Button addButton = view.findViewById(R.id.fap_add_btn);
        Button clearButton = view.findViewById(R.id.fap_clear_btn);
        ImageButton saveButton = view.findViewById(R.id.fap_save_btn);
        ImageButton checkboxButton = view.findViewById(R.id.fap_checkbox_btn);

        addButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        checkboxButton.setOnClickListener(this);

        setupRecyclerView(mRecyclerView);
        setupEditText(mNameEditText);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();
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
        // Call autoSaveGroup before onPause super method just as a precaution before LiveData inactive state
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

        // Set adapter
        mListAdapter = new PlayerListAdapter();
        recyclerView.setAdapter(mListAdapter);

        // Set LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        // Set RecyclerView OnItemClickListener
        mListAdapter.setOnItemClickListener(new PlayerListAdapter.OnItemClickListener() {
            @Override
            public void onCheckboxClick(int position, Player player) {
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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
                mNumPlayersText.setText(getString(R.string.ph_total_players, integer.toString()));
            }
        });

        mViewModel.getAddPlayersActionLiveData().observe(this, new Observer<AddPlayersActionResource<Integer>>() {
            @Override
            public void onChanged(AddPlayersActionResource<Integer> addPlayersActionResource) {
                if (addPlayersActionResource == null) {
                    return;
                }

                switch (addPlayersActionResource.status) {
                    case PLAYER_ADDED:
                        handlePlayerAddedAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;

                    case PLAYER_DELETED:
                        handlePlayerDeletedAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;

                    case PLAYER_CHECKBOX_TOGGLED:
                        handlePlayerCheckboxToggledAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;

                    case CHECKBOX_BUTTON_TOGGLED:
                        handleCheckboxButtonToggledAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;

                    case SHOW_SAVE_GROUP_DIALOG:
                        handleShowSaveGroupDialogAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
                        break;

                    case SHOW_MSG:
                        handleShowMessageAction(addPlayersActionResource);
                        mViewModel.clearAddPlayersActionLiveData();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fap_add_btn:
                onAddPlayerButtonClick();
                break;

            case R.id.fap_clear_btn:
                mViewModel.clearAllPlayers();
                break;

            case R.id.fap_save_btn:
                mViewModel.showNameDialogOrSaveGroup(mGroupNameText.getText().toString());
                break;

            case R.id.fap_checkbox_btn:
                mViewModel.toggleCheckBoxButton();
                break;
        }
    }

    private void onAddPlayerButtonClick() {
        mViewModel.addPlayer(mNameEditText.getText().toString());
    }

    // Handle AddPlayersActions

    private void handlePlayerAddedAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        if (addPlayersActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemInserted(addPlayersActionResource.data);

            // Scroll to the bottom of list for quality of life
            mLinearLayoutManager.scrollToPosition(addPlayersActionResource.data); // data = item pos
            mNameEditText.setText("");
        }
    }

    private void handlePlayerDeletedAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        if (addPlayersActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemRemoved(addPlayersActionResource.data); // data = item pos
        }
    }

    private void handlePlayerCheckboxToggledAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        if (addPlayersActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemChanged(addPlayersActionResource.data); // data = item pos
        }
    }

    private void handleCheckboxButtonToggledAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        if (addPlayersActionResource.data != null) {
            mRecyclerView.setItemAnimator(null);
            mListAdapter.notifyItemRangeChanged(0, addPlayersActionResource.data); // data = playerListSize
        }
    }

    private void handleShowSaveGroupDialogAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        if (addPlayersActionResource.data != null) {
            switch (addPlayersActionResource.data) {
                case AddPlayersViewModel.DIALOG_SAVE_GROUP:
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        SaveGroupDialog saveGroupDialog = new SaveGroupDialog();
                        saveGroupDialog.show(fragmentManager, SaveGroupDialog.TAG);
                    }
                    break;

                case AddPlayersViewModel.DIALOG_EDIT_GROUP_NAME:
                    break;
            }
        }
    }

    private void handleShowMessageAction(AddPlayersActionResource<Integer> addPlayersActionResource) {
        showSnackbar(addPlayersActionResource.message);
    }


    // Utility

    private void showSnackbar(String message) {
        View parentView;

        if (mIsKeyboardShowing) {
            parentView = mNestedCoordinatorLayout;

        } else {
            // You don't even have to pass in a coordinator layout to Snackbar as long as it's a
            // view inside a coordinator layout. Snackbar will automatically check if the view
            // passed in has a CoordinatorLayout in it's parent hierarchy.  This will find the
            // activity's CoordinatorLayout.
            parentView = mNameEditText;
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


    // Anonymous Inner Classes

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
                        mIsKeyboardShowing = true;
                        onKeyboardVisibilityChanged(true);
                    }
                } else {
                    // Keyboard is closed
                    if (mIsKeyboardShowing) {
                        mIsKeyboardShowing = false;
                        onKeyboardVisibilityChanged(false);
                    }
                }
            }
        }
    };
}
