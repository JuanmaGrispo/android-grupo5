package com.example.tp0gym.navigation

import com.example.tp0gym.ui.screens.HomeScreen
import androidx.compose.runtime.*
import com.example.tp0gym.ui.screens.*

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("welcome") }
    var emailForOTP by remember { mutableStateOf("") }

    when (currentScreen) {
        "welcome" -> WelcomeScreen(
            onLoginSuccess = { currentScreen = "home" },
            onRegisterClick = { /* No hacemos nada, ya no hay registro */ },
            onForgotPasswordClick = { currentScreen = "email" } // primero email para OTP
        )

        "email" -> EmailScreen(
            onNext = {
                emailForOTP = it
                currentScreen = "verification"
            },
            onBackClick = { currentScreen = "welcome" }
        )

        "verification" -> VerificationScreen(
            email = emailForOTP,
            onNext = { currentScreen = "home" },
            onBackClick = { currentScreen = "email" }
        )

        "home" -> HomeScreen()
    }
}
