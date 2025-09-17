package com.example.tp0gym.ui.screens

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

@Composable
fun PermissionsScreen(
    activity: FragmentActivity,
    onFinished: () -> Unit
) {
    val prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var cameraGranted by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationGranted = granted
    }

    LaunchedEffect(Unit) {
        // primero cámara
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        } else {
            cameraGranted = true
        }
    }

    // cuando cámara ya está concedida, pedimos notificaciones si Android 13+
    LaunchedEffect(cameraGranted) {
        if (cameraGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                notificationGranted = true
            }
        } else if (cameraGranted) {
            // versiones < 33, no necesitamos notificaciones
            notificationGranted = true
        }
    }

    // cuando ambos permisos estén concedidos o ignorados, seguimos
    LaunchedEffect(cameraGranted, notificationGranted) {
        if (cameraGranted && notificationGranted) {
            prefs.edit().putBoolean("permissionsHandled", true).apply()
            onFinished()
        }
    }

    // UI simple
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
