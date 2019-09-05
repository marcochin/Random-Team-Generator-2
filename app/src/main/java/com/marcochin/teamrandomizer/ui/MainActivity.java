package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNavController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_randomize:
                    mNavController.navigate(R.id.action_loadFragment_to_addPlayersFragment);
                    break;
                case R.id.nav_load:
                    mNavController.navigate(R.id.action_addPlayersFragment_to_loadFragment);
                    break;
            }

            return true; // Return true if you want the bottom nav button to be selected
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("meme", "onBackPressed");
    }
}
