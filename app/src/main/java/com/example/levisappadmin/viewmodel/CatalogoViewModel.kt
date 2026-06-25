package com.example.levisappadmin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.CarritoDB
import com.example.levisappadmin.model.CarritoEntity
import com.example.levisappadmin.model.ItemCarrito
import com.example.levisappadmin.model.ItemVentaRequest
import com.example.levisappadmin.model.Producto
import com.example.levisappadmin.model.VentaRequest
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(context: Context) : ViewModel() {

    private val api = RetrofitClient.api
    private val carritoDao = CarritoDB.getInstance(context).carritoDao()

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

    init {
        // Cargar carrito guardado al iniciar
        viewModelScope.launch {
            carritoDao.obtenerCarrito().collect { entities ->
                _carrito.value = entities.map { entity ->
                    ItemCarrito(
                        producto = Producto(
                            id = entity.productoId,
                            nombre = entity.nombre,
                            precio = entity.precio,
                            stock = 0,
                            talla = entity.talla,
                            categoria = entity.categoria
                        ),
                        cantidad = entity.cantidad
                    )
                }
            }
        }
    }

    fun cargarProductos(token: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = api.getProductos(token)
                if (response.isSuccessful) {
                    _productos.value = (response.body() ?: emptyList())
                        .filter { it.stock > 0 }
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
        viewModelScope.launch {
            val carritoActual = _carrito.value.toMutableList()
            val existente = carritoActual.find { it.producto.id == producto.id }
            val nuevaCantidad = if (existente != null) {
                if (existente.cantidad < producto.stock) existente.cantidad + 1
                else existente.cantidad
            } else 1

            carritoDao.insertar(
                CarritoEntity(
                    productoId = producto.id ?: 0,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = nuevaCantidad,
                    talla = producto.talla,
                    categoria = producto.categoria
                )
            )
        }
    }

    fun quitarDelCarrito(producto: Producto) {
        viewModelScope.launch {
            val existente = _carrito.value.find { it.producto.id == producto.id }
            if (existente != null) {
                if (existente.cantidad > 1) {
                    carritoDao.insertar(
                        CarritoEntity(
                            productoId = producto.id ?: 0,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = existente.cantidad - 1,
                            talla = producto.talla,
                            categoria = producto.categoria
                        )
                    )
                } else {
                    carritoDao.eliminar(
                        CarritoEntity(
                            productoId = producto.id ?: 0,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = existente.cantidad,
                            talla = producto.talla,
                            categoria = producto.categoria
                        )
                    )
                }
            }
        }
    }

    fun eliminarDelCarrito(producto: Producto) {
        viewModelScope.launch {
            val existente = _carrito.value.find { it.producto.id == producto.id }
            if (existente != null) {
                carritoDao.eliminar(
                    CarritoEntity(
                        productoId = producto.id ?: 0,
                        nombre = producto.nombre,
                        precio = producto.precio,
                        cantidad = existente.cantidad,
                        talla = producto.talla,
                        categoria = producto.categoria
                    )
                )
            }
        }
    }

    fun totalCarrito(): Double {
        return _carrito.value.sumOf { it.producto.precio * it.cantidad }
    }

    fun cantidadEnCarrito(producto: Producto): Int {
        return _carrito.value.find { it.producto.id == producto.id }?.cantidad ?: 0
    }

    fun limpiarCarrito() {
        viewModelScope.launch {
            carritoDao.limpiarCarrito()
        }
    }

    fun confirmarVenta(token: String, idUsuario: Int) {
        val items = _carrito.value.map {
            ItemVentaRequest(
                id_producto = it.producto.id ?: 0,
                cantidad = it.cantidad
            )
        }
        if (items.isEmpty()) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = api.crearVenta(token, VentaRequest(idUsuario, items))
                if (response.isSuccessful) {
                    carritoDao.limpiarCarrito()
                    _ventaExitosa.value = true
                    cargarProductos(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error al confirmar venta: $errorBody"
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