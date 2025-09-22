package com.example.tp0gym;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp0gym.utils.BiometricHelper;
import com.example.tp0gym.navigation.NavigationManager;

public class MainActivity extends AppCompatActivity {

    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationManager = new NavigationManager(this);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);
        boolean hasLoggedInOnce = prefs.getBoolean("hasLoggedInOnce", false);

        if (token != null && !token.isEmpty() && hasLoggedInOnce) {
            // Si hay token y el usuario ya inició sesión antes, ir a Home
            BiometricHelper.tryBiometric(
                    this,
                    () -> navigationManager.navigateTo("home"),
                    () -> navigationManager.navigateTo("welcome")
            );
        } else {
            // No hay token o nunca inició sesión, mostrar pantalla de bienvenida/login
            navigationManager.navigateTo("welcome");
        }
    }

    // Método para marcar al usuario como logueado
    public void setUserLoggedIn(String token) {
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        prefs.edit()
                .putString("TOKEN", token)
                .putBoolean("hasLoggedInOnce", true)
                .apply();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
