package com.example.tp0gym;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp0gym.utils.BiometricHelper;
import com.example.tp0gym.navigation.NavigationManager;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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
            BiometricHelper.tryBiometric(
                    this,
                    () -> navigationManager.navigateTo("home"),
                    () -> navigationManager.navigateTo("welcome")
            );
        } else {
            navigationManager.navigateTo("welcome");
        }
    }

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
