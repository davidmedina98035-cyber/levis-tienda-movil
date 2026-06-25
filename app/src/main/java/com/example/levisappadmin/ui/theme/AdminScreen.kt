package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onCerrarSesion: () -> Unit
) {
    var esModoEdicion by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PANEL ADMINISTRADOR", fontWeight = FontWeight.Black, color = Color(0xFFC41230)) },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEEEEEE))
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de modo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = !esModoEdicion,
                    onClick = { esModoEdicion = false },
                    label = { Text("NUEVA PRENDA") }
                )
                FilterChip(
                    selected = esModoEdicion,
                    onClick = { esModoEdicion = true },
                    label = { Text("EDITAR PRENDA") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario Simplificado Temporal para evitar errores
            FormularioPrendaMock(esModoEdicion = esModoEdicion)
        }
    }
}

@Composable
fun FormularioPrendaMock(esModoEdicion: Boolean) {
    var idProducto by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (esModoEdicion) {
            OutlinedTextField(
                value = idProducto,
                onValueChange = { idProducto = it },
                label = { Text("ID del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la prenda") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Temporalmente libre de errores */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC41230))
        ) {
            Text(if (esModoEdicion) "ACTUALIZAR PRENDA" else "PUBLICAR EN ALMACÉN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}