package com.example.tp0gym;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Automáticamente arranca el splash debido a que main es contenedora de res/navigation/nav_graph
    }
}