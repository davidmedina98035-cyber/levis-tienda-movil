package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.model.RegisterRequest
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

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
            Text("Registro", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Email *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña *") },
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Icon(
                            if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null, tint = Color(0xFFC41230)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(value = telefono, onValueChange = { telefono = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = direccion, onValueChange = { direccion = it },
                label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                        error = "Nombre, email y contraseña son obligatorios"
                        return@Button
                    }
                    scope.launch {
                        cargando = true
                        error = null
                        try {
                            val response = RetrofitClient.api.register(
                                RegisterRequest(
                                    nombre = nombre,
                                    email = email,
                                    password = password,
                                    rol = "cliente",
                                    telefono = telefono.ifEmpty { null },
                                    direccion = direccion.ifEmpty { null }
                                )
                            )
                            if (response.isSuccessful) {
                                onRegisterSuccess()
                            } else {
                                error = "Error al registrar: ${response.errorBody()?.string()}"
                            }
                        } catch (e: Exception) {
                            error = "Error de conexión: ${e.message}"
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
                else Text("REGISTRARSE", fontWeight = FontWeight.Bold)
            }

            error?.let {
                Text(it, color = Color.Red, fontSize = 13.sp)
            }
        }
    }
}