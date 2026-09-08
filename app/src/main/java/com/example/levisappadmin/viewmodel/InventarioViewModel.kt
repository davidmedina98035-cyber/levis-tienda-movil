package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.model.Proveedor
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class InventarioViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _proveedores = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = _proveedores

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarProductos(token: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.api.getProductos("Bearer $token")
                if (response.isSuccessful) {
                    _productos.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar productos: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarProveedores(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getProveedores("Bearer $token")
                if (response.isSuccessful) {
                    _proveedores.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar proveedores: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión proveedores: ${e.message}"
            }
        }
    }

    fun cambiarEstadoProducto(token: String, id: Int, nuevoEstado: Boolean) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.api.cambiarEstadoProducto("Bearer $token", id, mapOf("activo" to nuevoEstado))
                if (response.isSuccessful) {
                    cargarProductos(token)
                } else {
                    _error.value = "Error al cambiar estado: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión al cambiar estado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun agregarProducto(
        token: String,
        nombre: String,
        descripcion: String,
        precio: String,
        talla: String,
        categoria: String,
        stock: String,
        genero: String,
        imagenPart: MultipartBody.Part?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.api.crearProducto(
                    token = "Bearer $token",
                    nombre = nombre.toRequestBody("text/plain".toMediaType()),
                    descripcion = descripcion.toRequestBody("text/plain".toMediaType()),
                    precio = precio.toRequestBody("text/plain".toMediaType()),
                    talla = talla.toRequestBody("text/plain".toMediaType()),
                    categoria = categoria.toRequestBody("text/plain".toMediaType()),
                    stock = stock.toRequestBody("text/plain".toMediaType()),
                    genero = genero.toRequestBody("text/plain".toMediaType()),
                    imagen = imagenPart
                )
                if (response.isSuccessful) {
                    cargarProductos(token)
                    onSuccess()
                } else {
                    _error.value = "Error al agregar: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun editarProducto(
        token: String,
        id: Int,
        nombre: String,
        descripcion: String,
        color: String,
        precio: String,
        categoria: String,
        genero: String,
        idProveedor: String,
        tallas: RequestBody,
        imagenPart: MultipartBody.Part?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.actualizarProducto(
                    token = "Bearer $token",
                    id = id,
                    nombre = nombre.toRequestBody("text/plain".toMediaTypeOrNull()),
                    descripcion = descripcion.toRequestBody("text/plain".toMediaTypeOrNull()),
                    color = color.toRequestBody("text/plain".toMediaTypeOrNull()),
                    precio = precio.toRequestBody("text/plain".toMediaTypeOrNull()),
                    categoria = categoria.toRequestBody("text/plain".toMediaTypeOrNull()),
                    genero = genero.toRequestBody("text/plain".toMediaTypeOrNull()),
                    idProveedor = if (idProveedor.isNotBlank()) idProveedor.toRequestBody("text/plain".toMediaTypeOrNull()) else null,
                    tallas = tallas,
                    imagen = imagenPart
                )
                if (response.isSuccessful) {
                    cargarProductos(token)
                    onSuccess()
                } else {
                    _error.value = "Error al actualizar el producto: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Fallo de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }
}