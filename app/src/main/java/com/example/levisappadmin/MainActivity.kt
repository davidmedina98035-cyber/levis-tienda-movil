package com.example.levisappadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levisappadmin.viewmodel.CatalogoViewModel
import com.example.levisappadmin.viewmodel.CatalogoViewModelFactory
import com.example.levisappadmin.viewmodel.LoginViewModel
import com.example.levisappadmin.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = viewModel()
            val context = LocalContext.current
            val catalogoViewModel: CatalogoViewModel = viewModel(
                factory = CatalogoViewModelFactory(context)
            )

            var token by remember { mutableStateOf("") }
            var idUsuario by remember { mutableStateOf(0) }

            NavHost(navController = navController, startDestination = "login") {

                composable("login") {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { t: String ->
                            token = t
                            idUsuario = t.toIntOrNull() ?: 0
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onRegisterClick = { navController.navigate("register") },
                        onForgotPasswordClick = { navController.navigate("forgotPassword") }
                    )
                }

                composable("home") {
                    HomeScreen(
                        onCatalogoClick = { navController.navigate("catalogo") },
                        onCarritoClick = { navController.navigate("carrito") }, // 👈 agregar
                        onCerrarSesion = {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                composable("catalogo") {
                    CatalogoScreen(
                        token = token,
                        onVerCarrito = { navController.navigate("carrito") },
                        onVolver = { navController.popBackStack() },
                        viewModel = catalogoViewModel
                    )
                }

                composable("carrito") {
                    CarritoScreen(
                        token = token,
                        idUsuario = idUsuario,
                        onBack = { navController.popBackStack() },
                        onVentaConfirmada = {
                            navController.navigate("catalogo") {
                                popUpTo("catalogo") { inclusive = true }
                            }
                        },
                        viewModel = catalogoViewModel
                    )
                }

                composable("register") {
                    RegisterScreen(
                        viewModel = loginViewModel,
                        onBack = { navController.popBackStack() },
                        onRegisterSuccess = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }

                composable("forgotPassword") {
                    ForgotPasswordScreen(
                        viewModel = loginViewModel,
                        onVolverAlLogin = { navController.popBackStack() },
                        onEnvioExitoso = {
                            navController.navigate("login") {
                                popUpTo("forgotPassword") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}