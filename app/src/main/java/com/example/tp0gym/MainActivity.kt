package com.example.tp0gym

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import com.example.tp0gym.ui.screens.*
import com.example.tp0gym.ui.theme.Tp0GymTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply() // solo para debug
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val permissionsGranted = prefs.getBoolean("permissionsGranted", false)

        setContent {
            Tp0GymTheme {
                var showWelcome by remember { mutableStateOf(false) }
                var showHome by remember { mutableStateOf(false) }
                var showPermissions by remember { mutableStateOf(false) }
                var showEmailScreen by remember { mutableStateOf(false) }
                var showVerificationScreen by remember { mutableStateOf(false) }
                var emailForOTP by remember { mutableStateOf("") }

                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {

                    // 👇 Biometría al abrir la app si el usuario no está logueado
                    LaunchedEffect(Unit) {
                        if (!isLoggedIn) {
                            tryBiometric(this@MainActivity) {
                                // Biométrico OK → marcar como logueado
                                prefs.edit().putBoolean("isLoggedIn", true).apply()
                                showWelcome = false
                                if (!permissionsGranted) showPermissions = true
                                else showHome = true
                            }
                            showWelcome = true
                        } else {
                            if (!permissionsGranted) showPermissions = true
                            else showHome = true
                        }
                    }

                    when {
                        // LOGIN CON MAIL Y CONTRASEÑA
                        showWelcome && !showEmailScreen -> WelcomeScreen(
                            onLoginSuccess = {
                                prefs.edit().putBoolean("isLoggedIn", true).apply()
                                tryBiometric(this@MainActivity) {
                                    showWelcome = false
                                    if (!permissionsGranted) showPermissions = true
                                    else showHome = true
                                }
                            },
                            onRegisterClick = { /* Registro */ },
                            onForgotPasswordClick = {
                                showEmailScreen = true
                            }
                        )

                        // INGRESO POR EMAIL (OTP)
                        showEmailScreen -> EmailScreen(
                            onNext = { email ->
                                emailForOTP = email
                                showEmailScreen = false
                                showWelcome = false // cerramos welcome
                                showVerificationScreen = true
                            },
                            onBackClick = {
                                showEmailScreen = false
                            }
                        )

                        showVerificationScreen -> VerificationScreen(
                            email = emailForOTP,
                            onNext = {
                                showVerificationScreen = false
                                showWelcome = false
                                if (!permissionsGranted) showPermissions = true
                                else showHome = true
                            },
                            onBackClick = {
                                showVerificationScreen = false
                                showEmailScreen = true
                            }
                        )

                        // PIDE PERMISOS SOLO UNA VEZ
                        showPermissions -> PermissionsScreen(
                            activity = this@MainActivity,
                            onFinished = {
                                prefs.edit().putBoolean("permissionsGranted", true).apply()
                                showPermissions = false
                                showHome = true
                            }
                        )

                        // HOME
                        showHome -> HomeScreen()

                    }
                }
            }
        }
    }
}
