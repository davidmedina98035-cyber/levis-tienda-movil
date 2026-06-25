package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.viewmodel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    token: String,
    onVerCarrito: () -> Unit,
    onVolver: () -> Unit,
    viewModel: CatalogoViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val carrito by viewModel.carrito.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos(token)
    }

    val totalItems = carrito.sumOf { it.cantidad }

    Scaffold(
        containerColor = LevisGrisClaro,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catálogo",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (totalItems > 0) Badge { Text("$totalItems") }
                        }
                    ) {
                        IconButton(onClick = onVerCarrito) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevisRojo
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                cargando -> CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = LevisRojo
                )
                error != null -> Text(
                    "Error: $error",
                    color = LevisRojo,
                    modifier = Modifier.padding(16.dp)
                )
                productos.isEmpty() -> Text(
                    "No hay productos disponibles.",
                    modifier = Modifier.padding(16.dp),
                    color = LevisNegro
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(productos) { producto ->
                        val cantidadEnCarrito = carrito
                            .find { it.producto.id == producto.id }?.cantidad ?: 0
                        val stockDisponible = producto.stock - cantidadEnCarrito

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    producto.nombre,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LevisRojo
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (!producto.descripcion.isNullOrBlank()) {
                                    Text(
                                        "Descripción: ${producto.descripcion}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    "Precio: $${producto.precio}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = LevisNegro
                                )

                                if (!producto.talla.isNullOrBlank()) {
                                    Text(
                                        "Talla: ${producto.talla}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LevisNegro
                                    )
                                }

                                if (!producto.categoria.isNullOrBlank()) {
                                    Text(
                                        "Categoría: ${producto.categoria}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LevisNegro
                                    )
                                }

                                Text(
                                    "Stock disponible: $stockDisponible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (stockDisponible < 10) LevisRojo else LevisNegro,
                                    fontWeight = if (stockDisponible < 10) FontWeight.Bold else FontWeight.Normal
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { viewModel.agregarAlCarrito(producto) },
                                        enabled = stockDisponible > 0
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ShoppingCart,
                                            contentDescription = "Agregar al carrito",
                                            tint = if (stockDisponible > 0) LevisRojo else Color.Gray
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