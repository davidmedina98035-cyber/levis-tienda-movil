package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {

    private val api = RetrofitClient.api

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
                val response = api.getProductos(token)
                if (response.isSuccessful) {
                    _productos.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar productos"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _cargando.value = false
            }
        }
    }

    fun agregarProducto(token: String, producto: Producto) {
        viewModelScope.launch {
            try {
                val response = api.addProducto(token, producto)
                if (response.isSuccessful) {
                    cargarProductos(token)
                } else {
                    _error.value = "Error al agregar producto"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun editarProducto(token: String, producto: Producto) {
        viewModelScope.launch {
            try {
                val response = api.updateProducto(token, producto.id ?: 0, producto)
                if (response.isSuccessful) {
                    cargarProductos(token)
                } else {
                    _error.value = "Error al editar producto"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun cambiarStock(token: String, producto: Producto, delta: Int) {
        val nuevoStock = (producto.stock + delta).coerceAtLeast(0)
        val productoActualizado = producto.copy(stock = nuevoStock)
        viewModelScope.launch {
            try {
                val response = api.updateProducto(token, producto.id ?: 0, productoActualizado)
                if (response.isSuccessful) {
                    cargarProductos(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error al actualizar stock ${response.code()}: $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Excepción al actualizar stock: ${e.message}"
            }
        }
    }

    fun eliminarProducto(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteProducto(token, id)
                if (response.isSuccessful) {
                    cargarProductos(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error eliminar ${response.code()}: $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Excepción eliminar: ${e.message}"
            }
        }
    }
}