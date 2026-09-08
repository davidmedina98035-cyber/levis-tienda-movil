package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    val backgroundColor = Color(0xFF0B0B0B)
    val cardBackground = Color(0xFF121212)
    val borderColor = Color(0xFF1F1F1F)
    val neonRed = Color(0xFFE31837)

    val productosFiltrados = productos.filter { producto ->
        val nombre = producto.nombreProducto ?: ""
        val coincideNombre = nombre.lowercase().contains(busqueda.lowercase())
        val catProd = producto.categoria ?: "Sin categoría"
        val coincideCategoria = categoriaSeleccionada == "Todas" ||
                catProd.lowercase() == categoriaSeleccionada.lowercase()
        coincideNombre && coincideCategoria
    }

    LaunchedEffect(Unit) {
        viewModel.cargarProductos(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GESTIÓN DE INVENTARIO", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B0B0B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = neonRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Ítem")
            }
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = neonRed
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Control de existencias, tallas y referencias en tiempo real.",
                                color = Color(0xFF888888),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = busqueda,
                                onValueChange = { busqueda = it },
                                label = { Text("Buscar producto o referencia...", color = Color.Gray) },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = neonRed,
                                    unfocusedBorderColor = borderColor,
                                    focusedContainerColor = cardBackground,
                                    unfocusedContainerColor = cardBackground,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
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
                                InventarioCardItem(
                                    producto = producto,
                                    cardBackground = cardBackground,
                                    borderColor = borderColor,
                                    neonRed = neonRed,
                                    onEditar = { onEditar(producto) },
                                    onCambiarEstado = { nuevoEstado ->
                                        val idProd = producto.id_producto ?: producto.id ?: 0
                                        viewModel.cambiarEstadoProducto(token, idProd, nuevoEstado)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventarioCardItem(
    producto: Producto,
    cardBackground: Color,
    borderColor: Color,
    neonRed: Color,
    onEditar: () -> Unit,
    onCambiarEstado: (Boolean) -> Unit
) {
    val idReal = producto.id_producto ?: producto.id ?: 0
    val nombre = producto.nombreProducto ?: "Sin nombre"
    val categoria = producto.categoria ?: "Sin categoría"
    val colorText = producto.color ?: "N/A"
    val generoText = producto.genero ?: "Unisex"
    val precio = producto.precioProducto ?: 0.0

    val estaActivo = producto.estaActivo
    val imageUrl = if (!producto.imagen.isNullOrEmpty()) {
        if (producto.imagen.startsWith("http")) producto.imagen
        else "http://192.168.1.9:3002${producto.imagen}"
    } else ""

    // Procesar las tallas y el stock total
    val tallasLista = producto.tallas ?: emptyList()
    val desgloseTallas = if (tallasLista.isNotEmpty()) {
        tallasLista.joinToString(" | ") { "${it.talla ?: "?"}: ${it.stock ?: 0}" }
    } else {
        "Sin tallas registradas"
    }
    val stockTotal = producto.totalStock

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (estaActivo) Color(0xFF22C55E) else neonRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REF #$idReal",
                        color = if (estaActivo) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    color = Color(0xFF1A1A1A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = categoria,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("N/A", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombre,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Género: $generoText | Color: $colorText",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ $precio",
                        color = neonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECCIÓN DE STOCK Y TALLAS NUEVA
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF161616),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF222222))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Tallas: $desgloseTallas",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stock Total: $stockTotal",
                        color = if (stockTotal > 0) Color(0xFF22C55E) else neonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = borderColor, thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = { onCambiarEstado(!estaActivo) }) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (estaActivo) Color(0xFF22C55E) else neonRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (estaActivo) "Desactivar" else "Activar",
                        color = if (estaActivo) Color(0xFF888888) else Color(0xFF22C55E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}