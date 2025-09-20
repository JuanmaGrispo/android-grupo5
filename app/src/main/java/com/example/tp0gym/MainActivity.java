package com.example.tp0gym;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp0gym.navigation.NavigationManager;
import com.example.tp0gym.ui.screens.BiometricHelper;

public class MainActivity extends AppCompatActivity {

    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationManager = new NavigationManager(this);

        // Pedimos biometría antes de mostrar cualquier pantalla
        BiometricHelper.tryBiometric(this, () -> {
            // Siempre mostramos WelcomeFragment al iniciar
            navigationManager.navigateTo("welcome");
        });
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
