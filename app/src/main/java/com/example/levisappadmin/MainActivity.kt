package com.example.levisappadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.ui.theme.AgregarProductoScreen
import com.example.levisappadmin.ui.theme.AjustesScreen
import com.example.levisappadmin.ui.theme.CarritoScreen
import com.example.levisappadmin.ui.theme.CatalogoScreen
import com.example.levisappadmin.ui.theme.EditarProductoScreen
import com.example.levisappadmin.ui.theme.ForgotPasswordScreen
import com.example.levisappadmin.ui.theme.InventarioScreen
import com.example.levisappadmin.ui.theme.LoginScreen
import com.example.levisappadmin.ui.theme.ProveedoresScreen
import com.example.levisappadmin.ui.theme.RegisterScreen
import com.example.levisappadmin.ui.theme.UsuariosScreen
import com.example.levisappadmin.ui.theme.VentasScreen
import com.example.levisappadmin.viewmodel.CatalogoViewModel
import androidx.compose.foundation.BorderStroke

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var token by remember { mutableStateOf<String?>(null) }
                var emailUsuario by remember { mutableStateOf("") }
                var idUsuario by remember { mutableStateOf(0) }
                var pantalla by remember { mutableStateOf("login") }
                var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

                // Compartido entre CatalogoScreen y CarritoScreen para mantener el carrito
                val catalogoViewModel: CatalogoViewModel = viewModel()

                when (pantalla) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { tkn, email, id ->
                            token = tkn
                            emailUsuario = email
                            idUsuario = id.toIntOrNull() ?: 0
                            pantalla = "dashboard"
                        },
                        onRegisterClick = { pantalla = "register" },
                        onForgotPasswordClick = { pantalla = "forgotPassword" }
                    )
                    "register" -> RegisterScreen(
                        onBack = { pantalla = "login" },
                        onRegisterSuccess = { pantalla = "login" }
                    )
                    "forgotPassword" -> ForgotPasswordScreen(
                        onBack = { pantalla = "login" },
                        onExitooso = { pantalla = "login" }
                    )
                    "dashboard" -> MainDashboard(
                        onInventarioClick = { pantalla = "inventario" },
                        onUsuariosClick = { pantalla = "usuarios" },
                        onVentasClick = { pantalla = "ventas" },
                        onAjustesClick = { pantalla = "ajustes" },
                        onCatalogoClick = { pantalla = "catalogo" },
                        onProveedoresClick = { pantalla = "proveedores" }
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
                    "proveedores" -> ProveedoresScreen(
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
                    "catalogo" -> CatalogoScreen(
                        token = token ?: "",
                        onVerCarrito = { pantalla = "carrito" },
                        onBack = { pantalla = "dashboard" },
                        viewModel = catalogoViewModel
                    )
                    "carrito" -> CarritoScreen(
                        token = token ?: "",
                        idUsuario = idUsuario,
                        onBack = { pantalla = "catalogo" },
                        onVentaConfirmada = { pantalla = "catalogo" },
                        viewModel = catalogoViewModel
                    )
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
    onAjustesClick: () -> Unit,
    onCatalogoClick: () -> Unit,
    onProveedoresClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0B0B0B)
    val cardBackground = Color(0xFF121212)
    val neonRed = Color(0xFFE31837)
    val subtitleColor = Color(0xFF888888)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "LEVI'S",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = neonRed,
            letterSpacing = 2.sp
        )
        Text(
            text = "SISTEMA DE ADMINISTRACIÓN",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MenuCard("Inventario", Icons.Default.List, "Stock Jeans", cardBackground, neonRed, subtitleColor, onInventarioClick) }
            item { MenuCard("Usuarios", Icons.Default.Person, "Roles", cardBackground, neonRed, subtitleColor, onUsuariosClick) }
            item { MenuCard("Ventas", Icons.Default.ShoppingCart, "Facturas", cardBackground, neonRed, subtitleColor, onVentasClick) }
            item { MenuCard("Ajustes", Icons.Default.Settings, "Config", cardBackground, neonRed, subtitleColor, onAjustesClick) }
            item { MenuCard("Catálogo", Icons.Default.Info, "Nueva venta", cardBackground, neonRed, subtitleColor, onCatalogoClick) }
            item { MenuCard("Proveedores", Icons.Default.Business, "Aliados", cardBackground, neonRed, subtitleColor, onProveedoresClick) }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    subtitle: String,
    cardBg: Color,
    neonRed: Color,
    subColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, Color(0xFF1F1F1F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = neonRed
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = subColor
            )
        }
    }
}