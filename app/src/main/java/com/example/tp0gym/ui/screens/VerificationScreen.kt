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

fun censorEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email
    val name = parts[0]
    val domain = parts[1]

    val censoredName = if (name.length <= 2) {
        name.first() + "*".repeat(name.length - 1)
    } else {
        name.first() + "*".repeat(name.length - 2) + name.last()
    }

    return "$censoredName@$domain"
}

@Composable
fun VerificationScreen(
    email: String,
    onNext: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf(TextFieldValue("")) }
    val correctCode = "123456"

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Ingrese el código enviado a ${censorEmail(email)}",
            color = Color.Yellow,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = code,
            onValueChange = { code = it },
            placeholder = "Código OTP"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    code.text.isEmpty() -> Toast.makeText(context, "Ingrese el código", Toast.LENGTH_SHORT).show()
                    code.text == correctCode -> {
                        Toast.makeText(context, "Código correcto, sesión iniciada", Toast.LENGTH_SHORT).show()
                        onNext()
                    }
                    else -> Toast.makeText(context, "Código incorrecto, intente de nuevo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Verificar", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Código reenviado a ${censorEmail(email)}", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Reenviar código", color = Color.Black)
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
