package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersFragment;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.editgroupname.EditGroupNameDialog;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.savegroup.SaveGroupDialog;
import com.marcochin.teamrandomizer.ui.loadgroup.LoadGroupFragment;

public class MainActivity extends AppCompatActivity implements SaveGroupDialog.GroupNameReceiver,
        EditGroupNameDialog.GroupNameReceiver, LoadGroupFragment.OnActionReceiver {

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

    // SaveGroupDialog.GroupNameReceiver
    @Override
    public void onReceiveNameFromSaveGroupDialog(String groupName) {
        mAddPlayersFragment.saveGroup(groupName);
    }

    // EditGroupNameDialog.GroupNameReceiver
    @Override
    public void onReceiveNameFromEditGroupNameDialog(String groupName) {
        mAddPlayersFragment.setGroupName(groupName);
    }

    // LoadGroupFragment.OnActionReceiver
    @Override
    public void onNewGroupRequested() {
        mAddPlayersFragment.startNewGroup();
        mBottomNavigationView.setSelectedItemId(R.id.addPlayersFragment);
    }

    // LoadGroupFragment.OnActionReceiver
    @Override
    public void onGroupSelected(Group group) {
        mAddPlayersFragment.setGroup(group);
        mBottomNavigationView.setSelectedItemId(R.id.addPlayersFragment);
    }

    // LoadGroupFragment.OnActionReceiver
    @Override
    public void onGroupDeleted(int deletedGroupId) {
        mAddPlayersFragment.syncGroupDeletion(deletedGroupId);
    }
}
