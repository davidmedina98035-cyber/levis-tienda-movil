package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levisappadmin.model.Proveedor
import com.example.levisappadmin.model.ProveedorRequest
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch


private val RojoLevis = Color(0xFFE31837)
private val FondoNegro = Color(0xFF0B0B0B)
private val SuperficieCard = Color(0xFF121212)
private val BordeCard = Color(0xFF1F1F1F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedoresScreen(token: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var proveedores by remember { mutableStateOf<List<Proveedor>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(false) }

    // Campos del formulario
    var nombreInput by remember { mutableStateOf("") }
    var correoInput by remember { mutableStateOf("") }
    var rolInput by remember { mutableStateOf("") }

    // Estados para edición y eliminación (con tipado explícito nullable)
    var proveedorEditando by remember { mutableStateOf<Proveedor?>(null) }
    var proveedorAEliminar by remember { mutableStateOf<Proveedor?>(null) }

    fun cargarProveedores() {
        scope.launch {
            cargando = true
            try {
                val response = RetrofitClient.api.getProveedores(token = "Bearer $token")
                if (response.isSuccessful) {
                    proveedores = response.body() ?: emptyList()
                } else {
                    mensaje = "Error al cargar proveedores"
                    esError = true
                }
            } catch (e: Exception) {
                mensaje = "Error de conexión"
                esError = true
            }
            cargando = false
        }
    }

    LaunchedEffect(Unit) {
        cargarProveedores()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Proveedores", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoNegro)
            )
        },
        containerColor = FondoNegro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoNegro)
                .padding(16.dp)
        ) {
            // Formulario de Crear / Editar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SuperficieCard),
                border = BorderStroke(1.dp, BordeCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (proveedorEditando == null) "Agregar Proveedor" else "Editar Proveedor",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nombreInput,
                        onValueChange = { nombreInput = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoLevis,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = RojoLevis,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = correoInput,
                        onValueChange = { correoInput = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoLevis,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = RojoLevis,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rolInput,
                        onValueChange = { rolInput = it },
                        label = { Text("Rol / Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoLevis,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = RojoLevis,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val request = ProveedorRequest(nombre = nombreInput, correo = correoInput, rol = rolInput)
                                    val response = if (proveedorEditando == null) {
                                        RetrofitClient.api.crearProveedor(token = "Bearer $token", body = request)
                                    } else {
                                        RetrofitClient.api.actualizarProveedor(token = "Bearer $token", id = proveedorEditando!!.id, body = request)
                                    }

                                    if (response.isSuccessful) {
                                        mensaje = if (proveedorEditando == null) "Proveedor agregado" else "Proveedor actualizado"
                                        esError = false
                                        nombreInput = ""
                                        correoInput = ""
                                        rolInput = ""
                                        proveedorEditando = null
                                        cargarProveedores()
                                    } else {
                                        mensaje = "Error al guardar"
                                        esError = true
                                    }
                                } catch (e: Exception) {
                                    mensaje = "Error de red"
                                    esError = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RojoLevis)
                    ) {
                        Text(if (proveedorEditando == null) "Guardar Proveedor" else "Actualizar Proveedor", color = Color.White)
                    }

                    if (proveedorEditando != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                proveedorEditando = null
                                nombreInput = ""
                                correoInput = ""
                                rolInput = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar edición", color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RojoLevis)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(proveedores) { proveedor ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BordeCard),
                            colors = CardDefaults.cardColors(containerColor = SuperficieCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("#${proveedor.id} - ${proveedor.nombre}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Correo: ${proveedor.correo}", color = Color.Gray, fontSize = 13.sp)
                                Text("Rol: ${proveedor.rol}", color = Color.Gray, fontSize = 13.sp)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        proveedorEditando = proveedor
                                        nombreInput = proveedor.nombre
                                        correoInput = proveedor.correo
                                        rolInput = proveedor.rol ?: ""
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Cyan)
                                    }
                                    IconButton(onClick = { proveedorAEliminar = proveedor }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    proveedorAEliminar?.let { prov ->
        AlertDialog(
            onDismissRequest = { proveedorAEliminar = null },
            containerColor = SuperficieCard,
            title = { Text("Eliminar Proveedor", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de eliminar a ${prov.nombre}?", color = Color.Gray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val p = proveedorAEliminar
                        proveedorAEliminar = null
                        if (p != null) {
                            scope.launch {
                                try {
                                    val response = RetrofitClient.api.eliminarProveedor(token = "Bearer $token", id = p.id)
                                    if (response.isSuccessful) {
                                        cargarProveedores()
                                    } else {
                                        mensaje = "No se pudo eliminar"
                                        esError = true
                                    }
                                } catch (e: Exception) {
                                    mensaje = "Error de red"
                                    esError = true
                                }
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { proveedorAEliminar = null }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}