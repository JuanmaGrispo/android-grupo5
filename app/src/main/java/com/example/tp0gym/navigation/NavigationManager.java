package com.example.tp0gym.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.tp0gym.R;
import com.example.tp0gym.ui.screens.ClasesFragment;
import com.example.tp0gym.ui.screens.EmailLoginFragment;
import com.example.tp0gym.ui.screens.LoginFragment;
import com.example.tp0gym.ui.screens.PermissionsFragment;
import com.example.tp0gym.ui.screens.VerificationCodeFragment;
import com.example.tp0gym.ui.screens.ProfileFragment;

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
                fragment = new LoginFragment();
                break;
            case "email":
                fragment = new EmailLoginFragment();
                break;
            case "verification":
                fragment = new VerificationCodeFragment();
                break;
            case "home":
                fragment = new ClasesFragment();
                break;
            case "permissions":
                PermissionsFragment pf = new PermissionsFragment();
                pf.setOnFinished(() -> navigateTo("home"));
                fragment = pf;
                break;
            case "profile":       // <-- NUEVO
                fragment = new ProfileFragment();
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
