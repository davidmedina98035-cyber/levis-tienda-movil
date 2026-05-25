package com.example.levisappadmin.ui.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.viewmodel.InventarioViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    token: String,
    producto: Producto,
    onBack: () -> Unit,
    viewModel: InventarioViewModel = viewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf(producto.nombreProducto) }
    var descripcion by remember { mutableStateOf(producto.descripcionProducto ?: "") }
    var precio by remember { mutableStateOf(producto.precioProducto.toString()) }
    var talla by remember { mutableStateOf(producto.talla ?: "") }
    var categoria by remember { mutableStateOf(producto.categoria ?: "pantalon") }
    var stock by remember { mutableStateOf(producto.stockProducto.toString()) }
    var genero by remember { mutableStateOf(producto.genero ?: "Hombre") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var mensajeExito by remember { mutableStateOf(false) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedGenero by remember { mutableStateOf(false) }

    val categorias = listOf("pantalon", "camiseta", "chaqueta", "accesorio")
    val generos = listOf("Hombre", "Mujer", "Unisex")
    val error by viewModel.error.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    // Selector de imagen
    val imagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imagenUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC41230),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = talla,
                onValueChange = { talla = it },
                label = { Text("Talla (S, M, L, XL...)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Selector Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Selector Género
            ExposedDropdownMenuBox(
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = !expandedGenero }
            ) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedGenero,
                    onDismissRequest = { expandedGenero = false }
                ) {
                    generos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                genero = opcion
                                expandedGenero = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // ✅ Imagen actual del producto
            if (imagenUri == null && producto.imagen != null) {
                Text(text = "Imagen actual:", fontWeight = FontWeight.Bold)
                AsyncImage(
                    model = "http://10.0.2.2:3002${producto.imagen}",
                    contentDescription = "Imagen actual",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // ✅ Selector de imagen nueva
            Button(
                onClick = { imagenLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(if (imagenUri != null) "✅ Nueva imagen seleccionada" else "📁 Cambiar imagen")
            }

            // Vista previa de imagen nueva
            imagenUri?.let { uri ->
                Text(text = "Nueva imagen:", fontWeight = FontWeight.Bold)
                AsyncImage(
                    model = uri,
                    contentDescription = "Vista previa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            if (mensajeExito) {
                Text(
                    text = "✅ Producto actualizado exitosamente",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }

            error?.let {
                Text(text = it, color = Color.Red)
            }

            Button(
                onClick = {
                    val imagenPart = imagenUri?.let { uri ->
                        val stream = context.contentResolver.openInputStream(uri)
                        val bytes = stream?.readBytes() ?: return@let null
                        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestBody)
                    }

                    viewModel.editarProducto(
                        token = token,
                        id = producto.id_producto,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio,
                        talla = talla,
                        categoria = categoria,
                        stock = stock,
                        genero = genero,
                        imagenPart = imagenPart
                    ) {
                        mensajeExito = true
                        imagenUri = null
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC41230)),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}