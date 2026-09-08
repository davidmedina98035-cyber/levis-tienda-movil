package com.example.levisappadmin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levisappadmin.model.ActualizarUsuarioRequest
import com.example.levisappadmin.model.CrearUsuarioRequest
import com.example.levisappadmin.model.Usuario
import com.example.levisappadmin.viewmodel.UsuariosViewModel

private val RojoLevis = Color(0xFFC41230)
private val FondoNegro = Color(0xFF121212)
private val SuperficieCard = Color(0xFF1E1E1E)
private val BordeCard = Color(0xFF2C2C2C)

@Composable
fun UsuariosScreen(token: String, onBack: () -> Unit) {
    val viewModel: UsuariosViewModel = viewModel()
    val usuarios by viewModel.usuarios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var tabSeleccionado by remember { mutableStateOf(0) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioEditando by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(Unit) { viewModel.cargarUsuarios(token) }

    Column(
        modifier = Modifier.fillMaxSize().background(FondoNegro)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(FondoNegro).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("GESTIÓN DE USUARIOS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        TabRow(
            selectedTabIndex = tabSeleccionado,
            containerColor = SuperficieCard,
            contentColor = Color.White
        ) {
            Tab(
                selected = tabSeleccionado == 0,
                onClick = { tabSeleccionado = 0 },
                selectedContentColor = RojoLevis,
                unselectedContentColor = Color.Gray
            ) {
                Text("Clientes", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(
                selected = tabSeleccionado == 1,
                onClick = { tabSeleccionado = 1 },
                selectedContentColor = RojoLevis,
                unselectedContentColor = Color.Gray
            ) {
                Text("Administradores", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoLevis)
            }
        } else {
            val filtrados = usuarios.filter {
                if (tabSeleccionado == 0) it.rol == "cliente" else it.rol == "admin"
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (filtrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay usuarios en esta categoría", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(filtrados) { usuario ->
                            TarjetaUsuario(
                                usuario = usuario,
                                onEditar = { usuarioEditando = it; mostrarFormulario = true },
                                onEliminar = { viewModel.eliminarUsuario(token, it.id_usuario) }
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { usuarioEditando = null; mostrarFormulario = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    containerColor = RojoLevis,
                    contentColor = Color.White
                ) { Icon(Icons.Default.Add, contentDescription = null) }
            }
        }

        error?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(16.dp))
        }
    }

    if (mostrarFormulario) {
        FormularioUsuario(
            usuario = usuarioEditando,
            token = token,
            rolPorDefecto = if (tabSeleccionado == 0) "cliente" else "admin",
            viewModel = viewModel,
            onDismiss = { mostrarFormulario = false; usuarioEditando = null }
        )
    }
}

@Composable
fun TarjetaUsuario(
    usuario: Usuario,
    onEditar: (Usuario) -> Unit,
    onEliminar: (Usuario) -> Unit
) {
    var mostrarConfirmar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = RojoLevis,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(usuario.email, color = Color.Gray, fontSize = 13.sp)
                usuario.telefono?.let { Text("Tel: $it", color = Color.LightGray, fontSize = 12.sp) }
                usuario.direccion?.let { Text("Dir: $it", color = Color.LightGray, fontSize = 12.sp) }
            }
            IconButton(onClick = { onEditar(usuario) }) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.LightGray)
            }
            IconButton(onClick = { mostrarConfirmar = true }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = RojoLevis)
            }
        }
    }

    if (mostrarConfirmar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmar = false },
            containerColor = SuperficieCard,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = { Text("¿Eliminar usuario?") },
            text = { Text("¿Seguro que deseas eliminar a ${usuario.nombre}?") },
            confirmButton = {
                Button(
                    onClick = { onEliminar(usuario); mostrarConfirmar = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoLevis)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmar = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun FormularioUsuario(
    usuario: Usuario?,
    token: String,
    rolPorDefecto: String,
    viewModel: UsuariosViewModel,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var email by remember { mutableStateOf(usuario?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf(usuario?.telefono ?: "") }
    var direccion by remember { mutableStateOf(usuario?.direccion ?: "") }
    val rol = usuario?.rol ?: rolPorDefecto
    val esCliente = rol == "cliente"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SuperficieCard,
        titleContentColor = Color.White,
        textContentColor = Color.LightGray,
        title = {
            Text(
                if (usuario == null) "Nuevo ${if (esCliente) "Cliente" else "Administrador"}"
                else "Editar ${if (esCliente) "Cliente" else "Administrador"}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
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
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (usuario == null) "Contraseña" else "Contraseña (opcional)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
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
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono (opcional)") },
                    singleLine = true,
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
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección (opcional)") },
                    singleLine = true,
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (usuario == null) {
                        viewModel.crearUsuario(token, CrearUsuarioRequest(nombre, email, password, rol, telefono.ifEmpty { null }, direccion.ifEmpty { null }), onDismiss)
                    } else {
                        viewModel.actualizarUsuario(token, usuario.id_usuario, ActualizarUsuarioRequest(nombre, email, rol, telefono.ifEmpty { null }, direccion.ifEmpty { null }, password.ifEmpty { null }), onDismiss)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RojoLevis)
            ) { Text(if (usuario == null) "Crear" else "Guardar", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}