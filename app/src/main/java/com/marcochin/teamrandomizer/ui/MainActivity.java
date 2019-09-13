package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;

public class MainActivity extends AppCompatActivity {
    private Fragment mAddPlayersFragment;
    private Fragment mLoadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Ui
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);

        // Find Fragments
        mAddPlayersFragment = getSupportFragmentManager().findFragmentById(R.id.addPlayersFragment);
        mLoadFragment = getSupportFragmentManager().findFragmentById(R.id.loadFragment);

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
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("back", "backpressed");
    }
}
