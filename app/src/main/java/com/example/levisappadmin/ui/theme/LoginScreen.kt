package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String, String) -> Unit = { _, _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    // Colores basados en la interfaz web oscura y neón
    val backgroundColor = Color(0xFF0B0B0B)
    val cardBackground = Color(0xFF121212)
    val neonRed = Color(0xFFE31837)
    val inputBackground = Color(0xFF1E1E1E)
    val inputBorderColor = Color(0xFF2C2C2C)
    val lightGrayText = Color(0xFFA0A0A0)

    val estado = viewModel.estado.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(cardBackground, shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF1F1F1F), shape = RoundedCornerShape(8.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge del logo LEVI'S
            Box(
                modifier = Modifier
                    .background(neonRed, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "LEVI'S",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Campo EMAIL
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                placeholder = { Text("EMAIL", color = Color(0xFF666666), fontSize = 14.sp) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Campo CONTRASEÑA
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                placeholder = { Text("CONTRASEÑA", color = Color(0xFF666666), fontSize = 14.sp) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Mensaje de Error si ocurre
            if (estado is LoginViewModel.Estado.Error) {
                Text(
                    text = estado.mensaje,
                    color = neonRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Botón INGRESAR consumiendo el ViewModel real
            Button(
                onClick = {
                    viewModel.login { token, email, id ->
                        onLoginSuccess(token, email, id.toString())
                    }
                },
                enabled = estado !is LoginViewModel.Estado.Cargando,
                colors = ButtonDefaults.buttonColors(containerColor = neonRed),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (estado is LoginViewModel.Estado.Cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "INGRESAR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enlaces de registro y recuperación
            Row {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = lightGrayText,
                    fontSize = 13.sp
                )
                Text(
                    text = "Regístrate aquí",
                    color = neonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = lightGrayText,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }
    }
}