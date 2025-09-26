package com.example.tp0gym.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    private static final String PREFS_NAME = "APP_PREFS";

    @Provides @Singleton
    public SharedPreferences provideSharedPrefs(@ApplicationContext Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Provides @Singleton
    public AppPreferences provideAppPreferences(SharedPreferences prefs) {
        return new AppPreferences(prefs);
    }

}