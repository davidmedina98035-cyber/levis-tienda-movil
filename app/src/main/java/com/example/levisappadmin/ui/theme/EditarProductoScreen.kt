package com.example.levisappadmin.ui.theme

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
    return NumberFormat.getNumberInstance(Locale.GERMANY).format(numero)
}

private fun desformatearAPrecio(input: String): Double {
    val soloDigitos = input.filter { it.isDigit() }
    return soloDigitos.toDoubleOrNull() ?: 0.0
}

@Composable
fun EditarProductoScreen(
    producto: Producto,
    onSave: (Producto) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var descripcion by remember { mutableStateOf(producto.descripcion ?: "") }
    var precioTexto by remember { mutableStateOf(formatearMiles(producto.precio.toLong().toString())) }
    var stock by remember { mutableStateOf(producto.stock.toString()) }
    var talla by remember { mutableStateOf(producto.talla ?: "") }
    var categoria by remember { mutableStateOf(producto.categoria ?: "") }
    var genero by remember { mutableStateOf(producto.genero ?: "") }
    var imagen by remember { mutableStateOf(producto.imagen ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Editar Producto", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precioTexto,
            onValueChange = { nuevoValor -> precioTexto = formatearMiles(nuevoValor) },
            label = { Text("Precio") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it.filter { c -> c.isDigit() } },
            label = { Text("Stock") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = talla,
            onValueChange = { talla = it },
            label = { Text("Talla") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = genero,
            onValueChange = { genero = it },
            label = { Text("Género") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imagen,
            onValueChange = { imagen = it },
            label = { Text("Imagen (URL)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                val productoEditado = Producto(
                    id = producto.id,
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = desformatearAPrecio(precioTexto),
                    talla = talla,
                    categoria = categoria,
                    stock = stock.toIntOrNull() ?: 0,
                    genero = genero,
                    imagen = imagen
                )
                onSave(productoEditado)
            }) {
                Text("Guardar")
            }

            OutlinedButton(onClick = { onCancel() }) {
                Text("Cancelar")
            }
        }
    }
}