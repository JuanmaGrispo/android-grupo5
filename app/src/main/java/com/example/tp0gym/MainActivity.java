package com.example.tp0gym;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NavHost
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();

        // Bottom Nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Si querés que solo aparezca después del login:
        bottomNav.setVisibility(android.view.View.GONE);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.clasesFragment
                    || destination.getId() == R.id.reservationFragment
                    || destination.getId() == R.id.profileFragment
                    || destination.getId() == R.id.historyFragment) {
                bottomNav.setVisibility(android.view.View.VISIBLE);
            } else {
                bottomNav.setVisibility(android.view.View.GONE);
            }
        });
    }
}