package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.ClienteConVentas
import com.example.levisappadmin.model.DetalleProducto
import com.example.levisappadmin.model.VentaAgrupada
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
            try {
                val response = RetrofitClient.api.getReporteVentas("Bearer $token")
                if (response.isSuccessful) {
                    val filas = response.body() ?: emptyList()

                    // Agrupar por cliente → venta → productos
                    val agrupado = filas
                        .groupBy { it.email_usuario }
                        .map { (_, filasCliente) ->
                            val primerFila = filasCliente.first()
                            val ventas = filasCliente
                                .groupBy { it.id_venta }
                                .map { (_, filasVenta) ->
                                    val v = filasVenta.first()
                                    VentaAgrupada(
                                        id_venta = v.id_venta,
                                        fecha = v.fecha,
                                        total_venta = v.total_venta,
                                        productos = filasVenta.map { fila ->
                                            DetalleProducto(
                                                nombreProducto = fila.nombreProducto,
                                                cantidad = fila.cantidad,
                                                precioUnitario = fila.precioUnitario,
                                                subtotal = fila.cantidad * fila.precioUnitario
                                            )
                                        }
                                    )
                                }
                            ClienteConVentas(
                                nombre_usuario = primerFila.nombre_usuario,
                                email_usuario = primerFila.email_usuario,
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