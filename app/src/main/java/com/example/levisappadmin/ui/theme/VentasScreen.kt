package com.example.levisappadmin.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.model.ClienteConVentas
import com.example.levisappadmin.model.VentaAgrupada
import com.example.levisappadmin.viewmodel.VentasViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton

private val RojoLevis = Color(0xFFD32F2F)

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
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = RojoLevis
            )
        }
        Text(
            text = "Reporte de Ventas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RojoLevis,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoLevis)
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
            clientes.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay ventas registradas")
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
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(cliente.nombre_usuario, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(cliente.email_usuario, fontSize = 13.sp, color = Color.Gray)
                    Text("${cliente.ventas.size} venta(s)", fontSize = 13.sp, color = RojoLevis)
                }
                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = RojoLevis
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    cliente.ventas.forEach { venta ->
                        VentaCard(venta)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VentaCard(venta: VentaAgrupada) {
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandido = !expandido },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Venta #${venta.id_venta}", fontWeight = FontWeight.SemiBold)
                Text("Fecha: ${venta.fecha.take(10)}", fontSize = 12.sp, color = Color.Gray)
                Text("Total: $${"%,.0f".format(venta.total_venta)}", fontSize = 13.sp, color = RojoLevis, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(visible = expandido) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                venta.productos.forEach { producto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombreProducto, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Cant: ${producto.cantidad} × $${"%,.0f".format(producto.precioUnitario)}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("$${"%,.0f".format(producto.subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}