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

// Importamos la función censorEmail desde VerificationScreen
import com.example.tp0gym.ui.screens.censorEmail

@Composable
fun EmailScreen(
    onNext: (email: String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Ingrese su email",
            color = Color.Yellow,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    email.text.isEmpty() -> Toast.makeText(context, "Ingrese un email", Toast.LENGTH_SHORT).show()
                    !email.text.contains("@") -> Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                    else -> {
                        // Mostramos el email censurado
                        Toast.makeText(context, "Código enviado a ${censorEmail(email.text)}", Toast.LENGTH_SHORT).show()
                        onNext(email.text)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Enviar código", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Volver", color = Color.White)
        }
    }
}
