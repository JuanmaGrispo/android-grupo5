//com/example/tp0gym/ui/screens/SplashFragment.java
package com.example.tp0gym.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavOptions;

import com.example.tp0gym.R;
import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {

    @Inject AppPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Layout simple/blank. Podés usar un ProgressBar si querés.
        return new View(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        decideDestination();
    }

    private void decideDestination() {
        String token = prefs.getToken();

        NavController nav = NavHostFragment.findNavController(this);

        // Opciones para limpiar el back stack del splash
        NavOptions clearBackStackToSplash = new NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build();

        if (isTokenValid("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NGVmZjJmNS1hYjljLTQ4ZjMtODdjZi0yZWZmZTQyZDgwMWEiLCJlbWFpbCI6ImJhbHRhLm1hcmVuZGFAZ21haWwuY29tIiwiaWF0IjoxNzU4OTQxMDU5LCJleHAiOjE3NTk1NDU4NTl9.01fHWLl0ZVOsRvGSd8CecjT_pMLkEBh5omaeuAw4RNA")) {
            // Ir a Home y sacar el splash del back stack
            nav.navigate(R.id.action_login_to_home, null, clearBackStackToSplash);
        } else {
            // Ir a Login y sacar el splash del back stack
            nav.navigate(R.id.action_splash_to_login, null, clearBackStackToSplash);
        }
    }

    // “Válido” mínimo: existe. Si querés, validá exp de JWT.
    private boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) return false;

        // Opcional: validar expiración leyendo el payload del JWT (sin red)
        // o ping rápido al backend para refresh/validación.
        return true;
    }
}