package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.ItemVentaRequest
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.model.VentaRequest
import com.example.levisappadmin.model.ItemCarrito
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ventaExitosa = MutableStateFlow(false)
    val ventaExitosa: StateFlow<Boolean> = _ventaExitosa

    fun cargarProductos(token: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.api.getProductos("Bearer $token")
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    _productos.value = lista.filter { (it.stockProducto ?: it.stock) > 0 }
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

    // ✅ DESPUÉS — reemplaza el objeto completo
    fun agregarAlCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val index = carritoActual.indexOfFirst { it.producto.id_producto == producto.id_producto }
        if (index != -1) {
            val existente = carritoActual[index]
            val stock = producto.stockProducto ?: producto.stock
            if (existente.cantidad < stock) {
                carritoActual[index] = existente.copy(cantidad = existente.cantidad + 1)
            }
        } else {
            carritoActual.add(ItemCarrito(producto, 1))
        }
        _carrito.value = carritoActual.toList()
    }

    fun quitarDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val index = carritoActual.indexOfFirst { it.producto.id_producto == producto.id_producto }
        if (index != -1) {
            val existente = carritoActual[index]
            if (existente.cantidad > 1) {
                carritoActual[index] = existente.copy(cantidad = existente.cantidad - 1)
            } else {
                carritoActual.removeAt(index)
            }
        }
        _carrito.value = carritoActual.toList()
    }

    fun eliminarDelCarrito(producto: Producto) {
        _carrito.value = _carrito.value.filter { it.producto.id_producto != producto.id_producto }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
    }

    fun totalCarrito(): Double {
        return _carrito.value.sumOf {
            val precio = it.producto.precioProducto ?: it.producto.precio
            precio * it.cantidad
        }
    }

    fun cantidadEnCarrito(producto: Producto): Int {
        return _carrito.value.find { it.producto.id_producto == producto.id_producto }?.cantidad ?: 0
    }

    fun confirmarVenta(token: String, idUsuario: Int) {
        val items = _carrito.value.map {
            ItemVentaRequest(
                id_producto = it.producto.id_producto ?: 0,
                cantidad = it.cantidad,
                precioProducto = it.producto.precioProducto ?: it.producto.precio
            )
        }
        if (items.isEmpty()) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val total = totalCarrito()
                val response = RetrofitClient.api.crearVenta(
                    "Bearer $token",
                    VentaRequest(idUsuario, total, items)
                )
                if (response.isSuccessful) {
                    limpiarCarrito()
                    _ventaExitosa.value = true
                    cargarProductos(token)
                } else {
                    _error.value = "Error al confirmar venta: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetVentaExitosa() {
        _ventaExitosa.value = false
    }

    fun limpiarError() {
        _error.value = null
    }
}