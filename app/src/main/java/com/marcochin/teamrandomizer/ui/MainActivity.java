package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersFragment;
import com.marcochin.teamrandomizer.ui.loadgroup.LoadGroupFragment;

public class MainActivity extends AppCompatActivity implements LoadGroupFragment.OnActionListener {

    private AddPlayersFragment mAddPlayersFragment;
    private LoadGroupFragment mLoadGroupFragment;

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Ui
        mBottomNavigationView = findViewById(R.id.main_bottom_nav);

        // Find Fragments
        mAddPlayersFragment = (AddPlayersFragment) getSupportFragmentManager().findFragmentById(R.id.addPlayersFragment);
        mLoadGroupFragment = (LoadGroupFragment) getSupportFragmentManager().findFragmentById(R.id.loadFragment);

        // Hide the LoadFragment initially
        getSupportFragmentManager().beginTransaction().hide(mLoadGroupFragment).commit();

        // Setup the BottomNavigationView callback
        setupBottomViewNavigation(mBottomNavigationView);
    }

    private void setupBottomViewNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.addPlayersFragment:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mLoadGroupFragment)
                                .show(mAddPlayersFragment).commit();
                        break;

                    case R.id.loadFragment:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mAddPlayersFragment)
                                .show(mLoadGroupFragment).commit();
                        break;
                }
                return true;
            }
        });
    }


    // Interface overrides
    // LoadGroupFragment.OnActionListener
    @Override
    public void onNewGroupClicked() {
        mAddPlayersFragment.startNewGroup();
        mBottomNavigationView.setSelectedItemId(R.id.addPlayersFragment);
    }

    // LoadGroupFragment.OnActionListener
    @Override
    public void onGroupSelected(Group group) {
        mAddPlayersFragment.setGroup(group);
        mBottomNavigationView.setSelectedItemId(R.id.addPlayersFragment);
    }

    // LoadGroupFragment.OnActionListener
    @Override
    public void onGroupDeleted(int deletedGroupId) {
        mAddPlayersFragment.syncGroupDeletion(deletedGroupId);
    }
}
