package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersFragment;
import com.marcochin.teamrandomizer.ui.addplayers.savegroup.SaveGroupDialog;
import com.marcochin.teamrandomizer.ui.load.LoadFragment;

public class MainActivity extends AppCompatActivity implements SaveGroupDialog.GroupNameReceiver {
    private AddPlayersFragment mAddPlayersFragment;
    private LoadFragment mLoadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Ui
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);

        // Find Fragments
        mAddPlayersFragment = (AddPlayersFragment) getSupportFragmentManager().findFragmentById(R.id.addPlayersFragment);
        mLoadFragment = (LoadFragment) getSupportFragmentManager().findFragmentById(R.id.loadFragment);

        // Hide the LoadFragment initially
        getSupportFragmentManager().beginTransaction().hide(mLoadFragment).commit();

        // Setup the BottomNavigationView callback
        setupBottomViewNavigation(bottomNavigationView);
    }

    private void setupBottomViewNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.addPlayersFragment:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mLoadFragment)
                                .show(mAddPlayersFragment).commit();
                        break;

                    case R.id.loadFragment:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mAddPlayersFragment)
                                .show(mLoadFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onReceiveNameFromSaveGroupDialog(String groupName) {
        mAddPlayersFragment.saveGroup(groupName);
    }
}
