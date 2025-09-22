package com.example.tp0gym.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.tp0gym.R;
import com.example.tp0gym.ui.screens.ClasesFragment;
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

    public void navigateTo(String screen) {
        navigateTo(screen, null);
    }

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
                fragment = new VerificationFragment();
                break;
            case "home":
                fragment = new ClasesFragment();
                break;
            case "permissions":
                PermissionsFragment pf = new PermissionsFragment();
                pf.setOnFinished(() -> navigateTo("home"));
                fragment = pf;
                break;

        }

        if (fragment != null) {
            if (args != null) {
                fragment.setArguments(args);
            }
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
