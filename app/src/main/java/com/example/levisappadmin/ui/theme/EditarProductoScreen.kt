package com.example.levisappadmin.ui.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.model.Proveedor
import com.example.levisappadmin.model.TallaStockRequest
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
    var nombre by remember { mutableStateOf(producto.nombreProducto ?: "") }
    var precio by remember { mutableStateOf(producto.precioProducto?.toString() ?: "") }
    var color by remember { mutableStateOf(producto.color ?: "") }

    var stockS by remember { mutableStateOf("0") }
    var stockM by remember { mutableStateOf("0") }
    var stockL by remember { mutableStateOf("0") }
    var stockXL by remember { mutableStateOf("0") }
    var stockXXL by remember { mutableStateOf("0") }

    LaunchedEffect(producto) {
        producto.tallas?.forEach { t ->
            when (t.talla) {
                "S" -> stockS = t.stock?.toString() ?: "0"
                "M" -> stockM = t.stock?.toString() ?: "0"
                "L" -> stockL = t.stock?.toString() ?: "0"
                "XL" -> stockXL = t.stock?.toString() ?: "0"
                "XXL" -> stockXXL = t.stock?.toString() ?: "0"
            }
        }
    }

    // Categoría inicial mapeada correctamente
    var categoria by remember { mutableStateOf(producto.categoria ?: "Jeans") }
    var genero by remember { mutableStateOf(producto.genero ?: "Hombre") }

    val proveedores: List<Proveedor> by viewModel.proveedores.collectAsState()
    var proveedorSeleccionadoId by remember { mutableStateOf(producto.id_proveedor) }
    var proveedorSeleccionadoNombre by remember { mutableStateOf("Seleccione un proveedor") }
    var expandedProveedor by remember { mutableStateOf(false) }

    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var mensajeExito by remember { mutableStateOf(false) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedGenero by remember { mutableStateOf(false) }

    // Todas las categorías completas de la base de datos
    val categorias = listOf(
        "Jeans", "Camisetas", "Chaqueta", "Vestidos", "Shorts",
        "Accesorios", "Interiores", "Medias", "Faldas",
        "Buzos y Hoodies", "Ropa Deportiva", "Pantalones",
        "Camisas Formales", "Blusas", "Ropa Interior Femenina", "Ropa Interior Masculina"
    )
    val generos = listOf("Hombre", "Mujer", "Unisex")
    val error by viewModel.error.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    val backgroundColor = Color(0xFF0B0B0B)
    val cardBackground = Color(0xFF121212)
    val borderColor = Color(0xFF1F1F1F)
    val inputBackground = Color(0xFF1E1E1E)
    val neonRed = Color(0xFFE31837)

    val imagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imagenUri = uri }

    LaunchedEffect(Unit) {
        viewModel.cargarProveedores(token)
    }

    LaunchedEffect(proveedores, proveedorSeleccionadoId) {
        if (proveedorSeleccionadoId != null) {
            val prov = proveedores.firstOrNull { it.id.toInt() == proveedorSeleccionadoId }
            if (prov != null) {
                proveedorSeleccionadoNombre = prov.nombre
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EDITAR PRODUCTO", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        "NOMBRE DE LA REFERENCIA",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = neonRed,
                    unfocusedBorderColor = borderColor,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = {
                        Text(
                            "PRECIO (COP)",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonRed,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = {
                        Text(
                            "COLOR",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonRed,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "STOCK POR TALLA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TallaStockInput("S", stockS) { stockS = it }
                        TallaStockInput("M", stockM) { stockM = it }
                        TallaStockInput("L", stockL) { stockL = it }
                        TallaStockInput("XL", stockXL) { stockXL = it }
                        TallaStockInput("XXL", stockXXL) { stockXXL = it }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedProveedor,
                onExpandedChange = { expandedProveedor = !expandedProveedor }
            ) {
                OutlinedTextField(
                    value = proveedorSeleccionadoNombre,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            "PROVEEDOR",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProveedor) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonRed,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedProveedor,
                    onDismissRequest = { expandedProveedor = false },
                    modifier = Modifier.background(cardBackground)
                ) {
                    proveedores.forEach { proveedor ->
                        DropdownMenuItem(
                            text = { Text(proveedor.nombre, color = Color.White) },
                            onClick = {
                                proveedorSeleccionadoId = proveedor.id.toInt()
                                proveedorSeleccionadoNombre = proveedor.nombre
                                expandedProveedor = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            "CATEGORÍA",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonRed,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false },
                    modifier = Modifier.background(cardBackground)
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion, color = Color.White) },
                            onClick = {
                                categoria = opcion
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = !expandedGenero }
            ) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            "GÉNERO",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonRed,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedGenero,
                    onDismissRequest = { expandedGenero = false },
                    modifier = Modifier.background(cardBackground)
                ) {
                    generos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion, color = Color.White) },
                            onClick = {
                                genero = opcion
                                expandedGenero = false
                            }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "IMAGEN NUEVA (Opcional)",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { imagenLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = inputBackground),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = if (imagenUri != null) "✅ Imagen nueva seleccionada" else "📁 CAMBIAR ARCHIVO DE IMAGEN",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            val displayImage = imagenUri ?: producto.imagen
            displayImage?.let { img ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = img,
                        contentDescription = "Vista previa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            if (mensajeExito) {
                Text(
                    text = "✅ Producto actualizado correctamente",
                    color = Color(0xFF22C55E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            error?.let {
                Text(text = it, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && precio.isNotBlank()) {
                        val imagenPart = imagenUri?.let { uri ->
                            val stream = context.contentResolver.openInputStream(uri)
                            val bytes = stream?.readBytes() ?: return@let null
                            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestBody)
                        }

                        // Creamos el arreglo de tallas en formato JSON o lista que espera el backend
                        val tallasJson =
                            "[{\"talla\":\"S\",\"stock\":$stockS},{\"talla\":\"M\",\"stock\":$stockM},{\"talla\":\"L\",\"stock\":$stockL},{\"talla\":\"XL\",\"stock\":$stockXL},{\"talla\":\"XXL\",\"stock\":$stockXXL}]"
                        val tallasBody = tallasJson.toRequestBody("text/plain".toMediaTypeOrNull())

                        viewModel.editarProducto(
                            token = token,
                            id = producto.id_producto ?: 0,
                            nombre = nombre,
                            descripcion = producto.descripcionProducto
                                ?: "", // Mantenemos la descripción real o vacía
                            color = color, // <--- ¡AHORA SÍ SE ENVÍA EL COLOR CORRECTAMENTE!
                            precio = precio,
                            categoria = categoria,
                            genero = genero,
                            idProveedor = proveedorSeleccionadoId?.toString() ?: "",
                            tallas = tallasBody, // <--- Enviamos el stock empaquetado en las tallas
                            imagenPart = imagenPart
                        ) {
                            mensajeExito = true
                            onBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = neonRed),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "GUARDAR CAMBIOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
            @Composable
            fun RowScope.TallaStockInput(
                talla: String,
                value: String,
                onValueChange: (String) -> Unit
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = talla,
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { onValueChange(it) },
                        singleLine = true,
                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE31837),
                            unfocusedBorderColor = Color(0xFF1F1F1F),
                            focusedContainerColor = Color(0xFF1E1E1E),
                            unfocusedContainerColor = Color(0xFF1E1E1E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }
    }
}