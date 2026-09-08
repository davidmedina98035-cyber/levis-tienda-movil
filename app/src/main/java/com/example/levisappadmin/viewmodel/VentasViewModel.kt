package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.ClienteConVentas
import com.example.levisappadmin.model.DetalleProducto
import com.example.levisappadmin.model.VentaAgrupada
import com.example.levisappadmin.model.VentaDetalleRaw
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VentasViewModel : ViewModel() {

    private val _clientes = MutableStateFlow<List<ClienteConVentas>>(emptyList())
    val clientes: StateFlow<List<ClienteConVentas>> = _clientes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarReporte(token: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.api.getReporteVentas(authHeader) // Cambiado de apiService a api

                if (response.isSuccessful) {
                    val filas: List<VentaDetalleRaw> = response.body() ?: emptyList()

                    val agrupado = filas
                        .groupBy { it.email_usuario ?: "sin_correo" }
                        .map { entry ->
                            val filasCliente = entry.value
                            val primerFila = filasCliente.first()
                            val ventas = filasCliente
                                .groupBy { it.id_venta }
                                .map { ventaEntry ->
                                    val filasVenta = ventaEntry.value
                                    val v = filasVenta.first()
                                    VentaAgrupada(
                                        id_venta = v.id_venta,
                                        fecha = v.fecha ?: "",
                                        total_venta = v.total_venta,
                                        productos = filasVenta.map { fila ->
                                            DetalleProducto(
                                                nombreProducto = fila.nombreProducto ?: "Producto",
                                                cantidad = fila.cantidad,
                                                precioUnitario = fila.precioUnitario,
                                                subtotal = fila.cantidad * fila.precioUnitario,
                                                imagen = fila.imagen,
                                                talla = fila.talla ?: "N/A"
                                            )
                                        }
                                    )
                                }
                            ClienteConVentas(
                                nombre_usuario = primerFila.nombre_usuario ?: "Cliente",
                                email_usuario = primerFila.email_usuario ?: "",
                                ventas = ventas
                            )
                        }

                    _clientes.value = agrupado
                } else {
                    _error.value = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }
}