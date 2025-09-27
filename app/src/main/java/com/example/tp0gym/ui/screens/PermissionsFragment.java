package com.example.tp0gym.ui.screens;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tp0gym.R;
import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PermissionsFragment extends Fragment {

    @Inject AppPreferences prefs;
    private ActivityResultLauncher<String[]> permissionLauncher;

    public PermissionsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_permissions, container, false);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setTint(
                ContextCompat.getColor(requireContext(), R.color.purple_80)
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    // Marcar que ya pedimos permisos
                    prefs.setPermissionsAsked(true);
                    goToClases();
                }
        );

        requestPermissionsIfNeeded();
        return view;
    }

    private void requestPermissionsIfNeeded() {
        if (!prefs.getPermissionsAsked()) {
            launchPermissions();
        } else {
            goToClases();
        }
    }

    private void launchPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA
            });
        }
    }

    private void goToClases() {
        NavController nav = NavHostFragment.findNavController(this);
        nav.navigate(R.id.clasesFragment);
    }
}
