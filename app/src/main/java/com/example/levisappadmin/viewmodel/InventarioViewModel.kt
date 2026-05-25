package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.levisappadmin.model.ProductoRequest

class InventarioViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

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
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun eliminarProducto(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.deleteProducto("Bearer $token", id)
                if (response.isSuccessful) {
                    _productos.value = _productos.value.filter { it.id_producto != id }
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
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
                val response = RetrofitClient.api.actualizarProducto(
                    token = "Bearer $token",
                    id = id,
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
                    _error.value = "Error al editar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }
}