package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);

        // Connect bottomNavView with the navController and it will know which fragment to load
        // as long as the bottomNavView menu item ids match the fragment ids in the nav_graph.xml
        NavigationUI.setupWithNavController(bottomNavigationView,
                Navigation.findNavController(this, R.id.main_nav_host_fragment));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("meme", "onBackPressed");
    }
}
