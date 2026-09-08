package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

    // Paleta de colores idéntica a la del Login
    val backgroundColor = Color(0xFF0B0B0B)
    val cardBackground = Color(0xFF121212)
    val neonRed = Color(0xFFE31837)
    val inputBackground = Color(0xFF1E1E1E)
    val inputBorderColor = Color(0xFF2C2C2C)
    val placeholderColor = Color(0xFF666666)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra superior estilo neón limpia sin errores de dibujo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REGISTRO DE ADMINISTRACIÓN",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFF1F1F1F))

            // Contenedor principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Tarjeta contenedora para los campos
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1F1F1F), shape = RoundedCornerShape(8.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "NUEVO USUARIO",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("NOMBRE *", color = placeholderColor, fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBackground,
                            unfocusedContainerColor = inputBackground,
                            disabledContainerColor = inputBackground,
                            focusedIndicatorColor = neonRed,
                            unfocusedIndicatorColor = inputBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("EMAIL *", color = placeholderColor, fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBackground,
                            unfocusedContainerColor = inputBackground,
                            disabledContainerColor = inputBackground,
                            focusedIndicatorColor = neonRed,
                            unfocusedIndicatorColor = inputBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("CONTRASEÑA *", color = placeholderColor, fontSize = 14.sp) },
                        visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                                Icon(
                                    if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = placeholderColor
                                )
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBackground,
                            unfocusedContainerColor = inputBackground,
                            disabledContainerColor = inputBackground,
                            focusedIndicatorColor = neonRed,
                            unfocusedIndicatorColor = inputBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        placeholder = { Text("TELÉFONO", color = placeholderColor, fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBackground,
                            unfocusedContainerColor = inputBackground,
                            disabledContainerColor = inputBackground,
                            focusedIndicatorColor = neonRed,
                            unfocusedIndicatorColor = inputBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        placeholder = { Text("DIRECCIÓN", color = placeholderColor, fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBackground,
                            unfocusedContainerColor = inputBackground,
                            disabledContainerColor = inputBackground,
                            focusedIndicatorColor = neonRed,
                            unfocusedIndicatorColor = inputBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = neonRed),
                        enabled = !cargando
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "REGISTRARSE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    error?.let {
                        Text(it, color = neonRed, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}