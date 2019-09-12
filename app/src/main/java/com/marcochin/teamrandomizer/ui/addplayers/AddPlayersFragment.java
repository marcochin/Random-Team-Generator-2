package com.marcochin.teamrandomizer.ui.addplayers;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelProviderFactory;
import com.marcochin.teamrandomizer.keyboarddetection.KeyboardHeightObserver;
import com.marcochin.teamrandomizer.keyboarddetection.KeyboardHeightProvider;
import com.marcochin.teamrandomizer.model.Player;
import com.marcochin.teamrandomizer.ui.addplayers.adapters.PlayerListAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class AddPlayersFragment extends DaggerFragment implements View.OnClickListener {

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

    /**
     * See https://github.com/siebeprojects/samples-keyboardheight
     * or https://stackoverflow.com/a/41035914/5673746
     * Basically the soft keyboard pushes our layout up with adjustPan so we need this to calculate
     * the height of the soft keyboard so we can adjust our layout back down to show the
     * necessary information
     */
    private KeyboardHeightProvider mKeyboardHeightProvider;

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

        // Setup KeyBoardHeightProvider
        mKeyboardHeightProvider = new KeyboardHeightProvider(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyboardHeightProvider.setKeyboardHeightObserver(mKeyboardHeightObserver);

        // LIBRARY CREATOR: Make sure to start the keyboard height provider after the onResume
        // of this activity. This is because a popup window must be initialised and attached to
        // the activity root view.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mKeyboardHeightProvider.start();
            }
        }, 200);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mKeyboardHeightProvider.close();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mListItemAnimator = recyclerView.getItemAnimator();
        recyclerView.setHasFixedSize(true);

        // Set adapter
        mListAdapter = new PlayerListAdapter();
        mListAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(mListAdapter);

        // Set layoutManager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
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
//                mViewModel.addPlayer(mNameEditText.getText().toString());
                int[] outLocation = new int[2];
                mTopConstraint.getLocationOnScreen(outLocation);
                Log.d("meme", outLocation[0] + " " + outLocation[1]);
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


    // Anonymous Inner Classes
    private PlayerListAdapter.OnItemClickListener mOnItemClickListener = new PlayerListAdapter.OnItemClickListener() {
        @Override
        public void onCheckboxClick(int position, Player player) {
            mViewModel.togglePlayerCheckBox(position);
        }

        @Override
        public void onDeleteClick(int position, Player player) {
            mViewModel.deletePlayer(position);
        }
    };

    private KeyboardHeightObserver mKeyboardHeightObserver = new KeyboardHeightObserver() {
        @Override
        public void onKeyboardHeightChanged(int height, int orientation) {
//            Log.d("meme", height + "");
//            if(getActivity() != null) {
//                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mTopConstraint.getLayoutParams();
//                layoutParams.topMargin = (int) PxDpConversionUtil.convertPixelsToDp(height, getActivity());
//                mTopConstraint.setLayoutParams(layoutParams);
//            }

//            int[] outLocation = new int[2];
//            mTopConstraint.getLocationOnScreen(outLocation);
//            Log.d("meme", outLocation[0] + " " + outLocation[1]);
        }
    };
}
