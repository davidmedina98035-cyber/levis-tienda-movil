package com.example.levisappadmin.model

data class VentaDetalleRaw(
    val id_venta: Int,
    val total_venta: Double,
    val fecha: String,
    val nombre_usuario: String,
    val email_usuario: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val nombreProducto: String,
    val imagen: String?
)

data class DetalleProducto(
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class VentaAgrupada(
    val id_venta: Int,
    val fecha: String,
    val total_venta: Double,
    val productos: List<DetalleProducto>
)

data class ClienteConVentas(
    val nombre_usuario: String,
    val email_usuario: String,
    val ventas: List<VentaAgrupada>
)