package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    token: String,
    onBack: () -> Unit,
    onAgregar: () -> Unit,
    onEditar: (Producto) -> Unit,
    viewModel: InventarioViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    val categorias = listOf("Todas", "pantalon", "camiseta", "chaqueta", "accesorio")

    // ✅ Filtro aplicado localmente
    val productosFiltrados = productos.filter { producto ->
        val coincideNombre = producto.nombreProducto
            .lowercase()
            .contains(busqueda.lowercase())
        val coincideCategoria = categoriaSeleccionada == "Todas" ||
                producto.categoria?.lowercase() == categoriaSeleccionada.lowercase()
        coincideNombre && coincideCategoria
    }

    LaunchedEffect(Unit) {
        viewModel.cargarProductos(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC41230),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = Color(0xFFC41230)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
                .padding(padding)
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFC41230)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = busqueda,
                                onValueChange = { busqueda = it },
                                label = { Text("Buscar producto...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }


                        item {
                            ExposedDropdownMenuBox(
                                expanded = expandedFiltro,
                                onExpandedChange = { expandedFiltro = !expandedFiltro }
                            ) {
                                OutlinedTextField(
                                    value = categoriaSeleccionada,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Filtrar por categoría") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedFiltro,
                                    onDismissRequest = { expandedFiltro = false }
                                ) {
                                    categorias.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                categoriaSeleccionada = cat
                                                expandedFiltro = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                text = "${productosFiltrados.size} producto(s) encontrado(s)",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        if (productosFiltrados.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No se encontraron productos",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(productosFiltrados) { producto ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = producto.nombreProducto,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "$ ${producto.precioProducto}",
                                                color = Color(0xFFC41230),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Stock: ${producto.stockProducto}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Talla: ${producto.talla ?: "N/A"}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Categoría: ${producto.categoria ?: "N/A"}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Row {
                                            IconButton(onClick = { onEditar(producto) }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Editar",
                                                    tint = Color(0xFFC41230)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    viewModel.eliminarProducto(token, producto.id_producto)
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.Red
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
    }
}