package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levisappadmin.viewmodel.CatalogoViewModel

private val RojoLevis = Color(0xFFC41230)
private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    token: String,
    onVerCarrito: () -> Unit,
    onBack: () -> Unit,
    viewModel: CatalogoViewModel
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val carrito by viewModel.carrito.collectAsState()
    val totalItems = carrito.sumOf { item -> item.cantidad }

    LaunchedEffect(Unit) {
        viewModel.cargarProductos(token)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Catálogo", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    BadgedBox(badge = {
                        if (totalItems > 0) Badge { Text("$totalItems") }
                    }) {
                        IconButton(onClick = onVerCarrito) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RojoLevis)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                cargando -> CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = RojoLevis)
                error != null -> Text("Error: $error", color = RojoLevis, modifier = Modifier.padding(16.dp))
                productos.isEmpty() -> Text("No hay productos disponibles.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(productos) { producto ->
                        val cantidadEnCarrito = viewModel.cantidadEnCarrito(producto)
                        val stockTotal = producto.totalStock
                        val stockDisponible = stockTotal - cantidadEnCarrito

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    producto.nombreProducto ?: "Sin nombre",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold, color = RojoLevis
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val desc = producto.descripcionProducto
                                if (!desc.isNullOrBlank()) {
                                    Text("Descripción: $desc", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                }
                                val precio = producto.precioProducto ?: 0.0
                                Text("Precio: $$precio", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))

                                if (!producto.categoria.isNullOrBlank()) {
                                    Text("Categoría: ${producto.categoria}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                }

                                if (!producto.tallas.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tallas y Stock:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                                    producto.tallas.forEach { itemTalla ->
                                        Text("• ${itemTalla.talla}: ${itemTalla.stock} unds", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Stock Total Disponible: $stockDisponible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (stockDisponible < 10) RojoLevis else Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    IconButton(
                                        onClick = { viewModel.agregarAlCarrito(producto) },
                                        enabled = stockDisponible > 0
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = if (stockDisponible > 0) RojoLevis else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}