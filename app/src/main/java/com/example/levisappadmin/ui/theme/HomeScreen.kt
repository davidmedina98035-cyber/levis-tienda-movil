package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag

data class MenuOpcion(
    val titulo: String,
    val subtitulo: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onCatalogoClick: () -> Unit,
    onCarritoClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val opciones = listOf(
        MenuOpcion("Catálogo", "Comprar", Icons.Filled.ShoppingCart, onCatalogoClick),
        MenuOpcion("Carrito", "Mi carrito", Icons.Filled.ShoppingBag , onCarritoClick),
        MenuOpcion("Cerrar Sesión", "Salir", Icons.Filled.Person, onCerrarSesion)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "LEVI'S",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFCC0000),
                fontSize = 36.sp
            )
        )
        Text(
            "SISTEMA DE ADMINISTRACIÓN",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(opciones) { opcion ->
                TarjetaMenu(opcion = opcion)
            }
        }
    }
}

@Composable
private fun TarjetaMenu(opcion: MenuOpcion) {
    Card(
        onClick = opcion.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = opcion.titulo,
                tint = Color(0xFFCC0000),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                opcion.titulo,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                opcion.subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}