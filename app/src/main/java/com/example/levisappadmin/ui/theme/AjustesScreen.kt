package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.model.ActualizarPerfilRequest
import com.example.levisappadmin.model.ActualizarUsuarioRequest
import com.example.levisappadmin.model.CrearUsuarioRequest
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch

private val RojoLevis = Color(0xFFC41230)

@Composable
fun AjustesScreen(token: String, email: String, onBack: () -> Unit) {
    var seccion by remember { mutableStateOf<String?>(null) }

    when (seccion) {
        "perfil" -> MiPerfilScreen(token = token, email = email, onBack = { seccion = null })
        "admin" -> AgregarAdminScreen(token = token, onBack = { seccion = null })
        else -> MenuAjustes(onBack = onBack, onPerfilClick = { seccion = "perfil" }, onAdminClick = { seccion = "admin" })
    }
}

@Composable
fun MenuAjustes(onBack: () -> Unit, onPerfilClick: () -> Unit, onAdminClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(RojoLevis).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Ajustes", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                onClick = onPerfilClick
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = RojoLevis, modifier = Modifier.size(36.dp))
                    Column {
                        Text("Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Editar nombre, contraseña y datos", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                onClick = onAdminClick
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = RojoLevis, modifier = Modifier.size(36.dp))
                    Column {
                        Text("Agregar Administrador", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Crear nuevo usuario con rol admin", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun MiPerfilScreen(token: String, email: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getPerfil("Bearer $token", email)
            if (response.isSuccessful) {
                response.body()?.let {
                    nombre = it.nombre
                    telefono = it.telefono ?: ""
                    direccion = it.direccion ?: ""
                }
            }
        } catch (e: Exception) {
            mensaje = "Error al cargar perfil"
            esError = true
        }
        cargando = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE))) {
        Row(
            modifier = Modifier.fillMaxWidth().background(RojoLevis).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Mi Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        if (cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoLevis)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(email, color = Color.Gray, fontSize = 13.sp)

                OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text("Nueva contraseña (opcional)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), singleLine = true)

                OutlinedTextField(value = telefono, onValueChange = { telefono = it },
                    label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                OutlinedTextField(value = direccion, onValueChange = { direccion = it },
                    label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            cargando = true
                            try {
                                val response = RetrofitClient.api.actualizarPerfil(
                                    "Bearer $token",
                                    ActualizarPerfilRequest(
                                        nombre = nombre,
                                        email = email,
                                        password = password.ifEmpty { null },
                                        telefono = telefono.ifEmpty { null },
                                        direccion = direccion.ifEmpty { null }
                                    )
                                )
                                if (response.isSuccessful) {
                                    mensaje = "Perfil actualizado correctamente"
                                    esError = false
                                    password = ""
                                } else {
                                    mensaje = "Error al actualizar"
                                    esError = true
                                }
                            } catch (e: Exception) {
                                mensaje = "Error de conexión"
                                esError = true
                            }
                            cargando = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoLevis)
                ) {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                }

                mensaje?.let {
                    Text(it, color = if (esError) Color.Red else Color(0xFF2E7D32),
                        fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun AgregarAdminScreen(token: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE))) {
        Row(
            modifier = Modifier.fillMaxWidth().background(RojoLevis).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Agregar Administrador", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                label = { Text("Correo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = password, onValueChange = { password = it },
                label = { Text("Contraseña *") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                        mensaje = "Todos los campos son obligatorios"
                        esError = true
                        return@Button
                    }
                    scope.launch {
                        cargando = true
                        try {
                            val response = RetrofitClient.api.crearUsuario(
                                "Bearer $token",
                                CrearUsuarioRequest(
                                    nombre = nombre,
                                    email = email,
                                    password = password,
                                    rol = "admin",
                                    telefono = null
                                )
                            )
                            if (response.isSuccessful) {
                                mensaje = "Administrador creado correctamente"
                                esError = false
                                nombre = ""
                                email = ""
                                password = ""
                            } else {
                                mensaje = "Error al crear administrador"
                                esError = true
                            }
                        } catch (e: Exception) {
                            mensaje = "Error de conexión"
                            esError = true
                        }
                        cargando = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoLevis),
                enabled = !cargando
            ) {
                if (cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("CREAR ADMINISTRADOR", fontWeight = FontWeight.Bold)
            }

            mensaje?.let {
                Text(it, color = if (esError) Color.Red else Color(0xFF2E7D32),
                    fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}