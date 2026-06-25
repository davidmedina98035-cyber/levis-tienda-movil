package com.example.levisappadmin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.ui.theme.AgregarProductoScreen
import com.example.levisappadmin.ui.theme.AjustesScreen
import com.example.levisappadmin.ui.theme.EditarProductoScreen
import com.example.levisappadmin.ui.theme.InventarioScreen
import com.example.levisappadmin.ui.theme.LoginScreen
import com.example.levisappadmin.ui.theme.UsuariosScreen
import com.example.levisappadmin.ui.theme.VentasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var token by remember { mutableStateOf<String?>(null) }
                var emailUsuario by remember { mutableStateOf("") }
                var pantalla by remember { mutableStateOf("login") }
                var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

                when (pantalla) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { tkn, email ->
                            token = tkn
                            emailUsuario = email
                            pantalla = "dashboard"
                        }
                    )
                    "dashboard" -> MainDashboard(
                        onInventarioClick = { pantalla = "inventario" },
                        onUsuariosClick = { pantalla = "usuarios" },
                        onVentasClick = { pantalla = "ventas" },
                        onAjustesClick = { pantalla = "ajustes" }
                    )
                    "ajustes" -> AjustesScreen(
                        token = token ?: "",
                        email = emailUsuario,
                        onBack = { pantalla = "dashboard" }
                    )
                    "inventario" -> InventarioScreen(
                        token = token ?: "",
                        onBack = { pantalla = "dashboard" },
                        onAgregar = { pantalla = "agregar" },
                        onEditar = { producto ->
                            productoSeleccionado = producto
                            pantalla = "editar"
                        }
                    )
                    "usuarios" -> UsuariosScreen(
                        token = token ?: "",
                        onBack = { pantalla = "dashboard" }
                    )
                    "ventas" -> VentasScreen(
                        token = token ?: "",
                        onBack = { pantalla = "dashboard" }
                    )
                    "agregar" -> AgregarProductoScreen(
                        token = token ?: "",
                        onBack = { pantalla = "inventario" }
                    )
                    "editar" -> productoSeleccionado?.let { producto ->
                        EditarProductoScreen(
                            token = token ?: "",
                            producto = producto,
                            onBack = { pantalla = "inventario" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboard(
    onInventarioClick: () -> Unit,
    onUsuariosClick: () -> Unit,
    onVentasClick: () -> Unit,
    onAjustesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
            .padding(16.dp)
    ) {
        Text(
            text = "LEVI'S",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFC41230),
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "SISTEMA DE ADMINISTRACIÓN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MenuCard("Inventario", Icons.Default.List, "Stock Jeans", onClick = onInventarioClick) }
            item { MenuCard("Usuarios", Icons.Default.Person, "Roles", onClick = onUsuariosClick) }
            item { MenuCard("Ventas", Icons.Default.ShoppingCart, "Facturas", onClick = onVentasClick) }
            item { MenuCard("Ajustes", Icons.Default.Settings, "Config", onClick = onAjustesClick) }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFC41230)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}