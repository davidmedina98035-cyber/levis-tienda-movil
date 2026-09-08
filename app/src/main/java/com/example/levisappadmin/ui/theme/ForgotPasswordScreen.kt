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
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onExitooso: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(false) }

    // Paleta de colores idéntica a la web y al login
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
            // Barra superior estilo neón
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECUPERAR CONTRASEÑA",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1F1F1F), shape = RoundedCornerShape(8.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Logo o título idéntico al diseño web
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(neonRed, shape = RoundedCornerShape(4.dp))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LEVI'S",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Text(
                        text = "RECUPERAR CONTRASEÑA",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("CORREO ELECTRÓNICO", color = placeholderColor, fontSize = 13.sp) },
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
                        value = codigo,
                        onValueChange = { codigo = it },
                        placeholder = { Text("INGRESE CÓDIGO DE VERIFICACIÓN", color = placeholderColor, fontSize = 13.sp) },
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
                        value = nuevaPassword,
                        onValueChange = { nuevaPassword = it },
                        placeholder = { Text("NUEVA CONTRASEÑA", color = placeholderColor, fontSize = 13.sp) },
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || codigo.isBlank() || nuevaPassword.isBlank()) {
                                mensaje = "Todos los campos son obligatorios"
                                esError = true
                                return@Button
                            }
                            scope.launch {
                                cargando = true
                                try {
                                    val response = RetrofitClient.api.recuperarPassword(
                                        mapOf(
                                            "email" to email,
                                            "codigo" to codigo,
                                            "newPassword" to nuevaPassword
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        mensaje = "Contraseña actualizada correctamente"
                                        esError = false
                                        onExitooso()
                                    } else {
                                        mensaje = "Código incorrecto o correo no registrado"
                                        esError = true
                                    }
                                } catch (e: Exception) {
                                    mensaje = "Error de conexión: ${e.message}"
                                    esError = true
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
                                text = "ACTUALIZAR CONTRASEÑA",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    mensaje?.let {
                        Text(it, color = if (esError) neonRed else Color(0xFF4CAF50), fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Recordaste tu clave? ", color = placeholderColor, fontSize = 13.sp)
                        TextButton(
                            onClick = onBack,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Inicia sesión", color = neonRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}