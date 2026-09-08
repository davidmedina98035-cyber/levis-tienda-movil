package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.viewmodel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    token: String,
    idUsuario: Int,
    onBack: () -> Unit,
    onVentaConfirmada: () -> Unit,
    viewModel: CatalogoViewModel
) {
    val carrito by viewModel.carrito.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val ventaExitosa by viewModel.ventaExitosa.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(ventaExitosa) {
        if (ventaExitosa) mostrarDialogo = true
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        viewModel.resetVentaExitosa()
                        onVentaConfirmada()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC41230))
                ) { Text("Aceptar", color = Color.White) }
            },
            title = { Text("¡Venta confirmada!", fontWeight = FontWeight.Bold, color = Color(0xFFC41230)) },
            text = { Text("Tu compra se realizó exitosamente.", color = Color(0xFFE0E0E0)) },
            containerColor = Color(0xFF121212)
        )
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Carrito", fontWeight = FontWeight.Bold, color = Color(0xFFFF3B30)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFFFF3B30))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            if (carrito.isEmpty()) {
                Text("Tu carrito está vacío.", modifier = Modifier.padding(16.dp), color = Color(0xFFA0A0A0))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(carrito) { item ->
                        val precio = item.producto.precioProducto ?: 0.0
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.producto.nombreProducto ?: "Sin nombre",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF3B30)
                                        )
                                        Text("Precio unit.: $$precio", fontSize = 12.sp, color = Color(0xFFA0A0A0))
                                    }
                                    Text(
                                        "$${"%.2f".format(precio * item.cantidad)}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = { viewModel.quitarDelCarrito(item.producto) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30))
                                    ) { Text("−", fontSize = 18.sp) }

                                    Text(
                                        "${item.cantidad}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    OutlinedButton(
                                        onClick = { viewModel.agregarAlCarrito(item.producto) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30))
                                    ) { Text("+", fontSize = 18.sp) }

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(onClick = { viewModel.eliminarDelCarrito(item.producto) }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF3B30))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            "$${"%.2f".format(viewModel.totalCarrito())}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFFFF3B30)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                error?.let {
                    Text(it, color = Color(0xFFFF3B30), modifier = Modifier.padding(bottom = 8.dp))
                }

                OutlinedButton(
                    onClick = { viewModel.limpiarCarrito() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30))
                ) { Text("Limpiar carrito") }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.confirmarVenta(token, idUsuario) },
                    enabled = !cargando && carrito.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC41230))
                ) {
                    Text(
                        if (cargando) "Procesando..." else "Confirmar compra",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}