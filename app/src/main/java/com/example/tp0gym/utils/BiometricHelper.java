package com.example.tp0gym.utils;

import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class BiometricHelper {

    public static void tryBiometric(FragmentActivity activity, Runnable onSuccess, Runnable onCancel) {
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                activity,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(activity, "Error biométrico: " + errString, Toast.LENGTH_SHORT).show();


                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                                errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            onCancel.run();
                        } else {
                            onCancel.run();
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        onSuccess.run();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                }
        );

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloqueo con biometría")
                .setSubtitle("Usa tu huella o rostro para acceder a la app")
                .setNegativeButtonText("Cancelar")
                .build();

        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(activity, "Biometría no disponible", Toast.LENGTH_SHORT).show();
            onCancel.run();
        }
    }
}
