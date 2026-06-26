package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onExitooso: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFC41230)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Recuperar Contraseña", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ingresa tu nombre y email para recuperar tu contraseña.", color = Color.Gray, fontSize = 13.sp)

            OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = nuevaPassword, onValueChange = { nuevaPassword = it },
                label = { Text("Nueva contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || email.isBlank() || nuevaPassword.isBlank()) {
                        mensaje = "Todos los campos son obligatorios"
                        esError = true
                        return@Button
                    }
                    scope.launch {
                        cargando = true
                        try {
                            val response = RetrofitClient.api.recuperarPassword(
                                mapOf(
                                    "nombre" to nombre,
                                    "email" to email,
                                    "newPassword" to nuevaPassword
                                )
                            )
                            if (response.isSuccessful) {
                                mensaje = "Contraseña actualizada correctamente"
                                esError = false
                                onExitooso()
                            } else {
                                mensaje = "Datos incorrectos, verifica tu nombre y email"
                                esError = true
                            }
                        } catch (e: Exception) {
                            mensaje = "Error de conexión: ${e.message}"
                            esError = true
                        }
                        cargando = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC41230)),
                enabled = !cargando
            ) {
                if (cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("RECUPERAR CONTRASEÑA", fontWeight = FontWeight.Bold)
            }

            mensaje?.let {
                Text(it, color = if (esError) Color.Red else Color(0xFF2E7D32), fontSize = 13.sp)
            }

            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Volver al login", color = Color(0xFFC41230))
            }
        }
    }
}