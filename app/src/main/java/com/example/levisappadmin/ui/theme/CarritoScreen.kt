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
        if (ventaExitosa) {
            mostrarDialogo = true
        }
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
                    colors = ButtonDefaults.buttonColors(containerColor = LevisRojo)
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            title = {
                Text(
                    "¡Venta confirmada!",
                    fontWeight = FontWeight.Bold,
                    color = LevisRojo
                )
            },
            text = {
                Text("Tu compra se realizó exitosamente.")
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = LevisGrisClaro,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Carrito",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
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
                .padding(16.dp)
        ) {
            if (carrito.isEmpty()) {
                Text(
                    "Tu carrito está vacío.",
                    modifier = Modifier.padding(16.dp),
                    color = LevisGris
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(carrito) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.producto.nombre,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = LevisRojo
                                            )
                                        )
                                        Text(
                                            "Precio unit.: $${item.producto.precio}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LevisGris
                                        )
                                    }
                                    Text(
                                        "$${"%.2f".format(item.producto.precio * item.cantidad)}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = LevisNegro
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.quitarDelCarrito(item.producto) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LevisRojo)
                                    ) {
                                        Text("−", fontSize = 18.sp)
                                    }

                                    Text(
                                        "${item.cantidad}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        color = LevisNegro
                                    )

                                    OutlinedButton(
                                        onClick = { viewModel.agregarAlCarrito(item.producto) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LevisRojo)
                                    ) {
                                        Text("+", fontSize = 18.sp)
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(
                                        onClick = { viewModel.eliminarDelCarrito(item.producto) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar",
                                            tint = LevisRojo
                                        )
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LevisNegro
                            )
                        )
                        Text(
                            "$${"%.2f".format(viewModel.totalCarrito())}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = LevisRojo
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (error != null) {
                    Text(
                        error ?: "",
                        color = LevisRojo,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.limpiarCarrito() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LevisRojo)
                ) {
                    Text("Limpiar carrito")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.confirmarVenta(token, idUsuario) },
                    enabled = !cargando && carrito.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LevisRojo,
                        contentColor = Color.White,
                        disabledContainerColor = LevisGris,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        if (cargando) "Procesando..." else "Confirmar compra",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}