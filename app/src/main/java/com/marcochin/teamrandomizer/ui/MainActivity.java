package com.marcochin.teamrandomizer.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marcochin.teamrandomizer.R;

public class MainActivity extends AppCompatActivity {

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
}
