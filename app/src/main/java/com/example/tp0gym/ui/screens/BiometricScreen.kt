package com.example.tp0gym.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

fun tryBiometric(activity: FragmentActivity, onFinished: () -> Unit) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(activity, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
                onFinished()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onFinished()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setSubtitle("Use your fingerprint or face to unlock the app")
        .setNegativeButtonText("Cancel")
        .build()

    val biometricManager = BiometricManager.from(activity)
    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
        BiometricManager.BIOMETRIC_SUCCESS
    ) {
        biometricPrompt.authenticate(promptInfo)
    } else {
        Toast.makeText(activity, "Biometric not available", Toast.LENGTH_SHORT).show()
        onFinished()
    }
}
