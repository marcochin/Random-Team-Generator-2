package com.marcochin.teamrandomizer.ui.addplayers;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.PlayerListAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener {
    private static final String TAG = "AddPlayersFragment";

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

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

        mTopConstraint = view.findViewById(R.id.top_constraint);
        mGroupNameText = view.findViewById(R.id.group_name_text);
        mNameEditText = view.findViewById(R.id.name_edit_text);
        mNumPlayersText = view.findViewById(R.id.total_players_text);
        mRecyclerView = view.findViewById(R.id.players_recycler_view);
        Button addButton = view.findViewById(R.id.add_btn);
        Button clearButton = view.findViewById(R.id.clear_btn);
        ImageButton checkboxButton = view.findViewById(R.id.checkbox_btn);

        addButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        checkboxButton.setOnClickListener(this);

        setupRecyclerView(mRecyclerView);

        // Retrieve the viewModel
        mViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(AddPlayersViewModel.class);
        observeLiveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Won't get called when a fragment is hidden, but will get called when user resumes app from bg
        // even if fragment if hidden.
        addKeyboardLayoutListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Won't get called when a fragment is hidden, but will get called when user hits home
        // button etc even if fragment if hidden.
        removeKeyboardLayoutListener();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // onHiddenChanged will only get triggered when the fragment is hidden (switching tabs).
        // It will not get triggered when user press home button, back button etc.
        if(hidden){
            removeKeyboardLayoutListener();
        }else{
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
        mLinearLayoutManager = new LinearLayoutManager(getActivity()) {
        };
        recyclerView.setLayoutManager(mLinearLayoutManager);

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

    private void onKeyboardVisibilityChanged(boolean opened) {
//        Log.d(TAG, "onKeyboardVisibilityChanged : " + opened);
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)mTopConstraint.getLayoutParams();

        if(opened){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int[] pushedUpCoords = new int[2];
                    mTopConstraint.getLocationOnScreen(pushedUpCoords);

                    // Calculate the position difference set on the layout params
                    layoutParams.topMargin = mTopConstraintOriginalCoords[1] - pushedUpCoords[1];
                    mTopConstraint.setLayoutParams(layoutParams);

                }
            }, 250);
        }else{
            layoutParams.topMargin = 0;
            mTopConstraint.setLayoutParams(layoutParams);
        }
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

        mViewModel.getListActionLiveData().observe(this, new Observer<ListActionResource<Integer>>() {
            @Override
            public void onChanged(ListActionResource<Integer> listActionResource) {
                switch (listActionResource.status) {
                    case PLAYER_ADDED:
                        handlePlayerAddedAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case PLAYER_DELETED:
                        handlePlayerDeletedAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case PLAYER_CHECKBOX_TOGGLED:
                        handlePlayerCheckboxToggledAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;

                    case CHECKBOX_BUTTON_TOGGLED:
                        handleCheckboxButtonToggledAction(listActionResource);
                        mViewModel.resetListActionLiveData();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                mViewModel.addPlayer(mNameEditText.getText().toString());
                break;

            case R.id.clear_btn:
                mViewModel.clearAllPlayers();
                break;

            case R.id.checkbox_btn:
                mViewModel.toggleCheckBoxButton();
                break;
        }
    }

    private void handlePlayerAddedAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemInserted(listActionResource.data);

            if (mListAdapter.getItemCount() > 0) {
                mLinearLayoutManager.scrollToPosition(listActionResource.data); // Item pos
            }
            mNameEditText.setText("");
        }
    }

    private void handlePlayerDeletedAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemRemoved(listActionResource.data); // Item pos
        }
    }

    private void handlePlayerCheckboxToggledAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(mListItemAnimator);
            mListAdapter.notifyItemChanged(listActionResource.data); // Item pos
        }
    }

    private void handleCheckboxButtonToggledAction(ListActionResource<Integer> listActionResource) {
        if (listActionResource.data != null) {
            mRecyclerView.setItemAnimator(null);
            mListAdapter.notifyItemRangeChanged(0, listActionResource.data); // playerListSize
        }
    }

    private void addKeyboardLayoutListener(){
        // ContentView is the root view of the layout of this activity/fragment
        if(!mIsKeyboardLayoutListenerAdded && getView() != null){
            getView().getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardLayoutListener);
            mIsKeyboardLayoutListenerAdded = true;
        }
    }

    private void removeKeyboardLayoutListener(){
        if(mIsKeyboardLayoutListenerAdded && getView() != null){
            getView().getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardLayoutListener);
            mIsKeyboardLayoutListenerAdded = false;
        }
    }

    // Anonymous Inner Classes
    /**
     * Basically the soft keyboard pushes our layout up with adjustPan so we need to push it back
     * down when the keyboard is showing. This is a way to detect if the keyboard is showing or not.
     * https://stackoverflow.com/a/26964010/5673746
     * */
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
