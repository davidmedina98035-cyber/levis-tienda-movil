package com.example.levisappadmin.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.levisappadmin.model.Producto
import java.text.NumberFormat
import java.util.Locale

private fun formatearMiles(input: String): String {
    val soloDigitos = input.filter { it.isDigit() }
    if (soloDigitos.isEmpty()) return ""
    val numero = soloDigitos.toLong()
    return NumberFormat.getNumberInstance(Locale.GERMANY).format(numero) // usa "." como separador de miles
}

private fun desformatearAPrecio(input: String): Double {
    val soloDigitos = input.filter { it.isDigit() }
    return soloDigitos.toDoubleOrNull() ?: 0.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    token: String,
    onAgregar: (Producto) -> Unit,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

            OutlinedTextField(
                value = precioTexto,
                onValueChange = { nuevoValor -> precioTexto = formatearMiles(nuevoValor) },
                label = { Text("Precio") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(value = talla, onValueChange = { talla = it }, label = { Text("Talla") })
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it.filter { c -> c.isDigit() } },
                label = { Text("Stock") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(value = genero, onValueChange = { genero = it }, label = { Text("Género") })
            OutlinedTextField(value = imagen, onValueChange = { imagen = it }, label = { Text("Imagen (URL)") })

            Button(
                onClick = {
                    val producto = Producto(
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = desformatearAPrecio(precioTexto),
                        talla = talla,
                        categoria = categoria,
                        stock = stock.toIntOrNull() ?: 0,
                        genero = genero,
                        imagen = if (imagen.isBlank()) null else imagen
                    )
                    onAgregar(producto)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}