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
                    _productos.value = lista.filter { it.totalStock > 0 }
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

    fun agregarAlCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val idProd = producto.id_producto ?: producto.id ?: 0
        val index = carritoActual.indexOfFirst { (it.producto.id_producto ?: it.producto.id ?: 0) == idProd }
        if (index != -1) {
            val existente = carritoActual[index]
            val stockTotal = producto.totalStock
            if (existente.cantidad < stockTotal) {
                carritoActual[index] = existente.copy(cantidad = existente.cantidad + 1)
            }
        } else {
            carritoActual.add(ItemCarrito(producto, 1))
        }
        _carrito.value = carritoActual.toList()
    }

    fun quitarDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val idProd = producto.id_producto ?: producto.id ?: 0
        val index = carritoActual.indexOfFirst { (it.producto.id_producto ?: it.producto.id ?: 0) == idProd }
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
        val idProd = producto.id_producto ?: producto.id ?: 0
        _carrito.value = _carrito.value.filter { (it.producto.id_producto ?: it.producto.id ?: 0) != idProd }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
    }

    fun totalCarrito(): Double {
        return _carrito.value.sumOf { item ->
            val precio = item.producto.precioProducto ?: 0.0
            precio * item.cantidad
        }
    }

    fun cantidadEnCarrito(producto: Producto): Int {
        val idProd = producto.id_producto ?: producto.id ?: 0
        return _carrito.value.find { (it.producto.id_producto ?: it.producto.id ?: 0) == idProd }?.cantidad ?: 0
    }

    fun confirmarVenta(token: String, idUsuario: Int) {
        val items = _carrito.value.map { item ->
            ItemVentaRequest(
                id_producto = item.producto.id_producto ?: item.producto.id ?: 0,
                cantidad = item.cantidad,
                talla = null // O un valor por defecto / campo de talla si tu ItemCarrito lo almacena en lugar de Producto
            )
        }
        if (items.isEmpty()) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val total = totalCarrito()
                val response = RetrofitClient.api.crearVenta(
                    "Bearer $token",
                    VentaRequest(
                        total_venta = total,
                        productos = items
                    )
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