// app/src/main/java/com/example/tp0gym/utils/AppPreferences.java
package com.example.tp0gym.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

public class AppPreferences {
    private static final String KEY_TOKEN = "token";
    private static final String KEY_HAS_LOGGED_IN_ONCE = "hasLoggedInOnce";
    private static final String KEY_PERMISSIONS_ASKED = "permissionsAsked";

    private final SharedPreferences prefs;

    // NUEVO: constructor recomendado para Hilt
    public AppPreferences(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void setToken(String token) { prefs.edit().putString(KEY_TOKEN, token).apply(); }
    public String getToken() { return prefs.getString(KEY_TOKEN, ""); }

    public void setHasLoggedInOnce(boolean value) { prefs.edit().putBoolean(KEY_HAS_LOGGED_IN_ONCE, value).apply(); }
    public boolean hasLoggedInOnce() { return prefs.getBoolean(KEY_HAS_LOGGED_IN_ONCE, false); }

    public void setPermissionsAsked(boolean value) { prefs.edit().putBoolean(KEY_PERMISSIONS_ASKED, value).apply(); }
    public boolean getPermissionsAsked() { return prefs.getBoolean(KEY_PERMISSIONS_ASKED, false); }

    public void clear() { prefs.edit().clear().apply(); }
}