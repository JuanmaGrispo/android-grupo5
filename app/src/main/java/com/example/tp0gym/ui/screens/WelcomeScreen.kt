package com.example.tp0gym.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.tp0gym.ui.components.CustomTextField

@Composable
fun WelcomeScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "GymApp",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Yellow
        )
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(value = email, onValueChange = { email = it }, placeholder = "Email")
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(value = password, onValueChange = { password = it }, placeholder = "Contraseña", isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    email.text.isEmpty() -> Toast.makeText(context, "Ingrese un email", Toast.LENGTH_SHORT).show()
                    !email.text.contains("@") -> Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                    password.text.isEmpty() -> Toast.makeText(context, "Ingrese una contraseña", Toast.LENGTH_SHORT).show()
                    else -> onLoginSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Iniciar sesión", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onForgotPasswordClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Ingreso mediante código", color = Color.Black)
        }
    }
}
