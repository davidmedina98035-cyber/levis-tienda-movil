package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levisappadmin.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: LoginViewModel,
    onEnvioExitoso: () -> Unit,
    onVolverAlLogin: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    val error = viewModel.error.value
    val cargando = viewModel.cargando.value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recuperar Contraseña") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario o Email") },
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    onEnvioExitoso()
                },
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (cargando) "Enviando..." else "Enviar enlace de recuperación")
            }

            TextButton(onClick = onVolverAlLogin) {
                Text("Volver al inicio de sesión")
            }
        }
    }
}