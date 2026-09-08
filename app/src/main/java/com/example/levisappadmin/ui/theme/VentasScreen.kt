package com.example.levisappadmin.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.levisappadmin.model.ClienteConVentas
import com.example.levisappadmin.model.DetalleProducto
import com.example.levisappadmin.model.VentaAgrupada
import com.example.levisappadmin.viewmodel.VentasViewModel

private val RojoLevis = Color(0xFFC41230)
private val FondoNegro = Color(0xFF121212)
private val SuperficieCard = Color(0xFF1E1E1E)
private val BordeCard = Color(0xFF2C2C2C)
private val FondoSubItem = Color(0xFF161616)

@Composable
fun VentasScreen(token: String, onBack: () -> Unit, viewModel: VentasViewModel = viewModel()) {
    val clientes by viewModel.clientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarReporte(token)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = "Reporte de Ventas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = RojoLevis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        when {
            cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoLevis)
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
            clientes.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay ventas registradas", color = Color.Gray)
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(clientes) { cliente ->
                    ClienteCard(cliente)
                }
            }
        }
    }
}

@Composable
fun ClienteCard(cliente: ClienteConVentas) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BordeCard),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cliente.nombre_usuario, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text(cliente.email_usuario, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${cliente.ventas.size} venta(s)", fontSize = 13.sp, color = RojoLevis, fontWeight = FontWeight.SemiBold)
                }
                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = RojoLevis
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cliente.ventas.forEach { venta ->
                        VentaCard(venta)
                    }
                }
            }
        }
    }
}

@Composable
fun VentaCard(venta: VentaAgrupada) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BordeCard),
        colors = CardDefaults.cardColors(containerColor = FondoSubItem)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pedido #${venta.id_venta}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(venta.fecha.take(10), fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${"%,.0f".format(venta.total_venta)} COP",
                        fontSize = 14.sp,
                        color = RojoLevis,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = BordeCard)
                    venta.productos.forEach { producto ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Foto del producto
                            AsyncImage(
                                model = producto.imagen ?: "https://via.placeholder.com/150",
                                contentDescription = producto.nombreProducto,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BordeCard),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            // Nombre, Talla y Cantidad
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = producto.nombreProducto,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val tallaStr = if (!producto.talla.isNullOrEmpty()) "Talla: ${producto.talla} | " else ""
                                Text(
                                    text = "${tallaStr}Cant: ${producto.cantidad} × $${"%,.0f".format(producto.precioUnitario)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // Subtotal
                            Text(
                                text = "$${"%,.0f".format(producto.subtotal)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}