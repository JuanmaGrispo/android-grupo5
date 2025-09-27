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

import com.example.tp0gym.R;
import com.example.tp0gym.utils.AppPreferences;
import com.example.tp0gym.utils.BiometricHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {

    @Inject
    AppPreferences prefs;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return new View(requireContext()); // Splash vacío
    }

    @Override
    public void onResume() {
        super.onResume();

        // Posponemos la decisión al hilo de UI para asegurar que el fragment esté agregado
        requireView().post(this::decideDestination);
    }

    private void decideDestination() {
        String token = prefs.getToken();

        NavController nav = NavHostFragment.findNavController(this);

        if (isTokenValid(token) && prefs.hasLoggedInOnce()) {
            // Segunda vez → biometría
            BiometricHelper.tryBiometric(
                    requireActivity(),
                    this::goToClases, // éxito → ClasesFragment
                    this::goToLogin   // cancelar/fallo → LoginFragment
            );
        } else {
            // Primera vez o token inválido → login
            if (isAdded()) {
                nav.navigate(R.id.loginFragment);
            }
        }
    }

    private void goToClases() {
        if (!isAdded()) return;
        requireView().post(() -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.clasesFragment);
        });
    }

    private void goToLogin() {
        if (!isAdded()) return;
        requireView().post(() -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.loginFragment);
        });
    }

    private boolean isTokenValid(String token) {
        return token != null && !token.trim().isEmpty();
    }
}
