package com.example.tp0gym.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.tp0gym.R;
import com.example.tp0gym.ui.screens.EmailFragment;
import com.example.tp0gym.ui.screens.HomeFragment;
import com.example.tp0gym.ui.screens.VerificationFragment;
import com.example.tp0gym.ui.screens.WelcomeFragment;
import com.example.tp0gym.ui.screens.PermissionsFragment;

public class NavigationManager {
    private final FragmentActivity activity;

    public NavigationManager(FragmentActivity activity) {
        this.activity = activity;
    }

    // Navegación simple sin datos
    public void navigateTo(String screen) {
        navigateTo(screen, null);
    }

    // Navegación con datos (Bundle)
    public void navigateTo(String screen, Bundle args) {
        Fragment fragment = null;

        switch (screen) {
            case "welcome":
                fragment = new WelcomeFragment();
                break;
            case "email":
                fragment = new EmailFragment();
                break;
            case "verification":
                fragment = new VerificationFragment(); // constructor vacío
                break;
            case "home":
                fragment = new HomeFragment();
                break;
            case "permissions":
                PermissionsFragment pf = new PermissionsFragment();
                pf.setOnFinished(() -> navigateTo("home"));
                fragment = pf;
                break;

        }

        if (fragment != null) {
            if (args != null) {
                fragment.setArguments(args); // pasamos los datos por Bundle
            }
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
