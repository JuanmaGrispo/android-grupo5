package com.example.tp0gym.ui.screens;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.tp0gym.R;

public class PermissionsFragment extends Fragment {

    private Runnable onFinished;
    private ActivityResultLauncher<String[]> permissionLauncher;

    public PermissionsFragment() {
        // constructor vacío requerido
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

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

        // Configuramos el launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    // Guardamos que ya se mostró la solicitud de permisos para este dispositivo
                    SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("permissionsAsked", true).apply();

                    if (onFinished != null) onFinished.run();
                }
        );

        // Pedimos permisos solo si no se mostraron antes en este dispositivo
        requestPermissionsIfNeeded();

        return view;
    }

    private void requestPermissionsIfNeeded() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean alreadyAsked = prefs.getBoolean("permissionsAsked", false);

        if (!alreadyAsked) {
            launchPermissions();
        } else {
            if (onFinished != null) onFinished.run();
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
}
